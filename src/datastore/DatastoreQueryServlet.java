package datastore;

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
public class DatastoreQueryServlet extends HttpServlet {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		resp.setContentType("text/plain");
		List<Entity> result = new ArrayList<Entity>();
		
		Hashtable<String, Entity> hTable = new Hashtable<String, Entity>();
		
		//createData();

		Query q = new Query("Contract");
		q.addFilter("active", Query.FilterOperator.EQUAL, true);
		q.addSort("price", Query.SortDirection.ASCENDING);
		List<Entity> rows = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		
		for(Entity e: rows){
			if(hTable.containsKey(e.getProperty("profile"))){
				if( (((Double)e.getProperty("price")) < (Double) hTable.get(e.getProperty("profile")).getProperty("price"))){
					result.remove(hTable.put(((String) e.getProperty("profile")), e));
					result.add(e);
					hTable.put(((String) e.getProperty("profile")), e);
				}
			}
			else{
				hTable.put(((String) e.getProperty("profile")), e);
				result.add(e);
			}
		}
		
		
		syncCache.put("Contract", result);
		
		//TODO testing
		int count = 0;
		List<Entity> tmp = (List<Entity>) syncCache.get("Contract");
		for(Object t : tmp){
			resp.getWriter().println("Count:" + count++ + " Profile:" + (String) ((Entity) t).getProperty("profile"));
		}
		
	}
	
	
	public void createData(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Random rand = new Random(); 

		String[] user = {"batman", "robin", "bart", "lisa"};
		Boolean[] cType = {true, false};
		int AMI, region, type;
		
		for(int i = 0; i < 10 ; i++){
		
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
			
			datastore.put(contract);
		}
			
					
	}

}
