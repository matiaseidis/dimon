package org.test.streaming.status;

import org.test.streaming.CachoRequest;
import org.test.streaming.ProgressReport;

public class CachoProgress {
	
	private CachoRequest cachoRequest;
	private ProgressReport progressReport;
	
	public CachoProgress(){
		
	}
	
	public CachoProgress(CachoRequest cachoRequest,
			ProgressReport progressReport) {
		super();
		this.cachoRequest = cachoRequest;
		this.progressReport = progressReport;
	}

	public CachoRequest getCachoRequest() {
		return cachoRequest;
	}

	public void setCachoRequest(CachoRequest cachoRequest) {
		this.cachoRequest = cachoRequest;
	}

	public ProgressReport getProgressReport() {
		return progressReport;
	}

	public void setProgressReport(ProgressReport progressReport) {
		this.progressReport = progressReport;
	}

}
