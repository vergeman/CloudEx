package edu.columbia.e6998.cloudexchange.datastore;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobInfo;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserServiceFactory;

import edu.columbia.e6998.cloudexchange.toolkit.GenericToolkit;

@SuppressWarnings("serial")
public class BlobServeHandler extends HttpServlet {

	//private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	final String destination = "/views/account.jsp";
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		

		String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
		// Store the blob key in user table
		Query q = new Query("UserProfile");
		q.addFilter("userId", Query.FilterOperator.EQUAL, userId);
		Entity userProfile = datastore.prepare(q).asSingleEntity();

		// if userProfile does not exist, create entry in the database
		userProfile.setProperty("defaultAmi", "ami-8c1fece5");
		userProfile.setProperty("keyPair", "MyKeyPair");
		userProfile.setProperty("securityGroup", "NewSecurityGroup");
		datastore.put(userProfile);
		
		q = new Query("UserProfile");
		q.addFilter("userId", Query.FilterOperator.EQUAL, "110709289717792221869");
		userProfile = datastore.prepare(q).asSingleEntity();

		// if userProfile does not exist, create entry in the database
		userProfile.setProperty("defaultAmi", "ami-8c1fece5");
		userProfile.setProperty("keyPair", "MyKeyPair");
		userProfile.setProperty("securityGroup", "NewSecurityGroup");
		datastore.put(userProfile);
		
		q = new Query("UserProfile");
		q.addFilter("userId", Query.FilterOperator.EQUAL, "103831381031551283236");
		userProfile = datastore.prepare(q).asSingleEntity();

		// if userProfile does not exist, create entry in the database
		userProfile.setProperty("defaultAmi", "ami-8c1fece5");
		userProfile.setProperty("keyPair", "MyKeyPair");
		userProfile.setProperty("securityGroup", "NewSecurityGroup");
		datastore.put(userProfile);

		RequestDispatcher rd = getServletContext().getRequestDispatcher(destination);
		try {
			rd.forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		doGet(req, resp);
	}
}

