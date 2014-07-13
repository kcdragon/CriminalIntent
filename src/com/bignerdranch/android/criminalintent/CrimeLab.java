package com.bignerdranch.android.criminalintent;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

import org.json.*;

public class CrimeLab {

    private static final String FILENAME = "crimes-gson.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(final Context appContext) {
	mAppContext = appContext;
	mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

	try {
            mCrimes = mSerializer.loadCrimes();
	}
	catch (IOException e) {
	    mCrimes = new ArrayList<Crime>();
	    Log.e("CrimeLab", "Error loading crimes: ", e);
	}
    }

    public static CrimeLab get(final Context c) {
	if (sCrimeLab == null) {
	    sCrimeLab = new CrimeLab(c.getApplicationContext());
	}
	return sCrimeLab;
    }

    public Crime createCrime() {
	final Crime crime = new Crime();
	addCrime(crime);
	return crime;
    }

    public void addCrime(final Crime crime) {
	mCrimes.add(crime);
    }

    public void deleteCrime(final Crime crime) {
	mCrimes.remove(crime);
	notifyListeners();
    }

    public int getCount() {
	return mCrimes.size();
    }

    public ArrayList<Crime> getCrimes() {
	return mCrimes;
    }

    public Crime getCrime(final UUID id) {
	for (Crime c: getCrimes()) {
	    if (c.getId().equals(id)) {
		return c;
	    }
	}
	return null;
    }

    public boolean saveCrimes() {
	try {
	    Log.d("CrimeLab", "attempting to save crimes to file");
            mSerializer.saveCrimes(mCrimes);
	    Log.d("CrimeLab", "crimes saved to file");
	    return true;
	}
	catch (IOException e) {
	    Log.e("CrimeLab", "Error saving crimes: ", e);
	    return false;
	}
    }

    public void addListener(final Listener listener) {
	Crime.addListener(listener);
    }

    private void notifyListeners() {
	for(Listener l: Crime.getListeners()) {
	    l.modelChange();
	}
    }
}
