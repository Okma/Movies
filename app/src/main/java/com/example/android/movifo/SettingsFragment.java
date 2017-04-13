package com.example.android.movifo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.movifo.sync.SyncMovieDataTask;

/**
 * Created by Carl on 4/9/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        // Assign preference summaries on creation.
        for(int i = 0; i < preferenceScreen.getPreferenceCount(); ++i) {

            Preference preferenceAtIndex = preferenceScreen.getPreference(i);
            if(!(preferenceAtIndex instanceof CheckBoxPreference)) {
                setPreferenceSummary(preferenceAtIndex, sharedPreferences.getString(preferenceAtIndex.getKey(), ""));
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register this as shared preference listener.
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister this as shared preference listener.
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Find the changed preference by key.
        Preference preference = findPreference(key);

        // If preference is valid and not a checkbox, set its summary.
        if(preference != null) {
            if (!(preference instanceof CheckBoxPreference)) {
                final String PREFERENCE_VALUE = sharedPreferences.getString(key, "");
                setPreferenceSummary(preference, PREFERENCE_VALUE);
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {

        // Only handling list preference for now.
        if(!(preference instanceof ListPreference)) return;

        ListPreference listPreference = (ListPreference) preference;
        int indexOfSelection = listPreference.findIndexOfValue(value);

        if(indexOfSelection >= 0) {
            listPreference.setSummary(listPreference.getEntries()[indexOfSelection]);
        }
    }
}
