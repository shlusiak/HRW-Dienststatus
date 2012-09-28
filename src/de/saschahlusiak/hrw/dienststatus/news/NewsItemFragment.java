package de.saschahlusiak.hrw.dienststatus.news;

import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.model.NewsItem;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsItemFragment extends Fragment {
	NewsItem news;
	static final String tag = NewsItemFragment.class.getSimpleName();
	
	public NewsItemFragment() {
		news = null;
	}
	
	public NewsItemFragment(NewsItem item) {
		this.news = item;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			news = (NewsItem)savedInstanceState.getSerializable("item");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("item", news);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		getActivity().getActionBar().setTitle(news.title.equals("") ? news.header : news.header);
		getActivity().getActionBar().setSubtitle(news.title.equals("") ? null : news.title);
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.news_item, container, false);
		WebView web;
		TextView title;
		
		web = (WebView) v.findViewById(R.id.content);
		title = (TextView) v.findViewById(R.id.title);
		title.setVisibility(View.GONE);

		web.setBackgroundColor(Color.TRANSPARENT);
		web.setClickable(true);
		web.setFocusable(false);
		web.setEnabled(true);
				
		String content = "";
		content += 
				"<html>" +
		  		  "<head>" +
				    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
		  		  "</head>" +
		  		  "<body style=\"background-color:#000000\">";
		if (news.pictureURL != null && !news.pictureURL.equals(""))
			content +=
					 "<img src=\"" + news.pictureURL + "\" width=140 style=\"float:left;padding:3px\" />";
		content +=
		  		    "<font color=#cfcfcf>" +
		  		      news.teaser +
					"</font>" +
				  "</body>" +
				"</html>";
		
		web.loadData(content, "text/html", "UTF-8");
		
		if (news.title.length() <= 1) {
			title.setText(news.header);
		} else {
			title.setText(news.title);
		}
		
		return v;
	}
}
