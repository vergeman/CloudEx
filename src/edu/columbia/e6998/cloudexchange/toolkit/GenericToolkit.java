package edu.columbia.e6998.cloudexchange.toolkit;
import edu.columbia.e6998.cloudexchange.aws.*;
import edu.columbia.e6998.cloudexchange.aws.AWSCodes.Zone;
//import edu.columbia.e6998.cloudexchange.aws.AWSCodes.Zones;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class GenericToolkit {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	SimpleDateFormat dateYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
	
	final int REGION 		= 0;
	final int ZONE 			= 1;
	final int OS 			= 2;
	final int INSTANCE_TYPE = 3;
	final int DATE 			= 4;
	
	public String generateProfileKey(String region, String zone, String OS, String instanceType, Date date){
		StringBuilder sDate = new StringBuilder(dateYYYYMMDD.format(date));
		
		String profile =  String.format("%02d", AWSCodes.Region.valueOf(region).ordinal()) 
						+ String.format("%02d", AWSCodes.Zone.valueOf(zone).ordinal())
						+ String.format("%02d", AWSCodes.OS.valueOf(OS).ordinal())
						+ String.format("%02d", AWSCodes.InstanceType.valueOf(instanceType).ordinal())
						+ sDate;
		
		return profile;

		
	}
	
	public String[] reverseLookUpProfile(String profile){
		String [] lookup = new String[5];
		//TODO tweaks needed

		lookup[REGION] 			= AWSCodes.Region.values()[Integer.valueOf(profile.substring(0, 2))].toString();
		lookup[ZONE] 			= AWSCodes.Zone.values()[Integer.valueOf(profile.substring(2, 4))].getZone();
		lookup[OS] 				= AWSCodes.OS.values()[Integer.valueOf(profile.substring(4, 6))].toString();
		lookup[INSTANCE_TYPE] 	= AWSCodes.InstanceType.values()[Integer.valueOf(profile.substring(6, 8))].getCode();//.toString();;
		lookup[DATE] = profile.substring(8, 16);
		return lookup;
		
	}
	
	public Date dateConvert(String date) {
		//TODO needs error handling here
		try {
			return (Date) dateYYYYMMDD.parse(date);
		} catch (ParseException e) {
			return new Date();
		}
	}
	

	private ArrayList<String> removeProfile(String profile){
		
		@SuppressWarnings("unchecked")
		ArrayList<String> profiles = (ArrayList<String>) syncCache.get("Profiles");
		if (profiles == null)
			return null;
		if(profiles.remove(profile));
			syncCache.put("Profiles", profiles);
		return profiles;
		
	}

	private ArrayList<String> addProfile(String profile){
		
		@SuppressWarnings("unchecked")
		ArrayList<String> profiles = (ArrayList<String>) syncCache.get("Profiles");
		if (profiles == null)
			profiles = new ArrayList<String>();
		if (profiles.contains(profile))
			return profiles;

		profiles.add(profile);
		syncCache.put("Profiles", profiles);
		return profiles;
		
	}
	
	public ArrayList<String> getProfiles(){
		return removeProfile("xxx");
	}
	
	public String queryDataStore(){
		return queryDataStore("");
	}
	
	public String queryDataStore(String optProfile){
		String s = "";
		Entity[] tmpList = new Entity[48];
		String memKey = "";
		
		Query qSeller = new Query("Contract");
		qSeller.addFilter("active", Query.FilterOperator.EQUAL, true);
		qSeller.addFilter("seller", FilterOperator.EQUAL, true);
		if (!optProfile.equals(""))
			qSeller.addFilter("profile", FilterOperator.EQUAL, optProfile);
		qSeller.addSort("price", Query.SortDirection.DESCENDING);
		
		
		List<Entity> rSellers = datastore.prepare(qSeller).asList(FetchOptions.Builder.withDefaults());
		for(Entity e: rSellers){
			memKey = (String) e.getProperty("profile");
			tmpList = (Entity[]) syncCache.get(memKey);
			
			if (tmpList != null){
				tmpList[Integer.valueOf((String) e.getProperty("hour"))*2] = e;
			}else{
				tmpList = new Entity[48];
				tmpList[Integer.valueOf((String) e.getProperty("hour"))*2] = e;
			}
			addProfile(memKey);
			syncCache.put(memKey, tmpList);
		}
		
		Query qBuyer = new Query("Contract");
		qBuyer.addFilter("active", Query.FilterOperator.EQUAL, true);
		qBuyer.addFilter("seller", FilterOperator.EQUAL, false);
		if (!optProfile.equals(""))
			qBuyer.addFilter("profile", FilterOperator.EQUAL, optProfile);
		qBuyer.addSort("price", Query.SortDirection.ASCENDING);

		
		List<Entity> rBuyers = datastore.prepare(qBuyer).asList(FetchOptions.Builder.withDefaults());
		for(Entity e: rBuyers){
			memKey = (String) e.getProperty("profile");
			tmpList = (Entity[]) syncCache.get(memKey);
			
			if (tmpList != null){
				tmpList[Integer.valueOf((String) e.getProperty("hour"))*2 + 1] = e;
			}else{
				tmpList = new Entity[48];
				tmpList[Integer.valueOf((String) e.getProperty("hour"))*2 + 1] = e;
			}
			
			addProfile(memKey);
			syncCache.put(memKey, tmpList);

		}
		
		return s + "\n DONE!";
	}
	
	public String createBidOffer(String profile, double price, String user, String arrayIndex){
		Entity contract = new Entity("Contract");
		String[] lookup = reverseLookUpProfile(profile);
		contract.setProperty("profile", 		profile);
		contract.setProperty("date", 			dateConvert(lookup[DATE]));
		contract.setProperty("qty", 			1);
		contract.setProperty("price", 			price);
		contract.setProperty("hour", 			0);
		contract.setProperty("user", 			"new market maker");
		contract.setProperty("region", 			lookup[REGION]);
		contract.setProperty("zone", 			lookup[ZONE]);
		contract.setProperty("OS", 				lookup[OS]);
		contract.setProperty("instanceType", 	lookup[INSTANCE_TYPE]);
		contract.setProperty("seller", 			Integer.valueOf(arrayIndex)%2 == 0);
		contract.setProperty("active", 			true);
		contract.setProperty("user", 			user);
		contract.setProperty("price", 			price);
		datastore.put(contract);
		queryDataStore(profile);
		return "read_memcache::" + profile + "_" + String.format("%02d", arrayIndex);
		//updateMemcache(contract);
	}
	
	public String createTransaction(String profile, String arrayIndex, String buyer, String offerOwner, String ami, String instanceType, String securityGroupName, String keyPairName){
		Entity offer = ((Entity[])syncCache.get(profile))[Integer.valueOf(arrayIndex)];
		String[] lookup = reverseLookUpProfile(profile);
		
		Entity transaction = new Entity("Transaction");
		transaction.setProperty("buyer", buyer);
		transaction.setProperty("seller", 			offer.getProperty("user"));
		transaction.setProperty("buy/sell", 		offer.getProperty("seller"));
		transaction.setProperty("price", 			offer.getProperty("price"));
		transaction.setProperty("date", 			offer.getProperty("date"));
		transaction.setProperty("time", 			arrayIndex);	//calendar always 1 hour after this
		transaction.setProperty("ami", 				ami);				//variable - user provided
		transaction.setProperty("instanceType", 	instanceType);	//micro, large
		transaction.setProperty("region", 			lookup[REGION]);			//usa, jp etc
		transaction.setProperty("zone", 			lookup[ZONE]);			//variable
		transaction.setProperty("securityGroup", 	securityGroupName);	//user provided or system generated by http and ssh access only
		transaction.setProperty("keyPair",			keyPairName);
		transaction.setProperty("instanceID", 		"NA");
		datastore.put(transaction);
		queryDataStore(profile);
		return "read_memcache::" + profile + "_" + String.format("%02d", arrayIndex);
		
	}
	
	public String deleteBidOffer(String profile, String arrayIndex){
		Entity delete = ((Entity[])syncCache.get(profile))[Integer.valueOf(arrayIndex)];
		Entity e = new Entity("Contract");
		e.setPropertiesFrom(e);
		e.setProperty("active", false);
		datastore.delete(delete.getKey());
		datastore.put(e);
		queryDataStore(profile);
		return "read_memcache::" + profile + "_" + String.format("%02d", arrayIndex);
	}
	
	public boolean updateMemcache(Entity e){
		//TODO visit later
		Entity[] tmpList = new Entity[48];
		
		if (!syncCache.contains(e.getProperty("profile"))){
			//simple add
		}else{
			//compare
			tmpList = (Entity[]) syncCache.get(e.getProperty("profile"));
			if((Boolean) e.getProperty("seller")){
				if ((Double) e.getProperty("price") > (Double) tmpList[(Integer) e.getProperty("hour")].getProperty("price")){
					return false;
				}
			}
			else{
				if((Boolean) e.getProperty("seller")){
					if ((Double) e.getProperty("price") <= (Double) tmpList[(Integer) e.getProperty("hour")].getProperty("price")){
						//do nottin mon!!!
						return false;
					}
				}
			}
		}


		return false;
		
	}
	
	public String test(){
		//String profile =   AWSCodes.Zone.ASIANE_2A.zone();
		//String profile = generateProfileKey("US_EAST", "US_EAST1A", "Windows","MICRO", new Date());
		//createData();
		String[] r = reverseLookUpProfile("0004000120111207");
		//String s = queryDataStore();
		//String s = r[0] + r[1] + r[2] + r[3] + r[4];
		//TODO remove testing
		String s = "";
		for (String k : getProfiles()){
			for(Object t : (Entity[]) syncCache.get(k)){
				if (t!= null)
					s 	+= "\nProfile: " + ((Entity) t).getProperty("profile").toString()
						+ " \t\nhour: " + ((Entity) t).getProperty("hour").toString()
						+ " \tprice: " + ((Entity) t).getProperty("price").toString()
						+ " \tb/o: " + ((Entity) t).getProperty("seller").toString();
			}
		}
	
		return r[0] + " " + r[1] + " " + r[2] + " " + r[3] + " " + r[4];
		//return AWSCodes.Region.values()[0].description();
	}
	
	public void createData(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String[] region = {"US_EAST", "US_WEST_1", "US_WEST_2"};
		String[] zone = {"US_EAST1A", "US_EAST1C", "US_EAST1D", "US_WEST1B", "US_WEST1C"};
		String[] OS = {"Linux","Windows", "SUSE_Linux"};
		String[] instanceType = {"MICRO", "STDSMALL"};
		String[] user = {"batman", "robin", "bart", "lisa"};
		
		Date date = new Date();

		Random rand = new Random(); 

		String r,z,O,it;
//		r = region[rand.nextInt(region.length)]; 
//		rand = new Random(); 
//		z =	zone[rand.nextInt(zone.length)];
//		rand = new Random(); 
//		O =	OS[rand.nextInt(OS.length)];
//		rand = new Random(); 
//		it =	instanceType[rand.nextInt(instanceType.length)]; 
		r = region[0]; 
		rand = new Random(); 
		z =	zone[rand.nextInt(zone.length)];
		rand = new Random(); 
		O =	OS[0];
		rand = new Random(); 
		it =	instanceType[0];
		String profile = generateProfileKey(r,z,O, it, date);
		String sDate = new StringBuilder(dateYYYYMMDD.format(date)).toString();
		
		int hour = rand.nextInt(2);
		insertNewOffer(profile, rand.nextDouble() + 0.001, user[rand.nextInt(4)], String.valueOf(hour));
		
		
		
		rand = new Random(); 
		
		for(int i = 0; i < 5 ; i++){
		
			Entity contract = new Entity("Contract");
			contract.setProperty("profile", 		profile);
			contract.setProperty("date", 			sDate);
			contract.setProperty("qty", 			rand.nextInt(5) + 1);
			contract.setProperty("price", 			rand.nextDouble() + 0.001);
			contract.setProperty("hour", 			String.valueOf(hour));
			contract.setProperty("user", 			user[rand.nextInt(4)]);
			contract.setProperty("region", 			r);
			contract.setProperty("zone", 			z);
			contract.setProperty("OS", 				O);
			contract.setProperty("instanceType", 	it);
			contract.setProperty("active", 			true);
			contract.setProperty("seller", 			true);
			datastore.put(contract);
			 
		}
	}
}
