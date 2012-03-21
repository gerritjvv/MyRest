package org.myrest.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.myrest.util.RestPathParser;
import org.myrest.util.RestPathSchema;
import org.myrest.util.RestPathVar;

/**
 * 
 * Test that the Rest Path Schema parsing and matching works as expected.
 * 
 */
public class RestPathParserTest {

	@Test
	public void testParserTwoVariables() {

		String path = "/myvar/${a}/${b}";

		RestPathSchema schema = RestPathParser.parseSchema(path);

		RestPathVar[] vars = schema.getVars();
		assertEquals(2, vars.length);
		assertEquals("a", vars[0].getName());
		assertEquals(1, vars[0].getIndex());
		assertEquals("b", vars[1].getName());
		assertEquals(2, vars[1].getIndex());

		System.out.println("Pattern: " + schema.getPattern());
		// test matching
		assertTrue(schema.matches("/myvar/122/2333"));
		assertTrue(schema.matches("/myvar/122/2333/"));
		assertTrue(schema.matches("/myvar/avb11/xyz123"));

		assertFalse(schema.matches("/myvar/avb11"));
		assertFalse(schema.matches("/avb11"));
	}

	@Test
	public void testParserPlaceVarPlaceVar() {

		String path = "/myvar/${a}/myvar2/${b}";

		RestPathSchema schema = RestPathParser.parseSchema(path);

		RestPathVar[] vars = schema.getVars();
		assertEquals(2, vars.length);
		assertEquals("a", vars[0].getName());
		assertEquals(1, vars[0].getIndex());
		assertEquals("b", vars[1].getName());
		assertEquals(3, vars[1].getIndex());

		System.out.println("Pattern: " + schema.getPattern());
		// test matching
		assertTrue(schema.matches("/myvar/122/myvar2/121"));
		assertTrue(schema.matches("/myvar/122/myvar2/121/"));
		assertTrue(schema.matches("/myvar/avb11/myvar2/xyz123"));

		assertFalse(schema.matches("/myvar/myvar2/xyz123"));
		assertFalse(schema.matches("/myvar"));

	}

}
