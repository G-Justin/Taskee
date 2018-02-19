package com.ss_salt.android.taskee.packages.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaCasException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.ss_salt.android.taskee.R;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.ss_salt.android.taskee.packages.models.SubTask;
import com.ss_salt.android.taskee.packages.models.Task;
import com.ss_salt.android.taskee.packages.models.TaskLab;

/**
 * Created by Justin G on 05/02/2018.
 *
 *  Fragment that holds a list of subtasks. Will need to be refactored.
 */

public class SubTaskListFragment extends Fragment {

    //========================================================================================
    // Properties
    //========================================================================================
    private static final String ARG_TASK_ID = "task_id";
    private static final String DIALOG_TITLE = "DialogTitle";
    private static final int REQUEST_TITLE = 0;
    private static final int REQUEST_EDIT = 1;

    private Task mTask;
    private RecyclerView mSubTaskRecyclerView;
    private SubTaskAdapter mSubTaskAdapter;
    private FloatingActionButton mAddSubTaskButton;
    private Button mCreateSubTaskButton;

    private SubTask mHelperSubTask;
    // Local mSubTaskList that will be used to update the database.
    private List<SubTask> mSubTaskList;

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


        mSubTaskRecyclerView = v.findViewById(R.id.task_recycler_view);
        mSubTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        attachItemTouchHelperToAdapter();

        mCreateSubTaskButton = v.findViewById(R.id.button_create_task);
        mCreateSubTaskButton.setText(R.string.create_subtask_button);
        mCreateSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialogForNewTask();
            }
        });

        mAddSubTaskButton = v.findViewById(R.id.button_add_task);
        mAddSubTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialogForNewTask();
            }
        });

        updateUI();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_TITLE) {
            String taskTitle = (String) data
                    .getSerializableExtra(EditTitleDialogFragment.EXTRA_TITLE);
            addSubTaskToList(taskTitle);
        } else if (requestCode == REQUEST_EDIT) {
            String taskTitle = (String) data
                    .getSerializableExtra(EditTitleDialogFragment.EXTRA_TITLE);
            editSubTaskTitle(taskTitle);
        }
    }

    @Override
    public void onPause() {
        updateDatabaseFromLocalList();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onDestroy() {
        updateDatabaseFromLocalList();
        super.onDestroy();
    }

    //========================================================================================
    // Accessors
    //========================================================================================

    private void updateUI() {
        List<SubTask> subTasks = mTask.getSubTaskList();
        mSubTaskList = subTasks;

        if (mSubTaskAdapter == null) {
            mSubTaskAdapter = new SubTaskAdapter(subTasks);
            mSubTaskRecyclerView.setAdapter(mSubTaskAdapter);
        } else {
            mSubTaskAdapter.notifyDataSetChanged();
        }

        checkToShowCreateButton();
    }

    private void checkToShowCreateButton() {
        if (mSubTaskAdapter.getItemCount() == 0) {
            mCreateSubTaskButton.setVisibility(View.VISIBLE);
        } else {
            mCreateSubTaskButton.setVisibility(View.GONE);
        }
    }

    private void showEditDialogForNewTask() {
        FragmentManager fragmentManager = getFragmentManager();
        EditTitleDialogFragment dialogFragment = EditTitleDialogFragment.newInstance();
        dialogFragment.setTargetFragment(SubTaskListFragment.this, REQUEST_TITLE);
        dialogFragment.show(fragmentManager, DIALOG_TITLE);
    }

    private void showEditDialogForEditTask(String taskTitle) {
        FragmentManager fragmentManager = getFragmentManager();
        EditTitleDialogFragment dialogFragment = EditTitleDialogFragment.newInstance(taskTitle);
        dialogFragment.setTargetFragment(SubTaskListFragment.this, REQUEST_EDIT);
        dialogFragment.show(fragmentManager, DIALOG_TITLE);
    }


    private void addSubTaskToList(String taskTitle) {
        SubTask subTask = new SubTask();
        subTask.setTitle(taskTitle);

        mSubTaskList.add(subTask);
        updateDatabaseFromLocalList();
        updateUI();
    }

    private void updateDatabaseFromLocalList() {
        mTask.setSubTaskList(mSubTaskList);
        TaskLab.get(getActivity()).updateTask(mTask);
    }

    private void editSubTaskTitle(String taskTitle) {
        int index = mSubTaskList.indexOf(mHelperSubTask);
        mSubTaskList.get(index).setTitle(taskTitle);

        updateDatabaseFromLocalList();
        updateUI();
    }

    private void attachItemTouchHelperToAdapter() {
        ItemTouchHelper.SimpleCallback touchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        final int fromPosition = viewHolder.getAdapterPosition();
                        final int toPosition = target.getAdapterPosition();

                        rearrangeSubTasks(fromPosition, toPosition);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        SubTask subTaskToDelete = mTask.getSubTaskList().get(position);
                        updateLocalSubTaskList(subTaskToDelete);
                        mSubTaskAdapter.notifyItemRemoved(position);
                        checkToShowCreateButton();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(mSubTaskRecyclerView);
    }

    private void updateLocalSubTaskList(SubTask subTask) {
        mSubTaskList.remove(subTask);
    }

    private void rearrangeSubTasks(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mSubTaskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mSubTaskList, i, i - 1);
            }
        }
        mSubTaskAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    //========================================================================================
    // Inner Classes
    //========================================================================================
    /**
     * Viewholder that holds each individual cardview of a task.
     */
    private class SubTaskHolder extends RecyclerView.ViewHolder {
        private SubTask mSubTask;

        private TextView mSubTaskTitle;
        private ImageView mEditTitle;

        public SubTaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.task_list_item, parent, false));

            mSubTaskTitle = itemView.findViewById(R.id.task_title);
            mEditTitle = itemView.findViewById(R.id.edit_title_image);
            mEditTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHelperSubTask = mSubTask;
                    showEditDialogForEditTask(mSubTask.getTitle());
                }
            });
        }

        public void bind(SubTask task) {
            mSubTask = task;
            mSubTaskTitle.setText(task.getTitle());
        }
    }

    /**
     *
     * Adapter to hold the Viewholders for each task
     *
     * */
    private class SubTaskAdapter extends RecyclerView.Adapter<SubTaskHolder> {
        private List<SubTask> mSubTasks;

        public SubTaskAdapter(List<SubTask> tasks) {
            mSubTasks = tasks;
        }

        @Override
        public SubTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SubTaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(SubTaskHolder holder, int position) {
            SubTask task = mSubTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mSubTasks.size();
        }
    }
}
