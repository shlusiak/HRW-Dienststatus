package de.saschahlusiak.hrw.dienststatus.dienstdetails;

import de.saschahlusiak.hrw.dienststatus.NodeFragmentInterface;
import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.model.Dienststatus;
import de.saschahlusiak.hrw.dienststatus.model.HRWNode;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment implements NodeFragmentInterface{
	private TextView path, name, status, label, duration, url;
	private HRWNode node;
	private ExpandableListView services;
	RefreshTask refreshTask;
	Menu optionsMenu;
	View mRefreshIndeterminateProgressView;
	String nodeId;
	DetailServiceAdapter adapter;
	
	public DetailFragment(String nodeId) {
		this.nodeId = nodeId;
		this.node = Dienststatus.findNode(nodeId);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (node.getParent() != null) {
			getActivity().getActionBar().setTitle(node.getParent().name);
			getActivity().getActionBar().setSubtitle(node.name);
		} else {
			getActivity().getActionBar().setTitle(node.name);
			getActivity().getActionBar().setSubtitle(null);			
		}
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.node_detail, container, false);
		
		path = (TextView) v.findViewById(R.id.path);
		name = (TextView) v.findViewById(R.id.name);
		status = (TextView) v.findViewById(R.id.status);
		label = (TextView) v.findViewById(R.id.label);
		url = (TextView) v.findViewById(R.id.url);
		duration = (TextView) v.findViewById(R.id.duration);
		services = (ExpandableListView) v.findViewById(R.id.services);
		
		updateView();
		
		return v;
	}
	
	public void refresh() {
		if (refreshTask != null) {
			refreshTask.cancel(true);
			try {
				refreshTask.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		refreshTask = new RefreshTask();
		refreshTask.execute();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.optionsmenu_dienststatus, menu);
		optionsMenu = menu;
		if (refreshTask != null && refreshTask.getStatus() == Status.RUNNING)
			setProgressActionView(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			refresh();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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
	
	void updateView() {
		if (node.url != null && node.title != null)
			if (node.url.equals(node.title))
				node.title = null;
		label.setTextColor(Color.DKGRAY);

		path.setText(node.getParentPath());
		name.setText(node.name);
		status.setText(node.getStatusText());
		if (node.url != null)
			url.setText(node.url);
		else
			url.setVisibility(View.GONE);
		if (node.title != null)
			label.setText(node.title);
		else
			label.setVisibility(View.GONE);
		if (node.duration != null)
			duration.setText("Seit " + node.duration);
		else
			duration.setVisibility(View.GONE);

		status.setBackgroundColor(node.getStatusBackgroundColor());
		status.setTextColor(node.getStatusTextColor());
		duration.setBackgroundColor(node.getStatusBackgroundColor());
		duration.setTextColor(node.getStatusTextColor());
		
		adapter = new DetailServiceAdapter(getActivity(), node.output);
		services.setAdapter(adapter);
		if (adapter.getStatusGroupPosition(HRWNode.ERROR) >= 0)
			services.expandGroup(adapter.getStatusGroupPosition(HRWNode.ERROR));
		if (adapter.getStatusGroupPosition(HRWNode.WARNING) >= 0)
			services.expandGroup(adapter.getStatusGroupPosition(HRWNode.WARNING));
		if (adapter.getStatusGroupPosition(HRWNode.UNKNOWN) >= 0)
			services.expandGroup(adapter.getStatusGroupPosition(HRWNode.UNKNOWN));
	}



	@Override
	public HRWNode getNode() {
		return node;
	}

	private class RefreshTask extends AsyncTask<Void, Integer, String> {
		@Override
		protected void onPreExecute() {
			setProgressActionView(true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			publishProgress(0, 0);
			try {
				return Dienststatus.fetch(getActivity());
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		protected void onCancelled() {
			refreshTask = null;
			if (getActivity() != null) {
				setProgressActionView(false);
				Toast.makeText(getActivity(), getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
			}

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			refreshTask = null;
			setProgressActionView(false);
			
			if (result == null) {
				node = Dienststatus.findNode(nodeId);
				updateView();
			} else {
				Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}	
}
