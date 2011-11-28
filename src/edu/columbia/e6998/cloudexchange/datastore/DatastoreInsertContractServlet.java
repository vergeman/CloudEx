package edu.columbia.e6998.cloudexchange.datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class DatastoreInsertContractServlet extends HttpServlet {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		//TODO remove testing code
		Random rand = new Random(); 

		String[] user = {"batman", "robin", "bart", "lisa"};
		Boolean[] cType = {true, false};
		int AMI, region, type;
		
		
		Entity contract = new Entity("Contract");
		
		 AMI = rand.nextInt(4);
		 region = rand.nextInt(4);
		 type = rand.nextInt(4);
		 
		 //TODO hash functions here
		 String profile = AMI + "_" + region + "_" + type;
		 
		contract.setProperty("profile", profile);
		contract.setProperty("date", (rand.nextInt(12) + 1) + "/" + (rand.nextInt(28) + 1) + "/2011 " + rand.nextInt(24) + ":"+ "00");
		contract.setProperty("qty", rand.nextInt(5) + 1);
		contract.setProperty("price", rand.nextFloat() + 0.001);
		contract.setProperty("seller", cType[rand.nextInt(2)]);
		contract.setProperty("user", user[rand.nextInt(4)]);
		contract.setProperty("active", true);
		
//		Entity contract = new Entity("Contract");
//		
//		 //TODO hash functions here
//		 String profile = 	req.getParameter("profile")	+ "_" 
//				 			+ req.getParameter("region") + "_"
//				 			+ req.getParameter("type");
//		 
//		contract.setProperty("profile", profile);
//		contract.setProperty("date", req.getParameter("date"));
//		contract.setProperty("qty", req.getParameter("qty"));
//		contract.setProperty("price", req.getParameter("price"));
//		contract.setProperty("seller", req.getParameter("seller"));
//		contract.setProperty("user", req.getParameter("user"));
//		contract.setProperty("active", true);
		
		datastore.put(contract);
		List<Entity> result = new ArrayList<Entity>();
		
		//TODO Update MemCache
		List<Entity> mem = (List<Entity>) syncCache.get("Contract");
		//where MemcahceRecord.profile = newInsert.profile  
		for(Entity m : mem){
			if(((String) m.getProperty("profile")).equals(profile)){
				//MemcacheRecord price > newInsert.price
				Double s = (Double) m.getProperty("price");
				Double n = (Double) contract.getProperty("price");
				if(n<s){
					result.add(contract);
					resp.getWriter().println("Memcache:" + m.getProperty("price") + " New Record:" + contract.getProperty("price"));
				}
			}
			result.add(m);
			
		}
		syncCache.put("Contract", result);
		
	}
}
