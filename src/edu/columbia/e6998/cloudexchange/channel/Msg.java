package edu.columbia.e6998.cloudexchange.channel;

/* internal class for messages*/
/* we will probably add the validations to this class
 * validity will be handled (on the client first)
 * but also as a property of a recieved msg
 * SC - made it its own class, datastore needs it, in m r you dont inherit the msg, msg inherits you
 */
public class Msg {
	String type = null;
	String action = null;
	String price = null;
	String qty = null;
	String key = null;

	public Msg(String type, String action, String price, String qty, String key) {
		this.type = type;
		this.action = action;
		this.price = price;
		this.qty = qty;
		this.key = key;
	}
	
	public String toString() {
		return type + " " + action + " " + price + " " + qty + " " + key;
	}

	public void printString() {
		System.out.println(this.toString());
	}
}