package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
	final UUID crimeId = (UUID) getIntent()
	    .getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);

	return CrimeFragment.newInstance(crimeId);
    }
}
