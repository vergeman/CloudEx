package edu.columbia.e6998.coudexchange;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TheCloudExchange extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
