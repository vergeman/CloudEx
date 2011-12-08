package edu.columbia.e6998.cloudexchange.aws;

import java.util.List;

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
		public String getOrdinal() { return String.format("%02d", this.ordinal()); }
	}
	
	public enum Zones {
		US_EAST ("us-east-1a", "us-east-1c", "us-east-1d"),
		US_WEST_1 ("us-west-1b", "us-west-1c"), 
		US_WEST_2 ("us-west-2a", "us-west-2b"), 
		EU_WEST ("eu-west-1a", "eu-west-1b", "eu-west-1c"), 
		ASIA_1 ("ap-southeast-1a", "ap-southeast-1b"), 
		ASIA_2 ("ap-northeast-1a", "ap-northeast-1b"); 
		
		private final String[] zones;
		
		Zones(String... zones) {
			this.zones = zones;
		}
		
		public String[] getZones() { return zones; }
		public String getOrdinal() { return String.format("%02d", this.ordinal()); }
	}
	
	public enum Zone {
		US_EAST1A 	("us-east-1a"),
		US_EAST1C 	("us-east-1c"),
		US_EAST1D 	("us-east-1d"),
		US_WEST1B 	("us-west-1b"), 
		US_WEST1C 	("us-west-1c"),
		US_WEST2A 	("us-west-2a"),
		US_WEST2B 	("us-west-2b"), 
		EU_WEST1A 	("eu-west-1a"), 
		EU_WEST1B 	("eu-west-1b"), 
		EU_WEST1C 	("eu-west-1c"), 
		ASIASE_1A   ("ap-southeast-1a"),
		ASIASE_1B	("ap-southeast-1b"), 
		ASIANE_2A 	("ap-northeast-1a"),
		ASIANE_2B	("ap-northeast-1b");
		
		private final String zone;
		
		Zone(String zone) {
			this.zone = zone;
		}
		
		public String getZone() { return zone; }
		public String getOrdinal() { return String.format("%02d", this.ordinal()); }
	}
	

	/*ignoring VPC stuff*/
	public enum OS {
		LINUX	("Linux/UNIX"),
		SUSE_LINUX	("SUSE Linux"), 
		WINDOWS	("Windows");
		
		private final String description;
		
		OS(String description) {
			this.description = description;
		}
		
		public String getDescription() { return description; }
		public String getOrdinal() { return String.format("%02d", this.ordinal()); }
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
		public String getOrdinal() { return String.format("%02d", this.ordinal()); }

		
	}
	
}
