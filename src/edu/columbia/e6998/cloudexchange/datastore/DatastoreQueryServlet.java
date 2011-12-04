package edu.columbia.e6998.cloudexchange.datastore;

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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class DatastoreQueryServlet extends HttpServlet {
       private DatastoreService datastore =
DatastoreServiceFactory.getDatastoreService();
       private MemcacheService syncCache =
MemcacheServiceFactory.getMemcacheService();

       @SuppressWarnings("unchecked")
       public void doGet(HttpServletRequest req, HttpServletResponse resp)
                       throws IOException {
               String s = "";
               resp.setContentType("text/plain");
               List<Entity> tmpList = new ArrayList<Entity>();
               Hashtable<String, Entity> hTable = new Hashtable<String, Entity>();

               //createData();

               Query q = new Query("Contract");
               q.addFilter("active", Query.FilterOperator.EQUAL, true);
               q.addSort("price", Query.SortDirection.ASCENDING);
               List<Entity> rows = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

               for(Entity e: rows){
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


               //TODO testing
               int count = 1;
               List<Entity> tmp = (List<Entity>) syncCache.get("1_2_3_20111124");
               for(Object t : tmp){
                       resp.getWriter().println("Count:" + count++ + " Profile:" + 
                    		   (String) ((Entity) t).getProperty("profile"));
               }

               //resp.getWriter().println(s);
       }


       public void createData(){
               DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

               Random rand = new Random();

               String[] user = {"batman", "robin", "bart", "lisa"};
               Boolean[] cType = {true, false};
               int AMI, region, type;
               String date;

               for(int i = 0; i < 100 ; i++){

                       Entity contract = new Entity("Contract");

                        AMI = rand.nextInt(4);
                        region = rand.nextInt(4);
                        type = rand.nextInt(4);
                        date =  "2011"
                                        + String.format("%02d", (rand.nextInt(12) + 1))
                                        + String.format("%02d", (rand.nextInt(28) + 1))
                                        + String.format("%02d", rand.nextInt(24));

                        //TODO hash functions here
                        String profile = AMI + "_" + region + "_" + type + "_" + date;
                        //.substring(0, date.length() - 2);
                        profile = "1_2_3_20111124" + String.format("%02d", rand.nextInt(24));
                        //profile = "1_0_1" + "_" + date;
                        contract.setProperty("profile", profile);
                        contract.setProperty("date", date);
                        //(rand.nextInt(12) + 1) + "/"+ (rand.nextInt(28) + 1) + "/2011 " + rand.nextInt(24) + ":"+ "00");
                        contract.setProperty("qty", rand.nextInt(5) + 1);
                        contract.setProperty("price", rand.nextFloat() + 0.001);
                        contract.setProperty("seller", cType[rand.nextInt(2)]);
                        contract.setProperty("user", user[rand.nextInt(4)]);
                        contract.setProperty("active", true);

                        datastore.put(contract);
               }
       }

}