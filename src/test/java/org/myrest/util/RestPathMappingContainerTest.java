package org.myrest.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;
import org.myrest.io.PlainText;

public class RestPathMappingContainerTest {

	@Test
	public void testMapping() throws Throwable {

		RestPathMappingContainer mcontainer = new RestPathMappingContainer(
				"/myvar/${id}/${name}  org.myrest.util.MockController.myrestOp");

		PlainText w = (PlainText) mcontainer.call(null, "/myvar/123/abc");

		assertNotNull(w);
		assertEquals("123abc", w.asString());
	}

	@Test
	public void testMapping2() throws Throwable {

		RestPathMappingContainer mcontainer = new RestPathMappingContainer(
				"/myvar/${id}/${name}  org.myrest.util.MockController.myrestOp2");

		HttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.GET, "test");

		PlainText w = (PlainText) mcontainer.call(req, "/myvar/123/abc");

		assertNotNull(w);
		assertEquals("GET", w.asString());

	}

}
