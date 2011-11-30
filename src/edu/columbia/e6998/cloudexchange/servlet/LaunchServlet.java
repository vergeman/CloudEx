package edu.columbia.e6998.cloudexchange.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.columbia.edu.e6998.cloudexchange.aws.testInstance;



@SuppressWarnings("serial")
public class LaunchServlet extends HttpServlet {

	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		/*this just goes through the HW1 sample
		 * launches and terminates an instance
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
	}
}
