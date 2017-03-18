package com.nyceapps.chorerallye.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.nyceapps.chorerallye.R;

/**
 * Created by bela on 22.02.17.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private RallyeData data;
    private String previousHouseholdName;

    private SharedPreferences sharedPreferences;

    private EditTextPreference prefHouseholdName;
    private EditTextPreference prefWinningPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        setHouseholdNamePreference();
        setWinningPercentagePreference();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, final Object newValue) {
        String prefKey = preference.getKey();
        if (Constants.PREF_KEY_HOUSEHOLD_NAME.equals(prefKey)) {
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
        } else if (Constants.PREF_KEY_WINNING_PERCENTAGE.equals(prefKey)) {
            if (newValue instanceof String) {
                String winningPerecentageStr = (String) newValue;
                int winningPercentage = Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
                if (!TextUtils.isEmpty(winningPerecentageStr) && TextUtils.isDigitsOnly(winningPerecentageStr)) {
                    winningPercentage = Integer.parseInt(winningPerecentageStr);
                }
                data.getSettings().setWinningPercentage(winningPercentage);
                setWinningPercentageValue();
                setWinningPercentageSummary();
            }
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
        Utils.setHouseholdIdByName(householdName, this);
    }

    private void setHouseholdNamePreference() {
        prefHouseholdName = (EditTextPreference) findPreference(Constants.PREF_KEY_HOUSEHOLD_NAME);
        prefHouseholdName.setOnPreferenceChangeListener(this);
        previousHouseholdName = prefHouseholdName.getText();

        setHouseholdNameSummary();
    }

    private void setHouseholdNameSummary() {
        String householdNameSummary = prefHouseholdName.getText();
        if (TextUtils.isEmpty(householdNameSummary)) {
            householdNameSummary = getString(R.string.pref_summary_household_name);
        }
        prefHouseholdName.setSummary(householdNameSummary);
    }

    private void setWinningPercentagePreference() {
        prefWinningPercentage = (EditTextPreference) findPreference(Constants.PREF_KEY_WINNING_PERCENTAGE);
        prefWinningPercentage.setOnPreferenceChangeListener(this);

        setWinningPercentageValue();
        setWinningPercentageSummary();
    }

    private void setWinningPercentageValue() {
        int winningPercentage = Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
        if (data != null) {
            winningPercentage = data.getSettings().getWinningPercentage();
        }
        prefWinningPercentage.setText(String.valueOf(winningPercentage));
    }

    private void setWinningPercentageSummary() {
        int winningPercentage = Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
        if (data != null) {
            winningPercentage = data.getSettings().getWinningPercentage();
        }
        prefWinningPercentage.setSummary(String.valueOf(winningPercentage));
    }
}
