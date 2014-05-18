package com.bignerdranch.android.criminalintent;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.text.format.DateFormat;

public class Crime {

    private static final List<Listener> listeners = new ArrayList<Listener>();

    public static void addListener(final Listener listener) {
	listeners.add(listener);
    }

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
	mId = UUID.randomUUID();
	mDate = new Date();
    }

    public UUID getId() {
	return mId;
    }

    public String getTitle() {
	return mTitle;
    }

    public void setTitle(final String title) {
	notifyListeners();
	mTitle = title;
    }

    public Date getDate() {
	return mDate;
    }

    public CharSequence getFormattedDate() {
	final String format = "cccc, MMMM d, yyyy";
	return DateFormat.format(format, getDate());
    }

    public void setDate(final Date date) {
	notifyListeners();
	mDate = date;
    }

    public boolean isSolved() {
	return mSolved;
    }

    public void setSolved(final Boolean solved) {
	notifyListeners();
	mSolved = solved;
    }

    @Override
    public String toString() {
	return getTitle();
    }

    private void notifyListeners() {
	for(Listener l: listeners) {
	    l.modelChange();
	}
    }
}
