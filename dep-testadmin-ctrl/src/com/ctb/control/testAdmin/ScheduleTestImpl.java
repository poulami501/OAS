package com.ctb.control.testAdmin; 

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javax.naming.InitialContext;

import org.apache.beehive.controls.api.bean.ControlImplementation;

import com.ctb.bean.request.FilterParams;
import com.ctb.bean.request.PageParams;
import com.ctb.bean.request.SortParams;
import com.ctb.bean.request.FilterParams.FilterParam;
import com.ctb.bean.request.FilterParams.FilterType;
import com.ctb.bean.request.SortParams.SortParam;
import com.ctb.bean.request.SortParams.SortType;
import com.ctb.bean.request.testAdmin.FormAssignmentCount;
import com.ctb.bean.testAdmin.CustomerConfigurationValue;
import com.ctb.bean.testAdmin.EditCopyStatus;
import com.ctb.bean.testAdmin.Node;
import com.ctb.bean.testAdmin.OrgNodeStudent;
import com.ctb.bean.testAdmin.ProctorAssignment;
import com.ctb.bean.testAdmin.RosterElement;
import com.ctb.bean.testAdmin.ScheduleElement;
import com.ctb.bean.testAdmin.ScheduledSession;
import com.ctb.bean.testAdmin.ScheduledStudentDetailsWithManifest;
import com.ctb.bean.testAdmin.SchedulingStudent;
import com.ctb.bean.testAdmin.SchedulingStudentData;
import com.ctb.bean.testAdmin.SessionStudent;
import com.ctb.bean.testAdmin.SessionStudentData;
import com.ctb.bean.testAdmin.Student;
import com.ctb.bean.testAdmin.StudentAccommodations;
import com.ctb.bean.testAdmin.StudentAccommodationsData;
import com.ctb.bean.testAdmin.StudentData;
import com.ctb.bean.testAdmin.StudentManifest;
import com.ctb.bean.testAdmin.StudentManifestData;
import com.ctb.bean.testAdmin.StudentNode;
import com.ctb.bean.testAdmin.StudentNodeData;
import com.ctb.bean.testAdmin.StudentSessionStatus;
import com.ctb.bean.testAdmin.StudentSubtestAssignment;
import com.ctb.bean.testAdmin.TABERecommendedLevel;
import com.ctb.bean.testAdmin.TestElement;
import com.ctb.bean.testAdmin.TestElementData;
import com.ctb.bean.testAdmin.TestProduct;
import com.ctb.bean.testAdmin.TestProductData;
import com.ctb.bean.testAdmin.TestSession;
import com.ctb.bean.testAdmin.User;
import com.ctb.bean.testAdmin.UserData;
import com.ctb.bean.testAdmin.UserNode;
import com.ctb.bean.testAdmin.UserNodeData;
import com.ctb.exception.CTBBusinessException;
import com.ctb.exception.request.InvalidFilterFieldException;
import com.ctb.exception.testAdmin.CustomerConfigurationDataNotFoundException;
import com.ctb.exception.testAdmin.InsufficientLicenseQuantityException;
import com.ctb.exception.testAdmin.InvalidNoOfProgramsException;
import com.ctb.exception.testAdmin.ManifestUpdateFailException;
import com.ctb.exception.testAdmin.NotEditableManifestException;
import com.ctb.exception.testAdmin.OrgNodeDataNotFoundException;
import com.ctb.exception.testAdmin.ProductDataNotFoundException;
import com.ctb.exception.testAdmin.RosterDataNotFoundException;
import com.ctb.exception.testAdmin.SessionCreationException;
import com.ctb.exception.testAdmin.SessionDeletionException;
import com.ctb.exception.testAdmin.StudentDataNotFoundException;
import com.ctb.exception.testAdmin.StudentNotAddedToSessionException;
import com.ctb.exception.testAdmin.TestAdminDataNotFoundException;
import com.ctb.exception.testAdmin.TestElementDataNotFoundException;
import com.ctb.exception.testAdmin.TransactionTimeoutException;
import com.ctb.exception.testAdmin.UserDataNotFoundException;
import com.ctb.exception.validation.ValidationException;
import com.ctb.util.SimpleCache;
import com.ctb.util.request.Filterer;
import com.ctb.util.testAdmin.AccessCodeGenerator;
import com.ctb.util.testAdmin.PasswordGenerator;
import com.ctb.util.testAdmin.TestAdminStatusComputer;
import com.ctb.util.testAdmin.TestFormSelector;

/**
 * Platform control provides functions related to test session
 * scheduling, including methods to obtain lists of
 * products available for scheduling, tests within a product,
 * and subtests available in a test, methods for persisting test
 * session, roster, and student accomodation information, 
 * and support methods for browsing org, student and user hierarchies
 * 
 * @author Nate_Cohen, John_Wang
 * @editor-info:code-gen control-interface="true"
 */
@ControlImplementation(isTransient=true)
public class ScheduleTestImpl implements ScheduleTest
{ 
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.StudentItemSetStatus studentItemSetStatus;

    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.testAdmin.CustomerConfigurations customerConfigurations;

    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.TestAdminUserRole taur;


    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.Product product;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.ItemSet itemSet;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.OrgNode orgNode;
     
    /**
    * @common:control
    */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.OrgNodeStudent orgNodeStudent;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.Students students;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.Users users;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.StudentAccommodation accommodation;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.TestAdmin admins;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.TestAdminItemSet tais;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.StudentItemSetStatus siss;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.TestRoster rosters;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.testAdmin.FormAssignment formAssignments;

    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.validation.Validator validator;

    static final long serialVersionUID = 1L;
    
    private static final int CTB_CUSTOMER_ID =2;
    
    static final ResourceBundle rb = ResourceBundle.getBundle("security");
    

    /**
     * Retrieves a filtered, sorted, paged list of products which the specified user is able to schedule tests for.
     * Each product contains a list of the unique set of levels of the tests within that product.
     * and a list of the unique set of grades of the tests within that product
     * @common:operation
     * @param userName - identifies the user
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return TestProductData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public TestProductData getTestProductsForUser(String userName, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
    	//Attempt to improve performance
        //validator.validate(userName, null, "testAdmin.getTestProductsForUser");
        try {
            TestProductData tpd = new TestProductData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            tpd.setTestProducts(product.getProductsForUser(userName), pageSize);
            if(filter != null) tpd.applyFiltering(filter);
            if(sort != null) tpd.applySorting(sort);
            if(page != null) tpd.applyPaging(page);
            /*
            TestProduct [] products = tpd.getTestProducts();
            for(int i=0;i<products.length && products[i] != null;i++) {
                TestProduct prod = products[i];
                prod.setLevels(itemSet.getLevelsForProduct(prod.getProductId()));
                prod.setGrades(itemSet.getGradesForProduct(prod.getProductId()));
            }
            */
            return tpd;
        } catch (SQLException se) {
            ProductDataNotFoundException pde = new ProductDataNotFoundException("ScheduleTestImpl: getTestProductsForUser: " + se.getMessage());
            pde.setStackTrace(se.getStackTrace());
            throw pde;
        }
    }
    
    /**
     * Retrieves a filtered, sorted, paged list of products which the specified user is able to schedule tests for.
     * Each product contains a list of the unique set of levels of the tests within that product.
     * and a list of the unique set of grades of the tests within that product
     * @common:operation
     * @param userName - identifies the user
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return TestProductData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public TestProductData getTestCatalogForUser(String userName, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
    	//Attempt to improve performance
        //validator.validate(userName, null, "testAdmin.getTestProductsForUser");
        try {
            TestProductData tpd = new TestProductData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            tpd.setTestProducts(product.getTestCatalogForUser(userName), pageSize);
            if(filter != null) tpd.applyFiltering(filter);
            if(sort != null) tpd.applySorting(sort);
            if(page != null) tpd.applyPaging(page);
            /*
            TestProduct [] products = tpd.getTestProducts();
            for(int i=0;i<products.length && products[i] != null;i++) {
                TestProduct prod = products[i];
                prod.setLevels(itemSet.getLevelsForProduct(prod.getProductId()));
                prod.setGrades(itemSet.getGradesForProduct(prod.getProductId()));
            }
            */
            return tpd;
        } catch (SQLException se) {
            ProductDataNotFoundException pde = new ProductDataNotFoundException("ScheduleTestImpl: getTestProductsForUser: " + se.getMessage());
            pde.setStackTrace(se.getStackTrace());
            throw pde;
        }
    }
    
    
    /**
     * Retrieves a filtered, sorted, paged list of tests under the specified product.
     * Each testElement object also contains a overrideFormAssigmentMethod field, which
     * if populated (see TestSession.FormAssignment constants) indicates that
     * only the specified assignment method should be available for scheduling, and a
     * overrideLoginStartDate field, which if populated indicates that no earlier (than the override date) 
     * login start date can be chosen for sessions of this test.
     * @common:operation
     * @param userName - identifies the user
     * @param productId - identifies the product
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return TestElementData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public TestElementData getTestsForProduct(String userName, Integer productId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
    	//Attempt to improve performance
        //validator.validateProduct(userName, productId, "testAdmin.getTestsForProduct");
    	//commented, since product is already validated while checking for license.
        try {
            TestElementData ted = new TestElementData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            ted.setTestElements(itemSet.getTestsForProduct(userName, productId), pageSize);
            if(filter != null) ted.applyFiltering(filter);
            if(sort != null) ted.applySorting(sort);
            if(page != null) ted.applyPaging(page);
            TestElement [] tests = ted.getTestElements();
            for(int i=0;i<tests.length && tests[i] != null;i++) {
                TestElement test = tests[i];
                test.setForms(itemSet.getFormsForTest(test.getItemSetId()));
            }
            return ted;
        } catch (SQLException se) {
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: getTestsForProduct: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    private static class TestElementCacheObject {
        private TestElement[] testElements;
    }
    
    /**
     * Retrieves a filtered, sorted, paged list of independantly schedulable units of the specified test. 
     * Each element returned contains a randomly generated "suggested" test access code for that
     * unit.
     * @common:operation
	 * @param userName - identifies the user
     * @param testItemSetId - identifies the test
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return TestElementData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public TestElementData getSchedulableUnitsForTest(String userName, Integer testItemSetId, Boolean generateAccessCodes, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateItemSet(userName, testItemSetId, "testAdmin.getSchedulableUnitsForTest");
        try {
            String [] forms = itemSet.getFormsForTest(testItemSetId);
            TestElementData ted = new TestElementData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            String cacheKey = String.valueOf(testItemSetId) + "|TS";
            TestElementCacheObject cacheObj = (TestElementCacheObject) SimpleCache.checkCache5min("TEST_ELEMENT", cacheKey);
            if(cacheObj == null) {
                cacheObj = new TestElementCacheObject();
                cacheObj.testElements = itemSet.getTestElementsForParent(testItemSetId, "TS");
                SimpleCache.cacheResult("TEST_ELEMENT", cacheKey, cacheObj);
            }
            ted.setTestElements(cacheObj.testElements, pageSize);
            if(filter != null) ted.applyFiltering(filter);
            if(sort != null) ted.applySorting(sort);
            if(page != null) ted.applyPaging(page);
            if(generateAccessCodes.booleanValue() == true) {
            TestElement [] subtests = ted.getTestElements();
                HashMap accessCodeHashmap = new HashMap();
                for(int i=0;i<subtests.length && subtests[i] != null;i++) {
                    TestElement subtest = subtests[i];
                    subtest.setForms(forms);
                    boolean validCode = false;
                    String code = null;
                    while(!validCode) {
                        code = AccessCodeGenerator.generateAccessCode();
                        if (!accessCodeHashmap.containsKey(code))
                            validCode = admins.getTestAdminsByAccessCode(code).length == 0;
                    }
                    accessCodeHashmap.put(code, code);
                    subtest.setAccessCode(code);
                }
            }
            return ted;
        } catch (SQLException se) {
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: getSchedulableUnitsForTest: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
        
    }
    
    
    public TestElementData getSchedulableUnitsForTestWithBlankAccessCode(String userName, Integer testItemSetId, Boolean generateAccessCodes, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateItemSet(userName, testItemSetId, "testAdmin.getSchedulableUnitsForTest");
        try {
            String [] forms = itemSet.getFormsForTest(testItemSetId);
            TestElementData ted = new TestElementData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            String cacheKey = String.valueOf(testItemSetId) + "|TS";
            TestElementCacheObject cacheObj = (TestElementCacheObject) SimpleCache.checkCache5min("TEST_ELEMENT", cacheKey);
            if(cacheObj == null) {
                cacheObj = new TestElementCacheObject();
                cacheObj.testElements = itemSet.getTestElementsForParent(testItemSetId, "TS");
                SimpleCache.cacheResult("TEST_ELEMENT", cacheKey, cacheObj);
            }
            ted.setTestElements(cacheObj.testElements, pageSize);
            if(filter != null) ted.applyFiltering(filter);
            if(sort != null) ted.applySorting(sort);
            if(page != null) ted.applyPaging(page);
            if(generateAccessCodes.booleanValue() == true) {
            TestElement [] subtests = ted.getTestElements();
                HashMap accessCodeHashmap = new HashMap();
                for(int i=0;i<subtests.length && subtests[i] != null;i++) {
                    TestElement subtest = subtests[i];
                    subtest.setForms(forms);
                    /*boolean validCode = false;
                    String code = "";
                    subtest.setAccessCode(code);*/
                }
            }
            return ted;
        } catch (SQLException se) {
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: getSchedulableUnitsForTest: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
        
    }
    
    
    public List<String> getFixedNoAccessCode(int count) throws CTBBusinessException
    {
    	HashMap accessCodeHashmap = new HashMap();
    	List<String> accessCodeList = new ArrayList<String>(count);
    	int genCount = 0;
    	try{
    		while(count>genCount) {
        		boolean validCode = false;
                String code = null;
                while(!validCode) {
                    code = AccessCodeGenerator.generateAccessCode();
                    if (!accessCodeHashmap.containsKey(code))
                        validCode = admins.getTestAdminsByAccessCode(code).length == 0;
                }
                genCount++;
                accessCodeList.add(code);
                accessCodeHashmap.put(code, code);
        		
        	}
        	return accessCodeList;
    	}catch (SQLException se) {
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: getFixedNoAccessCode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    	
    	
    }
    
    /**
     * Retrieves a filtered, sorted, paged list of the unique student grade/accommodation
     * option sets of all students in orgs visible to the current user.
     * @common:operation
     * @param userName - identifies the user
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentAccommodationsData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentAccommodationsData getAccommodationOptionsForUser(String userName, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        //validator.validate(userName, null, "testAdmin.getAccommodationOptionsForUser");
        try {
            StudentAccommodationsData sad = new StudentAccommodationsData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            sad.setStudentAccommodations(accommodation.getUniqueStudentAccommodationsForUser(userName), pageSize);
            if(filter != null) sad.applyFiltering(filter);
            if(sort != null) sad.applySorting(sort);
            if(page != null) sad.applyPaging(page);
            return sad;
        } catch (SQLException se) {
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: getAccommodationOptionsForUser: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged subset of students in the provided list
     * of students who have been scheduled for any administration of the test corresponding
     * to the provided testItemSetId. The resulting list can be used to prevent
     * scheduling of these students in additional sessions of the same test.
     * @common:operation
     * @param userName - identifies the user
     * @param studentList - a list of students to check
     * @param testItemSetId - identifies the test (TC item_set_id) to check against
     * @param testAdminId - identifies the current test admin id for scheduling (excluded for restriction)
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return SessionStudentData
	 * @throws com.ctb.exception.CTBBusinessException
     */

    public SessionStudentData getRestrictedStudentsForTest(String userName, SessionStudent[] studentList, Integer testItemSetId, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateItemSet(userName, testItemSetId, "testAdmin.getRestrictedStudentsForTest");
        try {
            SessionStudentData std = new SessionStudentData();
            SessionStudent [] sstudents = studentList;
            ArrayList badStudentList = new ArrayList(); 
            for(int i=0;i<sstudents.length;i++) {
                // if this is a restricted test, student can only be added
                // if they haven't tested before
                Integer restrictedAdmin = students.isTestRestrictedForStudent(userName, sstudents[i].getStudentId(), testItemSetId);
                if (restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    if (testAdminId == null || !restrictedAdmin.equals(testAdminId)) {
                    	SessionStudent stdd = sstudents[i];
                    	if(stdd.getFirstName() == null) {
                    		stdd = students.getStudent(sstudents[i].getStudentId());
                    	}
                        badStudentList.add(stdd);
                        EditCopyStatus status = new EditCopyStatus();
                        status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                        TestSession ts = admins.getTestAdminDetails(restrictedAdmin);  ///
                        TestAdminStatusComputer.adjustSessionTimesToLocalTimeZone(ts);  ///
                        status.setPriorSession(ts);
                        stdd.setStatus(status);
                    }
                }
            }
            std.setSessionStudents((SessionStudent [])badStudentList.toArray(new SessionStudent[0]), null);
            if(filter != null) std.applyFiltering(filter);
            if(sort != null) std.applySorting(sort);
            if(page != null) std.applyPaging(page);
            return std;
        } catch (SQLException se) {
            StudentDataNotFoundException tee = new StudentDataNotFoundException("ScheduleTestImpl: getRestrictedStudentsForTest: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of students at the specified org node.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentData getStudentsForOrgNode(String userName, Integer orgNodeId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getStudentsForOrgNode");
        try {
            StudentData std = new StudentData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            std.setStudents(students.getStudentsForOrgNode(orgNodeId), pageSize);
            if(filter != null) std.applyFiltering(filter);
            if(sort != null) std.applySorting(sort);
            if(page != null) std.applyPaging(page);
            return std;
        } catch (SQLException se) {
            StudentDataNotFoundException tee = new StudentDataNotFoundException("ScheduleTestImpl: getStudentsForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of students at the specified org node.
     * Each returned SchedulingStudent object also contains grade and accommodations data,
     * and an editable flag indicating whether or not the student can be scheduled for
     * the test corresponding to the provided testItemSetId. If the editable flag is false,
     * the editableCause field contains a description of the reason the student is non-editable.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testItemSetId - identifies the TC item set of the test being scheduled
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentData
	 * @throws com.ctb.exception.CTBBusinessException
	 */
    public SchedulingStudentData getSchedulingStudentsForOrgNode(String userName, Integer orgNodeId, Integer testItemSetId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getSchedulingStudentsForOrgNode");
        try {
            SchedulingStudentData std = new SchedulingStudentData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            SchedulingStudent [] scstudents = students.getSchedulingStudentsForOrgNode(orgNodeId, userName, testItemSetId, new Integer(-1));
            for(int i=0;i<scstudents.length;i++) {
                // if this is a restricted test, student can only be added
                // if they haven't tested before
                Integer restrictedAdmin = scstudents[i].getPriorAdmin();
                EditCopyStatus status = scstudents[i].getStatus();
                status.setEditable("T");
                status.setCopyable("T");
                if (restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    status.setEditable("F");
                    status.setCopyable("F");
                    status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                    status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                }
            }
            std.setSchedulingStudents(scstudents, pageSize);
            if(filter != null) std.applyFiltering(filter);
            if(sort != null) std.applySorting(sort);
            if(page != null) std.applyPaging(page);
            return std;
        } catch (SQLException se) {
            StudentDataNotFoundException tee = new StudentDataNotFoundException("ScheduleTestImpl: getSchedulingStudentsForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of students at the specified org node.
     * Each returned SessionStudent object also contains grade and accommodations data,
     * and if testAdminId is not null, assigned form and scheduled org node name are also
     * included, and the editable flag indicates ("T" or "F") whether or not the student
     * can be removed from or otherwise manipulated within the session. If the editable flag is false,
     * the editableCause field contains a description of the reason the student is non-editable. Only students who
     * are in the specified test admin are returned by this call. If all students in the org
     * node are desired, use getSessionStudentsForOrgNode instead. If testAdminId is null, 
     * this call behaves the same as getSchedulingStudentsForOrgNode() but returns 
     * SessionStudent objects.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testAdminId - identifies the test session
     * @param testItemSetId - identifies the TC item set of the test being scheduled
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public SessionStudentData getSessionStudentsForOrgNodeAndAdmin(String userName, Integer orgNodeId, Integer testAdminId, Integer testItemSetId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getSessionStudentsForOrgNode");
        try {
            SessionStudentData std = new SessionStudentData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            SessionStudent [] sstudents = null;
            if(testAdminId == null) {
                SessionStudent [] scstudents = students.getSchedulingStudentsForOrgNode(orgNodeId, userName, testItemSetId, new Integer(-1));;
                sstudents= new SessionStudent[scstudents.length];
                for(int i=0;i<scstudents.length;i++) {
                    // if this is a restricted test, student can only be added
                    // if they haven't tested before
                    Integer restrictedAdmin = scstudents[i].getPriorAdmin();
                    EditCopyStatus status = scstudents[i].getStatus();
                    status.setEditable("T");
                    status.setCopyable("T");
                    if (restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                        status.setEditable("F");
                        status.setCopyable("F");
                        status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                        status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                    }
                    sstudents[i] = scstudents[i];
                }
            } else {
                sstudents = students.getSessionStudentsForOrgNode(orgNodeId, testAdminId);
                for(int i=0;i<sstudents.length;i++) {
                    // if this is a restricted test, student can only be added
                    // if they haven't tested before
                    Integer restrictedAdmin = students.isTestRestrictedForStudentAndAdmin(userName, sstudents[i].getStudentId(), testItemSetId, testAdminId);
                    EditCopyStatus status = sstudents[i].getStatus();
                    status.setEditable("T");
                    status.setCopyable("T");
                    if (restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                        status.setEditable("F");
                        status.setCopyable("F");
                        status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                        status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                    }
                    boolean editableByUser = students.isStudentEditableByUserForAdminAndOrg(userName, sstudents[i].getStudentId(), testAdminId, orgNodeId).intValue() > 0;
                    if(!editableByUser) {
                        status.setEditable("F");
                        status.setCopyable("F");
                        status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                    }
                }
            }
            std.setSessionStudents(sstudents, pageSize);
            if(filter != null) std.applyFiltering(filter);
            if(sort != null) std.applySorting(sort);
            if(page != null) std.applyPaging(page);
            return std;
        } catch (SQLException se) {
            StudentDataNotFoundException tee = new StudentDataNotFoundException("ScheduleTestImpl: getSessionStudentsForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    private static class SchedulingStudentCacheObject {
        private SchedulingStudent [] scstudents;
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of students at the specified org node.
     * Each returned SessionStudent object also contains grade and accommodations data,
     * and if testAdminId is not null, assigned form and scheduled org node name are also
     * included, and the editable flag indicates ("T" or "F") whether or not the student
     * can be removed from or otherwise manipulated within the session. If the editable flag is false,
     * the editableCause field contains a description of the reason the student is non-editable. All students in the
     * specified org node are always returned, even when testAdminId is provided.
     * If testAdminId is null, this call behaves the same as getSchedulingStudentsForOrgNode()
     * but returns SessionStudent objects.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testAdminId - identifies the test session
     * @param testItemSetId - identifies the TC item set of the test being scheduled
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public SessionStudentData getSessionStudentsForOrgNode(String userName, Integer orgNodeId, Integer testAdminId, Integer testItemSetId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getSessionStudentsForOrgNode");
        String searchOrder = " ";
        String searchFilter = " ";
               
        
        searchOrder = getOrderString(sort);
        System.out.println("searchOrder:"+searchOrder);
        if(filter!=null){
        	searchFilter = getFilterString(filter);
        }
        System.out.println("searchFilter :"+searchFilter);
        
        searchOrder = " main "+ searchFilter +  searchOrder;
        System.out.println("Final String:"+searchOrder);
        try {
            SessionStudentData std = new SessionStudentData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            SessionStudent [] sstudents = null;
            if(testAdminId == null) testAdminId = new Integer(-1);
            //String cacheKey = String.valueOf(orgNodeId) + "|" + String.valueOf(testItemSetId) + "|" + String.valueOf(testAdminId);
            //SchedulingStudentCacheObject cacheObj = (SchedulingStudentCacheObject) SimpleCache.checkCache5min("SESSION_STUDENTS", cacheKey);
            //if(cacheObj == null) {
            //    cacheObj = new SchedulingStudentCacheObject();
            //    cacheObj.scstudents = students.getSchedulingStudentsForOrgNode(orgNodeId, userName, testItemSetId, testAdminId);
            //    SimpleCache.cacheResult("SESSION_STUDENTS", cacheKey, cacheObj);
            //}
            //SchedulingStudent [] scstudents = cacheObj.scstudents;
            SessionStudent [] scstudents = students.getSchedulingStudentsForOrgNodeByOrder(orgNodeId, userName, testItemSetId, testAdminId, searchOrder);
            sstudents = new SessionStudent[scstudents.length];
            for(int i=0;i<scstudents.length;i++) {
                sstudents[i] = scstudents[i];
                // if this is a restricted test, student can only be added
                // if they haven't tested before
                Integer restrictedAdmin = scstudents[i].getPriorAdmin();
                EditCopyStatus status = scstudents[i].getStatus();
                status.setEditable("T");
                status.setCopyable("T");
                if (restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    status.setEditable("F");
                    status.setCopyable("F");
                    status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                    status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                }
                if(testAdminId != null && testAdminId.intValue() != -1) {
                    boolean editable = students.isStudentEditableByUserForAdminAndOrg(userName, sstudents[i].getStudentId(), testAdminId, orgNodeId).intValue() > 0 ? true : false;
                    if(!editable) {
                        status.setEditable("F");
                        status.setCopyable("F");
                        status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                    }
                    RosterElement existingRoster = rosters.getRosterElementForStudentAndAdmin(sstudents[i].getStudentId(), testAdminId);
                    if(existingRoster != null) {
                        boolean oldEditable = students.isStudentEditableByUserForAdminAndOrg(userName, sstudents[i].getStudentId(), testAdminId, existingRoster.getOrgNodeId()).intValue() > 0 ? true : false;
                        if(!oldEditable) {
                            status.setEditable("F");
                            status.setCopyable("F");
                            status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                            if("CO".equals(existingRoster.getTestCompletionStatus())) {
                                status.setCode(EditCopyStatus.StatusCode.SESSION_COMPLETED);
                            } else if(!"SC".equals(existingRoster.getTestCompletionStatus()) &&
                                        !"NA".equals(existingRoster.getTestCompletionStatus()) ) {
                                status.setCode(EditCopyStatus.StatusCode.SESSION_IN_PROGRESS);
                            }                        
                        }
                    }
                }
            }
            std.setSessionStudents(sstudents, pageSize);
            /*if(filter != null) std.applyFiltering(filter);
            if(sort != null) std.applySorting(sort); */
            if(page != null) std.applyPaging(page);
            return std;
        } catch (SQLException se) {
            StudentDataNotFoundException tee = new StudentDataNotFoundException("ScheduleTestImpl: getSessionStudentsForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
     
    private String getOrderString(SortParams sort) {
    	 String searchOrder = " order by upper(lastName)";
        if (sort != null){
        	SortParam [] params = sort.getSortParams();
        	
        	if(params!=null && params.length>0) {
        		SortParam  param = params[0];
        		SortType type = param.getType();
        		String field = param.getField();
        		if (field.equalsIgnoreCase("FirstName")){
        			searchOrder = " order by upper(firstName) ";
        		} else if (field.equalsIgnoreCase("LastName")){
        			searchOrder = " order by upper(lastName) ";
        		} else if (field.equalsIgnoreCase("MiddleName")){
        			searchOrder = " order by upper(middleName) ";
        		} else if (field.equalsIgnoreCase("Grade")){
        			searchOrder = " order by grade ";
        		} else if (field.equalsIgnoreCase("Calculator")){
        			searchOrder = " order by calculator ";
        		} else if (field.equalsIgnoreCase("HasColorFontAccommodations")){
        			searchOrder = " order by hasColorFontAccommodations ";
        		} else if (field.equalsIgnoreCase("TestPause")){
        			searchOrder = " order by testPause ";
        		} else if (field.equalsIgnoreCase("ScreenReader")){
        			searchOrder = " order by screenReader ";
        		} else if (field.equalsIgnoreCase("UntimedTest")){
        			searchOrder = " order by untimedTest ";
        		} else if (field.equalsIgnoreCase("UntimedTest")){
        			searchOrder = " order by untimedTest ";
        		}
        		
        		if ((type.equals(SortType.ALPHADESC)) || (type.equals(SortType.NUMDESC))){
        			searchOrder += " desc ";
        		}
        	}
        	
        } 
		return searchOrder;
	}

	private String getFilterString(FilterParams params) throws InvalidFilterFieldException {
		String filterString=" ";
		boolean isWhereAdded= false;
		boolean isAndAdd= true;
		int gradeCount = 0;
		
		
		ArrayList <String> list = new ArrayList<String>();
		
		for( FilterParam param : params.getFilterParams()) {
            try {
              	String operation = "=";
            	FilterType operationType = param.getType(); 
            	if(operationType.equals(FilterType.NOTEQUAL)){
            		operation = " != ";
            	}else if(operationType.equals(FilterType.EQUALS)){
            		operation = " = ";
            	} else {
            		throw new Exception("Filter operation ["+operationType.getType()+ "] not supported.");
            	}
            		
            	
            	if (param.getField().equalsIgnoreCase("StudentGrade")){
            		list.add(" grade "+ operation +" '"+(param.getArgument())[0]+"' ");
            		gradeCount++;
            	}else if (param.getField().equalsIgnoreCase("Calculator")){
            		list.add(" calculator "+ operation +" '"+(param.getArgument())[0]+"' ");
            	}else if (param.getField().equalsIgnoreCase("HasColorFontAccommodations")){
            		list.add(" hasColorFontAccommodations "+ operation +" '"+(((String)(param.getArgument())[0]).equalsIgnoreCase("true")? "t":"f")+"'");	
            	}else if (param.getField().equalsIgnoreCase("TestPause")){
					list.add(" testPause "+ operation +" '"+(param.getArgument())[0]+"' ");	
            	}else if (param.getField().equalsIgnoreCase("UntimedTest")){
					list.add(" untimedTest "+ operation +" '"+(param.getArgument())[0]+"' ");	
            	}else if (param.getField().equalsIgnoreCase("ScreenReader")){
            		list.add(" screenReader "+ operation +" '"+(param.getArgument())[0]+"' ");
            	} else 
            		throw new Exception("Search filter ["+param.getField()+ "] not found.");
               
            } catch (Exception e) {
                InvalidFilterFieldException ife = new InvalidFilterFieldException(e.getMessage());
                ife.setStackTrace(e.getStackTrace());
                throw ife;
            }
        }
		
		if (gradeCount > 1) {
			String value = "(";
			for (int i=list.size()-1 ; i>=0 ; i--) {
				String val = list.get(i);
				int index = val.indexOf("grade");
				if (index >= 0) {
					value += val;
					gradeCount--;
					if (gradeCount >= 1) {
						value += " or ";
					}
					list.remove(i);
				}
			}
			value += ")";
			list.add(value);			
		}
		
		for( String val : list){
			if (!isWhereAdded){
				filterString += " where ";
				isAndAdd = false;
        		isWhereAdded= true;
			} else {
        		isAndAdd = true;
        	}
        	if(isAndAdd){
        		filterString += " and ";
        	}
        	filterString+=val;
		}
		
		
		return filterString;
	}

	/**
     * Retrieves a sorted, filtered, paged list of users at the specified org node.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return UserData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public UserData getUsersForOrgNode(String userName, Integer orgNodeId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getUsersForOrgNode");
        try {
            UserData usd = new UserData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            usd.setUsers(users.getUsersForOrgNode(orgNodeId), pageSize);
            if(filter != null) usd.applyFiltering(filter);
            if(sort != null) usd.applySorting(sort);
            if(page != null) usd.applyPaging(page);
            return usd;
        } catch (SQLException se) {
            UserDataNotFoundException tee = new UserDataNotFoundException("ScheduleTestImpl: getUsersForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    /**
     * Retrieves a list of child org nodes of the specified org node
     * <br/><br/>Each node contains two counts: the number of students rostered
     * in the specified admin which are at or below the specified node (rosterCount),
     * and the number of students matching the grade and student test
     * accommodation filter criteria which are at or below the specified node (studentCount).
     * If no test admin id is specified, only the student count is populated, not
     * the roster count.
     * <br/><br/>The filter params passed to this call only affect the student
     * count, not the set of org nodes returned, whereas the sort and paging params
     * affect the org node list, as usual.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testAdminId - identifies the test admin
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentNodeData getStudentNodesForParentAndAdmin(String userName, Integer orgNodeId, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getStudentNodesForParentAndAdmin");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getOrgNodesByParent(orgNodeId);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            studentNodes = ond.getStudentNodes();
            
            for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {   
            	/*
                StudentAccommodationsData cachedAccommData = (StudentAccommodationsData) SimpleCache.checkCache("STUDENT_ACCOMMODATIONS_BY_ORG", String.valueOf(studentNodes[i].getOrgNodeId()));
                // copy cached data to new object before filtering
                StudentAccommodationsData accommData = new StudentAccommodationsData();
                if(cachedAccommData != null && cachedAccommData.getStudentAccommodations() != null) {
                    StudentAccommodations[] saArray = new StudentAccommodations[cachedAccommData.getStudentAccommodations().length];
                    for(int j=0;j<saArray.length;j++) {
                        saArray[j] = cachedAccommData.getStudentAccommodations()[j];
                    }
                    accommData.setStudentAccommodations(saArray, null);
                    if(filter != null) accommData.applyFiltering(filter);
                    studentNodes[i].setStudentCount(accommData.getFilteredCount());
                } else {
                    studentNodes[i].setStudentCount(new Integer(0));
                }
                */
                studentNodes[i].setStudentCount(new Integer(0));
                if(testAdminId != null) {
                    studentNodes[i].setRosterCount(orgNode.getRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));
                }
            }
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getStudentNodesForParent: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a list of org nodes at which the specified user has a role defined.
     * <br/><br/>Each node contains two counts: the number of students rostered
     * in the specified admin which are at or below the node (rosterCount),
     * and the number of students matching the grade and student test
     * accommodation filter criteria which are at or below the node (studentCount).
     * If no test admin id is specified, only the student count is populated, not
     * the roster count.
     * <br/><br/>The filter params passed to this call only affect the student
     * count, not the set of org nodes returned, whereas the sort and paging params
     * affect the org node list, as usual.
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test admin
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentNodeData getTopStudentNodesForUserAndAdmin(String userName, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getTopStudentNodesForUserAndAdmin");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getTopNodesForUser(userName);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            studentNodes = ond.getStudentNodes();
            for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {
            	/*
                StudentAccommodationsData cachedAccommData = (StudentAccommodationsData) SimpleCache.checkCache5min("STUDENT_ACCOMMODATIONS_BY_ORG", String.valueOf(studentNodes[i].getOrgNodeId()));
                if(cachedAccommData == null) {
                    StudentAccommodations[] accommData = accommodation.getStudentAccommodationsByAncestorNode(studentNodes[i].getOrgNodeId());
                    HashMap accommsByOrg = new HashMap(studentNodes.length);
                    for(int j=0;j<accommData.length;j++) {
                        StudentAccommodations sa = accommData[j];
                        ArrayList thisOrgAccoms = (ArrayList) accommsByOrg.get(sa.getOrgNodeId());
                        if(thisOrgAccoms == null) thisOrgAccoms = new ArrayList();
                        thisOrgAccoms.add(sa);
                        accommsByOrg.put(sa.getOrgNodeId(), thisOrgAccoms);
                    }
                    Object[] keys = accommsByOrg.keySet().toArray();
                    for(int j=0;j<keys.length;j++) {
                        ArrayList accomms = (ArrayList) accommsByOrg.get(keys[j]);
                        cachedAccommData = new StudentAccommodationsData();
                        cachedAccommData.setStudentAccommodations((StudentAccommodations[]) accomms.toArray(new StudentAccommodations[0]), null);
                        SimpleCache.cacheResult("STUDENT_ACCOMMODATIONS_BY_ORG", String.valueOf((Integer)keys[j]), cachedAccommData);
                    }
                    cachedAccommData = (StudentAccommodationsData) SimpleCache.checkCache("STUDENT_ACCOMMODATIONS_BY_ORG", String.valueOf(studentNodes[i].getOrgNodeId()));
                }
                StudentAccommodationsData newAccommData = new StudentAccommodationsData();
                if(cachedAccommData != null && cachedAccommData.getStudentAccommodations() != null) {
                    StudentAccommodations[] saArray = new StudentAccommodations[cachedAccommData.getStudentAccommodations().length];
                    for(int j=0;j<saArray.length;j++) {
                        saArray[j] = cachedAccommData.getStudentAccommodations()[j];
                    }
                    newAccommData.setStudentAccommodations(saArray, null);
                }
               
                if(newAccommData.getStudentAccommodations() != null && newAccommData.getStudentAccommodations().length > 0) {
                    if(filter != null) newAccommData.applyFiltering(filter);
                    studentNodes[i].setStudentCount(newAccommData.getFilteredCount());
                } else {
                    studentNodes[i].setStudentCount(new Integer(0));
                }
            	*/
                studentNodes[i].setStudentCount(new Integer(0));
            	
                if(testAdminId != null) {
                    studentNodes[i].setRosterCount(orgNode.getRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));
                }            
            }
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getTopStudentNodesForUserAndAdmin: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of child org nodes of the specified org node
     * <br/><br/>Each node contains a count of the number of students rostered
     * in the specified admin which are at or below the node (rosterCount). Any nodes 
     * for which this count is 0 will be filtered out of the result list.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testAdminId - identifies the test admin
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentNodeData getRosterNodesForParentAndAdmin(String userName, Integer orgNodeId, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getRosterNodesForParentAndAdmin");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getOrgNodesByParent(orgNodeId);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(testAdminId != null) {
                for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {
                    studentNodes[i].setRosterCount(orgNode.getRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));            
                }
                FilterParams implicitFilter = new FilterParams();
                FilterParam [] newFilters = new FilterParam [1];
                Integer [] args = new Integer[1];
                args[0] = new Integer(0);
                FilterParam countFilter = new FilterParam("RosterCount", args, FilterType.GREATERTHAN);
                newFilters[0] = countFilter;
                implicitFilter.setFilterParams(newFilters);
                ond.applyFiltering(implicitFilter);
            }
            ond.setStudentNodes(ond.getStudentNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getRosterNodesForParentAndAdmin: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of child org nodes of the specified org node
     * <br/><br/>Each node contains a count of the number of students rostered
     * in the specified admin which are at or below the node (rosterCount). Any nodes 
     * for which this count is 0 will be filtered out of the result list.
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testAdminId - identifies the test admin
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentNodeData getTestTicketNodesForParent(String userName, Integer orgNodeId, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getTestTicketNodesForParent");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            //Node [] nodes = orgNode.getOrgNodesByParent(orgNodeId);
            Node [] nodes = orgNode.getOrgNodesByParentAncestor(orgNodeId);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(testAdminId != null) {
                for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {
                    studentNodes[i].setRosterCount(orgNode.getTestTicketRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));            
                }
                FilterParams implicitFilter = new FilterParams();
                FilterParam [] newFilters = new FilterParam [1];
                Integer [] args = new Integer[1];
                args[0] = new Integer(0);
                FilterParam countFilter = new FilterParam("RosterCount", args, FilterType.GREATERTHAN);
                newFilters[0] = countFilter;
                implicitFilter.setFilterParams(newFilters);
                ond.applyFiltering(implicitFilter);
            }
            ond.setStudentNodes(ond.getStudentNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getRosterNodesForParentAndAdmin: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    
    public StudentNodeData getTestTicketNodesHaveStudentForParent(String userName, Integer orgNodeId, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getTestTicketNodesForParent");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            StudentNode [] studentNodes = orgNode.getOrgNodesHaveStudentByParentAncestor(orgNodeId, testAdminId);
            ond.setStudentNodes(studentNodes, pageSize);

            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getRosterNodesForParentAndAdmin: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of org nodes at which the specified user has a role defined.
     * <br/><br/>Each node contains a count of the number of students rostered
     * in the specified admin which are at or below the node (rosterCount). Any nodes 
     * for which this count is 0 will be filtered out of the result list.
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test admin
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentNodeData getTopRosterNodesForUserAndAdmin(String userName, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getTopRosterNodesForUserAndAdmin");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getTopNodesForUser(userName);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(testAdminId != null) {
                for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {
                    studentNodes[i].setRosterCount(orgNode.getRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));            
                }
                FilterParams implicitFilter = new FilterParams();
                FilterParam [] newFilters = new FilterParam [1];
                Integer [] args = new Integer[1];
                args[0] = new Integer(0);
                FilterParam countFilter = new FilterParam("RosterCount", args, FilterType.GREATERTHAN);
                newFilters[0] = countFilter;
                implicitFilter.setFilterParams(newFilters);
                ond.applyFiltering(implicitFilter);
            }
            ond.setStudentNodes(ond.getStudentNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getTopRosterNodesForUserAndAdmin: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a sorted, filtered, paged list of org nodes at which the specified user, or the
     * user who schduled the specified test admin, has a role defined.
     * <br/><br/>Each node contains a count of the number of students rostered
     * in the specified admin which are at or below the node (rosterCount). Any nodes 
     * for which this count is 0 will be filtered out of the result list.
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test admin
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return StudentNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public StudentNodeData getTopTestTicketNodes(String userName, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getTopTestTicketNodes");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getTopNodesForUserAndAdmin(userName, testAdminId);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(testAdminId != null) {
                for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {
                    studentNodes[i].setRosterCount(orgNode.getRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));            
                }
                FilterParams implicitFilter = new FilterParams();
                FilterParam [] newFilters = new FilterParam [1];
                Integer [] args = new Integer[1];
                args[0] = new Integer(0);
                FilterParam countFilter = new FilterParam("RosterCount", args, FilterType.GREATERTHAN);
                newFilters[0] = countFilter;
                implicitFilter.setFilterParams(newFilters);
                ond.applyFiltering(implicitFilter);
            }
            ond.setStudentNodes(ond.getStudentNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getTopTestTicketNodes: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a list of child org nodes of the specified org node,
     * plus a count of users who have roles anywhere at/below each 
     * org node in the list (userCount). If testAdminId is not null, a count of users at
     * or below each node who are assigned as proctors to the specified session
     * is also attached to each node (proctorCount).
     * @common:operation
     * @param userName - identifies the user
     * @param orgNodeId - identifies the parent org node
     * @param testAdminId - identifies the test session
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return UserNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public UserNodeData getUserNodesForParent(String userName, Integer orgNodeId, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getUserNodesForParent");
        try {
            UserNodeData ond = new UserNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getOrgNodesByParent(orgNodeId);
            UserNode [] userNodes = new UserNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                userNodes[i] = new UserNode(nodes[i]);                     
            }
            ond.setUserNodes(userNodes, pageSize);
            for(int i=0;i<userNodes.length && userNodes[i] != null;i++) {
                userNodes[i].setUserCount(orgNode.getUserCountForAncestorNode(userNodes[i].getOrgNodeId()));            
                if(testAdminId != null) {
                    userNodes[i].setProctorCount(orgNode.getProctorCountForAncestorNodeAndAdmin(userNodes[i].getOrgNodeId(), testAdminId));            
                }
            }
            FilterParams implicitFilter = new FilterParams();
            FilterParam [] newFilters = new FilterParam [1];
            Integer [] args = new Integer[1];
            args[0] = new Integer(0);
            FilterParam countFilter = new FilterParam("UserCount", args, FilterType.GREATERTHAN);
            newFilters[0] = countFilter;
            implicitFilter.setFilterParams(newFilters);
            ond.applyFiltering(implicitFilter);
            ond.setUserNodes(ond.getUserNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getUserNodesForParent: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Retrieves a list of child org nodes of the specified org node,
     * plus a count of users who have roles anywhere at/below each 
     * org node in the list (userCount). If testAdminId is not null, a count of users at
     * or below each node who are assigned as proctors to the specified session
     * is also attached to each node (proctorCount).
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test session
	 * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
	 * @return UserNodeData
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public UserNodeData getTopUserNodesForUser(String userName, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
    	//Attempt to improve performance
    	if(testAdminId != null)
    		validator.validateAdmin(userName, testAdminId, "testAdmin.getTopUserNodesForUser");
        try {
            UserNodeData ond = new UserNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getTopNodesForUser(userName);
            UserNode [] userNodes = new UserNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                userNodes[i] = new UserNode(nodes[i]);                     
            }
            ond.setUserNodes(userNodes, pageSize);
            for(int i=0;i<userNodes.length && userNodes[i] != null;i++) {
                userNodes[i].setUserCount(orgNode.getUserCountForAncestorNode(userNodes[i].getOrgNodeId()));            
                if(testAdminId != null) {
                    userNodes[i].setProctorCount(orgNode.getProctorCountForAncestorNodeAndAdmin(userNodes[i].getOrgNodeId(), testAdminId));            
                }
            }
            FilterParams implicitFilter = new FilterParams();
            FilterParam [] newFilters = new FilterParam [1];
            Integer [] args = new Integer[1];
            args[0] = new Integer(0);
            FilterParam countFilter = new FilterParam("UserCount", args, FilterType.GREATERTHAN);
            newFilters[0] = countFilter;
            implicitFilter.setFilterParams(newFilters);
            ond.applyFiltering(implicitFilter);
            ond.setUserNodes(ond.getUserNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getTopUserNodesForUser: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    /**
     * Creates a test session. newSession.getTestSession().getTestAdminId() must be null.
     * @common:operation
     * @param userName - identifies the user
     * @param newSession - contains test session information and the student and proctor lists
     * @return the test admin id of the newly created session
     * @throws com.ctb.exception.CTBBusinessException
     */
    public Integer createNewTestSession(String userName, ScheduledSession newSession) throws CTBBusinessException {
    	try {
	    	validator.validateAdmin(userName, newSession.getTestSession().getTestAdminId(), "testAdmin.createNewTestSession");
	        validator.validateItemSet(userName, newSession.getTestSession().getItemSetId(), "testAdmin.createNewTestSession");
	        if(newSession.getTestSession().getTestAdminId() != null)
	            throw new SessionCreationException("testAdmin: createNewTestSession: cannot create a session with existing session id: " + newSession.getTestSession().getTestAdminId());
	        return writeTestSession(userName, newSession);
    	} catch (CTBBusinessException e) { 
    		throw e;
    	}catch (Exception e) {
    		String message = (e.getMessage()!= null) ? e.getMessage().toLowerCase() : "";
            CTBBusinessException ctbe = new SessionCreationException(message);
            ctbe.setStackTrace(e.getStackTrace());
    		throw ctbe;
    	}
    }
        
    /**
     * Updates a test session. newSession.getTestSession().getTestAdminId() must be a valid existing testAdminId -
     * the corresponding test session is updated with the new information.
     * @common:operation
     * @param userName - identifies the user
     * @param newSession - contains test session information and the student and proctor lists
     * @return the test admin id of the updated session
     * @throws com.ctb.exception.CTBBusinessException
     */
    public Integer updateTestSession(String userName, ScheduledSession newSession) throws CTBBusinessException {
    	try {
			validator.validateAdmin(userName, newSession.getTestSession().getTestAdminId(), "testAdmin.updateTestSession");
	        validator.validateItemSet(userName, newSession.getTestSession().getItemSetId(), "testAdmin.updateTestSession");
	        if(newSession.getTestSession().getTestAdminId() == null)
	            throw new SessionCreationException("testAdmin: updateTestSession: cannot update a session with null session id.");
	        return writeTestSession(userName, newSession);
    	} catch (CTBBusinessException e) { 
    		throw e;
    	} catch (Exception e) {
    		String message = (e.getMessage()!= null) ? e.getMessage().toLowerCase() : "";
            CTBBusinessException ctbe = new SessionCreationException(message);
            ctbe.setStackTrace(e.getStackTrace());
    		throw ctbe;
    	}
    }
    
    /**
     * checks each of the provided list of access codes against the
     * database to ensure they are not in use. Result array contains "valid" 
     * for each code that is valid and a variety of other strings appended
     * to each other for each code that is already in use,
     * or is otherwise invalid (eg. contains special characters, <6 characters in length).
     * If testAdminId parameter is non-null, codes in use withing the corresponding test
     * session are not considered invalid (allows re-arranging of access codes within
     * the schedulable units of a session).
     * Example 1, valid code returns: "valid"
     * Example 2, code is too short, contains bad chars, and is in use returns: "shortbadcharsexists"
     * @common:operation
     * @param userName - identifies the user
     * @param accessCodes - array of proposed access codes
     * @param testAdminId - ignore existing codes within this session when checking for existence
     * @return array of validation results
     * @throws CTBBusinessException
     */
    public String [] validateAccessCodes(String userName, String [] accessCodes, Integer testAdminId) throws CTBBusinessException {
        //validator.validate(userName, null, "testAdmin.validateAccessCodes");
        String[] invalidChars = { "!", "@", "#", "$", "%", "^", "&", "(", ")", "-", "+", "<", ">", "*" };            
        String results [] = new String [accessCodes.length];
        try {
            for(int i=0;i<accessCodes.length;i++) {
                String code = accessCodes[i];
                results[i] = "";
                if(code.length() < 6)
                    results[i] += "short";
                for(int j=0;j<invalidChars.length;j++) {
                    if (code.indexOf(invalidChars[j]) >= 0)
                        results[i] += "badchars";
                }
                boolean exists = false;
                if(testAdminId != null) {
                    exists = admins.getTestAdminsByAccessCodeIgnoreAdmin(code, testAdminId).length != 0;
                } else {
                   exists = admins.getTestAdminsByAccessCode(code).length != 0;
                }
                if(exists) results[i] += "exists";
                if("".equals(results[i]))
                    results[i] = "valid";
            }
            return results;
        } catch (SQLException se) {
            CTBBusinessException cbe = new SessionCreationException("ScheduleTestImpl: validateAccessCodes: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    /**
     * Get user information including full name and system id for the specified user name.
     * If the specified user lies withing the requesting user's visible hierarchy,
     * all fields are returned - if not, only the first, last, and middle names
     * of the specified user are returned. Each user object contains a Customer object,
     * which contains information about the user's customer, including a flag, hideAccommodations,
     * which indicates whether accommodation-related UI elements should be hidden for
     * the specified user.
     * @common:operation
     * @param userName - identifies the calling user
     * @param detailUserName - identifies the user whose information is desired
     * @return User
     * @throws CTBBusinessException
     */
    public User getUserDetails(String userName, String detailUserName) throws CTBBusinessException{
        boolean hasPerms = true;
        try {
            validator.validateUser(userName, detailUserName, "testAdmin.getUserDetails");
        } catch (ValidationException ve) {
            hasPerms = false;
        }
        try {
            //User user = users.getUserDetails(detailUserName);
        	//Attempt to improve performance
            User user = users.getUserDetailsWithoutPassword(detailUserName);
            user.setCustomer(users.getCustomer(detailUserName));
            user.setRole(users.getRole(detailUserName));
            if(!hasPerms) {
                User secureUser = new User();
                secureUser.setFirstName(user.getFirstName());
                secureUser.setLastName(user.getLastName());
                secureUser.setMiddleName(user.getMiddleName());
                secureUser.setUserId(user.getUserId());
                secureUser.setUserName(user.getUserName());
                secureUser.setCustomer(user.getCustomer());
                return secureUser;
            } else {
                return user;
            }
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getUserDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    /**
     * Retrieves a paged, sorted, filtered list of students that are in the given test session..
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test session
     * @return SessionStudentData
     * @throws CTBBusinessException
     */
    public SessionStudentData getSessionStudents(String userName, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getSessionStudents");
        SessionStudentData ssd = new SessionStudentData();
        try {
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            SessionStudent [] roster = students.getSessionStudentsForAdmin(testAdminId);
            ssd.setSessionStudents(roster, pageSize);
            if(filter != null) ssd.applyFiltering(filter);
            if(sort != null) ssd.applySorting(sort);
            if(page != null) ssd.applyPaging(page);     
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getSessionStudents: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        } 
        return ssd;
    }
    
    /**
     * Retrieves all information pertaining to a test session, including the schedulable unit manifest,
     * the roster, the proctor list, and a count of students who have logged into the session.
     * The returned ScheduledSession object can be edited and passed
     * back in via the updateTestSession or createNewTestSession methods to persist changes or create a
     * copy if the copyable flag is 'T'.
     * The copyable flag will be set to 'T' if the requesting user
     * has a non-proctor role, the test is active and distributed
     * to the requesting user's top node, and the requesting user
     * has the scheduling node as one of their top nodes. Otherwise
     * the flag will be set to 'F'. The ScheduledSession.testSession object also contains a overrideFormAssigmentMethod field, which
     * if populated (see TestSession.FormAssignment constants) indicates that
     * only the specified assignment method should be available for scheduling, and a
     * overrideLoginStartDate field, which if populated indicates that no earlier (than the override date) login
     * start date can be chosen for the session.
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test session
     * @return ScheduledSession
     * @throws CTBBusinessException
     */
    public ScheduledSession getScheduledSession(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledSession");
        try {
            ScheduledSession session = new ScheduledSession();
            int studentsLoggedIn = 0;
            User [] proctors = users.getProctorUsersForAdmin(testAdminId);
            for(int i=0;i<proctors.length;i++) {
                boolean editable = users.isUserEditableByUserForAdmin(userName, proctors[i].getUserId(), testAdminId).intValue() > 0;
                proctors[i].setCopyable(editable ? "T" : "F");
                editable = editable && !proctors[i].getUserName().equals(userName);
                proctors[i].setEditable(editable ? "T" : "F");
            }
            session.setProctors(proctors);
            TestElement [] testUnits = itemSet.getTestElementsForSession(testAdminId);
            session.setScheduledUnits(testUnits);
//            TestElement [] defaultTestUnits = itemSet.getDefaultTestElementsForSession(testAdminId);
//            session.setDefaultUnits(defaultTestUnits);
            
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
            Integer productId = testSession.getProductId();
            TestProduct testProduct = product.getProduct(productId);
            boolean overrideUsingStudentManifest = false;
            if ("F".equalsIgnoreCase(testProduct.getStaticManifest())
                && "F".equalsIgnoreCase(testProduct.getSessionManifest()))
                overrideUsingStudentManifest = true;
            
            SessionStudent [] roster = students.getSessionStudentsForAdmin(testAdminId);
            boolean testRestricted = "T".equals(admins.isTestRestricted(testSession.getItemSetId()))?true:false;
            for(int i=0;i<roster.length;i++) {
                if (overrideUsingStudentManifest) {
                    StudentManifest [] studentManifests = studentItemSetStatus.getStudentManifestsForRoster(roster[i].getStudentId(),testAdminId);
                    ArrayList smAr = getFilteredStudentManifestForRoster(studentManifests);
                    roster[i].setStudentManifests((StudentManifest [])smAr.toArray(new StudentManifest[0]));
                }
                boolean copyable = students.isStudentEditableByUserForOrg(userName, roster[i].getStudentId(), roster[i].getOrgNodeId()).intValue() > 0;
                EditCopyStatus status = roster[i].getStatus();
                status.setEditable("T");
                status.setCopyable("T");
                if(!copyable) {
                    status.setCopyable("F");
                    status.setEditable("F");
                    status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                }
                Integer restrictedAdmin = new Integer(-1);
                if(testRestricted) {
                    restrictedAdmin = students.isTestRestrictedForStudent(userName, roster[i].getStudentId(), testSession.getItemSetId());
                }
                if(restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    status.setCopyable("F");
                    status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                    status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                }
                if(!"F".equals(roster[i].getTested())) {
                    status.setEditable("F");
                    studentsLoggedIn++;
                }
            }
            session.setStudents(roster);
            String [] forms = itemSet.getFormsForTest(testSession.getItemSetId());
            for(int i=0;i<testUnits.length && testUnits[i] != null;i++) {
                TestElement testUnit = testUnits[i];
                testUnit.setForms(forms);
            }
            TestAdminStatusComputer.adjustSessionTimesToLocalTimeZone(testSession);
            session.setTestSession(testSession);
            session.setStudentsLoggedIn(new Integer(studentsLoggedIn));
            session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledSession: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    /**
     * Deletes all information pertaining to a test session, including the schedulable unit manifest,
     * the roster, and the proctor list. An exception will be thrown if any roster elements are in a
     * state which prevents deletion (non-SC completion status).
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test session
     * @throws CTBBusinessException
     */
    public void deleteTestSession(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.deleteTestSession");
        try {
        	//siss.getConnection().setAutoCommit(false);
            siss.deleteStudentItemSetStatusesForAdmin(testAdminId);
            //rosters.getConnection().setAutoCommit(false);
            rosters.deleteTestRostersForAdmin(testAdminId);
            //tais.getConnection().setAutoCommit(false);
            tais.deleteTestAdminItemSetsForAdmin(testAdminId);
            //taur.getConnection().setAutoCommit(false);
            taur.deleteTestAdminUserRolesForAdmin(testAdminId);
            //admins.getConnection().setAutoCommit(false);
            admins.deleteTestAdmin(testAdminId);
        } catch (SQLException se) {
            CTBBusinessException cbe = new SessionDeletionException("ScheduleTestImpl: deleteTestSession: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    
    
    /**
     * Get grades for the specified customer.
     * @common:operation
     * @param userName - identifies the calling user
     * @param customerId - identifies the customer whose information is desired
     * @return String []
     * @throws CTBBusinessException
     */
    public String [] getGradesForCustomer(String userName, Integer customerId) throws CTBBusinessException
    {
        validator.validateCustomer(userName, customerId, "StudentManagementImpl.getGradesForCustomer");
        try {
            String [] grades = null;
            CustomerConfigurationValue [] customerConfigurationValues = customerConfigurations.getCustomerConfigurationValuesForGrades(customerId.intValue());
            if (customerConfigurationValues == null || customerConfigurationValues.length == 0) {
                customerConfigurationValues = customerConfigurations.getCustomerConfigurationValuesForGrades(CTB_CUSTOMER_ID);
            }
            if (customerConfigurationValues != null && customerConfigurationValues.length > 0) 
            {
                grades = new String[customerConfigurationValues.length];
                for (int i = 0; i < customerConfigurationValues.length; i++) {
                    grades[i] = customerConfigurationValues[i].getCustomerConfigurationValue();
                    
                }
            }
            return grades;
        } catch (SQLException se) {
            CustomerConfigurationDataNotFoundException tee = new CustomerConfigurationDataNotFoundException("StudentManagementImpl: getGradesForCustomer: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
        
    }
    

    private Integer writeTestSession(String userName, ScheduledSession newSession) throws CTBBusinessException {
        
    	Integer thisTestAdminId = null;
    	Double extendedTimeValue = 0.0; // Added for Student Pacing
    	try {
			Integer userId = users.getUserIdForName(userName);
            Integer customerId = users.getCustomerIdForName(userName);
            TestSession session = newSession.getTestSession();
            Integer testAdminId = session.getTestAdminId();
            
            // Start changes for Student Pacing
			CustomerConfigurationValue [] customerConfigurationValues = customerConfigurations.getCustomerConfigurationValue(customerId,"Extended_Time");				
			if (customerConfigurationValues != null && customerConfigurationValues.length > 0) 
            {
				extendedTimeValue = new Double (customerConfigurationValues[0].getCustomerConfigurationValue());
            }
			// End changes for Student Pacing
			
			if(testAdminId == null) {
				session.setTestAdminId(createNewTestAdminRecord(userId, customerId, session));
                ArrayList subtests = createTestAdminItemSetRecords(newSession);
                createTestRosters(userName, userId, subtests, newSession, extendedTimeValue);
                createProctorAssignments(true, userId, newSession);
            } else {
            	updateTestAdminRecord(userId, session, customerId);
                ArrayList subtests = updateTestAdminItemSetRecords(newSession);
                //updateTestRosters(userName, userId, subtests, newSession, session.getItemSetId(), extendedTimeValue);
                updateProctorAssignments(userName, userId, newSession);
                writeTestAdminRecord(userId, session);
                updateTestRosters(userName, userId, subtests, newSession, session.getItemSetId(), extendedTimeValue);
            }
            
            thisTestAdminId = session.getTestAdminId();
            
        } catch (Exception se) {
        	CTBBusinessException ctbe = null;
            String message = se.getMessage().toLowerCase();
            System.out.println(se.getMessage());
            if(message.indexOf("insufficient available license quantity") >=0) {
                ctbe = new InsufficientLicenseQuantityException("Insufficient available license quantity");
                ctbe.setStackTrace(se.getStackTrace());
            }
            //START- Changed for deferred defect 64446
            else{
            	if(message.indexOf("transaction timed out") >=0) {
					System.out.println("Transaction timed out:"+message);
                    ctbe = new TransactionTimeoutException("Transaction timed out");
                }
            	 else {
					 System.out.println("Exception");
                     ctbe = new SessionCreationException("ScheduleTestImpl: writeTestSession: " + message);
                     ctbe.setStackTrace(se.getStackTrace());
                 }
            }
            //END- Changed for deferred defect 64446
            throw ctbe;
        }

        return thisTestAdminId;
    }

    private Integer createNewTestAdminRecord(Integer userId, Integer customerId, TestSession session) throws SessionCreationException,com.ctb.exception.CTBBusinessException {
        try {
            Integer testAdminId = admins.getNextPK();
            //Integer creatorOrgNodeId = users.getFirstTopNodeForUser(userName);
            session.setTestAdminId(testAdminId);
            session.setCreatedBy(String.valueOf(userId.intValue()));
            Date now = new Date();
            session.setCreatedDateTime(now);
            session.setCustomerId(customerId);
            //session.setCreatorOrgNodeId(creatorOrgNodeId);
            session.setActivationStatus("AC");
            // adjust window dates/times to GMT
            TestAdminStatusComputer.adjustSessionTimesToGMT(session);
            TestAdminStatusComputer.setTestSessionStatus(session);
            //boolean validCode = admins.getTestAdminsByAccessCode(session.getAccessCode()).length == 0;
            //if(!validCode) throw new SessionCreationException("ScheduleTestImpl: createNewTestSession: access code is in use.");
            Random rnd = new Random();
            int rndDigit  = Math.abs( rnd.nextInt());
            session.setSessionNumber(String.valueOf(rndDigit % 9999999));
            Date loginStartOverride = admins.getTestCatalogLoginStartDateOverride(session.getItemSetId(), session.getCreatorOrgNodeId());
            Date loginEndOverride = admins.getTestCatalogLoginEndDateOverride(session.getItemSetId(), session.getCreatorOrgNodeId());
            if(loginStartOverride != null && session.getLoginStartDate().before(loginStartOverride)) {
                session.setOverrideLoginStartDate(loginStartOverride);
                session.setLoginStartDate(loginStartOverride);
            }
            // commenting this out because user cannot select a date beyond the overirde loging end date from the UI
            /* if(loginEndOverride != null && session.getLoginEndDate().after(loginEndOverride)){
            	 session.setLoginEndDate(loginEndOverride);
            }*/ 
            String formAssignmentOverride = admins.getTestCatalogFormAssignmentOverride(session.getItemSetId(), session.getCreatorOrgNodeId());
            if(formAssignmentOverride != null && !"".equals(formAssignmentOverride)) {
                session.setOverrideFormAssignmentMethod(formAssignmentOverride);
                session.setFormAssignmentMethod(formAssignmentOverride);
            }
            // need to constrain test admin name in case 'Copy of' pushes it over the limit when copying a session
            if(session.getTestAdminName().length() > 64) session.setTestAdminName(session.getTestAdminName().substring(0,63));
            Integer [] programIds = admins.getProgramIdForCustomerAndProduct(customerId,session.getProductId(),session.getLoginStartDate());
            if(programIds.length > 1 ){
                throw new InvalidNoOfProgramsException("This customer has more no of programs for this product");
            }  
            if(programIds != null && programIds.length ==1)
                session.setProgramId(programIds[0]);
            //admins.getConnection().setAutoCommit(false);
            admins.createNewTestAdmin(session);
            return testAdminId;
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: createNewTestAdmin: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void updateTestAdminRecord(Integer userId, TestSession session, Integer customerId) throws SessionCreationException {
        try {
        	session.setCustomerId(customerId);
            session.setUpdatedBy(String.valueOf(userId.intValue()));
            Date now = new Date();
            session.setUpdatedDateTime(now);
            // adjust window dates/times to GMT
            TestAdminStatusComputer.adjustSessionTimesToGMT(session);
            TestAdminStatusComputer.setTestSessionStatus(session);          
            TestSession oldSession = admins.getTestAdminDetails(session.getTestAdminId());          
            if(!oldSession.getItemSetId().equals(session.getItemSetId())) {
                // existing form assignments are invalid if test has changed
            	//rosters.getConnection().setAutoCommit(false);
                rosters.clearFormAssignmentsForAdmin(session.getTestAdminId());
            }
            session.setSessionNumber(oldSession.getSessionNumber());
            Date loginStartOverride = session.getOverrideLoginStartDate();
            Date loginEndOverride = session.getOverrideLoginEndDate();
            
            if(loginStartOverride != null && session.getLoginStartDate().before(loginStartOverride)) {
                session.setLoginStartDate(loginStartOverride);
            }
            
            //commenting this out as the user cannot select a date beyond the override login date form the UI
            /*if(loginEndOverride != null && session.getLoginEndDate().after(loginEndOverride)) {
            	session.setLoginEndDate(loginEndOverride);
            }*/
            
            String formAssignmentOverride = session.getOverrideFormAssignmentMethod();
            if(formAssignmentOverride != null && !"".equals(formAssignmentOverride)) {
                session.setFormAssignmentMethod(formAssignmentOverride);
            }
           
            session.setProgramId(admins.getProgramIdForCustomerAndProduct(session.getCustomerId(), session.getProductId(), session.getLoginStartDate())[0]);
            
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: updateTestAdmin: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void writeTestAdminRecord(Integer userId, TestSession session) throws SessionCreationException {
        try {
            admins.updateTestAdmin(session);
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: updateTestAdmin: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private ArrayList createTestAdminItemSetRecords(ScheduledSession newSession) throws SessionCreationException {
        try {
            //sessionDefault
/*            HashMap defaultSubtestHash = new HashMap();
            TestElement [] defaultElements = newSession.getDefaultUnits();
            if (defaultElements != null && defaultElements.length > 0) {
                for (int i = 0; i < defaultElements.length; i++) {
                    defaultSubtestHash.put(defaultElements[i].getItemSetId(),defaultElements[i].getItemSetId());
                }
            }
*/            
            TestElement [] scheduledElements = newSession.getScheduledUnits();
            ArrayList subtests = new ArrayList();
            for(int i=0;i<scheduledElements.length;i++) {
                TestElement te = scheduledElements[i];
                //sessionDefault
/*                if (defaultSubtestHash.containsKey(te.getItemSetId()))
                    te.setSessionDefault("T");
                else
                    te.setSessionDefault("F");
*/                    
                ScheduleElement se = new ScheduleElement();
                se.setAccessCode(te.getAccessCode());
                se.setItemSetId(te.getItemSetId());
                se.setItemSetOrder(new Integer(i));
                se.setTestAdminId(newSession.getTestSession().getTestAdminId());
                se.setItemSetForm(te.getItemSetForm()); ///tabe form
                se.setSessionDefault(te.getSessionDefault()); //sessionDefault
                //tais.getConnection().setAutoCommit(false);
                tais.createNewTestAdminItemSet(se);
                String cacheKey = String.valueOf(te.getItemSetId()) + "|TD";
                TestElementCacheObject cacheObj = (TestElementCacheObject) SimpleCache.checkCache5min("TEST_ELEMENT", cacheKey);
                if(cacheObj == null) {
                    cacheObj = new TestElementCacheObject();
                    cacheObj.testElements = itemSet.getTestElementsForParent(te.getItemSetId(), "TD");
                    SimpleCache.cacheResult("TEST_ELEMENT", cacheKey, cacheObj);
                }
                TestElement [] scheduledSubtests = cacheObj.testElements;
                for(int ii=0;ii<scheduledSubtests.length;ii++) {
                    subtests.add(scheduledSubtests[ii]);
                }
            }
            return subtests;
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: createTestAdminItemSetRecords: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private ArrayList updateTestAdminItemSetRecords(ScheduledSession newSession) throws SessionCreationException,CTBBusinessException {
        try {
            
            //sessionDefault
/*            HashMap defaultSubtestHash = new HashMap();
            TestElement [] defaultElements = newSession.getDefaultUnits();
            if (defaultElements != null && defaultElements.length > 0) {
                for (int i = 0; i < defaultElements.length; i++) {
                    defaultSubtestHash.put(defaultElements[i].getItemSetId(),defaultElements[i].getItemSetId());
                }
            }
*/            
            ArrayList subtests = new ArrayList();
            ScheduleElement [] oldUnits = tais.getTestAdminItemSetsForAdmin(newSession.getTestSession().getTestAdminId());
            HashMap oldMap = new HashMap();
            boolean sameTAC = false;
            String testAccessCode = null;
            TestElement [] newUnits = newSession.getScheduledUnits();
            for(int i=0;i<oldUnits.length;i++) {
                   oldMap.put(oldUnits[i].getItemSetId(), oldUnits[i]);
            }
            // Add student  subtests
            TestSession  newTestSession = newSession.getTestSession();
            SessionStudent [] sessionStudents = newSession.getStudents();
            HashMap totalSubtestsHM = new HashMap();
            int testOrder = 0;
            int stdSubtestOrder = 0;
            HashMap testOrderHM = new HashMap();
            for(int k=0;sessionStudents != null && k < sessionStudents.length;k++){
                SessionStudent sessionStudent = sessionStudents[k];
                StudentManifest [] studentManifest = sessionStudent.getStudentManifests();
                for(int kk = 0; studentManifest!=null && kk < studentManifest.length; kk++){
                    StudentManifest sm = studentManifest[kk];
                    if(!totalSubtestsHM.containsKey(sm.getItemSetId())){
                        totalSubtestsHM.put(sm.getItemSetId(),sm);
                        testOrderHM.put(new Integer(stdSubtestOrder),sm.getItemSetId());
                        stdSubtestOrder++;
                    }
                }
            }
            HashMap tacHM = new HashMap();
            //
            for(int j=0;j<newUnits.length;j++) {
                TestElement newUnit = newUnits[j];
                
                //sessionDefault
/*                if (defaultSubtestHash.containsKey(newUnit.getItemSetId()))
                    newUnit.setSessionDefault("T");
                else
                    newUnit.setSessionDefault("F");
*/                
                ScheduleElement oldUnit = (ScheduleElement) oldMap.get(newUnit.getItemSetId());
                ScheduleElement se = new ScheduleElement();
                //Add student  subtests
                if(!"T".equals(newTestSession.getEnforceBreak())){
                    sameTAC = true;
                    se.setAccessCode(newUnit.getAccessCode());
                }else{
                    if(tacHM.containsValue(newUnit.getAccessCode())){
                        boolean validCode = false;
                        String code = null;
                        while(!validCode) {
                            code = AccessCodeGenerator.generateAccessCode();
                            if (!tacHM.containsValue(code))
                                validCode = true;
                        }
                        se.setAccessCode(code);
                        newUnit.setAccessCode(code);
                        
                    }else{
                        se.setAccessCode(newUnit.getAccessCode());
                    }
                }
               //
                se.setItemSetId(newUnit.getItemSetId());
                se.setItemSetOrder(new Integer(j));
                se.setItemSetForm(newUnit.getItemSetForm()); /// tabe form
                se.setSessionDefault(newUnit.getSessionDefault()); //sessionDefault
                se.setTestAdminId(newSession.getTestSession().getTestAdminId());
                if(oldUnit == null) {
                	//tais.getConnection().setAutoCommit(false);
                    tais.createNewTestAdminItemSet(se);
                } else {
                    if("T".equals(oldUnit.getTested())) {
                        if(!(oldUnit.getAccessCode().equals(newUnit.getAccessCode()) &&
                            oldUnit.getItemSetOrder().equals(new Integer(j)))) {
                            throw new SessionCreationException("ScheduleTestImpl: updateTestAdminItemSetRecords: attempted to update a test element which has already been taken");    
                        }
                    } else {
                    	//tais.getConnection().setAutoCommit(false);                    	
                        tais.updateTestAdminItemSet(se);
                    }
                    oldMap.remove(newUnit.getItemSetId());                   
                   
                }
                TestElement [] scheduledSubtests = itemSet.getTestElementsForParent(se.getItemSetId(), "TD");
                for(int ii=0;ii<scheduledSubtests.length;ii++) {
                    subtests.add(scheduledSubtests[ii]);
                } 
                totalSubtestsHM.remove(newUnit.getItemSetId()); //Add student  subtests   
                testAccessCode = newUnit.getAccessCode(); // 
                tacHM.put(newUnit.getItemSetId(),newUnit.getAccessCode());
                testOrder++;   //Add student  subtests             
            }
            
            //Add student  subtests
            
           // Iterator iterator = totalSubtestsHM.keySet().iterator();
            if(totalSubtestsHM.size()>0){
                List orderdSubtests = getOrderedSubtests(totalSubtestsHM,testOrderHM);
                Iterator iterator = orderdSubtests.iterator();
                boolean isLocator = false;
                while(iterator.hasNext()){  
                  //  StudentManifest sm1 = (StudentManifest)totalSubtestsHM.get(iterator.next());  
                    StudentManifest sm1 = (StudentManifest)iterator.next();            
                    ScheduleElement se = (ScheduleElement)oldMap.get(sm1.getItemSetId());
                    if(se == null){
                        se = new ScheduleElement();
                        se.setItemSetId(sm1.getItemSetId());
                    }
                    Integer testAdminId = newSession.getTestSession().getTestAdminId();
                    if(sm1.getItemSetName().indexOf("Locator")>0){ 
                            testOrder = updateSubtestsOrder(testAdminId); 
                            isLocator = true; 
                    }
                    if(isLocator){
                        se.setItemSetOrder(new Integer(0));
                        isLocator = false;
                    }else{
                        se.setItemSetOrder(new Integer(testOrder));
                    }
                    if(sameTAC){
                        se.setAccessCode(testAccessCode);                   
                    }else{
                        boolean validCode = false;
                            String code = null;
                            while(!validCode) {
                                code = AccessCodeGenerator.generateAccessCode();
                                if (!tacHM.containsValue(code)){
                                    validCode = true;
                                    tacHM.put(se.getItemSetId(),code);
                                }
                            }
                       se.setAccessCode(code);
                    }
                    se.setItemSetForm(null); /// tabe form
                    se.setSessionDefault("F"); //sessionDefault
                    se.setTestAdminId(newSession.getTestSession().getTestAdminId());
                    if(!"T".equals(se.getTested())) {
                        if(oldMap.containsKey(sm1.getItemSetId())){
                        	//tais.getConnection().setAutoCommit(false);
                            tais.updateTestAdminItemSet(se);
                            oldMap.remove(sm1.getItemSetId());
                        }else{
                        	//tais.getConnection().setAutoCommit(false);
                            tais.createNewTestAdminItemSet(se); 
                        }
                    }else{
                        throw new SessionCreationException("ScheduleTestImpl: updateTestAdminItemSetRecords: attempted to update a test element which has already been taken");    
                    }
                    testOrder++;
                }
            }
            //
            Iterator iter = oldMap.values().iterator();
            while (iter.hasNext()) {
                ScheduleElement oldUnit = (ScheduleElement) iter.next();
                if("T".equals(oldUnit.getTested())) {
                    throw new SessionCreationException("ScheduleTestImpl: updateTestAdminItemSetRecords: attempted to remove a test element which has already been taken");  
                } else {
                	//tais.getConnection().setAutoCommit(false);
                    tais.deleteTestAdminItemSet(oldUnit);
                }
            }
            return subtests;
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: updateTestAdminItemSetRecords: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void createProctorAssignments(boolean checkVisibility, Integer userId, ScheduledSession newSession) throws SessionCreationException{
        try {
            Integer testAdminId = newSession.getTestSession().getTestAdminId();
            //Integer userId = new Integer(Integer.parseInt(newSession.getTestSession().getCreatedBy()));
            User [] proctors = newSession.getProctors();
            boolean addedDefaultProctor = false;
            // add specified proctors
            for(int k=0;proctors != null && k<proctors.length;k++) {
                if(!checkVisibility || !"F".equals(proctors[k].getCopyable())) {
                    ProctorAssignment proctor = new ProctorAssignment();
                    proctor.setCreatedBy(userId);
                    proctor.setCreatedDateTime(new Date());
                    proctor.setRoleId(new Integer(1004));
                    proctor.setTestAdminId(testAdminId);
                    proctor.setUserId(proctors[k].getUserId());
                    //taur.getConnection().setAutoCommit(false);
                    taur.createNewTestAdminUserRole(proctor);
                    if(newSession.getTestSession().getCreatedBy().equals(proctors[k].getUserName()) || userId.equals(proctors[k].getUserId())) {
                        addedDefaultProctor = true;
                    }
                }
            }
            if(!addedDefaultProctor) {
                // add default proctor
                ProctorAssignment proctor = new ProctorAssignment();
                proctor.setCreatedBy(userId);
                proctor.setCreatedDateTime(new Date());
                proctor.setRoleId(new Integer(1004));
                proctor.setTestAdminId(testAdminId);
                proctor.setUserId(userId);
                //taur.getConnection().setAutoCommit(false);
                taur.createNewTestAdminUserRole(proctor);
            }
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: createProctorAssignments: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void updateProctorAssignments(String userName, Integer userId, ScheduledSession newSession) throws SessionCreationException{
        try {
        	//taur.getConnection().setAutoCommit(false);
            taur.deleteTestAdminUserRolesForAdmin(newSession.getTestSession().getTestAdminId());
            createProctorAssignments(false, userId, newSession);
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: createProctorAssignments: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void createTestRosters(String userName, Integer userId, ArrayList subtests, ScheduledSession newSession, Double extendedTimeValue) throws CTBBusinessException {
        try{
            Integer productId = newSession.getTestSession().getProductId();
            TestProduct testProduct = product.getProduct(productId);
            boolean overrideUsingStudentManifest = false;
            if ("F".equalsIgnoreCase(testProduct.getStaticManifest())
                && "F".equalsIgnoreCase(testProduct.getSessionManifest()))
                overrideUsingStudentManifest = true;
            Integer testAdminId = newSession.getTestSession().getTestAdminId();
            //Integer userId = new Integer(Integer.parseInt(newSession.getTestSession().getCreatedBy()));
            Integer customerId = newSession.getTestSession().getCustomerId();
            String defaultCustomerFlagStatus = customerConfigurations.getDefaulCustomerFlagStatus(customerId);
            SessionStudent [] scheduledStudents = newSession.getStudents();
            String form = newSession.getTestSession().getPreferredForm();
            FormAssignmentCount [] formCounts = null;
            if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.ROUND_ROBIN)) {
                formCounts = formAssignments.getFormAssignmentCounts(testAdminId);
            }
            ArrayList subtestAssignments = new ArrayList();
            boolean testRestricted = "T".equals(admins.isTestRestricted(newSession.getTestSession().getItemSetId()))?true:false;
            for(int j=0;scheduledStudents != null && j<scheduledStudents.length;j++) {
                SessionStudent student = scheduledStudents[j];
                Integer restrictedAdmin = new Integer(-1);
                
                // Start changes for Student Pacing
                Double rosterExtendedTime = null;
                String extendedTimeAccom = "F";
                if(student.getExtendedTimeAccom() != null){
                	extendedTimeAccom = student.getExtendedTimeAccom();
                }
                // End changes for Student Pacing
                
                if(testRestricted) {
                    restrictedAdmin = students.isTestRestrictedForStudent(userName, student.getStudentId(), newSession.getTestSession().getItemSetId());
                }
                if((restrictedAdmin == null || new Integer(-1).equals(restrictedAdmin)) && !"F".equals(student.getStatus().getCopyable())) {
                    if (overrideUsingStudentManifest) {
                        form = null;    
                    }
                    else {
                        if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.MANUAL)) {
                            form = student.getItemSetForm();
                        } else if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.ROUND_ROBIN)) {
                            form = TestFormSelector.getFormWithLowestCountAndIncrement(formCounts);
                        }
                    }
                    RosterElement roster = new RosterElement();
                    //Integer testRosterId = rosters.getNextPK();
                    roster.setActivationStatus("AC");
                    roster.setCreatedBy(userId);
                    roster.setCreatedDateTime(new Date());
                    roster.setCustomerId(customerId);
                    roster.setFormAssignment(form);
                    roster.setOrgNodeId(student.getOrgNodeId());
                    roster.setOverrideTestWindow("F");
                    String password = PasswordGenerator.generatePassword();
                    roster.setPassword(password);
                    roster.setScoringStatus("NA");
                    roster.setStudentId(student.getStudentId());
                    roster.setTestAdminId(testAdminId);
                    roster.setTestCompletionStatus("SC");
                    //roster.setTestRosterId(testRosterId);
                    roster.setValidationStatus("VA");
                    roster.setCustomerFlagStatus(defaultCustomerFlagStatus);
                    
                    // Start changes for Student Pacing
                    if(extendedTimeValue > 0){
                    	if(extendedTimeAccom != null && extendedTimeAccom.equalsIgnoreCase("T"))
                    		rosterExtendedTime = extendedTimeValue;
                    }
                    roster.setExtendedTime(rosterExtendedTime);
                    // End changes for Student Pacing
                    
                    try {
                    	//rosters.getConnection().setAutoCommit(false);
                        rosters.createNewTestRoster(roster);
                    } catch (SQLException se) {
                        boolean validPassword = rosters.getRosterCountForPassword(password).intValue() == 0;
                        while(!validPassword) {
                            password = PasswordGenerator.generatePassword();
                            validPassword = rosters.getRosterCountForPassword(password).intValue() == 0;
                        }
                        roster.setPassword(password);
                        //rosters.getConnection().setAutoCommit(false);
                        rosters.createNewTestRoster(roster);
                    }
                    StudentSubtestAssignment newAssignment = new StudentSubtestAssignment();
                    newAssignment.setForm(form);
                    newAssignment.setTestAdminId(testAdminId);
                    newAssignment.setStudentId(student.getStudentId());
                    if (overrideUsingStudentManifest) { 
                        newAssignment.setSubtests(getTDTestElementList(student.getStudentManifests()));
                    } else {
                        newAssignment.setSubtests(subtests);   
                    }
                    subtestAssignments.add(newAssignment);
                }
            }
            createStudentItemSetStatusRecords(customerId, subtestAssignments);
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: createTestRosters: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private ArrayList getTDTestElementList(StudentManifest [] studentManifests) 
        throws SQLException
    {
        ArrayList subtests = new ArrayList();
        for(int i=0;i<studentManifests.length;i++) {
            Integer itemSetId = studentManifests[i].getItemSetId();
            String form = studentManifests[i].getItemSetForm() ==  null || "".equals(studentManifests[i].getItemSetForm())?"-":studentManifests[i].getItemSetForm();
            ArrayList elements = (ArrayList) SimpleCache.checkCache("TSSubtestFormList", String.valueOf(itemSetId) + form);
            if(elements == null) {
                elements = new ArrayList();
                TestElement [] scheduledSubtests;
                if ("-".equals(form)) {
                    scheduledSubtests = itemSet.getTestElementsForParent(itemSetId, "TD");
                } else { 
                    scheduledSubtests = itemSet.getTestElementsForParentByForm(itemSetId, "TD", form);
                } for(int ii=0;ii<scheduledSubtests.length;ii++) {
                    subtests.add(scheduledSubtests[ii]);
                    elements.add(scheduledSubtests[ii]);
                }
                SimpleCache.cacheResult("TSSubtestFormList", String.valueOf(itemSetId) + form, elements);
            } else {
                subtests.addAll(elements);
            }
        }
        return subtests;
    }
    
    private void updateTestRosters(String userName, Integer userId, ArrayList subtests, ScheduledSession newSession, Integer itemSetId, Double extendedTimeValue) throws CTBBusinessException {
        try {
            Integer productId = newSession.getTestSession().getProductId();
            TestProduct testProduct = product.getProduct(productId);
            boolean overrideUsingStudentManifest = false;
            if ("F".equalsIgnoreCase(testProduct.getStaticManifest())
                && "F".equalsIgnoreCase(testProduct.getSessionManifest()))
                overrideUsingStudentManifest = true;
            
            Integer testAdminId = newSession.getTestSession().getTestAdminId();
            String [] validForms = itemSet.getFormsForTest(itemSetId);
            Integer customerId = newSession.getTestSession().getCustomerId();
            String defaultCustomerFlagStatus = customerConfigurations.getDefaulCustomerFlagStatus(customerId);
            FormAssignmentCount [] formCounts = null;
            if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.ROUND_ROBIN)) {
                formCounts = formAssignments.getFormAssignmentCounts(testAdminId);
            }
            RosterElement [] oldUnits = rosters.getRosterForTestSession(testAdminId);
            HashMap oldMap = new HashMap();
            SessionStudent [] newUnits = newSession.getStudents();
            for(int i=0;i<oldUnits.length;i++) {
                   oldMap.put(oldUnits[i].getStudentId(), oldUnits[i]);
            }
            ArrayList assignments = new ArrayList();
            for(int j=0;newUnits!=null && j<newUnits.length;j++) {
                String form = newSession.getTestSession().getPreferredForm();
                SessionStudent newUnit = newUnits[j];
                //Integer testRosterId = null;
                RosterElement oldUnit = (RosterElement) oldMap.get(newUnit.getStudentId());
                RosterElement re = new RosterElement();
                // org node id is the only thing we can 'update'
                re.setOrgNodeId(newUnit.getOrgNodeId());
                re.setStudentId(newUnit.getStudentId());
                re.setTestAdminId(newSession.getTestSession().getTestAdminId());
                
                // Start changes for Student Pacing
                Double rosterExtendedTime = null;
                String extendedTimeAccom = "F";
                if(newUnit.getExtendedTimeAccom() != null){
                	extendedTimeAccom = newUnit.getExtendedTimeAccom();
                }
                // End changes for Student Pacing
                
                if(oldUnit == null) {
                    re.setOverrideTestWindow("F");
                    re.setStudentId(newUnit.getStudentId());
                    re.setTestAdminId(testAdminId);
                    re.setCreatedBy(userId);
                    re.setCreatedDateTime(new Date());
                    re.setCustomerId(customerId);
                    re.setActivationStatus("AC");
                    String password = PasswordGenerator.generatePassword();
                    re.setPassword(password);
                    //testRosterId = rosters.getNextPK();
                    //re.setTestRosterId(testRosterId);
                    re.setScoringStatus("NA");
                    if("PA".equals(newSession.getTestSession().getTestAdminStatus()))
                        re.setTestCompletionStatus("NT");
                    else 
                        re.setTestCompletionStatus("SC");
                    re.setValidationStatus("VA");
                    if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.MANUAL)) {
                        form = newUnit.getItemSetForm();
                    } else if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.ROUND_ROBIN)) {
                        form = TestFormSelector.getFormWithLowestCountAndIncrement(formCounts);
                    }
                    re.setFormAssignment(form);
                    re.setCustomerFlagStatus(defaultCustomerFlagStatus);
                    
                    // Start changes for Student Pacing
                    if(extendedTimeValue > 0){
                    	if(extendedTimeAccom != null && extendedTimeAccom.equalsIgnoreCase("T"))
                    		rosterExtendedTime = extendedTimeValue;
                    }
                    re.setExtendedTime(rosterExtendedTime);
                    // End changes for Student Pacing
                    try {
                    	//rosters.getConnection().setAutoCommit(false);
                        rosters.createNewTestRoster(re);
                    } catch (SQLException se) {
                        boolean validPassword = rosters.getRosterCountForPassword(password).intValue() == 0;
                        while(!validPassword) {
                            password = PasswordGenerator.generatePassword();
                            validPassword = rosters.getRosterCountForPassword(password).intValue() == 0;
                        }
                        re.setPassword(password);
                        //rosters.getConnection().setAutoCommit(false);
                        rosters.createNewTestRoster(re);
                    }
                    re = rosters.getRosterElementForStudentAndAdmin(re.getStudentId(), testAdminId);
                } else {
                    re.setTestRosterId(oldUnit.getTestRosterId());
                    re.setFormAssignment(oldUnit.getFormAssignment());
                    re.setValidationStatus(oldUnit.getValidationStatus());
                    re.setCustomerFlagStatus(oldUnit.getCustomerFlagStatus());
                    //testRosterId = oldUnit.getTestRosterId();
                    if(!("SC".equals(oldUnit.getTestCompletionStatus()) || "NT".equals(oldUnit.getTestCompletionStatus()))) {
                        if(!oldUnit.getOrgNodeId().equals(newUnit.getOrgNodeId())){
                            throw new SessionCreationException("ScheduleTestImpl: updateTestRosters: attempted to update a roster which has already begun testing");    
                        }else if(oldUnit.getFormAssignment() != null){
                            if(!oldUnit.getFormAssignment().equals(newUnit.getItemSetForm())) {
                                throw new SessionCreationException("ScheduleTestImpl: updateTestRosters: attempted to update a roster which has already begun testing");    
                            }
                        }else {
                           if(newUnit.getItemSetForm()!=null) 
                                throw new SessionCreationException("ScheduleTestImpl: updateTestRosters: attempted to update a roster which has already begun testing");    
                        }
                        form = oldUnit.getFormAssignment();
                    } else {
                        boolean validForm = false;
                        //re.setTestRosterId(testRosterId);
                        if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.MANUAL)) {
                            for(int k=0;k<validForms.length;k++) {
                                if(validForms[k].equals(newUnit.getItemSetForm())) {
                                    form = newUnit.getItemSetForm();
                                    validForm = true;
                                }
                            }
                            if(!validForm) {
                                if (overrideUsingStudentManifest)
                                    form = null;
                                else 
                                    form = TestFormSelector.getFormWithLowestCountAndIncrement(formCounts);
                            }
                        } else if (oldUnit.getFormAssignment() != null && !"".equals(oldUnit.getFormAssignment())) {
                            for(int k=0;k<validForms.length;k++) {
                                if(validForms[k].equals(oldUnit.getFormAssignment())) {
                                    form = oldUnit.getFormAssignment();
                                    validForm = true;
                                }
                            }
                            if(!validForm) {
                                if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.ROUND_ROBIN)) {
                                     form = TestFormSelector.getFormWithLowestCountAndIncrement(formCounts);
                                }
                            }
                        } else if(newSession.getTestSession().getFormAssignmentMethod().equals(TestSession.FormAssignment.ROUND_ROBIN)) {
                            form = TestFormSelector.getFormWithLowestCountAndIncrement(formCounts);
                        }
                        re.setFormAssignment(form);
                        
                        re.setValidationStatus("VA");
                        re.setCustomerFlagStatus(defaultCustomerFlagStatus);
                    }
                    if(!"PA".equals(newSession.getTestSession().getTestAdminStatus()) && "NT".equals(oldUnit.getTestCompletionStatus()))
                        re.setTestCompletionStatus("SC");
                    else if(!"PA".equals(newSession.getTestSession().getTestAdminStatus()) && "IC".equals(oldUnit.getTestCompletionStatus()))
                        re.setTestCompletionStatus("IN");
                    else if("PA".equals(newSession.getTestSession().getTestAdminStatus()) && "SC".equals(oldUnit.getTestCompletionStatus()))
                        re.setTestCompletionStatus("NT");
                    else if("PA".equals(newSession.getTestSession().getTestAdminStatus()) && "IN".equals(oldUnit.getTestCompletionStatus()))
                        re.setTestCompletionStatus("IC");
                    else
                        re.setTestCompletionStatus(oldUnit.getTestCompletionStatus());
                    // only need to update if something changed
                    if(!re.getOrgNodeId().equals(oldUnit.getOrgNodeId()) ||
                        (re.getFormAssignment() !=null && !re.getFormAssignment().equals(oldUnit.getFormAssignment()) )||
                        !re.getTestCompletionStatus().equals(oldUnit.getTestCompletionStatus()) ||
                        !re.getValidationStatus().equals(oldUnit.getValidationStatus()) ||
                        (re.getCustomerFlagStatus() != null && !re.getCustomerFlagStatus().equals(oldUnit.getCustomerFlagStatus()))  ) {
                    	//rosters.getConnection().setAutoCommit(false);
                        rosters.updateTestRoster(re);
                    }
                    oldMap.remove(newUnit.getStudentId());
                }
                if(oldUnit == null || ("SC".equals(oldUnit.getTestCompletionStatus()) || "NT".equals(oldUnit.getTestCompletionStatus()))) {
                    StudentSubtestAssignment assignment = new StudentSubtestAssignment();
                    assignment.setForm(form);
                    assignment.setTestAdminId(re.getTestAdminId());
                    assignment.setStudentId(re.getStudentId());
                    assignment.setTestRosterId(re.getTestRosterId());
                    if (overrideUsingStudentManifest) {
                        assignment.setSubtests(getTDTestElementList(newUnit.getStudentManifests()));
                    } else {
                        assignment.setSubtests(subtests);
                    }
                    assignments.add(assignment);
                }
            }
            updateStudentItemSetStatusRecords(customerId, assignments);
            if(!newSession.isFromTAS()){
            	return;
            } else{
            	Iterator iter = oldMap.values().iterator();
                while (iter.hasNext()) {
                    RosterElement oldUnit = (RosterElement) iter.next();
                    if(!("SC".equals(oldUnit.getTestCompletionStatus()) || "NT".equals(oldUnit.getTestCompletionStatus()))) {
                        throw new SessionCreationException("ScheduleTestImpl: updateTestRosters: attempted to remove a roster which has already begun testing");  
                    } else {
                        // can only remove rosters to which editing user has permission
                        if(students.isStudentEditableByUser(userName, oldUnit.getStudentId()).intValue() > 0 ? true : false) {
                        	//siss.getConnection().setAutoCommit(false);
                        	siss.deleteStudentItemSetStatusesForRoster(oldUnit.getTestRosterId());
                        	//rosters.getConnection().setAutoCommit(false);
                        	rosters.deleteTestRoster(oldUnit);
                        }
                    }
                }
            }
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: updateTestRosters: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void createStudentItemSetStatusRecords(Integer customerId, ArrayList manifests) throws SessionCreationException {
        try {
            HashMap subtestMap = new HashMap();
            Iterator manifestIterator = manifests.iterator();
            while(manifestIterator.hasNext()) {
                StudentSubtestAssignment manifest = (StudentSubtestAssignment) manifestIterator.next();
                if(manifest.getSubtests().size() < 1) {
                    throw new SessionCreationException("ScheduleTestImpl: updateStudentItemSetStatusRecords: no subtests for assigned form: " + manifest.getForm());
                }
                Iterator iter = manifest.getSubtests().iterator();
                int sub=0;
                while(iter.hasNext()) {
                    TestElement subtest = (TestElement) iter.next();
                    if(manifest.getForm() == null 
                        || subtest.getItemSetForm().equals(manifest.getForm())) { //null form is wildcard
                        Integer order = new Integer(sub);
                        HashMap orderMap = (HashMap) subtestMap.get(subtest.getItemSetId());
                        if(orderMap == null) {
                            orderMap = new HashMap();
                        }
                        ArrayList rosterList = (ArrayList) orderMap.get(order);
                        if(rosterList == null) {
                            rosterList = new ArrayList();
                        }
                        rosterList.add(manifest);
                        orderMap.put(order, rosterList);
                        subtestMap.put(subtest.getItemSetId(), orderMap);
                        sub++;
                    }
                }
            }
            Iterator subtestIter = subtestMap.keySet().iterator();
            while(subtestIter.hasNext()) {
                Integer subtestId = (Integer) subtestIter.next();
                HashMap orderMap = (HashMap) subtestMap.get(subtestId);
                Iterator orderIter = orderMap.keySet().iterator();
                while(orderIter.hasNext()) {
                    Integer order = (Integer) orderIter.next();
                    ArrayList rosterList = (ArrayList) orderMap.get(order);
                    Iterator rosterIter = rosterList.iterator();
                    String admins = "";
                    String students = "";
                    int rosterCount = 0;
                    while(rosterIter.hasNext()) {
                        StudentSubtestAssignment roster = ((StudentSubtestAssignment) rosterIter.next());
                        if("".equals(admins) || "".equals(students)) {
                            admins = String.valueOf(roster.getTestAdminId());
                            students = String.valueOf(roster.getStudentId());
                        } else {
                            admins = admins + "," + String.valueOf(roster.getTestAdminId());
                            students = students + "," + String.valueOf(roster.getStudentId());
                        }
                        rosterCount++;
                        //START -added for LLO-109 change
                        if(rosterCount >= 999) {
                        	//siss.getConnection().setAutoCommit(false);
                            siss.createNewStudentItemSetStatus(customerId, admins, students, subtestId, order, "F", "VA", "SC","N","N");
                            rosterCount = 0;
                            admins = "";
                            students = "";
                        }
                    }
                    if(!"".equals(admins) && !"".equals(students)) {
                    	//siss.getConnection().setAutoCommit(false);
                        siss.createNewStudentItemSetStatus(customerId, admins, students, subtestId, order, "F", "VA", "SC","N","N");
                    }
                    //END - added for LLO-109 change
                }
            }
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: createStudentItemSetStatusRecords: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }
    
    private void updateStudentItemSetStatusRecords(Integer customerId, ArrayList manifests) throws SessionCreationException {
        try {
            Iterator manifestIterator = manifests.iterator();
            while(manifestIterator.hasNext()) {
                StudentSubtestAssignment assignment = (StudentSubtestAssignment) manifestIterator.next();
                //siss.getConnection().setAutoCommit(false);
                siss.deleteStudentItemSetStatusesForRoster(assignment.getTestRosterId());
            }
            createStudentItemSetStatusRecords(customerId, manifests);
        } catch (SQLException se) {
            SessionCreationException sce = new SessionCreationException("ScheduleTestImpl: updateStudentItemSetStatusRecords: " + se.getMessage());
            sce.setStackTrace(se.getStackTrace());
            throw sce;
        }
    }  
    
     /**
     * Retrieves all information pertaining to a test session, including the schedulable unit manifest,
     * the proctor list.
     * The returned ScheduledSession object can be edited and passed
     * back in via the updateTestSession or createNewTestSession methods to persist changes or create a
     * copy if the copyable flag is 'T'.
     * The copyable flag will be set to 'T' if the requesting user
     * has a non-proctor role, the test is active and distributed
     * to the requesting user's top node, and the requesting user
     * has the scheduling node as one of their top nodes. Otherwise
     * the flag will be set to 'F'. The ScheduledSession.testSession object also contains a overrideFormAssigmentMethod field, which
     * if populated (see TestSession.FormAssignment constants) indicates that
     * only the specified assignment method should be available for scheduling, and a
     * overrideLoginStartDate field, which if populated indicates that no earlier (than the override date) login
     * start date can be chosen for the session.
     * @common:operation
     * @param userName - identifies the user
     * @param testAdminId - identifies the test session
     * @return ScheduledSession
     * @throws CTBBusinessException
     */    
    public ScheduledSession  getTestSessionDataWithoutRoster(java.lang.String userName, java.lang.Integer testAdminId) throws com.ctb.exception.CTBBusinessException{
      
      validator.validateAdmin(userName, testAdminId, "getTestSessionDataWithoutRoster");
        try {
            ScheduledSession session = new ScheduledSession();
            int studentsLoggedIn = 0;
            User [] proctors = users.getProctorUsersForAdmin(testAdminId);
            for(int i=0;i<proctors.length;i++) {
                boolean editable = users.isUserEditableByUserForAdmin(userName, proctors[i].getUserId(), testAdminId).intValue() > 0;
                proctors[i].setCopyable(editable ? "T" : "F");
                editable = editable && !proctors[i].getUserName().equals(userName);
                proctors[i].setEditable(editable ? "T" : "F");
            }
            session.setProctors(proctors);
            TestElement [] testUnits = itemSet.getTestElementsForSession(testAdminId);
            session.setScheduledUnits(testUnits);
            
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
                      
            String [] forms = itemSet.getFormsForTest(testSession.getItemSetId());
            for(int i=0;i<testUnits.length && testUnits[i] != null;i++) {
                TestElement testUnit = testUnits[i];
                testUnit.setForms(forms);
            }
            TestAdminStatusComputer.adjustSessionTimesToLocalTimeZone(testSession);
            session.setTestSession(testSession);
            session.setStudentsLoggedIn(new Integer(studentsLoggedIn));
            session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getTestSessionDataWithoutRoster: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
        
    }     
    
    /**
     * Retrieves the manifest for the  given roster
     * 
     *  @common:operation
     * @userName -  Identifies the user
     * @ rosterId  -  Identifies the test roster id
     * @param filter - filtering params
	 * @param page - paging params
	 * @param sort - sorting params
     */
    public StudentManifestData  getManifestForRoster(java.lang.String userName, java.lang.Integer studentId,java.lang.Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws com.ctb.exception.CTBBusinessException
    {
        // validator.validate(userName,studentId,"getManifestForRoster");
            try{
                  StudentManifestData smd = new StudentManifestData();
                  StudentManifest []  sm =  siss.getStudentManifestsForRoster(studentId,testAdminId);
                  ArrayList smAr = getFilteredStudentManifestForRoster(sm);
                  sm = new StudentManifest[smAr.size()];
                  for (int i=0;i<smAr.size();i++)
                  {
                    sm[i] =(StudentManifest)smAr.get(i);                    
                  }                 
                  Integer pageSize = null;
                  if(page != null)
                    pageSize = new Integer(page.getPageSize());
                                   
                  smd.setStudentManifests(sm,pageSize);                                
                  if(filter != null) smd.applyFiltering(filter);
                  if(sort != null) smd.applySorting(sort);
                  if(page != null) smd.applyPaging(page);
                  return smd;                  
            }catch(SQLException se){
                CTBBusinessException cbe = new RosterDataNotFoundException("ScheduleTestImpl: getManifestForRoster: " + se.getMessage());
                cbe.setStackTrace(se.getStackTrace());
                throw cbe;                
            }        
    }
    
    private ArrayList getFilteredStudentManifestForRoster(StudentManifest [] studentManifest) throws RosterDataNotFoundException
    {
            ArrayList  sms;
            
                Integer tsId = null;
                Integer counter = null;
                HashMap smhs = new HashMap();            
                for(int i=0;studentManifest != null && i<studentManifest.length;i++){
                    StudentManifest sm = studentManifest[i];
                    if(!sm.getItemSetId().equals(tsId))
                    {                
                        smhs.put(sm.getItemSetId(),sm.getItemSetForm());
                    }else{
                        smhs.remove(sm.getItemSetId());
                        smhs.put(sm.getItemSetId(),"1");
                    }                   
                    tsId = sm.getItemSetId();
                }
                sms =  new ArrayList();
                tsId = null;
                for (int j=0;j<studentManifest.length;j++)
                {   
                    StudentManifest sm2 = studentManifest[j];
                   if(!sm2.getItemSetId().equals(tsId))
                    {   
                        if(smhs.get(sm2.getItemSetId()).equals("1"))
                        {
                            sm2.setItemSetForm(null);
                        }else{                  
                            sm2.setItemSetForm(smhs.get(sm2.getItemSetId()).toString());
                        }
                        sms.add(sm2);
                    }            
                    tsId = sm2.getItemSetId();                    
                }     
                       
            return sms;
    }
    
    /**
     * Adds a new roster element to the specified session. The scheduling student contains the manifest 
     * (collection of subtests) selected for the student. This will be used by the rapid registration 
     * process to add a student to an existing session. This call should update license usage quantities 
     * appropriately, as described above in the �Product licensure/usage? section.
     * @userName - - identifies the user
     * @SchedulingStudent - 
     * @testAdminId - Identifies the test session
     *  @common:operation
     */
     public RosterElement addStudentToSession(String userName, com.ctb.bean.testAdmin.SessionStudent sessionStudent, Integer testAdminId)throws com.ctb.exception.CTBBusinessException
     {       
       // validator.validate(userName,testAdminId,"addStudentToSession");
        RosterElement roster = new RosterElement(); 
        Double extendedTimeValue = 0.0;// added for student pacing
        
    
        try{  
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
            Integer userId = users.getUserIdForName(userName);
            Integer customerId = testSession.getCustomerId();
            String defaultCustomerFlagStatus = customerConfigurations.getDefaulCustomerFlagStatus(customerId);
            
            // Start changes for Student Pacing
    		CustomerConfigurationValue [] customerConfigurationValues = customerConfigurations.getCustomerConfigurationValue(customerId,"Extended_Time");				
    		if (customerConfigurationValues != null && customerConfigurationValues.length > 0) 
            {
    			extendedTimeValue = new Double (customerConfigurationValues[0].getCustomerConfigurationValue());
            }
    		// End changes for Student Pacing
            
            String form = testSession.getFormAssignmentMethod();
            Integer productId = testSession.getProductId();
            
            if(sessionStudent != null){                       
                Student student =(Student) sessionStudent; 
                Integer studentId = student.getStudentId(); 
                
                // Start changes for Student Pacing
                Double rosterExtendedTime = null;
                String extendedTimeAccom = "F";
                String studentExtendedTimeAccom = rosters.getExtendedTimeAccomForStudent(studentId);

                if(studentExtendedTimeAccom != null){
                	extendedTimeAccom = studentExtendedTimeAccom;
                }
                
                // End changes for Student Pacing
                
                
                roster.setActivationStatus("AC");
                roster.setCreatedBy(userId);
                roster.setCreatedDateTime(new Date());
                roster.setStartDateTime(null);
                roster.setCompletionDateTime(null);
                roster.setCustomerId(customerId);
                roster.setFormAssignment(form);
                roster.setOrgNodeId(sessionStudent.getOrgNodeId());
                roster.setOverrideTestWindow("F");
                String password = PasswordGenerator.generatePassword();
                roster.setPassword(password);
                roster.setScoringStatus("NA");
                roster.setStudentId(studentId);
                roster.setTestAdminId(testAdminId);
                roster.setTestCompletionStatus("SC");
                roster.setValidationStatus("VA");
                roster.setCustomerFlagStatus(defaultCustomerFlagStatus);
                
                // Start changes for Student Pacing
                if(extendedTimeValue > 0){
                	if(extendedTimeAccom != null && extendedTimeAccom.equalsIgnoreCase("T"))
                		rosterExtendedTime = extendedTimeValue;
                }
                 roster.setExtendedTime(rosterExtendedTime);
                // End changes for Student Pacing
            
                try {
                	//rosters.getConnection().setAutoCommit(false);
                    rosters.createNewTestRoster(roster);
                } catch (SQLException se) {
                    boolean validPassword = rosters.getRosterCountForPassword(password).intValue() == 0;
                    while(!validPassword) {
                        password = PasswordGenerator.generatePassword();
                        validPassword = rosters.getRosterCountForPassword(password).intValue() == 0;
                    }
                    roster.setPassword(password);
                    //rosters.getConnection().setAutoCommit(false);
                    rosters.createNewTestRoster(roster);
                }                
                
                StudentManifest [] studentManifests = sessionStudent.getStudentManifests();
                checkTestAdminItemSets(testAdminId,studentManifests);
                roster = rosters.getRosterElementForStudentAndAdmin(studentId,testAdminId);
                
                Integer rosterId = roster.getTestRosterId(); 
                
                int subtestOrder = 0;  
                
                for(int i=0;studentManifests != null && i<studentManifests.length;i++) {
                    StudentManifest studentManifest = studentManifests[i];
                    if(studentManifest != null) {
                        Integer parentItemsetId = studentManifest.getItemSetId();
                        String tdform =  studentManifest.getItemSetForm();
                        if(tdform == null || tdform.length()==0 || tdform.trim().length()==0)
                                tdform = "%";
                        Integer [] itemSetIds = siss.getItemSetIdsForFormForParent(parentItemsetId,tdform);
                        for(int j=0;j<itemSetIds.length;j++){
                                StudentSessionStatus sss = new StudentSessionStatus();
                                sss.setItemSetId(itemSetIds[j]);
                                sss.setCompletionStatus("SC");
                                sss.setItemSetOrder(new Integer(subtestOrder));
                                sss.setStartDateTime(null);
                                sss.setCompletionDateTime(null);
                                sss.setValidationStatus("VA");
                                sss.setTimeExpired("F");
                                sss.setValidationUpdatedBy(userId);
                                sss.setValidationUpdatedDateTime(new Date());
                                sss.setValidationUpdatedNote("");
                                //for defect #-  65787
                                //rosters.getConnection().setAutoCommit(false);
                                rosters.createNewStudentItemSetStatusForRoster(customerId, sss,rosterId);
                                subtestOrder++;
                            }
                        }
                    }
             }
            //license update 
            /*
                OrgNodeLicenseInfo [] orgNodeLicenseInfos = orgNode.getLicenseInfoForUserOrgNode(customerId,productId,userId);                    
                for(int j=0;orgNodeLicenseInfos != null && j<orgNodeLicenseInfos.length;j++){                        
                    OrgNodeLicenseInfo orgNodeLicenseInfo = (OrgNodeLicenseInfo) orgNodeLicenseInfos[j];   
                    if(orgNodeLicenseInfo.getLicPurchased().intValue() != -1){
                        Integer orgNodeId = orgNodeLicenseInfo.getOrgNodeId();
                        Integer licPurchased = new Integer(orgNodeLicenseInfo.getLicPurchased().intValue()-1);
                        Integer licReserved = new Integer(orgNodeLicenseInfo.getLicReserved().intValue()+1);
                        orgNode.updateLicenseInfoForUserOrgNode(orgNodeId,customerId,productId,licPurchased,licReserved);
                        break;
                    }
                    else if(orgNodeLicenseInfo.getLicPurchased().intValue() == 0){
                             throw new ZeroLicensesException("Zero licenses");
                    }                    
                }      */          
                return roster;
        } catch (Exception se) {
        	CTBBusinessException ctbe = null;
            String message = se.getMessage().toLowerCase();
            if(message.indexOf("insufficient available license quantity") >=0) {
                ctbe = new InsufficientLicenseQuantityException("Insufficient available license quantity");
            } else {
                ctbe = new StudentNotAddedToSessionException("ScheduleTestImpl: addStudentToSession: " + message);
                ctbe.setStackTrace(se.getStackTrace());
            }
            throw ctbe;
        }
     } 
 
    /**
     * Adjusts the manifest as specified for a roster element/student 
     * already part of an existing session. This will be used in the 
     * edit test session UI to persist changes to the manifest or subtest 
     * selection for a particular student within an existing test session. 
     * This call should fail if the roster element is not in an editable completion state (eg. Not SC)
     * @param userName - identifies the user
     * @param StudentManifestData - identifies StudentManifest info
     * @return RosterElement - roster
     * @throws CTBBusinessException
     *  @common:operation
     */
    
     public RosterElement updateManifestForRoster(java.lang.String userName,java.lang.Integer studentId,java.lang.Integer stdentOrgNodeId,java.lang.Integer testAdminId, com.ctb.bean.testAdmin.StudentManifestData studentManifestData) throws com.ctb.exception.CTBBusinessException
     {
       // validator.validate(userName,studentId,"updateManifestForRoster");
        //START -Added for deferred defect #64306 and #64308  
        try{	
        //END -Added for deferred defect #64306 and #64308        		
                 Integer  userId  = users.getUserIdForName(userName);     
                 Integer customerId = users.getCustomerIdForName(userName);            
                 String completionStatus =  siss.geCompletionStatusForRoster(studentId,testAdminId);                 
                 if(!completionStatus.equals("SC"))
                    throw new NotEditableManifestException("This Student manifest can not be changed");
                 RosterElement roster = null; 
                 
                 if(studentManifestData != null){
                  //  Integer rosterId = rosters.getRosterIdForStudentAndTestAdmin(studentId,testAdminId);
                    roster = rosters.getRosterElementForStudentAndAdmin(studentId,testAdminId);
                    if(!roster.getOrgNodeId().equals(stdentOrgNodeId) && stdentOrgNodeId != null){
                        OrgNodeStudent orgNodeStd =  orgNodeStudent.getValidStudentOrgNode(studentId,stdentOrgNodeId);
                        if(orgNodeStd.getOrgNodeId() != null){
                            roster.setOrgNodeId(orgNodeStd.getOrgNodeId());
                            //rosters.getConnection().setAutoCommit(false);
                            rosters.updateTestRoster(roster);
                         }
                    }                   
                    Integer rosterId = roster.getTestRosterId();
                    //siss.getConnection().setAutoCommit(false);
                    siss.deleteStudentItemSetStatusesForRoster(rosterId);
                    StudentManifest[] studentManifests = studentManifestData.getStudentManifests();  
                    checkTestAdminItemSets(testAdminId,studentManifests);
                    int subtestOrder = 0;
                    for(int i=0;studentManifests != null && i<studentManifests.length;i++) {
                        StudentManifest studentManifest = studentManifests[i];
                        if(studentManifest != null) {
                            Integer parentItemsetId = studentManifest.getItemSetId();
                            String form =  studentManifest.getItemSetForm();
                            if(form == null || form.length()==0 || form.trim().length()==0)
                                form = "%";
                            
                           Integer [] itemSetIds = siss.getItemSetIdsForFormForParent(parentItemsetId,form);
                            for(int j=0;j<itemSetIds.length;j++){
                                StudentSessionStatus sss = new StudentSessionStatus();
                                sss.setItemSetId(itemSetIds[j]);
                                sss.setCompletionStatus("SC");
                                sss.setItemSetOrder(new Integer(subtestOrder));
                                sss.setStartDateTime(null);
                                sss.setCompletionDateTime(null);
                                sss.setValidationStatus("VA");
                                sss.setTimeExpired("F");
                                sss.setValidationUpdatedBy(userId);
                                sss.setValidationUpdatedDateTime(new Date());
                                sss.setValidationUpdatedNote("");
                                //siss.getConnection().setAutoCommit(false);
                                siss.createNewStudentItemSetStatusForRoster(customerId, sss, rosterId);
                                subtestOrder++;
                            }                            
                        }                    
                    }
                 }    
               //  RosterElement roster = rosters.getRosterElementForStudentAndAdmin(studentId,testAdminId);                
                 return roster;
         } 
         //START- Added for deferred defect #64306 and #64308
         
         catch(Exception se){

     	 CTBBusinessException muf = null;
     	 String message = se.getMessage().toLowerCase();
     	 if(message.indexOf("insufficient available license quantity") >=0) {
     		 muf = new InsufficientLicenseQuantityException("Insufficient available license quantity");
          } else {
         	 muf = new ManifestUpdateFailException("ScheduleTestImpl: getManifestForRoster: " + se.getMessage());
              muf.setStackTrace(se.getStackTrace());
          }
         throw muf; 
         }
        //END- Added for deferred defect #64306 and #64308   
     }  
     
     /**
     * Delete the  Student Item set Status for a roster and the roster for a student.
     * @param userName - identifies the user
     * @param studentId -  identifies the student
     * @param testAdminId -  identifies the test session
     * @throws CTBBusinessException
     *  @common:operation
     */
     public void deleteAddedStudentFromSession(String userName,Integer studentId,Integer testAdminId) throws com.ctb.exception.CTBBusinessException
     {
           // validator.validate(userName,studentId,"deleteAddedStudentFormSession");
        try{            
            RosterElement re  = rosters.getRosterElementForStudentAndAdmin(studentId,testAdminId);
            //siss.getConnection().setAutoCommit(false);
            siss.deleteStudentItemSetStatusesForRoster(re.getTestRosterId());
            //rosters.getConnection().setAutoCommit(false);
            rosters.deleteTestRoster(re);            
        }catch(SQLException se){
            CTBBusinessException cbe = new RosterDataNotFoundException("ScheduleTestImpl: deleteAddedStudentFromSession: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
        }
     }      
     
    /**
     * Retrieves the TestProduct for the given testAdminId
     * 
     *  @common:operation
     * @userName -  Identifies the user
     * @ testAdminId  -  Identifies the test admin
     */
    public TestProduct  getProductForTestAdmin(String userName, Integer testAdminId) throws com.ctb.exception.CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getProductForTestAdmin");
            try{
                TestProduct tp = product.getProductForTestAdmin(testAdminId);
                return tp;                  
            }catch(SQLException se){
                CTBBusinessException cbe = new RosterDataNotFoundException("ScheduleTestImpl: getProductForTestAdmin: " + se.getMessage());
                cbe.setStackTrace(se.getStackTrace());
                throw cbe;                
            }        
    }
    
    
     /**
     * @common:operation
     */
    public String isTestAdminExists(Integer testAdminId) throws CTBBusinessException {
        
        String isExistsTestAdmin = null;
        
        try {
              
              isExistsTestAdmin = admins.isTestAdminExists(testAdminId);
           
        } catch (SQLException se) {
            
            TestAdminDataNotFoundException tae = new TestAdminDataNotFoundException("ScheduleTestImpl:isTestAdminExists");
            tae.setStackTrace(se.getStackTrace());
            throw tae;
        }
        
        return isExistsTestAdmin;
    } 
    
    
    /**
     * Retrieves an array of TABERecommendedLevel for a student based on his prior locator tests' 
     * recommended levels.
     * @common:operation
	 * @param userName - identifies the user
     * @param studentId - identifies the student
     * @param testItemSetId - identifies the test
	 * @param locatorItemSetId - identifies the locator test
	 * @return TABERecommendedLevel []
	 * @throws com.ctb.exception.CTBBusinessException
     */
    public TABERecommendedLevel [] getTABERecommendedLevelForStudent(String userName, Integer studentId, Integer testItemSetId, Integer locatorItemSetId) throws CTBBusinessException
    {
        validator.validateItemSet(userName, testItemSetId, "testAdmin.getTABERecommendedLevelForStudent");
        ArrayList result = new ArrayList();
        try {
            TABERecommendedLevel [] recommendedLevels = studentItemSetStatus.getTABERecommendedLevelForStudent(studentId, locatorItemSetId);
            HashMap hashmap = new HashMap();
            for (int i = 0; i < recommendedLevels.length; i++) {
                String key = null;
                if (recommendedLevels[i].getItemSetName().indexOf("Reading")>0)
                    key = "Reading";
                else if (recommendedLevels[i].getItemSetName().indexOf("Mathematics")>0)
                    key = "Mathematics";
                else if (recommendedLevels[i].getItemSetName().indexOf("Language")>0)
                    key = "Language";

                if (key != null) {
                    if (hashmap.containsKey(key)) {
                        TABERecommendedLevel cachedRL = (TABERecommendedLevel) hashmap.get(key);
                        if (recommendedLevels[i].getCompletedDate().after(cachedRL.getCompletedDate()))
                            hashmap.put(key, recommendedLevels[i]);
                    }
                    else {
                        hashmap.put(key, recommendedLevels[i]);
                    }                    
                }                    
            }

            TestElement [] tes = itemSet.getTestElementsForParent(testItemSetId, "TS");
            for (int i = 0; i < tes.length; i++) { 
                if (!tes[i].getItemSetLevel().equals("L")) {
                String key = null;
                    if (tes[i].getItemSetName().indexOf("Reading")>0 
                        || tes[i].getItemSetName().indexOf("Vocabulary")>0)
                        key = "Reading";
                    else if (tes[i].getItemSetName().indexOf("Mathematics")>0)
                        key = "Mathematics";
                    else if (tes[i].getItemSetName().indexOf("Language")>0
                        || tes[i].getItemSetName().indexOf("Spelling")>0)
                        key = "Language";
                    if (key != null && hashmap.containsKey(key)) {
                        TABERecommendedLevel cashedRL = (TABERecommendedLevel) hashmap.get(key);
                        TABERecommendedLevel newRL = new TABERecommendedLevel();
                        newRL.setTestAdminId(cashedRL.getTestAdminId());
                        newRL.setTestAdminName(cashedRL.getTestAdminName());
                        newRL.setCompletedDate(cashedRL.getCompletedDate());
                        newRL.setRecommendedLevel(cashedRL.getRecommendedLevel());
                        newRL.setItemSetId(tes[i].getItemSetId());
                        newRL.setItemSetName(tes[i].getItemSetName());
                        result.add(newRL);
                    }
                    else {
                        TABERecommendedLevel newRL = new TABERecommendedLevel();
                        newRL.setItemSetId(tes[i].getItemSetId());
                        newRL.setItemSetName(tes[i].getItemSetName());
                        result.add(newRL);
                    }
                }
            }
            return (TABERecommendedLevel []) result.toArray(new TABERecommendedLevel[0]);
        } catch (SQLException se) {
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: getTABERecommendedLevelForStudent: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
        
    }    
    
    private void checkTestAdminItemSets(Integer testAdminId, StudentManifest [] studentManifests) throws CTBBusinessException
    {
        try{            
            StudentManifest [] totalStudentManifest = siss.getTotalStudentManifest(testAdminId);
            HashMap totalSM = new HashMap();
            HashMap orderHM = new HashMap();
            int subtestOrder = 0;
            for(int j=0;j<studentManifests.length;j++){                
                    totalSM.put(studentManifests[j].getItemSetId(),studentManifests[j]);  
                    orderHM.put(new Integer(subtestOrder),studentManifests[j].getItemSetId());
                    subtestOrder++;
            }
            if(totalStudentManifest != null){                
                for (int k = 0;k<totalStudentManifest.length;k++){
                    if(!totalSM.containsKey(totalStudentManifest[k].getItemSetId())){
                        totalSM.put(totalStudentManifest[k].getItemSetId(),totalStudentManifest[k]);   
                        orderHM.put(new Integer(subtestOrder),totalStudentManifest[k].getItemSetId());
                        subtestOrder++;
                    }
                }
            }            
            String brakeFlag = admins.getTestBrakesFlag(testAdminId);
            ScheduleElement [] scheduleElements = tais.getTestAdminItemSets(testAdminId);
            HashMap sessionSubtestsHM = new HashMap();          
            String accessCode = "";
            HashMap tacHM = new HashMap();
            boolean  isLocator = false;
            int itemSetOrder=0;           
            for(int i=0;scheduleElements != null && i<scheduleElements.length;i++)
            { 
                sessionSubtestsHM.put(scheduleElements[i].getItemSetId(),scheduleElements[i]);
                accessCode = scheduleElements[i].getAccessCode();  
                tacHM.put(scheduleElements[i].getItemSetId(),scheduleElements[i].getAccessCode());          
            }
            if(sessionSubtestsHM.size()>0)
                itemSetOrder = sessionSubtestsHM.size();
            
           List orderedSubtests = getOrderedSubtests(totalSM,orderHM);
           Iterator totalManifestItr =  orderedSubtests.iterator();
            while(totalManifestItr.hasNext()){
                StudentManifest sm = (StudentManifest)totalManifestItr.next();
                Integer itemSetId = sm.getItemSetId();    
                if(!sessionSubtestsHM.containsKey(itemSetId)|| sessionSubtestsHM.size()==0)
                {                                         
                    if(sm.getItemSetName().indexOf("Locator")>0){
                       isLocator = true;
                       itemSetOrder = updateSubtestsOrder(testAdminId);
                    }                                        
                    ScheduleElement se = new ScheduleElement();                        
                    se.setItemSetId(sm.getItemSetId());
                    se.setTestAdminId(testAdminId);
                    if(isLocator){
                        se.setItemSetOrder(new Integer(0)); 
                        isLocator = false;                       
                    }else{
                        se.setItemSetOrder(new Integer(itemSetOrder));
                    }
                    se.setSessionDefault("F"); //sessionDefault
                    if(!"T".equals(brakeFlag))
                        se.setAccessCode(accessCode);
                    else{
                        if(sm.getTestAccessCode()!= null){
                            se.setAccessCode(sm.getTestAccessCode());
                        }else{
                            boolean validCode = false;
                            String code = null;
                            while(!validCode){
                               code =  AccessCodeGenerator.generateAccessCode();
                               if(!tacHM.containsValue(code)){
                                    validCode = true;
                                    tacHM.put(itemSetId,code);
                               }
                            }
                            se.setAccessCode(code);
                        }                        
                    }
                    //tais.getConnection().setAutoCommit(false);
                    tais.createNewTestAdminItemSet(se);
                    itemSetOrder++; 
                }else{
                    sessionSubtestsHM.remove(itemSetId);  
                }
            }  
            if(sessionSubtestsHM != null){
               Iterator sessionSubtestItr = sessionSubtestsHM.values().iterator();
               while(sessionSubtestItr.hasNext()){
                    ScheduleElement se = (ScheduleElement)sessionSubtestItr.next();
                    if ("F".equals(se.getSessionDefault())){
                    	//tais.getConnection().setAutoCommit(false);
                        tais.deleteTestAdminItemSet(se);
                    }
               }
            }            
        }catch(SQLException se){
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: checkSubtestInSession: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    private int updateSubtestsOrder(Integer testAdminId) throws CTBBusinessException
    {        
        try{
            ScheduleElement [] scheduleElements = tais.getTestAdminItemSets(testAdminId);                
           if(scheduleElements.length>0){
               for(int i=0;i<scheduleElements.length;i++){                
                    ScheduleElement se = scheduleElements[i];                    
                    se.setItemSetOrder(new Integer(i+1));
                    //tais.getConnection().setAutoCommit(false);
                    tais.updateTestAdminItemSetOrder(se);
               }    
            }   
           return  scheduleElements.length;           
        }catch(SQLException se){
            TestElementDataNotFoundException tee = new TestElementDataNotFoundException("ScheduleTestImpl: check Subtests order in Session: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
     
    private List  getOrderedSubtests(HashMap subtestsHM, HashMap orderHM) throws CTBBusinessException
    {
        
        Iterator orderItr = orderHM.keySet().iterator();
        int maxOrder = 0;
        while(orderItr.hasNext()){
           int  testOrder = ((Integer)orderItr.next()).intValue();
           if(maxOrder < testOrder)
                maxOrder = testOrder;
        }
        if(maxOrder < subtestsHM.size())
            maxOrder = subtestsHM.size();
        List orderedSubtests = new ArrayList();
        for(int i=0;i <= maxOrder;i++){
            if(orderHM.containsKey(new Integer(i)) && subtestsHM.containsKey(orderHM.get(new Integer(i)))){
                Integer itemSetId = (Integer)orderHM.get(new Integer(i));
                StudentManifest sm = (StudentManifest)subtestsHM.get(itemSetId);
                orderedSubtests.add(sm);
            }
        }
        return orderedSubtests;
    }
    
    @Override
    public ScheduledSession getScheduledSessionDetails(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledSessionDetails");
        try {
            ScheduledSession session = new ScheduledSession();
            int studentsLoggedIn = 0;
          
            TestElement [] testUnits = itemSet.getTestElementsForSession(testAdminId);
            session.setScheduledUnits(testUnits);
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
            
            Integer productId = testSession.getProductId();
            TestProduct testProduct = product.getProduct(productId);
            testSession.setProductType(testProduct.getProductType());
            testSession.setOffGradeBlocked(testProduct.getOffGradeTestingDisabled());
            
            String [] forms = itemSet.getFormsForTest(testSession.getItemSetId());
            for(int i=0;i<testUnits.length && testUnits[i] != null;i++) {
                TestElement testUnit = testUnits[i];
                testUnit.setForms(forms);
            }
            
            studentsLoggedIn =  students.getLoggedInSessionStudentCountForAdmin(testAdminId);
           
            TestAdminStatusComputer.adjustSessionTimesToLocalTimeZone(testSession);
            session.setTestSession(testSession);
            session.setStudentsLoggedIn(new Integer(studentsLoggedIn));
            session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledSessionDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    @Deprecated 
    public ScheduledSession getScheduledStudentsDetails(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledStudentsDetails");
        try {
            ScheduledSession session = new ScheduledSession();
            int studentsLoggedIn = 0;
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
            Integer productId = testSession.getProductId();
            TestProduct testProduct = product.getProduct(productId);
            boolean overrideUsingStudentManifest = false;
            if ("F".equalsIgnoreCase(testProduct.getStaticManifest())
                && "F".equalsIgnoreCase(testProduct.getSessionManifest()))
                overrideUsingStudentManifest = true;
            
           SessionStudent [] roster = students.getSessionStudentsForAdmin(testAdminId);
            boolean testRestricted = "T".equals(admins.isTestRestricted(testSession.getItemSetId()))?true:false;
            for(int i=0;i<roster.length;i++) {
                if (overrideUsingStudentManifest) {
                    StudentManifest [] studentManifests = studentItemSetStatus.getStudentManifestsForRoster(roster[i].getStudentId(),testAdminId);
                    ArrayList smAr = getFilteredStudentManifestForRoster(studentManifests);
                    roster[i].setStudentManifests((StudentManifest [])smAr.toArray(new StudentManifest[0]));
                }
                boolean copyable = students.isStudentEditableByUserForOrg(userName, roster[i].getStudentId(), roster[i].getOrgNodeId()).intValue() > 0;
                EditCopyStatus status = roster[i].getStatus();
                status.setEditable("T");
                status.setCopyable("T");
                if(!copyable) {
                    status.setCopyable("F");
                    status.setEditable("F");
                    status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                }
                Integer restrictedAdmin = new Integer(-1);
                if(testRestricted) {
                    restrictedAdmin = students.isTestRestrictedForStudent(userName, roster[i].getStudentId(), testSession.getItemSetId());
                }
                if(restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    status.setCopyable("F");
                    status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                    status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                }
                if(!"F".equals(roster[i].getTested())) {
                    status.setEditable("F");
                    studentsLoggedIn++;
                }
            }
            session.setStudents(roster);
            session.setStudentsLoggedIn(new Integer(studentsLoggedIn));
            session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledSessionDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    
    
    @Deprecated 
    public ScheduledSession getScheduledProctorsDetails(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledProctorsDetails");
        try {
            ScheduledSession session = new ScheduledSession();
            User [] proctors = users.getProctorUsersForAdmin(testAdminId);
            for(int i=0;i<proctors.length;i++) {
                boolean editable = users.isUserEditableByUserForAdmin(userName, proctors[i].getUserId(), testAdminId).intValue() > 0;
                proctors[i].setCopyable(editable ? "T" : "F");
                editable = editable && !proctors[i].getUserName().equals(userName);
                proctors[i].setEditable(editable ? "T" : "F");
            }
            session.setProctors(proctors);
            session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledProctorsDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    


    @Override
    public ScheduledSession getScheduledProctorsMinimalInfoDetails(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledProctorsDetails");
        try {
            ScheduledSession session = new ScheduledSession();
            //User [] proctors = users.getProctorUsersForAdmin(testAdminId);
            User [] proctors = users.getProctorUsersMinimalInfoForAdmin(testAdminId);
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
            User scheduler = users.getUserDetailsWithoutPassword(testSession.getCreatedBy());
            for(int i=0;i<proctors.length;i++) {
                boolean editable = users.isUserEditableByUserForAdmin(userName, proctors[i].getUserId(), testAdminId).intValue() > 0;
                proctors[i].setCopyable(editable ? "T" : "F");
                editable = editable && !proctors[i].getUserName().equals(userName);
                proctors[i].setEditable(editable ? "T" : "F");
                if(proctors[i].getUserId().intValue() == scheduler.getUserId().intValue()){
                	proctors[i].setEditable("F");
                	proctors[i].setDefaultScheduler("T");
                }
            }
            session.setProctors(proctors);
            //session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledProctorsDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    @Override
    public ScheduledSession getScheduledStudentsMinimalInfoDetails(String userName, Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledStudentsDetails");
        try {
            ScheduledSession session = new ScheduledSession();
            int studentsLoggedIn = 0;
            TestSession testSession = admins.getTestAdminDetails(testAdminId);
            Integer productId = testSession.getProductId();
            TestProduct testProduct = product.getProduct(productId);
            boolean overrideUsingStudentManifest = false;
            if ("F".equalsIgnoreCase(testProduct.getStaticManifest())
                && "F".equalsIgnoreCase(testProduct.getSessionManifest()))
                overrideUsingStudentManifest = true;
            
           SessionStudent [] roster = students.getSessionStudentsMinimalInfoForAdmin(testAdminId);
            boolean testRestricted = "T".equals(admins.isTestRestricted(testSession.getItemSetId()))?true:false;
            for(int i=0;i<roster.length;i++) {
                if (overrideUsingStudentManifest) {
                    StudentManifest [] studentManifests = studentItemSetStatus.getStudentManifestsForRoster(roster[i].getStudentId(),testAdminId);
                    ArrayList smAr = getFilteredStudentManifestForRoster(studentManifests);
                    roster[i].setStudentManifests((StudentManifest [])smAr.toArray(new StudentManifest[0]));
                }
                boolean copyable = students.isStudentEditableByUserForOrg(userName, roster[i].getStudentId(), roster[i].getOrgNodeId()).intValue() > 0;
                EditCopyStatus status = roster[i].getStatus();
                status.setEditable("T");
                status.setCopyable("T");
                if(!copyable) {
                    status.setCopyable("F");
                    status.setEditable("F");
                    status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                }
                Integer restrictedAdmin = new Integer(-1);
                if(testRestricted) {
                    restrictedAdmin = students.isTestRestrictedForStudent(userName, roster[i].getStudentId(), testSession.getItemSetId());
                }
                if(restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    status.setCopyable("F");
                    status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                    status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                }
                if(!"F".equals(roster[i].getTested())) {
                    status.setEditable("F");
                    studentsLoggedIn++;
                }
            }
            session.setStudents(roster);
            session.setStudentsLoggedIn(new Integer(studentsLoggedIn));
            session.setCopyable(admins.checkCopyable(userName, testAdminId));
            return session;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledSessionDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    
    public SessionStudentData getSessionStudentsMinimalInfoForOrgNode(String userName, Integer orgNodeId, Integer testAdminId, Integer testItemSetId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getSessionStudentsForOrgNode");
        String searchOrder = " ";
        String searchFilter = " ";
               
        
        searchOrder = getOrderString(sort);
        if(filter!=null){
        	searchFilter = getFilterString(filter);
        }
        searchOrder = " main "+ searchFilter +  searchOrder;
        System.out.println("Final String:"+searchOrder);
        try {
            SessionStudentData std = new SessionStudentData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            SessionStudent [] sstudents = null;
            if(testAdminId == null) testAdminId = new Integer(-1);
            //String cacheKey = String.valueOf(orgNodeId) + "|" + String.valueOf(testItemSetId) + "|" + String.valueOf(testAdminId);
            //SchedulingStudentCacheObject cacheObj = (SchedulingStudentCacheObject) SimpleCache.checkCache5min("SESSION_STUDENTS", cacheKey);
            //if(cacheObj == null) {
            //    cacheObj = new SchedulingStudentCacheObject();
            //    cacheObj.scstudents = students.getSchedulingStudentsForOrgNode(orgNodeId, userName, testItemSetId, testAdminId);
            //    SimpleCache.cacheResult("SESSION_STUDENTS", cacheKey, cacheObj);
            //}
            //SchedulingStudent [] scstudents = cacheObj.scstudents;
            SessionStudent [] scstudents = students.getSchedulingStudentsMinimalInfoForOrgNodeByOrder(orgNodeId, userName, testItemSetId, testAdminId, searchOrder);
            sstudents = new SessionStudent[scstudents.length];
            for(int i=0;i<scstudents.length;i++) {
                sstudents[i] = scstudents[i];
                // if this is a restricted test, student can only be added
                // if they haven't tested before
                Integer restrictedAdmin = scstudents[i].getPriorAdmin();
                EditCopyStatus status = scstudents[i].getStatus();
                status.setEditable("T");
                status.setCopyable("T");
                if (restrictedAdmin != null && !new Integer(-1).equals(restrictedAdmin)) {
                    status.setEditable("F");
                    status.setCopyable("F");
                    status.setCode(EditCopyStatus.StatusCode.PREVIOUSLY_SCHEDULED);
                    status.setPriorSession(admins.getTestAdminDetails(restrictedAdmin));
                }
                if(testAdminId != null && testAdminId.intValue() != -1) {
                    boolean editable = students.isStudentEditableByUserForAdminAndOrg(userName, sstudents[i].getStudentId(), testAdminId, orgNodeId).intValue() > 0 ? true : false;
                    if(!editable) {
                        status.setEditable("F");
                        status.setCopyable("F");
                        status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                    }
                    RosterElement existingRoster = rosters.getRosterElementForStudentAndAdmin(sstudents[i].getStudentId(), testAdminId);
                    if(existingRoster != null) {
                        boolean oldEditable = students.isStudentEditableByUserForAdminAndOrg(userName, sstudents[i].getStudentId(), testAdminId, existingRoster.getOrgNodeId()).intValue() > 0 ? true : false;
                        if(!oldEditable) {
                            status.setEditable("F");
                            status.setCopyable("F");
                            status.setCode(EditCopyStatus.StatusCode.OUTSIDE_VISIBLE_ORG);
                            if("CO".equals(existingRoster.getTestCompletionStatus())) {
                                status.setCode(EditCopyStatus.StatusCode.SESSION_COMPLETED);
                            } else if(!"SC".equals(existingRoster.getTestCompletionStatus()) &&
                                        !"NA".equals(existingRoster.getTestCompletionStatus()) ) {
                                status.setCode(EditCopyStatus.StatusCode.SESSION_IN_PROGRESS);
                            }                        
                        }
                    }
                }
            }
            std.setSessionStudents(sstudents, pageSize);
            if(page != null) std.applyPaging(page);
            return std;
        } catch (SQLException se) {
            StudentDataNotFoundException tee = new StudentDataNotFoundException("ScheduleTestImpl: getSessionStudentsForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    public UserData getUsersMinimalInfoForOrgNode(String userName, Integer orgNodeId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateNode(userName, orgNodeId, "testAdmin.getUsersForOrgNode");
        try {
            UserData usd = new UserData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            User [] proctors = users.getUsersMinimalInfoForOrgNode(orgNodeId);
            
            /*for(int i=0;i<proctors.length;i++) {
                boolean editable = users.isUserEditableByUserForAdmin(userName, proctors[i].getUserId(), testAdminId).intValue() > 0;
                proctors[i].setCopyable(editable ? "T" : "F");
                editable = editable && !proctors[i].getUserName().equals(userName);
                proctors[i].setEditable(editable ? "T" : "F");
            }*/
            usd.setUsers(proctors, pageSize);
            if(filter != null) usd.applyFiltering(filter);
            if(sort != null) usd.applySorting(sort);
            if(page != null) usd.applyPaging(page);
            return usd;
        } catch (SQLException se) {
            UserDataNotFoundException tee = new UserDataNotFoundException("ScheduleTestImpl: getUsersForOrgNode: " + se.getMessage());
            tee.setStackTrace(se.getStackTrace());
            throw tee;
        }
    }
    
    
    public TestElement getTestElementMinInfoById(Integer customerId, Integer itemsetIdTC) throws CTBBusinessException
    {
    	 try {

    		 TestElement test = itemSet.getTestElementMinInfoById(customerId, itemsetIdTC);
    		 test.setForms(itemSet.getFormsForTest(test.getItemSetId()));
    		 return test;
    	 } catch (SQLException se){
    		 UserDataNotFoundException tee = new UserDataNotFoundException("ScheduleTestImpl: getTestElementMinInfoById: " + se.getMessage());
             tee.setStackTrace(se.getStackTrace());
             throw tee;
    	 }
    	
    }

    public TestElement getTestElementMinInfoByIds(Integer customerId, Integer itemsetIdTC, Integer orgNodeId) throws CTBBusinessException
    {
    	 try {

    		 TestElement test = itemSet.getTestElementMinInfoByIds(customerId, itemsetIdTC, orgNodeId);
    		 if (test != null)
    			 test.setForms(itemSet.getFormsForTest(test.getItemSetId()));
    		 return test;
    	 } catch (SQLException se){
    		 UserDataNotFoundException tee = new UserDataNotFoundException("ScheduleTestImpl: getTestElementMinInfoById: " + se.getMessage());
             tee.setStackTrace(se.getStackTrace());
             throw tee;
    	 }
    	
    }

    public TestElement getTestElementMinInfoByIdsAndUserName(Integer customerId, Integer itemsetIdTC, String userName) throws CTBBusinessException
    {
    	 try {

    		 TestElement test = itemSet.getTestElementMinInfoByIdsAndUserName(customerId, itemsetIdTC, userName);
    		 test.setForms(itemSet.getFormsForTest(test.getItemSetId()));
    		 return test;
    	 } catch (SQLException se){
    		 UserDataNotFoundException tee = new UserDataNotFoundException("ScheduleTestImpl: getTestElementMinInfoById: " + se.getMessage());
             tee.setStackTrace(se.getStackTrace());
             throw tee;
    	 }
    	
    }
    
    public StudentNodeData getTopTestTicketNodesForPrintTT(String userName, Integer testAdminId, FilterParams filter, PageParams page, SortParams sort) throws CTBBusinessException
    {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getTopTestTicketNodes");
        try {
            StudentNodeData ond = new StudentNodeData();
            Integer pageSize = null;
            if(page != null) {
                pageSize = new Integer(page.getPageSize());
            }
            Node [] nodes = orgNode.getTopNodesForUserAndAdminForPrintTT(userName, testAdminId);
            StudentNode [] studentNodes = new StudentNode [nodes.length];
            for(int i=0;i<nodes.length;i++) {
                studentNodes[i] = new StudentNode(nodes[i]);                     
            }
            ond.setStudentNodes(studentNodes, pageSize);
            if(testAdminId != null) {
                for(int i=0;i<studentNodes.length && studentNodes[i] != null;i++) {
                    studentNodes[i].setRosterCount(orgNode.getRosterCountForAncestorNode(studentNodes[i].getOrgNodeId(), testAdminId));            
                }
                FilterParams implicitFilter = new FilterParams();
                FilterParam [] newFilters = new FilterParam [1];
                Integer [] args = new Integer[1];
                args[0] = new Integer(0);
                FilterParam countFilter = new FilterParam("RosterCount", args, FilterType.GREATERTHAN);
                newFilters[0] = countFilter;
                implicitFilter.setFilterParams(newFilters);
                ond.applyFiltering(implicitFilter);
            }
            ond.setStudentNodes(ond.getStudentNodes(), pageSize);
            if(filter != null) ond.applyFiltering(filter);
            if(sort != null) ond.applySorting(sort);
            if(page != null) ond.applyPaging(page);
            return ond;
        } catch (SQLException se) {
            OrgNodeDataNotFoundException one = new OrgNodeDataNotFoundException("ScheduleTestImpl: getTopTestTicketNodes: " + se.getMessage());
            one.setStackTrace(se.getStackTrace());
            throw one;
        }
    }
    
    
    @Override
    public ScheduledStudentDetailsWithManifest getScheduledStudentsManifestDetails(String userName, Integer studentId,  Integer testAdminId) throws CTBBusinessException {
        validator.validateAdmin(userName, testAdminId, "testAdmin.getScheduledStudentsManifestDetails");
        ScheduledStudentDetailsWithManifest details = new ScheduledStudentDetailsWithManifest(); 
        try {
        		StudentManifest [] studentManifests = studentItemSetStatus.getStudentManifestsForRoster(studentId,testAdminId);
                ArrayList<StudentManifest> smAr = getFilteredStudentManifestForRoster(studentManifests);
                studentManifests =   smAr.toArray(new StudentManifest[smAr.size()]);
                details.setStudentManifests(studentManifests);
                
                TestElement[] allSubtest = this.itemSet.getTestElementByTestAdmin(testAdminId);
                details.setAllSchedulableUnit(allSubtest);
                
                TestSession testSession = this.admins.getTestAdminDetails(testAdminId);
                details.setTestSession(testSession);
                TestProduct tp =  this.product.getProductForTestAdmin(testAdminId);
                testSession.setProductType(tp.getProductType());
                TestElement [] testAdminItemSet = itemSet.getTestElementsForSession(testAdminId);
                details.setTestAdminItemSet(testAdminItemSet);
                
                return details;
        } catch (SQLException se) {
            CTBBusinessException cbe = new UserDataNotFoundException("ScheduleTestImpl: getScheduledStudentsManifestDetails: " + se.getMessage());
            cbe.setStackTrace(se.getStackTrace());
            throw cbe;
        }  
    }
    
    public String[] getTestCatalogForUserForScoring(String userName) throws CTBBusinessException
    {
    	//Attempt to improve performance
        //validator.validate(userName, null, "testAdmin.getTestProductsForUser");
        try {
            
        	String[] catalogNames = product.getTestCatalogForUserForScoring(userName);
            
            return catalogNames;
        } catch (SQLException se) {
            ProductDataNotFoundException pde = new ProductDataNotFoundException("ScheduleTestImpl: getTestCatalogForUserForScoring: " + se.getMessage());
            pde.setStackTrace(se.getStackTrace());
            throw pde;
        }
    }
    
    public String[] getAllContentAreaOptionsForUser(String userName) throws CTBBusinessException
    {
        //validator.validate(userName, null, "testAdmin.getTestProductsForUser");
        try {
            
        	String[] catalogNames = product.getAllContentAreaOptionsForUser(userName);
            
            return catalogNames;
        } catch (SQLException se) {
            ProductDataNotFoundException pde = new ProductDataNotFoundException("ScheduleTestImpl: getAllContentAreaOptionsForUser: " + se.getMessage());
            pde.setStackTrace(se.getStackTrace());
            throw pde;
        }
    }
    
    public String[] getAllFormOptionsForUser(String userName) throws CTBBusinessException
    {
        try {
            
        	String[] formNames = product.getAllFormOptionsForUser(userName);
            
            return formNames;
        } catch (SQLException se) {
            ProductDataNotFoundException pde = new ProductDataNotFoundException("ScheduleTestImpl: getAllFormOptionsForUser: " + se.getMessage());
            pde.setStackTrace(se.getStackTrace());
            throw pde;
        }
    }
    
} 
