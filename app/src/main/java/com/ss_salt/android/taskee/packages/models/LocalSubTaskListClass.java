package com.ss_salt.android.taskee.packages.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin G on 28/02/2018.
 */

public class LocalSubTaskListClass {

    //========================================================================================
    // Properties
    //========================================================================================

    private static LocalSubTaskListClass sLocalSubTaskListClass;
    List<Task> mLocalSubTaskList = new ArrayList<>();

    //========================================================================================
    // Constructors
    //========================================================================================

    public static LocalSubTaskListClass get() {
        if (sLocalSubTaskListClass == null) {
            sLocalSubTaskListClass = new LocalSubTaskListClass();
        }
        return sLocalSubTaskListClass;
    }

    private LocalSubTaskListClass() {
    }

    //========================================================================================
    // Accessors
    //========================================================================================


    public List<Task> getLocalSubTaskList() {
        return mLocalSubTaskList;
    }

    public void setLocalSubTaskList(List<Task> localSubTaskList) {
        mLocalSubTaskList = localSubTaskList;
    }
}
