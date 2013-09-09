package org.test.streaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LastRetrievalPlanLocator {
	
	protected static final Log log = LogFactory.getLog(LastRetrievalPlanLocator.class);
	
	private static final LastRetrievalPlanLocator INSTANCE = new LastRetrievalPlanLocator();
	private final List<CachoStreamer> streamers = new ArrayList<CachoStreamer>();
	private Map<CachoRequest, ProgressReport> progress = new HashMap<CachoRequest, ProgressReport>();
	
	private LastRetrievalPlanLocator(){
		log.debug("LastRetrievalPlanLocator created");
	}
	
	public static LastRetrievalPlanLocator getInstance() {
		return INSTANCE;
	}

	public List<CachoStreamer> getStreamers() {
		return streamers;
	}

	/**
	 * Cierro los streams del plan anterior
	 */
	public void clean(){
		for(CachoStreamer sc : this.getStreamers()) {
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
		return "plan-id";
	}

	public void addProgress(Map<CachoRequest, ProgressReport> progress) {
		// TODO Auto-generated method stub
		
	}

	public Map<CachoRequest, ProgressReport> getProgress() {
		return progress;
	}

	public void setProgress(Map<CachoRequest, ProgressReport> progress) {
		this.progress = progress;
	}

}
