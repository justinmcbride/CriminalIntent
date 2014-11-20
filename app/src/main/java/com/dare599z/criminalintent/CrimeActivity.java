package com.dare599z.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;


public class CrimeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
