package com.ss_salt.android.taskee.packages.models;

import java.util.UUID;

/**
 * Created by Justin G on 04/02/2018.
 *
 *  SubTask will extend Task.
 */

public class SubTask {
    //========================================================================================
    // Properties
    //========================================================================================

    private UUID mId;
    private String mTitle;

    //========================================================================================
    // Constructors
    //========================================================================================

    public SubTask() {
        this(UUID.randomUUID());
    }

    public SubTask(UUID id) {
        mId = id;
    }

    //========================================================================================
    // Accessors
    //========================================================================================

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
