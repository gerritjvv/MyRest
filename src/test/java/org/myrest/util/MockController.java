package org.myrest.util;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.myrest.io.PlainText;

public class MockController{
	
	
	public static HttpResponse myrestOp(String id, String name) throws UnsupportedEncodingException{
		return new PlainText(id + name);
	}
	
	public static HttpResponse myrestOp2(HttpRequest req, String id, String name) throws UnsupportedEncodingException{
		return new PlainText(req.getMethod().getName());
	}
	
}