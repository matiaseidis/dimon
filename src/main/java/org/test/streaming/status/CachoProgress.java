package org.test.streaming.status;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CachoProgress {

	// private CachoRequest cachoRequest;
	// private ProgressReport progressReport;

	private int firstByteIndex;
	private int length;
	private int amountOfReceivedBytes;
	private double bandWidth;
	private int progressPct;
	private long msToComplete;
	private String host;
	private int port;
	private boolean repotedAsComplete = false;

	public CachoProgress(int firstByteIndex, int length) {
		super();
		this.firstByteIndex = firstByteIndex;
		this.length = length;
	}

	public CachoProgress() {

	}

	// public CachoProgress(CachoRequest cachoRequest,
	// ProgressReport progressReport) {
	// super();
	// this.cachoRequest = cachoRequest;
	// this.progressReport = progressReport;
	// }
	//
	// public CachoRequest getCachoRequest() {
	// return cachoRequest;
	// }
	//
	// public void setCachoRequest(CachoRequest cachoRequest) {
	// this.cachoRequest = cachoRequest;
	// }
	//
	// public ProgressReport getProgressReport() {
	// return progressReport;
	// }
	//
	// public void setProgressReport(ProgressReport progressReport) {
	// this.progressReport = progressReport;
	// }

	public int getFirstByteIndex() {
		return firstByteIndex;
	}

	public void setFirstByteIndex(int firstByteIndex) {
		this.firstByteIndex = firstByteIndex;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void update(int amountOfReceivedBytes, double bandWidth, int progressPct, long msToComplete, String host, int port) {
		this.setAmountOfReceivedBytes(amountOfReceivedBytes);
		this.setBandWidth(bandWidth);
		this.setProgressPct(progressPct);
		this.setMsToComplete(msToComplete);
		this.setHost(host);
		this.setPort(port);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int getAmountOfReceivedBytes() {
		return amountOfReceivedBytes;
	}

	public void setAmountOfReceivedBytes(int amountOfReceivedBytes) {
		this.amountOfReceivedBytes = amountOfReceivedBytes;
	}

	public double getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(double bandWidth) {
		this.bandWidth = bandWidth;
	}

	public int getProgressPct() {
		return progressPct;
	}

	public void setProgressPct(int progressPct) {
		this.progressPct = progressPct;
	}

	public long getMsToComplete() {
		return msToComplete;
	}

	public void setMsToComplete(long msToComplete) {
		this.msToComplete = msToComplete;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void reportedAsComplete() {
		this.repotedAsComplete = true;
	}

	public boolean isRepotedAsComplete() {
		return repotedAsComplete;
	}


}
