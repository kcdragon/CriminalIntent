package com.bignerdranch.android.criminalintent;

import lombok.*;

public class Photo {

    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ORIENTATION = "orientation";
    
    @Getter private String filename;
    @Getter private int orientation;
    
    public Photo(String filename, int orientation) {
        this.filename = filename;
        this.orientation = orientation;
    }
}
