package com.bignerdranch.android.criminalintent;

import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.util.Log;
import android.widget.*;

public class ImageFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_PATH = "com.bignerdranch.android.criminalintent.image_path";
    public static final String EXTRA_ORIENTATION = "com.bignerdranch.android.criminalintent.orientation";

    public static ImageFragment newInstance(String imagePath, int orientation) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putInt(EXTRA_ORIENTATION, orientation);
        
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());

        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        int orientation = (int) getArguments().getInt(EXTRA_ORIENTATION);

        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        if (orientation == 0) {
            image = PictureUtils.getRotatedDrawable(image);
        }
        
        imageView.setImageDrawable(image);
        
        return imageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(imageView);
    }
}
