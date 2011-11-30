package edu.columbia.e6998.cloudexchange.channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import edu.columbia.e6998.cloudexchange.datastore.ConnectedUserManager;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class MessageHandlerServlet extends HttpServlet {

	private void sendUpdates(String msg) {
		/*need to get all users and send messages to them*/
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ConnectedUserManager connectedUsers = new ConnectedUserManager();
		HashMap<String, String> users = connectedUsers.getUsersMap();
		JSONObject message = null;
		
		try {
			message = new JSONObject();
			message.append("msg", msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*right now we are just going by userID, but they may be different 
		 *keys later on..
		 */
		for (String user : users.values()) {
			String channelKey = user;
			channelService.sendMessage(new ChannelMessage(channelKey, message.toString()));
		}
	}
	
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		/*get message - we'll tag it somehow
		 * filter content accordingly and forward to appropriate
		 * action / servlet
		 */
		String msg = req.getParameter("msg");
		
		/*here we handle the type of message and
		 *determine which handler the bid/offer/some other kind message 
		 *is to be passed on to
		 */
	
		
		/*propagate the message to clients*/
		sendUpdates(msg);
		

	}
}
