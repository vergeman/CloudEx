package edu.columbia.e6998.cloudexchange.toolkit;
import edu.columbia.e6998.cloudexchange.aws.AWSCodes;
import edu.columbia.e6998.cloudexchange.channel.Msg;


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
		return "Done";
	}
	
	public Entity[] queryDataStore(String optProfile){

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
		if (!optProfile.equals("")){
			qSeller.addFilter("profile", FilterOperator.EQUAL, optProfile);
			syncCache.delete(optProfile);
			removeProfile(optProfile);
			}
		qSeller.addSort("price", Query.SortDirection.DESCENDING);

		List<Entity> rSellers = datastore.prepare(qSeller).asList(FetchOptions.Builder.withDefaults());
		for(Entity e: rSellers){
			memKey = (String) e.getProperty("profile");
			tmpList = (Entity[]) syncCache.get(memKey);
			index = hourToIndex(((String) e.getProperty("hour")), ((Boolean) e.getProperty("seller")));
			if (e!=null)
				//System.out.println("index:" + index + e.toString());			
			if (tmpList != null  && !deletes.contains(e.getKey())){
				tmpList[index] = e;
			}else{
				tmpList = new Entity[48];
				tmpList[index] = e;
			}
			addProfile(memKey);
			//syncCache.put(memKey, tmpList);
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
			if (e!=null)
				//System.out.println("index:" + index + e.toString());
			if (tmpList != null && !deletes.contains(e.getKey())){
					tmpList[index] = e;
			}else{
				tmpList = new Entity[48];
				tmpList[index] = e;
			}
			
			addProfile(memKey);

		}
		
		syncCache.put(memKey, tmpList);		
		return tmpList;
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
//		System.out.println(	"CreateBidOffer::" + 
//							profile + " " + 
//							price + " " + 
//							arrayIndex + " " + 
//							indexToHour(arrayIndex));
		datastore.put(contract);
		return updateMemcache((Boolean) contract.getProperty("seller"), profile, arrayIndex, (Entity[]) syncCache.get(profile), contract);
		
	}
	
	public Msg createTransaction(String profile, String arrayIndex, String buyer, String ami, String securityGroupName, String keyPairName){
		
		Entity offer = ((Entity[])syncCache.get(profile))[Integer.valueOf(arrayIndex)];

		String[] lookup = reverseLookUpProfile(profile);
		
		Entity transaction = new Entity("Transaction");
		transaction.setProperty("profile",profile);
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
		transaction.setProperty("instanceID", 		"N/A");
		transaction.setProperty("priceExecuted", 	"N/A");
		
		datastore.put(transaction);
		Entity[] mem = deleteBidOffer(profile, arrayIndex);
		for(Entity e: mem){
			if(e!=null)
				System.out.println(e.toString());
		}
		System.out.println("delete done");
		return updateMemcache((Boolean) offer.getProperty("seller"), profile, arrayIndex, mem, null);
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
		transaction.setProperty("ami", 	"ami-8c1fece5"); //variable - user provided
		transaction.setProperty("instanceType", "t1.micro");	//micro, large
		transaction.setProperty("region", "US East");//usa, jp etc
		transaction.setProperty("zone", "us-east-1a");	//variable
		transaction.setProperty("securityGroup", "NewSecurityGroup");	//user provided or system generated by http and ssh access only
		transaction.setProperty("keyPair",	"MyKeyPair");
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
	
	// Return e-mail addresses for buyer - String[0], and seller - String[1]
	public String[] getBuyerSellerMailForTransaction(Key key) {
		String[] mails = new String[2];
		try {
			Entity transaction = datastore.get(key);
			String buyerId = (String) transaction.getProperty("buyer");
			String sellerId = (String) transaction.getProperty("seller");
			mails[0] = (String) getUserProfileForUser(buyerId).getProperty("email");
			mails[1] = (String) getUserProfileForUser(sellerId).getProperty("email");
		} catch (EntityNotFoundException e) {
			System.err.print(e.getMessage());
		}
		return mails;
	}
	
	private Entity[] deleteBidOffer(String profile, String arrayIndex){
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
			return null;
		}
		
		addDelete(e.getKey().toString());
		return queryDataStore(profile); 
	}

	public String profileToKey(String profile, String arrayIndex){
		return profile + arrayIndex;
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
			return (h*2)+1;
		else
			return (h*2);
	}
	
	private Msg updateMemcache(Boolean flag, String profile, String arrayIndex, Entity[] cache, Entity e){
		Entity[] tmpList = null;
		Entity m = null;
		int index = Integer.valueOf(arrayIndex);

//		System.out.println("updateMemCache::1");
		//check if this is post create transaction
		if(e==null){
			if(cache==null){
				System.out.println("Something is not right!!!!!!");
				//TODO return something very ugly to teach them a lesson
				return null;
			}else
				m = cache[index];
				if(m!= null)
					return sendChannelMessage("UPDATE", 
							"bidOffer", 
							String.valueOf((Double) m.getProperty("price")), 
							"1", 
							profile,
							String.valueOf(index));
				else{
					System.out.println("No next best bid/offer");
					return sendChannelMessage("UPDATE", 
							"bidOffer", 
							"", 
							"1", 
							profile,
							String.valueOf(index));
				}
		}
		
		if(cache!=null){
			tmpList = cache;
			m = tmpList[index];
		}
		
		if(m==null){
			//Simple insert
			tmpList = new Entity[48];
			tmpList[index] = e;
			syncCache.put(profile, tmpList);
			return sendChannelMessage("UPDATE", 
					"bidOffer", 
					String.valueOf((Double) e.getProperty("price")), 
					"1", 
					profile,
					String.valueOf(index));
		}

		System.out.println("updateMemCache::4");
		if((Boolean) m.getProperty("seller")){
			//compare
			System.out.println("updateMemCache::5");
			if((Double) m.getProperty("price") <= (Double) e.getProperty("price"))
				return null;
		}else{
			System.out.println("updateMemCache::6");
			if((Double) m.getProperty("price") >= (Double) e.getProperty("price"))
				return null;
		}
		
		System.out.println("updateMemCache::7");
		tmpList[index] = e;
		syncCache.put(profile, tmpList);
		return sendChannelMessage("UPDATE", 
				"bidOffer", 
				String.valueOf((Double) e.getProperty("price")), 
				"1", 
				(String) e.getProperty("profile"), String.valueOf(index));
		
	}
	
	
	
		
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
