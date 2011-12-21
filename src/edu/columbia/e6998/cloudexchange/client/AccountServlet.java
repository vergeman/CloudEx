package edu.columbia.e6998.cloudexchange.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
public class AccountServlet extends HttpServlet {

	final String destination = "/views/account.jsp";

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
		
		// 1. Getting name of the file uploaded
		String fileName = null;
		
		Entity userProfile = GenericToolkit.getInstance().getUserProfileForUser(userId);

		// if userProfile does not exist, create entry in the database
		if (userProfile == null) {
			fileName = "No AWS credentials file uploaded";
		} else {
			String blobKeyString = (String) userProfile.getProperty("CredentialsBlobKey");
			BlobKey blobKey = new BlobKey(blobKeyString);
			BlobInfoFactory bFactory = new BlobInfoFactory();
			BlobInfo bInfo = bFactory.loadBlobInfo(blobKey);
			fileName = bInfo.getFilename() + " uploaded at "+ bInfo.getCreation().toString();
		}
		req.setAttribute("fileName", fileName);
		
		// 2. Get the list of open positions 
		ArrayList<PositionEntry> positions = new ArrayList<PositionEntry>();
		List<Entity> transactions = GenericToolkit.getInstance().getOpenTransactions();
		for (Entity transaction : transactions) {
			String buyerId = (String) transaction.getProperty("buyer");
			String sellerId = (String) transaction.getProperty("seller");
			if (userId.equals(buyerId) || userId.equals(sellerId))  {
				PositionEntry entry = new PositionEntry();
				if (userId.equals(buyerId))
					entry.buyOrSell = "Buy";
				else 
					entry.buyOrSell = "Sell";
				
				
				entry.zone = (String) transaction.getProperty("zone");
				entry.region = (String) transaction.getProperty("region");
				entry.ami = (String) transaction.getProperty("ami");
				entry.instance = (String) transaction.getProperty("instanceType");
				entry.contractPrice = (Double) transaction.getProperty("price");
				
				String dateString = (String) transaction.getProperty("date");
				String timeString = (String) transaction.getProperty("time");
				String dateTime = dateString + " " + timeString;
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH");
				Date date = null;
				try {
					date = sdf.parse(dateTime);
				} catch (Exception e) {
				
				}
				entry.date = date;
				
				// TODO: get current price
				entry.bidAskPrice = null;
				// TODO: get current spot price
				entry.spotPrice = null;
				
				positions.add(entry);
			}
		}
		
		req.setAttribute("positions", positions);
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

