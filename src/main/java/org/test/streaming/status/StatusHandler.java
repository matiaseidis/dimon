package org.test.streaming.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.test.streaming.CachoRequest;
import org.test.streaming.CachoServerHandler;
import org.test.streaming.Conf;
import org.test.streaming.Dimon;
import org.test.streaming.LastRetrievalPlanLocator;
import org.test.streaming.ProgressReport;

public class StatusHandler {

	protected static final Log log = LogFactory.getLog(Dimon.class);
	private static StatusHandler instance = new StatusHandler();
	private long REPORT_WINDOW = 5000;
	private static Conf conf = null;
	private CachoServerHandler cachoServerHandler;

	private StatusHandler() {}

	public StatusHandler init(Conf conf) {
		setConf(conf);
		// final CachoServerHandler handler = this.getCachoServerHandler();
		if (conf.isStatusReportEnabled()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {

						int currentActivities = LastRetrievalPlanLocator
								.getInstance().getProgress().size();

						log.debug("about to total activities: "
								+ currentActivities);
						boolean iddle = currentActivities == 0;

						if (iddle) {
							logAlive();
						} else {
							logActivities(LastRetrievalPlanLocator
									.getInstance().getProgress());
						}

						try {
							Thread.sleep(REPORT_WINDOW);
						} catch (InterruptedException e) {
							log.error("inetrrupted on report window wait", e);
						}
					}
				}
			}).start();
		} else {
			log.info("status report is disabled");
		}
		return instance;
	}

	private void setConf(Conf conf) {
		StatusHandler.conf = conf;
	}

	private void logActivities(Map<CachoRequest, ProgressReport> activities) {
		
		if(!StatusHandler.conf.isStatusReportEnabled()){
			return;
		}

		Map<CachoRequest, ProgressReport> completed = new HashMap<CachoRequest, ProgressReport>();
		for (Map.Entry<CachoRequest, ProgressReport> cachoProgress : activities
				.entrySet()) {

			log.debug("about to log progress perc. "
					+ cachoProgress.getValue().getProgressPct());
			if (cachoProgress.getValue().getProgressPct() < 100) {
				log(urlFor(activity(cachoProgress)));
			} else {
				completed.put(cachoProgress.getKey(), cachoProgress.getValue());
			}
		}
		if (!completed.isEmpty()) {
			log.debug("about to remove " + completed.size()
					+ " cachos already completed");
			for (CachoRequest cachoCompleted : completed.keySet()) {
				LastRetrievalPlanLocator.getInstance().getProgress()
						.remove(cachoCompleted);
			}
		}
	}

	private Content log(String urlFor) {
		log.debug("about to notify status logger: " + urlFor);
		Content content = launch(request(urlFor));
		return content;
	}

	private Content launch(Request request) {
		try {
			return request.execute().returnContent();
		} catch (IOException e) {
			log.error("unable to perform request to " + request.toString()
					+ " - " + e.getMessage());
		}
		return null;
	}

	private Request request(String url) {
		return Request.Get(url);
	}

	private void logAlive() {
		if(!StatusHandler.conf.isStatusReportEnabled()){
			return;
		}
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
		String toEncode = "http://" + conf.getStatusLoggerHost() + conf.getStatusLoggerServiceUri() + suffix;
		try {
			return URLEncoder.encode(toEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("unable to encode url: "+toEncode, e);
			return null;
		}
	}

	private String status(String status) {
		long bandWidth = 123;
		//#statusReport/{event}/{ip}/{port}/{clientId}/{bandWidth}
		//status.logger.service.suffix.statusReport=statusReport/%s/%s/%s/%s/%s
		return String.format(
				conf.getStatusLoggerServiceReportStateSuffix(), 
				status, 
				conf.getDaemonHost(),
				conf.getDaemonPort(), 
				conf.getUserId(), 
				bandWidth);
	}

	private String activity(
			Map.Entry<CachoRequest, ProgressReport> cachoProgress) {

		CachoRequest request = cachoProgress.getKey();
		ProgressReport progress = cachoProgress.getValue();
		String action = request.getDirection().name();
		String planId = LastRetrievalPlanLocator.getInstance().getPlanId();
		int byteCurrent = progress.getAmountOfReceivedBytes()
				+ request.getCacho().getFirstByteIndex();
		int byteFrom = request.getCacho().getFirstByteIndex();
		int byteTo = request.getCacho().getLastByteIndex();
		double bandWidth = progress.getBandWidth();

		return String.format(conf.getStatusLoggerServiceReportActivitySuffix(), action, conf.getDaemonHost(),
				conf.getDaemonPort(), planId, conf.getUserId(), byteCurrent,
				byteFrom, byteTo, bandWidth);
	}

	public static StatusHandler getInstance() {
		return instance;
	}

	public void setCachoServerHandler(CachoServerHandler cachoServerHandler) {
		this.cachoServerHandler = cachoServerHandler;

	}

	public CachoServerHandler getCachoServerHandler() {
		return cachoServerHandler;
	}

}
