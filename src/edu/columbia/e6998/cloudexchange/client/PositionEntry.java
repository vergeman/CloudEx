package edu.columbia.e6998.cloudexchange.client;

import java.util.Date;

public class PositionEntry {

	public String buyOrSell;
	public String date;
	public String ami;
	public String instance;
	public String region;
	public String zone;
	public Double contractPrice;
	public Double spotPrice;
	
	public PositionEntry () {
		
	}
	
	public String getBuyOrSell() {
		return this.buyOrSell;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getAmi() {
		return this.ami;
	}
	
	public String getRegion() {
		return this.region;
	}
	
	public String getZone() {
		return this.zone;
	}
	
	public Double getSpotPrice() {
		return this.spotPrice;
	}
	
	public Double getContractPrice() {
		return this.contractPrice;
	}
	
	public String getInstance() {
		return this.instance;
	}
}
