package com.nyceapps.chorerallye;

import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.CHORE_COLUMNS;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_CHORES;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_MEMBERS;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_RACE;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_VALUE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean enterInit;

    private RallyeData data;

    private TextView pointsTextView;
    private MembersAdapter membersAdapter;
    private ChoresAdapter choresAdapter;
    private DatabaseReference membersDatabase;
    private DatabaseReference choresDatabase;
    private DatabaseReference raceDatabase;

    private FirebaseAuth rallyeAuth;
    private FirebaseAuth.AuthStateListener rallyeAuthListener;

    private AlertDialog membersDialog;
    private AlertDialog choresDialog;

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
            initData();

            initMembersView();
            initChoresView();
            initPointsView();

            initDatabases();

            showPointsText();
        }
    }

    private void showGotoMembersDialog() {
        if (choresDialog != null && choresDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_text_no_members)
                .setPositiveButton(R.string.main_menu_manage_members, new DialogInterface.OnClickListener() {
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
                    public void onClick(DialogInterface dialog, int id) {
                        manageChores();
                    }
                });
        choresDialog = builder.create();
        choresDialog.show();

    }

    private void showGotoPreferencesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_text_no_household_name)
                .setPositiveButton(R.string.main_menu_manage_preferences, new DialogInterface.OnClickListener() {
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
                    showHouseholdQRCore();
                }
                break;
            case R.id.action_scan_household_qr_code:
                if (householdId == null) {
                    showGotoPreferencesDialog();
                } else {
                    scanHouseholdQRCore();
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

    private void showHouseholdQRCore() {
        enterInit = true;

        String householdId = Utils.getHouseholdId(this);

        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra(EXTRA_MESSAGE_VALUE, householdId);
        startActivity(intent);
    }

    private void scanHouseholdQRCore() {
        enterInit = true;

        // TODO: scan QR code
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

    private void initPointsView() {
        pointsTextView = (TextView) findViewById(R.id.points_view);
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
                membersAdapter.updateList(members);

                if (members.size() == 0) {
                    showGotoMembersDialog();
                }

                showPointsText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                showPointsText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                showPointsText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updatePoints(MemberItem pMember, ChoreItem pChore) {
        RaceItem raceItem = new RaceItem();
        String uid = raceDatabase.push().getKey();
        raceItem.setUid(uid);
        raceItem.setMemberUid(pMember.getUid());
        raceItem.setChoreUid(pChore.getUid());
        raceItem.setChoreValue(pChore.getValue());
        raceDatabase.child(uid).setValue(raceItem);

        showPointsToast(pMember, pChore);
    }

    private void showPointsText() {
        String pointsText = Utils.makeRacePointsText(data);
        pointsTextView.setText(pointsText);
    }

    private void showPointsToast(MemberItem pMember, ChoreItem pChore) {
        String toastText = String.format(getString(R.string.toast_text_member_points_for_chore), pMember.getName(), pChore.getValue(), pChore.getName());
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }
}
