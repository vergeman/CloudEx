package edu.columbia.e6998.cloudexchange.toolkit;
import edu.columbia.e6998.cloudexchange.aws.*;
import edu.columbia.e6998.cloudexchange.aws.AWSCodes.Zone;
//import edu.columbia.e6998.cloudexchange.aws.AWSCodes.Zones;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	
	
	public String generateProfileKey(String region, String zone, String OS, String instanceType, Date date){
		

		StringBuilder sDate = new StringBuilder(dateYYYYMMDD.format(date));
		
		String profile =  String.format("%02d", AWSCodes.Region.valueOf(region).ordinal()) 
						+ String.format("%02d", AWSCodes.Zone.valueOf(zone).ordinal())
						+ String.format("%02d", AWSCodes.OS.valueOf(OS).ordinal())
						+ String.format("%02d", AWSCodes.InstanceType.valueOf(instanceType).ordinal())
						+ sDate;
		
		//profile = "0000000020111124";// + String.format("%02d", rand.nextInt(24));
		return profile;

		
	}
	
	@SuppressWarnings("unchecked")
	public void queryDataStore(){
		//TODO add buyers as well
		String s = "";
		List<Entity> tmpList = new ArrayList<Entity>();
		Hashtable<String, Entity> hTable = new Hashtable<String, Entity>();

		//createData();

		Query qSeller = new Query("Contract");
		qSeller.addFilter("active", Query.FilterOperator.EQUAL, true);
		qSeller.addFilter("seller", FilterOperator.EQUAL, true);
		qSeller.addSort("price", Query.SortDirection.ASCENDING);
		
		List<Entity> rSellers = datastore.prepare(qSeller).asList(FetchOptions.Builder.withDefaults());
		
		for(Entity e: rSellers){
			int iHour = (Integer) e.getProperty("hour");
			String memKey = ((String) e.getProperty("profile"));
			
			if(hTable.containsKey(e.getProperty("profile"))){
				s += ("\nAlready exists, do nothing");
			}
			else{
				tmpList.clear();
				hTable.put((String) e.getProperty("profile"), e);
				if(((List<Entity>) syncCache.get(memKey)) == null){
					tmpList.add(e);
				}else{
					tmpList = (List<Entity>) syncCache.get(memKey);
					tmpList.add(e);
				}
				s += ("\n***Adding new profile to memkey:"  + memKey + " Profile:" + (String) e.getProperty("profile"));	
				syncCache.put(memKey, tmpList);
				}
			
		}

		//TODO remove testing
		int count = 1;
		List<Entity> tmp = (List<Entity>) syncCache.get("0000000020111124");
		for(Object t : tmp){
			System.out.println("Count:" + count++ + " Profile:" + (String) ((Entity) t).getProperty("profile"));
		}
		System.out.println(s);
	}
	
	public void createData(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String[] region = {"US_EAST", "US_WEST_1", "US_WEST_2"};
		String[] zone = {"US_EAST1A", "US_EAST1C", "US_EAST1D", "US_WEST1B", "US_WEST1C"};
		String[] OS = {"Linux","Windows", "SUSE_Linux"};
		String[] instanceType = {"MICRO", "STDSMALL"};
		String[] user = {"batman", "robin", "bart", "lisa"};
		Boolean[] active = {true, false};

		Date date = new Date();

		Random rand = new Random(); 

		String r,z,O,it,u;
		r = region[rand.nextInt(region.length)]; 
		z =	zone[rand.nextInt(zone.length)]; 
		O =	OS[rand.nextInt(OS.length)];
		it =	instanceType[rand.nextInt(instanceType.length)]; 

		String profile = generateProfileKey(r,z,O, it, date);
		String sDate = new StringBuilder(dateYYYYMMDD.format(date)).toString();
		for(int i = 0; i < 100 ; i++){
		
			Entity contract = new Entity("Contract");

			 //profile = "1_0_1" + "_" + date;
			 contract.setProperty("profile", profile);
			 contract.setProperty("date", 			sDate);
			 contract.setProperty("qty", 			rand.nextInt(5) + 1);
			 contract.setProperty("price", 			rand.nextFloat() + 0.001);
			 contract.setProperty("seller", 		active[rand.nextInt(2)]);
			 contract.setProperty("user", 			user[rand.nextInt(4)]);
			 contract.setProperty("region", 		r);
			 contract.setProperty("zone", 			z);
			 contract.setProperty("OS", 			O);
			 contract.setProperty("instanceType", 	it);
			 contract.setProperty("active", true);

			 datastore.put(contract);
			 
		}
	}
	
	public void insertNewOffer(String profile, double price, String user, int arrayIndex){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity contract = new Entity("Contract");
		//contract.setPropertiesFrom(e);
		contract.setProperty("user", user);
		//contract.setProperty("hour", hour);
		contract.setProperty("price", price);
		//TODO Check if MemCache needs to be updated
		datastore.put(contract);
	}

	public boolean memCachceUpdate(Entity e){
		//ArrayList<Entity> contracts = (ArrayList<Entity>) syncCache.get(e.getProperty("profile"));
		String [] s = {"asd1","asd2"};
		return false;
		
	}
	
	public void datastoreSaveTransaction(){
		Entity transaction = new Entity("Transaction");
		transaction.setProperty("buyer", "");
		transaction.setProperty("seller", "");
		transaction.setProperty("price", "");
		transaction.setProperty("date", "");
		transaction.setProperty("time", "");			//calendar always 1 hour after this
		transaction.setProperty("ami", "");				//variable - user provided
		transaction.setProperty("instanceType", "");	//micro, large
		transaction.setProperty("region", "");			//usa, jp etc
		transaction.setProperty("zone", "");			//variable
		transaction.setProperty("securityGroup", "");	//user provided or system generated by http and ssh access only
		transaction.setProperty("keyPair", "");
		transaction.setProperty("instanceID", "");
		datastore.put(transaction);
		//the offer is no longer available, update memcache
	}
	
	public String test(){
		//String profile =   AWSCodes.Zone.ASIANE_2A.zone();
		//String profile = generateProfileKey("US_EAST", "US_EAST1A", "Windows","MICRO", new Date());
		createData();
		return "Done";
	}

}
