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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserServiceFactory;

import edu.columbia.e6998.cloudexchange.toolkit.GenericToolkit;

@SuppressWarnings("serial")
public class BlobServeHandler extends HttpServlet {

	//private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	final String destination = "/views/account.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {


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

