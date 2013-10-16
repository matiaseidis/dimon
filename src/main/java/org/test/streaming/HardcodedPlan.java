package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class HardcodedPlan implements MovieRetrievalPlan {

	private final String videoId;
	private final Conf conf;

	public HardcodedPlan(String videoId, Conf conf) {
		super();
		this.videoId = videoId;
		this.conf = conf;
	}

	@Override
	public List<CachoRetrieval> getRequests() {
		List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();

		int requestSize = 1024 * 1024 * 2;
		String movieFileName = conf.get("test.video.file.name");
		String daemonHost = "localhost";
		int daemonPort = 27016;
		System.err.println(daemonPort);
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 4194304, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 16777216, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 29360128, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 41943040, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 54525952, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 67108864, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 79691776, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 92274688, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 104857600, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 117440512, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 130023424, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 142606336, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 155189248, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 167772160, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 180355072, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 192937984, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 205520896, requestSize)));
		requests.add(new CachoRetrieval(daemonHost, daemonPort, new CachoRequest(null, movieFileName, 218103808, requestSize)));
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
