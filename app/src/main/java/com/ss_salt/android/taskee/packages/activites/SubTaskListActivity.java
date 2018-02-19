package com.ss_salt.android.taskee.packages.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.UUID;

import com.ss_salt.android.taskee.packages.fragments.SubTaskListFragment;

/**
 * Created by Justin G on 05/02/2018.
 */

public class SubTaskListActivity extends SingleFragmentActivity {

    private static final String EXTRA_TASK_ID =
            "com.ss_salt.android.taskee";

    private UUID mExtraTaskId;

    public static Intent newIntent(Context context, UUID taskId) {
        Intent intent = new Intent(context, SubTaskListActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mExtraTaskId = (UUID) getIntent().getSerializableExtra(EXTRA_TASK_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return SubTaskListFragment.newInstance(mExtraTaskId);
    }
}
