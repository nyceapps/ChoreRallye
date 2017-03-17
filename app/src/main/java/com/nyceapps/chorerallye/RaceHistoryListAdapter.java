package com.nyceapps.chorerallye;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.nyceapps.chorerallye.Constants.CONTEXT_MENU_ACTION_EDIT;
import static com.nyceapps.chorerallye.Constants.CONTEXT_MENU_ACTION_REMOVE;
import static com.nyceapps.chorerallye.Constants.DISPLAY_MODE_RALLYE;

/**
 * Created by lugosi on 06.02.17.
 */
public class RaceHistoryListAdapter extends RecyclerView.Adapter<RaceHistoryListAdapter.ViewHolder> {
    private RallyeData data;
    private RaceHistoryActivity callingActivity;
    private final boolean includePoints;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RaceHistoryListAdapter(RallyeData pData, RaceHistoryActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;

        includePoints = (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode()));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.race_history_list_item_layout, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RaceItem raceItem = data.getRace().getRaceItems().get(position);

        String raceHistoryItemText = Utils.makeRaceItemText(raceItem.getMemberName(), raceItem.getChoreName(), raceItem.getChoreValue(), callingActivity, includePoints);
        holder.raceHistoryItemTextView.setText(raceHistoryItemText);
        holder.raceHistoryItemTextView.setTag(raceItem);
    }

    public void updateList(List<RaceItem> pRaceItems) {
        if (pRaceItems.size() != data.getMembers().size() || !data.getMembers().containsAll(pRaceItems)) {
            data.getRace().setRaceItems(pRaceItems);
            notifyDataSetChanged();
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        // each data item is just a string in this case
        public TextView raceHistoryItemTextView;

        public ViewHolder(View v) {
            super(v);
            raceHistoryItemTextView = (TextView) v.findViewById(R.id.race_history_list_item);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(raceHistoryItemTextView.getText());
            MenuItem menuItemRemove = menu.add(Menu.NONE, CONTEXT_MENU_ACTION_REMOVE, Menu.NONE, R.string.list_context_menu_remove);
            menuItemRemove.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            RaceItem raceItem = (RaceItem) raceHistoryItemTextView.getTag();
            switch (item.getItemId()) {
                case CONTEXT_MENU_ACTION_REMOVE:
                    callingActivity.removeRaceHistoryItem(raceItem);
                    break;
                default:
            }
            return true;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.getRace().getRaceItems().size();
    }
}
