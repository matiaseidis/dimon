package org.test.streaming;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

public class Progress extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Conf conf = (Conf)this.getServletContext().getAttribute("conf");
		String url = "http://"+conf.getStatusLoggerHost()+conf.getStatusLoggerServiceUri();
		Response progressResponse = Request.Get(url).execute();
		resp.setContentType("application/json");
		String jsonProgress = new String(progressResponse.returnContent().asBytes());
		PrintWriter out = resp.getWriter();
		out.print(jsonProgress);
		out.flush();
	}
}
