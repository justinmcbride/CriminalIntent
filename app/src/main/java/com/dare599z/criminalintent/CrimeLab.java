package com.dare599z.criminalintent;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Justin on 11/19/2014.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSON mSerial;

    private static CrimeLab mCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context context) {
        mAppContext = context;

        mSerial = new CriminalIntentJSON(mAppContext, FILENAME);

        try {
            mCrimes = mSerial.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "error loading crimes", e);
        }
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime c) {
        mCrimes.remove(c);
    }

    public static CrimeLab get(Context c) {
        if (mCrimeLab == null) {
            mCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return mCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }


    public boolean saveCrimes() {
        try {
            mSerial.saveCrimes(mCrimes);
            Toast.makeText(mAppContext, "saved good", Toast.LENGTH_LONG);
            Log.d(TAG, "crimes saved");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "error saving crimes: ", e);
            Toast.makeText(mAppContext, "saved bad: " + e.toString(), Toast.LENGTH_LONG);
            return false;
        }
    }
}
