package de.saschahlusiak.hrw.dienststatus.main;

import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.dienste.DienststatusActivity;
import de.saschahlusiak.hrw.dienststatus.news.NewsListActivity;
import de.saschahlusiak.hrw.dienststatus.statistic.StatisticsActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainTabActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// setContentView(R.layout.main_tabs);

		TabHost tabHost = (TabHost) getTabHost();

		TabSpec firstTabSpec = tabHost.newTabSpec("tid1");

		firstTabSpec.setIndicator(getString(R.string.tab_all), getResources()
				.getDrawable(android.R.drawable.ic_menu_sort_by_size));
		Intent intent = new Intent(this, DienststatusActivity.class);
		intent.putExtra("level", "all");
		firstTabSpec.setContent(intent);
		tabHost.addTab(firstTabSpec);

		firstTabSpec = tabHost.newTabSpec("tid2");
		firstTabSpec.setIndicator(getString(R.string.tab_warnings),
				getResources().getDrawable(android.R.drawable.ic_dialog_alert));
		intent = new Intent(this, DienststatusActivity.class);
		intent.putExtra("level", (String) null);
		firstTabSpec.setContent(intent);
		tabHost.addTab(firstTabSpec);

		firstTabSpec = tabHost.newTabSpec("tid3");
		firstTabSpec.setIndicator(getString(R.string.tab_statistics),
				getResources().getDrawable(android.R.drawable.ic_menu_gallery));
		intent = new Intent(this, StatisticsActivity.class);
		firstTabSpec.setContent(intent);
		tabHost.addTab(firstTabSpec);

		firstTabSpec = tabHost.newTabSpec("tid4");
		firstTabSpec.setIndicator(getString(R.string.news), getResources()
				.getDrawable(android.R.drawable.ic_menu_my_calendar));
		intent = new Intent(this, NewsListActivity.class);
		firstTabSpec.setContent(intent);
		tabHost.addTab(firstTabSpec);
	}
}
