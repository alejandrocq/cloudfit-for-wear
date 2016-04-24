package com.alejandro_castilla.cloudfitforwear.activities;

import android.os.Bundle;

import com.alejandro_castilla.cloudfitforwear.R;

import preference.WearPreferenceActivity;

public class SettingsActivity extends WearPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setTitle(getString(R.string.settings_title));
    }
}
