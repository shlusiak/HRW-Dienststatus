package de.saschahlusiak.hrw.dienststatus;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class HRWDienststatusActivity extends Activity implements
		OnItemClickListener {
	DienststatusAdapter adapter;
	private final String tag = HRWDienststatusActivity.class.getSimpleName();
	private String level = "all";
	private final HttpUriRequest uri = new HttpGet(
			"http://nagvis-pub.hs-weingarten.de/cgi-bin/nagxml.pl?all");
	private static Document dom = null;
	private static ArrayList<HRWNode> allnodes = new ArrayList<HRWNode>();
	private Handler handler = new Handler();
	private ProgressDialog progress;

	private void fillLevel(String level) {
		for (HRWNode node : allnodes) {
			if (level != null) {
				if (node.id.matches("^" + level + "\\.\\d*$"))
					adapter.addNode(node);
			} else {
				if (node.status != 0 && !node.hasSubItems)
					adapter.addNode(node);
			}
		}
	}

	Runnable refresh = new Runnable() {
		public void parseLevel(Node item, HRWNode HRWparent) {
			NodeList properties = item.getChildNodes();
			HRWNode node = new HRWNode();
			node.parent = HRWparent;
			allnodes.add(node);
			for (int j = 0; j < properties.getLength(); j++) {
				Node property = properties.item(j);
				String name = property.getNodeName();

				if (name.equals("name"))
					node.name = property.getTextContent();
				if (name.equals("title"))
					node.title = property.getTextContent();
				if (name.equals("url"))
					node.url = property.getTextContent();
				if (name.equals("duration"))
					node.duration = property.getTextContent();
				if (name.equals("acknowledged"))
					node.acknowledged = Integer.valueOf(property
							.getTextContent()) == 1;
				if (name.equals("status"))
					node.status = Integer.valueOf(property.getTextContent());
				if (name.equals("menuindex"))
					node.id = property.getTextContent();
				if (name.equals("group")) {
					node.hasSubItems = true;
					parseLevel(property, node);
				}
			}
		}

		@Override
		public void run() {
			if (dom == null) {
				try {
					DefaultHttpClient client = new DefaultHttpClient();
					HttpResponse resp = client.execute(uri);

					StatusLine status = resp.getStatusLine();
					if (status.getStatusCode() != 200) {
						Log.d(tag, "HTTP error, invalid server status code: "
								+ resp.getStatusLine());
					}

					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					dom = builder.parse(resp.getEntity().getContent());
				} catch (Exception e) {
					Log.e(tag, e.getMessage());
					dom = null;
				}

				if (dom != null) {
				Element root = dom.getDocumentElement();
				NodeList items = root.getElementsByTagName("map");
				allnodes.clear();
				parseLevel(items.item(0), null);
				} else {
					allnodes.clear();
				}
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					progress.dismiss();
					if (dom != null)
						fillLevel(level);
					else
						Toast.makeText(HRWDienststatusActivity.this, "Verbindungsfehler", Toast.LENGTH_LONG).show();
					adapter.notifyDataSetChanged();
				}

			});
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent;
		Bundle extras;

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);
		
		adapter = new DienststatusAdapter(this);
		
		ListView listview = (ListView) findViewById(R.id.items);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		registerForContextMenu(listview);

		intent = getIntent();

		level = "all";
		// level = null;
		if (intent != null) {
			extras = intent.getExtras();
			if (extras != null) {
				level = extras.getString("level");
				setTitle(extras.getString("name"));
			}
		} else
			setTitle("Dienststatus");

		if (dom != null) {
			fillLevel(level);
		} else { 
			progress = ProgressDialog.show(this, "Bitte warten", "Daten werden gelesen");
			new Thread(refresh).start();
		}

	}

	public void showDetails(HRWNode node) {
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("name", node.name);
		intent.putExtra("duration", node.duration);
		intent.putExtra("url", node.url);
		intent.putExtra("label", node.title);
		intent.putExtra("status", node.status);
		intent.putExtra("acknowledged", node.acknowledged);
		intent.putExtra("name", node.name);
		intent.putExtra("path", node.parent.getPath());
		startActivity(intent);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HRWNode node;
		String newtitle;

		node = (HRWNode) adapter.getItem(position);
		if (node.hasSubItems == false) {
			showDetails(node);
			return;
		}

		Intent viewIntent = new Intent(this, HRWDienststatusActivity.class);

		newtitle = node.getPath();
		viewIntent.putExtra("level", node.id);
		viewIntent.putExtra("name", newtitle);
		startActivity(viewIntent);
	}

	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v,
			android.view.ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contextmenu, menu);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		HRWNode node = (HRWNode) adapter.getItem((int) info.id);

		menu.setHeaderTitle(node.name);

		if (node.url == null)
			menu.findItem(R.id.openinbrowser).setEnabled(false);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		HRWNode node;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		node = (HRWNode) adapter.getItem((int) info.id);
		if (node == null)
			return false;

		switch (item.getItemId()) {
		case R.id.openinbrowser:
			if (node.url == null)
				return false;
			Intent intent = new Intent("android.intent.action.VIEW", Uri
					.parse(node.url));
			startActivity(intent);
			return true;
		case R.id.details:
			showDetails(node);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh) {
			dom = null;
			adapter.clear();
			adapter.notifyDataSetChanged();
			progress = ProgressDialog.show(this, "Bitte warten", "Daten werden gelesen");
			new Thread(refresh).start();
			return true;
		}
		if (item.getItemId() == R.id.preferences) {
			Intent intent = new Intent(this, HRWPreferences.class);
			startActivity(intent);
		}
/*		if (item.getItemId() == R.id.view_all) {
			level = "all";

			adapter.clear();
			fillLevel(level);
			adapter.notifyDataSetChanged();

			return true;
		}
		if (item.getItemId() == R.id.view_warn) {
			level = null;

			adapter.clear();
			fillLevel(level);
			adapter.notifyDataSetChanged();

			return true;
		} */
		if (item.getItemId() == R.id.gotowebsite) {
			Intent intent = new Intent(
					"android.intent.action.VIEW",
					Uri
							.parse("http://www.hs-weingarten.de/web/rechenzentrum/dienststatus"));
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}