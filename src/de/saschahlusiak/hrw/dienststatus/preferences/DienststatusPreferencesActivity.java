package de.saschahlusiak.hrw.dienststatus.preferences;

import de.saschahlusiak.hrw.dienststatus.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class DienststatusPreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		
	}

}