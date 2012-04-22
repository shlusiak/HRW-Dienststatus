package de.saschahlusiak.hrw.dienststatus.model;

import java.io.Serializable;
import java.util.ArrayList;
import de.saschahlusiak.hrw.dienststatus.R;
import android.graphics.Color;

public class HRWNode implements Serializable {
	static final long serialVersionUID = 1;

	public static final int UNSET = -1;
	public static final int OK = 0;
	public static final int UNKNOWN = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	
	public String name, title, url, duration, path;
	public boolean acknowledged;
	public int status;
	public String id;
	public boolean hasSubItems = false;
	public ArrayList<HRWService> output;
	HRWNode parent = null;
	
	
	public HRWNode(HRWNode parent) {
		output = new ArrayList<HRWService>();
		url = null;
		this.parent = parent;
		if (parent != null)
			parent.hasSubItems = true;
	}

	public HRWNode() {
		output = new ArrayList<HRWService>();
		url = null;
		parent = null;
	}
	
	public HRWNode getParent() {
		return parent;
	}
	
	public String getParentId() {
		return parent == null ? "all" : parent.id;
	}
	
	public String getId() {
		return id;
	}
	
	public HRWNode setName(String name) {
		this.name = name;
		return this;
	}
	
	public HRWNode setStatus(int status) {
		this.status = status;
		return this;
	}
	
	public HRWNode setId(String id) {
		this.id = id;
		return this;
	}
	
	public static int getStatusText(int status, boolean acknowledged) {
		switch(status){
		case OK:
			return R.string.status_ok;
		case UNKNOWN:
			return R.string.status_unknown;
		case WARNING:
			if (acknowledged)
				return R.string.status_inwork;
			else
				return R.string.status_warning;
		case ERROR:
			return R.string.status_error;
		
		case UNSET:
		default:
			return R.string.status_unset;
		}
	}
	
	public int getStatusText() {
		return getStatusText(status, acknowledged);
	}
	
	
	public static int getStatusBackgroundColor(int status, boolean acknowledged) {
		if (status == OK)
			return Color.BLACK;
		if (status == UNKNOWN)
			return Color.BLACK;
		if (status == WARNING && acknowledged)
			return Color.argb(200, 0xef, 0xdf, 0x5f);
		if (status == WARNING && !acknowledged)
			return Color.rgb(0xff, 0xd7, 0x5f);
		if (status == ERROR)
			return Color.rgb(0xff, 0x57, 0x57);
		return Color.BLACK;
	}
	
	public int getStatusBackgroundColor() {
		return getStatusBackgroundColor(status, acknowledged);
	}
	
	public static int getStatusTextColor(int status) {
		if (status == OK)
			return Color.WHITE;
		if (status == UNKNOWN)
			return Color.LTGRAY;
		if (status == WARNING)
			return Color.BLACK;
		if (status == ERROR)
			return Color.BLACK;
		return Color.DKGRAY;
	}
	
	public int getStatusTextColor() {
		return getStatusTextColor(status);
	}
	
	
	public String getFullPath() {
		String ret;
		ret = name;
		if (parent == null || parent.name == null)
			return ret;
		ret = parent.getFullPath() + " > " + name;
		
		return ret;
	}
	
	public String getPath(boolean dots) {
		String ret;
		ret = name;
		if (parent == null || parent.name == null)
			return ret;
		ret = parent.name + " > " + name;
		
		if (parent.parent == null || parent.parent.name == null || !dots)
			return ret;
		
		ret = "..." + " > " + ret;
		return ret;
	}
	public String getParentPath() {
		if (parent != null)
			return parent.getPath(false);
		else
			return "";
	}
	
}
