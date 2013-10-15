package org.test.streaming;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class CachoRequester implements ProgressObserver {

	protected static final Log log = LogFactory.getLog(CachoRequester.class);

	private Map<CachoRequest, ProgressReport> progress = Collections.synchronizedMap(new TreeMap<CachoRequest, ProgressReport>());
	/**
	 * Maybe null
	 */
	private StreamingProgressObserver progressObserver;

	public CachoRequester() {
	}

	public void requestCacho(String host, int port, String movieFileName, int zeroBasedFirstBytePosition, int amountOfBytes, final OutputStream out) {
		CachoRequest cachoRequest = new CachoRequest(null, movieFileName, zeroBasedFirstBytePosition, amountOfBytes);

		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(new OioClientSocketChannelFactory(Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		CachoClientPullJandler cachoClientPullJandler = new CachoClientPullJandler(cachoRequest, out);
		cachoClientPullJandler.setProgressObserver(this);

		CachoRequest key = cachoRequest;
		ProgressReport value = cachoClientPullJandler.getProgressReport();

		this.getProgress().put(key, value);
		// LastRetrievalPlanLocator.getInstance().updateCachoProgress(key,
		// value);

		final ChannelPipeline pipeline = Channels.pipeline(new ObjectEncoder(), cachoClientPullJandler);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline;
			}
		});

		System.out.println(host);
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		// Wait until the connection is closed or the connection attempt fails.
		ChannelFuture awaitUninterruptibly = future.getChannel().getCloseFuture().awaitUninterruptibly();
		int expectedReceivedBytes = cachoRequest.getLength();
		int amountOfReceivedBytes = this.getProgress().get(cachoRequest).getAmountOfReceivedBytes();
		if (expectedReceivedBytes != amountOfReceivedBytes) {
			System.err.println("Fatal, channel closed before all expected bytes are received: " + expectedReceivedBytes + ", but received " + amountOfReceivedBytes);
		}
		Throwable cause = awaitUninterruptibly.getCause();
		// Shut down thread pools to exit.
		bootstrap.releaseExternalResources();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void progressed(ProgressReport progressReport) {
		CachoRequest key = (CachoRequest) progressReport.getTarget();
		ProgressReport value = progressReport;
		this.getProgress().put(key, value);
		// LastRetrievalPlanLocator.getInstance().updateCachoProgress(key,
		// value);
		if (this.getProgressObserver() != null) {
			this.getProgressObserver().progressed(this.getProgress());
		}
	}

	public Map<CachoRequest, ProgressReport> getProgress() {
		return progress;
	}

	public void setProgress(Map<CachoRequest, ProgressReport> progress) {
		this.progress = progress;
	}

	public StreamingProgressObserver getProgressObserver() {
		return progressObserver;
	}

	public void setProgressObserver(StreamingProgressObserver progressObserver) {
		this.progressObserver = progressObserver;
	}

}
