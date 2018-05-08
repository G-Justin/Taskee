package com.ss_salt.android.taskee.packages.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import android.support.annotation.Nullable;
import android.view.MenuItem;


import com.ss_salt.android.taskee.R;
import com.ss_salt.android.taskee.packages.fragments.MainPreferenceFragment;
import com.ss_salt.android.taskee.packages.models.PreferenceUtils;
import com.ss_salt.android.taskee.packages.models.ThemeUtils;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity
implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setPreferredTheme();
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MainPreferenceFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPreferredTheme() {
        String themeValue = PreferenceUtils.getThemePreferenceValue(this);
        ThemeUtils.setActivityTheme(this, themeValue);
    }

}
