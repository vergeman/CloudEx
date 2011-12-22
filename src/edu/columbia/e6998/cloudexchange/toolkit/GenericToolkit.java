package edu.columbia.e6998.cloudexchange.toolkit;
import edu.columbia.e6998.cloudexchange.aws.AWSCodes;
import edu.columbia.e6998.cloudexchange.channel.Msg;
import edu.columbia.e6998.cloudexchange.client.UserProfile;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.google.appengine.api.users.UserServiceFactory;

public class GenericToolkit {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	private static GenericToolkit instance = null;
	
	private GenericToolkit() {
		
	}
	
	public static GenericToolkit getInstance() {
		if (instance == null) {
			instance = new GenericToolkit();
		}
		return instance;
	}

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
		//TODO fix unchecked
		@SuppressWarnings("unchecked")
		ArrayList<String> profiles = (ArrayList<String>) syncCache.get("Profiles");
		if (profiles == null)
			return null;
		if(profiles.remove(profile));
			syncCache.put("Profiles", profiles);
		return profiles;
	}

	private ArrayList<String> addProfile(String profile){
		//TODO fix unchecked
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
	
	private ArrayList<String> removeDelete(String delete){
		//TODO fix unchecked
		@SuppressWarnings("unchecked")
		ArrayList<String> deletes = (ArrayList<String>) syncCache.get("Deletes");
		if (deletes == null)
			return null;
		if(deletes.remove(delete));
			syncCache.put("Deletes", deletes);
		return deletes;
	}

	private ArrayList<String> addDelete(String delete){
		//TODO fix unchecked
		@SuppressWarnings("unchecked")
		ArrayList<String> deletes = (ArrayList<String>) syncCache.get("Deletes");
		if (deletes == null)
			deletes = new ArrayList<String>();
		if (deletes.contains(delete))
			return deletes;

		deletes.add(delete);
		syncCache.put("Deletes", deletes);
		return deletes;

	}
	
	public ArrayList<String> getDeletes(){
		return removeDelete("xxx");
	}
	
	
	public String[][] getBidsOffers(String profile){
		String[][] results = new String[2][24];
		
		if(!syncCache.contains("Profiles"))
			queryDataStore(profile);
		
		//can't iterate through null list (but can an empty list..)
		if (!syncCache.contains(profile))
			return results;	
		
		for(Entity t : (Entity[]) syncCache.get(profile)){
			if (t!= null){
				if ((Boolean) t.getProperty("seller"))
					results[1][Integer.parseInt(((Entity) t).getProperty("hour").toString())] = ((Entity) t).getProperty("price").toString();
				else
					results[0][Integer.parseInt(((Entity) t).getProperty("hour").toString())] = ((Entity) t).getProperty("price").toString();
			}
		}

		return results;
	}
	
	public String queryDataStore(){
		return queryDataStore("");
	}
	
	public String queryDataStore(String optProfile){
		//TODO can be made into single i/o batch
		String s = "";
		Entity[] tmpList = new Entity[48];
		String memKey = "";
		int index = 0;
		List<String> deletes = new ArrayList<String>();
		deletes = getDeletes();
		if (deletes==null)
			deletes = Collections.emptyList();
		
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
			
			if (tmpList != null  && !deletes.contains(e.getKey())){
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
			
			if (tmpList != null && !deletes.contains(e.getKey())){
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

	public Msg createBidOffer(String profile, double price, String user, String arrayIndex){
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
		contract.setProperty("seller", 			Integer.valueOf(arrayIndex)%2 != 0);
		contract.setProperty("active", 			true);
		System.out.println(	"CreateBidOffer::" + 
							profile + " " + 
							price + " " + 
							arrayIndex + " " + 
							indexToHour(arrayIndex));
		datastore.put(contract);
		if (updateMemcache(contract))
			return sendChannelMessage("UPDATE", "bidOffer", String.valueOf(price), "1", profile, arrayIndex);
		else
			return null;
	}
	
	public Msg createTransaction(String profile, String arrayIndex, String buyer, String ami, String securityGroupName, String keyPairName){
		
		Entity offer = ((Entity[])syncCache.get(profile))[Integer.valueOf(arrayIndex)];
//		for(int i =0; i< 48; i++){
//			if(offer[i]!=null)
//				System.out.println("\nfound: " + i);
//		}
//		if(!syncCache.contains(profile)){
//			System.out.println("not found: " + profile);
//			return "no";
//		}
//		
//		if (offer==null){
//			System.out.println("not found: " + profile + " index: " + index);
//			return "no";
//		}
		String[] lookup = reverseLookUpProfile(profile);
		
		Entity transaction = new Entity("Transaction");
		transaction.setProperty("profile",profile);
		transaction.setProperty("buyer", buyer);
		transaction.setProperty("seller", 			offer.getProperty("user"));
		transaction.setProperty("is_buy", 			offer.getProperty("seller"));
		transaction.setProperty("price", 			offer.getProperty("price"));
		transaction.setProperty("date", 			offer.getProperty("date"));
		transaction.setProperty("time", 			indexToHour(arrayIndex));	//calendar always 1 hour after this
	//	transaction.setProperty("ami", 				ami);			//variable - user provided
		transaction.setProperty("instanceType", 	lookup[INSTANCE_TYPE]);	//micro, large
		transaction.setProperty("region", 			lookup[REGION]);//usa, jp etc
		transaction.setProperty("zone", 			lookup[ZONE]);	//variable
	//	transaction.setProperty("securityGroup", 	securityGroupName);	//user provided or system generated by http and ssh access only
	//	transaction.setProperty("keyPair",			keyPairName);
		transaction.setProperty("instanceID", 		"N/A");
		transaction.setProperty("priceExecuted", 	"N/A");
		
		datastore.put(transaction);
		deleteBidOffer(profile, arrayIndex);
		//return "read_memcache::" + profile + "_" + arrayIndex;
		return sendChannelMessage("update", "transaction", (String) offer.getProperty("price"), "1", profile, indexToHour(arrayIndex));
	}
	
	public String createTestTransaction(String date, String time) {
		
		Entity transaction = new Entity("Transaction");
		transaction.setProperty("profile", "0000000020110101");
		transaction.setProperty("buyer", "114224896744063045840"); // fedotoveugene@gmail.com
		transaction.setProperty("seller", "110709289717792221869"); // 
		transaction.setProperty("is_buy", true);
		transaction.setProperty("price",  0.05);
		transaction.setProperty("date",  date); // date in MMM-dd format
		transaction.setProperty("time",  time); // time in 24-hour format (16 = 4pm)
		//transaction.setProperty("ami", 	"ami-8c1fece5"); //variable - user provided
		transaction.setProperty("instanceType", "t1.micro");	//micro, large
		transaction.setProperty("region", "US East");//usa, jp etc
		transaction.setProperty("zone", "us-east-1a");	//variable
		//transaction.setProperty("securityGroup", "NewSecurityGroup");	user provided or system generated by http and ssh access only
		//transaction.setProperty("keyPair",	"MyKeyPair");
		transaction.setProperty("instanceID",  "N/A"); // will be populated once the instance is launched
		transaction.setProperty("priceExecuted", "N/A"); // actual spot price - will be populated once the instance is lanched
		datastore.put(transaction);
		return "put into datastore";
	}
	
	// Get the list of transaction for which we have not yet launched an instance
	public List<Entity> getOpenTransactions() {
		List<Entity> openTransactions = null;
		Query q = new Query("Transaction");
		q.addFilter("instanceID", Query.FilterOperator.EQUAL, "N/A");
		openTransactions = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		return openTransactions;
	}
	
	// update transaction entry once the instance is launched
	public void updateTransaction(Key key, String propertyName, String value) {
		try {
			Entity transaction = datastore.get(key);
			transaction.setProperty(propertyName, value);
			datastore.put(transaction);
		} catch (EntityNotFoundException e) {
			System.err.print(e.getMessage());
		}
	}
	
	// Get user profile entity for a specified userId
	public Entity getUserProfileForUser(String userId) {
		Query q = new Query("UserProfile");
		q.addFilter("userId", Query.FilterOperator.EQUAL, userId);
		Entity userProfile = datastore.prepare(q).asSingleEntity();
		return userProfile;
	}
	
	// update user profile property
	public void updateUserProfile(String userId, String propertyName, String value) {
		try {
			Query q = new Query("UserProfile");
			q.addFilter("userId", Query.FilterOperator.EQUAL, userId);
			Entity userProfile = datastore.prepare(q).asSingleEntity();
			userProfile.setProperty(propertyName, value);
			datastore.put(userProfile);
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
	
	// Return e-mail addresses for buyer - String[0], and seller - String[1]
	public UserProfile[] getBuyerSellerProfileForTransaction(Key key) {
		UserProfile[] profiles = new UserProfile[2];
		try {
			Entity transaction = datastore.get(key);
			String buyerId = (String) transaction.getProperty("buyer");
			String sellerId = (String) transaction.getProperty("seller");
			String buyerEmail = (String) getUserProfileForUser(buyerId).getProperty("email");
			String sellerEmail = (String) getUserProfileForUser(sellerId).getProperty("email");
			profiles[0] = new UserProfile(buyerId, buyerEmail);
			profiles[1] = new UserProfile(sellerId, sellerEmail);
		} catch (EntityNotFoundException e) {
			System.err.print(e.getMessage());
		}
		return profiles;
	}
	
	// Create charge for the seller
	public void createCharge(Key key, String instanceId, String userId, String amount, String type) {
		Entity charge = new Entity("Charge");
		charge.setProperty("transactionKey", key);
		charge.setProperty("type", type);
		charge.setProperty("userId", userId);
		charge.setProperty("amount", amount);
		datastore.put(charge);
	}
	
	// Return all charges for a given user
	public List<Entity> getChargesForUser(String userId) {
		Query q = new Query("Charge");
		q.addFilter("userId", Query.FilterOperator.EQUAL, userId);
		List<Entity> charges = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		return charges;
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
		
		addDelete(e.getKey().toString());
		queryDataStore(profile);
		return "read_memcache::" + profile + "_" + arrayIndex;
	}

	public String profileToKey(String profile, String arrayIndex){
		return profile + "_" + arrayIndex;
	}
	
	private Msg sendChannelMessage(String type, String action, String value, String qty, String profile, String arrayIndex){
		//Msg msg =  new Msg(type, action, value, qty, profile);
		int i = Integer.valueOf(arrayIndex);
		
		Msg msg = new Msg(type, action, value, "1", profile + String.format("%d", i));
		return msg;
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
	
	private boolean updateMemcache(Entity e){
		Entity[] tmpList;
		Entity m;
		int index = hourToIndex((String) e.getProperty("hour"), (Boolean) e.getProperty("seller"));
		String profile = (String) e.getProperty("profile");
		//first check if profile exists - if not, add it!!!!
//		System.out.println("updateMemCache::1");
		if(!syncCache.contains(profile)){
			tmpList = new Entity[48];
			tmpList[index] = e;
			syncCache.put(profile, tmpList);
			return true;
		}else
			tmpList = (Entity[]) syncCache.get(profile);
		
//		System.out.println("updateMemCache::2");
		if (tmpList==null)
			return false;
//		System.out.println("updateMemCache::3");
		m = tmpList[index];
		
		if(m==null){
			tmpList[index] = e;
			syncCache.put(profile, tmpList);
			return true;
		}

//		System.out.println("updateMemCache::4");
		if((Boolean) m.getProperty("seller")){
			//compare
//			System.out.println("updateMemCache::5");
			if((Double) m.getProperty("price") <= (Double) e.getProperty("price"))
				return false;
		}else{
//			System.out.println("updateMemCache::6");
			if((Double) m.getProperty("price") >= (Double) e.getProperty("price"))
				return false;
		}
//		System.out.println("updateMemCache::7");
		tmpList[index] = e;
		syncCache.put(profile, tmpList);
		return true;
		
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
		String s = "\n";
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
//		s+="Query Data Store:\n";
//		s+= dumpMemCache();
//		s+= "\n";
//
//		s+= createTransaction("0000000020111216", "01", "joker", "ami", "SG", "KP");
//		s+= "\n";
//		s+= "After Buy:\n";
//		s+= dumpMemCache();
//		for(int i = 0; i <= 100000; i++){
//			//do nottin mon
//		}
//			
//		s+="After sell:\n";
//		s+= dumpMemCache();
//		s+= indexToHour("46");

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
	
//	public void createData(String profile){
//		Random r = new Random();
//		r.setSeed(7/9);
//		String[] user = {"batman", "robin", "joker", "lisa"};
//		String[] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
//				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20,",
//				"21", "22", "23"};
//		if(syncCache.get(profile) == null)
//			createBidOffer(profile, r.nextDouble() + 0.001, user[r.nextInt(4)], hours[r.nextInt(24)]);
//	}
	
}
