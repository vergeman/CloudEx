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


@SuppressWarnings("serial")
public class MyOrdersServlet extends HttpServlet {
	GenericToolkit gt = GenericToolkit.getInstance();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
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
		for(Entity e: myorders){
			//System.out.println(e.toString());
			if((Boolean) e.getProperty("seller")){
				offers.add(e);
			}else
				bids.add(e);
		}

		//System.out.println("\nProcessing Bid\\Offers.");
		
		if(offers.isEmpty()){
			rs = "You do not have any active offers.";
		}else{
			rs	+=	"Current Offers\n";
			for(Entity e:offers){
				rs	+=	"Profile:" + e.getProperty("profile") +
						"\tprice:" + String.valueOf((Double) e.getProperty("price"));
			}
		}
		
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
		
	resp.getWriter().write(rs);
	}
	
	public String profileToDetail(String profile){
		String detail = "";
		SimpleDateFormat dateMMddYYYY= new SimpleDateFormat("MM/dd/yyyy");
		String[] lookup = gt.reverseLookUpProfile(profile);
		StringBuilder sDate = new StringBuilder(dateMMddYYYY.format(gt.dateConvert(lookup[gt.DATE])));
		
		detail = sDate + "::" + 
				"[" + lookup[gt.REGION] + " " + "]" + 
				"[" + lookup[gt.OS] + " " + "]" +
				"[" + lookup[gt.INSTANCE_TYPE] + "]";
		return detail;
	}

	
}
