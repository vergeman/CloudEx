package edu.columbia.e6998.cloudexchange.toolkit;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;


@SuppressWarnings("serial")
public class MyOrdersServlet extends HttpServlet {
	GenericToolkit gt = GenericToolkit.getInstance();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String rs = "";
		List<Entity> myorders = null;
		ArrayList<Entity> bids = new ArrayList<Entity>();
		ArrayList<Entity> offers = new ArrayList<Entity>();
		
		UserService userService = UserServiceFactory.getUserService();
		String user = userService.getCurrentUser().getUserId();

		if(user==null){
			//System.out.println("\nUser not found.");
		}else{
			//System.out.println("\nUser:"  + user);
			myorders = gt.getMyOrders(user);
		}

		for (Entity e : myorders) {
			 //System.out.println(e.toString());
			if ((Boolean) e.getProperty("seller")) {
				offers.add(e);
			} else
				bids.add(e);
		}

		//System.out.println("\nProcessing Bid\\Offers.");
		JSONArray current_offers = new JSONArray();
		JSONArray current_bids = new JSONArray();
		JSONObject out = new JSONObject();
		
		try {
			for (Entity e : offers) {
				JSONObject obj = new JSONObject();
				obj.put("key", e.getKey().toString());
				obj.put("profile", profileToDetail((String) e.getProperty("profile")));
				obj.put("date", profileToDate((String) e.getProperty("profile")));
				obj.put("price", e.getProperty("price"));
				obj.put("hour", e.getProperty("hour"));
				
				current_offers.put(obj);
			} 
			
			
			for (Entity e : bids) {
				JSONObject obj = new JSONObject();
				obj.put("key", e.getKey().toString());
				obj.put("profile", profileToDetail((String) e.getProperty("profile")));
				obj.put("date", profileToDate((String) e.getProperty("profile")));
				obj.put("price", e.getProperty("price"));
				obj.put("hour", e.getProperty("hour"));
				
				current_bids.put(obj);
			}

			out.put("current_bids", current_bids);
			out.put("current_offers", current_offers);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		resp.setContentType("application/json");
		resp.getWriter().println(out.toString());
		/*
		if(offers.isEmpty()){
			rs = "You do not have any active offers.";
		}else{
			rs	+=	"Current Offers\n";
			for(Entity e:offers){
				rs	+=	"Profile:" + e.getProperty("profile") +
						"\tprice:" + String.valueOf((Double) e.getProperty("price"));
			}
		}
		*/
		/*
		if(bids.isEmpty()){
			rs += "\nYou do not have any active bids.";
		}else{
			rs	+=	"Current bids\n";
			for(Entity e:bids){
				rs	+=	profileToDetail((String) e.getProperty("profile"))+
						" hour:" + e.getProperty("hour") +
						" price:" + String.valueOf((Double) e.getProperty("price")) +
						"\n";
			}
		}
		*/
		
	//resp.getWriter().write(rs);
	}
	
	public String profileToDetail(String profile){
		String detail = "";
		String[] lookup = gt.reverseLookUpProfile(profile);
		
		detail = 
				"[" + lookup[gt.REGION] + " " + "]" + 
				"[" + lookup[gt.OS] + " " + "]" +
				"[" + lookup[gt.INSTANCE_TYPE] + "]";
		
		return detail;
	}

	public String profileToDate(String profile){
		SimpleDateFormat dateMMddYYYY= new SimpleDateFormat("MM/dd/yyyy");
		String[] lookup = gt.reverseLookUpProfile(profile);
		StringBuilder sDate = new StringBuilder(dateMMddYYYY.format(gt.dateConvert(lookup[gt.DATE])));
		return sDate.toString();
	}
}
