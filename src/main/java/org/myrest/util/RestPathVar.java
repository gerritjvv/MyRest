package org.myrest.util;

import java.util.List;

/**
 * 
 * Represents a variable in the rest path .e.g /myparam/${var}/
 * 
 */
public class RestPathVar {

	final int index;
	final String name;

	public RestPathVar(int index, String name) {
		super();
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public final String getValue(String[] arr) {
		return arr[index];
	}

	public final String getValue(List<String> list) {
		return list.get(index);
	}
}
