package com.dare599z.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Justin on 11/19/2014.
 */
public class CrimeLab {
    private ArrayList<Crime> mCrimes;

    private static CrimeLab mCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context context) {
        mAppContext = context;
        mCrimes = new ArrayList<Crime>();

    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
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
}
