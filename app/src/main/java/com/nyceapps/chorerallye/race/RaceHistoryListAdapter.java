package com.nyceapps.chorerallye.race;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.main.RallyeData;
import com.nyceapps.chorerallye.main.Utils;
import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_RALLYE;

/**
 * Created by lugosi on 06.02.17.
 */
public class RaceHistoryListAdapter extends SimpleSectionedAdapter<RaceHistoryListAdapter.ViewHolder> {
    private RallyeData data;
    private RaceHistoryActivity callingActivity;
    private boolean includePoints;
    private List<String> raceHistorySections;
    private Map<String, List<RaceItem>> raceHistoryItems;
    private java.text.DateFormat dateFormat;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RaceHistoryListAdapter(RallyeData pData, RaceHistoryActivity pCallingActivity) {
        data = pData;
        callingActivity = pCallingActivity;

        includePoints = (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode()));
        dateFormat = DateFormat.getDateFormat(pCallingActivity);
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.race_history_list_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int section, int position) {
        RaceItem raceItem = null;

        String sectionKey = raceHistorySections.get(section);
        String raceHistoryItemText = "";
        List<RaceItem> raceHistoryItemList = raceHistoryItems.get(sectionKey);
        if (raceHistoryItemList != null) {
            raceItem = raceHistoryItemList.get(position);
            if (raceItem != null) {
                raceHistoryItemText = Utils.makeRaceItemText(raceItem.getMemberName(), raceItem.getChoreName(), raceItem.getChoreValue(), callingActivity, includePoints);
                if (!TextUtils.isEmpty(raceItem.getNote())) {
                    raceHistoryItemText += " (" + raceItem.getNote() + ")";
                }
            }
        }

        holder.raceHistoryItemTextView.setText(raceHistoryItemText);
        holder.raceHistoryItemTextView.setTag(raceItem);
    }

    public void updateList(List<RaceItem> pRaceItems) {
        if (pRaceItems.size() != data.getMembers().size() || !data.getMembers().containsAll(pRaceItems)) {
            prepareSectionData(pRaceItems);
            data.getRace().setRaceItems(pRaceItems);
            notifyDataSetChanged();
        }
    }

    private void prepareSectionData(List<RaceItem> pRaceItems) {
        raceHistorySections = new ArrayList<>();
        raceHistoryItems = new HashMap<>();

        for (int i = pRaceItems.size() - 1; i >= 0; i--) {
            RaceItem raceItem = pRaceItems.get(i);
            Date raceItemDate = raceItem.getDate();
            String raceItemDateStr = dateFormat.format(raceItemDate);
            if (!raceHistorySections.contains(raceItemDateStr)) {
                raceHistorySections.add(raceItemDateStr);
                raceHistoryItems.put(raceItemDateStr, new ArrayList<RaceItem>());
            }

            List<RaceItem> raceHistoryItemList = raceHistoryItems.get(raceItemDateStr);
            raceHistoryItemList.add(raceItem);
            raceHistoryItems.put(raceItemDateStr, raceHistoryItemList);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView raceHistoryItemTextView;

        public ViewHolder(View v) {
            super(v);
            raceHistoryItemTextView = (TextView) v.findViewById(R.id.race_history_list_item);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RaceItem raceItem = (RaceItem) raceHistoryItemTextView.getTag();
            if (raceItem != null) {
                callingActivity.editRaceHistoryItemNote(raceItem);
            }
        }
    }

    @Override
    protected String getSectionHeaderTitle(int section) {
        if (raceHistorySections == null) {
            return "";
        }
        return raceHistorySections.get(section);
    }

    @Override
    protected int getSectionCount() {
        if (raceHistorySections == null) {
            return 0;
        }
        return raceHistorySections.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        if (raceHistorySections == null) {
            return 0;
        }
        String sectionKey = raceHistorySections.get(section);
        List<RaceItem> raceHistoryItemList = raceHistoryItems.get(sectionKey);
        if (raceHistoryItemList == null) {
            return 0;
        }
        return raceHistoryItemList.size();
    }
}
