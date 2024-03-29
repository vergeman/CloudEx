package edu.columbia.e6998.cloudexchange.aws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CancelSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.Key;

import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.appengine.repackaged.com.google.common.util.Base64DecoderException;

import edu.columbia.e6998.cloudexchange.client.UserProfile;
import edu.columbia.e6998.cloudexchange.mailer.MailManager;
import edu.columbia.e6998.cloudexchange.toolkit.GenericToolkit;

import java.util.logging.Logger;

@SuppressWarnings("serial")
public class SpotInstanceLauncher extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(SpotInstanceLauncher.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		
		log.info("Queue PUSH request received");
		AWSCredentials credentials = null;
	    InstanceConfiguration config = null;
	    Key key = null;
	    String price = null;
		try {
			byte[] configBytes = Base64.decode((String)req.getParameter("config"));
			byte[] blobKeyBytes = Base64.decode((String)req.getParameter("credentials"));
			byte[] transactionKeyBytes = Base64.decode((String)req.getParameter("key"));
			price = (String) req.getParameter("price");
			
			ObjectInputStream inConfig = new ObjectInputStream(new ByteArrayInputStream(configBytes));
			ObjectInputStream inBlob = new ObjectInputStream(new ByteArrayInputStream(blobKeyBytes));
			ObjectInputStream inTransactionKey = new ObjectInputStream(new ByteArrayInputStream(transactionKeyBytes));
			
			config = (InstanceConfiguration) inConfig.readObject();
			inConfig.close();
			
			BlobKey blobKey = (BlobKey) inBlob.readObject();
			inBlob.close();
			
			key = (Key) inTransactionKey.readObject();
			inTransactionKey.close();
			
			BlobstoreInputStream credentialsInputStream = new BlobstoreInputStream(blobKey);
			
			// Trying buyer's credentials file from blobstore
			credentials = new PropertiesCredentials(credentialsInputStream);
			
		} catch (IOException e1) {
			log.severe("Credentials were not properly entered into AwsCredentials.properties.");
			log.severe(e1.getMessage());
		} catch (Base64DecoderException ne) {
			log.severe(ne.getMessage());
		} catch (ClassNotFoundException ne) {
			log.severe(ne.getMessage());
		}
		
		try {

		// Create the AmazonEC2Client object so we can call various APIs.
		AmazonEC2 ec2 = new AmazonEC2Client(credentials);

		// Initializes a Spot Instance Request
    	RequestSpotInstancesRequest requestRequest = new RequestSpotInstancesRequest();
   
    	// TODO: Get spot prices here to make an educated bid	 
    	// Increase current spot price by $1 to make sure we get an instance
    	Double currentSpotPrice = Double.parseDouble(price);
    	Double bidPrice = currentSpotPrice + 1;
    	requestRequest.setSpotPrice(bidPrice.toString());
    	requestRequest.setInstanceCount(Integer.valueOf(1));
    	
    	// Setup the specifications of the launch. This includes the instance type (e.g. t1.micro)
    	// and the latest Amazon Linux AMI id available. Note, you should always use the latest 
    	// Amazon Linux AMI id or another of your choosing.
    	LaunchSpecification launchSpecification = new LaunchSpecification();
    	
    	Placement placement = new Placement();
    	placement.setAvailabilityZone(config.zone);
    	
    	launchSpecification.setPlacement(placement);
    	launchSpecification.setKeyName(config.keyPair);
    	launchSpecification.setInstanceType(config.instanceType);
    	
    	if(config.keyPair.isEmpty()) {
    		try {
    			Calendar now = Calendar.getInstance();
				CreateKeyPairRequest kpr = new CreateKeyPairRequest(String.valueOf(now.getTime()));
				ec2.createKeyPair(kpr);
				launchSpecification.setKeyName(String.valueOf(now.getTime()));
    		}
    		catch (Exception e) {}
    	}
    	
    	if(config.ami.isEmpty())
    		config.ami = "ami-8c1fece5";
    	
    	launchSpecification.setImageId(config.ami);

    	
    	// Add the security group to the request.
    	ArrayList<String> securityGroups = new ArrayList<String>();
    	if (config.securityGroup.isEmpty())
        	config.securityGroup = "default";
    	
    	securityGroups.add(config.securityGroup);
    	launchSpecification.setSecurityGroups(securityGroups); 

    	// Add the launch specifications to the request.
    	requestRequest.setLaunchSpecification(launchSpecification);
    	
    	//============================================================================================//
    	//=========================== Getting the Request ID from the Request ========================// 
    	//============================================================================================//

    	// Call the RequestSpotInstance API. 
    	RequestSpotInstancesResult requestResult = ec2.requestSpotInstances(requestRequest);        	
    	List<SpotInstanceRequest> requestResponses = requestResult.getSpotInstanceRequests();
    	
    	// Setup an arraylist to collect all of the request ids we want to watch hit the running
    	// state.
    	ArrayList<String> spotInstanceRequestIds = new ArrayList<String>();
    	
    	// Add all of the request ids to the hashset, so we can determine when they hit the 
    	// active state.
    	for (SpotInstanceRequest requestResponse : requestResponses) {
    		log.info("Created Spot Request: "+requestResponse.getSpotInstanceRequestId());
    		spotInstanceRequestIds.add(requestResponse.getSpotInstanceRequestId());
    	}

    	//============================================================================================//
    	//=========================== Determining the State of the Spot Request ======================// 
    	//============================================================================================//
    	
        // Create a variable that will track whether there are any requests still in the open state.
	    boolean anyOpen;
	
	    // Initialize variables.
	    ArrayList<String> instanceIds = new ArrayList<String>();

	    do {
	        // Create the describeRequest with tall of the request id to monitor (e.g. that we started).
	        DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();    	
	        describeRequest.setSpotInstanceRequestIds(spotInstanceRequestIds);
	    	
	        // Initialize the anyOpen variable to false � which assumes there are no requests open unless
	        // we find one that is still open.
	        anyOpen = false;
	
	    	try {
	    		// Retrieve all of the requests we want to monitor. 
	    		DescribeSpotInstanceRequestsResult describeResult = ec2.describeSpotInstanceRequests(describeRequest);
	    		List<SpotInstanceRequest> describeResponses = describeResult.getSpotInstanceRequests();
	
	            // Look through each request and determine if they are all in the active state.
	            for (SpotInstanceRequest describeResponse : describeResponses) {      		
	            		// If the state is open, it hasn't changed since we attempted to request it.
	            		// There is the potential for it to transition almost immediately to closed or
	            		// cancelled so we compare against open instead of active.
	            		if (describeResponse.getState().equals("open")) {
	            			anyOpen = true;
	            			break;
	            		}
	            		
	            		// Update transaction with new instanceID and price
	            		
	            		//String priceExecuted = describeResponse.getSpotPrice();
	            		
	            		GenericToolkit.getInstance().updateTransaction(key, "instanceID",  describeResponse.getInstanceId());
	            		GenericToolkit.getInstance().updateTransaction(key, "priceExecuted", currentSpotPrice.toString());
	            	
	            		// Add the instance id to the list we will eventually terminate.
	            		instanceIds.add(describeResponse.getInstanceId());
	            		
	            		//TODO: email buyer & seller
	            		// profiles[0] - buyer
	            		// profiles[1] - seller
	            		UserProfile[] profiles = GenericToolkit.getInstance().getBuyerSellerProfileForTransaction(key);
	            		
	            		// TODO: create generic email for TheCloudExchange
	            		MailManager mm = new MailManager("fedotoveugene@gmail.com", "TheCloudExchange",
	            				profiles[0].email, "Customer", "Spot instance delivered");
	            		String message = "Dear TheCloudExchange Customer,\n\nYour spot instance has been created:\n" +
	            				"Instance ID = " + instanceIds + "\nPrice Executed = " + currentSpotPrice.toString() +"\n\n" +
	            						"Yours truly,\nTheCloudExchange";
	            		mm.setMsgBody(message);
	            		mm.Send();
	            		
	            		mm = new MailManager("fedotoveugene@gmail.com", "TheCloudExchange",
	            				profiles[1].email, "Customer", "Spot instance delivered");
	            		message = "Dear TheCloudExchange Customer,\n\nA spot instance for your short contract has been delivered:\n" +
	            				"\nPrice Executed = " + currentSpotPrice.toString() +"\n\n" +
	            						"Yours truly,\nTheCloudExchange";
	            		mm.setMsgBody(message);
	            		mm.Send();
	            		
	            		// Charge the seller
	            		GenericToolkit.getInstance().createCharge(key, 
	            				describeResponse.getInstanceId(), profiles[1].id, currentSpotPrice.toString(), "delivery");
	            }
	    	} catch (AmazonServiceException e) {
	            // If we have an exception, ensure we don't break out of the loop.
	    		// This prevents the scenario where there was blip on the wire.
	    		anyOpen = true;
	        }
	
	    	try {
		    	// Sleep for 30 seconds.
		    	Thread.sleep(30*1000);
	    	} catch (Exception e) {
	    		// Do nothing because it woke up early.
	    	}
	    } while (anyOpen);
	   
        // Cancel requests.
        CancelSpotInstanceRequestsRequest cancelRequest = new CancelSpotInstanceRequestsRequest(spotInstanceRequestIds);
        ec2.cancelSpotInstanceRequests(cancelRequest);
		
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
	}	
}
