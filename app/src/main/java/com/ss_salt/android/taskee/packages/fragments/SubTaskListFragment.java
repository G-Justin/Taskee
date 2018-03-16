package com.ss_salt.android.taskee.packages.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ss_salt.android.taskee.R;

import java.util.UUID;

import com.ss_salt.android.taskee.packages.activites.ChildTaskListActivity;
import com.ss_salt.android.taskee.packages.models.LocalSubTaskListClass;
import com.ss_salt.android.taskee.packages.models.Task;
import com.ss_salt.android.taskee.packages.models.TaskLab;

/**
 * Created by Justin G on 05/02/2018.
 *
 *  Fragment that holds a list of subtasks. Will need to be refactored.
 */

public class SubTaskListFragment extends TaskListFragment {

    //========================================================================================
    // Properties
    //========================================================================================

    private static final String ARG_TASK_ID = "task_id";
    private Task mTask;

    //========================================================================================
    // Constructors
    //========================================================================================

    public static SubTaskListFragment newInstance(UUID taskId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_ID, taskId);

        SubTaskListFragment fragment = new SubTaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //========================================================================================
    // LifeCycle
    //========================================================================================


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mTask = TaskLab.get(getActivity()).getTask(taskId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_task_list, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_subtask);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(mTask.getTitle());
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskLab.get(getActivity()).updateTask(mTask);
                getActivity().onBackPressed();
            }
        });

        mTaskRecyclerView = v.findViewById(R.id.task_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        attachItemTouchHelperToAdapter();

        mAddTaskFloatingActionButton = v.findViewById(R.id.button_add_task);
        mAddTaskFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForNewTask();
            }
        });

        mCreateTaskButton = v.findViewById(R.id.button_create_task);
        mCreateTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForNewTask();
            }
        });

        updateRecyclerView();
        return v;
    }

    //========================================================================================
    // Accessors
    //========================================================================================


    @Override
    void updateRecyclerView() {
        LocalSubTaskListClass.get().setLocalSubTaskList(mTask.getSubTaskList());
        mTaskList = LocalSubTaskListClass.get().getLocalSubTaskList();

        if (mTaskAdapter == null) {
           createNewTaskAdapterThenBindToRecyclerView();
        } else {
            updateAdapter();
        }

        checkIfAdapterIsEmptyToShowCreateButton();
    }

    @Override
    void updateDatabaseFromLocalTaskList() {
        mTask.setSubTaskList(mTaskList);
        TaskLab.get(getActivity()).updateTask(mTask);
    }

    @Override
    void startNewTaskActivity(Task task, int adapterPosition) {
        UUID taskId = mTask.getId();
        int subTaskIndex = adapterPosition;
        Intent intent = ChildTaskListActivity
                .newIntent(getActivity(), taskId, subTaskIndex);
        startActivity(intent);
    }
}

