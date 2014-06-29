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
import android.view.View;

public abstract class AbstractPickerFragment extends DialogFragment {

    protected Date mDate;
    protected View mPickerView;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
	setDate();
	setPicker();

	return new AlertDialog.Builder(getActivity())
	    .setView(getPickerView())
	    .setTitle(getTitle())
	    .setPositiveButton(android.R.string.ok,
			       new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(final DialogInterface dialog, final int which) {
				       sendResult(Activity.RESULT_OK);
				   }
			       })
	    .create();
    }

    protected void setDate() {
	mDate = (Date) getArguments().getSerializable(getExtraKey());
    }

    protected void sendResult(final int resultCode) {
	if (getTargetFragment() == null) {
	    return;
	}

	final Intent intent = new Intent();
	intent.putExtra(getExtraKey(), mDate);

	getTargetFragment()
	    .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private View getPickerView() {
	return mPickerView;
    }

    protected abstract void setPicker();
    protected abstract String getExtraKey();
    protected abstract int getTitle();
}
