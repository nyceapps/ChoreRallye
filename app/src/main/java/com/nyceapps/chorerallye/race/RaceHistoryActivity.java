package com.nyceapps.chorerallye.race;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_ITEMS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_RACE;

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

        ItemTouchHelper raceHistoryTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, RIGHT) {
                    @Override
                    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        return makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, RIGHT);
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        RaceItem raceItem = data.getRace().getRaceItems().get(adapterPosition);
                        removeRaceHistoryItem(raceItem);
                    }
                });
        raceHistoryTouchHelper.attachToRecyclerView(raceHistoryListView);


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
                        raceHistoryListAdapter.notifyDataSetChanged();
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
