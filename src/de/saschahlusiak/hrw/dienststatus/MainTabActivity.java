package de.saschahlusiak.hrw.dienststatus;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainTabActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tabs);
		
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

		TabSpec firstTabSpec = tabHost.newTabSpec("tid1");

		firstTabSpec.setIndicator("First Tab Name");
		firstTabSpec.setContent(new Intent(this,HRWDienststatusActivity.class));
		tabHost.addTab(firstTabSpec);

		firstTabSpec = tabHost.newTabSpec("tid2");

		firstTabSpec.setIndicator("Second Tab Name");
		firstTabSpec.setContent(new Intent(this,HRWDienststatusActivity.class));
		tabHost.addTab(firstTabSpec);

	}
}
