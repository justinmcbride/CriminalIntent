package com.dare599z.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Date;

/**
 * Created by Justin on 11/23/2014.
 */
public class DateTimePickerFragment extends DialogFragment {

    public static final String EXTRA_TYPE = "com.dare599z.criminalintent.whattochange";
    public static final int EXTRA_DATE = 0, EXTRA_TIME = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.change_date_or_time)
                .setPositiveButton(R.string.change_date, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent();
                        i.putExtra(EXTRA_TYPE, EXTRA_DATE);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    }
                })
                .setNegativeButton(R.string.change_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent();
                        i.putExtra(EXTRA_TYPE, EXTRA_TIME);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                    }
                });
        return builder.create();
    }
}
