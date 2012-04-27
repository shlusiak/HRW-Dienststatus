package de.saschahlusiak.hrw.dienststatus.model;

import java.io.Serializable;

public class NewsItem implements Serializable {
	private static final long serialVersionUID = 7804036429254970248L;
	
	public String header, title;
	public String teaser;
	public String pictureURL;
	
	public NewsItem() {
		teaser = header = title = pictureURL = null;
	}
}
