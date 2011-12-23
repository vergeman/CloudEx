package edu.columbia.e6998.cloudexchange.toolkit;


import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class ToolkitServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		Date date = new Date();
		String time = (String) req.getParameter("t");
		 
		GenericToolkit asd = GenericToolkit.getInstance();
		resp.getWriter().write(asd.createTestTransaction(date, time));
		
	
	}

	
}
