package edu.columbia.e6998.cloudexchange.aws;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.SpotPrice;

/*
 * this class encapsulates infrequently updated / static
 * information on instances
 * 
 * amazon regions (eu, east, west..)
 * instance types (m1.large, t1.micro)
 * product description (Linux, SUSE, Windows)
 * availability Zones
 */

/*SCRATCH CODE*/
public class StaticDataManager {

	private AmazonEC2 ec2;
	
	private HashMap<String, Integer> regions;
	private HashMap<String, Integer> availabilityZones;
	private HashMap<String, Integer> os;
	private HashMap<String, Integer> types;
	
	private Calendar lastUpdate;
	
	
	public StaticDataManager() throws IOException {
		
		CredentialsManager credentials = new CredentialsManager();
		
		ec2 = new AmazonEC2Client(credentials.getCredentials());

	}
	
	
	public void UpdateAll() {
		DescribeRegionsResult regionResult = ec2.describeRegions();
	
		for (Region r : regionResult.getRegions()) {
			//regions.put(key, value)
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
				
			//resp.getWriter().println(out);
			}
		}
		
		
		
	}
	
	
	
	
}
