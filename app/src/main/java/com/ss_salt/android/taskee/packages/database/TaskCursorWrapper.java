package com.ss_salt.android.taskee.packages.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ss_salt.android.taskee.packages.models.SubTask;
import com.ss_salt.android.taskee.packages.models.Task;

import com.ss_salt.android.taskee.packages.database.TaskDbSchema.TaskTable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Justin G on 07/02/2018.
 */

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Task getTask() {
        String uuid = getString(getColumnIndex(TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        String subTaskList = getString(getColumnIndex(TaskTable.Cols.SUBTASK_LIST));

        Task task = new Task(UUID.fromString(uuid));
        task.setTitle(title);
        // Convert to List object first with GSON.
        task.setSubTaskList(getSubTaskList(subTaskList));


        return task;
    }


    /***
     * Convert subTaskList String into a List object with GSON.
     * */
    private List<SubTask> getSubTaskList(String subTaskList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<SubTask>>(){}.getType();
        ArrayList<SubTask> fromGsonList = gson.fromJson(subTaskList, type);

        return fromGsonList;
    }
}
