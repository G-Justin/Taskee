package com.ss_salt.android.taskee.packages.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.ss_salt.android.taskee.packages.models.LocalSubTaskListClass;
import com.ss_salt.android.taskee.packages.models.Task;
import com.ss_salt.android.taskee.packages.models.TaskLab;

/**
 * Created by Justin G on 05/02/2018.
 *
 *  Fragment that holds a list of subtasks. Will need to be refactored.
 */

public class ChildTaskListFragment extends Fragment {

    //========================================================================================
    // Properties
    //========================================================================================
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_SUBTASK_INDEX = "subtask_index";
    private static final String DIALOG_TITLE = "DialogTitle";
    private static final int REQUEST_TITLE = 0;
    private static final int REQUEST_EDIT = 1;

    private Task mTask;
    private Task mSubTask;
    private RecyclerView mChildTaskRecyclerView;
    private ChildTaskAdapter mChildTaskAdapter;
    private FloatingActionButton mAddChildTaskButton;
    private Button mCreateChildTaskButton;

    private Task mHelperChildTask;
    // Local mChildTaskList that will be used to update the database.
    private List<Task> mChildTaskList;

    //========================================================================================
    // Constructors
    //========================================================================================

    public static ChildTaskListFragment newInstance(UUID taskId, int subTaskIndex) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_ID, taskId);
        args.putInt(ARG_SUBTASK_INDEX, subTaskIndex);

        ChildTaskListFragment fragment = new ChildTaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //========================================================================================
    // LifeCycle
    //========================================================================================


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        int index = getArguments().getInt(ARG_SUBTASK_INDEX);

        mTask = TaskLab.get(getActivity()).getTask(taskId);
        mSubTask = LocalSubTaskListClass.get().getLocalSubTaskList().get(index);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_task_list, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_subtask);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(mSubTask.getTitle());
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskLab.get(getActivity()).updateTask(mTask);
                getActivity().onBackPressed();
            }
        });


        mChildTaskRecyclerView = v.findViewById(R.id.task_recycler_view);
        mChildTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        attachItemTouchHelperToAdapter();

        mCreateChildTaskButton = v.findViewById(R.id.button_create_task);
        mCreateChildTaskButton.setText(R.string.create_childtask_button);
        mCreateChildTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialogForNewTask();
            }
        });

        mAddChildTaskButton = v.findViewById(R.id.button_add_task);
        mAddChildTaskButton.setOnClickListener(new View.OnClickListener() {
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
        int subTaskIndex = getArguments().getInt(ARG_SUBTASK_INDEX);
        mSubTask = LocalSubTaskListClass.get().getLocalSubTaskList().get(subTaskIndex);

        mChildTaskList = mSubTask.getSubTaskList();

        if (mChildTaskAdapter == null) {
            mChildTaskAdapter = new ChildTaskAdapter(mChildTaskList);
            mChildTaskRecyclerView.setAdapter(mChildTaskAdapter);
        } else {
            mChildTaskAdapter.notifyDataSetChanged();
        }

        checkToShowCreateButton();
    }

    private void checkToShowCreateButton() {
        if (mChildTaskAdapter.getItemCount() == 0) {
            mCreateChildTaskButton.setVisibility(View.VISIBLE);
        } else {
            mCreateChildTaskButton.setVisibility(View.GONE);
        }
    }

    private void showEditDialogForNewTask() {
        FragmentManager fragmentManager = getFragmentManager();
        EditTitleDialogFragment dialogFragment = EditTitleDialogFragment.newInstance();
        dialogFragment.setTargetFragment(ChildTaskListFragment.this, REQUEST_TITLE);
        dialogFragment.show(fragmentManager, DIALOG_TITLE);
    }

    private void showEditDialogForEditTask(String taskTitle) {
        FragmentManager fragmentManager = getFragmentManager();
        EditTitleDialogFragment dialogFragment = EditTitleDialogFragment.newInstance(taskTitle);
        dialogFragment.setTargetFragment(ChildTaskListFragment.this, REQUEST_EDIT);
        dialogFragment.show(fragmentManager, DIALOG_TITLE);
    }


    private void addSubTaskToList(String taskTitle) {
        Task subTask = new Task();
        subTask.setTitle(taskTitle);

        mChildTaskList.add(subTask);
        updateDatabaseFromLocalList();
        updateUI();
    }

    private void updateDatabaseFromLocalList() {
        mSubTask.setSubTaskList(mChildTaskList);
        mTask.setSubTaskList(LocalSubTaskListClass.get().getLocalSubTaskList());
        TaskLab.get(getActivity()).updateTask(mTask);
    }

    private void editSubTaskTitle(String taskTitle) {
        int index = mChildTaskList.indexOf(mHelperChildTask);
        mChildTaskList.get(index).setTitle(taskTitle);

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
                        mChildTaskAdapter.onItemRemove(viewHolder, mChildTaskRecyclerView);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(mChildTaskRecyclerView);
    }

    private void removeFromLocalTaskList(Task subTask) {
        mChildTaskList.remove(subTask);
    }

    private void rearrangeSubTasks(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mChildTaskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mChildTaskList, i, i - 1);
            }
        }
        mChildTaskAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    //========================================================================================
    // Inner Classes
    //========================================================================================
    /**
     * Viewholder that holds each individual cardview of a task.
     */
    private class ChildTaskHolder extends RecyclerView.ViewHolder {
        private Task mChildTask;

        private TextView mChildTaskTitle;
        private ImageView mEditTitle;
        private ImageView mHasListImageView;

        public ChildTaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.task_list_item, parent, false));

            mHasListImageView = itemView.findViewById(R.id.has_list_image);

            mChildTaskTitle = itemView.findViewById(R.id.task_title);
            mEditTitle = itemView.findViewById(R.id.edit_title_image);
            mEditTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHelperChildTask = mChildTask;
                    showEditDialogForEditTask(mChildTask.getTitle());
                }
            });
        }

        public void bind(Task task) {
            mChildTask = task;
            mChildTaskTitle.setText(task.getTitle());
            if (mChildTask.hasSubTasks()) {
                mHasListImageView.setVisibility(View.VISIBLE);
            } else {
                mHasListImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     *
     * Adapter to hold the Viewholders for each task
     *
     * */
    private class ChildTaskAdapter extends RecyclerView.Adapter<ChildTaskHolder> {
        private List<Task> mChildTasks;

        public ChildTaskAdapter(List<Task> tasks) {
            mChildTasks = tasks;
        }

        @Override
        public ChildTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ChildTaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ChildTaskHolder holder, int position) {
            Task task = mChildTasks.get(position);
            holder.bind(task);
        }

        public void setChildTasks(List<Task> childTasks) {
            mChildTasks = childTasks;
        }

        @Override
        public int getItemCount() {
            return mChildTasks.size();
        }

        public void onItemRemove(final RecyclerView.ViewHolder viewHolder, RecyclerView recyclerView) {
            final int adapterPosition = viewHolder.getAdapterPosition();
            final Task taskToRemove = mChildTasks.get(adapterPosition);

            Snackbar snackbar = Snackbar
                    .make(recyclerView, getContext().getString(R.string.task_deleted), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            reinsertTask(adapterPosition, taskToRemove);
                        }
                    });
            snackbar.show();
            notifyItemRemoved(adapterPosition);
            removeFromLocalTaskList(taskToRemove);
            checkToShowCreateButton();
        }

        private void reinsertTask(int adapterPosition, Task taskToRemove) {
            mChildTaskList.add(adapterPosition, taskToRemove);
            mChildTaskAdapter.setChildTasks(mChildTaskList);
            mChildTaskAdapter.notifyItemInserted(adapterPosition);
            mChildTaskAdapter.notifyItemRangeChanged(adapterPosition, mChildTaskAdapter.getItemCount());
            mChildTaskRecyclerView.scrollToPosition(adapterPosition);
            checkToShowCreateButton();
        }
    }
}
