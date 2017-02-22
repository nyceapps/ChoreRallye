package com.nyceapps.chorerallye;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_CHORES;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_FILE_STRING;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_NAME;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_UID;
import static com.nyceapps.chorerallye.Constants.EXTRA_MESSAGE_VALUE;
import static com.nyceapps.chorerallye.Constants.PREFS_FILE_NAME;
import static com.nyceapps.chorerallye.Constants.PREFS_KEY_HOUSEHOLD_NAME;
import static com.nyceapps.chorerallye.Constants.REQUEST_CODE_ADD_CHORE;
import static com.nyceapps.chorerallye.Constants.REQUEST_CODE_EDIT_CHORE;

public class ChoresListActivity extends AppCompatActivity {
    private RallyeData data;
    private ChoresListAdapter choresListAdapter;
    private DatabaseReference choresDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores_list);

        RecyclerView choresListView = (RecyclerView) findViewById(R.id.chores_list_view);

        choresListView.setHasFixedSize(true);

        LinearLayoutManager choresListLayoutManager = new LinearLayoutManager(this);
        choresListView.setLayoutManager(choresListLayoutManager);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();
        choresListAdapter = new ChoresListAdapter(data, this);
        choresListView.setAdapter(choresListAdapter);

        SharedPreferences sharedPrefs = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        String householdName = sharedPrefs.getString(PREFS_KEY_HOUSEHOLD_NAME, null);
        choresDatabase = FirebaseDatabase.getInstance().getReference(householdName + "/" + DATABASE_SUBPATH_CHORES);
        choresDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChoreItem> chores = new ArrayList<>();
                for (DataSnapshot choreDataSnapshot : dataSnapshot.getChildren()) {
                    ChoreItem chore = choreDataSnapshot.getValue(ChoreItem.class);
                    chores.add(chore);
                }
                choresListAdapter.updateList(chores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                addChore();
                break;
            default:
                break;
        }

        return true;
    }

    public void addChore() {
        Intent intent = new Intent(this, ChoreDetailActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_CHORE);
    }

    public void editChore(ChoreItem pChore) {
        Intent intent = new Intent(this, ChoreDetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE_UID, pChore.getUid());
        intent.putExtra(EXTRA_MESSAGE_NAME, pChore.getName());
        intent.putExtra(EXTRA_MESSAGE_VALUE, pChore.getValue());
        intent.putExtra(EXTRA_MESSAGE_FILE_STRING, pChore.getImageString());
        startActivityForResult(intent, REQUEST_CODE_EDIT_CHORE);
    }

    public void removeChore(final ChoreItem pChore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.confirmation_text_remove_chore), pChore.getName()))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        choresDatabase.child(pChore.getUid()).removeValue();
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
            String choreName = intent.getStringExtra(EXTRA_MESSAGE_NAME);
            int choreValue = intent.getIntExtra(EXTRA_MESSAGE_VALUE, -1);
            if (!Utils.isEmptyString(choreName) && choreValue > 0) {
                ChoreItem chore = new ChoreItem();
                String uid = null;
                switch (requestCode) {
                    case REQUEST_CODE_ADD_CHORE:
                        uid = choresDatabase.push().getKey();
                        break;
                    case REQUEST_CODE_EDIT_CHORE:
                        uid = intent.getStringExtra(EXTRA_MESSAGE_UID);
                        break;
                }
                chore.setUid(uid);
                chore.setName(choreName);
                chore.setValue(choreValue);
                String choreImageString = intent.getStringExtra(EXTRA_MESSAGE_FILE_STRING);
                if (!Utils.isEmptyString(choreImageString)) {
                    chore.setImageString(choreImageString);
                }

                choresDatabase.child(uid).setValue(chore);
            }
        }
    }
}
