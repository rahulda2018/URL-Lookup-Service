package com.java.app.urllookupservice.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class URL {
	@Id
	private String hostName;
	private String fullURL;
	private int severity;
	private String description;
	//private String modifiedDate;

	public static int BLACKLIST = 1;
	public static int WHITELIST = 0;
	
	public URL() {
		//Default constructor
	}
	
	public URL(String hostName, String fullURL, int severity, String description/*, String modifiedDate*/) {
		this.hostName = hostName;
		this.fullURL = fullURL;
		this.severity = severity;
		this.description = description;
		//this.modifiedDate = modifiedDate;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}


	public String getFullURL() {
		return fullURL;
	}

	public void setFullURL(String fullURL) {
		this.fullURL = fullURL;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	/*
	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	*/
	
}
