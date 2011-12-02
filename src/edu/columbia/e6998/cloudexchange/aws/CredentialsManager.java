package edu.columbia.e6998.cloudexchange.aws;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

/*
 * Wrapper for credentials
 * we'll probably want to add user mgmt 
 * and credentials file saving accordingly
 */
public class CredentialsManager {
	
	AWSCredentials credentials;
	
	public CredentialsManager() throws IOException {
		credentials = new PropertiesCredentials(CredentialsManager.class
				.getResourceAsStream("AwsCredentials.properties"));
	}

	public AWSCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(AWSCredentials credentials) {
		this.credentials = credentials;
	}
	
	
}
