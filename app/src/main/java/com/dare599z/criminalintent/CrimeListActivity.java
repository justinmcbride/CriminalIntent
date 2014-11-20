package com.dare599z.criminalintent;

import android.app.Fragment;

/**
 * Created by Justin on 11/19/2014.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
