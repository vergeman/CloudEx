package edu.columbia.e6998.cloudexchange.aws;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.repackaged.com.google.common.util.Base64;

import edu.columbia.e6998.cloudexchange.aws.spotprices.SpotPriceManager;
import edu.columbia.e6998.cloudexchange.toolkit.GenericToolkit;

@SuppressWarnings("serial")
public class LaunchServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(LaunchServlet.class.getName());
	private static final int ETA_TIME = 5*60*1000; // Approximate time to execute task, set to 5 mins
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		List<Entity> entities = 
			GenericToolkit.getInstance().getOpenTransactions();
		
		Calendar currentTime = Calendar.getInstance();
		log.info("[Current time = " + currentTime.getTime().toString() + "]");
		
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY); // get hour in 24-hour format
		int currentDate = currentTime.get(Calendar.DAY_OF_YEAR); // get date
		int currentYear = currentTime.get(Calendar.YEAR);
	
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		
		//log.info("Current time:" + currentHour + " hrs " + currentDate + " " + currentYear);
		
		log.info("Checking for instances to launch..." + entities.size());
		if (entities.size() > 0) { 
			Queue queue = QueueFactory.getDefaultQueue();
			
		try {
			for (Entity e : entities) {
			
				//String tDateString = (String) e.getProperty("date"); // get transaction date
				Date eDate = (Date) e.getProperty("date"); // get transaction date
				String tUserId = (String) e.getProperty("buyer"); // get userId
				String tHourString = (String) e.getProperty("time"); // get transaction hour

				int tHour = Integer.parseInt(tHourString);

				Calendar tDate = Calendar.getInstance();
				tDate.setTime(eDate);

				//Calendar tDate = Calendar.getInstance();
				//tDate.setTime(sdf.parse(tDateString)); // parse the date
			
				// check if execution date is today
				if (currentDate == tDate.get(Calendar.DAY_OF_YEAR) &&
						currentYear == tDate.get(Calendar.YEAR)) {
					
					// Check if hour is the same hour or previous hour in case we missed smth
					
					// TODO: Use Calendar.add(Calendar.HOUR, -1) instead of currentHour-1
					if (tHour == currentHour || tHour == currentHour-1  || true) {
	
						
						// Get input stream for credentials file
						Entity userProfile = 
							GenericToolkit.getInstance().getUserProfileForUser(tUserId);
			
						String blobKeyString = (String) userProfile.getProperty("CredentialsBlobKey");
						BlobKey blobKey = new BlobKey(blobKeyString);
						BlobstoreInputStream iStream = new BlobstoreInputStream(blobKey);
					
						// Get launch configuration
						InstanceConfiguration config = new InstanceConfiguration();
						config.region  = (String) e.getProperty("region");
						config.zone = (String) e.getProperty("zone");
						config.instanceType = (String) e.getProperty("instanceType");
						
						config.ami = (String) userProfile.getProperty("defaultAmi");
						config.securityGroup = (String) userProfile.getProperty("securityGroup");
						config.keyPair = (String) userProfile.getProperty("keyPair");
						
						// Get profile
						String profile = (String) e.getProperty("profile");

						SpotPriceManager sm = new SpotPriceManager(iStream);
						String price = sm.getSpotprice(profile);
					
						// Serialize config
						ByteArrayOutputStream bosConfig = new ByteArrayOutputStream();
						ObjectOutputStream outConfig = new ObjectOutputStream(bosConfig);
						outConfig.writeObject(config);
						outConfig.close();
						
						// Serialize blobKey
						ByteArrayOutputStream bosBlob = new ByteArrayOutputStream();
						ObjectOutputStream outBlob = new ObjectOutputStream(bosBlob);
						outBlob.writeObject(blobKey);
						outBlob.close();
						
						// Get datastore key to update transaction once the instance is launched
						Key transactionKey = e.getKey();
						// Serialize transaction datastore key
						ByteArrayOutputStream bosKey = new ByteArrayOutputStream();
						ObjectOutputStream outKey = new ObjectOutputStream(bosKey);
						outKey.writeObject(transactionKey);
						outKey.close();
						
						// Add to the worker queue
						log.info("Adding to queue...");
						
						queue.add(withUrl("/worker").
								param("config", Base64.encode(bosConfig.toByteArray())).
								param("credentials", Base64.encode(bosBlob.toByteArray())).
								param("key", Base64.encode(bosKey.toByteArray())).
								param("price", price).
								etaMillis(ETA_TIME));
					}
				}
			}
		} catch (Exception e) {
			//log.info(e.getMessage());
		}
		}
	}
}
