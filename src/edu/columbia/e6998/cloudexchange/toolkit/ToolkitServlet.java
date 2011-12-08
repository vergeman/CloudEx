package edu.columbia.e6998.cloudexchange.toolkit;


import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class ToolkitServlet extends HttpServlet {

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		GenericToolkit asd = new GenericToolkit();
		resp.getWriter().write(asd.test());
		
	
	}

	
}
