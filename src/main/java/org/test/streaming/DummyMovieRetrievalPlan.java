package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

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
		int requestSize = totalSize / 5;
		// int requestSize = 1024 * 1024 * 64;
		int amountOfRequests = 0;
		String movieFileName = conf.get("test.video.file.name");
		// String daemonHost =
		// "ec2-54-212-135-180.us-west-2.compute.amazonaws.com";
		String daemonHost = "localhost";
		int daemonPort = 27018;
		while (totalSize - totalRequested >= requestSize) {
			daemonPort = 27017 + (amountOfRequests % 3);
			System.err.println(daemonPort);
			daemonPort = 10002;
			requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, totalRequested, requestSize)));
			totalRequested += requestSize;
			// requestSize = requestSize * 2;
			amountOfRequests++;
		}
		System.err.println(daemonPort);
		daemonPort = 27017 + (amountOfRequests % 3);
		daemonPort = 10002;
		System.out.println(totalSize - totalRequested);
		if (totalRequested < totalSize)
			requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, totalRequested, totalSize - totalRequested)));
		// requests = Lists.newLinkedList();
		// requests.add(new CachoRetrieval(daemonHost, daemonPort, new
		// CachoRequest(null, movieFileName, 0, totalSize)));
		return requests;
	}

	@Override
	public String getVideoId() {
		return videoId;
	}

}
