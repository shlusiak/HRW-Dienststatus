package de.saschahlusiak.hrw.dienststatus;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HRWPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
