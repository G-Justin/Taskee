package com.ss_salt.android.taskee.packages.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ss_salt.android.taskee.packages.database.TaskDbSchema.TaskTable;


/**
 * Created by Justin G on 07/02/2018.
 */

public class TaskBaseHelper extends SQLiteOpenHelper {

    //========================================================================================
    // Properties
    //========================================================================================

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "taskBase.db";


    //========================================================================================
    // LifeCycle
    //========================================================================================

    public TaskBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    //========================================================================================
    // Properties
    //========================================================================================
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TaskTable.NAME + "(" + "_id integer primary key autoincrement, "
                + TaskTable.Cols.UUID + ", " + TaskTable.Cols.TITLE + ", " + TaskTable.Cols.SUBTASK_LIST + ")");

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
