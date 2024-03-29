package edu.columbia.e6998.cloudexchange.channel;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import edu.columbia.e6998.cloudexchange.datastore.ConnectedUserManager;


@SuppressWarnings("serial")
public class ConnectHandlerServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		
		/* we will want to populate a list of connected clients
		 * so we know who to propagate messages to
		 */		
		ConnectedUserManager connectedUsers = new ConnectedUserManager();
		connectedUsers.addUser(presence.clientId());

		System.out.println("Connected: " + presence.clientId());
	}
}
