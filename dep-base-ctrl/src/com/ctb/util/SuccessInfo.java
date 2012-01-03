package com.ctb.util;

import java.util.ArrayList;
import java.util.List;


public class SuccessInfo {


	private String key ;
	private List <String> message = new ArrayList <String>();
	private String messageHeader;
	public SuccessInfo(String key) {
		this.key = key;
		
	}
	public SuccessInfo() {
		
	}
	public void updateMessage(String message){
		this.message.add(message);
		
	}
	
	/**
	 * @return the isValidationFailed
	 */
	public boolean isValidationFailed() {
		if(this.key == null)
			return false;
		else
			return true;
	}
	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * @return the messageHeader
	 */
	public String getMessageHeader() {
		return messageHeader;
	}
	
	/**
	 * @param messageHeader the messageHeader to set
	 */
	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}

}