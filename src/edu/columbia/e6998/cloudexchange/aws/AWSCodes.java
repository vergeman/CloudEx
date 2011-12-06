package edu.columbia.e6998.cloudexchange.aws;

@SuppressWarnings("unused")
public class AWSCodes {

	public enum Region {
		US_EAST ("US East (Virginia)", "US East"),
		US_WEST_1 ("US West (N. California)", "US West (1)"), 
		US_WEST_2 ("US West (Oregon)", "US West (2)"), 
		EU_WEST ("EU West (Ireland)", "EU"), 
		ASIA_1 ("Asia Pacific (Singapore)", "Asia (1)"), 
		ASIA_2 ("Asia Pacific (Tokyo)", "Asia (2)"); 
		
		private final String description;
		private final String view_name;
		
		Region(String description, String view_name) {
			this.description = description;
			this.view_name = view_name;
		}
		
		public String getDescription() { return description;}
		public String getView_name() { return view_name;}
		
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
		
		public String[] getZones() { return zones; }
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
		
		public String getDescription() { return description; }
	}
	
	/*we'll figure out some logic crap to choose appropriate values*/
	public enum InstanceType {
		MICRO ("micro", "t1.micro"),
		STDSMALL ("small", "m1.small"),
		STDLARGE("large", "m1.large"),
		STDXLARGE("xlarge", "m1.xlarge"),
		CPUMED ("high-cpu med", "c1.medium"),
		CPUXLARGE("high-cpu xl", "c1.xlarge"),
		HIGHMEM_XLARGE("high-mem xl", "m2.xlarge"),
		HIGHMEM_2XLARGE("high-mem 2xlarge", "m2.2xlarge"),
		HIGHMEM_4XLARGE("high-mem quad  xlarge", "m2.4xlarge"),
		CLUSTER_XLARGE4("cluster xl 4 core", "cc1.4xlarge"),
		CLUSTER_XLARGE8("cluster xl 8 core", "cc2.8xlarge"),
		CLUSTER_GPU_XLARGE("cluster xl gpu", "g1.4xlarge");
		
		private final String description;
		private final String code;
		
		InstanceType(String description, String code) {
			this.description = description;
			this.code = code;
		}
		
		public String getDescription() { return description; }
		public String getCode() { return code; }
		
	}
	
}
