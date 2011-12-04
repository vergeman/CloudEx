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
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class BlobUploadHandler extends HttpServlet {

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		final String destination = "/account";
		RequestDispatcher rd;

		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("myFile");

		if (blobKey == null) {

		} else {

			String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
			// Store the blob key in user table
			Query q = new Query("UserProfile");
			q.addFilter("userId", Query.FilterOperator.EQUAL, userId);
			Entity userProfile = datastore.prepare(q).asSingleEntity();

			// if userProfile does not exist, create entry in the database
			if (userProfile == null) {
				userProfile = new Entity("UserProfile");
				userProfile.setProperty("userId", UserServiceFactory.getUserService().getCurrentUser().getUserId());
				userProfile.setProperty("CredentialsBlobKey", blobKey.getKeyString());
			} else {
				userProfile.setProperty("CredentialsBlobKey", blobKey.getKeyString());
			}
			datastore.put(userProfile);
		}

		rd = getServletContext().getRequestDispatcher(destination);
		try {
			rd.forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}

