package org.test.streaming;

import java.util.List;

import org.test.streaming.status.StatusHandler;

import com.google.common.collect.Lists;

public class CompositeMovieRetrievalPlan {

	private final String videoId;
	private List<MovieRetrievalPlan> plans = Lists.newLinkedList();

	public CompositeMovieRetrievalPlan(MovieRetrievalPlan plan, int maxParalReqs) {
		this.videoId = plan.getVideoId();
		List<CachoRetrieval> requests = plan.getRequests();

		int d = requests.size() / maxParalReqs;
		int r = requests.size() % maxParalReqs;
		for (int j = 0; j < d; j++) {
			WatchMovieRetrievalPlan currentPlan = new WatchMovieRetrievalPlan(videoId);
			plans.add(currentPlan);
			for (int i = 0; i < maxParalReqs; i++) {
				currentPlan.getRequests().add(requests.get(j * maxParalReqs + i));
			}
		}

		if (r > 0) {
			WatchMovieRetrievalPlan currentPlan = new WatchMovieRetrievalPlan(videoId);
			plans.add(currentPlan);
			for (int i = 0; i < r; i++) {
				currentPlan.getRequests().add(requests.get(d * maxParalReqs + i));
			}
		}
		
		for(MovieRetrievalPlan p : this.getPlans()) {
			for(CachoRetrieval cr : p.getRequests()) {
				LastRetrievalPlanLocator.getInstance().registerCacho(cr.getRequest());
			}
		}
	}

	public String getVideoId() {
		return videoId;
	}

	public List<MovieRetrievalPlan> getPlans() {
		return plans;
	}

	public void setPlans(List<MovieRetrievalPlan> plans) {
		this.plans = plans;
	}

}
