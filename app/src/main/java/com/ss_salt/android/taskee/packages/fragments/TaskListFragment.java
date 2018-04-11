package com.ss_salt.android.taskee.packages.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.ss_salt.android.taskee.R;
import com.ss_salt.android.taskee.packages.activites.SettingsActivity;
import com.ss_salt.android.taskee.packages.activites.SubTaskListActivity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.ss_salt.android.taskee.packages.models.Task;
import com.ss_salt.android.taskee.packages.models.TaskLab;

/**
 * Created by Justin G on 04/02/2018.
 *
 * Main task list fragment. The database which contains the tasks is updated like so:
 *
 *  - a function of a task is called (edit, delete, etc.)
 *  - a class which holds a local list is then updated
 *  - database is then also updated onPause and onDestroy
 */

public class TaskListFragment extends Fragment {
    //========================================================================================
    // Properties
    //========================================================================================

    private static final int REQUEST_TITLE = 0;
    private static final int REQUEST_EDIT = 1;
    private static final String DIALOG_TITLE = "DialogTitle";

    DrawerLayout mDrawerLayout;
    RecyclerView mTaskRecyclerView;
    TaskAdapter mTaskAdapter;
    FloatingActionButton mAddTaskFloatingActionButton;
    Button mCreateTaskButton;

    private Task mTaskWithTitleToEdit;
    List<Task> mTaskList;


    //========================================================================================
    // Constructors
    //========================================================================================

    public static TaskListFragment newInstance() {
        Bundle args = new Bundle();

        TaskListFragment fragment = new TaskListFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task_list, container, false);

        mDrawerLayout = v.findViewById(R.id.drawer_layout);

        NavigationView navigationView = v.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.settings:
                        openSettingsPage();
                }

                return true;
            }
        });

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.Taskee));
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String taskTitle = (String) data
                .getSerializableExtra(EditTitleDialogFragment.EXTRA_TITLE);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_TITLE) {
            addNewTaskWithTheTitleOf(taskTitle);
        } else if (requestCode == REQUEST_EDIT) {
            replaceTaskTitle(taskTitle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    @Override
    public void onPause() {
        updateDatabaseFromLocalTaskList();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        updateDatabaseFromLocalTaskList();
        super.onDestroy();
    }

    //========================================================================================
    // Accessors
    //========================================================================================

    void updateRecyclerView() {
        mTaskList = TaskLab.get(getActivity()).getTasks();

        if (mTaskAdapter == null) {
            createNewTaskAdapterThenBindToRecyclerView();
        } else {
            updateAdapter();
        }

        checkIfAdapterIsEmptyToShowCreateButton();
    }

    private void updateApplicationWithNewSetOfTasks() {
        updateDatabaseFromLocalTaskList();
        updateRecyclerView();
    }

    void updateAdapter() {
        mTaskAdapter.setTasks(mTaskList);
        mTaskAdapter.notifyDataSetChanged();
    }

    void updateDatabaseFromLocalTaskList() {
        TaskLab.get(getActivity()).replaceDatabaseTasksToUpdate(mTaskList);
    }

    void createNewTaskAdapterThenBindToRecyclerView() {
        mTaskAdapter = new TaskAdapter(mTaskList);
        mTaskRecyclerView.setAdapter(mTaskAdapter);
    }

    void checkIfAdapterIsEmptyToShowCreateButton() {
        if (mTaskAdapter.getItemCount() == 0) {
            mCreateTaskButton.setVisibility(View.VISIBLE);
        } else {
            mCreateTaskButton.setVisibility(View.GONE);
        }
    }

    void addNewTaskWithTheTitleOf(String taskTitle) {
        Task task = new Task();
        task.setTitle(taskTitle);

        mTaskList.add(task);
        updateApplicationWithNewSetOfTasks();
    }

    void replaceTaskTitle(String newTitle) {
        mTaskWithTitleToEdit.setTitle(newTitle);

        updateApplicationWithNewSetOfTasks();
    }

    void removeFromLocalTaskList(Task taskToDelete) {
        mTaskList.remove(taskToDelete);
    }

    void showDialogToEditTaskTitle(String taskTitleToEdit) {
        FragmentManager fragmentManager = getFragmentManager();
        EditTitleDialogFragment dialogFragment = EditTitleDialogFragment.newInstance(taskTitleToEdit);
        dialogFragment.setTargetFragment(TaskListFragment.this, REQUEST_EDIT);
        dialogFragment.show(fragmentManager, DIALOG_TITLE);
    }

    void showDialogForNewTask() {
        FragmentManager fragmentManager = getFragmentManager();
        EditTitleDialogFragment dialogFragment = EditTitleDialogFragment.newInstance();
        dialogFragment.setTargetFragment(TaskListFragment.this, REQUEST_TITLE);
        dialogFragment.show(fragmentManager, DIALOG_TITLE);
    }

    void attachItemTouchHelperToAdapter() {
        ItemTouchHelper.SimpleCallback touchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        final int fromPosition = viewHolder.getAdapterPosition();
                        final int toPosition = target.getAdapterPosition();
                        rearrangeTasks(fromPosition, toPosition);

                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        mTaskAdapter.onItemRemove(viewHolder, mTaskRecyclerView);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(mTaskRecyclerView);
    }

    void rearrangeTasks(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mTaskList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mTaskList, i, i - 1);
            }
        }
        mTaskAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    void openSettingsPage() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    void startNewTaskActivity(Task task, int adapterPosition) {
        UUID id = task.getId();
        Intent intent = SubTaskListActivity
                .newIntent(getActivity(), id);
        startActivity(intent);
    }

    //========================================================================================
    // Inner Classes
    //========================================================================================

    class TaskHolder extends RecyclerView.ViewHolder {
        private Task mTask;

        private TextView mTaskTitle;
        private ImageView mEditTitle;
        private CardView mTaskCardView;
        private ImageView mHasListImageView;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.task_list_item, parent, false));

            mTaskCardView = itemView.findViewById(R.id.clickable_card_task);
            mTaskCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startNewTaskActivity(mTask, getAdapterPosition());
                }
            });

            mHasListImageView = itemView.findViewById(R.id.has_list_image);

            mTaskTitle = itemView.findViewById(R.id.task_title);
            mEditTitle = itemView.findViewById(R.id.edit_title_image);
            mEditTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTaskWithTitleToEdit = mTask;
                    showDialogToEditTaskTitle(mTask.getTitle());
                }
            });
        }

        public void bind(Task task) {
            mTask = task;
            mTaskTitle.setText(task.getTitle());
            if (mTask.hasSubTasks()) {
                mHasListImageView.setVisibility(View.VISIBLE);
            } else {
                mHasListImageView.setVisibility(View.GONE);
            }
        }
    }

    class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> mTasks;

        public TaskAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        public void setTasks(List<Task> tasks) {
            mTasks = tasks;
        }

        public List<Task> getTasks() {
            return mTasks;
        }

        public void onItemRemove(final RecyclerView.ViewHolder viewHolder, RecyclerView recyclerView) {
            final int adapterPosition = viewHolder.getAdapterPosition();
            final Task taskToRemove = mTasks.get(adapterPosition);

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
            checkIfAdapterIsEmptyToShowCreateButton();
        }

        private void reinsertTask(int adapterPosition, Task taskToRemove) {
            mTaskList.add(adapterPosition, taskToRemove);
            mTaskAdapter.setTasks(mTaskList);
            mTaskAdapter.notifyItemInserted(adapterPosition);
            mTaskAdapter.notifyItemRangeChanged(adapterPosition, mTaskAdapter.getItemCount());
            mTaskRecyclerView.scrollToPosition(adapterPosition);
            checkIfAdapterIsEmptyToShowCreateButton();
        }
    }
}
