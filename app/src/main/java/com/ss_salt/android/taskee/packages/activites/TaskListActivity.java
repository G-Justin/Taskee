package com.ss_salt.android.taskee.packages.activites;

import android.support.v4.app.Fragment;

import com.ss_salt.android.taskee.packages.fragments.TaskListFragment;

public class TaskListActivity extends SingleFragmentActivity {
    TaskListFragment mFragment;

    @Override
    protected Fragment createFragment() {
        mFragment = TaskListFragment.newInstance();
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        if (mFragment == null) {
            super.onBackPressed();
        }

        if (!mFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
