package de.saschahlusiak.hrw.dienststatus;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Color;

public class HRWNode implements Serializable {
	static final long serialVersionUID = 1;
	public static class Service implements Serializable {
		static final long serialVersionUID = 2;
		public Service(String name, String output) {
			this.name = name;
			this.output = output;
		}
		String name, output;
	};
	
	
	public String name, title, url, duration, path;
	public Boolean acknowledged;
	public int status;
	public String id;
	public Boolean hasSubItems = false;
	public ArrayList<Service> output;
	HRWNode parent = null;
	
	
	public HRWNode() {
		output = new ArrayList<Service>();
		url = null;
	}
	
	
	public int getStatusText() {
		switch(status){
		case 0:
			return R.string.status_ok;
		case 1:
			return R.string.status_unknown;
		case 2:
			if (acknowledged)
				return R.string.status_inwork;
			else
				return R.string.status_warning;
		case 3:
			return R.string.status_error;
		
		case -1:
		default:
			return R.string.status_unset;
		}
	}
	public int getStatusBackgroundColor() {
		if (status == 0)
			return Color.BLACK;
		if (status == 1)
			return Color.BLACK;
		if (status == 2 && acknowledged)
			return Color.argb(200, 0xef, 0xdf, 0x5f);
		if (status == 2 && !acknowledged)
			return Color.rgb(0xff, 0xd7, 0x5f);
		if (status == 3)
			return Color.rgb(0xff, 0x57, 0x57);
		return Color.BLACK;
	}
	public int getStatusTextColor() {
		if (status == 0)
			return Color.WHITE;
		if (status == 1)
			return Color.LTGRAY;
		if (status == 2)
			return Color.BLACK;
		if (status == 3)
			return Color.BLACK;
		return Color.DKGRAY;
	}
	public String getPath(boolean dots) {
		String ret;
		if (parent == null)
			return name;
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
