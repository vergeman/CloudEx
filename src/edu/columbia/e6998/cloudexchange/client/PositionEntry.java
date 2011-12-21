package edu.columbia.e6998.cloudexchange.client;

import java.util.Date;

public class PositionEntry {

	public String buyOrSell;
	public Date date;
	public String ami;
	public String instance;
	public String region;
	public String zone;
	public Double contractPrice;
	public Double bidAskPrice;
	public Double spotPrice;
	
	public PositionEntry () {
		
	}
}
