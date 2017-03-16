package com.nyceapps.chorerallye;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.DISPLAY_MODE_RALLYE;

public class RaceHistoryActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RallyeData data = ((RallyeApplication) this.getApplication()).getRallyeData();

        final List<String> list = new ArrayList<>();
        for (RaceItem raceItem : data.getRace().getRaceItems()) {
            boolean includePoints = (DISPLAY_MODE_RALLYE.equals(data.getSettings().getDisplayMode()));
            String historyItem = Utils.makeRaceItemText(raceItem.getMemberName(), raceItem.getChoreName(), raceItem.getChoreValue(), this, includePoints);
            list.add(historyItem);
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        setListAdapter(adapter);
    }
}
