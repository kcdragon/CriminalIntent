package com.bignerdranch.android.criminalintent;

import java.io.*;
import java.util.ArrayList;

import android.content.Context;

import org.json.*;

public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(final Context context, final String filename) {
	mContext = context;
	mFilename = filename;
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
	final ArrayList<Crime> crimes = new ArrayList<Crime>();
	BufferedReader reader = null;
	try {
	    final InputStream in = mContext.openFileInput(mFilename);
	    reader = new BufferedReader(new InputStreamReader(in));
	    final StringBuilder jsonString = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		jsonString.append(line);
	    }

	    final JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

	    for (int i = 0; i < array.length(); i++) {
		crimes.add(new Crime(array.getJSONObject(i)));
	    }
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

    public void saveCrimes(final ArrayList<Crime> crimes) throws JSONException, IOException {
	final JSONArray array = new JSONArray();
	for (final Crime crime : crimes) {
	    array.put(crime.toJSON());
	}

	Writer writer = null;
	try {
	    final OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
	    writer = new OutputStreamWriter(out);
	    writer.write(array.toString());
	}
	finally {
	    if (writer != null) {
		writer.close();
	    }
	}
    }
}
