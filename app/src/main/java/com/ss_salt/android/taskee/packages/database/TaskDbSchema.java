package com.ss_salt.android.taskee.packages.database;

/**
 * Created by Justin G on 07/02/2018.
 */

public class TaskDbSchema {
    public static final class TaskTable {
        public static final String NAME = "tasks";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String SUBTASK_LIST = "subTaskList";
        }
    }
}
