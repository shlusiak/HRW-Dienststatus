package de.saschahlusiak.hrw.dienststatus.model;

import java.io.Serializable;

public class HRWService implements Serializable {
	static final long serialVersionUID = 2;

	public HRWService(String name, String output) {
		this.name = name;
		this.output = output;
	}

	public String name;
	public String output;

}
