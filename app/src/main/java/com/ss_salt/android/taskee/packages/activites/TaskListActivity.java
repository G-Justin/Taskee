package com.ss_salt.android.taskee.packages.activites;

import android.support.v4.app.Fragment;

import com.ss_salt.android.taskee.packages.fragments.TaskListFragment;

public class TaskListActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return TaskListFragment.newInstance();
    }
}
