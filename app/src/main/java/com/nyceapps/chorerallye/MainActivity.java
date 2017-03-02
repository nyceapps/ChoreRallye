package com.nyceapps.chorerallye;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.CHORE_COLUMNS;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_CHORES;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_MEMBERS;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_RACE;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_VALUE;
import static com.nyceapps.chorerallye.Constants.HOUSEHOLD_ID_INFIX;
import static com.nyceapps.chorerallye.Constants.REQUEST_CODE_SCAN_QR_CODE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean enterInit;

    private RallyeData data;
    private LocalHistory localHistory;

    private MembersAdapter membersAdapter;
    private ChoresAdapter choresAdapter;
    private RaceAdapter raceAdapter;

    private DatabaseReference membersDatabase;
    private DatabaseReference choresDatabase;
    private DatabaseReference raceDatabase;

    private FirebaseAuth rallyeAuth;
    private FirebaseAuth.AuthStateListener rallyeAuthListener;
    private AlertDialog membersDialog;
    private AlertDialog choresDialog;
    private AlertDialog participateHouseholdDialog;
    private ProgressDialog loadingDataDialog;

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

        localHistory = new LocalHistory(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (rallyeAuthListener != null) {
            rallyeAuth.removeAuthStateListener(rallyeAuthListener);
        }

        localHistory.save();
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
            case R.id.action_manage_history:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    manageHistory();
                }
                break;
            case R.id.action_manage_preferences:
                managePreferences();
                break;
            default:
                break;
        }

        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN_QR_CODE) {
                final String householdId = intent.getStringExtra(EXTRA_MESSAGE_VALUE);
                if (!TextUtils.isEmpty(householdId)) {
                    Log.d(TAG, String.format("householdId from scan = [%s]", householdId));
                    String[] parts = TextUtils.split(householdId, HOUSEHOLD_ID_INFIX);
                    if (parts != null && parts.length > 0) {
                        final String householdName = parts[0];
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
            }
        }
    }

    private void manageHistory() {
        enterInit = true;

        Intent intent = new Intent(this, RaceHistoryActivity.class);
        startActivity(intent);
    }

    private void managePreferences() {
        enterInit = true;

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void initData() {
        Log.d(TAG, "Initializing data...");

        data = new RallyeData();
        ((RallyeApplication) this.getApplication()).setRallyeData(data);
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

    private void initRaceView() {
        RecyclerView raceView = (RecyclerView) findViewById(R.id.race_view);

        raceView.setHasFixedSize(true);

        LinearLayoutManager raceLayoutManager = new LinearLayoutManager(this);
        raceView.setLayoutManager(raceLayoutManager);

        raceAdapter = new RaceAdapter(data, this);
        raceView.setAdapter(raceAdapter);
    }

    private void initDatabases() {
        Log.d(TAG, "Initializing databases...");

        String householdId = Utils.getHouseholdId(this);

        membersDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_MEMBERS);
        membersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MemberItem> members = new ArrayList<>();
                for (DataSnapshot memberDataSnapshot : dataSnapshot.getChildren()) {
                    MemberItem member = memberDataSnapshot.getValue(MemberItem.class);
                    members.add(member);
                }
                if (membersAdapter.updateList(members)) {
                    if (raceAdapter != null) {
                        raceAdapter.setMaxMemberTextWidth(Utils.calculateMaxMemberTextWidth(members, MainActivity.this));
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
        choresDatabase.addValueEventListener(new ValueEventListener() {
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
                List<RaceItem> raceItems = new ArrayList<>();
                for (DataSnapshot raceDataSnapshot : dataSnapshot.getChildren()) {
                    RaceItem raceItem = raceDataSnapshot.getValue(RaceItem.class);
                    raceItems.add(raceItem);
                }
                data.getRace().setRaceItem(raceItems);

                membersAdapter.notifyDataSetChanged();
                raceAdapter.notifyDataSetChanged();

                hideLoadingDataDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingDataDialog();
            }
        });
    }

    public void updatePoints(MemberItem pMember, ChoreItem pChore) {
        RaceItem raceItem = new RaceItem();
        String uid = raceDatabase.push().getKey();
        raceItem.setUid(uid);
        raceItem.setDate(new Date());
        raceItem.setMemberUid(pMember.getUid());
        raceItem.setMemberName(pMember.getName());
        raceItem.setChoreUid(pChore.getUid());
        raceItem.setChoreName(pChore.getName());
        raceItem.setChoreValue(pChore.getValue());
        raceDatabase.child(uid).setValue(raceItem);

        localHistory.add(raceItem);

        showPointsToast(pMember, pChore);
    }

    private void undoPoints() {
        RaceItem raceItem = localHistory.undo();
        if (raceItem != null) {
            raceDatabase.child(raceItem.getUid()).removeValue();
        }
    }

    private void showPointsToast(MemberItem pMember, ChoreItem pChore) {
        String toastText = Utils.makeRaceItemText(pMember, pChore, this);
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
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
