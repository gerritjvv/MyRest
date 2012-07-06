package org.myrest.util;

import dk.brics.automaton.RunAutomaton;

/**
 * Uses the automaton regex library.<br/>
 * http://www.java.net/external?url=http://www.brics.dk/~amoeller/automaton/
 * 
 *
 */
public final class AutomatonRestPathSchema {

	// private static final Logger LOG = Logger.getLogger(RestPathSchema.class);

	final RestPathVar[] pathVars;
	final RunAutomaton match;

	public AutomatonRestPathSchema(RestPathVar[] pathVars, RunAutomaton match) {
		super();
		this.pathVars = pathVars;
		this.match = match;
	}

	public final RunAutomaton getPattern() {
		return match;
	}

	public final RestPathVar[] getVars() {
		return pathVars;
	}

	public final boolean matches(String path) {
		return match.run(path);
	}

}
