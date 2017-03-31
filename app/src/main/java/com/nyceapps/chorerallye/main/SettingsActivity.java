package com.nyceapps.chorerallye.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.nyceapps.chorerallye.R;

import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_HOUSEHOLD_NAME;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_INSTANTLY_ADD_RACE_ITEM_NOTE;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_LENGTH_OF_RALLYE_IN_DAYS;
import static com.nyceapps.chorerallye.main.Constants.PREF_KEY_WINNING_PERCENTAGE;
import static com.nyceapps.chorerallye.main.Constants.SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS;
import static com.nyceapps.chorerallye.main.Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;

/**
 * Created by bela on 22.02.17.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    private RallyeData data;
    private String previousHouseholdName;

    private SharedPreferences sharedPreferences;

    private EditTextPreference prefHouseholdName;
    private EditTextPreference prefWinningPercentage;
    private EditTextPreference prefLengthOfRallyeInDays;
    private CheckBoxPreference prefInstantlyAddRaceItemNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        setHouseholdNamePreference();
        setWinningPercentagePreference();
        setLengthOfRallyeInDaysPreference();
        setInstantlyAddRaceItemNotePreference();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, final Object newValue) {
        String prefKey = preference.getKey();
        if (newValue instanceof String) {
            if (PREF_KEY_HOUSEHOLD_NAME.equals(prefKey)) {
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
                return false;
            } else if (PREF_KEY_WINNING_PERCENTAGE.equals(prefKey)) {
                String winningPercentageStr = (String) newValue;
                int winningPercentage = SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
                if (!TextUtils.isEmpty(winningPercentageStr) && TextUtils.isDigitsOnly(winningPercentageStr)) {
                    winningPercentage = Integer.parseInt(winningPercentageStr);
                }
                data.getSettings().setWinningPercentage(winningPercentage);
                setWinningPercentageValue();
                setWinningPercentageSummary();
            } else if (PREF_KEY_LENGTH_OF_RALLYE_IN_DAYS.equals(prefKey)) {
                String lengthOfRallyeInDaysStr = (String) newValue;
                int lengthOfRallyeInDays = SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS;
                if (!TextUtils.isEmpty(lengthOfRallyeInDaysStr) && TextUtils.isDigitsOnly(lengthOfRallyeInDaysStr)) {
                    lengthOfRallyeInDays = Integer.parseInt(lengthOfRallyeInDaysStr);
                }
                data.getSettings().setLengthOfRallyeInDays(lengthOfRallyeInDays);
                setLengthOfRallyeInDaysValue();
                setLengthOfRallyeInDaysSummary();
            }
        }

        return true;
    }

    private void setHouseholdData(String pHouseholdName) {
        String[] householdIdWithName = Utils.getHouseholdIdWithName(pHouseholdName);
        String householdId = householdIdWithName[0];
        String householdName = householdIdWithName[1];

        Utils.setHouseholdName(householdName, this);
        Utils.setHouseholdId(householdId, this);

        setHouseholdName(householdName);
        setHouseholdNameSummary();
    }

    private void setHouseholdName(String pHouseholdName) {
        if (!TextUtils.isEmpty(pHouseholdName)) {
            previousHouseholdName = pHouseholdName;
            prefHouseholdName.setText(pHouseholdName);
        }
    }

    private void setHouseholdNamePreference() {
        prefHouseholdName = (EditTextPreference) findPreference(PREF_KEY_HOUSEHOLD_NAME);
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
        prefWinningPercentage = (EditTextPreference) findPreference(PREF_KEY_WINNING_PERCENTAGE);
        prefWinningPercentage.setOnPreferenceChangeListener(this);

        setWinningPercentageValue();
        setWinningPercentageSummary();
    }

    private void setWinningPercentageValue() {
        int winningPercentage = SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
        if (data != null) {
            winningPercentage = data.getSettings().getWinningPercentage();
        }
        prefWinningPercentage.setText(String.valueOf(winningPercentage));
    }

    private void setWinningPercentageSummary() {
        int winningPercentage = SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
        if (data != null) {
            winningPercentage = data.getSettings().getWinningPercentage();
        }
        prefWinningPercentage.setSummary(String.valueOf(winningPercentage));
    }

    private void setLengthOfRallyeInDaysPreference() {
        prefLengthOfRallyeInDays = (EditTextPreference) findPreference(PREF_KEY_LENGTH_OF_RALLYE_IN_DAYS);
        prefLengthOfRallyeInDays.setOnPreferenceChangeListener(this);

        setLengthOfRallyeInDaysValue();
        setLengthOfRallyeInDaysSummary();
    }

    private void setLengthOfRallyeInDaysValue() {
        int lengthOfRallyeInDays = SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS;
        if (data != null) {
            lengthOfRallyeInDays = data.getSettings().getLengthOfRallyeInDays();
        }
        prefLengthOfRallyeInDays.setText(String.valueOf(lengthOfRallyeInDays));
    }

    private void setLengthOfRallyeInDaysSummary() {
        int lengthOfRallyeInDays = SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS;
        if (data != null) {
            lengthOfRallyeInDays = data.getSettings().getLengthOfRallyeInDays();
        }
        prefLengthOfRallyeInDays.setSummary(String.valueOf(lengthOfRallyeInDays));
    }

    private void setInstantlyAddRaceItemNotePreference() {
        prefInstantlyAddRaceItemNote = (CheckBoxPreference) findPreference(PREF_KEY_INSTANTLY_ADD_RACE_ITEM_NOTE);
        prefInstantlyAddRaceItemNote.setOnPreferenceChangeListener(this);
    }
}
