package org.test.streaming.status;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.CachoRequest;
import org.test.streaming.Conf;
import org.test.streaming.Dimon;
import org.test.streaming.LastRetrievalPlanLocator;
import org.test.streaming.ProgressReport;

public class StatusHandler {

	protected static final Log log = LogFactory.getLog(Dimon.class);
	private static StatusHandler instance = new StatusHandler();
	private long REPORT_WINDOW = 5000;
	private Conf conf = null;

	// statusEvent/{event}/{ip}/{port}/{clientId}
	private String statusUri = "/statusEvent/%s/%s/%s";
	// planEvent/{action}/{ip}/{port}/{planId}/{clientId}/{byteCurrent}/{byteFrom}/{byteTo}/{bandWidth}
	private String planUri = "planEvent/%s/%s/%s/%s/%s/%s/%s/%s/%s";

	private StatusHandler() {
	}

	void oo() {
		while (true) {

			boolean activity = false;

			if (activity) {
				logActivities(getActivities());
			} else {
				logAlive();
			}

			try {
				Thread.sleep(REPORT_WINDOW);
			} catch (InterruptedException e) {
				log.error("inetrrupted on report window wait", e);
			}
		}
	}

	private void logActivities(Map<CachoRequest, ProgressReport> activities) {

		for (Map.Entry<CachoRequest, ProgressReport> cachoProgress : activities
				.entrySet()) {
			log(urlFor(activity(cachoProgress)));
		}
	}

	private void log(String urlFor) {
		log.debug("about to notify status logger");
		// TODO make the request
	}

	private Map<CachoRequest, ProgressReport> getActivities() {
		return LastRetrievalPlanLocator.getInstance().getProgress();
	}

	private void logAlive() {
		log(urlFor(status(alive())));
	}
	
	public void logStartUp() {
		log(urlFor(status(up())));
	}
	
	public void logShutDown() {
		log(urlFor(status(down())));
	}

	private String alive() {
		return "ALIVE";
	}

	private String up() {
		return "UP";
	}

	private String down() {
		return "DOWN";
	}

	private String urlFor(String suffix) {
		return "http://" + conf.getStatusLoggerHost() + "/" + suffix;
	}

	private String status(String status) {
		return String.format(statusUri, status, conf.getDaemonHost(),
				conf.getDaemonPort(), conf.getUserId());
	}

	// planEvent/{action}/{ip}/{port}/{planId}/{clientId}/{byteCurrent}/{byteFrom}/{byteTo}/{bandWidth}
	private String activity(
			Map.Entry<CachoRequest, ProgressReport> cachoProgress) {

		CachoRequest request = cachoProgress.getKey();
		ProgressReport progress = cachoProgress.getValue();
		String action = request.getDirection().name();
		String planId = LastRetrievalPlanLocator.getInstance().getPlanId();
		int byteCurrent = progress.getAmountOfReceivedBytes() + request.getCacho().getFirstByteIndex();
		int byteFrom = request.getCacho().getFirstByteIndex();
		int byteTo = request.getCacho().getLastByteIndex();
		double bandWidth = progress.getBandWidth();

		return String.format(this.planUri, action, conf.getDaemonHost(),
				conf.getDaemonPort(), planId, conf.getUserId(), byteCurrent, byteFrom,
				byteTo, bandWidth);
	}

	public static StatusHandler getInstance() {
		return instance;
	}

}
