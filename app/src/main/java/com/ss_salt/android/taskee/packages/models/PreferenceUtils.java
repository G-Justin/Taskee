package com.ss_salt.android.taskee.packages.models;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.ss_salt.android.taskee.R;

import java.util.prefs.PreferenceChangeEvent;

public class PreferenceUtils {


    //=====================================================================================
    // Accessors
    //=====================================================================================

    public static String getThemePreferenceValue(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);

        return sharedPreferences.getString("KEY_APP_THEME", "TaskeePurple");
    }
}