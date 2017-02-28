package com.nyceapps.chorerallye;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class RaceHistoryActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RallyeData data = ((RallyeApplication) this.getApplication()).getRallyeData();

        final List<String> list = new ArrayList<>();
        for (RaceItem raceItem : data.getRace().getRaceItems()) {
            String historyItem = Utils.makeRaceItemText(raceItem.getMemberName(), raceItem.getChoreName(), raceItem.getChoreValue(), this);
            list.add(historyItem);
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        setListAdapter(adapter);
    }
}
