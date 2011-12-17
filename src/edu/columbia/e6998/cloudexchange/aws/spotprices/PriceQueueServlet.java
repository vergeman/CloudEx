package edu.columbia.e6998.cloudexchange.aws.spotprices;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskAlreadyExistsException;

import edu.columbia.e6998.cloudexchange.aws.CredentialsManager;
import edu.columbia.e6998.cloudexchange.datastore.ConnectedUserManager;


@SuppressWarnings("serial")
public class PriceQueueServlet extends HttpServlet {
	private static final int DEFER_TIME = 300000;
	/*
	 * this gets hit when we want to update prices
	 * it runs an offline SpotPrice update (downloading if necssary)
	 * and passes a message to the Channel to update
	 */
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		System.out.println("[PriceQueueServlet]");	
		String profile = req.getParameter("profile");
		
		CredentialsManager creds = new CredentialsManager();
		SpotPriceManager spm = new SpotPriceManager(creds);
		
		String price = spm.getSpotprice(profile);
		
		
		ConnectedUserManager cm = new ConnectedUserManager();
		if (cm.getUsersMap().size() != 0) {

			/*build message and forward it to channel...for some reason seems
			 * like messaging is broken at the moement so i'll wait
			 * for the breaker to fix it
			 */
			
			//build another taskqueue to repeat
			try {
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(withUrl("/marketprice").taskName(profile).param("profile", profile).countdownMillis(DEFER_TIME));
			}
			catch(TaskAlreadyExistsException e) {}
		}
		
		
		
	}
}
