package com.ss_salt.android.taskee.packages.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;

import com.ss_salt.android.taskee.R;
import com.ss_salt.android.taskee.packages.activites.TaskListActivity;

public class MainPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);

        ListPreference themeListPreference =
                (ListPreference) findPreference(getResources().getString(R.string.key_theme));
        themeListPreference.setOnPreferenceChangeListener
                (new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        restartActivity();
                        return true;
                    }
                });
    }

    private void restartActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), TaskListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
