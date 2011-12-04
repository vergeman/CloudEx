package edu.columbia.e6998.cloudexchange.aws;

@SuppressWarnings("unused")
public class AWSCodes {

	public enum Region {
		US_EAST ("US East (Virginia)"),
		US_WEST_1 ("US West (N. California)"), 
		US_WEST_2 ("US West (Oregon)"), 
		EU_WEST ("EU West (Ireland)"), 
		ASIA_1 ("Asia Pacific (Singapore)"), 
		ASIA_2 ("Asia Pacific (Tokyo)"); 
		
		private final String description;
		
		Region(String description) {
			this.description = description;
		}
		
		private String description() { return description;}
		
	}
	
	public enum Zones {
		US_EAST ("us-east-1a", "us-east-1c", "us-east-1d"),
		US_WEST_1 ("us-west-1b", "us-west-1c"), 
		US_WEST_2 ("us-west-2a", "us-west-2b"), 
		EU_WEST ("eu-west-1a", "eu-west-1b", "eu-west-1c"), 
		ASIA_1 ("ap-southeast-1a, ap-southeast-1b"), 
		ASIA_2 ("ap-northeast-1a, ap-northeast-1b"); 
		
		private final String[] zones;
		
		Zones(String... zones) {
			this.zones = zones;
		}
		
		private String[] zones() { return zones; }
	}
	
	
	/*ignoring VPC stuff*/
	public enum OS {
		Linux ("Linux/UNIX"),
		SUSE_Linux ("SUSE Linux"), 
		Windows ("Windows");
		
		private final String description;
		
		OS(String description) {
			this.description = description;
		}
		
		private String description() { return description; }
	}
	
	/*we'll figure out some logic crap to choose appropriate values*/
	public enum InstanceType {
		MICRO ("micro", "t1.micro"),
		STDSMALL ("small", "m1.small"),
		STDLARGE("large", "m1.large"),
		STDXLARGE("xlarge", "m1.xlarge"),
		CPUMED ("High-cpu med", "c1.medium"),
		CPUXLARGE("High-cpu xlarge", "c1.xlarge"),
		HIGHMEM_XLARGE("High-memory xlarge", "m2.xlarge"),
		HIGHMEM_2XLARGE("High-memory double xlarge", "m2.2xlarge"),
		HIGHMEM_4XLARGE("High-memory quad  xlarge", "m2.4xlarge"),
		CLUSTER_XLARGE4("cluster xlarge 4core", "cc1.4xlarge"),
		CLUSTER_XLARGE8("cluster xlarge 8core", "cc2.8xlarge"),
		CLUSTER_GPU_XLARGE("cluster xlarge gpu", "g1.4xlarge");
		
		private final String description;
		private final String code;
		
		InstanceType(String description, String code) {
			this.description = description;
			this.code = code;
		}
		
		private String description() { return description; }
		private String code() { return code; }
		
	}
	
}
