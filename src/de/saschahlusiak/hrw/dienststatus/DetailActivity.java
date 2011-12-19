package de.saschahlusiak.hrw.dienststatus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends Activity {
	private TextView path, name, status, label, duration;
	private Button url;
	private HRWNode node;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.node_detail);

		node = new HRWNode();

		path = (TextView) findViewById(R.id.path);
		name = (TextView) findViewById(R.id.name);
		status = (TextView) findViewById(R.id.status);
		label = (TextView) findViewById(R.id.label);
		url = (Button) findViewById(R.id.url);
		duration = (TextView) findViewById(R.id.duration);

		node.name = getIntent().getExtras().getString("name");
		node.url = getIntent().getExtras().getString("url");
		node.title = getIntent().getExtras().getString("label");
		node.duration = getIntent().getExtras().getString("duration");
		node.acknowledged = getIntent().getExtras().getBoolean("acknowledged");
		node.status = getIntent().getExtras().getInt("status");
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
		
		url.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent viewIntent = new Intent("android.intent.action.VIEW",
						Uri.parse(node.url));
				startActivity(viewIntent);
			}
		});
	}

}
