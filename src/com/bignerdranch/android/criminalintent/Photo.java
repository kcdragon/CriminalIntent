package com.bignerdranch.android.criminalintent;

import org.json.*;

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

    public Photo(JSONObject json) throws JSONException {
        filename = json.getString(JSON_FILENAME);
        orientation = json.getInt(JSON_ORIENTATION);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, filename);
        json.put(JSON_ORIENTATION, orientation);
        return json;
    }
}
