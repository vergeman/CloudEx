package edu.columbia.e6998.cloudexchange.channel;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import edu.columbia.e6998.cloudexchange.datastore.ConnectedUserManager;
import edu.columbia.e6998.cloudexchange.toolkit.GenericToolkit;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;



/*MESSAGE RECEIVE FORMAT
 * msg: update, reset
 * action: buy | sell | cancel, reset
 * price: x
 * qty: x
 * key: yyyymmdd[0-47]
 *
 *
 *MESSAGE SEND FORMAT
 *action: reload, update(key, payload)
 */
@SuppressWarnings("serial")
public class MessageHandlerServlet extends HttpServlet {

	private void sendUpdates(Msg msg) {
		/*need to get all users and send messages to them*/
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ConnectedUserManager connectedUsers = new ConnectedUserManager();
		HashMap<String, String> users = connectedUsers.getUsersMap();
		
		JSONObject message = null;
		
		System.out.println("Sending " + msg.toString());
		
		try {
			message = new JSONObject();
			message.append("msg", msg.type);
			
			if (msg.type.trim().equals("update")) {

				message.append("action", msg.action);
				message.append("key", msg.key);
				message.append("price", msg.price);
				message.append("qty", msg.qty);
			}

			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		/*right now we are just going by userID, but they may be different 
		 *keys later on..
		 */
		for (String user : users.values()) {
			String channelKey = user;
			try {
				channelService.sendMessage(new ChannelMessage(channelKey,
						message.toString()));
				System.out.println("[Channel] Propogating message to: " + user);
			} catch (ChannelFailureException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		/*get message - we'll tag it somehow
		 * filter content accordingly and forward to appropriate
		 * action / servlet
		 */
		UserService userService = UserServiceFactory.getUserService();
		String userId = userService.getCurrentUser().getUserId();
		
		
		String msg_type = req.getParameter("msg");
		
		/*here we handle the type of message and
		 *determine which handler the bid/offer/some other kind message 
		 *is to be passed on to
		 */
	
		System.out.println("msg recv: " + msg_type);
		
		/*update: action of bid, offer, cancel*/
		if (msg_type.equals("update")) {
			
			Msg msg = new Msg(msg_type, req.getParameter("action"),
							  req.getParameter("price"),
							  req.getParameter("qty"),
							  req.getParameter("key")); 
			
			msg.printString();
			/*need validation checks*/
				//TODO: is it a valid message?
						//a logged in user
						//legitimate data
				//TODO: are they allowed to do this (i.e. not their 100th instance, they have money)
			
			
			/*do appropriate datastore handling for a bid offer cancel action */
			GenericToolkit gt = new GenericToolkit();

			
			gt.createBidOffer(msg.key.substring(0, 16),
							  Double.parseDouble(msg.price),
							  userId, 
							  String.format("%02d", Integer.parseInt(msg.key.substring(16, msg.key.length()))));
								//ugly i know...
			
			/*propagate the message to clients*/
			sendUpdates(msg);
			
		}

	}
	
	
	/* internal class for messages*/
	/* we will probably add the validations to this class
	 * validity will be handled (on the client first)
	 * but also as a property of a recieved msg
	 */
	class Msg {
		String type = null;
		String action = null;
		String price = null;
		String qty = null;
		String key = null;

		Msg(String type, String action, String price, String qty, String key) {
			this.type = type;
			this.action = action;
			this.price = price;
			this.qty = qty;
			this.key = key;
		}
		
		public String toString() {
			return type + " " + action + " " + price + " " + qty + " " + key;
		}

		public void printString() {
			System.out.println(this.toString());
		}
	}
}
