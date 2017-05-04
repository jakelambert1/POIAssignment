package com.example.a0lambj41.poiassignment;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by 0lambj41 on 04/05/2017.
 */
public class PrefsActivity extends PreferenceActivity {

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
