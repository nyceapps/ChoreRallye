package com.nyceapps.chorerallye;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import java.util.UUID;

import static com.nyceapps.chorerallye.Constants.HOUSEHOLD_ID_INFIX;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_ID;
import static com.nyceapps.chorerallye.Constants.PREF_KEY_HOUSEHOLD_NAME;

/**
 * Created by bela on 22.02.17.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private String previousHouseholdName;

    private SharedPreferences sharedPreferences;
    private EditTextPreference prefHouseholdName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        prefHouseholdName = (EditTextPreference) findPreference(PREF_KEY_HOUSEHOLD_NAME);
        prefHouseholdName.setOnPreferenceChangeListener(this);
        previousHouseholdName = prefHouseholdName.getText();

        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        setHouseholdNameSummary();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, final Object newValue) {
        if (PREF_KEY_HOUSEHOLD_NAME.equals(preference.getKey())) {
            if (newValue instanceof String) {
                final String householdName = (String) newValue;
                if (!TextUtils.isEmpty(householdName) && !householdName.equals(previousHouseholdName)) {
                    if (!TextUtils.isEmpty(previousHouseholdName)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.confirmation_text_change_household_name)
                                .setPositiveButton(R.string.dialog_button_text_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        setHouseholdData(householdName);
                                    }
                                })
                                .setNegativeButton(R.string.dialog_button_text_cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.create().show();
                    } else {
                        setHouseholdData(householdName);
                    }
                }
            }
            return false;
        }

        return true;
    }

    private void setHouseholdData(String pHouseholdName) {
        setHouseholdName(pHouseholdName);
        setHouseholdId();
        setHouseholdNameSummary();
    }

    private void setHouseholdName(String pHouseholdName) {
        if (!TextUtils.isEmpty(pHouseholdName)) {
            String householdName = pHouseholdName.trim().toUpperCase();
            previousHouseholdName = householdName;
            prefHouseholdName.setText(householdName);
        }
    }

    public void setHouseholdId() {
        String householdName = prefHouseholdName.getText();
        Utils.setHouseholdId(householdName, this);
    }

    private void setHouseholdNameSummary() {
        String householdNameSummary = prefHouseholdName.getText();
        if (TextUtils.isEmpty(householdNameSummary)) {
            householdNameSummary = getString(R.string.pref_summary_household_name);
        }
        prefHouseholdName.setSummary(householdNameSummary);
    }
}
