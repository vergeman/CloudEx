package edu.columbia.e6998.cloudexchange.aws.spotprices;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.columbia.e6998.cloudexchange.aws.CredentialsManager;



@SuppressWarnings("serial")
public class PriceQueueServlet extends HttpServlet {

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		System.out.println("[PriceQueueServlet]");	
		String profile = req.getParameter("profile");
		
		CredentialsManager creds = new CredentialsManager();
		SpotPriceManager spm = new SpotPriceManager(creds);
		
		/*we can do historics if we have time*/
		Calendar time = Calendar.getInstance();
		time.add(Calendar.DAY_OF_YEAR, -1);
		String price = spm.getSpotpriceSince(profile, time);
		
	}
	
	
	/*for testing*/
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String profile = "0000000020121222";
		
		CredentialsManager creds = new CredentialsManager();
		SpotPriceManager spm = new SpotPriceManager(creds);
		
		String price = spm.getSpotprice(profile);
		
		resp.getWriter().println(price);
		
	}

	
}
