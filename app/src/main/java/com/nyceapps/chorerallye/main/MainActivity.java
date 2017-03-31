package com.nyceapps.chorerallye.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.chore.ChoreItem;
import com.nyceapps.chorerallye.chore.ChoresAdapter;
import com.nyceapps.chorerallye.chore.ChoresListActivity;
import com.nyceapps.chorerallye.member.MemberItem;
import com.nyceapps.chorerallye.member.MembersAdapter;
import com.nyceapps.chorerallye.member.MembersListActivity;
import com.nyceapps.chorerallye.race.RaceAdapter;
import com.nyceapps.chorerallye.race.RaceHistoryActivity;
import com.nyceapps.chorerallye.race.RaceItem;
import com.nyceapps.chorerallye.race.RaceStatisticsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nyceapps.chorerallye.main.Constants.BACKUP_FILENAME_STRING_PATTERN;
import static com.nyceapps.chorerallye.main.Constants.CHORE_COLUMNS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_KEY_DATE_ENDING;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_KEY_DATE_STARTED;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_KEY_ORDER_KEY;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_CHORES;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_HISTORY;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_ITEMS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_MEMBERS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_META;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_RACE;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_SETTINGS;
import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_LOG;
import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_RALLYE;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_VALUE;
import static com.nyceapps.chorerallye.main.Constants.LENGTH_OF_RACE_IN_DAYS_FOR_LOG_MODE;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_CREATE_BACKUP;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_MANAGE_PREFERENCES;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_RESTORE_BACKUP;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_SCAN_QR_CODE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean enterInit;

    private RallyeData data;
    private LocalHistory localHistory;
    private DisplayedRaceItems displayedRaceItems;

    private MembersAdapter membersAdapter;
    private ChoresAdapter choresAdapter;
    private RaceAdapter raceAdapter;

    private DatabaseReference settingsDatabase;
    private DatabaseReference membersDatabase;
    private DatabaseReference choresDatabase;
    private DatabaseReference raceDatabase;
    private DatabaseReference historyDatabase;

    private FirebaseAuth rallyeAuth;
    private FirebaseAuth.AuthStateListener rallyeAuthListener;

    private AlertDialog membersDialog;
    private AlertDialog choresDialog;
    private AlertDialog participateHouseholdDialog;
    private ProgressDialog loadingDataDialog;

    private TextView infoView;
    private RecyclerView raceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterInit = true;

        rallyeAuth = FirebaseAuth.getInstance();
        rallyeAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, String.format("user = [%s]", user));
                if (user != null && enterInit) {
                    init();
                    enterInit = false;
                }
            }
        };

        signIn();
    }

    @Override
    public void onStart() {
        super.onStart();

        rallyeAuth.addAuthStateListener(rallyeAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (rallyeAuthListener != null) {
            rallyeAuth.removeAuthStateListener(rallyeAuthListener);
        }

        if (localHistory != null) {
            localHistory.save();
        }

        if (displayedRaceItems != null) {
            displayedRaceItems.save();
        }
    }

    private void signIn() {
        rallyeAuth.signInAnonymously();
    }

    private void init() {
        Log.d(TAG, "Initializing...");
        String householdId = Utils.getHouseholdId(this);

        if (TextUtils.isEmpty(householdId)) {
            showGotoPreferencesDialog();
        } else {
            showLoadingDataDialog();

            initData();

            initMembersView();
            initChoresView();
            initInfoView();
            initRaceView();

            initDatabases();
        }
    }

    private void showGotoMembersDialog() {
        if (choresDialog != null && choresDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_text_no_members)
                .setPositiveButton(R.string.main_menu_manage_members, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        manageMembers();
                    }
                });
        membersDialog = builder.create();
        membersDialog.show();

    }

    private void showGotoChoresDialog() {
        if (membersDialog != null && membersDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_text_no_chores)
                .setPositiveButton(R.string.main_menu_manage_chores, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        manageChores();
                    }
                });
        choresDialog = builder.create();
        choresDialog.show();

    }

    private void showGotoPreferencesDialog() {
        if (participateHouseholdDialog != null && participateHouseholdDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_text_no_household_name)
                .setNeutralButton(R.string.main_menu_scan_household_qr_code, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scanHouseholdQRCode();
                    }
                })
                .setPositiveButton(R.string.main_menu_manage_preferences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        managePreferences();
                    }
                });
        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        if (data != null) {
            String displayMode = data.getSettings().getDisplayMode();

            MenuItem itemStartStop = menu.findItem(R.id.action_start_stop_race);
            if (itemStartStop != null) {
                boolean isRunning = data.getSettings().isRunning();
                if (isRunning) {
                    itemStartStop.setTitle(R.string.main_menu_stop_race);
                } else {
                    itemStartStop.setTitle(R.string.main_menu_start_race);
                }
                switch (displayMode) {
                    case DISPLAY_MODE_RALLYE:
                        itemStartStop.setVisible(true);
                        break;
                    case DISPLAY_MODE_LOG:
                        itemStartStop.setVisible(false);
                        break;
                }
            }

            MenuItem itemSwitchDisplayMode = menu.findItem(R.id.action_switch_display_mode);
            if (itemSwitchDisplayMode != null) {
                switch (displayMode) {
                    case DISPLAY_MODE_RALLYE:
                        itemSwitchDisplayMode.setTitle(R.string.main_menu_display_mode_log);
                        break;
                    case DISPLAY_MODE_LOG:
                        itemSwitchDisplayMode.setTitle(R.string.main_menu_display_mode_rallye);
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String householdId = Utils.getHouseholdId(this);
        switch (item.getItemId()) {
            case R.id.action_undo:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    undoPoints();
                }
                break;
            case R.id.action_race_history:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    showRaceHistory();
                }
                break;
            case R.id.action_race_statistics:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    showRaceStatistics();
                }
                break;
            case R.id.action_start_stop_race:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    boolean isRunning = data.getSettings().isRunning();
                    if (isRunning) {
                        stopRace();
                    } else {
                        startRace();
                    }
                }
                break;
            case R.id.action_manage_members:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    manageMembers();
                }
                break;
            case R.id.action_manage_chores:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    manageChores();
                }
                break;
            case R.id.action_show_household_qr_code:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    showHouseholdQRCode();
                }
                break;
            case R.id.action_scan_household_qr_code:
                scanHouseholdQRCode();
                break;
            case R.id.action_switch_display_mode:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    String displayMode = data.getSettings().getDisplayMode();
                    switch (displayMode) {
                        case DISPLAY_MODE_RALLYE:
                            switchDisplayMode(DISPLAY_MODE_LOG);
                            break;
                        case DISPLAY_MODE_LOG:
                            switchDisplayMode(DISPLAY_MODE_RALLYE);
                            break;
                    }
                }
                break;
            case R.id.action_create_backup:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    createBackup();
                }
                break;
            case R.id.action_restore_backup:
                restoreBackup();
                break;
            case R.id.action_manage_preferences:
                managePreferences();
                break;
            default:
                break;
        }

        return true;
    }

    private void showRaceHistory() {
        enterInit = true;

        Intent intent = new Intent(this, RaceHistoryActivity.class);
        startActivity(intent);
    }

    private void showRaceStatistics() {
        enterInit = true;

        Intent intent = new Intent(this, RaceStatisticsActivity.class);
        startActivity(intent);
    }

    private void startRace() {
        startRace(data.getSettings().getLengthOfRallyeInDays());
    }

    private void startRace(int pLengthOfRallyeInDays) {
        data.getSettings().setRunning(true);
        settingsDatabase.setValue(data.getSettings());

        localHistory.init();
        displayedRaceItems.init();

        Date dateStarted = new Date();
        Date dateEnding = Utils.getDateEndingForRace(dateStarted, pLengthOfRallyeInDays);
        raceDatabase.child(DATABASE_SUBPATH_META).child(DATABASE_KEY_DATE_STARTED).setValue(dateStarted);
        raceDatabase.child(DATABASE_SUBPATH_META).child(DATABASE_KEY_DATE_ENDING).setValue(dateEnding);
        raceDatabase.child(DATABASE_SUBPATH_ITEMS).removeValue();

        membersAdapter.notifyDataSetChanged();
        choresAdapter.notifyDataSetChanged();

        setInfoText();
    }

    private void stopRace() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation_text_end_rallye))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        data.getSettings().setRunning(false);
                        settingsDatabase.setValue(data.getSettings());

                        moveCurrentRaceToHistory();

                        membersAdapter.notifyDataSetChanged();
                        choresAdapter.notifyDataSetChanged();

                        setInfoText();
                    }
                })
                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog stopRallyeConfirmationDialog = builder.create();
        stopRallyeConfirmationDialog.show();

    }

    private void moveCurrentRaceToHistory() {
        String uid = historyDatabase.push().getKey();
        historyDatabase.child(uid).child(DATABASE_SUBPATH_META).child(DATABASE_KEY_DATE_STARTED).setValue(data.getRace().getDateStarted());
        for (RaceItem raceItem : data.getRace().getRaceItems()) {
            historyDatabase.child(uid).child(DATABASE_SUBPATH_ITEMS).child(raceItem.getUid()).setValue(raceItem);
        }
    }

    private void manageMembers() {
        enterInit = true;

        Intent intent = new Intent(this, MembersListActivity.class);
        startActivity(intent);
    }

    private void manageChores() {
        enterInit = true;

        Intent intent = new Intent(this, ChoresListActivity.class);
        startActivity(intent);
    }

    private void showHouseholdQRCode() {
        enterInit = true;

        String householdId = Utils.getHouseholdId(this);

        Intent intent = new Intent(this, ShowQRCodeActivity.class);
        intent.putExtra(EXTRA_MESSAGE_VALUE, householdId);
        startActivity(intent);
    }

    private void scanHouseholdQRCode() {
        enterInit = true;

        Intent intent = new Intent(this, ScanQRCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN_QR_CODE);
    }

    private void switchDisplayMode(String pDisplayMode) {
        data.getSettings().setDisplayMode(pDisplayMode);
        settingsDatabase.setValue(data.getSettings());
        Date dateStarted = data.getRace().getDateStarted();
        Date dateEnding = null;
        switch (pDisplayMode) {
            case DISPLAY_MODE_RALLYE:
                dateEnding = Utils.getDateEndingForRace(dateStarted, data.getSettings().getLengthOfRallyeInDays());
                break;
            case DISPLAY_MODE_LOG:
                boolean isRunning = data.getSettings().isRunning();
                if (!isRunning) {
                    startRace(LENGTH_OF_RACE_IN_DAYS_FOR_LOG_MODE);
                    dateEnding = null;
                } else {
                    // Add ten years
                    dateEnding = Utils.getDateEndingForRace(dateStarted, LENGTH_OF_RACE_IN_DAYS_FOR_LOG_MODE);
                }
                break;
        }
        if (dateEnding != null) {
            raceDatabase.child(DATABASE_SUBPATH_META).child(DATABASE_KEY_DATE_ENDING).setValue(dateEnding);
        }
    }

    private void createBackup() {
        String householdName = Utils.getHouseholdName(this);
        if (householdName != null) {
            String datePostfix = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());;
            String fileName = String.format(BACKUP_FILENAME_STRING_PATTERN, householdName, datePostfix);
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
            startActivityForResult(intent, REQUEST_CODE_CREATE_BACKUP);
        }
    }

    private void restoreBackup() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        startActivityForResult(intent, REQUEST_CODE_RESTORE_BACKUP);
    }

    private void managePreferences() {
        enterInit = true;

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MANAGE_PREFERENCES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CODE_SCAN_QR_CODE:
                if (resultCode == RESULT_OK) {
                    final String householdId = intent.getStringExtra(EXTRA_MESSAGE_VALUE);
                    if (!TextUtils.isEmpty(householdId)) {
                        Log.d(TAG, String.format("householdId from scan = [%s]", householdId));
                        final String householdName = Utils.getHouseholdNameFromId(householdId);
                        if (!TextUtils.isEmpty(householdName)) {
                            Log.d(TAG, String.format("householdName from scan = [%s]", householdName));
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(String.format(getString(R.string.confirmation_text_participate_in_household), householdName))
                                    .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Utils.setHouseholdId(householdId, MainActivity.this);
                                            Utils.setHouseholdName(householdName, MainActivity.this);

                                            finish();
                                            startActivity(getIntent());
                                        }
                                    })
                                    .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });
                            participateHouseholdDialog = builder.create();
                            participateHouseholdDialog.show();
                        }
                    }
                }
                break;
            case REQUEST_CODE_CREATE_BACKUP:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (intent != null) {
                        uri = intent.getData();
                        Utils.createBackup(uri, data, this);
                    }
                }
                break;
            case REQUEST_CODE_RESTORE_BACKUP:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (intent != null) {
                        uri = intent.getData();
                        Utils.restoreBackup(uri, data, this);
                    }
                }
                break;
            case REQUEST_CODE_MANAGE_PREFERENCES:
            if (data != null) {
                    settingsDatabase.setValue(data.getSettings());
                }
            break;
        }
    }

    private void initData() {
        Log.d(TAG, "Initializing data...");

        data = new RallyeData();
        ((RallyeApplication) this.getApplication()).setRallyeData(data);

        localHistory = new LocalHistory(this);
        displayedRaceItems = new DisplayedRaceItems(this);
    }

    private void initMembersView() {
        RecyclerView membersView = (RecyclerView) findViewById(R.id.members_view);

        membersView.setHasFixedSize(true);

        LinearLayoutManager membersLayoutManager = new LinearLayoutManager(this);
        membersView.setLayoutManager(membersLayoutManager);

        membersAdapter = new MembersAdapter(data, this);
        membersView.setAdapter(membersAdapter);
    }

    private void initChoresView() {
        RecyclerView choresView = (RecyclerView) findViewById(R.id.chores_view);

        choresView.setHasFixedSize(true);

        LinearLayoutManager choresLayoutManager = new GridLayoutManager(this, CHORE_COLUMNS);
        choresView.setLayoutManager(choresLayoutManager);

        choresAdapter = new ChoresAdapter(data, this);
        choresView.setAdapter(choresAdapter);
    }

    private void initInfoView() {
        infoView = (TextView) findViewById(R.id.info_view);

        setInfoText();
    }

    private void setInfoText() {
        String infoText = Utils.getRaceInfoText(data, this);
        infoView.setText(infoText);
    }

    private void initRaceView() {
        raceView = (RecyclerView) findViewById(R.id.race_view);

        raceView.setHasFixedSize(true);

        LinearLayoutManager raceLayoutManager = new LinearLayoutManager(this);
        raceView.setLayoutManager(raceLayoutManager);

        raceAdapter = new RaceAdapter(data, this);
        raceView.setAdapter(raceAdapter);
    }

    private void setRaceViewBackground(int pMaxMemberTextWidth) {
        int raceRunnerWidth = (int) getResources().getDimension(R.dimen.race_runner_width);
        int raceRunnerHeight = (int) getResources().getDimension(R.dimen.race_runner_height);

        int raceViewWidth = raceView.getWidth();
        int raceViewHeight = data.getMembers().size() * raceRunnerHeight;

        if (raceViewHeight > 0) {
            int startX = 0;
            int finishX = raceViewWidth - pMaxMemberTextWidth;
            int onePercent = finishX / 100;
            int goalX = Math.round(onePercent * data.getSettings().getWinningPercentage());

            finishX -= raceRunnerWidth;

            Paint primary = new Paint();
            primary.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            Paint dark = new Paint();
            dark.setColor(Color.BLACK);

            Bitmap bitmap = Bitmap.createBitmap(raceViewWidth, raceViewHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawLine(startX, 0, startX, raceViewHeight, dark);
            canvas.drawLine(goalX - 1, 0, goalX - 1, raceViewHeight, primary);
            canvas.drawLine(goalX, 0, goalX, raceViewHeight, primary);
            canvas.drawLine(goalX + 1, 0, goalX + 1, raceViewHeight, primary);
            canvas.drawLine(finishX, 0, finishX, raceViewHeight, dark);

            Drawable drawable = new BitmapDrawable(getResources(), bitmap);

            raceView.setBackground(drawable);
        }
    }

    private void initDatabases() {
        Log.d(TAG, "Initializing databases...");

        String householdId = Utils.getHouseholdId(this);

        settingsDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_SETTINGS);
        settingsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Settings settings = dataSnapshot.getValue(Settings.class);
                if (settings == null) {
                    settings = new Settings();
                }
                data.setSettings(settings);

                invalidateOptionsMenu();

                membersAdapter.notifyDataSetChanged();
                choresAdapter.notifyDataSetChanged();

                if (raceView != null) {
                    switch (settings.getDisplayMode()) {
                        case DISPLAY_MODE_RALLYE:
                            infoView.setVisibility(View.VISIBLE);
                            raceView.setVisibility(View.VISIBLE);
                            break;
                        case DISPLAY_MODE_LOG:
                            infoView.setVisibility(View.GONE);
                            raceView.setVisibility(View.GONE);
                            break;
                    }
                }

                hideLoadingDataDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingDataDialog();
            }
        });

        membersDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_MEMBERS);
        membersDatabase.orderByChild(DATABASE_KEY_ORDER_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MemberItem> members = new ArrayList<>();
                for (DataSnapshot memberDataSnapshot : dataSnapshot.getChildren()) {
                    MemberItem member = memberDataSnapshot.getValue(MemberItem.class);
                    members.add(member);
                }
                if (membersAdapter.updateList(members)) {
                    if (raceAdapter != null) {
                        int maxMemberTextWidth = Utils.calculateMaxMemberTextWidth(members, MainActivity.this);
                        raceAdapter.setMaxMemberTextWidth(maxMemberTextWidth);
                        setRaceViewBackground(maxMemberTextWidth);
                    }
                }

                if (members.size() == 0) {
                    showGotoMembersDialog();
                }

                hideLoadingDataDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingDataDialog();
            }
        });

        choresDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_CHORES);
        choresDatabase.orderByChild(DATABASE_KEY_ORDER_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChoreItem> chores = new ArrayList<>();
                for (DataSnapshot choreDataSnapshot : dataSnapshot.getChildren()) {
                    ChoreItem chore = choreDataSnapshot.getValue(ChoreItem.class);
                    chores.add(chore);
                }
                choresAdapter.updateList(chores);

                if (chores.size() == 0) {
                    showGotoChoresDialog();
                }

                hideLoadingDataDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingDataDialog();
            }
        });

        raceDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_RACE);
        raceDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date dateStarted = dataSnapshot.child(DATABASE_SUBPATH_META).child(DATABASE_KEY_DATE_STARTED).getValue(Date.class);
                data.getRace().setDateStarted(dateStarted);
                Date dateEnding = dataSnapshot.child(DATABASE_SUBPATH_META).child(DATABASE_KEY_DATE_ENDING).getValue(Date.class);
                data.getRace().setDateEnding(dateEnding);

                List<RaceItem> raceItems = new ArrayList<>();
                for (DataSnapshot raceDataSnapshot : dataSnapshot.child(DATABASE_SUBPATH_ITEMS).getChildren()) {
                    RaceItem raceItem = raceDataSnapshot.getValue(RaceItem.class);
                    raceItems.add(raceItem);
                }
                data.getRace().setRaceItems(raceItems);

                membersAdapter.notifyDataSetChanged();
                raceAdapter.notifyDataSetChanged();

                if (raceItems.size() > 0) {
                    RaceItem lastRaceItem = raceItems.get(raceItems.size() - 1);
                    if (displayedRaceItems.size() > 0) {
                        for (int i = raceItems.size() - 1; i >= 0; i--) {
                            RaceItem raceItem = raceItems.get(i);
                            if (!displayedRaceItems.contains(raceItem.getUid())) {
                                showPointsToast(raceItem.getMemberName(), raceItem.getChoreName(), raceItem.getChoreValue());
                            } else {
                                break;
                            }
                        }
                    } else {
                        showPointsToast(lastRaceItem.getMemberName(), lastRaceItem.getChoreName(), lastRaceItem.getChoreValue());
                    }
                    displayedRaceItems.add(lastRaceItem.getUid());
                }

                setInfoText();

                hideLoadingDataDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingDataDialog();
            }
        });

        historyDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_HISTORY);
    }

    public void updatePoints(MemberItem pMember, ChoreItem pChore) {
        RaceItem raceItem = new RaceItem();
        String uid = raceDatabase.child(DATABASE_SUBPATH_ITEMS).push().getKey();
        raceItem.setUid(uid);
        raceItem.setDate(new Date());
        raceItem.setMemberUid(pMember.getUid());
        raceItem.setMemberName(pMember.getName());
        raceItem.setChoreUid(pChore.getUid());
        raceItem.setChoreName(pChore.getName());
        raceItem.setChoreValue(pChore.getValue());
        raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(uid).setValue(raceItem);
        if (Utils.isInstantlyAddRaceItemNote(this) || pChore.isInstantlyAddNote()) {
            addRaceHistoryItemNote(raceItem);
        }

        localHistory.add(raceItem.getUid());

        showPointsToast(pMember, pChore);
        displayedRaceItems.add(raceItem.getUid());
    }

    private void addRaceHistoryItemNote(final RaceItem pRaceItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_race_history_list_item_note, null);
        final EditText raceHistoryItemNoteEditText = (EditText) dialogView.findViewById(R.id.race_history_item_note);
        boolean includePoints = (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode()));
        final String raceHistoryItemText = Utils.makeRaceItemText(pRaceItem.getMemberName(), pRaceItem.getChoreName(), pRaceItem.getChoreValue(), this, includePoints);
        builder.setView(dialogView);
        builder.setMessage(String.format(getString(R.string.dialog_text_note_for_race_history_item), raceHistoryItemText))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String raceHistoryItemNote = raceHistoryItemNoteEditText.getText().toString();
                        pRaceItem.setNote(raceHistoryItemNote);
                        raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(pRaceItem.getUid()).setValue(pRaceItem);
                    }
                })
                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog.
                    }
                });
        builder.create().show();
    }

    private void undoPoints() {
        String raceItemUid = localHistory.undo();
        if (raceItemUid != null) {
            List<RaceItem> raceItems = data.getRace().getRaceItems();
            raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(raceItemUid).removeValue();
        }
    }

    private void showPointsToast(String pMemberName, String pChoreName, int pChoreValue) {
        if (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode())) {
            String toastText = Utils.makeRaceItemText(pMemberName, pChoreName, pChoreValue, this, true);
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPointsToast(MemberItem pMember, ChoreItem pChore) {
        if (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode())) {
            String toastText = Utils.makeRaceItemText(pMember, pChore, this, true);
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoadingDataDialog() {
        if (loadingDataDialog != null && loadingDataDialog.isShowing()) {
            return;
        }
        loadingDataDialog = ProgressDialog.show(this, getString(R.string.dialog_text_loading_data), getString(R.string.dialog_text_please_wait), true);
    }

    private void hideLoadingDataDialog() {
        if (loadingDataDialog != null && loadingDataDialog.isShowing()) {
            loadingDataDialog.dismiss();
        }
    }
}
