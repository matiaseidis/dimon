package org.test.streaming.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.test.streaming.Dimon;
import org.test.streaming.status.StatusHandler;

public class DimonStarterListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// try {
		// Conf conf = new Conf();
		// Dimon dimon = new Dimon(conf.getDaemonPort());
		// sce.getServletContext().setAttribute("dimon", dimon);
		// dimon.run();
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		
		StatusHandler.getInstance().logStartUp();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Dimon dimon = (Dimon) sce.getServletContext().getAttribute("dimon");
		dimon.stop();
		
		StatusHandler.getInstance().logShutDown();
	}

}
