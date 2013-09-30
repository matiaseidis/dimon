package org.test.streaming.status;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

	private StatusHandler() {
	}

	public StatusHandler init(Conf conf) {
		setConf(conf);
		if (conf.isStatusReportEnabled()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {

						try {
							int currentActivities = LastRetrievalPlanLocator.getInstance().getProgress().size();
//							log.debug("about to log total activities: " + currentActivities);
							if (currentActivities == 0) {
								logAlive();
							} else {
								logActivities(LastRetrievalPlanLocator.getInstance().getProgress());
							}
						} catch (Exception e) {
							e.printStackTrace();
							log.error("ups...", e);
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

	private void logActivities(List<CachoProgress> activities) {

		if (!StatusHandler.conf.isStatusReportEnabled()) {
			return;
		}

		log(urlFor(activity()), progressBody(activities));

		List<CachoProgress> completed = new ArrayList<CachoProgress>();
		for (CachoProgress cachoProgress : activities) {
			if (cachoProgress.getProgressReport().getProgressPct() >= 100) {
				completed.add(cachoProgress);
			}
		}
		if (!completed.isEmpty()) {
			log.debug("about to remove " + completed.size() + " cacho progress already completed");
			for (CachoProgress finished : completed) {
				LastRetrievalPlanLocator.getInstance().getProgress().remove(finished);
			}
		}
	}

	private void log(String url, JSONObject progressBody) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(progressBody.toString(), ContentType.APPLICATION_JSON));
		try {
			client.execute(post);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private JSONObject progressBody(List<CachoProgress> activities) {

		JSONObject body = new JSONObject();
		JSONArray cachos = new JSONArray();
		try {
			for (CachoProgress p : activities) {
				JSONObject cacho = new JSONObject();
				CachoRequest request = p.getCachoRequest();
				ProgressReport progress = p.getProgressReport();
				String action = request.getDirection().name();
				String planId = LastRetrievalPlanLocator.getInstance().getPlanId();
				int byteCurrent = progress.getAmountOfReceivedBytes() + request.getCacho().getFirstByteIndex();
				int byteFrom = request.getCacho().getFirstByteIndex();
				int byteTo = request.getCacho().getLastByteIndex();
				double bandWidth = progress.getBandWidth();
				cacho.put("ip", conf.getDaemonHost());
				cacho.put("port", conf.getDaemonPort());
				cacho.put("action", action);
				cacho.put("planId", planId);
				cacho.put("clientId", conf.getUserId());
				cacho.put("byteCurrent", byteCurrent);
				cacho.put("byteFrom", byteFrom);
				cacho.put("byteTo", byteTo);
				cacho.put("bandWidth", bandWidth);
				cachos.put(cacho);
			}
			body.put("cachos", cachos);
		} catch (JSONException e) {
			log.error("unable to report cacho progress", e);
		}

		return body;
	}

	private String activity() {
		return conf.getStatusLoggerServiceReportActivitySuffix();
	}

	private Content log(String urlFor) {
		log.debug("about to notify status logger: " + urlFor);
		Content content = launch(request(urlFor));
		return content;
	}

	private Content launch(Request request) {
		try {
			return request.execute().returnContent();
		} catch (Throwable e) {
			log.error("unable to perform request to " + request.toString() + " - " + e.getMessage());
		}
		return null;
	}

	private Request request(String url) {
		return Request.Get(url);
	}

	private void logAlive() {
		if (!StatusHandler.conf.isStatusReportEnabled()) {
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
		String url = "http://" + conf.getStatusLoggerHost() + conf.getStatusLoggerServiceUri() + suffix;
		return url;
	}

	private String status(String status) {
		long bandWidth = 123;
		// #statusReport/{event}/{ip}/{port}/{clientId}/{bandWidth}
		// status.logger.service.suffix.statusReport=statusReport/%s/%s/%s/%s/%s
		return String.format(conf.getStatusLoggerServiceReportStateSuffix(), status, conf.getDaemonHost(),
				conf.getDaemonPort(), conf.getUserId(), Long.toString(bandWidth));
	}

//	private String activity(CachoProgress cachoProgress) {
//
//		CachoRequest request = cachoProgress.getCachoRequest();
//		ProgressReport progress = cachoProgress.getProgressReport();
//		String action = request.getDirection().name();
//		String planId = LastRetrievalPlanLocator.getInstance().getPlanId();
//		int byteCurrent = progress.getAmountOfReceivedBytes() + request.getCacho().getFirstByteIndex();
//		int byteFrom = request.getCacho().getFirstByteIndex();
//		int byteTo = request.getCacho().getLastByteIndex();
//		double bandWidth = progress.getBandWidth();
//
//		return String.format(conf.getStatusLoggerServiceReportActivitySuffix(), action, conf.getDaemonHost(),
//				conf.getDaemonPort(), planId, conf.getUserId(), byteCurrent, byteFrom, byteTo,
//				Double.toString(bandWidth));
//	}

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
