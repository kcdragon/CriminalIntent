package com.bignerdranch.android.criminalintent;

import android.os.Build;

public class VersionChecker {

    public static boolean isEleven() {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
