package com.nyceapps.chorerallye.race;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_ITEMS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_RACE;
import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_RALLYE;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_full_history:
                deleteFullRaceHistory();
                break;
            default:
                break;
        }

        return true;
    }

    public void deleteFullRaceHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmation_text_delete_full_race_history))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showSavingDataDialog();

                        Map<String, Object> raceItemsMap = new HashMap<>();
                        for (RaceItem raceItem : data.getRace().getRaceItems()) {
                            raceItemsMap.put(raceItem.getUid(), null);
                        }
                        raceHistoryDatabase.updateChildren(raceItemsMap);

                        hideSavingDataDialog();
                    }
                })
                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel the dialog
                    }
                });
        builder.create().show();
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

    public void editRaceHistoryItemNote(final RaceItem pRaceItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_race_history_list_item_note, null);
        final EditText raceHistoryItemNoteEditText = (EditText) dialogView.findViewById(R.id.race_history_item_note);
        raceHistoryItemNoteEditText.setText(pRaceItem.getNote());
        boolean includePoints = (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode()));
        String raceHistoryItemText = Utils.makeRaceItemText(pRaceItem.getMemberName(), pRaceItem.getChoreName(), pRaceItem.getChoreValue(), this, includePoints);
        builder.setView(dialogView);
        builder.setMessage(String.format(getString(R.string.dialog_text_note_for_race_history_item), raceHistoryItemText))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showSavingDataDialog();

                        String raceHistoryItemNote = raceHistoryItemNoteEditText.getText().toString();
                        pRaceItem.setNote(raceHistoryItemNote);
                        raceHistoryDatabase.child(pRaceItem.getUid()).setValue(pRaceItem);

                        hideSavingDataDialog();
                    }
                })
                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog.
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
