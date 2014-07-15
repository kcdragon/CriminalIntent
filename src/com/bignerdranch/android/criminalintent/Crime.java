package com.bignerdranch.android.criminalintent;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.text.format.DateFormat;

import lombok.*;

public class Crime {

    private static final List<Listener> listeners = new ArrayList<Listener>();
    private static final List<CrimeListener> crimeListeners = new ArrayList<CrimeListener>();

    public static List<Listener> getListeners() {
	return listeners;
    }

    public static void addListener(final Listener listener) {
	listeners.add(listener);
    }

    public static void addCrimeListener(CrimeListener listener) {
        crimeListeners.add(listener);
    }

    public static void removeCrimeListener(CrimeListener listener) {
        crimeListeners.remove(listener);
    }

    @Getter private UUID id;
    @Getter private String title;
    @Getter private Date date;
    @Getter private boolean solved;
    @Getter private Photo photo;
    @Getter private String suspect;

    public Crime() {
	id = UUID.randomUUID();
	date = new Date();
    }

    public void setTitle(String title) {
	this.title = title;
	notifyListeners();
    }

    public void setDate(Date date) {
	this.date = date;
	notifyListeners();
    }

    public void setSolved(Boolean solved) {
	this.solved = solved;
	notifyListeners();
    }

    public void setPhoto(Photo photo) {
        for(CrimeListener listener : crimeListeners) {
            listener.photoChange(this.photo, photo);
        }

        this.photo = photo;
        notifyListeners();
    }

    public void setSuspect(String suspect) {
	this.suspect = suspect;
	notifyListeners();
    }

    public CharSequence getFormattedDate() {
	final String format = "cccc, MMMM d, yyyy";
	return DateFormat.format(format, getDate());
    }

    public CharSequence getFormattedTime() {
	final String format = "h:mm a";
	return DateFormat.format(format, getDate());
    }

    private void notifyListeners() {
	for(Listener l: listeners) {
	    l.modelChange();
	}
    }

    @Override
    public boolean equals(final Object other) {
	boolean result = false;
	if (other instanceof Crime &&
	    ((Crime) other).getId().equals(this.getId())) {
	    result = true;
	}
	return result;
    }

    @Override
    public String toString() {
	return getTitle();
    }
}
