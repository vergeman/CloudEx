package edu.columbia.e6998.cloudexchange.aws;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public  class testInstance {
	static AmazonEC2      ec2;
	
	public testInstance(HttpServletResponse resp) throws IOException {


   	 AWSCredentials credentials = new PropertiesCredentials(
   			testInstance.class.getResourceAsStream("AwsCredentials.properties"));

        /*********************************************
         * 
         *  #1 Create Amazon Client object
         *  
         *********************************************/
   	resp.getWriter().println("#1 Create Amazon Client object");
        ec2 = new AmazonEC2Client(credentials);

        
      
       try {
       	
       	/*********************************************
       	 * 
            *  #2 Describe Availability Zones.
            *  
            *********************************************/
       	resp.getWriter().println("#2 Describe Availability Zones.");
           DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
           resp.getWriter().println("You have access to " + availabilityZonesResult.getAvailabilityZones().size() +
                   " Availability Zones.");

           /*********************************************
            * 
            *  #3 Describe Available Images
            *  
            *********************************************/
           resp.getWriter().println("#3 Describe Available Images");
           DescribeImagesResult dir = ec2.describeImages();
           List<Image> images = dir.getImages();
           resp.getWriter().println("You have " + images.size() + " Amazon images");
           
           
           /*********************************************
            *                 
            *  #4 Describe Key Pair
            *                 
            *********************************************/
           resp.getWriter().println("#9 Describe Key Pair");
           DescribeKeyPairsResult dkr = ec2.describeKeyPairs();
           resp.getWriter().println(dkr.toString());
           
           /*********************************************
            * 
            *  #5 Describe Current Instances
            *  
            *********************************************/
           resp.getWriter().println("#4 Describe Current Instances");
           DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
           List<Reservation> reservations = describeInstancesRequest.getReservations();
           Set<Instance> instances = new HashSet<Instance>();
           // add all instances to a Set.
           for (Reservation reservation : reservations) {
           	instances.addAll(reservation.getInstances());
           }
           
           resp.getWriter().println("You have " + instances.size() + " Amazon EC2 instance(s).");
           for (Instance ins : instances){
           	
           	// instance id
           	String instanceId = ins.getInstanceId();
           	
           	// instance state
           	InstanceState is = ins.getState();
           	resp.getWriter().println(instanceId+" "+is.getName());
           }
           
           /*********************************************
            * 
            *  #6 Create an Instance
            *  
            *********************************************/
           resp.getWriter().println("#5 Create an Instance");
           String imageId = "ami-76f0061f"; //Basic 32-bit Amazon Linux AMI
           int minInstanceCount = 1; // create 1 instance
           int maxInstanceCount = 1;
           RunInstancesRequest rir = new RunInstancesRequest(imageId, minInstanceCount, maxInstanceCount);
           RunInstancesResult result = ec2.runInstances(rir);
           
           //get instanceId from the result
           List<Instance> resultInstance = result.getReservation().getInstances();
           String createdInstanceId = null;
           for (Instance ins : resultInstance){
           	createdInstanceId = ins.getInstanceId();
           	resp.getWriter().println("New instance has been created: "+ins.getInstanceId());
           }
           
           
           /*********************************************
            * 
            *  #7 Create a 'tag' for the new instance.
            *  
            *********************************************/
           resp.getWriter().println("#6 Create a 'tag' for the new instance.");
           List<String> resources = new LinkedList<String>();
           List<Tag> tags = new LinkedList<Tag>();
           Tag nameTag = new Tag("Name", "MyFirstInstance");
           
           resources.add(createdInstanceId);
           tags.add(nameTag);
           
           CreateTagsRequest ctr = new CreateTagsRequest(resources, tags);
           ec2.createTags(ctr);
           
           
                       
           /*********************************************
            * 
            *  #8 Stop/Start an Instance
            *  
            *********************************************/
           resp.getWriter().println("#7 Stop the Instance");
           List<String> instanceIds = new LinkedList<String>();
           instanceIds.add(createdInstanceId);
           
           //stop
           StopInstancesRequest stopIR = new StopInstancesRequest(instanceIds);
           //ec2.stopInstances(stopIR);
           
           //start
           StartInstancesRequest startIR = new StartInstancesRequest(instanceIds);
           //ec2.startInstances(startIR);
           
           
           /*********************************************
            * 
            *  #9 Terminate an Instance
            *  
            *********************************************/
           resp.getWriter().println("#8 Terminate the Instance");
           TerminateInstancesRequest tir = new TerminateInstancesRequest(instanceIds);
           ec2.terminateInstances(tir);
           
                       
           /*********************************************
            *  
            *  #10 shutdown client object
            *  
            *********************************************/
           ec2.shutdown();
           
           
           
       } catch (AmazonServiceException ase) {
               resp.getWriter().println("Caught Exception: " + ase.getMessage());
               resp.getWriter().println("Reponse Status Code: " + ase.getStatusCode());
               resp.getWriter().println("Error Code: " + ase.getErrorCode());
               resp.getWriter().println("Request ID: " + ase.getRequestId());
       }

       
	}
}
