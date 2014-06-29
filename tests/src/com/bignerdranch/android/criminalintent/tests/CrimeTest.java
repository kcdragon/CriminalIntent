package com.bignerdranch.android.criminalintent.tests;

import java.util.Calendar;
import java.util.Date;

import android.test.AndroidTestCase;

import com.bignerdranch.android.criminalintent.Crime;

public class CrimeTest extends AndroidTestCase {

    private Crime crime;

    public void setUp() {
	crime = new Crime();
    }

    public void testTitle() {
	crime.setTitle("Foo");

	assertEquals("Foo", crime.getTitle());
    }

    public void testFormattedDate() {
	crime.setDate(getDate());

	assertEquals("Sunday, May 18, 2014",
		     crime.getFormattedDate());
    }

    public void testFormattedTime() {
	crime.setDate(getDate());

	assertEquals("1:01 PM",
		     crime.getFormattedTime());
    }

    private Date getDate() {
	final Calendar calendar = Calendar.getInstance();
	calendar.set(2014, 4, 18, 13, 1);
	return calendar.getTime();
    }
}
