package org.test.streaming;

import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class CachoClientPullJandler extends CachoClientHandler {
	protected static final Log log = LogFactory.getLog(CachoClientPullJandler.class);
	private OutputStream out;
	private long firstChunnkTimestamp;
	private ProgressReport progressReport;
	private ProgressObserver progressObserver;

	public CachoClientPullJandler(CachoRequest cachoRequest, OutputStream out) {
		super(cachoRequest);
		this.setOut(out);
		this.setProgressReport(new ProgressReport(cachoRequest));
		this.setFirstChunnkTimestamp(System.currentTimeMillis());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		synchronized (this) {
			int readableBytes = cacho.readableBytes();
			cacho.readBytes(this.getOut(), readableBytes);
			this.setAmountOfReceivedBytes(this.getAmountOfReceivedBytes() + readableBytes);
		}
		long now = System.currentTimeMillis();
		int length = this.getCachoRequest().getLength();
		int remainingBytes = length - this.getAmountOfReceivedBytes();
		long deltaT = now - this.getFirstChunnkTimestamp();
		this.setMsDownloading(deltaT);
		long remainingTime = remainingBytes * deltaT / this.getAmountOfReceivedBytes();
		this.setMsToComplete(remainingTime);
		this.setProgressPct((int) (((double) this.getAmountOfReceivedBytes() / length) * 100));
		double bw = this.getAmountOfReceivedBytes() / ((double) deltaT / 1000d);
		this.getProgressReport().setBandWidth(bw);
		this.getProgressObserver().progressed(this.getProgressReport());
		
		String ip = "localhost";
		LastRetrievalPlanLocator.getInstance()
		.getProgressFor(this.getCachoRequest().getFirstByteIndex(), this.getCachoRequest().getLength())
		.update(
				this.getAmountOfReceivedBytes(),
				this.getProgressReport().getBandWidth(),
				this.getProgressReport().getProgressPct(),
				this.getProgressReport().getMsToComplete(),
//				((InetSocketAddress)ctx.getChannel().getRemoteAddress()).getAddress().getCanonicalHostName(),
				ip,
				((InetSocketAddress)ctx.getChannel().getRemoteAddress()).getPort()
				);
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public int getAmountOfReceivedBytes() {
		return this.getProgressReport().getAmountOfReceivedBytes();
	}

	public void setAmountOfReceivedBytes(int amountOfReceivedBytes) {
		this.getProgressReport().setAmountOfReceivedBytes(amountOfReceivedBytes);
	}

	public long getMsToComplete() {
		return this.getProgressReport().getMsToComplete();
	}

	public void setMsToComplete(long msToComplete) {
		this.getProgressReport().setMsToComplete(msToComplete);
	}

	public long getFirstChunnkTimestamp() {
		return firstChunnkTimestamp;
	}

	public void setFirstChunnkTimestamp(long firstChunnkTimestamp) {
		this.firstChunnkTimestamp = firstChunnkTimestamp;
	}

	public void setProgressPct(int progressPct) {
		this.getProgressReport().setProgressPct(progressPct);
	}

	public void setMsDownloading(long msDownloading) {
		this.getProgressReport().setMsDownloading(msDownloading);
	}

	public ProgressReport getProgressReport() {
		return progressReport;
	}

	public void setProgressReport(ProgressReport progressReport) {
		this.progressReport = progressReport;
	}

	public ProgressObserver getProgressObserver() {
		return progressObserver;
	}

	public void setProgressObserver(ProgressObserver progressObserver) {
		this.progressObserver = progressObserver;
	}

}
