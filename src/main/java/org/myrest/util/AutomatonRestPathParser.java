package org.myrest.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * 
 * Parses the schema that is defined for a path.<br/>
 * Uses the automaton regex library.<br/>
 * http://www.java.net/external?url=http://www.brics.dk/~amoeller/automaton/
 */
public final class AutomatonRestPathParser {

	/**
	 * Creates a RestPathSchema instance that can match a given path and extract
	 * its url values.
	 * 
	 * @param path
	 * @return RestPathScema
	 */
	public static final AutomatonRestPathSchema parseSchema(String path) {

		final String[] arr = StringUtils.split(path, '/');
		final int len = arr.length;
		final RestPathVar[] vars = new RestPathVar[len];

		final StringBuilder pattern = new StringBuilder(100);
		int varsIndex = 0;
		int index = 0;

		for (int i = 0; i < len; i++) {
			final String val = arr[i].trim();

			if (val.length() < 1)
				continue;

			if (val.startsWith("${")) {
				vars[varsIndex++] = new RestPathVar(index, val.substring(2,
						val.length() - 1));
				pattern.append("/[\\-\\$\\.\\^\\_\\[\\]\\(\\)0-9A-Za-z]+");
			} else {
				// is a place holder
				pattern.append("/").append(val);
			}
			index++;
		}

		pattern.append("/*.*");

		return new AutomatonRestPathSchema(Arrays.copyOf(vars, varsIndex),
				new RunAutomaton(new RegExp(pattern.toString()).toAutomaton()));
	}

}
