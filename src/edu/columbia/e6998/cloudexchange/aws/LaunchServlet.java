package edu.columbia.e6998.cloudexchange.aws;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.columbia.e6998.cloudexchange.aws.spotprices.SpotPriceManager;




@SuppressWarnings("serial")
public class LaunchServlet extends HttpServlet {

	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		/*this just goes through the HW1 sample
		 * launches and terminates an instance
		 * 
		 * should probably be done in the form of a task queue
		 * 
		 * you will need your AwsCredentials.properties file in the aws folder 
		 * for now - we can adapt to use datastore
		 * just wanted to get the library working
		 * 
		 * note:
		 * add the third party libs
		 * common-codec, commons-logging, jackson-1.4 to your build path AND 
		 * BOOTSTRAP CLASSPATH
		 * (Run As -> Run Configurations -> Add Jars
		 * 
		 * */
		
		
		//we comment this out for now since our cron job hits it every hour
		//but it works on deploy
		
		
		//testInstance t = new testInstance(resp);
		SpotPriceManager sm = new SpotPriceManager(new CredentialsManager());

		String price = sm.getSpotprice("0000000020110101");
		
		System.out.println(price);
	}
}
