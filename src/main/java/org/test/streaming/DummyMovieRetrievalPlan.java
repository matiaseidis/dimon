package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class DummyMovieRetrievalPlan implements MovieRetrievalPlan {

	private final String videoId;
	private final Conf conf;

	public DummyMovieRetrievalPlan(String videoId, Conf conf) {
		super();
		this.videoId = videoId;
		this.conf = conf;
	}

	@Override
	public List<CachoRetrieval> getRequests() {
		List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();

		int totalSize = Integer.parseInt(conf.get("test.video.file.size"));
		int totalRequested = 0;
		int requestSize = 1024 * 1024 * 2;
		int amountOfRequests = 0;
		String movieFileName = conf.get("test.video.file.name");
		// String daemonHost =
		// "ec2-54-212-135-180.us-west-2.compute.amazonaws.com";
		String daemonHost = "localhost";
		int daemonPort = 27014;
		while (totalSize - totalRequested >= requestSize) {
			daemonPort = 27014 + (amountOfRequests % 6);
			daemonPort = 10002;
			System.err.println(daemonPort);
			requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, totalRequested, requestSize)));
			totalRequested += requestSize;
			// requestSize = requestSize * 2;
			amountOfRequests++;
		}
		daemonPort = 27014 + (amountOfRequests % 6);
		daemonPort = 10002;
		System.err.println(daemonPort);
		System.out.println(totalSize - totalRequested);
		if (totalRequested < totalSize) {
			CachoRetrieval last = requests.get(requests.size() - 1);
			last.getRequest().getCacho().setLength(last.getRequest().getCacho().getLength() + (totalSize - totalRequested));
		}
		// requests = Lists.newLinkedList();
		// requests.add(new CachoRetrieval(daemonHost, daemonPort, new
		// CachoRequest(null, movieFileName, 0, totalSize)));
		return requests;
	}

	@Override
	public String getVideoId() {
		return videoId;
	}

	@Override
	public String getPlanId() {
		return "dummyPlanId";
	}

}
