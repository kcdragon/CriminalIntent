package com.bignerdranch.android.criminalintent;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;

import org.json.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(final Context context, final String filename) {
	mContext = context;
	mFilename = filename;
    }

    public ArrayList<Crime> loadCrimes() throws IOException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
	BufferedReader reader = null;
	try {
	    final InputStream in = mContext.openFileInput(mFilename);
	    reader = new BufferedReader(new InputStreamReader(in));
	    final StringBuilder jsonString = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		jsonString.append(line);
	    }

            Type crimeType = new TypeToken<ArrayList<Crime>>() {}.getType();
            crimes = new Gson().fromJson(jsonString.toString(), crimeType);
	}
	catch (FileNotFoundException e) {
	    // ignoring since there will not be a file when first starting the app
	}
	finally {
	    if (reader != null) {
		reader.close();
	    }
	}
	return crimes;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws IOException, FileNotFoundException {
        Writer writer = null;
	try {
	    OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
	    writer = new OutputStreamWriter(out);
	    writer.write(new Gson().toJson(crimes));
	}
	finally {
	    if (writer != null) {
		writer.close();
	    }
	}
    }
}
