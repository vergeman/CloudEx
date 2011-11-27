package edu.columbia.e6998.cloudexchange.mailer;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class MailManager {
	private String _msgBody;
	private String _from;
	private String _from_name;
	private String _to;
	private String _to_name;
	private String _subject;
	private Message _msg;

	
	public MailManager(String from, String from_name, 
			String to, String to_name, 
			String subject, String msgBody) {
		this(from, from_name, to, to_name, subject);
		this._msgBody = msgBody;
	}
	

	public MailManager(String from, String from_name, String to,
			String to_name, String subject) {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		_msg = new MimeMessage(session);
		
		_from = from;
		_from_name = from_name;

		_to = to;
		_to_name = to_name;

		_subject = subject;
	}
	
	public void Send() {

		try {

			_msg.setFrom(new InternetAddress(_from, _from_name));
			_msg.addRecipient(Message.RecipientType.TO, 
					new InternetAddress(_to, _to_name));
			_msg.setSubject(_subject);
			_msg.setText(_msgBody);
			Transport.send(_msg);

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getMsgBody() {
		return _msgBody;
	}

	public void setMsgBody(String msgBody) {
		this._msgBody = msgBody;
	}

	public String get_from() {
		return _from;
	}

	public void set_from(String _from) {
		this._from = _from;
	}

	public String get_from_name() {
		return _from_name;
	}

	public void set_from_name(String _from_name) {
		this._from_name = _from_name;
	}

	public String get_to() {
		return _to;
	}

	public void set_to(String _to) {
		this._to = _to;
	}

	public String get_to_name() {
		return _to_name;
	}

	public void set_to_name(String _to_name) {
		this._to_name = _to_name;
	}

	public String get_subject() {
		return _subject;
	}

	public void set_subject(String _subject) {
		this._subject = _subject;
	}

	public void PrintMessage() {
		System.out.println(_msgBody);
	}
	
}

