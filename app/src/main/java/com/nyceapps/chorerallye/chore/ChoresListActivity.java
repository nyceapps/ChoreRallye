package com.nyceapps.chorerallye.chore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.nyceapps.chorerallye.main.DialogManager;
import com.nyceapps.chorerallye.main.RallyeApplication;
import com.nyceapps.chorerallye.main.RallyeData;
import com.nyceapps.chorerallye.main.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.nyceapps.chorerallye.main.Constants.DATABASE_CHILD_KEY_CHORE_NAME;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_CHILD_KEY_CHORE_VALUE;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_KEY_ORDER_KEY;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_CHORES;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_ITEMS;
import static com.nyceapps.chorerallye.main.Constants.DATABASE_SUBPATH_RACE;
import static com.nyceapps.chorerallye.main.Constants.EXTRA_MESSAGE_CHORE;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_ADD_CHORE;
import static com.nyceapps.chorerallye.main.Constants.REQUEST_CODE_EDIT_CHORE;

public class ChoresListActivity extends AppCompatActivity {
    private DialogManager dialogManager;
    private RallyeData data;
    private ChoresListAdapter choresListAdapter;
    private DatabaseReference choresDatabase;
    private DatabaseReference raceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores_list);

        dialogManager = new DialogManager(this);

        RecyclerView choresListView = (RecyclerView) findViewById(R.id.chores_list_view);

        choresListView.setHasFixedSize(true);

        LinearLayoutManager choresListLayoutManager = new LinearLayoutManager(this);
        choresListView.setLayoutManager(choresListLayoutManager);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();
        choresListAdapter = new ChoresListAdapter(data, this);
        choresListView.setAdapter(choresListAdapter);

        ItemTouchHelper choreItemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        int fromPos = viewHolder.getAdapterPosition();
                        int toPos = target.getAdapterPosition();
                        swapChores(fromPos, toPos);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        ChoreItem chore = data.getChores().get(adapterPosition);
                        removeChore(chore);
                    }
                });
        choreItemTouchHelper.attachToRecyclerView(choresListView);

        String householdId = Utils.getHouseholdId(this);
        choresDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_CHORES);
        choresDatabase.orderByChild(DATABASE_KEY_ORDER_KEY).addValueEventListener(new ValueEventListener() {
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

        raceDatabase = FirebaseDatabase.getInstance().getReference(householdId + "/" + DATABASE_SUBPATH_RACE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        List<ChoreItem> chores = data.getChores();
        for (int i = 0; i < chores.size(); i++) {
            ChoreItem chore = chores.get(i);
            choresDatabase.child(chore.getUid()).child(DATABASE_KEY_ORDER_KEY).setValue(i);
        }
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
        intent.putExtra(EXTRA_MESSAGE_CHORE, new ChoreItem());
        startActivityForResult(intent, REQUEST_CODE_ADD_CHORE);
    }

    public void editChore(ChoreItem pChore) {
        int orderKey = pChore.getOrderKey();
        List<ChoreItem> chores = data.getChores();
        for (int i = 0; i < chores.size(); i++) {
            if (pChore.getUid().equals(chores.get(i).getUid())) {
                orderKey = i;
                break;
            }
        }
        pChore.setOrderKey(orderKey);
        pChore.setNameUpdate(false);
        pChore.setValueUpdate(false);

        Intent intent = new Intent(this, ChoreDetailActivity.class);
        intent.putExtra(EXTRA_MESSAGE_CHORE, pChore);
        startActivityForResult(intent, REQUEST_CODE_EDIT_CHORE);
    }

    private void swapChores(int fromPos, int toPos) {
        List<ChoreItem> chores = data.getChores();
        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(chores, i, i + 1);
            }
        } else {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(chores, i, i - 1);
            }
        }
        choresListAdapter.notifyItemMoved(fromPos, toPos);
    }

    public void removeChore(final ChoreItem pChore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.confirmation_text_remove_chore), pChore.getName()))
                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogManager.showSavingDataDialog();

                        if (data.getRace().hasChore(pChore.getUid())) {
                            Set<String> removedRaceItems = data.getRace().removeChores(pChore.getUid());
                            for (String removedUid : removedRaceItems) {
                                raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(removedUid).removeValue();
                            }
                        }
                        choresDatabase.child(pChore.getUid()).removeValue();
                        //choresListAdapter.notifyDataSetChanged();

                        dialogManager.hideSavingDataDialog();
                    }
                })
                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        choresListAdapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            boolean databaseUpdateNeedsConfirmation = false;
            boolean updateChoreNamesInDatabase = false;

            final ChoreItem chore = intent.getParcelableExtra(EXTRA_MESSAGE_CHORE);
            String choreName = chore.getName();
            int choreValue = chore.getValue();
            if (!TextUtils.isEmpty(choreName) && choreValue > 0) {
                dialogManager.showSavingDataDialog();

                String uid = null;
                switch (requestCode) {
                    case REQUEST_CODE_ADD_CHORE:
                        uid = choresDatabase.push().getKey();
                        chore.setUid(uid);
                        break;
                    case REQUEST_CODE_EDIT_CHORE:
                        uid = chore.getUid();
                        if (chore.hasValueUpdate() && data.getRace().hasChore(uid)) {
                            databaseUpdateNeedsConfirmation = (data.getRace().hasChore(uid));
                        }
                        if (chore.hasNameUpdate() && data.getRace().hasChore(uid)) {
                            updateChoreNamesInDatabase = true;
                        }

                        break;
                }

                if (databaseUpdateNeedsConfirmation) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final boolean finalUpdateChoreNamesInDatabase = updateChoreNamesInDatabase;
                    builder.setMessage(String.format(getString(R.string.confirmation_text_update_chore_value), chore.getName()))
                            .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialogManager.showSavingDataDialog();

                                    Set<String> updatedRaceItemsForValue = data.getRace().updateChoreValues(chore.getUid(), chore.getValue());
                                    for (String updatedUid : updatedRaceItemsForValue) {
                                        raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(updatedUid).child(DATABASE_CHILD_KEY_CHORE_VALUE).setValue(chore.getValue());
                                    }
                                    if (finalUpdateChoreNamesInDatabase) {
                                        Set<String> updatedRaceItemsForName = data.getRace().updateChoreNames(chore.getUid(), chore.getName());
                                        for (String updatedUid : updatedRaceItemsForName) {
                                            raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(updatedUid).child(DATABASE_CHILD_KEY_CHORE_NAME).setValue(chore.getName());
                                        }
                                    }
                                    choresDatabase.child(chore.getUid()).setValue(chore);

                                    dialogManager.hideSavingDataDialog();
                                }
                            })
                            .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.create().show();

                } else {
                    if (updateChoreNamesInDatabase) {
                        Set<String> updatedRaceItems = data.getRace().updateChoreNames(uid, choreName);
                        for (String updatedUid : updatedRaceItems) {
                            raceDatabase.child(DATABASE_SUBPATH_ITEMS).child(updatedUid).child(DATABASE_CHILD_KEY_CHORE_NAME).setValue(choreName);
                        }
                    }
                    choresDatabase.child(uid).setValue(chore);
                }

                dialogManager.hideSavingDataDialog();
            }
        }
    }
}
