package edu.columbia.e6998.cloudexchange.datastore;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class BlobServeHandler extends HttpServlet {
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
			
			String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
			PrintWriter out = resp.getWriter();
			String fileName = null;
			
			// Store the blob key in user table
			Query q = new Query("UserProfile");
			q.addFilter("userId", Query.FilterOperator.EQUAL, userId);
			Entity userProfile = datastore.prepare(q).asSingleEntity();
	
			// if userProfile does not exist, create entry in the database
			if (userProfile == null) {
				fileName = "No AWS credentials file uploaded";
			} else {
				String blobKeyString = (String) userProfile.getProperty("CredentialsBlobKey");
				//BlobKey blobKey = new BlobKey(blobKeyString);
				fileName = blobKeyString;
			}
			
			/*
			req.setAttribute("fileName", fileName);
			
			RequestDispatcher rd = getServletContext().getRequestDispatcher(destination);
			try {
				rd.forward(req, resp);
			} catch (ServletException e) {
				e.printStackTrace();
			}
			*/
			out.println(fileName);
	}
}
