package com.nyceapps.chorerallye;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.nyceapps.chorerallye.Constants.PREF_KEY_LOCAL_HISTORY_COUNT;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_PREFEIX_LOCAL_HISTORY_ENTRY;

/**
 * Created by lugosi on 26.02.17.
 */

public class LocalHistory {
    private final static String TAG = LocalHistory.class.getSimpleName();

    private final Context context;

    List<String> entries;

    public LocalHistory(Context pContext) {
        context = pContext;
        load();
    }

    public void add(String pEntry) {
        entries.add(pEntry);
    }

    public String undo() {
        if (entries.size() > 0) {
            return entries.remove(entries.size() - 1);
        }

        return null;
    }

    public void init() {
        entries = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int localHistoryCount = sharedPreferences.getInt(PREF_KEY_LOCAL_HISTORY_COUNT, 0);
        if (localHistoryCount > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREF_KEY_LOCAL_HISTORY_COUNT, 0);
            for (int i = 0; i < entries.size(); i++) {
                String key = PREF_KEY_PREFEIX_LOCAL_HISTORY_ENTRY + i;
                editor.remove(key);
            }
            editor.commit();
        }
    }

    public void load() {
        entries = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int localHistoryCount = sharedPreferences.getInt(PREF_KEY_LOCAL_HISTORY_COUNT, 0);
        Log.d(TAG, String.format("localHistoryCount = [%d]", localHistoryCount));
        for (int i = 0; i < localHistoryCount; i++) {
            String key = PREF_KEY_PREFEIX_LOCAL_HISTORY_ENTRY + i;
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
            editor.putInt(PREF_KEY_LOCAL_HISTORY_COUNT, entries.size());
            for (int i = 0; i < entries.size(); i++) {
                String key = PREF_KEY_PREFEIX_LOCAL_HISTORY_ENTRY + i;
                editor.putString(key, entries.get(i));
            }
            editor.commit();
        }
    }
}
