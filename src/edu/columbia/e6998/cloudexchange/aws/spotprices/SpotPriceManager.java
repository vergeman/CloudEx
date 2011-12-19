package edu.columbia.e6998.cloudexchange.aws.spotprices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;


import edu.columbia.e6998.cloudexchange.aws.CredentialsManager;

public class SpotPriceManager {
	private static AmazonEC2 ec2;
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final int DEFER_TIME = 300000;
	
	public SpotPriceManager(CredentialsManager credentials) throws IOException {
		ec2 = new AmazonEC2Client(credentials.getCredentials());
	}
	
	public SpotPriceManager (InputStream inputStream) {
		try {
			ec2 = new AmazonEC2Client(new PropertiesCredentials(inputStream));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	public String getSpotprice(String profile) {
		
		SpotPriceRequest spr = new SpotPriceRequest(profile);
		SpotPrice sp = new SpotPrice();
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, -5);
		
		/* check datastore or d/l to get latest price in 5 min span
		 * for given profile (may be inefficient isn't clear how prices update)
		 */
		
		sp = queryDataStore(spr);
		
		if (sp.getSpotPrice() == null || sp.getTimestamp().before(now.getTime())) 	
			sp = downloadSP(spr);

		
		/*get last price and use the task queue
		 * to set cycle of price updates
		 * set the taskname to hack a "singleton" queue type
		 */

		// Eugene: commented for now as it was causing some weird unstoppable calls to /marketprice
		/**
		try {
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(withUrl("/marketprice").taskName(profile).param("profile", profile).countdownMillis(DEFER_TIME));
		}
		catch(TaskAlreadyExistsException e) {}
		
	    System.out.println("now " + now.getTime().toString());
	    System.out.println(sp.getTimestamp().toString());
	    System.out.println(sp.toString());
	    */
		return sp.getSpotPrice();
	}
	
	
	private SpotPrice queryDataStore(SpotPriceRequest spr) {

		SpotPrice sp = new SpotPrice();
		
		try {
			/* check memcache */

			/* build query */
			//TODO: optimize query
			Query q = new Query("SpotPrice");
			q.addFilter("zone", Query.FilterOperator.EQUAL, spr.getZone());
			q.addFilter("description", Query.FilterOperator.EQUAL, spr.getDescription());
			q.addFilter("instance", Query.FilterOperator.EQUAL, spr.getInstanceType());
			q.addSort("timestamp", SortDirection.DESCENDING);

			PreparedQuery pq = datastore.prepare(q);
		
			for (Entity e : pq.asIterable(FetchOptions.Builder.withLimit(1))) {
				sp.setAvailabilityZone((String) e.getProperty("zone"));
				sp.setProductDescription((String) e.getProperty("description"));
				sp.setInstanceType((String) e.getProperty("instance"));
				sp.setTimestamp((Date) e.getProperty("timestamp"));
				sp.setSpotPrice((String) e.getProperty("price"));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return sp;
	}
	

	
	
	//TODO: get max time once instead of constant overwriting
	
	private SpotPrice downloadSP(SpotPriceRequest spr) {
		SpotPrice latest_sp = null;
		
		try {
			/* to batch our writes */
			List<Entity> entities = new ArrayList<Entity>();

			/* restrict our request to valid region */
			ec2.setEndpoint(spr.getEndPoint());

			/*make request w/ filters*/
			DescribeSpotPriceHistoryRequest req = new DescribeSpotPriceHistoryRequest();
			req.setAvailabilityZone(spr.getZone());
			req.setProductDescriptions(Arrays.asList(spr.getDescription()));
			
			List<SpotPrice> prices = ec2.describeSpotPriceHistory(req).getSpotPriceHistory();

			for (SpotPrice sp : prices) {

				String key = buildKey(sp);

				Entity spotprice = new Entity("SpotPrice", key);

				spotprice.setProperty("timestamp", sp.getTimestamp());
				spotprice.setProperty("zone", sp.getAvailabilityZone());
				spotprice.setProperty("description", sp.getProductDescription());
				spotprice.setProperty("instance", sp.getInstanceType());
				spotprice.setProperty("price", sp.getSpotPrice());

				entities.add(spotprice);
				
				
				/*we have no values, grab from query 
				 *to avoid a round of querying
				 */
				
				if (spr.getZone().equals(sp.getAvailabilityZone()) &&
					spr.getDescription().equals(sp.getProductDescription()) &&
					spr.getInstanceType().equals(sp.getInstanceType()) &&
					(latest_sp == null || latest_sp.getTimestamp().before(sp.getTimestamp()))) {
						latest_sp = sp;
				}
				
			}

			datastore.put(entities);

			ec2.shutdown();
			
			
		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}
		
		return latest_sp;
	}
	
	
	private String buildKey(SpotPrice sp) {
		StringBuilder sb = new StringBuilder();
		sb.append(sp.getTimestamp().toString());
		sb.append("_");
		sb.append(sp.getAvailabilityZone());
		sb.append("_");
		sb.append(sp.getProductDescription());
		sb.append("_");
		sb.append(sp.getInstanceType());
		
		return sb.toString();
	}
	

}
