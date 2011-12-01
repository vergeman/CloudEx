package edu.columbia.e6998.cloudexchange.aws;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
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
		AWSCredentials credentials = new PropertiesCredentials(
				SpotPrices.class
						.getResourceAsStream("AwsCredentials.properties"));
	
		ec2 = new AmazonEC2Client(credentials);
	
		
		try {
		
			DescribeRegionsResult regionResult = ec2.describeRegions();
			resp.getWriter().println(
					"You have access to "
							+ regionResult.getRegions().size() + " Regions.");

			for (Region r : regionResult.getRegions()) {
				resp.getWriter().println(r.getRegionName());
				resp.getWriter().println(r.getEndpoint());
				String ep = r.getEndpoint();
				
				ec2.setEndpoint(ep);
				DescribeAvailabilityZonesResult availabilityZonesResult = ec2
						.describeAvailabilityZones();
				
				
				for (AvailabilityZone z : availabilityZonesResult.getAvailabilityZones()) {
					resp.getWriter().println("\t" + z.getZoneName());
				}

				
				List<SpotPrice> prices = ec2.describeSpotPriceHistory().getSpotPriceHistory();
				for (SpotPrice sp : prices) {
					String out = "";
					out += "\t" + sp.getTimestamp().toString();
					out += "\t" + sp.getInstanceType();
					out += "\t" + sp.getProductDescription();
					out += "\t" + sp.getSpotPrice();
					out += "\t" + sp.getAvailabilityZone();
					
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
