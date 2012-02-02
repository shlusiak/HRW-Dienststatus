package de.saschahlusiak.hrw.dienststatus.news;

import java.io.Serializable;

public class NewsItem implements Serializable {
	private static final long serialVersionUID = 7804036429254970248L;
	
	String header, title;
	String teaser;
	String pictureURL;
	
	NewsItem() {
		teaser = header = title = pictureURL = null;
	}
}
