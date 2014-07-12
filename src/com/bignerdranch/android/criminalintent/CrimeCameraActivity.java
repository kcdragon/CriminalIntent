package com.bignerdranch.android.criminalintent;

import android.content.*;
import android.hardware.Camera;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
