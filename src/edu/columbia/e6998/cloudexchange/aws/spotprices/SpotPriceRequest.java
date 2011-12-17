package edu.columbia.e6998.cloudexchange.aws.spotprices;

import edu.columbia.e6998.cloudexchange.aws.AWSCodes;
import edu.columbia.e6998.cloudexchange.aws.AWSCodes.*;


public class SpotPriceRequest {


	private String profile;
	
	private Region region;
	private Zone zone;
	private OS os;
	private InstanceType instanceType;
	
	public SpotPriceRequest(String key) {
		this.profile = key;
		convert();
	}
	
	private void convert() {
	
		region = AWSCodes.Region.values()[Integer.parseInt(this.profile.substring(0, 2))];
		zone = AWSCodes.Zone.values()[Integer.parseInt(this.profile.substring(2, 4))];
		os = AWSCodes.OS.values()[Integer.parseInt(this.profile.substring(4, 6))];
		instanceType = AWSCodes.InstanceType.values()[Integer.parseInt(this.profile.substring(6, 8))];
		
	}


	public String getProfile() {
		return profile;
	}

	/*translated for ec2*/
	public String getRegion() {
		String z = zone.getZone();
		return z.substring(0, z.length() - 1);
	}

	public String getEndPoint() {
		String z = zone.getZone();
		return "ec2." + getRegion() + ".amazonaws.com";
	}
	
	public String getZone() {
		return zone.getZone();
	}

	public String getDescription() {
		return os.getDescription();
	}

	public String getInstanceType() {
		return instanceType.getCode();
	}
	

}
