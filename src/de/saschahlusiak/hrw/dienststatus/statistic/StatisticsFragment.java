package de.saschahlusiak.hrw.dienststatus.statistic;

import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.io.InputStream;
import java.net.URL;
import de.saschahlusiak.hrw.dienststatus.AboutActivity;
import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.model.HRWNode;

public class StatisticsFragment extends ListFragment implements OnItemClickListener {
	private StatisticsAdapter adapter;
	int category;
	Menu optionsMenu;
	View mRefreshIndeterminateProgressView;

	
	static final String WEBSITE = "http://www.hs-weingarten.de/web/rechenzentrum/zahlen-und-fakten";
	
	public interface OnStatisticClicked {
		public void onStatisticClicked(StatisticsFragment fragment, int category);
	}
	
	OnStatisticClicked mListener;
	
	private static class PictureBundle {
		int index;
		BitmapDrawable d;
	};
	
	private class RefreshTask extends AsyncTask<String, PictureBundle, String> {
		@Override
		protected void onPreExecute() {			
			setProgressActionView(true);
			super.onPreExecute();			
		}
		
		@Override
		protected String doInBackground(String... urls) {
			InputStream is;
			publishProgress();
		
			for (int i=0; i < urls.length; i++) {
				try {
					PictureBundle b = new PictureBundle();
					b.index = i;
					is = (InputStream) new URL("http://static.hs-weingarten.de/portvis/" + urls[i] + ".png").getContent();
					b.d = (BitmapDrawable)Drawable.createFromStream(is, "src");
					if (isCancelled())
						break;
					publishProgress(b);
				} catch (Exception e) {
					e.printStackTrace();
					PictureBundle b = new PictureBundle();
					b.index = i;
					b.d = null;
					publishProgress(b);
//					return getString(R.string.connection_error);
				}
			}

			return null;
		}
		
		@Override
		protected void onProgressUpdate(PictureBundle... values) {
			if (values != null && values.length > 0) {
				adapter.add(values[0].d, values[0].index);
				adapter.setLoading(values[0].index + 1);
			} else {
				adapter.setLoading(0);
			}

			adapter.notifyDataSetChanged();
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onCancelled() {
			setProgressActionView(false);			
			adapter.setLoading(-1);
			adapter.notifyDataSetChanged();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null && getActivity() != null)
				Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			setProgressActionView(false);
			adapter.setLoading(-1);
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	RefreshTask task = null;

	
	private void refresh() {
		if (task != null)
			task.cancel(false);
		task = new RefreshTask();
		adapter.invalidate(StatisticsActivity.STATISTICS[category].length);
		task.execute(StatisticsActivity.STATISTICS[category]);
	}
	
	public StatisticsFragment() {
		this.category = 0;
	}
	
	public StatisticsFragment(int categoty) {
		this.category = categoty;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null)
			category = savedInstanceState.getInt("category");

		setHasOptionsMenu(true);
		adapter = new StatisticsAdapter(getActivity());
		setListAdapter(adapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("category", category);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.mListener = (OnStatisticClicked) getActivity();
		getListView().setOnItemClickListener(this);
		
		if (category != 0) {
			getActivity().getActionBar().setTitle(R.string.tab_statistics);
			getActivity().getActionBar().setSubtitle(StatisticsActivity.STATISTIC_TITLES[category]);
		} else {
			getActivity().getActionBar().setTitle(R.string.tab_statistics);
			getActivity().getActionBar().setSubtitle(null);
		}
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(category > 0);
		getActivity().getActionBar().setHomeButtonEnabled(category > 0);	
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		/* first fragment, refresh to fetch images */
		if (task == null)
			refresh();		
	}
	
	@Override
	public void onStop() {
		if (task != null) {
			task.cancel(false);
			task = null;
		}
		super.onStop();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.optionsmenu_statistics, menu);
		optionsMenu = menu;
		if (task != null && task.getStatus() == Status.RUNNING)
			setProgressActionView(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh) {
			refresh();
			return true;
		}
		if (item.getItemId() == R.id.gotowebsite) {
			Intent intent = new Intent(
					"android.intent.action.VIEW",
					Uri.parse(WEBSITE));
			startActivity(intent);
			return true;
		}
		if (item.getItemId() == R.id.sendemail) {
			try {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "rz-service@hs-weingarten.de" });
				intent.putExtra(Intent.EXTRA_SUBJECT, "Frage an das Rechenzentrum");
				intent.putExtra(Intent.EXTRA_TEXT, "Siehe: " + WEBSITE);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (category != 0)
			return;
		
		mListener.onStatisticClicked(this, (int)id + 1);
	}
	
	void setProgressActionView(boolean refreshing) {
		if (optionsMenu == null)
			return;
        final MenuItem refreshItem = optionsMenu.findItem(R.id.refresh);
        if (refreshItem != null) {
            if (refreshing) {
                if (mRefreshIndeterminateProgressView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE);
                    mRefreshIndeterminateProgressView = inflater.inflate(
                            R.layout.actionbar_indeterminate_progress, null);
                }

                refreshItem.setActionView(mRefreshIndeterminateProgressView);
            } else {
                refreshItem.setActionView(null);
            }
        }
	}	

}
