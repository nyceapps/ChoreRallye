package com.nyceapps.chorerallye.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_DISPLAYED_RACE_ITEMS_COUNT;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_PREFIX_DISPLAYED_RACE_ITEMS_ENTRY;

/**
 * Created by lugosi on 26.02.17.
 */

public class DisplayedRaceItems {
    private final static String TAG = DisplayedRaceItems.class.getSimpleName();

    private final Context context;

    private List<String> entries;

    public DisplayedRaceItems(Context pContext) {
        context = pContext;
        load();
    }

    public void add(String pEntry) {
        entries.add(pEntry);
    }

    public void init() {
        entries = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int displayedRaceItemsCount = sharedPreferences.getInt(PREF_KEY_DISPLAYED_RACE_ITEMS_COUNT, 0);
        if (displayedRaceItemsCount > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREF_KEY_DISPLAYED_RACE_ITEMS_COUNT, 0);
            for (int i = 0; i < entries.size(); i++) {
                String key = PREF_KEY_PREFIX_DISPLAYED_RACE_ITEMS_ENTRY + i;
                editor.remove(key);
            }
            editor.commit();
        }
    }

    private void load() {
        entries = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int displayedRaceItemsCount = sharedPreferences.getInt(PREF_KEY_DISPLAYED_RACE_ITEMS_COUNT, 0);
        Log.d(TAG, String.format("displayedRaceItemsCount = [%d]", displayedRaceItemsCount));
        for (int i = 0; i < displayedRaceItemsCount; i++) {
            String key = PREF_KEY_PREFIX_DISPLAYED_RACE_ITEMS_ENTRY + i;
            String value = sharedPreferences.getString(key, null);
            if (!TextUtils.isEmpty(value)) {
                entries.add(value);
            }
        }
    }

    public void save() {
        if (entries != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREF_KEY_DISPLAYED_RACE_ITEMS_COUNT, entries.size());
            for (int i = 0; i < entries.size(); i++) {
                String key = PREF_KEY_PREFIX_DISPLAYED_RACE_ITEMS_ENTRY + i;
                editor.putString(key, entries.get(i));
            }
            editor.commit();
        }
    }

    public boolean contains(String pEntry) {
        if (entries != null) {
            return entries.contains(pEntry);
        }
        return false;
    }
}
