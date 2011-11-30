package edu.columbia.e6998.cloudexchange.client;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class MainServlet extends HttpServlet {

	/* our destination jsp to render stuff */
	final String destination = "/views/main.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		RequestDispatcher rd;
		UserService userService = UserServiceFactory.getUserService();

		/*verify there is a user*/
		if (userService.getCurrentUser() != null) {

			/* set unique channel id for user */
			ChannelService channelService = ChannelServiceFactory
					.getChannelService();
			String userId = userService.getCurrentUser().getUserId();
			String token = channelService.createChannel(userId);

			
			/* we will want initial list of contracts to populate from here */

			
			
			
			/* pass vars to jsp (see destination address) */
			req.setAttribute("token", token);

			rd = getServletContext().getRequestDispatcher(destination);

			try {
				rd.forward(req, resp);
			} catch (ServletException e) {
				e.printStackTrace();
			}
		} else {
			/*no signed in user, something went wrong, 
			 * user maybe directly tried to access main,
			 * whatever redirect back to index*/
			

			resp.sendRedirect("/");
		}

	}
}
