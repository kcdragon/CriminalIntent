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
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.view.View;

public class DatePickerFragment extends AbstractPickerFragment {

    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    public static DatePickerFragment newInstance(final Date date) {
	final Bundle args = new Bundle();
	args.putSerializable(EXTRA_DATE, date);

	final DatePickerFragment fragment = new DatePickerFragment();
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

	mPickerView = getDatePickerView();

	final DatePicker datePicker = (DatePicker) mPickerView.findViewById(R.id.dialog_date_datePicker);
	datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
	    @Override
	    public void onDateChanged(final DatePicker view, final int year, final int month, final int day) {
		mDate = new GregorianCalendar(year, month, day, hour, minute).getTime();

		getArguments().putSerializable(EXTRA_DATE, mDate);
	    }
	});
    }

    private View getDatePickerView() {
	return getActivity().getLayoutInflater()
	    .inflate(R.layout.dialog_date, null);
    }

    protected String getExtraKey() {
	return EXTRA_DATE;
    }

    protected int getTitle() {
	return R.string.date_picker_title;
    }
}
