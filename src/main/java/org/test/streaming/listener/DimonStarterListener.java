package org.test.streaming.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.Dimon;
import org.test.streaming.status.StatusHandler;

public class DimonStarterListener implements ServletContextListener {

	protected static final Log log = LogFactory.getLog(Dimon.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			Conf conf = new Conf();
			Dimon dimon = new Dimon(conf.getDaemonHost(), conf.getDaemonPort());
			sce.getServletContext().setAttribute("conf", conf);
			sce.getServletContext().setAttribute("dimon", dimon);
			dimon.run();
			StatusHandler.getInstance().init(conf).logStartUp();
		} catch (Exception e) {
			log.error("unable to start Dimon", e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Dimon dimon = (Dimon) sce.getServletContext().getAttribute("dimon");
		dimon.stop();

		StatusHandler.getInstance().logShutDown();
	}

}
