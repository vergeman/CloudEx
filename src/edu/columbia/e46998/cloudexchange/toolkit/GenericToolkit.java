package edu.columbia.e46998.cloudexchange.toolkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.servlet.http.*;

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
	
	public String generateProfileKey(){
		
		Random rand = new Random(); 

		int AMI, region, type;
		String date;
		
		AMI = rand.nextInt(4);
		region = rand.nextInt(4);
		type = rand.nextInt(4);
		date =  "2011" 
				+ String.format("%02d", (rand.nextInt(12) + 1))
				+ String.format("%02d", (rand.nextInt(28) + 1));
				//+ String.format("%02d", rand.nextInt(24));

		//TODO hash functions here
		String profile = AMI + "_" + region + "_" + type + "_" + date;//.substring(0, date.length() - 2);
		profile = "1_2_3_20111124" + String.format("%02d", rand.nextInt(24));
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
			int pInt = ((String) e.getProperty("profile")).length();
			String memKey = ((String) e.getProperty("profile")).substring(0, pInt - 2 );
			
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
		List<Entity> tmp = (List<Entity>) syncCache.get("1_2_3_20111124");
		for(Object t : tmp){
			System.out.println("Count:" + count++ + " Profile:" + (String) ((Entity) t).getProperty("profile"));
		}
		System.out.println(s);
	}
	
	public void createData(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String[] user = {"batman", "robin", "bart", "lisa"};
		Boolean[] cType = {true, false};
		int AMI, region, type;
		String date;

		Random rand = new Random(); 

		AMI = rand.nextInt(4);
		region = rand.nextInt(4);
		type = rand.nextInt(4);
		date =  "2011" 
				+ String.format("%02d", (rand.nextInt(12) + 1))
				+ String.format("%02d", (rand.nextInt(28) + 1))
				+ String.format("%02d", rand.nextInt(24));

		//TODO hash functions here
		String profile = AMI + "_" + region + "_" + type + "_" + date;//.substring(0, date.length() - 2);
		
		for(int i = 0; i < 100 ; i++){
		
			Entity contract = new Entity("Contract");
			

			 //profile = "1_0_1" + "_" + date;
			 contract.setProperty("profile", profile);
			 contract.setProperty("date", date);//(rand.nextInt(12) + 1) + "/" + (rand.nextInt(28) + 1) + "/2011 " + rand.nextInt(24) + ":"+ "00");
			 contract.setProperty("qty", rand.nextInt(5) + 1);
			 contract.setProperty("price", rand.nextFloat() + 0.001);
			 contract.setProperty("seller", cType[rand.nextInt(2)]);
			 contract.setProperty("user", user[rand.nextInt(4)]);
			 contract.setProperty("active", true);

			 datastore.put(contract);
			 
		}
	}
	
	public void insertNewOffer(Entity e, String user, int hour, Double price){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity contract = new Entity("Contract");
		contract.setPropertiesFrom(e);
		contract.setProperty("user", user);
		contract.setProperty("hour", hour);
		contract.setProperty("price", price);
		//TODO Check if MemCache needs to be updated
		datastore.put(contract);
	}

	public boolean memCachceUpdate(Entity e){
		//ArrayList<Entity> contracts = (ArrayList<Entity>) syncCache.get(e.getProperty("profile"));
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
		datastore.put(transaction);
		//the offer is no longer available, update memcache
	}
	
	
}