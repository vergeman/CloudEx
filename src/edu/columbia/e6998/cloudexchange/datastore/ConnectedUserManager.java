package edu.columbia.e6998.cloudexchange.datastore;

import java.util.HashMap;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("unchecked")
public class ConnectedUserManager {
	private HashMap<String, String> users;
	private MemcacheService syncCache;
	final private int cacheExpiry = 7200;
	
	/*TODO:
	 * we may have to do a <userid, username> tuple if we 
	 * want to refer to users in a more robust fashion
	 * but for now we'll just identify by the channel generated userid
	 * 
	 * this is probably racy with object updates - need to 
	 * handle in memcache
	 */
	public ConnectedUserManager() {
		syncCache = MemcacheServiceFactory.getMemcacheService();
		
		if(syncCache.contains("Users")) {
			users = (HashMap<String, String>) syncCache.get("Users");
		}
		else {
			users = new HashMap<String, String>();
			syncCache.put("Users", users, Expiration.byDeltaSeconds(cacheExpiry));
		}
		
	}
	
	
	public void removeUser(String user) {
		users.remove(user);
		syncCache.put("Users", users, Expiration.byDeltaSeconds(cacheExpiry));
	}
	
	public void addUser(String user) {
		users.put(user, user);
		syncCache.put("Users", users, Expiration.byDeltaSeconds(cacheExpiry));
	}
	
	
	public HashMap<String, String> getUsersMap() {
		return users;
	}
	
	public boolean isUserConnected(String user) {
		return users.containsKey(user);
	}
	

	
	
}
