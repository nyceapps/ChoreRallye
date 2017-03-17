package com.nyceapps.chorerallye;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_ITEMS;
import static com.nyceapps.chorerallye.Constants.DATABASE_SUBPATH_RACE;

public class RaceHistoryActivity extends AppCompatActivity {
    private RallyeData data;
    private RaceHistoryListAdapter raceHistoryListAdapter;
    private DatabaseReference raceHistoryDatabase;
    private ProgressDialog savingDataDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_history);

        RecyclerView raceHistoryListView = (RecyclerView) findViewById(R.id.race_history_list_view);

        raceHistoryListView.setHasFixedSize(true);

        LinearLayoutManager raceViewListLayoutManager = new LinearLayoutManager(this);
        raceHistoryListView.setLayoutManager(raceViewListLayoutManager);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();
        raceHistoryListAdapter = new RaceHistoryListAdapter(data, this);
        raceHistoryListView.setAdapter(raceHistoryListAdapter);

        String householdId = Utils.getHouseholdId(this);
        raceHistoryDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_RACE + "/" + DATABASE_SUBPATH_ITEMS);
        raceHistoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RaceItem> raceItems = new ArrayList<>();
                for (DataSnapshot raceDataSnapshot : dataSnapshot.getChildren()) {
                    RaceItem raceItem = raceDataSnapshot.getValue(RaceItem.class);
                    raceItems.add(raceItem);
                }
                raceHistoryListAdapter.updateList(raceItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeRaceHistoryItem(final RaceItem pRaceItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation_text_remove_race_history_item))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showSavingDataDialog();

                        raceHistoryDatabase.child(pRaceItem.getUid()).removeValue();

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
