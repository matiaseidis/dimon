package org.test.streaming;

public class ProgressReport {

	private Object target;
	private long timestamp;
	private int amountOfReceivedBytes;
	private long msToComplete = -1;
	private int progressPct;
	private long msDownloading;
	private double bandWidth = 0;

	public ProgressReport(Object target) {
		this.setTarget(target);
	}

	public int getAmountOfReceivedBytes() {
		return amountOfReceivedBytes;
	}

	public void setAmountOfReceivedBytes(int amountOfReceivedBytes) {
		this.amountOfReceivedBytes = amountOfReceivedBytes;
	}

	public long getMsToComplete() {
		return msToComplete;
	}

	public void setMsToComplete(long msToComplete) {
		this.msToComplete = msToComplete;
	}

	public int getProgressPct() {
		return progressPct;
	}

	public void setProgressPct(int progressPct) {
		this.progressPct = progressPct;
	}

	public long getMsDownloading() {
		return msDownloading;
	}

	public void setMsDownloading(long msDownloading) {
		this.msDownloading = msDownloading;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public double getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(double bandWidth) {
		this.bandWidth = bandWidth;
	}

}
