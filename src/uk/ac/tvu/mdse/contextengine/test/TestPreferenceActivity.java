package uk.ac.tvu.mdse.contextengine.test;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import uk.ac.tvu.mdse.contextengine.R;

public class TestPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        
    }
    
}