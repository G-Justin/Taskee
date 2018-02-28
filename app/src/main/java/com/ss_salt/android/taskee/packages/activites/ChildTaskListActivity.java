package com.ss_salt.android.taskee.packages.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ss_salt.android.taskee.packages.fragments.ChildTaskListFragment;


import java.util.UUID;

/**
 * Created by Justin G on 28/02/2018.
 */

public class ChildTaskListActivity extends SingleFragmentActivity {
    private static final String EXTRA_TASK_ID =
            "com.ss_salt.android.taskee.sub";

    private static final String EXTRA_SUBTASK_INDEX =
            "com.ss_salt.android.taskee.subtaskindex";

    private UUID mExtraTaskId;
    private int mExtraSubTaskIndex;

    public static Intent newIntent(Context context, UUID taskId, int extraSubTaskIndex) {
        Intent intent = new Intent(context, ChildTaskListActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        intent.putExtra(EXTRA_SUBTASK_INDEX, extraSubTaskIndex);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mExtraTaskId = (UUID) getIntent().getSerializableExtra(EXTRA_TASK_ID);
        mExtraSubTaskIndex = getIntent().getIntExtra(EXTRA_SUBTASK_INDEX, 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return ChildTaskListFragment.newInstance(mExtraTaskId, mExtraSubTaskIndex);
    }
}
