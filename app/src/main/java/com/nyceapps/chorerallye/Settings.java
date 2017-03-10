package com.nyceapps.chorerallye;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;
import java.util.UUID;

import static com.nyceapps.chorerallye.Constants.HOUSEHOLD_ID_INFIX;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_ID;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_NAME;
import static com.nyceapps.chorerallye.Constants.SETTINGS_KEY_IS_RUNNING;

/**
 * Created by lugosi on 09.03.17.
 */

public final class Settings {
    private static final String TAG = Settings.class.getSimpleName();

    private Settings() {
    }

    public static boolean isRunning(RallyeData pData) {
        Object isRunningObj = pData.getSettings().get(SETTINGS_KEY_IS_RUNNING);
        if (isRunningObj == null) {
            return false;
        }

        if (isRunningObj instanceof Boolean) {
            return (boolean) isRunningObj;
        }

        return false;
    }

    public static void setRunning(boolean pIsRunning, RallyeData pData, DatabaseReference pDatabase) {
        Map<String, Object> settings = pData.getSettings();
        settings.put(SETTINGS_KEY_IS_RUNNING, pIsRunning);
        pDatabase.setValue(settings);
    }

    public static String getHouseholdId(Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        String householdId = sharedPreferences.getString(PREF_KEY_HOUSEHOLD_ID, null);
        Log.d(TAG, String.format("householdId = [%s]", householdId));
        return householdId;
    }

    public static void setHouseholdIdByName(String pHouseholdName, Context pContext) {
        String householdId = null;
        if (!TextUtils.isEmpty(pHouseholdName)) {
            householdId = pHouseholdName + HOUSEHOLD_ID_INFIX + UUID.randomUUID().toString();
        }
        setHouseholdId(householdId, pContext);
    }

    public static void setHouseholdId(String pHouseholdId, Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_HOUSEHOLD_ID, pHouseholdId);
        editor.commit();
    }

    public static void setHouseholdName(String pHouseholdName, Context pContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_HOUSEHOLD_NAME, pHouseholdName);
        editor.commit();
    }
}
