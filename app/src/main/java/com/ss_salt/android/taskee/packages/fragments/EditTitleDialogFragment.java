package com.ss_salt.android.taskee.packages.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.ss_salt.android.taskee.R;

/**
 * Created by Justin G on 05/02/2018.
 *
 * Fragment which holds the dialog that is responsible for editing the title of the task
 * through an edit text, then sends the data back to the activity that hosts the list.
 *
 */

public class EditTitleDialogFragment extends DialogFragment {
    //========================================================================================
    // Properties
    //========================================================================================

    public static final String EXTRA_TITLE =
            "com.ss_salt.android.taskee.task_title";

    private static final String ARG_TITLE = "title";

    private AlertDialog mEditTitleDialog;
    private EditText mEditTitle;
    private String mTaskTitle = "";

    //========================================================================================
    // Constructors
    //========================================================================================

    public static EditTitleDialogFragment newInstance() {
        Bundle args = new Bundle();

        EditTitleDialogFragment fragment = new EditTitleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditTitleDialogFragment newInstance(String taskTitle) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, taskTitle);

        EditTitleDialogFragment fragment = new EditTitleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //========================================================================================
    // LifeCycle
    //========================================================================================


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String taskArgument = getArguments().getString(ARG_TITLE);
        if (taskArgument != null) {
            mTaskTitle = taskArgument;
        }

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_edit_title, null);

        /**
         * EditText widget that will be used to edit the title of the clicked task.
         * */
        mEditTitle = v.findViewById(R.id.edit_title);
        mEditTitle.setText(mTaskTitle);
        mEditTitle.setSelection(mTaskTitle.length());
        mEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTaskTitle = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendResult(Activity.RESULT_OK, mTaskTitle);
                            }
                        })
                .create();
    }

    //========================================================================================
    // Accessors
    //========================================================================================

    private void sendResult(int resultCode, String taskTitle) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, taskTitle);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
