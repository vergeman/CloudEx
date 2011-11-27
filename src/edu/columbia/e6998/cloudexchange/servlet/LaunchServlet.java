package edu.columbia.e6998.cloudexchange.servlet;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LaunchServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		
		resp.getWriter().println("Hello, the time is:" + 
				Calendar.getInstance().getTimeInMillis());
	}
}
