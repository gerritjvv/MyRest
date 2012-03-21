package org.myrest.http;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.streams.commons.app.ApplicationService;

/**
 * 
 * Startup an Http Netty Server
 * 
 */
public class HttpRestServer implements ApplicationService {

	final ExecutorService bossExecutor;
	final ExecutorService workExecutor;
	final ChannelUpstreamHandler httpHandler;
	final int port;
	ServerBootstrap bootstrap;

	final Timer timer = new HashedWheelTimer();
	final int timeoutSeconds = 20;

	public HttpRestServer(ExecutorService bossExecutor,
			ExecutorService workExecutor, ChannelUpstreamHandler httpHandler,
			int port) {
		super();
		this.bossExecutor = bossExecutor;
		this.workExecutor = workExecutor;
		this.httpHandler = httpHandler;
		this.port = port;
	}

	/**
	 * Start the server
	 */
	public void start() {
		if (bootstrap != null)
			return;

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				bossExecutor, workExecutor));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = new DefaultChannelPipeline();
				pipeline.addLast("readtimeout", new ReadTimeoutHandler(timer,
						timeoutSeconds));
				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("handler", httpHandler);

				return pipeline;
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
	}

	/**
	 * Shutdown Server
	 */
	public void shutdown() {
		bossExecutor.shutdown();
		workExecutor.shutdown();
	}

}
