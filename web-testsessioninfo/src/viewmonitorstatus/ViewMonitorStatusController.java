package viewmonitorstatus;
import org.apache.beehive.netui.pageflow.Forward;
import org.apache.beehive.netui.pageflow.PageFlowController;
import com.ctb.bean.request.FilterParams;
import com.ctb.bean.request.PageParams;
import com.ctb.bean.request.SortParams;
import com.ctb.bean.testAdmin.Customer;
import com.ctb.bean.testAdmin.CustomerConfiguration;
import com.ctb.bean.testAdmin.RosterElement;
import com.ctb.bean.testAdmin.RosterElementData;
import com.ctb.bean.testAdmin.StudentManifest;
import com.ctb.bean.testAdmin.StudentManifestData;
import com.ctb.bean.testAdmin.StudentSessionStatus;
import com.ctb.bean.testAdmin.StudentSessionStatusData;
import com.ctb.bean.testAdmin.TestElement;
import com.ctb.bean.testAdmin.TestElementData;
import com.ctb.bean.testAdmin.TestProduct;
import com.ctb.bean.testAdmin.TestSession;
import com.ctb.bean.testAdmin.TestSessionData;
import com.ctb.bean.testAdmin.User;
import com.ctb.exception.CTBBusinessException;
import com.ctb.forms.FormFieldValidator;
import com.ctb.testSessionInfo.dto.SubtestDetail;
import com.ctb.testSessionInfo.dto.TestRosterFilter;
import com.ctb.testSessionInfo.dto.TestRosterVO;
import com.ctb.widgets.bean.PagerSummary;
import com.ctb.testSessionInfo.dto.TestSessionVO;
import com.ctb.testSessionInfo.utils.DateUtils;
import java.io.IOException;
import java.util.List;
import com.ctb.testSessionInfo.utils.FilterSortPageUtils;
import com.ctb.util.web.sanitizer.SanitizedFormData;
import com.ctb.widgets.bean.ColumnSortEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.beehive.controls.api.bean.Control;
import org.apache.beehive.netui.pageflow.annotations.Jpf;

/**
 * @jpf:controller
 *  */
@Jpf.Controller()
public class ViewMonitorStatusController extends PageFlowController
{
    static final long serialVersionUID = 1L;

    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.testAdmin.TestSessionStatus testSessionStatus;
    
    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.testAdmin.ScheduleTest scheduleTest;

    private String userName = null;
    private String callerId = null;
    private TestRosterFilter testRosterFilter = null;
    private Integer sessionId = null;
    private boolean sessionDetailsShowScores = false;
    
    public String[] nameOptions = {FilterSortPageUtils.FILTERTYPE_CONTAINS, FilterSortPageUtils.FILTERTYPE_BEGINSWITH, FilterSortPageUtils.FILTERTYPE_EQUALS};
    public String[] validationStatusOptions = {FilterSortPageUtils.FILTERTYPE_SHOWALL, FilterSortPageUtils.FILTERTYPE_INVALID, FilterSortPageUtils.FILTERTYPE_VALID};
    public String[] testStatusOptions = {FilterSortPageUtils.FILTERTYPE_SHOWALL, FilterSortPageUtils.FILTERTYPE_COMPLETED, FilterSortPageUtils.FILTERTYPE_INCOMPLETE, 
                                        FilterSortPageUtils.FILTERTYPE_INPROGRESS, FilterSortPageUtils.FILTERTYPE_NOTTAKEN, FilterSortPageUtils.FILTERTYPE_SCHEDULED, 
                                        FilterSortPageUtils.FILTERTYPE_STUDENTSTOP, FilterSortPageUtils.FILTERTYPE_SYSTEMSTOP, FilterSortPageUtils.FILTERTYPE_TESTLOCKED,
                                        FilterSortPageUtils.FILTERTYPE_TESTABANDONED, FilterSortPageUtils.FILTERTYPE_STUDENTPAUSE};
     
    ViewMonitorStatusForm savedForm = null;
    public String defaultcustomerFlagStatus = "";
    public String setCustomerFlagToogleButton="false";

    private boolean subtestValidationAllowed = false;
    private boolean showStudentReportButton = false;
    private List studentStatusSubtests = null;
    private List TABETestElements = null;
                 
    // Uncomment this declaration to access Global.app.
    // 
    //     protected global.Global globalApp;
    // 

    // For an example of page flow exception handling see the example "catch" and "exception-handler"
    // annotations in {project}/WEB-INF/src/global/Global.app

    /**
     * This method represents the point of entry into the pageflow
     * @jpf:action
     * @jpf:forward name="success" path="view_monitor_status.do"
     */ 
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "view_monitor_status.do")
    })
    protected Forward begin()
    {
        retrieveInfoFromSession();                        
        this.testRosterFilter = new TestRosterFilter();            
        ViewMonitorStatusForm form = new ViewMonitorStatusForm();
        form.init();
        
        this.sessionDetailsShowScores = isSessionDetailsShowScores();

        this.subtestValidationAllowed = isSubtestValidationAllowed();
        this.studentStatusSubtests = new ArrayList();
                        
        this.showStudentReportButton = showStudentReportButton();
                        
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="viewDetails" path="to_view_subtests_detail.do"
     * @jpf:forward name="validationDetails" path="to_validate_subtests_detail.do"
     * @jpf:forward name="viewIndividualReport" path="viewIndividualReport.do"
     * @jpf:forward name="success" path="view_monitor_status.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "viewDetails",
                     path = "to_view_subtests_detail.do"), 
        @Jpf.Forward(name = "validationDetails",
                     path = "to_validate_subtests_detail.do"), 
        @Jpf.Forward(name = "viewIndividualReport",
                     path = "viewIndividualReport.do"), 
        @Jpf.Forward(name = "success",
                     path = "view_monitor_status.jsp")
    })
    protected Forward view_monitor_status(ViewMonitorStatusForm form)
    {
        String testAdminId = getRequest().getParameter("testAdminId");
        if (testAdminId != null)
            this.sessionId = Integer.valueOf(testAdminId);
        form.resetValuesForAction();        
        form.validateValues();
        FormFieldValidator.validateFilterForm(form, getRequest());
                
        String forwardName = handleAction(form);
        if (forwardName != null)
        {                
            this.savedForm = form.createClone();
            return new Forward(forwardName, form);
        }

        RosterElementData red = getRosterForTestSession(this.sessionId, form);
        List rosterList = buildRosterList(red);        
        this.getRequest().setAttribute("rosterList", rosterList);
        
        PagerSummary pagerSummary = buildTestRosterPagerSummary(red, form.getPageRequested());
        this.getRequest().setAttribute("pagerSummary", pagerSummary);

        prepareStudentSelection(rosterList, form.getTestRosterId());
        form.setMaxPage(red.getFilteredPages());

        getRequest().setAttribute("totalStudents", red.getTotalCount().toString());

        TestElementData ted = getTestElementsForTestSession(this.sessionId); 
        
        List subtestList = buildSubtestList(ted);
        getRequest().setAttribute("subtestList", subtestList);
                
        Integer breakCount = ted.getBreakCount();
        if ((breakCount != null) && (breakCount.intValue() > 0))
        {
            if (isSameAccessCode(subtestList)) 
                getRequest().setAttribute("hasBreak", "singleAccesscode");
            else
                getRequest().setAttribute("hasBreak", "multiAccesscodes");
        }
        else
        {
            getRequest().setAttribute("hasBreak", "false");
        }

        TestSessionVO testSession = getTestSessionDetails(this.sessionId);
        getRequest().setAttribute("testSession", testSession);

        form.setActionElement("none");   
        return new Forward("success");
    }

    private boolean retrieveInfoFromSession()
    {
        boolean success = true;
        this.userName = (String)getSession().getAttribute("userName");
        if (this.userName == null)
        {
            success = false;
        }
        
        this.callerId = (String)getSession().getAttribute("callerId");
        if (this.callerId == null) 
            this.callerId = (String)getRequest().getParameter("callerId");
        if (this.callerId == null) 
            this.callerId = "goto_homepage";
            
        if (getSession().getAttribute("sessionId") != null)
            this.sessionId = new Integer((String)getSession().getAttribute("sessionId")); 
        else if (getRequest().getParameter("sessionId") != null)
            this.sessionId = new Integer((String)getRequest().getParameter("sessionId")); 
        else
            success = false;

        String sessionFilterTab = "CU";            
        if (getSession().getAttribute("sessionFilterTab") != null)
            sessionFilterTab = (String)getSession().getAttribute("sessionFilterTab"); 
        setTestStatusOptions(sessionFilterTab); 
                
        return success;
    }
    
    private String handleAction(ViewMonitorStatusForm form)
    {
        String forwardName = null;
        String actionElement = form.getActionElement();
        String currentAction = form.getCurrentAction();
        
        if (actionElement.equals("{actionForm.currentAction}"))
        {
            if (currentAction.equals("applyFilters"))
            {
                applyFilters(form);
            }
            if (currentAction.equals("clearFilters"))
            {
                clearFilters(form);
                applyFilters(form);
            }
            if (currentAction.equals("toggleValidationStatus"))
            {
                toggleValidationStatus(form);
            }
            if (currentAction.equals("toggleCustomerFlagStatus"))
            {
                toggleCustomerFlagStatus(form);
            }         
            if (currentAction.equals("refreshList"))
            {
                refreshList();
            }
            if (currentAction.equals("viewDetails"))
            {
                forwardName = currentAction;
            }
            if (currentAction.equals("validationDetails"))
            {
                forwardName = currentAction;
            }
            if (currentAction.equals("viewIndividualReport"))
            {
                forwardName = currentAction;
            }
        }
        return forwardName;
    }
    
    private void applyFilters(ViewMonitorStatusForm form) 
    {
        TestRosterFilter trf = form.getTestRosterFilter();
        this.testRosterFilter.copyValues(trf);
        form.resetSortPage();
    }

    private void clearFilters(ViewMonitorStatusForm form) 
    {
        TestRosterFilter trf = new TestRosterFilter();
        form.setTestRosterFilter(trf);
        
        getRequest().setAttribute("formIsClean", null);        
    }

    private void refreshList() 
    {
    }

    /**
     */ 
    protected boolean prepareSubtestsDetailInformation(ViewMonitorStatusForm form, boolean validation)
    {
        RosterElement re = getTestRosterDetails(form.getTestRosterId());
        
        TestSessionVO testSession = getTestSessionDetails(this.sessionId);
        
        TestProduct testProduct = getProductForTestAdmin(this.sessionId);
        
        boolean isTabeSession = isTabeSession(testProduct.getProductType());
        
        boolean isTabeLocatorSession = isTabeLocatorSession(testProduct.getProductType());

        boolean testSessionCompleted = isTestSessionCompleted(testSession);
                                
        this.studentStatusSubtests = buildStudentStatusSubtests(re.getStudentId(), this.sessionId, testSessionCompleted, isTabeSession, isTabeLocatorSession);
                
        String testGrade = getTestGrade(this.studentStatusSubtests);
        String testLevel = getTestLevel(this.studentStatusSubtests);
        
        getRequest().setAttribute("studentName", re.getFirstName() + " " + re.getLastName());
        getRequest().setAttribute("loginName", re.getUserName());
        getRequest().setAttribute("password", re.getPassword());
        getRequest().setAttribute("testSessionName", testSession.getTestAdminName());
        getRequest().setAttribute("testName", testSession.getTestName());
        
        if (! isTabeSession)
        {
            if (! testGrade.equals("--"))
                getRequest().setAttribute("testGrade", testGrade);
            if (! testLevel.equals("--"))
                getRequest().setAttribute("testLevel", testLevel);
        }
        
        getRequest().setAttribute("testCompletionStatus", FilterSortPageUtils.testStatus_CodeToString(re.getTestCompletionStatus()));
        getRequest().setAttribute("studentStatusSubtests", this.studentStatusSubtests);

        getRequest().setAttribute("isTabeSession", new Boolean(isTabeSession));
        
        boolean showStudentFeedback = false;
        if ((testSession.getShowStudentFeedback() != null) && (testSession.getShowStudentFeedback().equalsIgnoreCase("T")))
        {
            showStudentFeedback = true;
        }
        boolean isShowScores = this.sessionDetailsShowScores;
        
        getRequest().setAttribute("isShowScores", new Boolean(isShowScores));
        
        int numberColumn = 4;
        if (isTabeSession)
            numberColumn += 1;
        if (isShowScores)
            numberColumn += 3;
        if (validation)
        {
            numberColumn += 1;
            if (this.setCustomerFlagToogleButton.equals("true"))
                numberColumn += 1;
            prepareValidateButtons(form.getSelectedItemSetIds());
        }
                
        getRequest().setAttribute("numberColumn", String.valueOf(numberColumn));
        
        return true;
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="view_subtests_detail.jsp"
     */ 
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "view_subtests_detail.jsp")
    })
    protected Forward to_view_subtests_detail(ViewMonitorStatusForm form)
    {
        prepareSubtestsDetailInformation(form, false);
        
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="done" path="from_view_subtests_detail.do"
     * @jpf:forward name="success" path="validate_subtests_detail.jsp"
     */ 
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "done",
                     path = "from_view_subtests_detail.do"), 
        @Jpf.Forward(name = "success",
                     path = "validate_subtests_detail.jsp")
    })
    protected Forward to_validate_subtests_detail(ViewMonitorStatusForm form)
    {
        String forwardName = handleValidateAction(form);
        
        prepareSubtestsDetailInformation(form, true);
        
        return new Forward(forwardName, form);
    }

    private Integer[] getSelectedSubtestIds(ViewMonitorStatusForm form)
    {
        String[] selectedItemSetIds = form.getSelectedItemSetIds();
        List resultList = new ArrayList();
        for (int i=0; i < this.studentStatusSubtests.size(); i++)
        {
            SubtestDetail sd = (SubtestDetail)this.studentStatusSubtests.get(i);
            boolean found = false;
            for (int j=0; j < selectedItemSetIds.length; j++)
            {  
                String value = (String)selectedItemSetIds[j];
                if (value != null)
                {
                    Integer selectedItemSetId = new Integer(value);          
                    if (sd.getItemSetId().intValue() == selectedItemSetId.intValue())
                    {
                        found = true;
                        break;
                    }
                }    
            }
            if (found)
            {

                resultList.add(sd.getItemSetId());
                
                if (this.TABETestElements != null)
                {
                    String stn = sd.getSubtestName();
                    stn = stn.replaceAll("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", "");
                    for (int k=0; k < this.TABETestElements.size(); k++)
                    {
                        TestElement te = (TestElement)this.TABETestElements.get(k);
                        if (te.getItemSetName().equals(stn) && (te.getItemSetId().intValue() != sd.getItemSetId().intValue()))
                        {
                            resultList.add(te.getItemSetId());                            
                        }
                    }
                }
                
            }
        }
        
        
        return (Integer[])resultList.toArray(new Integer[0]);
    }
    
    private String handleValidateAction(ViewMonitorStatusForm form)
    {
        String currentAction = form.getCurrentAction();
        
        if (currentAction.equals("toggleSubtestValidation"))
        {
            toggleSubtestValidationStatus(form);
        }
        else if (currentAction.equals("toggleSubtestCustom"))
        {
            toggleSubtestCustomerFlagStatus(form);
        }
        else if (currentAction.equals("done"))
        {
            return currentAction;
        }
        
        return "success";
    }

    private void prepareValidateButtons(String[] itemSetIds)
    {            
        if ((itemSetIds != null) && (itemSetIds.length > 0) && (itemSetIds[0] != null))
            this.getRequest().setAttribute("disableToogleButton", "false");
        else 
            this.getRequest().setAttribute("disableToogleButton", "true");
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="view_monitor_status.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "view_monitor_status.do")
    })
    protected Forward from_view_subtests_detail(ViewMonitorStatusForm form)
    {
        this.savedForm.setActionElement("none");   
        return new Forward("success", this.savedForm);
    }

    private boolean isSubtestValidationAllowed()
    {
        boolean isValidationAllowed = false; 
        try
        {    
            isValidationAllowed = this.testSessionStatus.allowSubtestInvalidation(this.userName).booleanValue();
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }   

        if (isValidationAllowed)
        {
            List options = new ArrayList();
            options.add(FilterSortPageUtils.FILTERTYPE_SHOWALL);
            options.add(FilterSortPageUtils.FILTERTYPE_INVALID);
            options.add(FilterSortPageUtils.FILTERTYPE_PARTIALLY_INVALID);
            options.add(FilterSortPageUtils.FILTERTYPE_VALID);
            this.validationStatusOptions = (String[])options.toArray(new String[0]);
        }

        return isValidationAllowed;
    }
    
    private boolean isSessionDetailsShowScores() 
    {               
        boolean showScores = false; 

        try
        {      
            User user = this.testSessionStatus.getUserDetails(this.userName, this.userName);
            Customer customer = user.getCustomer();
            Integer customerId = customer.getCustomerId();
            
            CustomerConfiguration [] ccArray = this.testSessionStatus.getCustomerConfigurations(this.userName, customerId);       
            for (int i=0; i < ccArray.length; i++)
            {
                CustomerConfiguration cc = (CustomerConfiguration)ccArray[i];
                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Session_Details_Show_Scores") && cc.getDefaultValue().equalsIgnoreCase("T"))
                {
                    showScores = true; 
                }
                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Roster_Status_Flag"))
                {
                    this.setCustomerFlagToogleButton = "true";
                     
                }
            }
            this.getRequest().setAttribute("setCustomerFlagToogleButton", setCustomerFlagToogleButton);  
                
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        

        return showScores;
    }
    
    private boolean showStudentReportButton() 
    {               
        boolean showButton = false; 

        try
        {      
            User user = this.testSessionStatus.getUserDetails(this.userName, this.userName);
            Customer customer = user.getCustomer();
            Integer customerId = customer.getCustomerId();
            
            CustomerConfiguration [] ccArray = this.testSessionStatus.getCustomerConfigurations(this.userName, customerId);       
            for (int i=0; i < ccArray.length; i++)
            {
                CustomerConfiguration cc = (CustomerConfiguration)ccArray[i];
                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Session_Status_Student_Reports") && cc.getDefaultValue().equalsIgnoreCase("T"))
                {
                    showButton = true; 
                }
            }     
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        

        return showButton;
    }

    private TestElementData getTestElementsForTestSession(Integer sessionId) 
    {
        TestElementData ted = null;
        try
        {      
            ted = this.testSessionStatus.getTestElementsForTestSession(this.userName, sessionId, null, null, null);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return ted;
    }

    private TestElement[] getTestElementsForParent(Integer parentItemSetId, String itemSetType) 
    {
        TestElement[] tes = null;
        try
        {      
            TestElementData ted = this.testSessionStatus.getTestElementsForParent(this.userName, parentItemSetId, itemSetType, null, null, null);
            tes = ted.getTestElements();            
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return tes;
    }

    private StudentSessionStatus[] getStudentItemSetStatusesForRoster(Integer studentId, Integer testAdminId) 
    {
        StudentSessionStatus[] ssss = null;
        try
        {
            SortParams sort = FilterSortPageUtils.buildSortParams("ItemSetOrder", ColumnSortEntry.ASCENDING);
            StudentSessionStatusData sssData = testSessionStatus.getStudentItemSetStatusesForRoster(this.userName, studentId, testAdminId, null, null, sort);
            ssss = sssData.getStudentSessionStatuses();            
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return ssss;
    }

    private boolean isMultipleSubtest(TestElementData ted)
    {
        TestElement[] subtestelements = ted.getTestElements(); 
        if (subtestelements.length > 1)
        {
            return true;
        }         
        for (int i=0; i < subtestelements.length; i++)
        {
            TestElement te = subtestelements[i];
            if (te != null)
            {
                TestElement[] tes = getTestElementsForParent(te.getItemSetId(), "TD"); 
                if (tes.length > 1)
                {
                    return true;
                }                         
            }
        }                                
        return false;
    }

    private List buildSubtestList(TestElementData ted)
    {
        List subtestList = new ArrayList();        
        TestElement[] subtestelements = ted.getTestElements();  
        int sequence = 1;
        for (int i=0; i < subtestelements.length; i++)
        {
            TestElement te = subtestelements[i];
            if (te != null && "T".equals(te.getSessionDefault()))
            {
                SubtestDetail sd = new SubtestDetail(te, sequence);
                subtestList.add(sd);
                sequence++;
            }
        }        
                
        return subtestList;
    }

    private List buildStudentStatusSubtests(Integer studentId, Integer testAdminId, boolean testSessionCompleted, boolean isTabeSession, boolean isTabeLocatorSession)
    {
        this.TABETestElements = null;        
        String userTimeZone = (String)getSession().getAttribute("userTimeZone"); 
        List subtestList = new ArrayList();        
        TestElementData ted = getTestElementsForTestSession(testAdminId); 
        StudentSessionStatus[] ssss = getStudentItemSetStatusesForRoster(studentId, testAdminId);                 
        TestElement[] subtestelements = ted.getTestElements(); 
        HashMap recLevelHM = new HashMap();
        if (isTabeSession)
        {
            subtestelements = orderedSubtestList(subtestelements, studentId, testAdminId);
        }                
        boolean isLocatorTD = false;
        for (int i=0; i < subtestelements.length; i++)
        {
            TestElement te = subtestelements[i];          
              
            if (te != null)
            {
                SubtestDetail sd_TS = new SubtestDetail(te, i + 1);
          
                TestElement[] tes = getTestElementsForParent(sd_TS.getItemSetId(), "TD"); 
                boolean addTS = true;               
                HashMap subTestHM = new HashMap();
                for (int j=0; j < ssss.length; j++)
                {
                    StudentSessionStatus sss = ssss[j];
                    
                    for (int k=0; k < tes.length; k++)
                    {
                        TestElement te_TD = tes[k];

                        if (isTabeSession)
                        {
                            addTABESubtest(te_TD);
                        }
                        
                        if (sss.getItemSetId().intValue() == te_TD.getItemSetId().intValue())
                        {
                            
                            SubtestDetail sd_TD = new SubtestDetail(te_TD, -1);
                            
                            sd_TD.setValidationStatus(FilterSortPageUtils.validationStatus_CodeToString(sss.getValidationStatus()));
                            sd_TD.setCustomStatus(FilterSortPageUtils.customStatus_ToString(sss.getCustomerFlagStatus()));
                            
                            if (addTS)
                            {
                                if (sd_TD.getSubtestName().indexOf("Locator") < 0)
                                {                                    
                                    subtestList.add(sd_TS); 
                                    addTS = false;                                                         
                                }
                                else
                                {  
                                    isLocatorTD = true;                                      
                                }                                
                            }
                            
                            if (isTabeSession)
                            {
                                String level = te_TD.getItemSetForm();
                                if ((level == null) || level.equals("1"))
                                    level = "";
                                sd_TD.setLevel(level);
                            }
                            
                            
                            String status = FilterSortPageUtils.testStatus_CodeToString(sss.getCompletionStatus());
                            if (testSessionCompleted)
                            {
                                if (status.equals(FilterSortPageUtils.FILTERTYPE_SCHEDULED))
                                {
                                    status = FilterSortPageUtils.FILTERTYPE_NOTTAKEN; 
                                }
                                if (status.equals(FilterSortPageUtils.FILTERTYPE_SYSTEMSTOP) || status.equals(FilterSortPageUtils.FILTERTYPE_STUDENTSTOP) || status.equals(FilterSortPageUtils.FILTERTYPE_INPROGRESS))
                                {
                                    status = FilterSortPageUtils.FILTERTYPE_INCOMPLETE; 
                                }
                            }
                            sd_TD.setCompletionStatus(status);
                                                        
                            if (sss.getStartDateTime() != null)
                            {                                
                                Date adjStartDate = com.ctb.util.DateUtils.getAdjustedDate(sss.getStartDateTime(), "GMT", userTimeZone, sss.getStartDateTime());
                                String startDate = DateUtils.formatDateToDateString(adjStartDate);
                                String startTime = DateUtils.formatDateToTimeString(adjStartDate);                                
                                sd_TD.setStartDate(startDate + " " + startTime);
                            }
                            
                            if (sss.getCompletionDateTime() != null)
                            {
                                Date adjEndDate = com.ctb.util.DateUtils.getAdjustedDate(sss.getCompletionDateTime(), "GMT", userTimeZone, sss.getCompletionDateTime());
                                String endDate = DateUtils.formatDateToDateString(adjEndDate);
                                String endTime = DateUtils.formatDateToTimeString(adjEndDate);                                
                                sd_TD.setEndDate(endDate + " " + endTime);
                            }
                            
                            sd_TD.setMaxScore(sss.getMaxScore());
                            sd_TD.setRawScore(sss.getRawScore());
                            sd_TD.setUnScored(sss.getUnscored());
                            String tdSubtestName = sd_TD.getSubtestName();
                            String sn = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                        tdSubtestName;
                            sd_TD.setSubtestName(sn);   
                            if (!isLocatorTD)
                            {                                
                                subtestList.add(sd_TD);
                            }
                            else
                            {
                                if (!subTestHM.containsValue(tdSubtestName))
                                {
                                    if (addTS)
                                    {                                    
                                        subtestList.add(sd_TS); 
                                        addTS = false;
                                    }
                                    if (status.equals(FilterSortPageUtils.FILTERTYPE_COMPLETED))
                                    {
                                        if (sss.getRecommendedLevel() != null)
                                        {   
                                            if (tdSubtestName.indexOf("Reading") > 0)                                         
                                                recLevelHM.put("Reading", sss.getRecommendedLevel());
                                            else if (tdSubtestName.indexOf("Mathematics Computation") > 0)
                                                recLevelHM.put("Mathematics Computation", sss.getRecommendedLevel());
                                            else if (tdSubtestName.indexOf("Applied Mathematics") > 0)
                                                recLevelHM.put("Applied Mathematics", sss.getRecommendedLevel());
                                            else if (tdSubtestName.indexOf("Language") > 0)
                                                recLevelHM.put("Language", sss.getRecommendedLevel());
                                        }
                                    }
                                    else
                                        sd_TD.setLevel("");
                                    
                                    String subtestName = tdSubtestName.substring(5, tdSubtestName.length()).trim();
                                    if (subtestName.indexOf("Sample") > 0)
                                    {
                                        int indexOfSample = subtestName.indexOf("Sample");
                                        subtestName = subtestName.substring(0, indexOfSample).trim();
                                    }
                                    if (recLevelHM.size() > 0)
                                    {                                      
                                        if (recLevelHM.containsKey(subtestName))
                                        {
                                            if (subtestName.indexOf("Mathematics Computation") >= 0 || subtestName.indexOf("Applied Mathematics") >= 0)
                                            {                                        
                                                if (recLevelHM.containsKey("Mathematics Computation") && recLevelHM.containsKey("Applied Mathematics"))
                                                {
                                                    sd_TD.setLevel(recLevelHM.get(subtestName).toString());
                                                }                                               
                                            }
                                            else
                                            {
                                                sd_TD.setLevel(recLevelHM.get(subtestName).toString());
                                            }
                                        }
                                        if (subtestName.indexOf("Vocabulary") >= 0 && recLevelHM.containsKey("Reading"))
                                            sd_TD.setLevel(recLevelHM.get("Reading").toString());
                                        if (recLevelHM.containsKey("Language"))
                                        {
                                            if (subtestName.indexOf("Language Mechanics") >= 0)
                                                sd_TD.setLevel(recLevelHM.get("Language").toString());
                                            else if (subtestName.indexOf("Spelling") >= 0)
                                                sd_TD.setLevel(recLevelHM.get("Language").toString());
                                        }                                   
                                    }
                                        
                                    subtestList.add(sd_TD);  
                                    subTestHM.put(sd_TD, tdSubtestName);
                                }                              
                            }
                            break;
                            
                        }
                    }
                }
            }
        }                                        
        return subtestList;
    }

    private String getTestGrade(List subtestList)
    {
        String grade = null;
        for (int i=0; i < subtestList.size(); i++)
        {
            SubtestDetail sd = (SubtestDetail)subtestList.get(i);
            if ((sd.getGrade() != null) && (sd.getGrade() != ""))
            {
                return sd.getGrade();    
            }
        }
        return grade;
    }


    private String getTestLevel(List subtestList)
    {
        String level = null;
        for (int i=0; i < subtestList.size(); i++)
        {
            SubtestDetail sd = (SubtestDetail)subtestList.get(i);
            if ((sd.getLevel() != null) && (sd.getLevel() != ""))
            {
                return sd.getLevel();    
            }
        }
        return level;
    }

    private List getSubtestIds(List subtestList)
    {
        List resultList = new ArrayList();
        for (int i=0; i < subtestList.size(); i++)
        {
            SubtestDetail sd = (SubtestDetail)subtestList.get(i);
            resultList.add(sd.getItemSetId());
        }
        return resultList;
    }

    private boolean isSameAccessCode(List subtestList)
    {
        if (subtestList.size() <= 1)
            return true;
            
        boolean sameAccessCode = true;
        SubtestDetail sd = (SubtestDetail)subtestList.get(0);
        String accessCode = sd.getAccessCode();
        for (int i=1; i < subtestList.size(); i++)
        {
            sd = (SubtestDetail)subtestList.get(i);
            if (! sd.getAccessCode().equals(accessCode))
                sameAccessCode = false;    
        }
        return sameAccessCode;
    }
    
    private TestSessionVO getTestSessionDetails(Integer sessionId) 
    {
        TestSessionVO testSession = null;
        try
        {      
            TestSessionData tsd = this.testSessionStatus.getTestSessionDetails(this.userName, sessionId);
            TestSession[] testsessions = tsd.getTestSessions();            
            TestSession ts = testsessions[0];
            testSession = new TestSessionVO(ts);            
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return testSession;
    }

    private RosterElement getTestRosterDetails(Integer testRosterId) 
    {
        RosterElement re = null;      
        try
        {
            re = this.testSessionStatus.getRoster(testRosterId);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }    
        return re;
    }    

    private RosterElementData getRosterForTestSession(Integer sessionId, ViewMonitorStatusForm form) 
    {
        if (this.testRosterFilter == null)
            this.testRosterFilter = new TestRosterFilter();            

        FilterParams filter = FilterSortPageUtils.buildTestRosterFilterParams(this.testRosterFilter);
        PageParams page = FilterSortPageUtils.buildPageParams(form.getPageRequested(), FilterSortPageUtils.PAGESIZE_20);
        SortParams sort = FilterSortPageUtils.buildSortParams(form.getSortColumn(), form.getSortOrderBy());
        
        RosterElementData red = null;
        try
        {      
            red = this.testSessionStatus.getRosterForTestSession(this.userName, sessionId, filter, page, sort);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        
        return red;
    }

    private List buildRosterList(RosterElementData red)
    {
        List rosterList = new ArrayList();    
        if (red != null){
	        RosterElement[] rosterElements = red.getRosterElements();
	        for (int i=0; i < rosterElements.length; i++)
	        {
	            RosterElement rosterElt = rosterElements[i];
	            if (rosterElt != null)
	            {
	                TestRosterVO vo = new TestRosterVO(rosterElt);                    
	                rosterList.add(vo);
	            }
	        }   
        }
        return rosterList;
    }
    
    private PagerSummary buildTestRosterPagerSummary(RosterElementData red, Integer pageRequested)
    {
        PagerSummary pagerSummary = new PagerSummary();
        pagerSummary.setCurrentPage(pageRequested);
        pagerSummary.setTotalObjects(red.getTotalCount());
        pagerSummary.setTotalPages(red.getFilteredPages());
        pagerSummary.setTotalFilteredObjects(red.getFilteredCount());        
        
        return pagerSummary;
    }

    private void prepareStudentSelection(List rosterList, Integer testRosterId)
    {            
        boolean found = false;
        if ((testRosterId != null) && (rosterList != null) && (rosterList.size() > 0))
        {        
            for (int i=0; i < rosterList.size(); i++)
            {
                TestRosterVO tr = (TestRosterVO)rosterList.get(i);
                if ((tr != null) && tr.getTestRosterId().equals(testRosterId))
                {
                    found = true;
                }
            }
        }
        if (found) 
            this.getRequest().setAttribute("disableToogleButton", "false");
        else 
            this.getRequest().setAttribute("disableToogleButton", "true");
        
        if ((rosterList == null) || (rosterList.size() == 0)) 
            this.getRequest().setAttribute("disableRefreshButton", "true");
        else 
            this.getRequest().setAttribute("disableRefreshButton", "false");
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="/homepage/HomePageController.jpf"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "goto_homepage.do")
    })
    protected Forward redirect_to_homepage(ViewMonitorStatusForm form)
    {
        return new Forward("success");
    }
    

    /**
     * @jpf:action
     * @jpf:forward name="success" path="/homepage/HomePageController.jpf"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "/homepage/HomePageController.jpf")
    })
    protected Forward goto_homepage()
    {
        return new Forward("success");
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="/viewtestsessions/ViewTestSessionsController.jpf"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "/viewtestsessions/ViewTestSessionsController.jpf")
    })
    protected Forward goto_viewtestsessions()
    {
        return new Forward("success");
    }

    /**
     * @jpf:action
     * @jpf:forward name="goto_homepage" path="goto_homepage.do"
     * @jpf:forward name="goto_viewtestsessions" path="goto_viewtestsessions.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "goto_homepage",
                     path = "goto_homepage.do"), 
			@Jpf.Forward(name = "goto_viewtestsessions", path = "goto_viewtestsessions.do")
		}
	)
    protected Forward returnToCaller()
    {
        if (this.callerId == null) 
            this.callerId = "goto_homepage";
        
        if (this.callerId.equals("goto_homepage"))
            return new Forward("goto_homepage");
        else
            return new Forward("goto_viewtestsessions");
    }

    /**
     * @jpf:action
     */
	@Jpf.Action()
    protected Forward goto_session_information()
    {
        try {
            if (this.sessionId == null) {
                if (getSession().getAttribute("sessionId") != null)
                    this.sessionId = new Integer((String)getSession().getAttribute("sessionId")); 
            }
            if (this.sessionId != null) {            
                String contextPath = "/TestAdministrationWeb/scheduleTestPageflow/selectSettings.do";
                String action = "action=view";
                String testAdminId = "testAdminId=" + this.sessionId;
                String url = contextPath + "?" + action + "&" + testAdminId;            
                getResponse().sendRedirect(url);
            }
        } 
        catch( IOException ioe ) {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
    
    /**
     * @jpf:action
     * @jpf:forward name="report" path="/homepage/turnleaf_reports.jsp"
     * @jpf:forward name="error" path="/error.jsp"
     */
	@Jpf.Action(
		forwards = { 
			@Jpf.Forward(name = "report", path = "/homepage/turnleaf_reports.jsp"), 
			@Jpf.Forward(name = "error", path = "/error.jsp")
		}
	)
    protected Forward viewIndividualReport(ViewMonitorStatusForm form)
    {
        try {
        	// Defect 60476 
        	if (this.userName == null) {
        		
        		 java.security.Principal principal = getRequest().getUserPrincipal();
        	        if (principal != null) 
        	            this.userName = principal.toString();  
        	}
        	
            String reportUrl = this.testSessionStatus.getIndividualReportUrl(this.userName, form.getTestRosterId());           
            this.getRequest().setAttribute("reportUrl", reportUrl);
            this.getRequest().setAttribute("testAdminId", String.valueOf(this.sessionId));
        } catch (Exception e) {
            e.printStackTrace();
            return new Forward("error");
        }
                    
        return new Forward("report");
    }

    private void setTestStatusOptions(String sessionFilterTab)
    {
        if (sessionFilterTab.equals("CU")) {    // current
            testStatusOptions = new String[7];
            testStatusOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
            testStatusOptions[1] = FilterSortPageUtils.FILTERTYPE_COMPLETED;
            testStatusOptions[2] = FilterSortPageUtils.FILTERTYPE_INPROGRESS; 
            testStatusOptions[3] = FilterSortPageUtils.FILTERTYPE_SCHEDULED; 
            testStatusOptions[4] = FilterSortPageUtils.FILTERTYPE_STUDENTPAUSE;
            testStatusOptions[5] = FilterSortPageUtils.FILTERTYPE_STUDENTSTOP; 
            testStatusOptions[6] = FilterSortPageUtils.FILTERTYPE_SYSTEMSTOP; 
        }
        else 
        if (sessionFilterTab.equals("FU")) {    // future
            testStatusOptions = new String[2];
            testStatusOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
            testStatusOptions[1] = FilterSortPageUtils.FILTERTYPE_SCHEDULED; 
        }
        else {                                  // completed
            testStatusOptions = new String[6];
            testStatusOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
            testStatusOptions[1] = FilterSortPageUtils.FILTERTYPE_COMPLETED;
            testStatusOptions[2] = FilterSortPageUtils.FILTERTYPE_INPROGRESS; 
            testStatusOptions[3] = FilterSortPageUtils.FILTERTYPE_INCOMPLETE; 
            testStatusOptions[4] = FilterSortPageUtils.FILTERTYPE_NOTTAKEN; 
            testStatusOptions[5] = FilterSortPageUtils.FILTERTYPE_STUDENTPAUSE;
        }
    }

    private boolean isTestSessionCompleted(TestSessionVO testSession)
    {
        boolean completed = false;
        if (testSession.getTestAdminStatus().equalsIgnoreCase("PA")) {
            completed = true;
        }
        return completed;
    }
    

    private boolean isTabeSession(String productType)
    {
        if (productType.equalsIgnoreCase("TB") || productType.equalsIgnoreCase("TL"))
            return true;   
        else             
            return false;
    }


    private boolean isTabeLocatorSession(String productType)
    {
        if (productType.equalsIgnoreCase("TL"))
            return true;   
        else             
            return false;
    }

    private TestElement []  orderedSubtestList(TestElement[] subtestelements,Integer studentId,Integer testAdminId)
    {
        TestElement [] orderedSubtestElements = new TestElement[subtestelements.length];
        StudentManifest [] sms = getStudentManifests(studentId,testAdminId);
        HashMap smHM = new HashMap();
        for(int i=0;i<sms.length;i++){
           StudentManifest  sm = sms[i];
           smHM.put(sm.getItemSetId(),new Integer(i));
        }  
        for(int j=0;j<subtestelements.length;j++){
            TestElement te = subtestelements[j];
            if(smHM.containsKey(te.getItemSetId())){
                orderedSubtestElements[((Integer)smHM.get(te.getItemSetId())).intValue()] = te;
            }
        }
        return orderedSubtestElements;
    }

    private StudentManifest [] getStudentManifests(Integer studentId,Integer testAdminId)
    {
        StudentManifest [] sm = null;
        try {  
                StudentManifestData  smd =  this.scheduleTest.getManifestForRoster(this.userName,studentId,testAdminId,null,null,null);
                sm = smd.getStudentManifests();
        }catch (CTBBusinessException be) {
            be.printStackTrace();
        }   
        return sm;
    }
    
    private TestProduct getProductForTestAdmin(Integer testAdminId)
    {
        TestProduct tp = null;
        try {      
            tp = this.testSessionStatus.getProductForTestAdmin(this.userName, testAdminId);
        }
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }   
        return tp;
    }
    
    private void toggleValidationStatus(ViewMonitorStatusForm form) 
    {
        Integer testRosterId = form.getTestRosterId();
        
        try {      
            this.testSessionStatus.toggleRosterValidationStatus(this.userName, testRosterId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private void toggleCustomerFlagStatus(ViewMonitorStatusForm form) 
    {
        Integer testRosterId = form.getTestRosterId();
        
        try {      
            this.testSessionStatus.toggleCustomerFlagStatus(this.userName, testRosterId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }       
    }

    private void toggleSubtestValidationStatus(ViewMonitorStatusForm form) 
    {        
        Integer testRosterId = form.getTestRosterId();
        Integer[] itemSetIds = getSelectedSubtestIds(form);

        try {    
            this.testSessionStatus.toggleSubtestValidationStatus(this.userName, testRosterId, itemSetIds);
        }
        catch (Exception e) {
            e.printStackTrace();
        }                
    }
    

    private void toggleSubtestCustomerFlagStatus(ViewMonitorStatusForm form) 
    {
        Integer testRosterId = form.getTestRosterId();
        Integer[] itemSetIds = getSelectedSubtestIds(form);

        try {    
            this.testSessionStatus.toggleSubtestCustomerFlagStatus(this.userName, testRosterId, itemSetIds);
        }
        catch (Exception e) {
            e.printStackTrace();
        }                
    }

    private void addTABESubtest(TestElement te) 
    {
        if (this.TABETestElements == null)
            this.TABETestElements = new ArrayList();
            
        boolean found = false;
        for (int i=0 ; i<this.TABETestElements.size() ; i++) {
            TestElement tte = (TestElement)this.TABETestElements.get(i);
            if (tte.getItemSetId().intValue() == te.getItemSetId().intValue()) {
                found = true;
                break;
            }
        }
        if (! found) {
            this.TABETestElements.add(te);
        }
    }
    
    /**
     * FormData get and set methods may be overwritten by the Form Bean editor.
     */
    public static class ViewMonitorStatusForm extends SanitizedFormData
    {
		private static final long serialVersionUID = 1L;

		private String actionElement = null;

        private String sortColumn = null;
        private String sortOrderBy = null;
        private Integer pageRequested = null;
        private Boolean filterVisible = null;

        private Integer testRosterId = null;
        private Integer studentId = null;
        private Integer itemSetId = null;
        private String[] selectedItemSetIds = null;
        
        private TestRosterFilter testRosterFilter = null;
        private String currentAction = null;
        
        private Integer maxPage = null;

        public ViewMonitorStatusForm()
        {
        }
        
        public void init()
        {
            this.actionElement = "none";
            
            this.sortColumn = FilterSortPageUtils.TESTROSTER_DEFAULT_SORT;
            this.sortOrderBy = FilterSortPageUtils.ASCENDING;      
            this.pageRequested = new Integer(1);                  
            this.maxPage = new Integer(1);      
            this.filterVisible = Boolean.FALSE;
            
            this.testRosterId = new Integer(0);
            this.studentId = new Integer(0);
            this.itemSetId = new Integer(0);
            
            this.testRosterFilter = new TestRosterFilter();   
            this.currentAction = "none";  
            this.selectedItemSetIds = new String[1];       
        }
        
        public ViewMonitorStatusForm createClone()
        {
            ViewMonitorStatusForm copied = new ViewMonitorStatusForm();
            copied.setActionElement(this.actionElement);
            copied.setSortColumn(this.sortColumn);
            copied.setSortOrderBy(this.sortOrderBy);
            copied.setPageRequested(this.pageRequested);
            copied.setFilterVisible(this.filterVisible);
            copied.setTestRosterId(this.testRosterId);            
            copied.setStudentId(this.studentId);        
            copied.setItemSetId(this.itemSetId);            
            copied.setTestRosterFilter(this.testRosterFilter);
            copied.setCurrentAction(this.currentAction);        
            copied.setMaxPage(this.maxPage);
            copied.setSelectedItemSetIds(this.selectedItemSetIds);
            return copied;
        }
                
        public void validateValues() 
        {
            if ((this.maxPage == null) || (this.maxPage.intValue() == 0))
                this.maxPage = new Integer(1);
            
            if (this.pageRequested == null)
                this.pageRequested = new Integer(1);
            if (this.pageRequested.intValue() <= 0)
                this.pageRequested = new Integer(1);
            if (this.pageRequested.intValue() > this.maxPage.intValue())
                this.pageRequested = new Integer(this.maxPage.intValue());
                
            if (this.sortColumn == null)
                this.sortColumn = FilterSortPageUtils.TESTROSTER_DEFAULT_SORT;
            if (this.sortOrderBy == null)
                this.sortOrderBy = FilterSortPageUtils.ASCENDING;

            if (this.testRosterId == null)
                this.testRosterId = new Integer(0);
                
            if (this.studentId == null)
                this.studentId = new Integer(0);

            if (this.itemSetId == null)
                this.itemSetId = new Integer(0);

            if (this.currentAction == null)
                this.currentAction = "none";

            if (this.filterVisible == null)
            	this.filterVisible = Boolean.FALSE;
            
        }
        
        public void resetSortPage() 
        {
            this.sortColumn = FilterSortPageUtils.TESTROSTER_DEFAULT_SORT;
            this.sortOrderBy = FilterSortPageUtils.ASCENDING;      
            this.pageRequested = new Integer(1);                  
        }
        public void resetValuesForAction() 
        {
            if ((actionElement.indexOf("sortColumn") != -1) ||
                (actionElement.indexOf("sortOrderBy") != -1)) {
                this.pageRequested = new Integer(1);
            }            
        }
        public void setTestRosterId(Integer testRosterId)
        {
            this.testRosterId = testRosterId;
        }
        public Integer getTestRosterId()
        {
            return this.testRosterId;
        }                    
        public void setItemSetId(Integer itemSetId)
        {
            this.itemSetId = itemSetId;
        }
        public Integer getItemSetId()
        {
            return this.itemSetId;
        }                    
        public void setStudentId(Integer studentId)
        {
            this.studentId = studentId;
        }
        public Integer getStudentId()
        {
            return this.studentId;
        }                    
        public void setSortColumn(String sortColumn)
        {
            this.sortColumn = sortColumn;
        }
        public String getSortColumn()
        {
            return this.sortColumn;
        }       
        public void setSortOrderBy(String sortOrderBy)
        {
            this.sortOrderBy = sortOrderBy;
        }
        public String getSortOrderBy()
        {
            return this.sortOrderBy;
        }       
        public void setPageRequested(Integer pageRequested)
        {
            this.pageRequested = pageRequested;
        }
        public Integer getPageRequested()
        {
            return this.pageRequested;
        }   
        public void setFilterVisible(Boolean filterVisible)
        {
            this.filterVisible = filterVisible;
        }
        public void setMaxPage(Integer maxPage)
        {
            this.maxPage = maxPage;
        }
        public Integer getMaxPage()
        {
            return this.maxPage;
        }
        public Boolean getFilterVisible()
        {
            return this.filterVisible;
        }
        public void setActionElement(String actionElement)
        {
            this.actionElement = actionElement;
        }
        public String getActionElement()
        {
            return this.actionElement;
        }    
        public void setTestRosterFilter(TestRosterFilter testRosterFilter)
        {
            this.testRosterFilter = testRosterFilter;
        }
        public TestRosterFilter getTestRosterFilter()
        {
            if (this.testRosterFilter == null)
                this.testRosterFilter = new TestRosterFilter();            
        	
            return this.testRosterFilter;
        }    
        public void setCurrentAction(String currentAction)
        {
            this.currentAction = currentAction;
        }
        public String getCurrentAction()
        {
            return this.currentAction;
        }    
                                  
        public void reset(org.apache.struts.action.ActionMapping mapping,
                          javax.servlet.http.HttpServletRequest request)
        {
             this.testRosterFilter = new TestRosterFilter();
        }    
        public void setSelectedItemSetIds(String[] itemSetIds)
        {
            this.selectedItemSetIds = itemSetIds;
        }
        public String[] getSelectedItemSetIds()
        {
            if(this.selectedItemSetIds == null || this.selectedItemSetIds.length == 0) {
                this.selectedItemSetIds = new String[1];
            }                 
            return this.selectedItemSetIds;
        }
           
    }

	public String getSetCustomerFlagToogleButton() {
		return setCustomerFlagToogleButton;
	}

	public void setSetCustomerFlagToogleButton(String setCustomerFlagToogleButton) {
		this.setCustomerFlagToogleButton = setCustomerFlagToogleButton;
	}

	public String getDefaultcustomerFlagStatus() {
		return defaultcustomerFlagStatus;
	}

	public void setDefaultcustomerFlagStatus(String defaultcustomerFlagStatus) {
		this.defaultcustomerFlagStatus = defaultcustomerFlagStatus;
	}

	public boolean isShowStudentReportButton() {
		return showStudentReportButton;
	}

	public void setShowStudentReportButton(boolean showStudentReportButton) {
		this.showStudentReportButton = showStudentReportButton;
	}

	public void setSubtestValidationAllowed(boolean subtestValidationAllowed) {
		this.subtestValidationAllowed = subtestValidationAllowed;
	}

	public boolean getSubtestValidationAllowed() {
		return this.subtestValidationAllowed;
	}
	
	public String[] getNameOptions() {
		return nameOptions;
	}

	public void setNameOptions(String[] nameOptions) {
		this.nameOptions = nameOptions;
	}

	public String[] getValidationStatusOptions() {
		return validationStatusOptions;
	}

	public void setValidationStatusOptions(String[] validationStatusOptions) {
		this.validationStatusOptions = validationStatusOptions;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public List getStudentStatusSubtests() {
		return studentStatusSubtests;
	}

	public void setStudentStatusSubtests(List studentStatusSubtests) {
		this.studentStatusSubtests = studentStatusSubtests;
	}

	public List getTABETestElements() {
		return TABETestElements;
	}

	public void setTABETestElements(List testElements) {
		TABETestElements = testElements;
	}

	public com.ctb.control.testAdmin.TestSessionStatus getTestSessionStatus() {
		return testSessionStatus;
	}

	public void setTestSessionStatus(
			com.ctb.control.testAdmin.TestSessionStatus testSessionStatus) {
		this.testSessionStatus = testSessionStatus;
	}

	public String[] getTestStatusOptions() {
		return testStatusOptions;
	}

	public void setTestStatusOptions(String[] testStatusOptions) {
		this.testStatusOptions = testStatusOptions;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setSessionDetailsShowScores(boolean sessionDetailsShowScores) {
		this.sessionDetailsShowScores = sessionDetailsShowScores;
	}
}
 
