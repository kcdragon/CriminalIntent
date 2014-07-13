package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.hardware.Camera;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.util.Log;
import android.widget.*;

public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_IMAGE = "image";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

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
	setHasOptionsMenu(true);

	mCrime = getCurrentCrime();
    }

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, final Bundle savedInstanceState) {
	final View view = inflater.inflate(R.layout.fragment_crime, parent, false);

	if (VersionChecker.isEleven()) {
	    if (hasParent()) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	    }
	}

	mTitleField = (EditText) view.findViewById(R.id.crime_title);
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

	mDateButton = (Button) view.findViewById(R.id.crime_date);
	mDateButton.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View view) {
		final FragmentManager fm = getActivity()
		    .getSupportFragmentManager();
		final DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
		dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
		dialog.show(fm, DIALOG_DATE);
	    }
	});

	mTimeButton = (Button) view.findViewById(R.id.crime_time);
	mTimeButton.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View view) {
		final FragmentManager fm = getActivity()
		    .getSupportFragmentManager();
		final TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
		dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
		dialog.show(fm, DIALOG_TIME);
	    }
	});

	updateDate();

	mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
	mSolvedCheckBox.setChecked(mCrime.isSolved());
	mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    @Override
	    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
		mCrime.setSolved(isChecked);
	    }
	});

        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) view.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Photo photo = mCrime.getPhoto();
                if (photo == null) {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path, photo.getOrientation()).show(fm, DIALOG_IMAGE);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }

	return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    if (hasParent()) {
		NavUtils.navigateUpFromSameTask(getActivity());
	    }
	    return true;
	case R.id.menu_item_delete_current_crime:
	    getCrimeLab().deleteCrime(mCrime);
	    getActivity().finish();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onPause() {
	super.onPause();
	getCrimeLab().saveCrimes();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
	if (requestCode == REQUEST_DATE &&
	    resultCode == Activity.RESULT_OK) {

	    final Date date = (Date) intent
		.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
	    mCrime.setDate(date);
	    updateDate();
	}
	else if (requestCode == REQUEST_TIME &&
	    resultCode == Activity.RESULT_OK) {

	    final Date date = (Date) intent
		.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
	    mCrime.setDate(date);
	    updateDate();
	}
        else if (requestCode == REQUEST_PHOTO) {
            String filename = intent.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int orientation = display.getRotation();

                Photo photo = new Photo(filename, orientation);
                mCrime.setPhoto(photo);
                showPhoto();
            }
        }
    }

    private Crime getCurrentCrime() {
	final UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
	return getCrimeLab().getCrime(crimeId);
    }

    private void updateDate() {
	mDateButton.setText(mCrime.getFormattedDate());
	mTimeButton.setText(mCrime.getFormattedTime());
    }

    private boolean hasParent() {
	return NavUtils.getParentActivityName(getActivity()) != null;
    }

    private CrimeLab getCrimeLab() {
	return CrimeLab.get(getActivity());
    }

    private void showPhoto() {
        Photo photo = mCrime.getPhoto();
        BitmapDrawable drawable = null;
        if (photo != null) {
            String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
            drawable = PictureUtils.getScaledDrawable(getActivity(), path);

            if (photo.getOrientation() == 0) {
                drawable = PictureUtils.getRotatedDrawable(drawable);
            }
        }
        mPhotoView.setImageDrawable(drawable);
    }
}
