package de.saschahlusiak.hrw.dienststatus;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.InputStream;
import java.net.URL;

public class StatisticsActivity extends ListActivity implements OnItemClickListener {
	private StatisticsAdapter adapter;
	Handler handler = new Handler();
	int category;
	
	private class UpdateThread extends Thread {
		private String[] urls;
		
		UpdateThread(String[] urls) {
			this.urls = urls;
		}
		
		@Override
		public void run() {
			InputStream is;
			Drawable d = null;
		
			for (int i=0; i < urls.length; i++) {
				try {
					is = (InputStream) new URL("http://static.hs-weingarten.de/portvis/" + urls[i] + ".png").getContent();
					d = Drawable.createFromStream(is, "src");
					adapter.add(d, i);
					d = null;
				} catch (Exception e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(StatisticsActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG);
						}
					});
					e.printStackTrace();
					break;
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			}
		}
	}
	String titles[] = {
			null, "Internet", "IPv6", "E-Mail", "Wireless-LAN", "LSF", "Moodle", "VPN"
			};
	
	private void refresh() {
		String urls[][] = { 
		/* 0 */	{ "internet", "internet-ipv6", "email", "wlan-hswgt2", "lsf", "moodle-week", "vpn" },
		/* 1 */ { "internet", "internet-month", "internet-year" },
		/* 2 */ { "internet-ipv6", "internet-ipv6-month", "internet-ipv6-year", "internet-ipv6-percent", "internet-ipv6-percent-year" },
		/* 3 */ { "email", "email-year" },
		/* 4 */ { "wlan-hrw", "wlan-hrw-week", "wlan-hrw-month", "wlan-hrw-year", 
				  "wlan-hswgt2", "wlan-hswgt2-week", "wlan-hswgt2-month", "wlan-hswgt2-year",
				  "wlan-eduroam", "wlan-eduroam-week", "wlan-eduroam-month", "wlan-eduroam-year" },
		/* 5 */ { "lsf" },
		/* 6 */ { "moodle-week", "moodle-month" },
		/* 7 */ { "vpn" }
			};
		
		adapter.invalidate(urls[category].length);
		
		adapter.notifyDataSetChanged();
		
		new UpdateThread(urls[category]).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		adapter = new StatisticsAdapter(this);
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(this);
		
		category = 0;
		if (getIntent() != null) {
			if (getIntent().getExtras() != null) {
				category = (int) getIntent().getExtras().getLong("category");
				setTitle(getTitle() + " - " + titles[category]);
			}
		}
		
		refresh();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu, menu);
		
//		menu.findItem(R.id.preferences).setEnabled(false);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh) {
			refresh();
			return true;
		}
/*		if (item.getItemId() == R.id.preferences) {
			Intent intent = new Intent(this, HRWPreferences.class);
			startActivity(intent);
		} */
		if (item.getItemId() == R.id.about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		if (item.getItemId() == R.id.gotowebsite) {
			Intent intent = new Intent(
					"android.intent.action.VIEW",
					Uri.parse("http://www.hs-weingarten.de/web/rechenzentrum/zahlen-und-fakten"));
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (category != 0)
			return;
		
		Intent intent = new Intent(this, StatisticsActivity.class);
		intent.putExtra("category", arg3 + 1);
		startActivity(intent);
	}

}
