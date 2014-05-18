package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {

    private ArrayList<Crime> mCrimes;

    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(final Context appContext) {
	mAppContext = appContext;
	mCrimes = new ArrayList<Crime>();
	for (int i = 0; i < 100; i++) {
	    final Crime c = new Crime();
	    c.setTitle("Crime #" + i);
	    c.setSolved(i % 2 == 0);
	    mCrimes.add(c);
	}
    }

    public static CrimeLab get(final Context c) {
	if (sCrimeLab == null) {
	    sCrimeLab = new CrimeLab(c.getApplicationContext());
	}
	return sCrimeLab;
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
}
