package org.test.streaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.status.CachoProgress;

public class LastRetrievalPlanLocator {

	protected static final Log log = LogFactory.getLog(LastRetrievalPlanLocator.class);

	private static final LastRetrievalPlanLocator instance = new LastRetrievalPlanLocator();
	private final List<CachoStreamer> streamers = new ArrayList<CachoStreamer>();

	private final Map<String, CachoProgress> progress = new HashMap<String, CachoProgress>();

	private LastRetrievalPlanLocator() {
		log.debug("LastRetrievalPlanLocator created");
	}

	public static LastRetrievalPlanLocator getInstance() {
		return instance;
	}

	public List<CachoStreamer> getStreamers() {
		return streamers;
	}

	/**
	 * Cierro los streams del plan anterior
	 */
	public void clean() {
		for (CachoStreamer sc : this.getStreamers()) {
			try {
				log.debug("About to close stream...");
				sc.close();
			} catch (IOException e) {
				log.error("Unable to close stream...", e);
			}
		}
		this.getStreamers().clear();
	}

	public void addCachoStreamer(CachoStreamer cachoStreamer) {
		this.getStreamers().add(cachoStreamer);
	}

	public String getPlanId() {
		return "dummyPlanId";
	}

	public Map<String, CachoProgress> getProgress() {
		return progress;
	}

	public static String progressKey(CachoProgress cp) {
		return progressKey(cp.getFirstByteIndex(), cp.getLength());
	}

	public CachoProgress getProgressFor(int firstByteIndex, int length) {

		CachoProgress p = this.getProgress().get(progressKey(firstByteIndex, length));

		if (p == null) {
			p = new CachoProgress(firstByteIndex, length);
			this.getProgress().put(progressKey(p), p);
		}

		return p;
	}

	private static String progressKey(int firstByteIndex, int length) {
		return firstByteIndex + "-" + length;
	}
}
