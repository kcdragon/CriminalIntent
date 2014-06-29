package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.TimePicker;
import android.view.View;

public class TimePickerFragment extends AbstractPickerFragment {

    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";

    public static TimePickerFragment newInstance(final Date date) {
	final Bundle args = new Bundle();
	args.putSerializable(EXTRA_TIME, date);

	final TimePickerFragment fragment = new TimePickerFragment();
	fragment.setArguments(args);

	return fragment;
    }

    protected void setPicker() {
	final Calendar calendar = Calendar.getInstance();
	calendar.setTime(mDate);

	final int year = calendar.get(Calendar.YEAR);
	final int month = calendar.get(Calendar.MONTH);
	final int day = calendar.get(Calendar.DAY_OF_MONTH);

	final int hour = calendar.get(Calendar.HOUR);
	final int minute = calendar.get(Calendar.MINUTE);
	final int second = calendar.get(Calendar.SECOND);

	mPickerView = getDatePickerView();

	final TimePicker timePicker = (TimePicker) mPickerView.findViewById(R.id.dialog_time_timePicker);
	timePicker.setCurrentHour(hour);
	timePicker.setCurrentMinute(minute);
	timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
	    @Override
	    public void onTimeChanged(final TimePicker view, final int hour, final int minute) {
		mDate = new GregorianCalendar(year, month, day, hour, minute).getTime();

		getArguments().putSerializable(EXTRA_TIME, mDate);
	    }
	});
    }

    private View getDatePickerView() {
	return getActivity().getLayoutInflater()
	    .inflate(R.layout.dialog_time, null);
    }

    protected String getExtraKey() {
	return EXTRA_TIME;
    }

    protected int getTitle() {
	return R.string.time_picker_title;
    }
}
