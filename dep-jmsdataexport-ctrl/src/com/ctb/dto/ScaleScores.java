package com.ctb.dto;

import com.ctb.utils.EmetricUtil;




public class ScaleScores 
{

	private String speaking = "";
	private String listening= "";
	private String reading= "";
	private String writing= "";
	private String overall= "";
	private String comprehension= "";
	private String oral= "";

	public String getSpeaking() {
		return speaking;
	}

	public void setSpeaking(String speaking) {
		this.speaking = speaking;
	}

	public String getListening() {
		return listening;
	}

	public void setListening(String listening) {
		this.listening = listening;
	}

	public String getReading() {
		return reading;
	}

	public void setReading(String reading) {
		this.reading = reading;
	}

	public String getWriting() {
		return writing;
	}

	public void setWriting(String writing) {
		this.writing = writing;
	}

	public String getOverall() {
		return overall;
	}

	public void setOverall(String overall) {
		this.overall = overall;
	}

	public String getComprehension() {
		return comprehension;
	}

	public void setComprehension(String comprehension) {
		this.comprehension = comprehension;
	}

	public String getOral() {
		return oral;
	}

	public void setOral(String oral) {
		this.oral = oral;
	}
	
	public String toString(){
		String val="";
		val += EmetricUtil.getFormatedString(speaking, 3)
		+EmetricUtil.getFormatedString(listening, 3) 
		+EmetricUtil.getFormatedString(reading, 3) 
		+EmetricUtil.getFormatedString(writing, 3) 
		+EmetricUtil.getFormatedString(overall, 3) 
		+EmetricUtil.getFormatedString(comprehension, 3) 
		+EmetricUtil.getFormatedString(oral, 3) ;

		return val;
		
	}
	
	
}