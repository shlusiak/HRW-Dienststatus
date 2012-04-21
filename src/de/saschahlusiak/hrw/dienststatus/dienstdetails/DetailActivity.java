package de.saschahlusiak.hrw.dienststatus.dienstdetails;

import java.util.ArrayList;

import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.model.HRWNode;
import de.saschahlusiak.hrw.dienststatus.model.HRWService;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private TextView path, name, status, label, duration, url;
	private HRWNode node;
	private ExpandableListView services;
	DetailServiceAdapter adapter;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.node_detail);

		node = new HRWNode();

		path = (TextView) findViewById(R.id.path);
		name = (TextView) findViewById(R.id.name);
		status = (TextView) findViewById(R.id.status);
		label = (TextView) findViewById(R.id.label);
		url = (TextView) findViewById(R.id.url);
		duration = (TextView) findViewById(R.id.duration);
		services = (ExpandableListView) findViewById(R.id.services);

		node.name = getIntent().getExtras().getString("name");
		node.url = getIntent().getExtras().getString("url");
		node.title = getIntent().getExtras().getString("label");
		node.duration = getIntent().getExtras().getString("duration");
		node.acknowledged = getIntent().getExtras().getBoolean("acknowledged");
		node.status = getIntent().getExtras().getInt("status");
		node.output = (ArrayList<HRWService>) getIntent().getExtras().getSerializable("output");
		
		if (node.url != null && node.title != null)
			if (node.url.equals(node.title))
				node.title = null;
		label.setTextColor(Color.DKGRAY);

		path.setText(getIntent().getExtras().getString("path"));
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
		
		adapter = new DetailServiceAdapter(this, node.output);
		services.setAdapter(adapter);
		
		if (adapter.getStatusGroupPosition(HRWNode.ERROR) >= 0)
			services.expandGroup(adapter.getStatusGroupPosition(HRWNode.ERROR));
		if (adapter.getStatusGroupPosition(HRWNode.WARNING) >= 0)
			services.expandGroup(adapter.getStatusGroupPosition(HRWNode.WARNING));
		if (adapter.getStatusGroupPosition(HRWNode.UNKNOWN) >= 0)
			services.expandGroup(adapter.getStatusGroupPosition(HRWNode.UNKNOWN));
		}

}
