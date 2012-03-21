package org.myrest.util;

import java.util.regex.Pattern;

public class RestPathSchema {

	final RestPathVar[] pathVars;
	final Pattern match;

	public RestPathSchema(RestPathVar[] pathVars, Pattern match) {
		super();
		this.pathVars = pathVars;
		this.match = match;
	}

	public final Pattern getPattern() {
		return match;
	}

	public final RestPathVar[] getVars() {
		return pathVars;
	}

	public final boolean matches(String path) {
		return match.matcher(path).matches();
	}

}
