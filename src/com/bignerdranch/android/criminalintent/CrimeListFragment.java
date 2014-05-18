package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	getActivity().setTitle(R.string.crimes_title);

	final CrimeLab crimeLab = CrimeLab.get(getActivity());

	mCrimes = crimeLab.getCrimes();

	final CrimeAdapter adapter = new CrimeAdapter(mCrimes);
	setListAdapter(adapter);

	crimeLab.addListener(new Listener() {
	    @Override
	    public void modelChange() {
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
	    }
	});
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
	final Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);

	final Intent i = new Intent(getActivity(), CrimeActivity.class);
	i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
	startActivity(i);
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

    	    final Crime c = getItem(position);

    	    final TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
    	    titleTextView.setText(c.getTitle());

    	    final TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
    	    dateTextView.setText(c.getFormattedDate());

    	    final CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
    	    solvedCheckBox.setChecked(c.isSolved());

    	    return convertView;
    	}
    }
}
