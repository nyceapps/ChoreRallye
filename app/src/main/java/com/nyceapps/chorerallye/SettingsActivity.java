package com.nyceapps.chorerallye;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by bela on 22.02.17.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
