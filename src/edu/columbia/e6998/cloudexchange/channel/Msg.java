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
	String eKey = null;

	MsgType msg_type = null;
	MsgAction msg_action = null;
	
	
	public enum MsgType{
		UPDATE	("update"),
		REFRESH	("refresh"),
		LAUNCH	("launch"),
		CONFIRM	("confirm");
		
		private final String message;
		
		MsgType(String message){
			this.message = message;
		}
		
		public final String getMessage(){
			return this.message;
		}
		
	}
	
	public enum MsgAction{
		BUY 	("buy"),
		SELL	("sell"),
		CANCEL	("cancel"),
		EMAIL	("email"),
		CONFIRM ("confirm"),
		REFRESH ("refresh"),
		bidOffer ("bidOffer");	//i don't understand this datastore shouldn't be sending message types
		
		private final String action;
		
		MsgAction(String action){
			this.action = action;
		}
		
		public String getAction(){
			return this.action;
		}
		
	}
	
	public Msg(String type, String action, String price, String qty, String key, String eKey) {
		this.type = type;
		this.action = action;
		this.price = price;
		this.qty = qty;
		this.key = key;
		this.eKey = "";
		
		this.msg_type = MsgType.valueOf(type);
		this.msg_action = MsgAction.valueOf(action);
	}
	
	public String toString() {
		return type + " " + action + " " + price + " " + qty + " " + key + " " + eKey;
	}

	public void printString() {
		System.out.println(this.toString());
	}
}