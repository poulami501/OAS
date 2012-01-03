package com.ctb.bean.dataExportManagement; 

import com.ctb.bean.CTBBean;
import java.util.Date;
import java.util.List;

/**
 * Data bean representing the partial contents of the OAS.STUDENT table 
 * and the student's assigned org nodes.
 * 
 * @author John_Wang
 */
public class ManageStudent extends CTBBean
{ 
    static final long serialVersionUID = 1L;
    private Integer id;
    private String loginId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String studentName;
    private String studentIdNumber;
    private String studentIdNumber2;
    private String grade;
    private String productNameList;
    private String gender;
    private String scoringStatus;
    private Date birthDate;
    private Integer createdBy;
    private String extElmId;
    private Integer rosterId;
    private String testSessionName;
    private Integer  itemSetIdTC;
    private String accessCode;
    private String testAdminId;
    private Integer itemCountCRAI;
    private String studentUserName;
    private String exportStatus;
    private String testCompletionStatus;
    
    
    private String testPurpose;
    
    
	/**
	 * @return the testPurpose
	 */
	public String getTestPurpose() {
		return testPurpose;
	}
	/**
	 * @param testPurpose the testPurpose to set
	 */
	public void setTestPurpose(String testPurpose) {
		this.testPurpose = testPurpose;
	}
	/**
	 * @return the accessCode
	 */
	public String getAccessCode() {
		return accessCode;
	}
	/**
	 * @param accessCode the accessCode to set
	 */
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}
	
	/**
	 * @return the testSessionName
	 */
	public String getTestSessionName() {
		return testSessionName;
	}
	/**
	 * @param testSessionName the testSessionName to set
	 */
	public void setTestSessionName(String testSessionName) {
		this.testSessionName = testSessionName;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}
	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	/**
	 * @return the studentName
	 */
	public String getStudentName() {
		return studentName;
	}
	/**
	 * @param studentName the studentName to set
	 */
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	/**
	 * @return the grade
	 */
	public String getGrade() {
		return grade;
	}
	
	public String getProductNameList() {
		return productNameList;
	}
	public void setProductNameList(String productNameList) {
		this.productNameList = productNameList;
	}
	/**
	 * @param grade the grade to set
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public String getScoringStatus() {
		return scoringStatus;
	}
	/**
	 * @param scoringStatus the scoringStatus to set
	 */
	public void setScoringStatus(String scoringStatus) {
		this.scoringStatus = scoringStatus;
	}
	/**
	 * @return the studentIdNumber
	 */
	public String getStudentIdNumber() {
		return studentIdNumber;
	}
	/**
	 * @param studentIdNumber the studentIdNumber to set
	 */
	public void setStudentIdNumber(String studentIdNumber) {
		this.studentIdNumber = studentIdNumber;
	}
	/**
	 * @return the studentIdNumber2
	 */
	public String getStudentIdNumber2() {
		return studentIdNumber2;
	}
	/**
	 * @param studentIdNumber2 the studentIdNumber2 to set
	 */
	public void setStudentIdNumber2(String studentIdNumber2) {
		this.studentIdNumber2 = studentIdNumber2;
	}
	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}
	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}    
	/**
	 * @return Returns the createdBy.
	 */
	public Integer getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy The createdBy to set.
	 */
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the extElmId
	 */
	public String getExtElmId() {
		return extElmId;
	}
	/**
	 * @param extElmId the extElmId to set
	 */
	public void setExtElmId(String extElmId) {
		this.extElmId = extElmId;
	}
	/**
	 * @return Return RosterId
	 */
	public Integer getRosterId() {
		return rosterId;
	}
	/**
	 * @param rosterId - rosterId
	 */
	public void setRosterId(Integer rosterId) {
		this.rosterId = rosterId;
	}
	/**
	 * @return the itemSetIdTC
	 */
	public Integer getItemSetIdTC() {
		return itemSetIdTC;
	}
	/**
	 * @param itemSetIdTC the itemSetIdTC to set
	 */
	public void setItemSetIdTC(Integer itemSetIdTC) {
		this.itemSetIdTC = itemSetIdTC;
	}
	/**
	 * @return the testAdminId
	 */
	public String getTestAdminId() {
		return testAdminId;
	}
	/**
	 * @param testAdminId the testAdminId to set
	 */
	public void setTestAdminId(String testAdminId) {
		this.testAdminId = testAdminId;
	}
	/**
	 * @return the itemCountCRAI
	 */
	public Integer getItemCountCRAI() {
		return itemCountCRAI;
	}
	/**
	 * @param itemCountCRAI the itemCountCRAI to set
	 */
	public void setItemCountCRAI(Integer itemCountCRAI) {
		this.itemCountCRAI = itemCountCRAI;
	}
	/**
	 * @return the studentUserName
	 */
	public String getStudentUserName() {
		return studentUserName;
	}
	/**
	 * @param studentUserName the studentUserName to set
	 */
	public void setStudentUserName(String studentUserName) {
		this.studentUserName = studentUserName;
	}
	/**
	 * @return the exportStatus
	 */
	public String getExportStatus() {
		return exportStatus;
	}
	/**
	 * @param exportStatus the exportStatus to set
	 */
	public void setExportStatus(String exportStatus) {
		this.exportStatus = exportStatus;
	}
	/**
	 * @return the testCompletionStatus
	 */
	public String getTestCompletionStatus() {
		return testCompletionStatus;
	}
	/**
	 * @param testCompletionStatus the testCompletionStatus to set
	 */
	public void setTestCompletionStatus(String testCompletionStatus) {
		this.testCompletionStatus = testCompletionStatus;
	}
    
} 
