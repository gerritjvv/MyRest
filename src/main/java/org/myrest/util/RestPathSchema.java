package org.myrest.util;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class RestPathSchema {

	private static final Logger LOG = Logger.getLogger(RestPathSchema.class);
	
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
		LOG.info("Matcher: " + path + " to: " + match + " : " + match.matcher(path).matches());
		return match.matcher(path).matches();
	}

}
