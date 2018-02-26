package com.ss_salt.android.taskee.packages.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Justin G on 04/02/2018.
 *
 * Needs refactoring into a class that extends an abstract class.
 *
 *  This class will be renamed into MainTask.
 *
 *  MainTask will extend Task.
 */

public class Task {

    //========================================================================================
    // Properties
    //========================================================================================

    private UUID mId;
    private String mTitle;
    private List<Task> mSubTaskList = new ArrayList<>();

    //========================================================================================
    // Constructors
    //========================================================================================

    public Task() {
        this(UUID.randomUUID());
    }

    public Task(UUID id) {
        mId = id;
    }

    //========================================================================================
    // Accessors
    //========================================================================================


    public List<Task> getSubTaskList() {
        return mSubTaskList;
    }

    public void setSubTaskList(List<Task> subTaskList) {
        mSubTaskList = subTaskList;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public boolean hasSubTasks() {
        return !mSubTaskList.isEmpty();
    }

}
