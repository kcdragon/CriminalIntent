package com.bignerdranch.android.criminalintent;

import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

public abstract class ContextMenuFactory {

    public static void buildContextMenu(ListFragment fragment, ListView view) {
	makeContextMenuFactory(fragment, view).build();
    }

    private static ContextMenuFactory makeContextMenuFactory(ListFragment fragment, ListView view) {
	if (VersionChecker.isEleven())
	    return new NewContextMenuFactory(fragment, view);
	else
	    return new OldContextMenuFactory(fragment, view);
    }

    protected ListFragment fragment;
    protected ListView view;

    public ContextMenuFactory(ListFragment fragment, ListView view) {
	this.fragment = fragment;
	this.view = view;
    }

    public abstract void build();
}

// contextual action bar
class NewContextMenuFactory extends ContextMenuFactory {
    public NewContextMenuFactory(ListFragment fragment, ListView view) {
	super(fragment, view);
    }

    public void build() {
	view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	view.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
	    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.crime_list_item_context, menu);
		return true;
	    }

	    public boolean onPrepareActionMode(ActionMode mode, Menu item) {
		return false;
	    }

	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
		    ListAdapter adapter = fragment.getListAdapter();
		    CrimeLab crimeLab = CrimeLab.get(fragment.getActivity());
		    int size = crimeLab.getCount();
		    for (int i = size - 1; i >= 0; i--) {
			if (fragment.getListView().isItemChecked(i)) {
			    crimeLab.deleteCrime((Crime) adapter.getItem(i));
			}
		    }
		    mode.finish();
		    return true;
		default:
		    return false;
		}

	    }

	    public void onDestroyActionMode(ActionMode mode) {}
	});
    }
}

// floating context menu
class OldContextMenuFactory extends ContextMenuFactory {
    public OldContextMenuFactory(ListFragment fragment, ListView view) {
	super(fragment, view);
    }

    public void build() {
	fragment.registerForContextMenu(view);
    }
}
