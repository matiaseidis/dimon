package org.test.streaming;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CompositeProgressLogger extends ProgressLogger {

	List<Map<CachoRequest, ProgressReport>> reports = Lists.newLinkedList();
	int current = 0;

	public CompositeProgressLogger(List<MovieRetrievalPlan> plans) {
		for (MovieRetrievalPlan movieRetrievalPlan : plans) {
			List<CachoRetrieval> requests = movieRetrievalPlan.getRequests();
			Map<CachoRequest, ProgressReport> report = Maps.newHashMap();
			for (CachoRetrieval cachoRetrieval : requests) {
				report.put(cachoRetrieval.getRequest(), new ProgressReport(cachoRetrieval.getRequest()));
			}
			this.reports.add(report);
		}
	}

	@Override
	public synchronized void print(Map<CachoRequest, ProgressReport> progress) {
		if (current != 0)
			System.out.println(current + " done.");
		super.print(progress);
		int todo = this.reports.size() - current - 1;
		if (todo > 0) {
			System.out.println(todo + " not started yet.");
			System.out.println("-------------------------------------------");
			System.out.println("-------------------------------------------");
		}

	}

	@Override
	public synchronized void done(Map<CachoRequest, ProgressReport> progress) {
		this.current++;
		super.done(progress);
	}

}
