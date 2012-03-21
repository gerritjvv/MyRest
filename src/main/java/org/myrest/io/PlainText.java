package org.myrest.io;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * 
 * Support for writing plain text.
 * 
 */
public class PlainText extends DefaultHttpResponse {

	public PlainText(String val) throws UnsupportedEncodingException {
		super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(val.length());
		buffer.writeBytes(val.getBytes("UTF-8"));
		super.setContent(buffer);
	}

	public String asString() throws UnsupportedEncodingException {
		return new String(getContent().array(), "UTF-8");
	}

}
