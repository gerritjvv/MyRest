package org.myrest.util;

import org.junit.Test;

public class RegexPerfTest {

	@Test
	public void testJava() throws Throwable {
		String path = "/myvar/${a}/${b}";

		RestPathSchema schema = RestPathParser.parseSchema(path);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			schema.matches("/myvar/avb11/xyz123");
		}

		long end = System.currentTimeMillis() - start;

		System.out.println("Time: " + end + " ms");
	}

	@Test
	public void testJavaAutomaton() throws Throwable {
		String path = "/myvar/${a}/${b}";

		AutomatonRestPathSchema schema = AutomatonRestPathParser
				.parseSchema(path);

		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			schema.matches("/myvar/avb11/xyz123");
		}

		long end = System.currentTimeMillis() - start;

		System.out.println("Automaton Time: " + end + " ms");
	}

}
