package de.saschahlusiak.hrw.dienststatus.news;


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.saschahlusiak.hrw.dienststatus.AboutActivity;
import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.dienste.HRWDienststatusActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewsListActivity extends Activity implements OnItemClickListener {
	WebView webView;
	NewsAdapter adapter;
	ListView list;
	ProgressBar progress;
	
	private static final String WEBSITE = "http://www.hs-weingarten.de/web/rechenzentrum/aktuelles";

	private static final String tag = NewsListActivity.class.getSimpleName();
	private static final String CONTENT_URL =
			"http://portal.hs-weingarten.de/xml/web/rechenzentrum-intranet/aktuelles/-/101_INSTANCE_Ao9p?startpage=true&language=de&articleId=@articleId@&groupId=@groupId@&cur=@cur@&portletInstance=@portletInstanceName@&showIframe=@showIframe@&isArticle=@isArticle@&internet=true";
//	private static final String CONTENT_URL = "http://192.168.83.45/bla.xml";

	
	private class RefreshTask extends AsyncTask<Void, NewsItem, String> {
		ArrayList<NewsItem> mynodes = new ArrayList<NewsItem>();

		class MyParser extends DefaultHandler {
			Stack<String> current;
			String s;
			NewsItem news;
			boolean inContent;

			MyParser() {
				current = null;
				news = null;
				inContent = false;
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				if (localName.equals("xml-dataset"))
					news = new NewsItem();

				if (inContent) {
					int i;
					s += "<" + localName;
					for (i = 0; i < attributes.getLength(); i++) {
						s += " " + attributes.getLocalName(i);
						s += "=\"" + attributes.getValue(i) + "\"";
					}
					current.push(s);
				}
				s = "";

				if (localName.equals("xml-data-1")
				 || localName.equals("xml-data-2")
				 || localName.equals("xml-data-3")
				 || localName.equals("xml-data-4")) {
					inContent = true;
					current = new Stack<String>();
				}
			}

			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				s += String.copyValueOf(ch, start, length);
			}

			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				if (localName.equals("xml-dataset")) {
					publishProgress(news);
					news = null;
					current = null;
				} else if (localName.equals("xml-data-1")) {
					news.header = s;
					current = null;
					inContent = false;
				} else if (localName.equals("xml-data-2")) {
					news.title = s;
					current = null;
					inContent = false;
				} else if (localName.equals("xml-data-3")) {
					news.pictureURL = s;
					current = null;
					inContent = false;
				} else if (localName.equals("xml-data-4")) {
					news.teaser = s;
					current = null;
					inContent = false;
				} else if (inContent) {
					/* we are inside an element, record subelements */
					if (s.equals("")) {
						s = current.pop() + " />";
					} else {
						s = current.pop() + ">" + s + "</" + localName + ">";
					}
				}
			}
		}
		
		@Override
		protected void onPreExecute() {
			progress.setVisibility(View.VISIBLE);
			adapter.setNodes(mynodes);
			adapter.notifyDataSetChanged();
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... args) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				final HttpResponse resp = client.execute(new HttpGet(CONTENT_URL));

				final StatusLine status = resp.getStatusLine();
				if (isCancelled())
					return getString(R.string.cancelled);
				if (status.getStatusCode() != 200) {
					Log.d(tag,
							"HTTP error, invalid server status code: "
									+ resp.getStatusLine());

					return getString(R.string.invalid_http_status, resp.getStatusLine());
				}

				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				if (isCancelled())
					return getString(R.string.cancelled);
				sp.parse(resp.getEntity().getContent(), new MyParser());
			} catch (UnknownHostException e) { 
				Log.e(tag, e.getMessage());
				return getString(R.string.connection_error);
			} catch (Exception e) {
				Log.e(tag, e.getMessage());
				if (isCancelled())
					return getString(R.string.cancelled);
				return getString(R.string.connection_error);
			}

			return null;
		}
		
		@Override
		protected void onProgressUpdate(NewsItem... values) {
			mynodes.add(values[0]);
			adapter.notifyDataSetChanged();
			progress.setVisibility(View.GONE);
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onCancelled() {
			Toast.makeText(NewsListActivity.this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
			progress.setVisibility(View.GONE);
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null)
				Toast.makeText(NewsListActivity.this, result, Toast.LENGTH_SHORT).show();
			progress.setVisibility(View.GONE);
			super.onPostExecute(result);
		}
	}
	
	RefreshTask task = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list_activity);
		
		list = (ListView) findViewById(R.id.list);
		progress = (ProgressBar) findViewById(R.id.progress);
		
		adapter = new NewsAdapter(this);
		list.setAdapter(adapter);

		list.setOnItemClickListener(this);
		
		task = new RefreshTask();
		task.execute();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NewsItem news;

		news = (NewsItem) adapter.getItem(position);
		Intent intent = new Intent(this, NewsItemActivity.class);
		intent.putExtra("newsItem", news);
		startActivity(intent);		
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
			if (task != null) {
				task.cancel(false);
				try {
					task.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			task = new RefreshTask();
			task.execute();
			return true;
		}
		if (item.getItemId() == R.id.about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
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

}
