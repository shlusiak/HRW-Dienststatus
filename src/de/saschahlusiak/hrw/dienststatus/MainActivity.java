package de.saschahlusiak.hrw.dienststatus;

import de.saschahlusiak.hrw.dienststatus.dienstdetails.DetailFragment;
import de.saschahlusiak.hrw.dienststatus.dienste.DienststatusFragment;
import de.saschahlusiak.hrw.dienststatus.dienste.DienststatusFragment.OnNodeClicked;
import de.saschahlusiak.hrw.dienststatus.model.HRWNode;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity implements OnNodeClicked{
	static final String tag = MainActivity.class.getSimpleName();

	class DienststatusTabListener implements TabListener {
		Fragment f;
		boolean flat;
		
		DienststatusTabListener(boolean flat) {
			this.flat = flat;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (f == null) {
				f = new DienststatusFragment(flat ? null : "all", MainActivity.this);
				ft.add(android.R.id.content, f, flat ? "warning" : "all");
			} else {
				ft.attach(f);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (f != null)
				ft.detach(f);

			FragmentManager fm = getFragmentManager();
			if (fm.getBackStackEntryCount() > 0)
				fm.popBackStack(fm.getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setHomeButtonEnabled(false);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setDisplayShowTitleEnabled(true);
//		getActionBar().setSubtitle("huhu");
		
		getActionBar().addTab(getActionBar().newTab()
				.setText("Alle")
				.setTabListener(new DienststatusTabListener(false)));
		getActionBar().addTab(getActionBar().newTab()
				.setText("Warnung")
				.setTabListener(new DienststatusTabListener(true)));
//		getActionBar().addTab(getActionBar().newTab()
//				.setText("Statistik")
//				.setTabListener(new MyTabListener()));
//		getActionBar().addTab(getActionBar().newTab()
//				.setText("News")
//				.setTabListener(new MyTabListener()));

		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	@Override
	protected void onStart() {
		setProgressBarIndeterminateVisibility(false);
		super.onStart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NodeFragmentInterface f;
			f = (NodeFragmentInterface)getFragmentManager().findFragmentById(android.R.id.content);
			FragmentManager fragmentManager = getFragmentManager();
			HRWNode node = f.getNode();
			
/*			if (fragmentManager.findFragmentByTag(node.getParentId()) != null) {
				Log.v(tag, "popping back to existing " + node.getParentId());
				fragmentManager.popBackStack(node.getParentId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			} else {
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(android.R.id.content, new DienststatusFragment(node.getParent(), this), node.getParentId());
				Log.v(tag, "pushing new " + node.getParentId());
				fragmentTransaction.addToBackStack(node.getParentId());
				fragmentTransaction.commit();
			} */
			fragmentManager.popBackStack();

			break;
		case R.id.about:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		
		default: return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onNodeDetails(HRWNode node) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(
				R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit /*,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit */);
		fragmentTransaction.replace(android.R.id.content, new DetailFragment(node.id), node.id);
		fragmentTransaction.addToBackStack(node.getParentId());
		fragmentTransaction.commit();		
	}

	@Override
	public void onNodeClicked(HRWNode node) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(
				R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit /*,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit */);
		fragmentTransaction.replace(android.R.id.content, new DienststatusFragment(node, this), node.id);
		Log.v(tag, "pushing new " + node.getParentId());
		fragmentTransaction.addToBackStack(node.getParentId());
		fragmentTransaction.commit();
	}
}
