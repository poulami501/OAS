package com.ctb.testSessionInfo.dto; 

import java.util.ArrayList;

import com.ctb.bean.testAdmin.Node;
import com.ctb.bean.testAdmin.RosterElement;
import com.ctb.testSessionInfo.utils.FilterSortPageUtils;

public class TestRosterVO implements java.io.Serializable 
{
    static final long serialVersionUID = 1L;

    private Integer testRosterId = null;
    private Integer studentId = null;
    private Integer orgNodeId = null;
    private String firstName = null;
    private String lastName = null;
    private String studentNumber = null;
    private String loginName = null;
    private String password = null;
    private String validationStatus = null;
    private String testStatus = null;
    private String customerFlagStatus = null;
    private String scoringStatus = null;
    private String dnsStatus = null;
    private String assignedForm = null; 
    private String hasAccommodations;
    private String orgNodeName;
    private ArrayList<Node> orgList;
    
    public TestRosterVO()
    {
    }
    public TestRosterVO(RosterElement rElt)
    {
        this.testRosterId = rElt.getTestRosterId();
        this.studentId = rElt.getStudentId();
        this.firstName = rElt.getFirstName();
        this.lastName = rElt.getLastName();
        this.studentNumber = rElt.getExtPin1();
        this.loginName = rElt.getUserName();
        this.password = rElt.getPassword();
        this.validationStatus = rElt.getValidationStatus();
        this.testStatus = rElt.getTestCompletionStatus();
        this.customerFlagStatus = rElt.getCustomerFlagStatus();
        this.scoringStatus = rElt.getScoringStatus();
        this.dnsStatus =  rElt.getDnsStatus();
        this.assignedForm = rElt.getAssignedForm();
        this.hasAccommodations = rElt.getHasAccommodations();
        this.orgNodeName = rElt.getOrgNodeName();
        this.orgList = rElt.getOrgList();
        this.orgNodeId = rElt.getOrgNodeId();
    }
    public String getDnsStatus() {
		return dnsStatus;
	}
	public void setDnsStatus(String dnsStatus) {
		this.dnsStatus = dnsStatus;
	}
	public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public Integer getTestRosterId() {
        return testRosterId;
    }
    public void setTestRosterId(Integer id) {
        this.testRosterId = id;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getLoginName() {
        return loginName;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getStudentId() {
        return studentId;
    }
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    public String getStudentNumber() {
        return studentNumber;
    }
    public void setStudentId(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    public String getTestStatus() {
        return FilterSortPageUtils.testStatus_CodeToString(testStatus);
    }
    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }
    public String getValidationStatus() {
        return FilterSortPageUtils.validationStatus_CodeToString(validationStatus);
    }
    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }
    public String getcustomerFlagStatus() {
        return customerFlagStatus;
    }
    public void setcustomerFlagStatus(String customerFlagStatus) {
        this.customerFlagStatus = customerFlagStatus;
    }
    public String getScoringStatus() {
        return scoringStatus;
    }
    public void setScoringStatus(String scoringStatus) {
        this.scoringStatus = scoringStatus;
    }
	public String getAssignedForm() {
		return assignedForm;
	}
	public void setAssignedForm(String assignedForm) {
		this.assignedForm = assignedForm;
	}
	/**
	 * @return the hasAccommodations
	 */
	public String getHasAccommodations() {
		return hasAccommodations;
	}
	/**
	 * @param hasAccommodations the hasAccommodations to set
	 */
	public void setHasAccommodations(String hasAccommodations) {
		this.hasAccommodations = hasAccommodations;
	}
	/**
	 * @return the orgNodeName
	 */
	public String getOrgNodeName() {
		return orgNodeName;
	}
	/**
	 * @param orgNodeName the orgNodeName to set
	 */
	public void setOrgNodeName(String orgNodeName) {
		this.orgNodeName = orgNodeName;
	}
	/**
	 * @return the orgList
	 */
	public ArrayList<Node> getOrgList() {
		return orgList;
	}
	/**
	 * @param orgList the orgList to set
	 */
	public void setOrgList(ArrayList<Node> orgList) {
		this.orgList = orgList;
	}
	/**
	 * @return the orgNodeId
	 */
	public Integer getOrgNodeId() {
		return orgNodeId;
	}
	/**
	 * @param orgNodeId the orgNodeId to set
	 */
	public void setOrgNodeId(Integer orgNodeId) {
		this.orgNodeId = orgNodeId;
	}
    
    
} 
