package com.ss_salt.android.taskee.packages.models;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.ss_salt.android.taskee.R;
import com.ss_salt.android.taskee.packages.activites.SettingsActivity;


public class ThemeUtils {
    //=========================================================================================
    // Properties
    //=========================================================================================

    //=========================================================================================
    // Accessors
    //=========================================================================================

    public static void setActivityTheme(Activity activity, String themeValue) {
        switch (themeValue) {
            case "TaskeePurple":
                activity.setTheme(R.style.TaskeePurple);
                break;
            case "JanoskiGrey":
                activity.setTheme(R.style.JanoskiGrey);
                break;
            case "CottonCandy":
                activity.setTheme(R.style.CottonCandy);
                break;
            case "ApplePie":
                activity.setTheme(R.style.ApplePie);
                break;
            case "Leprechaun":
                activity.setTheme(R.style.Leprechaun);
                break;
            default:
                activity.setTheme(R.style.TaskeePurple);
                break;
        }
    }

}
