package org.myrest.util;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Parses the schema that is defined for a path
 * 
 */
public class RestPathParser {

	/**
	 * Creates a RestPathSchema instance that can match a given path and extract
	 * its url values.
	 * 
	 * @param path
	 * @return RestPathScema
	 */
	public static final RestPathSchema parseSchema(String path) {

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

		return new RestPathSchema(Arrays.copyOf(vars, varsIndex),
				Pattern.compile(pattern.toString()));
	}

}
