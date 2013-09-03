package org.test.streaming;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ProgressLogger implements StreamingProgressObserver {

	int count;

	@Override
	public void progressed(Map<CachoRequest, ProgressReport> progress) {
		count++;
		if (count % 20 == 0) {
			print(progress);
		}
	}

	protected void print(Map<CachoRequest, ProgressReport> progress) {
		Set<Entry<CachoRequest, ProgressReport>> entrySet = progress.entrySet();
		synchronized (progress) {
			double totalBw = 0;
			for (Entry<CachoRequest, ProgressReport> entry : entrySet) {
				String firstByteIndex = String.valueOf(entry.getKey().getFirstByteIndex());
				long msToComplete = entry.getValue().getMsToComplete();
				String ttg = String.valueOf(msToComplete >= 0 ? msToComplete : "-");
				double d = entry.getValue().getBandWidth();
				totalBw += d;
				System.out.println(firstByteIndex + StringUtils.repeat("-", 15 - firstByteIndex.length()) + entry.getValue().getProgressPct() + "%, " + ttg + " ms to complete, speed: " + d + " Bps");
			}
			System.out.println("Total speed: " + totalBw + " Bps");
		}
		System.out.println("-------------------------------------------");
	}

	@Override
	public void done(Map<CachoRequest, ProgressReport> progress) {
		print(progress);
		Set<Entry<CachoRequest, ProgressReport>> entrySet = progress.entrySet();
		for (Entry<CachoRequest, ProgressReport> entry : entrySet) {
			String firstByteIndex = String.valueOf(entry.getKey().getFirstByteIndex());
			long msDownloading = entry.getValue().getMsDownloading();
			String elapsed = String.valueOf(msDownloading);
			System.out.println(firstByteIndex + StringUtils.repeat("-", 15 - firstByteIndex.length()) + " downloaded in " + msDownloading / 1000 + " s.");
		}
		System.out.println("-------------------------------------------");

	}
}
