package edu.columbia.e6998.cloudexchange.aws;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.SpotPrice;

public class SpotPrices {
	static AmazonEC2 ec2;

	/*
	 * test code to get prices via Amazon
	 * had to fork and update the aws-sdk-for-java-on-gae library
	 * to reflect availabilityZones which is desired functionality
	 */
	public SpotPrices(HttpServletResponse resp) throws IOException {
		CredentialsManager credentials = new CredentialsManager();
		
		ec2 = new AmazonEC2Client(credentials.getCredentials());
	
		
		try {

			DescribeRegionsResult regionResult = ec2.describeRegions();
			//resp.getWriter().println(
			//		"You have access to "
			//				+ regionResult.getRegions().size() + " Regions.");

			for (Region r : regionResult.getRegions()) {
				//resp.getWriter().println(r.getRegionName());
				//resp.getWriter().println(r.getEndpoint());
				String ep = r.getEndpoint();
				
				ec2.setEndpoint(ep);
				DescribeAvailabilityZonesResult availabilityZonesResult = ec2
						.describeAvailabilityZones();
				
				
				for (AvailabilityZone z : availabilityZonesResult.getAvailabilityZones()) {
					//resp.getWriter().println("\t" + z.getZoneName());
				}

				//DescribeSpotPriceHistoryRequest req = new DescribeSpotPriceHistoryRequest();
				//System.out.println(req.getStartTime().toString());
				/*
				Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

				now.add(Calendar.HOUR, -5);
				System.out.println(now.toString());
				
				req.setStartTime(new Date(now.getTimeInMillis()));
				
				System.out.println(req.getStartTime().toGMTString());
				*/
				//List<SpotPrice> prices = ec2.describeSpotPriceHistory(req).getSpotPriceHistory();
				List<SpotPrice> prices = ec2.describeSpotPriceHistory().getSpotPriceHistory();

				
				for (SpotPrice sp : prices) {
					String out = "";
					out += "\t" + sp.getTimestamp().toString();
					out += "\t" + sp.getInstanceType();
					out += "\t\t" + sp.getProductDescription();
					out += "\t\t" + sp.getSpotPrice();
					out += "\t\t" + sp.getAvailabilityZone();
					
				resp.getWriter().println(out);
				}
			}
			
		
			
			
		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println(
					"Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}
		
		ec2.shutdown();
	}
}
