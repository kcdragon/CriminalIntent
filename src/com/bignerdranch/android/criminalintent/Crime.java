package com.bignerdranch.android.criminalintent;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.text.format.DateFormat;

import org.json.*;

import lombok.*;

public class Crime {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";

    private static final List<Listener> listeners = new ArrayList<Listener>();

    public static List<Listener> getListeners() {
	return listeners;
    }

    public static void addListener(final Listener listener) {
	listeners.add(listener);
    }

    @Getter private UUID id;
    @Getter private String title;
    @Getter private Date date;
    @Getter private boolean solved;
    @Getter private Photo photo;

    public Crime() {
	id = UUID.randomUUID();
	date = new Date();
    }

    public Crime(final JSONObject json) throws JSONException {
	id = UUID.fromString(json.getString(JSON_ID));
	if (json.has(JSON_TITLE)) {
	    title = json.getString(JSON_TITLE);
	}
	solved = json.getBoolean(JSON_SOLVED);
	date = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO))  {
            photo = new Photo(json.getJSONObject(JSON_PHOTO));
        }
    }

    public void setTitle(final String title) {
	this.title = title;
	notifyListeners();
    }

    public void setDate(final Date date) {
	this.date = date;
	notifyListeners();
    }

    public void setSolved(final Boolean solved) {
	this.solved = solved;
	notifyListeners();
    }

    public void setPhoto(final Photo photo) {
        this.photo = photo;
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

    public JSONObject toJSON() throws JSONException {
	final JSONObject json = new JSONObject();
	json.put(JSON_ID, id.toString());
	json.put(JSON_TITLE, title);
	json.put(JSON_SOLVED, solved);
	json.put(JSON_DATE, date.getTime());
        if (photo != null) {
            json.put(JSON_PHOTO, photo.toJSON());
        }
	return json;
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
