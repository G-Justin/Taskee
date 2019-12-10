package com.ss_salt.android.taskee.packages.fragments;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ss_salt.android.taskee.R;
import com.ss_salt.android.taskee.packages.activites.TaskListActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TaskListFragmentTest {

    @Rule
    public ActivityTestRule<TaskListActivity> mTaskListActivityActivityTestRule
            = new ActivityTestRule<>(TaskListActivity.class);


    @Test
    public void addTaskTest() {
        onView(withId(R.id.fab_add_task))
                .perform(click());

        onView(withId(R.id.edit_title))
                .perform(typeText("FUCK"));


        onView(withText("OK"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("FUCK"))
                .check(matches(isDisplayed()));
    }

}