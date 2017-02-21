package com.nyceapps.chorerallye;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.DATABASE_PATH_CHORES;
import static com.nyceapps.chorerallye.Constants.DATABASE_PATH_MEMBERS;
import static com.nyceapps.chorerallye.Constants.DATABASE_PATH_RACE;

public class MainActivity extends AppCompatActivity {
    private static final int CHORE_COLUMNS = 3;

    private RallyeData data;

    private TextView pointsTextView;
    private MembersAdapter membersAdapter;
    private ChoresAdapter choresAdapter;
    private DatabaseReference membersDatabase;
    private DatabaseReference choresDatabase;
    private DatabaseReference raceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        initPointsView();
        initMembersView();
        initChoresView();

        showPointsText();
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
        switch (item.getItemId()) {
            case R.id.action_manage_members:
                manageMembers();
                break;
            case R.id.action_manage_chores:
                manageChores();
                break;
            default:
                break;
        }

        return true;
    }

    private void manageMembers() {
        Intent intent = new Intent(this, MembersListActivity.class);
        startActivity(intent);
    }

    private void manageChores() {
        Intent intent = new Intent(this, ChoresListActivity.class);
        startActivity(intent);
    }

    private void initData() {
        data = new RallyeData();
        ((RallyeApplication) this.getApplication()).setRallyeData(data);

        membersDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_MEMBERS);
        membersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MemberItem> members = new ArrayList<>();
                for (DataSnapshot memberDataSnapshot : dataSnapshot.getChildren()) {
                    MemberItem member = memberDataSnapshot.getValue(MemberItem.class);
                    members.add(member);
                }
                membersAdapter.updateList(members);
                showPointsText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        choresDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_CHORES);
        choresDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChoreItem> chores = new ArrayList<>();
                for (DataSnapshot choreDataSnapshot : dataSnapshot.getChildren()) {
                    ChoreItem chore = choreDataSnapshot.getValue(ChoreItem.class);
                    chores.add(chore);
                }
                choresAdapter.updateList(chores);
                showPointsText();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        raceDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_RACE);
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

    private void initPointsView() {
        pointsTextView = (TextView) findViewById(R.id.points_view);
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
}
