package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Activity activity)  {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);
	setRetainInstance(true);

	getActivity().setTitle(R.string.crimes_title);

	mCrimes = getCrimeLab().getCrimes();
	mSubtitleVisible = false;

	final CrimeAdapter adapter = new CrimeAdapter(mCrimes);
	setListAdapter(adapter);

	getCrimeLab().addListener(new Listener() {
	    public void modelChange() {
		adapter.notifyDataSetChanged();
	    }
	});
    }

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, final Bundle savedInstanceState) {
	final View view = inflater.inflate(R.layout.fragment_crime_list, parent, false);

	if (VersionChecker.isEleven() && mSubtitleVisible) {
	    showSubtitle();
	}

	final Button newCrimeButton = (Button) view.findViewById(R.id.new_crime);
	newCrimeButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(final View view) {
		startCrimePagerForNewCrime();
	    }
	});

	final ListView listView = (ListView) view.findViewById(android.R.id.list);
	ContextMenuFactory.buildContextMenu(this, listView);

	return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	inflater.inflate(R.menu.fragment_crime_list, menu);

	final MenuItem showSubtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
	if (mSubtitleVisible && showSubtitleItem != null) {
	    showSubtitleItem.setTitle(R.string.hide_subtitle);
	}
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_item_new_crime:
	    startCrimePagerForNewCrime();
	    return true;

	case R.id.menu_item_show_subtitle:
	    final ActionBar actionBar = getActivity().getActionBar();

	    boolean hasSubtitle = (actionBar.getSubtitle() != null);
	    if (hasSubtitle) {
		hideSubtitle();
		item.setTitle(R.string.show_subtitle);
	    }
	    else {
		showSubtitle();
		item.setTitle(R.string.hide_subtitle);
	    }

	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
	Crime crime = ((CrimeAdapter) getListAdapter()).getItem(position);
        mCallbacks.onCrimeSelected(crime);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
	getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
	final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	final int position = info.position;
	final CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
	final Crime crime = adapter.getItem(position);

	switch (item.getItemId()) {
	case R.id.menu_item_delete_crime:
	    getCrimeLab().deleteCrime(crime);
	    return true;
	}
	return super.onContextItemSelected(item);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

    	public CrimeAdapter(final ArrayList<Crime> crimes) {
    	    super(getActivity(), 0, crimes);
    	}

    	@Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
    	    if (convertView == null) {
    		convertView = getActivity().getLayoutInflater()
    		    .inflate(R.layout.list_item_crime, null);
    	    }

    	    Crime c = getItem(position);

    	    TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
    	    titleTextView.setText(c.getTitle());

    	    TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
    	    dateTextView.setText(c.getFormattedDate() + " " + c.getFormattedTime());

    	    CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
    	    solvedCheckBox.setChecked(c.isSolved());

    	    return convertView;
    	}
    }

    public CrimeLab getCrimeLab() {
	return CrimeLab.get(getActivity());
    }

    private void startCrimePagerForNewCrime() {
	Crime crime = getCrimeLab().createCrime();
        mCallbacks.onCrimeSelected(crime);
    }

    private void showSubtitle() {
	getActivity().getActionBar().setSubtitle(R.string.subtitle);
	mSubtitleVisible = true;
    }

    private void hideSubtitle() {
	getActivity().getActionBar().setSubtitle(null);
	mSubtitleVisible = false;
    }

    public void updateUI() {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }
}
