package com.ss_salt.android.taskee.packages.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.ss_salt.android.taskee.packages.database.TaskBaseHelper;
import com.ss_salt.android.taskee.packages.database.TaskCursorWrapper;
import com.ss_salt.android.taskee.packages.database.TaskDbSchema.TaskTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Justin G on 04/02/2018.
 */

public class TaskLab {

    //========================================================================================
    // Properties
    //========================================================================================

    private static TaskLab sTaskLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //========================================================================================
    // Properties
    //========================================================================================

    public static TaskLab get(Context context) {
        if (sTaskLab == null) {
            sTaskLab = new TaskLab(context);
        }

        return sTaskLab;
    }

    private TaskLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TaskBaseHelper(mContext).getWritableDatabase();
    }

    //========================================================================================
    // Accessors
    //========================================================================================

    public void addTask(Task task) {
        ContentValues values = getContentValues(task);
        mDatabase.insert(TaskTable.NAME, null, values);
    }

    public void updateTask(Task task) {
        String uuidString = task.getId().toString();
        ContentValues values = getContentValues(task);

        mDatabase.update(TaskTable.NAME, values, TaskTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public void replaceDatabaseTasksToUpdate(List<Task> replacementTasks) {
        mDatabase.delete(TaskTable.NAME, null, null);
        for (Task task : replacementTasks) {
            addTask(task);
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        TaskCursorWrapper cursor = queryTasks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tasks.add(cursor.getTask());
                cursor.moveToNext();
                }
            } finally{
                cursor.close();
            }

        return tasks;
    }

    public Task getTask(UUID uuid) {
        TaskCursorWrapper cursor = queryTasks(TaskTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getTask();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Task task) {
        /**
         * Convert the task's subtask list into a string object through gson first.
         * */
        Gson gson = new Gson();
        String subTasksString = gson.toJson(task.getSubTaskList());

        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.SUBTASK_LIST, subTasksString);

        return values;
    }

    private TaskCursorWrapper queryTasks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new TaskCursorWrapper(cursor);
    }







}
