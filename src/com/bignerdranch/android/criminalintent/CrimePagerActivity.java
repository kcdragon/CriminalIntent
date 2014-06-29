package com.bignerdranch.android.criminalintent;

import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrimePagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mViewPager = new ViewPager(this);
    	mViewPager.setId(R.id.viewPager);
    	setContentView(mViewPager);

    	mCrimes = CrimeLab.get(this).getCrimes();

	setAdapterToCrimes();
	setCurrentCrime();
	setTitleToCrimeTitle();
    }

    private void setAdapterToCrimes() {
	mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
	    @Override
	    public int getCount() {
		return mCrimes.size();
	    }

	    @Override
	    public Fragment getItem(final int position) {
		final Crime crime = mCrimes.get(position);
		return CrimeFragment.newInstance(crime.getId());
	    }
	});
    }

    private void setCurrentCrime() {
	for (int i = 0; i < mCrimes.size(); i++) {
	    final Crime crime = mCrimes.get(i);

	    if (crime.getId().equals(getCurrentCrimeId())) {
		mViewPager.setCurrentItem(i);
		setTitleToCrimeTitle(crime);

		break;
	    }
	}
    }

    private UUID getCurrentCrimeId() {
	return (UUID) getIntent()
	    .getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
    }

    private void setTitleToCrimeTitle() {
	mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	    @Override public void onPageScrollStateChanged(int state) {}
	    @Override public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {}

	    @Override
	    public void onPageSelected(final int position) {
		final Crime crime = mCrimes.get(position);
		setTitleToCrimeTitle(crime);
	    }
	});
    }

    private void setTitleToCrimeTitle(final Crime crime) {
	if (crime.getTitle() != null) {
	    setTitle(crime.getTitle());
	}
    }
}
