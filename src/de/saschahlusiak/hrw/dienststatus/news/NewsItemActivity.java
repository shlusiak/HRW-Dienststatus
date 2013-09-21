package de.saschahlusiak.hrw.dienststatus.news;

import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.model.NewsItem;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsItemActivity extends Activity {
	NewsItem news;
	static final String tag = NewsItemActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebView web;
		TextView title;
		
		setContentView(R.layout.news_item);
		
		news = (NewsItem) getIntent().getSerializableExtra("newsItem");
		title = (TextView) findViewById(R.id.title);
		
		setTitle(news.header);

		web = (WebView) findViewById(R.id.content);
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
		
		web.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
		
		if (news.title.length() <= 1) {
			title.setText(news.header);
		} else {
			title.setText(news.title);
		}
	}
}
