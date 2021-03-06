package org.test.streaming;

import java.util.List;

public interface MovieRetrievalPlan {
	
	public List<CachoRetrieval> getRequests();
	
	public String getVideoId();
	
	public String getPlanId();
}
