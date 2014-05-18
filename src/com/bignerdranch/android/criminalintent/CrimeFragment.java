package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(final UUID crimeId) {
	final Bundle args = new Bundle();
	args.putSerializable(EXTRA_CRIME_ID, crimeId);

	final CrimeFragment fragment = new CrimeFragment();
	fragment.setArguments(args);

	return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	final UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
	mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, final Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_crime, parent, false);

	mTitleField = (EditText) v.findViewById(R.id.crime_title);
	mTitleField.setText(mCrime.getTitle());
	mTitleField.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void onTextChanged(final CharSequence c, final int start, final int before, final int count) {
		mCrime.setTitle(c.toString());
	    }

	    @Override
	    public void beforeTextChanged(final CharSequence c, final int start, final int count, final int after) {}

	    @Override
	    public void afterTextChanged(final Editable e) {}
	});

	mDateButton = (Button) v.findViewById(R.id.crime_date);
	mDateButton.setText(mCrime.getFormattedDate());
	mDateButton.setEnabled(false);

	mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
	mSolvedCheckBox.setChecked(mCrime.isSolved());
	mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    @Override
	    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
		mCrime.setSolved(isChecked);
	    }
	});

	return v;
    }
}
