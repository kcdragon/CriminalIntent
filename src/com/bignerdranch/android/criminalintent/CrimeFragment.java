package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.*;
import android.hardware.Camera;
import android.net.Uri;
import android.os.*;
import android.provider.*;
import android.support.v4.app.*;
import android.text.*;
import android.text.format.*;
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
    private static final int REQUEST_CONTACT = 3;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Button mSuspectButton;

    private ActionMode photoViewActionMode;

    public static CrimeFragment newInstance(UUID crimeId) {
	final Bundle args = new Bundle();
	args.putSerializable(EXTRA_CRIME_ID, crimeId);

	final CrimeFragment fragment = new CrimeFragment();
	fragment.setArguments(args);

	return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	setHasOptionsMenu(true);

	mCrime = getCurrentCrime();
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
	final View view = inflater.inflate(R.layout.fragment_crime, parent, false);

	if (VersionChecker.isEleven() && hasParent()) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}

        Crime.addCrimeListener(photoChangeListener);

	mTitleField = (EditText) view.findViewById(R.id.crime_title);
	mTitleField.setText(mCrime.getTitle());
	mTitleField.addTextChangedListener(new TextWatcher() {
	    public void onTextChanged(CharSequence c, int start, int before, int count) {
		mCrime.setTitle(c.toString());
	    }

	    public void beforeTextChanged(CharSequence c, int start, int count, int after) {}

	    public void afterTextChanged(Editable e) {}
	});

	mDateButton = (Button) view.findViewById(R.id.crime_date);
	mDateButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(final View view) {
		DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
		dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
		dialog.show(getSupportFragmentManager(), DIALOG_DATE);
	    }
	});

	mTimeButton = (Button) view.findViewById(R.id.crime_time);
	mTimeButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(final View view) {
		TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
		dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
		dialog.show(getSupportFragmentManager(), DIALOG_TIME);
	    }
	});

	updateDate();

	mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
	mSolvedCheckBox.setChecked(mCrime.isSolved());
	mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
		mCrime.setSolved(isChecked);
	    }
	});

        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) view.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Photo photo = mCrime.getPhoto();
                if (photo != null) {
                    String path = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
                    ImageFragment.newInstance(path, photo.getOrientation()).show(getSupportFragmentManager(), DIALOG_IMAGE);
                }
            }
        });

        if (VersionChecker.isEleven()) {
            mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                
                    if (photoViewActionMode != null) {
                        return false;
                    }
                    else {
                        photoViewActionMode = getActivity().startActionMode(new ActionMode.Callback() {
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater inflater = mode.getMenuInflater();
                                inflater.inflate(R.menu.crime_context, menu);
                                return true;
                            }

                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return false;
                            }

                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                switch (item.getItemId()) {
                                case R.id.crime_delete_photo:
                                    deletePhoto();
                                    mode.finish();
                                    return true;
                                default:
                                    return false;
                                }
                            }

                            public void onDestroyActionMode(ActionMode mode) {
                                photoViewActionMode = null;
                            }
                        });
                        view.setSelected(true);
                        return true;
                    }
                }
            });
        }
        else {
            registerForContextMenu(mPhotoView);
        }

        disablePhotoButtonIfNoCamera();

        Button reportButton = (Button) view.findViewById(R.id.crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        mSuspectButton = (Button) view.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                                           ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

	return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) intent
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;

            case REQUEST_TIME:
                Date time = (Date) intent
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setDate(time);
                updateDate();
                break;

            case REQUEST_PHOTO:
                String filename = intent.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                if (filename != null) {
                    Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    int orientation = display.getRotation();

                    Photo photo = new Photo(filename, orientation);
                    mCrime.setPhoto(photo);
                    showPhoto();
                }
                break;
            case REQUEST_CONTACT:
                Uri contactUri = intent.getData();
                String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
                };
                Cursor cursor = getActivity().getContentResolver().
                    query(contactUri, queryFields, null, null, null);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String suspect = cursor.getString(0);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                }
                cursor.close();
                break;
            }
        }
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	getActivity().getMenuInflater().inflate(R.menu.crime_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.crime_delete_photo:
            deletePhoto();
            return true;
        default:
            return false;
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
    public void onDestroyView() {
        super.onDestroyView();
        Crime.removeCrimeListener(photoChangeListener);
    }

    private FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }

    private boolean hasParent() {
	return NavUtils.getParentActivityName(getActivity()) != null;
    }

    
    /**
     * Crime
     *
     */

    private Crime getCurrentCrime() {
	final UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
	return getCrimeLab().getCrime(crimeId);
    }

    private void updateDate() {
	mDateButton.setText(mCrime.getFormattedDate());
	mTimeButton.setText(mCrime.getFormattedTime());
    }

    private CrimeLab getCrimeLab() {
	return CrimeLab.get(getActivity());
    }


    /**
     * Crime Photo
     *
     */

    private void disablePhotoButtonIfNoCamera() {
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }
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

    private void deletePhoto() {
        mCrime.setPhoto(null);
        mPhotoView.setImageDrawable(null);
    }

    private CrimeListener photoChangeListener = new CrimeListener() {
        @Override
        public void photoChange(Photo currentPhoto, Photo newPhoto) {
            if (currentPhoto != null) {
                String previousFilename = currentPhoto.getFilename();
                getActivity().deleteFile(previousFilename);
            }
        }
    };

    
    /**
     * Crime Report
     *
     */

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        }
        else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                                  mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
