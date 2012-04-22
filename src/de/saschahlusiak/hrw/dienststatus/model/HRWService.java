package de.saschahlusiak.hrw.dienststatus.model;

import java.io.Serializable;

public class HRWService implements Serializable {
	static final long serialVersionUID = 2;

	public HRWService(String name, String output, int status) {
		this.name = name;
		this.output = output;
		this.status = status;
		this.acknowledged = false;
	}

	public String name;
	public String output;
	public boolean acknowledged;
	public int status;

}
