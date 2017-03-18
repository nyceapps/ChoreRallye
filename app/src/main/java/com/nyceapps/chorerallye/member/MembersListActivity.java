package com.nyceapps.chorerallye.member;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.RallyeApplication;
import com.nyceapps.chorerallye.main.RallyeData;
import com.nyceapps.chorerallye.main.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.nyceapps.chorerallye.main.Constants.DATABASE_CHILD_KEY_MEMBER_NAME;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_ITEMS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_MEMBERS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_RACE;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_FILE_STRING;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_NAME;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_ORIGINAL_NAME;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_UID;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_ADD_MEMBER;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_EDIT_MEMBER;

public class MembersListActivity extends AppCompatActivity {
    private RallyeData data;
    private MembersListAdapter membersListAdapter;
    private DatabaseReference membersDatabase;
    private DatabaseReference raceDatabase;
    private ProgressDialog savingDataDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);

        RecyclerView membersListView = (RecyclerView) findViewById(R.id.members_list_view);

        membersListView.setHasFixedSize(true);

        LinearLayoutManager membersListLayoutManager = new LinearLayoutManager(this);
        membersListView.setLayoutManager(membersListLayoutManager);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();
        membersListAdapter = new MembersListAdapter(data, this);
        membersListView.setAdapter(membersListAdapter);

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
                membersListAdapter.updateList(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        raceDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_RACE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_detail:
                addMember();
                break;
            default:
                break;
        }

        return true;
    }

    public void addMember() {
        Intent intent = new Intent(this, MemberDetailActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_MEMBER);
    }

    public void editMember(final MemberItem pMember) {
        Intent intent = new Intent(this, MemberDetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE_UID, pMember.getUid());
        intent.putExtra(EXTRA_MESSAGE_NAME, pMember.getName());
        intent.putExtra(EXTRA_MESSAGE_ORIGINAL_NAME, pMember.getName());
        intent.putExtra(EXTRA_MESSAGE_FILE_STRING, pMember.getImageString());
        startActivityForResult(intent, REQUEST_CODE_EDIT_MEMBER);
    }

    public void removeMember(final MemberItem pMember) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.confirmation_text_remove_member), pMember.getName()))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showSavingDataDialog();

                        if (data.getRace().hasMember(pMember.getUid())) {
                            Set<String> removedRaceItems = data.getRace().removeMembers(pMember.getUid());
                            for (String removedUid : removedRaceItems) {
                                raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(removedUid).removeValue();
                            }
                        }
                        membersDatabase.child(pMember.getUid()).removeValue();

                        hideSavingDataDialog();
                    }
                })
                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            String memberName = intent.getStringExtra(EXTRA_MESSAGE_NAME);
            if (!TextUtils.isEmpty(memberName)) {
                showSavingDataDialog();

                MemberItem member = new MemberItem();
                String uid = null;
                switch (requestCode) {
                    case REQUEST_CODE_ADD_MEMBER:
                        uid = membersDatabase.push().getKey();
                        break;
                    case REQUEST_CODE_EDIT_MEMBER:
                        uid = intent.getStringExtra(EXTRA_MESSAGE_UID);
                        String memberOriginalName = intent.getStringExtra(EXTRA_MESSAGE_ORIGINAL_NAME);
                        if (!memberName.equals(memberOriginalName) && data.getRace().hasMember(uid)) {
                            Set<String> updatedRaceItems = data.getRace().updateMemberNames(uid, memberName);
                            for (String updatedUid : updatedRaceItems) {
                                raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(updatedUid).child(DATABASE_CHILD_KEY_MEMBER_NAME).setValue(memberName);
                            }
                        }
                        break;
                }
                member.setUid(uid);
                member.setName(memberName);
                String memberImageString = intent.getStringExtra(EXTRA_MESSAGE_FILE_STRING);
                if (!TextUtils.isEmpty(memberImageString)) {
                    member.setImageString(memberImageString);
                }

                membersDatabase.child(uid).setValue(member);

                hideSavingDataDialog();
            }
        }
    }

    private void showSavingDataDialog() {
        if (savingDataDialog != null && savingDataDialog.isShowing()) {
            return;
        }
        savingDataDialog = ProgressDialog.show(this, getString(R.string.dialog_text_saving_data), getString(R.string.dialog_text_please_wait), true);
    }

    private void hideSavingDataDialog() {
        if (savingDataDialog != null && savingDataDialog.isShowing()) {
            savingDataDialog.dismiss();
        }
    }
}
