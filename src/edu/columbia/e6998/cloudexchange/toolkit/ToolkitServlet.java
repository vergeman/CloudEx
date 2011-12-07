package edu.columbia.e6998.cloudexchange.toolkit;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

import edu.columbia.e6998.cloudexchange.aws.AWSCodes;




@SuppressWarnings("serial")
public class ToolkitServlet extends HttpServlet {
	
	int NUM_DAYS = 7;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		GenericToolkit asd = new GenericToolkit();
		resp.getWriter().write(asd.test());
		
	
	}

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		GenericToolkit gt = new GenericToolkit();

		String msg_type = req.getParameter("msg");

		
		/*get block*/
		if(msg_type.equals("update")) {
			String region = req.getParameter("data[region]");
			String os = req.getParameter("data[os]");
			String zone = req.getParameter("data[zone]");
			String instance = req.getParameter("data[instance]");
		
			String[] request = {region, zone, os, instance };
			
			ArrayList<String> key_list = new ArrayList<String>(NUM_DAYS);
			ArrayList<String> dates_list = new ArrayList<String>(NUM_DAYS);
			ArrayList<String[][]> contracts_list = new ArrayList<String[][]>(NUM_DAYS);

			JSONArray contracts_jsondata = new JSONArray();

			Populate_Contract_Data(request, key_list, dates_list, 
						contracts_list, contracts_jsondata);

		
			try {
				JSONObject out = new JSONObject();
				out.put("contract_data", contracts_jsondata);
				out.put("dates_data", dates_list);
				resp.getWriter().println(out.toString());
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
			

			
		}
		
		
		
	}
	
	public void Populate_Contract_Data(String[] defaults,
			ArrayList<String> key_list, ArrayList<String> dates_list,
			ArrayList<String[][]> contracts_list, JSONArray contracts_jsondata) {

		Calendar day = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd");

		GenericToolkit gt = new GenericToolkit();

		for (int d = 0; d < NUM_DAYS; d++) {
			String key = gt.generateProfileKey(defaults[0], defaults[1],
					defaults[2], defaults[3], day.getTime());

			System.out.println(key);
			key_list.add(key);
			// TODO: calls some toolkit function to get real array using key
			// TODO: need to prune first day for time (this seems like model
			// logic)
			// TODO: i'm going to separate buyers/sellers, easier to render
			String formatted_date = sdf.format(day.getTime());
			String[][] results = new String[2][24];

			dates_list.add(formatted_date);
			contracts_list.add(results);

			/* json populate */
			try {
				JSONObject contract_hours = new JSONObject();
				contract_hours.put(key, results);
				contracts_jsondata.put(contract_hours);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			day.add(Calendar.DAY_OF_YEAR, 1);
		}

	}
}
