package com.dare599z.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Justin on 11/23/2014.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_DATE = "com.dare599z.criminalintent.date";

    private Date mDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, false);

    }

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(TimePickerFragment.EXTRA_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        mDate.setHours(hourOfDay);
        mDate.setMinutes(minute);
        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(DatePickerFragment.EXTRA_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }
}
