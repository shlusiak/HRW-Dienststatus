package de.saschahlusiak.hrw.dienststatus.main;

import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.dienstdetails.DetailFragment;
import de.saschahlusiak.hrw.dienststatus.dienste.DienststatusFragment;
import de.saschahlusiak.hrw.dienststatus.dienste.DienststatusFragment.OnNodeClicked;
import de.saschahlusiak.hrw.dienststatus.model.HRWNode;
import de.saschahlusiak.hrw.dienststatus.model.NewsItem;
import de.saschahlusiak.hrw.dienststatus.news.NewsItemFragment;
import de.saschahlusiak.hrw.dienststatus.news.NewsListFragment;
import de.saschahlusiak.hrw.dienststatus.news.NewsListFragment.OnNewsClicked;
import de.saschahlusiak.hrw.dienststatus.preferences.DienststatusPreferencesActivity;
import de.saschahlusiak.hrw.dienststatus.statistic.StatisticsFragment;
import de.saschahlusiak.hrw.dienststatus.statistic.StatisticsFragment.OnStatisticClicked;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements OnNodeClicked, OnStatisticClicked, OnNewsClicked, OnPageChangeListener {
	static final String tag = MainActivity.class.getSimpleName();
	boolean isCreated = false;
	
	ViewPager pager;
	
	abstract class MyTabListener implements TabListener {
//		Fragment f;
		String tag;

		MyTabListener(String tag) {
			this.tag = tag;
/*
			f = getSupportFragmentManager().findFragmentById(android.R.id.content);
            if (f != null && !f.isDetached() && f.getTag().equals(tag)) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(f);
                ft.commit();
            } else
            	f = null; */
		}

		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		}

		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
	/*		if (!isCreated && f == null)
				return;
			
			if (f == null) {
				f = createFragment();
	//			ft.replace(R.id.viewPager, f, tag);
			} else {
	//			ft.attach(f);
			} */

			
			pager.setCurrentItem(tab.getPosition(), true);
		}

		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
/*			if (f == null)
				return;
			FragmentManager fm = getSupportFragmentManager();
			if (fm.getBackStackEntryCount() > 0)
				fm.popBackStack(fm.getBackStackEntryAt(0).getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			getActionBar().setDisplayHomeAsUpEnabled(false);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				getActionBar().setHomeButtonEnabled(false);
			}
			
			f = null; */
		}
	}
	
	class DienststatusTabListener extends MyTabListener {
		boolean flat;
		public DienststatusTabListener(boolean flat) {
			super(flat ? "warning" : "all");
			this.flat = flat;
		}
	}
	
	class StatisticsTabListener extends MyTabListener {
		public StatisticsTabListener() {
			super("statistics");
		}

	}

	class NewsTabListener extends MyTabListener {
		public NewsTabListener() {
			super("news");
		}
	}	
	
	class FragmentAdapter extends FragmentPagerAdapter {

		public FragmentAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new DienststatusFragment("all");
			case 1:
				return new DienststatusFragment((String)null);
			case 2:
				return new StatisticsFragment();
			case 3:
				return new NewsListFragment();

			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}
	}
	
	FragmentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_fragment_activity);
		
		pager = (ViewPager) findViewById(R.id.viewPager);
		adapter = new FragmentAdapter(getSupportFragmentManager());
		pager.setOnPageChangeListener(this);
		pager.setAdapter(adapter);
		
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getActionBar().setDisplayShowTitleEnabled(true);
//		getActionBar().setSubtitle("huhu");

		if (savedInstanceState == null)
			isCreated = true;
		
		getActionBar().addTab(getActionBar().newTab()
				.setText(R.string.tab_all)
				.setTabListener(new DienststatusTabListener(false)));
		getActionBar().addTab(getActionBar().newTab()
				.setText(R.string.tab_warnings)
				.setTabListener(new DienststatusTabListener(true)));
		getActionBar().addTab(getActionBar().newTab()
				.setText(R.string.tab_statistics)
				.setTabListener(new StatisticsTabListener()));
		getActionBar().addTab(getActionBar().newTab()
				.setText(R.string.news)
				.setTabListener(new NewsTabListener()));

		getActionBar().setDisplayHomeAsUpEnabled(false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setHomeButtonEnabled(false);
		}
		isCreated = true;
		
		if (savedInstanceState != null) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
	}
	
	@Override
	protected void onStart() {
		setProgressBarIndeterminateVisibility(false);
		super.onStart();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
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
			FragmentManager fragmentManager = getSupportFragmentManager();			
			fragmentManager.popBackStack();
			break;
		case R.id.preferences:
			Intent intent = new Intent(this, DienststatusPreferencesActivity.class);
			startActivity(intent);
			break;
		
		default: return super.onOptionsItemSelected(item);
		}
		return true;
	}

	
	/* FIXME:
	 * The ViewPager caches the Fragments based on the position and won't recreate
	 * the views simply on a fragment replacement.
	 * 
	 * TODO:
	 * Try to implement the sh** in:
	 * http://stackoverflow.com/questions/7723964/replace-fragment-inside-a-viewpager
	 */
	@Override
	public void onNodeDetails(DienststatusFragment fragment, HRWNode node) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.setCustomAnimations(
//				R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_left_exit /*,
//                R.animator.fragment_slide_right_enter,
//                R.animator.fragment_slide_right_exit */);
		fragmentTransaction.replace(R.id.viewPager, new DetailFragment(node.id), fragment.getTag());
		fragmentTransaction.addToBackStack(node.getParentId());
		fragmentTransaction.commit();		
	}

	@Override
	public void onNodeClicked(DienststatusFragment fragment, HRWNode node) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.setCustomAnimations(
//				R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_left_exit /*,
//                R.animator.fragment_slide_right_enter,
//                R.animator.fragment_slide_right_exit */);
		Fragment f = new DienststatusFragment(node);
		fragmentTransaction.replace(R.id.viewPager, f, fragment.getTag());

		Log.v(tag, "pushing new " + node.getParentId());
		fragmentTransaction.addToBackStack(node.getParentId());
		fragmentTransaction.commit();
	}

	@Override
	public void onStatisticClicked(StatisticsFragment fragment, int category) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.setCustomAnimations(
//				R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_left_exit /*,
//                R.animator.fragment_slide_right_enter,
//                R.animator.fragment_slide_right_exit */);
		fragmentTransaction.replace(R.id.viewPager, new StatisticsFragment(category), fragment.getTag());
//		Log.v(tag, "pushing new " + node.getParentId());
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	public void onNewsDetails(NewsListFragment fragment, NewsItem item) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.setCustomAnimations(
//				R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_left_exit /*,
//                R.animator.fragment_slide_right_enter,
//                R.animator.fragment_slide_right_exit */);
		fragmentTransaction.replace(R.id.viewPager, new NewsItemFragment(item), fragment.getTag());
//		Log.v(tag, "pushing new " + node.getParentId());
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	public void onPageScrollStateChanged(int position) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		getActionBar().selectTab(getActionBar().getTabAt(position));
		ActivityFragmentInterface f = (ActivityFragmentInterface)adapter.getItem(position);
		f.updateActionBar(getActionBar());
	}
}
