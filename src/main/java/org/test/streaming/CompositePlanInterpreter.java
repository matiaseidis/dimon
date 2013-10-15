package org.test.streaming;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompositePlanInterpreter {
	protected static final Log log = LogFactory.getLog(CompositePlanInterpreter.class);
	private File tempDir;
	private File shareDir;

	public CompositePlanInterpreter(File shareDir, File tempDir) {
		this.setShareDir(shareDir);
		this.setTempDir(tempDir);
	}

	public void interpret(CompositeMovieRetrievalPlan plan, OutputStream out, StreamingProgressObserver progressObserver) {
		List<MovieRetrievalPlan> plans = plan.getPlans();
		CompositeProgressLogger progressLogger = new CompositeProgressLogger(plans);
		for (MovieRetrievalPlan movieRetrievalPlan : plans) {
			DefaultMovieRetrievalPlanInterpreter defaultMovieRetrievalPlanInterpreter = new DefaultMovieRetrievalPlanInterpreter(this.getShareDir(), this.getTempDir());
			defaultMovieRetrievalPlanInterpreter.interpret(movieRetrievalPlan, out, progressLogger);
		}
	}

	public File getShareDir() {
		return shareDir;
	}

	public void setShareDir(File shareDir) {
		this.shareDir = shareDir;
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}


}
