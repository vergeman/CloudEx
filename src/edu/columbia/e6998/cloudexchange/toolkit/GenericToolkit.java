package edu.columbia.e6998.cloudexchange.toolkit;
import edu.columbia.e6998.cloudexchange.aws.*;
import edu.columbia.e6998.cloudexchange.channel.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class GenericToolkit {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	private ChannelService chn = ChannelServiceFactory.getChannelService();

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
		createData(profile);
		return profile;

		
	}
	
	public String[] reverseLookUpProfile(String profile){
		String [] lookup = new String[5];

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
	
	public String[][] getBidsOffers(String profile){
		System.out.println("[getBidsOffers] : " + profile);
		String[][] results = new String[2][24];
		
		queryDataStore(profile, null);
		
		//can't iterate through null list (but can an empty list..)
		if (!syncCache.contains(profile))
			return results;	
		
		for(Entity t : (Entity[]) syncCache.get(profile)){
			int i = 1;
			if (t!= null){
				if ((Boolean) t.getProperty("seller"))
					i = 0;
				//results[i][hourToIndex(((Entity) t).getProperty("hour").toString(), (Boolean) ((Entity) t).getProperty("seller"))] = ((Entity) t).getProperty("price").toString();
				//[2][24] - hourIndex unnecessary for now
				results[i][Integer.parseInt(((Entity) t).getProperty("hour").toString())] = ((Entity) t).getProperty("price").toString();

			}
		}

		return results;
	}
	
	public String queryDataStore(){
		return queryDataStore("", null);
	}
	
	public String queryDataStore(String optProfile, Key deleted){
		//TODO can be made into single i/o batch
		String s = "";
		Entity[] tmpList = new Entity[48];
		String memKey = "";
		int index = 0;
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
			index = hourToIndex(((String) e.getProperty("hour")), ((Boolean) e.getProperty("seller")));
			
			if (tmpList != null){
				if ( deleted == null || deleted != e.getKey())
					tmpList[index] = e;
			}else{
				tmpList = new Entity[48];
				tmpList[index] = e;
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
			index = hourToIndex(((String) e.getProperty("hour")), ((Boolean) e.getProperty("seller")));
			
			if (tmpList != null){
				if ( deleted == null || deleted != e.getKey())
					tmpList[index] = e;
			}else{
				tmpList = new Entity[48];
				tmpList[index] = e;
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
		contract.setProperty("hour", 			indexToHour(arrayIndex));
		contract.setProperty("user", 			user);
		contract.setProperty("region", 			lookup[REGION]);
		contract.setProperty("zone", 			lookup[ZONE]);
		contract.setProperty("OS", 				lookup[OS]);
		contract.setProperty("instanceType", 	lookup[INSTANCE_TYPE]);
		contract.setProperty("seller", 			Integer.valueOf(arrayIndex)%2 == 0);
		contract.setProperty("active", 			true);
		datastore.put(contract);
		queryDataStore(profile, null);
		return "read_memcache::" + profile + "_" + arrayIndex;
		//updateMemcache(contract);
	}
	
	public String createTransaction(String profile, String arrayIndex, String buyer, String ami, String securityGroupName, String keyPairName){
		int index = Integer.valueOf(arrayIndex);
		Entity offer = ((Entity[])syncCache.get(profile))[index];
		String[] lookup = reverseLookUpProfile(profile);
		
		Entity transaction = new Entity("Transaction");
		transaction.setProperty("buyer", buyer);
		transaction.setProperty("seller", 			offer.getProperty("user"));
		transaction.setProperty("is_buy", 			offer.getProperty("seller"));
		transaction.setProperty("price", 			offer.getProperty("price"));
		transaction.setProperty("date", 			offer.getProperty("date"));
		transaction.setProperty("time", 			indexToHour(arrayIndex));	//calendar always 1 hour after this
		transaction.setProperty("ami", 				ami);			//variable - user provided
		transaction.setProperty("instanceType", 	lookup[INSTANCE_TYPE]);	//micro, large
		transaction.setProperty("region", 			lookup[REGION]);//usa, jp etc
		transaction.setProperty("zone", 			lookup[ZONE]);	//variable
		transaction.setProperty("securityGroup", 	securityGroupName);	//user provided or system generated by http and ssh access only
		transaction.setProperty("keyPair",			keyPairName);
		transaction.setProperty("instanceID", 		"NA");
		datastore.put(transaction);
		deleteBidOffer(profile, arrayIndex);
		queryDataStore(profile, null);
		return "read_memcache::" + profile + "_" + arrayIndex;
		
	}
	
	public String deleteBidOffer(String profile, String arrayIndex){
		int index = Integer.valueOf(arrayIndex);
		Entity e;
		try {
			Transaction txn = datastore.beginTransaction();
			e = datastore.get(((Entity[])syncCache.get(profile))[index].getKey());
			e.setProperty("active", false);
			datastore.put(e);
			syncCache.put(profile, null);
			txn.commit();
		} catch (EntityNotFoundException e1) {
			return "entity not found::" + profile + "_" + arrayIndex;
		}
		
		queryDataStore(profile, e.getKey());
		return "read_memcache::" + profile + "_" + arrayIndex;
	}
	
	private void sendChannelMessage(){
		
	}
	
	private String indexToHour(String arrayIndex){
		int index = Integer.valueOf(arrayIndex);
		if(index%2 == 0)
			return String.format("%02d", index/2);
		else
			return String.format("%02d", (index - 1)/2);
	}
	
	private int hourToIndex(String hour, Boolean seller){
		int h = Integer.valueOf(hour);
		if(seller)
			return h*2;
		else
			return (h*2) + 1;
	}
	
	/*
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
	*/
	
	public String test(){
//		String s = "\n";
//		s+= createBidOffer("0000000020110101", 0.3, "batman", "46");
//		s+= "\n";
//		s+= createBidOffer("0000000020110101", 0.2, "robin", "47");
//		s+= "\n";
//		s+= createBidOffer("0000000020110201", 0.3, "lisa", "26");
//		s+= "\n";
//		s+= createBidOffer("0000000020110201", 0.3, "bart", "27");
//		s+= "\n";
//		s+= createBidOffer("0000000020110101", 0.3, "batman", "06");
//		s+= "\n";
//		s+="After inserts:\n";
//		s+= dumpMemCache();
//		s+= "\n";
//
//		s+= createTransaction("0000000020110101", "46", "joker", "ami", "SG", "KP");
//		s+= "\n";
//		for(int i = 0; i <= 100000; i++){
//			//do nottin mon
//		}
//			
//		s+="After sell:\n";
//		s+= dumpMemCache();
//		s+= indexToHour("46");
		String s;
		s = "";
		return s;
		
	}
	public String dumpMemCache(){
		String s = "";
		queryDataStore();
		for (String k : getProfiles()){
			for(Object t : (Entity[]) syncCache.get(k)){
				if (t!= null)
					s 	+= "\nProfile: " + ((Entity) t).getProperty("profile").toString()
						+ " \thour: " + ((Entity) t).getProperty("hour").toString()
						+ " \towner: " + ((Entity) t).getProperty("user").toString()
						+ " \tprice: " + ((Entity) t).getProperty("price").toString()
						+ " \tseller: " + ((Entity) t).getProperty("seller").toString();
			}
			s 	+= "\n";
		}
		return s;
	}
	
	public void createData(String profile){
		Random r = new Random();
		r.setSeed(7/9);
		String[] user = {"batman", "robin", "joker", "lisa"};
		String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20,",
				"21", "22", "23"};
		if(syncCache.get(profile) == null)
			createBidOffer(profile, r.nextDouble() + 0.001, user[r.nextInt(4)], hours[r.nextInt(24)]);
	}
	
}
