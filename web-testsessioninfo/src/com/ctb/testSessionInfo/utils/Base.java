package com.ctb.testSessionInfo.utils; 

import java.util.List;

import com.ctb.testSessionInfo.dto.TestSessionVO;



public class Base {
	
	private String page;
	private String total;
	private String records;
	private List<Row> rows;
	private List<TestSessionVO> testSessionCUFU;
	private List<TestSessionVO> testSessionPA;
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getRecords() {
		return records;
	}
	public void setRecords(String records) {
		this.records = records;
	}
	public List<Row> getRows() {
		return rows;
	}
	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	/**
	 * @return the testSessionCUFU
	 */
	public List<TestSessionVO> getTestSessionCUFU() {
		return testSessionCUFU;
	}
	/**
	 * @param testSessionCUFU the testSessionCUFU to set
	 */
	public void setTestSessionCUFU(List<TestSessionVO> testSessionCUFU) {
		this.testSessionCUFU = testSessionCUFU;
	}
	/**
	 * @return the testSessionPA
	 */
	public List<TestSessionVO> getTestSessionPA() {
		return testSessionPA;
	}
	/**
	 * @param testSessionPA the testSessionPA to set
	 */
	public void setTestSessionPA(List<TestSessionVO> testSessionPA) {
		this.testSessionPA = testSessionPA;
	}

}
