
package manageBulkAccommodation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.beehive.controls.api.bean.Control;
import org.apache.beehive.netui.pageflow.Forward;
import org.apache.beehive.netui.pageflow.PageFlowController;
import org.apache.beehive.netui.pageflow.annotations.Jpf;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import utils.FilterSortPageUtils;
import utils.OrgNodeUtils;
import utils.StudentPathListUtils;
import utils.StudentSearchUtils;
import utils.WebUtils;

import com.ctb.bean.request.FilterParams;
import com.ctb.bean.request.PageParams;
import com.ctb.bean.request.SortParams;
import com.ctb.bean.request.FilterParams.FilterParam;
import com.ctb.bean.request.FilterParams.FilterType;
import com.ctb.bean.studentManagement.CustomerConfiguration;
import com.ctb.bean.studentManagement.CustomerConfigurationValue;
import com.ctb.bean.studentManagement.CustomerDemographic;
import com.ctb.bean.studentManagement.CustomerDemographicValue;
import com.ctb.bean.studentManagement.ManageBulkStudentData;
import com.ctb.bean.testAdmin.Customer;
import com.ctb.bean.testAdmin.SessionStudent;
import com.ctb.bean.testAdmin.StudentAccommodations;
import com.ctb.bean.testAdmin.StudentNode;
import com.ctb.bean.testAdmin.StudentNodeData;
import com.ctb.bean.testAdmin.User;
import com.ctb.exception.CTBBusinessException;
import com.ctb.util.web.sanitizer.SanitizedFormData;
import com.ctb.widgets.bean.PagerSummary;
import com.ctb.widgets.bean.PathListEntry;

import dto.Message;
import dto.PathNode;
import dto.StudentAccommodationsDetail;
import dto.StudentProfileInformation;


/**
 * @jpf:controller
 **/

/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** ManageBulkAccommodationController ************* ///////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////

@Jpf.Controller()
public class ManageBulkAccommodationController extends PageFlowController
{
	static final long serialVersionUID = 1L;

	/**
	 * @common:control
	 */

	@Control()
	private com.ctb.control.studentManagement.StudentManagement studentManagement;

	@Control()
	private com.ctb.control.db.OrgNode orgnode;
	
	private static final String ACTION_DEFAULT           = "defaultAction";
	public static final String ACTION_CHANGE_ACCOMMODATION = "changeAccommodation";
	public static final String ACTION_APPLY = "apply";
	public static final String ACTION_CLEAR = "clear";
	public static final String ACTION_UPDATE_TOTAL = "updateTotal";
	public static final String ACTION_ADD_ALL = "addAll";
	public static final String ACTION_GET_DEMO_DATA = "getDemoData";


	public String[] gradeOptions = null;

	public String[] realDemographicOptions = null;
	public String[] demographic1 = null;
	public String[] demographic2 = null;
	public String[] demographic3 = null;
	public String[] demographicValues1 = null;
	public String[] demographicValues2 = null;
	public String[] demographicValues3 = null;
	CustomerDemographic [] customerDemographic = null;
	private String userName = null;
	private Integer customerId = null;
	private User user = null;
	
	private List<PathListEntry> orgNodePath = null;
	private Integer selectedOrgNodeId = null;    
	   

	private ManageBulkAccommodationForm savedForm = null;
	

	private HashMap currentOrgNodesInPathList = null;
	public List selectedOrgNodes = null;
	public Integer[] currentOrgNodeIds = null;

	public String action = "bulksAddStudent";
	private String selectedFormOperand = null;
	private String [] selectedAccommodations = null;

	// customer configuration
	CustomerConfiguration[] customerConfigurations = null;
	CustomerConfigurationValue[] customerConfigurationsValue = null;

	/* declaration of All list */
	// student demographics
	List demographics = null;

	private List selectedStudents = null;
	private List originalSelectedStudents = null;

	// student accommodations
	public StudentAccommodationsDetail accommodations = null;
	private boolean islaslinkCustomer = false;

	// misc
	public String pageTitle = null;
	public String pageMessage = null;
	public String studentName = null;   
	private String selectedGrade = null;
	private String selectedDemo1 = null;
	private String selectedDemo2 = null;
	private String selectedDemo3 = null;
	private String selectedDemoValue1 = null;
	private String selectedDemoValue2 = null;
	private String selectedDemoValue3 = null;

	private Integer orgNodeId = null;  
	private Hashtable studentsHashtable;

	public String[] accommodationOperandOptions = {FilterSortPageUtils.STUDENTS_WITH_AND_WITHOUT_ACCOMMODATIONS, FilterSortPageUtils.STUDENTS_WITH_ACCOMMODATIONS, FilterSortPageUtils.STUDENTS_WITHOUT_ACCOMMODATIONS};
	public String[] selectedAccommodationsOptions = {"Calculator", "Color/Font", "TestPause", "ScreenReader", "UntimedTest"};

/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** begin controller ************* ///////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * This method represents the point of entry into the pageflow
	 * @jpf:action
	 * @jpf:forward name="success" path="beginFindStudent.do"
	 */
	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "success",
					path = "beginAddBulkStudent.do")
	})
	protected Forward begin()
	{
		return new Forward("success");
	}




	/**
	 * initialize
	 */
	private ManageBulkAccommodationForm initialize(String action)
	{        
		getUserDetails();

		this.orgNodePath = new ArrayList<PathListEntry>();
		this.currentOrgNodesInPathList = new HashMap();
		this.currentOrgNodeIds = new Integer[0];
		this.selectedOrgNodes = new ArrayList();

		this.savedForm = new ManageBulkAccommodationForm();



		this.savedForm.init();
		savedForm.setSelectedStudentIds(null); 
		savedForm.setSelectedStudentCount(new Integer(0));
		savedForm.setSelectedStudentWithAccommodationCount(new Integer(0));
		savedForm.setShowAccommodations(Boolean.FALSE);        

		this.selectedOrgNodeId = new Integer(0);

		if ((savedForm.getAccommodationOperand() == null) || savedForm.getAccommodationOperand().equals(""))
			savedForm.setAccommodationOperand(FilterSortPageUtils.STUDENTS_WITH_AND_WITHOUT_ACCOMMODATIONS);
		this.selectedFormOperand = savedForm.getAccommodationOperand();  

		if ((savedForm.getSelectedGrade() == null) || savedForm.getSelectedGrade().equals(""))
			savedForm.setSelectedGrade(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedGrade = savedForm.getSelectedGrade();

		if ((savedForm.getSelectedDemo1() == null) || savedForm.getSelectedDemo1().equals(""))
			savedForm.setSelectedDemo1(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedDemo1 = savedForm.getSelectedDemo1();

		if ((savedForm.getSelectedDemo2() == null) || savedForm.getSelectedDemo2().equals(""))
			savedForm.setSelectedDemo2(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedDemo2 = savedForm.getSelectedDemo2();

		if ((savedForm.getSelectedDemo3() == null) || savedForm.getSelectedDemo3().equals(""))
			savedForm.setSelectedDemo3(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedDemo3 = savedForm.getSelectedDemo3();

		if ((savedForm.getSelectedDemoValue1() == null) || savedForm.getSelectedDemoValue1().equals(""))
			savedForm.setSelectedDemoValue1(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedDemoValue1 = savedForm.getSelectedDemoValue1();

		if ((savedForm.getSelectedDemoValue2() == null) || savedForm.getSelectedDemoValue2().equals(""))
			savedForm.setSelectedDemoValue2(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedDemoValue2 = savedForm.getSelectedDemoValue2();

		if ((savedForm.getSelectedDemoValue3() == null) || savedForm.getSelectedDemoValue3().equals(""))
			savedForm.setSelectedDemoValue3(FilterSortPageUtils.FILTERTYPE_SHOWALL);        
		this.selectedDemoValue3 = savedForm.getSelectedDemoValue3();

		String selectedAccommodationElements = savedForm.getSelectedAccommodationElements();
		if (selectedAccommodationElements != null && selectedAccommodationElements.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(selectedAccommodationElements, ",");
			this.selectedAccommodations = new String[st.countTokens()];
			int index = 0;
			while (st.hasMoreTokens())
			{
				String value = st.nextToken().trim();
				this.selectedAccommodations[index] = value;                    
				index++;
			}
			savedForm.setSelectedAccommodations(this.selectedAccommodations);
		}


		this.getSession().setAttribute("userHasReports", userHasReports());
		customerHasBulkAccommodation();
		customerHasScoring();//For hand scoring changes
		isTopLevelUser();
		copySelectedStudents(savedForm);

		return this.savedForm;
	}
	
	
	//LLO- 118 - Change for Ematrix UI
    private void isTopLevelUser(){
		
		boolean isUserTopLevel = false;
		boolean isLaslinkUserTopLevel = false;
		boolean isLaslinkUser = false;
		isLaslinkUser = this.islaslinkCustomer;
		try {
			if(isLaslinkUser) {
				isUserTopLevel = orgnode.checkTopOrgNodeUser(this.userName);	
				if(isUserTopLevel){
					isLaslinkUserTopLevel = true;				
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getSession().setAttribute("isTopLevelUser",isLaslinkUserTopLevel);	
	}
	/**
	 * userHasReports
	 */
	private Boolean customerHasBulkAccommodation() 
	{
		boolean hasBulkStudentConfigurable = false;
		//Bulk Accommodation
		for (int i=0; i < this.customerConfigurations.length; i++) {

			CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
			if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Bulk_Accommodation") && 
					cc.getDefaultValue().equals("T")) {
				hasBulkStudentConfigurable = true; 
				break;
			}
		}

		getSession().setAttribute("isBulkAccommodationConfigured", hasBulkStudentConfigurable);


		return new Boolean(hasBulkStudentConfigurable);           
	}

	//changes for scoring
		
		/**
		 * This method checks whether customer is configured to access the scoring feature or not.
		 * @return Return Boolean 
		 */
	
	private Boolean customerHasScoring()
    {               
        
        boolean hasScoringConfigurable = false;
        boolean isLaslinkCustomer = false;
        for (int i=0; i < customerConfigurations.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Hand_Scoring") && 
            		cc.getDefaultValue().equals("T")	) {
            	hasScoringConfigurable = true;
               // break;
            } 
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Laslink_Customer")
    				&& cc.getDefaultValue().equals("T")) {
    			isLaslinkCustomer = true;
    			//break;
            }
        }
        this.setIslaslinkCustomer(isLaslinkCustomer);
        getSession().setAttribute("isScoringConfigured", hasScoringConfigurable);
       
        return new Boolean(hasScoringConfigurable);
    }
    


	/**
	 * getStudentAccommodationsFromRequest
	 */
	/**
	 * getStudentAccommodationsFromRequest
	 */
	private void getStudentAccommodationsFromRequest() 
	{
		// first get values from request
		String screenReader = getRequest().getParameter("screen_reader");
		String calculator = getRequest().getParameter("calculator");
		String highlighter = getRequest().getParameter("highlighter");
		String testPause = getRequest().getParameter("test_pause");
		String untimedTest = getRequest().getParameter("untimed_test");
		String colorFont = getRequest().getParameter("colorFont");
		
		this.accommodations.setCalculatorRBValue(calculator);
		this.accommodations.setScreenReaderRBValue(screenReader);
		this.accommodations.setHighLighterRBValue(highlighter);
		this.accommodations.setTestPauseRBValue(testPause);
		this.accommodations.setUntimedTestRBValue(untimedTest);
		this.accommodations.setSelectedColorFont(colorFont);
		setCustomerAccommodations(this.accommodations, false);

		String question_bgrdColor = this.getRequest().getParameter("question_bgrdColor");
		if (question_bgrdColor != null)
		{
			this.accommodations.setQuestion_bgrdColor(question_bgrdColor);
		}

		String question_fontColor = this.getRequest().getParameter("question_fontColor");
		if (question_fontColor != null)
		{
			this.accommodations.setQuestion_fontColor(question_fontColor);
		}

		String answer_bgrdColor = this.getRequest().getParameter("answer_bgrdColor");
		if (answer_bgrdColor != null)
		{
			this.accommodations.setAnswer_bgrdColor(answer_bgrdColor);
		}

		String answer_fontColor = this.getRequest().getParameter("answer_fontColor");
		if (answer_fontColor != null)
		{
			this.accommodations.setAnswer_fontColor(answer_fontColor);
		}

		String fontSize = this.getRequest().getParameter("fontSize");
		if (fontSize != null)
		{
			this.accommodations.setFontSize(fontSize);
		}
	}

	private void copySelectedStudents(ManageBulkAccommodationForm form)
	{
		this.selectedStudents = form.getSelectedStudents();    
		if (this.selectedStudents == null)
			this.selectedStudents = new ArrayList();  

		this.originalSelectedStudents = new ArrayList();  

		for (int i=0 ; i<this.selectedStudents.size() ; i++) {
			SessionStudent ss = (SessionStudent) this.selectedStudents.get(i);
			this.originalSelectedStudents.add(ss);
		}
	}


	/**
	 * getUserDetails
	 */
	private void getUserDetails()
	{
		java.security.Principal principal = getRequest().getUserPrincipal();
		if (principal != null) 
			this.userName = principal.toString();
		else            
			this.userName = (String)getSession().getAttribute("userName");

		try
		{
			this.user = this.studentManagement.getUserDetails(this.userName, this.userName);     
			this.customerId = user.getCustomer().getCustomerId();
		}
		catch (CTBBusinessException be)
		{
			be.printStackTrace();
		}        
		getSession().setAttribute("userName", this.userName);

		getCustomerConfigurations();             
	}


	/**
	 * userHasReports
	 */
	private Boolean userHasReports() 
	{
		boolean hasReports = false;
		try
		{      
			Customer customer = this.user.getCustomer();
			Integer customerId = customer.getCustomerId();   
			hasReports = this.studentManagement.userHasReports(this.userName, customerId);
		}
		catch (CTBBusinessException be)
		{
			be.printStackTrace();
		}        
		return new Boolean(hasReports);           
	}





	/**
	 * @jpf:action
	 * @jpf:forward name="success" path="goToViewStudent.do" 
	 * @jpf:forward name="error" path="addEditStudent.do"
	 */
	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "success", 
					path = "beginAddBulkStudent.do"),
			@Jpf.Forward(name = "selectAccommodation", 
					path = "findBulkStudent.do")
	})
	protected Forward saveBulkStudentData(ManageBulkAccommodationForm form)
	{   

		Boolean isTokenValid = isTokenValid();
		if (isTokenValid) {
			commitSelection(form);
			boolean successFlag=true;
			if(this.selectedStudents != null && this.selectedStudents.size()>0 ) {
				Integer[] studentId = new Integer[this.selectedStudents.size()];

				for(int i=0;i< this.selectedStudents.size();i++)
				{
					SessionStudent sst = (SessionStudent)this.selectedStudents.get(i);
					if(sst != null) {
						studentId[i] = sst.getStudentId();
						//System.out.println(studentId[i]);
					}

				}

				if(studentId != null && studentId.length > 0) {
					StudentAccommodations sa = saveAccommodationsSelected();
					if (sa != null) {
						try {
							
							this.studentManagement.updateBulkStudentAccommodations(this.userName,sa,studentId);
						} catch (CTBBusinessException e) {
							// TODO Auto-generated catch block
							successFlag=false;
						}
						

						if(successFlag) {
							form.setMessage(Message.BULK_ADD_TITLE, Message.BULK_ADD_SUCCESSFUL, Message.INFORMATION);
							this.getRequest().setAttribute("pageMessage", form.getMessage());
						}
						else {
							form.setMessage(Message.BULK_ADD_TITLE, Message.BULK_ADD_ERROR, Message.INFORMATION);
							this.getRequest().setAttribute("pageMessage", form.getMessage());
						}
						
					} else {
						
							form.setMessage(Message.BULK_ADD_TITLE, Message.BULK_ACCOM_NOTSELECTED, Message.ALERT);
							this.getRequest().setAttribute("pageMessage", form.getMessage());
							return new Forward("selectAccommodation");
					}
					
				}
			}
		}

		return new Forward("success");

	}



	public StudentAccommodations saveAccommodationsSelected()
	{	
		boolean isAccommodationSelected = false;
		String screenReader = getRequest().getParameter("screen_reader");
		String calculator = getRequest().getParameter("calculator");
		String testPause = getRequest().getParameter("test_pause");
		String untimedTest = getRequest().getParameter("untimed_test");
		String highLighter = getRequest().getParameter("highlighter");
		String colorFont = getRequest().getParameter("colorFont");
		String questionBgrdColor = this.getRequest().getParameter("question_bgrdColor");

		String questionFontColor = this.getRequest().getParameter("question_fontColor");

		String answerBgrdColor = this.getRequest().getParameter("answer_bgrdColor");


		String answerFontColor = this.getRequest().getParameter("answer_fontColor");


		String fontSize = this.getRequest().getParameter("fontSize");

		StudentAccommodations stuAommodations = new StudentAccommodations();
		StudentAccommodationsDetail stdDetail = new StudentAccommodationsDetail();

		//generate dynamic sql query for update
		StringBuffer result = new StringBuffer();
		if (screenReader != null ) {
			stuAommodations.setScreenReader(screenReader);
			isAccommodationSelected = true;
		}

		if (calculator != null) {
			stuAommodations.setCalculator(calculator);
			isAccommodationSelected = true;
		}	 

		if (testPause != null ) {
			stuAommodations.setTestPause(testPause);
			isAccommodationSelected = true;
		}

		if (untimedTest != null ) {
			stuAommodations.setUntimedTest(untimedTest);
			isAccommodationSelected = true;
		}

		if (highLighter != null) {
			stuAommodations.setHighlighter(highLighter);
			isAccommodationSelected = true;
		}

		if (colorFont != null) {
			stuAommodations.setColorFont(colorFont);
			isAccommodationSelected = true;//Change for defect -# 65698 
		}
		if (questionBgrdColor != null) {
			
			stuAommodations.setQuestionBackgroundColor
			(stdDetail.getColorHexMapping(questionBgrdColor));
			isAccommodationSelected = true;
		}

		if (questionFontColor != null) {
			stuAommodations.setQuestionFontColor(
					stdDetail.getColorHexMapping(questionFontColor));
			isAccommodationSelected = true;
			
		}
		if (answerBgrdColor != null) {
			stuAommodations.setAnswerBackgroundColor(
					stdDetail.getColorHexMapping(answerBgrdColor));
			isAccommodationSelected = true;
		}

		if (answerFontColor != null) {
			stuAommodations.setAnswerFontColor(
					stdDetail.getColorHexMapping(answerFontColor));
			isAccommodationSelected = true;
		}

		if (fontSize != null) {
			stuAommodations.setAnswerFontSize(fontSize);
			isAccommodationSelected = true;
		}
		if (fontSize != null) {
			stuAommodations.setQuestionFontSize(fontSize);
			isAccommodationSelected = true;
		}

		if (colorFont != null && colorFont.equalsIgnoreCase("F")) {
			stuAommodations.setColorFont(colorFont);       //Change for defect -# 65698 
			stuAommodations.setAnswerBackgroundColor(null);
			stuAommodations.setAnswerFontColor(null);
			stuAommodations.setAnswerFontSize(null);
			stuAommodations.setQuestionBackgroundColor(null);
			stuAommodations.setQuestionFontColor(null);
			stuAommodations.setQuestionFontSize(null);
		}
		if(isAccommodationSelected)
			return stuAommodations;
		else
			return null;
	}





	/**
	 * setFullPathNodeName
	 */
	private void setFullPathNodeName(List orgNodes, Integer studentId)    
	{
		for (int i=0; i < orgNodes.size(); i++)
		{
			PathNode node = (PathNode)orgNodes.get(i);
			Integer orgNodeId = node.getId();
			String fullPathNodeName = StudentPathListUtils.getFullPathNodeName(
					this.userName, orgNodeId, this.studentManagement);
			node.setFullPathName(fullPathNodeName);   
		}    
	}


	/**
	 * buildOrgNodesForSelector
	 */
	private List buildOrgNodesForSelector(List selectedOrgNodes, List selectableOrgNodes, 
								Integer studentId, String sortOrderBy)    
	{
		List orgNodesForSelector = new ArrayList();

		for (int i=0; i < selectedOrgNodes.size(); i++)
		{
			PathNode node = (PathNode)selectedOrgNodes.get(i);
			node.setSelectable("false");
			orgNodesForSelector.add(node);
		}
		for (int i=0; i < selectableOrgNodes.size(); i++)
		{
			PathNode node = (PathNode)selectableOrgNodes.get(i);
			node.setSelectable("true");
			orgNodesForSelector.add(node);
		}

		setFullPathNodeName(orgNodesForSelector, studentId);    

		OrgNodeUtils.sortList(orgNodesForSelector, sortOrderBy); 

		return orgNodesForSelector; 
	}







/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** FIND STUDENT ************* //////////////////////////////////    
/////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method represents the point of entry into the pageflow
	 * @jpf:action
	 * @jpf:forward name="success" path="findStudent.do"
	 */
	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "success",
					path = "findBulkStudent.do")
	})
	protected Forward beginAddBulkStudent()
	{
		ManageBulkAccommodationForm form = initialize(ACTION_DEFAULT);
		form.setSelectedStudentId(null); 
		getCustomerDemographicOptions();
		initGradeOptions(ACTION_DEFAULT, form, null);

		form.setSelectedOrgNodeId(null);
		// accommodation change
		this.accommodations = null;

		return new Forward("success", form);
	}



	/**
	 * @jpf:action
	 * @jpf:forward name="success" path="find_student.jsp"
	 * @jpf:forward name="viewStudent" path="beginViewStudent.do"
	 * @jpf:forward name="editStudent" path="beginEditStudent.do"
	 * @jpf:forward name="deleteStudent" path="beginDeleteStudent.do"
	 * @jpf:validation-error-forward name="failure" path="logout.do"
	 */
	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "success",
					path = "add_bulk_student.jsp")

	}, 
	validationErrorForward = @Jpf.Forward(name = "failure",
			path = "logout.do"))
			protected Forward findBulkStudent(ManageBulkAccommodationForm form)
	{    

		form.validateValues();
		saveToken(this.getRequest());
		String currentAction = form.getCurrentAction();
		String actionElement = form.getActionElement();
		//accommodation changes
		if (actionElement != null && ((actionElement.equals("ButtonGoInvoked_studentTableAnchor")) ||
				(actionElement.equals("EnterKeyInvoked_studentTableAnchor")) || 
				(actionElement.equals("{actionForm.studentSortOrderBy}") ) || 
				(currentAction.equals(ACTION_ADD_ALL) ) || currentAction.equals(ACTION_GET_DEMO_DATA)
				|| (currentAction.equals(ACTION_CHANGE_ACCOMMODATION) )
				|| (actionElement.equals("{actionForm.studentPageRequested}")))
		) {

			if(this.accommodations != null) {
				getStudentAccommodationsFromRequest() ;
			} 
			else {
				this.accommodations = getStudentAccommodations();
			}
		} else {

			this.accommodations = getStudentAccommodations();
		}
		this.getRequest().setAttribute("accommodations",accommodations);

		
		form.resetValuesForAction(actionElement); 
		if (form.getSelectedGrade() == null)
		{                
			form.setSelectedGrade(this.selectedGrade);
		} 
		if (form.getSelectedDemo1() == null)
		{                
			form.setSelectedDemo1(this.selectedDemo1);
		} 
		if (form.getSelectedDemo2() == null)
		{                
			form.setSelectedDemo2(this.selectedDemo2);
		} 
		if (form.getSelectedDemo3() == null)
		{                
			form.setSelectedDemo3(this.selectedDemo3);
		} 
		if (form.getSelectedDemoValue1() == null)
		{                
			form.setSelectedDemoValue1(this.selectedDemoValue1);
		} 
		if (form.getSelectedDemoValue2() == null)
		{                
			form.setSelectedDemoValue2(this.selectedDemoValue2);
		} 
		if (form.getSelectedDemoValue3() == null)
		{                
			form.setSelectedDemoValue3(this.selectedDemoValue3);
		} 

		actionElement = form.getActionElement();

		Integer orgNodeId = form.getSelectedOrgNodeId();
		if ((orgNodeId != null) && (this.selectedOrgNodeId != null) && (orgNodeId.intValue() != this.selectedOrgNodeId.intValue()))
		{
			form.resetValuesForAction("newNodeSelected");
		}


		String orgNodeName = form.getOrgNodeName();
		this.orgNodeId = form.getOrgNodeId();   

		if (currentAction.equals(ACTION_APPLY))
		{
			this.selectedGrade = form.getSelectedGrade();
			this.selectedDemo1 = form.getSelectedDemo1();
			this.selectedDemo2 = form.getSelectedDemo2();
			this.selectedDemo3 = form.getSelectedDemo3();
			this.selectedDemoValue1 = form.getSelectedDemoValue1();
			this.selectedDemoValue2 = form.getSelectedDemoValue2();
			this.selectedDemoValue3 = form.getSelectedDemoValue3();
			this.selectedFormOperand = form.getAccommodationOperand();
			this.selectedAccommodations = form.getSelectedAccommodations();
			if (this.selectedAccommodations != null)
			{
				String selectedAccommodationElements = new String("");
				for (int i=0; i < this.selectedAccommodations.length; i++)
				{
					selectedAccommodationElements += (this.selectedAccommodations[i] + ",");                
				}
				form.setSelectedAccommodationElements(selectedAccommodationElements);
			}
		}
		if (currentAction.equals(ACTION_CLEAR))
		{

			this.selectedGrade = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedGrade(this.selectedGrade);    
			this.selectedDemo1 = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedDemo1(this.selectedDemo1);    
			this.selectedDemo2 = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedDemo2(this.selectedDemo2);    
			this.selectedDemo3 = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedDemo3(this.selectedDemo3);    
			this.selectedDemoValue1 = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedDemoValue1(this.selectedDemoValue1);    
			this.selectedDemoValue2 = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedDemoValue2(this.selectedDemoValue2);    
			this.selectedDemoValue3 = FilterSortPageUtils.FILTERTYPE_SHOWALL;
			form.setSelectedDemoValue3(this.selectedDemoValue3);    
			this.demographic1 = this.realDemographicOptions;
			this.demographic2 = this.realDemographicOptions;
			this.demographic3 = this.realDemographicOptions;
			this.selectedFormOperand = FilterSortPageUtils.STUDENTS_WITH_AND_WITHOUT_ACCOMMODATIONS;
			form.setAccommodationOperand(this.selectedFormOperand);
			this.selectedAccommodations = null;
			form.setSelectedAccommodations(this.selectedAccommodations);   
		}

		if (currentAction.equals(ACTION_GET_DEMO_DATA))
		{	
			String[] tempDemographic1 = new String[realDemographicOptions.length +1];
			String[] tempDemographic2 = new String[realDemographicOptions.length +1];
			String[] tempDemographic3 = new String[realDemographicOptions.length +1];

			tempDemographic1[0] = form.getSelectedDemo1();
			tempDemographic2[0] = form.getSelectedDemo2();
			tempDemographic3[0] = form.getSelectedDemo3();
			tempDemographic1[1] = (form.getSelectedDemo1().equals(FilterSortPageUtils.FILTERTYPE_SHOWALL)? null : FilterSortPageUtils.FILTERTYPE_SHOWALL);
			tempDemographic2[1] = (form.getSelectedDemo2().equals(FilterSortPageUtils.FILTERTYPE_SHOWALL)? null : FilterSortPageUtils.FILTERTYPE_SHOWALL);
			tempDemographic3[1] = (form.getSelectedDemo3().equals(FilterSortPageUtils.FILTERTYPE_SHOWALL)? null : FilterSortPageUtils.FILTERTYPE_SHOWALL);

			int x = 1;	
			for(int k = 1;k< realDemographicOptions.length; k++){
				if(realDemographicOptions[k] != null) {
					if( !realDemographicOptions[k].equals(form.getSelectedDemo1()) 
							&& !realDemographicOptions[k].equals(form.getSelectedDemo2()) 
							&& !realDemographicOptions[k].equals(form.getSelectedDemo3())){
						x += 1;
						tempDemographic1[x] =  realDemographicOptions[k];
						tempDemographic2[x] =  realDemographicOptions[k];
						tempDemographic3[x] =  realDemographicOptions[k];
					}
				}
			}
			demographic1 = tempDemographic1;
			demographic2 = tempDemographic2;
			demographic3 = tempDemographic3;

			for(int i= 0; i<customerDemographic.length; i++){

				if(form.getSelectedDemo1() != null && form.getSelectedDemo1().equals(FilterSortPageUtils.FILTERTYPE_SHOWALL)) {
					form.setSelectedDemoValue1(FilterSortPageUtils.FILTERTYPE_SHOWALL);
					this.selectedDemoValue1 = form.getSelectedDemoValue1();
				}
				if(form.getSelectedDemo2() != null && form.getSelectedDemo2().equals(FilterSortPageUtils.FILTERTYPE_SHOWALL)) {
					form.setSelectedDemoValue2(FilterSortPageUtils.FILTERTYPE_SHOWALL);
					this.selectedDemoValue2 = form.getSelectedDemoValue2();
				}
				if(form.getSelectedDemo3() != null && form.getSelectedDemo3().equals(FilterSortPageUtils.FILTERTYPE_SHOWALL)) {
					form.setSelectedDemoValue3(FilterSortPageUtils.FILTERTYPE_SHOWALL);
					this.selectedDemoValue3 = form.getSelectedDemoValue3();
				}

				if(!(this.savedForm.getSelectedDemo1().equals(form.getSelectedDemo1()))) {
					if(customerDemographic[i].getLabelName().equals(form.getSelectedDemo1())){
						CustomerDemographicValue[] customerDemographicValue  = customerDemographic[i].getCustomerDemographicValues();
						form.setSelectedDemoValue1(FilterSortPageUtils.FILTERTYPE_SHOWALL);
						demographicValues1 = new String[customerDemographicValue.length+1];
						demographicValues1[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
						for (int j=0 ; j<customerDemographicValue.length ; j++) {  
							if(customerDemographicValue[j].getVisible() != null 
									&& !(customerDemographicValue[j].getVisible().equals("")) 
									&& customerDemographicValue[j].getVisible().equals("T")) {
								demographicValues1[j+1] = customerDemographicValue[j].getValueName();
							}
						}

					}
				}
				if(!(this.savedForm.getSelectedDemo2().equals(form.getSelectedDemo2()))) {
					if(customerDemographic[i].getLabelName().equals(form.getSelectedDemo2())){
						CustomerDemographicValue[] customerDemographicValue  = customerDemographic[i].getCustomerDemographicValues();
						form.setSelectedDemoValue2(FilterSortPageUtils.FILTERTYPE_SHOWALL);
						demographicValues2 = new String[customerDemographicValue.length+1];
						demographicValues2[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
						for (int j=0 ; j<customerDemographicValue.length ; j++) { 
							if(customerDemographicValue[j].getVisible() != null 
									&& !(customerDemographicValue[j].getVisible().equals("")) 
									&& customerDemographicValue[j].getVisible().equals("T")) {
								demographicValues2[j+1] = customerDemographicValue[j].getValueName();
							}
						}

					}
				}
				if(!(this.savedForm.getSelectedDemo3().equals(form.getSelectedDemo3()))) {
					if(customerDemographic[i].getLabelName().equals(form.getSelectedDemo3())){
						CustomerDemographicValue[] customerDemographicValue  = customerDemographic[i].getCustomerDemographicValues();
						form.setSelectedDemoValue3(FilterSortPageUtils.FILTERTYPE_SHOWALL);
						demographicValues3 = new String[customerDemographicValue.length+1];
						demographicValues3[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
						for (int j=0 ; j<customerDemographicValue.length ; j++) { 
							if(customerDemographicValue[j].getVisible() != null 
									&& !(customerDemographicValue[j].getVisible().equals("")) 
									&& customerDemographicValue[j].getVisible().equals("T")) {
								demographicValues3[j+1] = customerDemographicValue[j].getValueName();
							}
						}

					}
				}

			}

		}


		String accommodationOperand = form.getAccommodationOperand();
		if ((accommodationOperand != null) && (accommodationOperand.equals(FilterSortPageUtils.STUDENTS_WITH_ACCOMMODATIONS)))
		{
			form.setShowAccommodations(Boolean.TRUE);        
			if ((form.getSelectedAccommodations() != null) && (form.getSelectedAccommodations().length > 0))  {            
				this.getRequest().setAttribute("disableApply", "false");     
				form.setDisableApply("false");
			} else {
				this.getRequest().setAttribute("disableApply", "true"); 
				form.setDisableApply("true");
			}
		}               
		else
		{
			form.setShowAccommodations(Boolean.FALSE); 
			form.setDisableApply("false");
			this.getRequest().setAttribute("disableApply", "false");     
		}      
		if(currentAction.equals(ACTION_GET_DEMO_DATA)) {
			savedForm.setSelectedDemo1(form.getSelectedDemo1());
			savedForm.setSelectedDemo2(form.getSelectedDemo2());
			savedForm.setSelectedDemo3(form.getSelectedDemo3());
			savedForm.setSelectedDemoValue1(form.getSelectedDemoValue1());
			savedForm.setSelectedDemoValue2(form.getSelectedDemoValue2());
			savedForm.setSelectedDemoValue3(form.getSelectedDemoValue3());
			commitSelection(form);
			savedForm.getStudentPagerSummary().setTotalSelectedObjects(this.selectedStudents.size());
			form.setStudentPagerSummary(savedForm.getStudentPagerSummary());
			form.setActionElement(ACTION_DEFAULT); 
			form.setCurrentAction(ACTION_DEFAULT); 
			//this.accommodations = getStudentAccommodations();
			setFormInfoOnRequest(this.savedForm);
			return new Forward("success");
		}

		// get data for Pathlist - getChildrenOrgNodes
		FilterParams filter = new FilterParams();
		ArrayList filters = new ArrayList();

		FilterParams demoFilter = new FilterParams();
		ArrayList demoFilters = new ArrayList();
		if (this.selectedGrade != null && !this.selectedGrade.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedGrade;
			filters.add(new FilterParam("StudentGrade", arg, FilterType.EQUALS));
		}
		if (this.selectedDemo1 != null && !this.selectedDemo1.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedDemo1;
			if (this.selectedDemoValue1 != null && !this.selectedDemoValue1.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
			{
				String [] valueArg = new String[1];
				valueArg[0] = arg[0]+ "_" + this.selectedDemoValue1;
				demoFilters.add(new FilterParam("ValueName", valueArg, FilterType.EQUALS));
			} else {
				demoFilters.add(new FilterParam("LabelName", arg, FilterType.EQUALS));
			}

		}

		if (this.selectedDemo2 != null && !this.selectedDemo2.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedDemo2;
			if (this.selectedDemoValue2 != null && !this.selectedDemoValue2.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
			{
				String [] valueArg = new String[1];
				valueArg[0] = arg[0]+ "_" + this.selectedDemoValue2;
				demoFilters.add(new FilterParam("ValueName", valueArg, FilterType.EQUALS));
			} else {
				demoFilters.add(new FilterParam("LabelName", arg, FilterType.EQUALS));
			}

		}

		if (this.selectedDemo3 != null && !this.selectedDemo3.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedDemo3;
			if (this.selectedDemoValue3 != null && !this.selectedDemoValue3.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
			{
				String [] valueArg = new String[1];
				valueArg[0] = arg[0]+ "_" +this.selectedDemoValue3;
				demoFilters.add(new FilterParam("ValueName", valueArg, FilterType.EQUALS));
			} else {
				demoFilters.add(new FilterParam("LabelName", arg, FilterType.EQUALS));
			}
		}


		if (this.selectedFormOperand != null && this.selectedFormOperand.equals(FilterSortPageUtils.STUDENTS_WITH_ACCOMMODATIONS))
		{
			if (this.selectedAccommodations != null)
			{
				for (int i=0; i < this.selectedAccommodations.length; i++)
				{
					if (this.selectedAccommodations[i].indexOf("Color/Font") >= 0)
					{
						String [] arg = new String[1];
						arg[0] = "T";
						filters.add(new FilterParam("ColorFontAccommodation", arg, FilterType.EQUALS));
					}
					else
					{
						String [] arg = new String[1];
						arg[0] = "T";
						filters.add(new FilterParam(this.selectedAccommodations[i], arg, FilterType.EQUALS));
					}
				}
			}
		}
		else if (this.selectedFormOperand != null && this.selectedFormOperand.equals(FilterSortPageUtils.STUDENTS_WITHOUT_ACCOMMODATIONS))
		{
			String [] arg = new String[1];
			arg[0] = "T";
			filters.add(new FilterParam("ScreenReader", arg, FilterType.NOTEQUAL));
			filters.add(new FilterParam("Calculator", arg, FilterType.NOTEQUAL));
			filters.add(new FilterParam("TestPause", arg, FilterType.NOTEQUAL));
			filters.add(new FilterParam("UntimedTest", arg, FilterType.NOTEQUAL));
			//filters.add(new FilterParam("Highlighter", arg, FilterType.NOTEQUAL));
			String [] arg2 = new String[1];
			arg2[0] = "F";
			filters.add(new FilterParam("ColorFontAccommodation", arg2, FilterType.EQUALS)); //HasColorFontAccommodations
		}
		filter.setFilterParams((FilterParam[])filters.toArray(new FilterParam[0]));
		demoFilter.setFilterParams((FilterParam[])demoFilters.toArray(new FilterParam[0]));

		PageParams page = FilterSortPageUtils.buildPageParams(form.getOrgPageRequested(), FilterSortPageUtils.PAGESIZE_5);
		SortParams sort = FilterSortPageUtils.buildSortParams(form.getOrgSortColumn(), form.getOrgSortOrderBy(), null, null);   

		List orgNodes = findByHierarchy(form,page,sort,filter,demoFilter); 
		
		if(((form.getSelectedOrgNodeId() != null &&  (this.savedForm.selectedOrgNodeId != null )) 
				&& (form.getSelectedOrgNodeId().intValue() != this.savedForm.selectedOrgNodeId.intValue())) 
				|| currentAction.equals(ACTION_APPLY)
				|| (currentAction.equals(ACTION_CLEAR) && this.savedForm.isAppliedFilterFlag())){
			
			form.studentPageRequested = new Integer(1);
			removeSelectedStudentToList();

		}
		else {
			if (isNeedCommitSelection(currentAction, actionElement, form)) {

				commitSelection(form);
				this.getRequest().setAttribute("viewOnly", Boolean.TRUE);//4th Jan Bulk Accommodation
			}
		}
		filters = new ArrayList();
		demoFilters = new ArrayList();
		if (this.selectedGrade != null && !this.selectedGrade.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedGrade;
			filters.add(new FilterParam("StudentGrade", arg, FilterType.EQUALS));
		}

		if (this.selectedDemo1 != null && !this.selectedDemo1.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedDemo1;
			if (this.selectedDemoValue1 != null && !this.selectedDemoValue1.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
			{
				String [] valueArg = new String[1];
				valueArg[0] = arg[0]+ "_" + this.selectedDemoValue1;
				demoFilters.add(new FilterParam("ValueName", valueArg, FilterType.EQUALS));
			} else {
				demoFilters.add(new FilterParam("LabelName", arg, FilterType.EQUALS));
			}

		}

		if (this.selectedDemo2 != null && !this.selectedDemo2.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedDemo2;
			if (this.selectedDemoValue2 != null && !this.selectedDemoValue2.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
			{
				String [] valueArg = new String[1];
				valueArg[0] = arg[0]+ "_" + this.selectedDemoValue2;
				demoFilters.add(new FilterParam("ValueName", valueArg, FilterType.EQUALS));
			} else {
				demoFilters.add(new FilterParam("LabelName", arg, FilterType.EQUALS));
			}

		}

		if (this.selectedDemo3 != null && !this.selectedDemo3.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
		{
			String [] arg = new String[1];
			arg[0] = this.selectedDemo3;
			if (this.selectedDemoValue3 != null && !this.selectedDemoValue3.equals(FilterSortPageUtils.FILTERTYPE_SHOWALL))
			{
				String [] valueArg = new String[1];
				valueArg[0] = arg[0]+ "_" +this.selectedDemoValue3;
				demoFilters.add(new FilterParam("ValueName", valueArg, FilterType.EQUALS));
			} else {
				demoFilters.add(new FilterParam("LabelName", arg, FilterType.EQUALS));
			}
		}

		if (this.selectedFormOperand != null && this.selectedFormOperand.equals(FilterSortPageUtils.STUDENTS_WITH_ACCOMMODATIONS))
		{
			if (this.selectedAccommodations != null)
			{
				for (int i=0; i < this.selectedAccommodations.length; i++)
				{
					if (this.selectedAccommodations[i].indexOf("Color/Font") >= 0)
					{
						String [] arg = new String[1];
						arg[0] = "true";
						filters.add(new FilterParam("HasColorFontAccommodations", arg, FilterType.EQUALS));
					}
					else
					{
						String [] arg = new String[1];
						arg[0] = "T";
						filters.add(new FilterParam(this.selectedAccommodations[i], arg, FilterType.EQUALS));
					}
				}
			}
		}
		else if (this.selectedFormOperand != null && this.selectedFormOperand.equals(FilterSortPageUtils.STUDENTS_WITHOUT_ACCOMMODATIONS))
		{
			String [] arg = new String[1];
			arg[0] = "T";
			filters.add(new FilterParam("ScreenReader", arg, FilterType.NOTEQUAL));
			filters.add(new FilterParam("Calculator", arg, FilterType.NOTEQUAL));
			filters.add(new FilterParam("TestPause", arg, FilterType.NOTEQUAL));
			filters.add(new FilterParam("UntimedTest", arg, FilterType.NOTEQUAL));
			//filters.add(new FilterParam("Highlighter", arg, FilterType.NOTEQUAL));
			String [] arg2 = new String[1];
			arg2[0] = "false";
			filters.add(new FilterParam("HasColorFontAccommodations", arg2, FilterType.EQUALS)); //HasColorFontAccommodations
		}
		filter.setFilterParams((FilterParam[])filters.toArray(new FilterParam[0]));
		demoFilter.setFilterParams((FilterParam[])demoFilters.toArray(new FilterParam[0]));


		page = FilterSortPageUtils.buildPageParams(form.getStudentPageRequested(), FilterSortPageUtils.PAGESIZE_10);
		sort = FilterSortPageUtils.buildSortParams(form.getStudentSortColumn(), form.getStudentSortOrderBy(), null, null);
		Integer selectedOrgNodeId = form.getSelectedOrgNodeId();
		if (currentAction.equals(ACTION_ADD_ALL))
		{
			addAllStudents(filter,demoFilter, selectedOrgNodeId, selectedOrgNodeId); 
			form.setCurrentAction(ACTION_DEFAULT);
		}  



		ManageBulkStudentData msData=null;
		msData = StudentSearchUtils.searchBulkStudentsByOrgNode(this.userName, this.studentManagement, selectedOrgNodeId, filter,demoFilter, page, sort);



		form.setStudentNodes(buildStudentList(msData));
		form.setStudentPagerSummary(buildStudentPagerSummary(msData, form.getStudentPageRequested()));        
		form.setStudentMaxPage(msData.getFilteredPages());
		form.setStudentNodesSize(form.getStudentNodes().size() > 0 ? "true" : null);
		form.setMsCount(msData.getTotalCount().intValue() > 0 ? "true" : null);
		this.getRequest().setAttribute("studentNodes", form.getStudentNodes());        
		this.getRequest().setAttribute("hasStudentNodes", form.getStudentNodesSize());
		this.getRequest().setAttribute("studentPagerSummary", form.getStudentPagerSummary());
		this.getRequest().setAttribute("nodeContainsStudents",form.getMsCount());			
		this.getRequest().setAttribute("isBulkAccommodation", Boolean.TRUE);
		this.getRequest().setAttribute("isScoring", Boolean.TRUE);

		setSelectedStudentOrgListToForm(form);                    
		setSelectedStudentCountToForm(form);
		setSelectedStudentCountToOrgNode(orgNodes);
		form.setCurrentAction(ACTION_DEFAULT);

		this.pageTitle = buildPageTitle(ACTION_DEFAULT, form);
		boolean appliedFilterFlag = false;

		if( (demoFilter != null && demoFilter.getFilterParams().length > 0)
				|| (filter != null && filter.getFilterParams().length > 0) ) {
			appliedFilterFlag = true;
		}
		this.getRequest().setAttribute("appliedFilterFlag", appliedFilterFlag);
		form.setAppliedFilterFlag(appliedFilterFlag);

		//Bulk Accommodation Changes
		customerHasBulkAccommodation();
		//scoring changes
		customerHasScoring();//For hand scoring changes
		isTopLevelUser();
		form.setCustomerConfigurations(this.customerConfigurations);
		this.getRequest().setAttribute("customerConfigurations", customerConfigurations);
		this.savedForm = form;
		form.setActionElement(ACTION_DEFAULT);    

		setFormInfoOnRequest(form);
		return new Forward("success");
	}



	public  PagerSummary buildStudentPagerSummary(ManageBulkStudentData msData,Integer pageRequested ) 
	{


		PagerSummary pagerSummary = new PagerSummary();
		pagerSummary.setCurrentPage(pageRequested);                
		pagerSummary.setTotalObjects(msData.getTotalCount());
		pagerSummary.setTotalPages(msData.getFilteredPages());
		pagerSummary.setTotalFilteredObjects(msData.getFilteredCount());                
		if(this.selectedStudents != null){
			pagerSummary.setTotalSelectedObjects(this.selectedStudents.size());
		} else {
			pagerSummary.setTotalSelectedObjects(0);
		}
		return pagerSummary;
	}
	private List buildStudentList(ManageBulkStudentData ssd) 
	{
		List studentList = new ArrayList();
		this.studentsHashtable = new Hashtable();        
		SessionStudent [] sessionStudents = ssd.getManageStudents();    

		for (int i=0; i < sessionStudents.length; i++)
		{
			SessionStudent ss = (SessionStudent)sessionStudents[i];
			if (ss != null)
			{
				String middleName = ss.getMiddleName();
				if ((middleName != null) && (middleName.length() > 0)) {
					ss.setMiddleName(String.valueOf(middleName.charAt(0)));
				}
				studentList.add(ss);
				this.studentsHashtable.put(ss.getStudentId(), ss);


			}
		}

		return studentList;
	}

	private String encodeStudentOrgIds(SessionStudent ss)
	{
		Integer studentId = ss.getStudentId();
		if ((ss != null) && (studentId != null) && (ss.getOrgNodeId() != null))
			return studentId + "-" + ss.getOrgNodeId();
		else
			return "";
	}



	private void setSelectedStudentCountToOrgNode(List orgNodes) 
	{
		if (orgNodes != null)
		{
			for (int i=0; i < orgNodes.size(); i++)
			{
				PathNode node = (PathNode)orgNodes.get(i);
				if (node != null)
				{
					Integer selectedCount = getSelectedStudentCountAtOrgNode(node.getId());
					node.setSelectedCount(selectedCount);
				}
			}
		}
	}

	private Integer getSelectedStudentCountAtOrgNode(Integer orgNodeId)
	{
		int count = 0;
		if ((this.selectedStudents != null) && (orgNodeId != null))
		{
			for (int i=0; i < this.selectedStudents.size(); i++)
			{   
				SessionStudent ss = (SessionStudent)this.selectedStudents.get(i);
				if ((ss != null) && (ss.getOrgNodeId() != null))
				{
					if (ss.getOrgNodeId().intValue() == orgNodeId.intValue())
						count++;
				}
			} 
		}
		return new Integer(count);
	}
	private void setSelectedStudentCountToForm(ManageBulkAccommodationForm form)
	{
		if (this.selectedStudents != null)
		{
			int selectedStudentWithAccommoCount = 0;
			for (int i=0; i < this.selectedStudents.size(); i++)
			{   
				SessionStudent ss = (SessionStudent)this.selectedStudents.get(i);
				if ((ss != null) && (ss.getHasAccommodations() != null))
				{
					if (ss.getHasAccommodations().equalsIgnoreCase("true"))
						selectedStudentWithAccommoCount++;
				}
			} 
			form.setSelectedStudentCount(new Integer(this.selectedStudents.size()));
			form.setSelectedStudentWithAccommodationCount(new Integer(selectedStudentWithAccommoCount));        
		}
	}

	private void setSelectedStudentOrgListToForm(ManageBulkAccommodationForm form)
	{
		if (this.selectedStudents != null)
		{
			int size = this.selectedStudents.size();
			int index = 0;
			Integer[] studentOrgList = new Integer[size];
			for (int i=0; i < size; i++)
			{   
				SessionStudent ss = (SessionStudent)this.selectedStudents.get(i);
				Integer studentId=ss.getStudentId();
				if ((ss != null) && (studentId != null))
				{
					if (this.studentsHashtable.containsKey(studentId))
					{
						studentOrgList[index] = studentId;
						index++;
					}    
				}
			}         
			form.setSelectedStudentOrgList(studentOrgList);
		}
	}


	private void addAllStudents(FilterParams filter,FilterParams demoFilter, Integer orgNodeId, Integer selectedOrgNodeId)
	{
		PageParams page = new PageParams(1, 10000, 1000);

		ManageBulkStudentData msData = StudentSearchUtils.searchBulkStudentsByOrgNode(this.userName, this.studentManagement, selectedOrgNodeId, filter,demoFilter, page, null);
		List studentNodes = buildStudentList(msData);


		for (int i=0; i <studentNodes.size(); i++) {   
			SessionStudent ss = (SessionStudent) studentNodes.get(i);         
			if ( (ss != null) ) {   
				addSelectedStudentToList(ss);                 
			}
		}
	}
	private boolean isNeedCommitSelection(String currentAction, String actionElement, ManageBulkAccommodationForm form)
	{
		boolean needCommit = false;

		if ((actionElement != null) && actionElement.equals("{actionForm.studentPageRequested}"))
			needCommit = true;
		if ((actionElement != null) && actionElement.equals("{actionForm.orgNodeId}"))
			needCommit = true;
		if ((actionElement != null) && actionElement.equals("{actionForm.studentSortOrderBy}"))
			needCommit = true;
		//Added for Table pager go button start
        if ((actionElement != null) && actionElement.equals("EnterKeyInvoked_studentTableAnchor"))
            needCommit = true;
        if ((actionElement != null) && actionElement.equals("ButtonGoInvoked_studentTableAnchor"))
            needCommit = true;
        //Added for Table pager go button end



		return needCommit;                         
	}

	private void commitSelection(ManageBulkAccommodationForm form)
	{
		Integer [] selectedStudentOrgList = form.getSelectedStudentOrgList();
		collectSelectedStudents(selectedStudentOrgList);
	}    
	private void collectSelectedStudents(Integer[] selectedStudentOrgList)
	{   
		if ((selectedStudentOrgList != null) && (this.selectedStudents != null))
		{

			List unselectedStudents = getUnselectedStudentsInPage(selectedStudentOrgList);
			for (int i=0; i < unselectedStudents.size(); i++)
			{
				SessionStudent unselected = (SessionStudent)unselectedStudents.get(i);
				for (int j=0; j < this.selectedStudents.size(); j++)
				{
					SessionStudent ss = (SessionStudent)this.selectedStudents.get(j);
					if (ss.getStudentId().intValue() == unselected.getStudentId().intValue())
					{
						this.selectedStudents.remove(j);
						break;
					}
				}                
			}

			for (int i=0; i < selectedStudentOrgList.length; i++)
			{   
				Integer studentOrg = selectedStudentOrgList[i];         
				if (studentOrg != null)
				{
					SessionStudent ss = (SessionStudent)this.studentsHashtable.get(studentOrg);

					addSelectedStudentToList(ss);

				}
			}
		}
	}

	private List getUnselectedStudentsInPage(Integer[] selectedStudentOrgList)
	{
		Iterator iter = null;
		ArrayList unselectedStudents = new ArrayList();       
		if (selectedStudentOrgList != null)
		{
			if ((selectedStudentOrgList.length == 1) && (selectedStudentOrgList[0] == null))
			{
				iter = this.studentsHashtable.values().iterator();
				while (iter.hasNext())
				{
					SessionStudent ss = (SessionStudent)iter.next();

					unselectedStudents.add(ss);
				}
				return unselectedStudents;
			}
		}
		if ((this.studentsHashtable != null) && (selectedStudentOrgList != null))
		{
			iter = this.studentsHashtable.values().iterator();
			while (iter.hasNext())
			{
				SessionStudent ss = (SessionStudent)iter.next();
				boolean found = false;
				for (int i=0; i < selectedStudentOrgList.length; i++)
				{   
					Integer studentOrg = selectedStudentOrgList[i];         
					if ((studentOrg != null) && (studentOrg.intValue() == ss.getStudentId()))
					{
						found = true;
						break;
					}
				}
				if (! found )
				{
					unselectedStudents.add(ss);
				}
			}
		}
		return unselectedStudents;
	}

	private void addSelectedStudentToList(SessionStudent student)
	{
		if (!this.selectedStudents.contains(student))
			this.selectedStudents.add(student);


	}

	private void removeSelectedStudentToList() {

		this.selectedStudents= new ArrayList();

	}

	private StudentAccommodationsDetail getStudentAccommodations()
	{


		this.accommodations = new StudentAccommodationsDetail();
		setCustomerAccommodations(this.accommodations, true);
		this.accommodations.convertHexToText();      

		return this.accommodations;
	}



	//Bulk Accommodation Changes
	/**
	 * setCustomerAccommodations
	 */
	private void setCustomerAccommodations(StudentAccommodationsDetail sad, boolean isSetDefaultValue) 
	{        
		// set checked value if there is configuration for this customer
		for (int i=0; i < this.customerConfigurations.length; i++)
		{
			CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
			String ccName = cc.getCustomerConfigurationName();
			String defaultValue = cc.getDefaultValue() != null ? cc.getDefaultValue() : "F";
			String editable = cc.getEditable() != null ? cc.getEditable() : "F";

			if (isSetDefaultValue)
				editable = "F";

			if (defaultValue.equalsIgnoreCase("T") && editable.equalsIgnoreCase("F"))
			{

				if (ccName.equalsIgnoreCase("screen_reader"))
				{
					sad.setScreenReader(Boolean.TRUE);
				}

				if (ccName.equalsIgnoreCase("calculator"))
				{
					sad.setCalculator(Boolean.TRUE);
				}

				if (ccName.equalsIgnoreCase("test_pause"))
				{
					sad.setTestPause(Boolean.TRUE);
				}

				if (ccName.equalsIgnoreCase("untimed_test"))
				{
					sad.setUntimedTest(Boolean.TRUE);
				}

				if (ccName.equalsIgnoreCase("highlighter"))
				{
					sad.setHighlighter(Boolean.TRUE);
				}
			}
		}
	}


/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** COLOR FONT SETTINGS ************* ////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @jpf:action
	 * @jpf:forward name="success" path="/previewer/PreviewerController.jpf"
	 */
	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "success",
					path = "/previewer/PreviewerController.jpf")
	})
	protected Forward colorFontPreview()
	{      
		String param = getRequest().getParameter("param");
		getSession().setAttribute("param", param);

		return new Forward("success");
	}

	/**
	 * @jpf:action
	 * @jpf:forward name="success" path="addEditStudent.do"
	 */
	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "success",
					path = "addEditStudent.do")
	})
	protected Forward colorFontPreviewDone() {

		return new Forward("success");
	}

	/**
	 * This method represents the point of entry into the pageflow
	 * @jpf:action
	 */
	@Jpf.Action()
	protected Forward findSuggestions()
	{                
		String controlId = this.getRequest().getHeader("controlId");        
		String controlValue = this.getRequest().getHeader("controlValue");        

		String suggestions = StudentSearchUtils.findStudentSuggestions(this.studentManagement, this.userName, controlId, controlValue.toLowerCase());   
		WebUtils.writeResponseContent(this.getResponse(), suggestions);        

		return null;
	}




	/**
	 * findByHierarchy
	 */
	private List findByHierarchy(ManageBulkAccommodationForm form,PageParams page,SortParams sort,FilterParams filter,FilterParams demoFilter)
	{      


		ManageBulkStudentData msData = null;
		String actionElement = form.getActionElement();
		String currentAction = form.getCurrentAction();

		form.resetValuesForAction(actionElement);        

		String orgNodeName = form.getOrgNodeName();
		Integer orgNodeId = form.getOrgNodeId();   



		StudentNodeData snd = StudentPathListUtils.getOrganizationNodesForBulkAccommodation(this.userName, this.studentManagement,this.customerId, orgNodeId, filter,demoFilter, page, sort);        

		if (form.getOrgPageRequested().intValue() > snd.getFilteredPages().intValue())
		{
			form.setOrgPageRequested(snd.getFilteredPages());
		}
		List orgNodes = buildOrgNodeList(snd);  //Added for CR017
		String orgCategoryName = StudentPathListUtils.getOrgCategoryName(orgNodes);
		PagerSummary orgPagerSummary = buildOrgNodePagerSummary(snd, form);   

		form.setOrgMaxPage(snd.getFilteredPages());

		if (actionElement.equals("actionElement"))  //Changes for Find_Student_Hierarchy Display Message
		{
			PathNode node = StudentPathListUtils.findOrgNode(orgNodes, form.getSelectedOrgNodeId());
			if (node != null)
			{
				form.setSelectedOrgNodeId(node.getId());
				form.setSelectedOrgNodeName(node.getName());                
				form.setSelectedStudentId(null);
			}
		}

		form.setOrgNodes(orgNodes);
		form.setOrgNodePath(this.orgNodePath);
		form.setOrgCategoryName(orgCategoryName);
		this.getRequest().setAttribute("orgNodePath", this.orgNodePath);
		this.getRequest().setAttribute("orgNodes", orgNodes);        
		this.getRequest().setAttribute("orgPagerSummary", orgPagerSummary);
		this.getRequest().setAttribute("orgCategoryName", orgCategoryName);  

		boolean nodeChanged = StudentPathListUtils.adjustOrgNodePath(this.orgNodePath, orgNodeId, orgNodeName);


		if (actionElement.equals("{actionForm.orgPageRequested}") || actionElement.equals("{actionForm.orgSortOrderBy}") )
		{                
			nodeChanged = true;
		}


		String selectedOrgNodeName = null;
		if (nodeChanged)
		{
			selectedOrgNodeName = initFirstOrgNode(orgNodes, form);
		}
		else
		{
			Integer id = form.getSelectedOrgNodeId();
			if (id != null)
			{
				for (int i=0; i < orgNodes.size(); i++)
				{
					PathNode node = (PathNode)orgNodes.get(i);
					if (node.getId().intValue() == id.intValue())
					{
						selectedOrgNodeName = node.getName();
						break;
					}
				}
			}
			if (selectedOrgNodeName == null)
			{
				selectedOrgNodeName = initFirstOrgNode(orgNodes, form);
			}
		}   

		return orgNodes;
	}

	private PagerSummary buildOrgNodePagerSummary(StudentNodeData snd, ManageBulkAccommodationForm form) 
	{
		Integer pageRequested = form.getOrgPageRequested();
		PagerSummary pagerSummary = new PagerSummary();
		pagerSummary.setCurrentPage(pageRequested);        
		pagerSummary.setTotalPages(snd.getFilteredPages());
		pagerSummary.setTotalObjects(snd.getFilteredCount());
		pagerSummary.setTotalFilteredObjects(null);     
		form.setOrgPagerSummary(pagerSummary);
		return pagerSummary;
	}
	private List buildOrgNodeList(StudentNodeData snd) 
	{
		ArrayList nodeList = new ArrayList();
		PathNode pathNode = null;
		StudentNode [] nodes = snd.getStudentNodes();        
		for (int i=0; i < nodes.length; i++)
		{
			StudentNode node = (StudentNode)nodes[i];
			if (node != null)
			{
				pathNode = new PathNode();
				pathNode.setName(node.getOrgNodeName());
				pathNode.setId(node.getOrgNodeId());   
				pathNode.setFilteredCount(node.getStudentCount());
				Integer selectedCount = getSelectedStudentCountAtOrgNode(node.getOrgNodeId());
				pathNode.setSelectedCount(selectedCount);
				pathNode.setChildrenNodeCount(node.getChildNodeCount());
				pathNode.setHasChildren((new Boolean(node.getChildNodeCount().intValue() > 0)).toString());                
				pathNode.setCategoryName(node.getOrgNodeCategoryName());                
				nodeList.add(pathNode);
			}
		}
		return nodeList;
	}

	private String initFirstOrgNode(List orgNodes,ManageBulkAccommodationForm form)
	{
		String selectedOrgNodeName = "";
		if (orgNodes.size() > 0)
		{
			PathNode node = (PathNode)orgNodes.get(0);
			form.setSelectedOrgNodeId(node.getId());
			form.setSelectedOrgNodeName(node.getName());
			this.orgNodeId = node.getId();
			selectedOrgNodeName = node.getName();
		}
		else
		{
			form.setSelectedOrgNodeId(new Integer(0));
			form.setSelectedOrgNodeName("");
			this.orgNodeId = new Integer(0);
			selectedOrgNodeName = "";
		}
		return selectedOrgNodeName;
	}



/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** PRIVATE METHODS ************* ///////////////////////////////    
/////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * buildPageTitle
	 */
	private String buildPageTitle(String action, ManageBulkAccommodationForm form)
	{
		String title = "";
		StudentProfileInformation student = null;


		title = "Find Student";


		return title;            
	}    


	/**
	 * getCustomerConfigurations
	 */
	private void getCustomerConfigurations()
	{
		try {
			this.customerConfigurations = this.studentManagement.getCustomerConfigurations(this.userName, this.customerId);

			Boolean supportAccommodations = Boolean.TRUE;            
			Customer customer = this.user.getCustomer();

			String hideAccommodations = customer.getHideAccommodations();
			if ((hideAccommodations != null) && hideAccommodations.equalsIgnoreCase("T"))
			{
				supportAccommodations = Boolean.FALSE;
			}
			getSession().setAttribute("supportAccommodations", supportAccommodations); 

		}
		catch (CTBBusinessException be) {
			be.printStackTrace();
		}
	}

	/**
	 * getCustomerDemographicOptions
	 */
	private  void getCustomerDemographicOptions()
	{	
		try {
			customerDemographic =  this.studentManagement.getCustomerDemographics(this.userName, this.customerId);
		}
		catch (CTBBusinessException be) {
			be.printStackTrace();
		}
		if (customerDemographic != null)
		{
			realDemographicOptions = new String[customerDemographic.length+1];
			realDemographicOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;

			for (int i=0 ; i<customerDemographic.length ; i++) { 
				if(customerDemographic[i].getVisible() != null 
						&& !(customerDemographic[i].getVisible().equals("")) 
						&& customerDemographic[i].getVisible().equals("T")) {
					realDemographicOptions[i+1] = customerDemographic[i].getLabelName();
				}
			}
		}
		if(realDemographicOptions != null) {
			demographic1 = realDemographicOptions;
			demographic2 = realDemographicOptions;
			demographic3 = realDemographicOptions;
		}

	}



	/**
	 * getGradeOptions
	 */
	private String [] getGradeOptions(String action)
	{
		String[] grades = null;
		try {
			grades =  this.studentManagement.getGradesForCustomer(this.userName, this.customerId);
		}
		catch (CTBBusinessException be) {
			be.printStackTrace();
		}

		List options = new ArrayList();
		options.add(FilterSortPageUtils.FILTERTYPE_SHOWALL);

		for (int i=0 ; i<grades.length ; i++) {        
			options.add(grades[i]);
		}

		return (String [])options.toArray(new String[0]);        
	}



	/**
	 * initGradeOptions
	 */
	private void initGradeOptions(String action, ManageBulkAccommodationForm form, String grade)
	{        
		this.gradeOptions = getGradeOptions(action);

	}

	/*
	 * set form Value in request
	 */
	private void setFormInfoOnRequest(ManageBulkAccommodationForm form) {

		this.getRequest().setAttribute("studentPagerSummary", form.getStudentPagerSummary());
		this.getRequest().setAttribute("orgPagerSummary", form.getOrgPagerSummary());
		//this.getRequest().setAttribute("pageMessage", form.getMessage());
		this.getRequest().setAttribute("orgNodePath", form.getOrgNodePath());
		this.getRequest().setAttribute("orgNodes", form.getOrgNodes());
		this.getRequest().setAttribute("orgCategoryName", form.getOrgCategoryName());
		this.getRequest().setAttribute("studentNodes", form.getStudentNodes());        
		this.getRequest().setAttribute("hasStudentNodes",form.getStudentNodesSize());
		this.getRequest().setAttribute("studentPagerSummary", form.getStudentPagerSummary());
		this.getRequest().setAttribute("nodeContainsStudents", form.getMsCount());			
		this.getRequest().setAttribute("isBulkAccommodation", Boolean.TRUE);
		this.getRequest().setAttribute("isScoring", Boolean.TRUE);
		this.getRequest().setAttribute("appliedFilterFlag", form.isAppliedFilterFlag());
		this.getRequest().setAttribute("customerConfigurations", form.getCustomerConfigurations());
	}


	@Jpf.Action(forwards = { 
			@Jpf.Forward(name = "done",
					path = "../manageStudent/beginFindStudent.do")
	})
	protected Forward selectStudentCancel(ManageBulkAccommodationForm form)
	{        
		form.setSelectedStudents(null);
		return new Forward("done");
	}




/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** ManageBulkAccommodationForm ************* /////////////////////////////    
/////////////////////////////////////////////////////////////////////////////////////////////    
	/**
	 * FormData get and set methods may be overwritten by the Form Bean editor.
	 */
	public static class ManageBulkAccommodationForm extends SanitizedFormData
	{
		static final long serialVersionUID = 1L;
		private String actionElement;
		private String currentAction;
		private String selectedOrgLevel;
		private Boolean filterVisible = Boolean.FALSE;
		private Integer selectedStudentId;

		private Integer[] selectedStudentOrgList;

		private PagerSummary studentPagerSummary ;
		private PagerSummary orgPagerSummary ;

		private List orgNodePath;
		private List orgNodes;
		private List studentNodes;
		private String studentNodesSize;
		private String msCount;
		private String orgCategoryName;
		private String disableApply;
		private boolean appliedFilterFlag = false;
		private CustomerConfiguration[] customerConfigurations;
		
		
		/**
		 * @return the selectedStudentOrgList
		 */
		public Integer[] getSelectedStudentOrgList() {
			return selectedStudentOrgList;
		}

		/**
		 * @return the studentNodes
		 */
		public List getStudentNodes() {
			return studentNodes;
		}

		/**
		 * @param studentNodes the studentNodes to set
		 */
		public void setStudentNodes(List studentNodes) {
			this.studentNodes = studentNodes;
		}

		/**
		 * @return the studentNodesSize
		 */
		public String getStudentNodesSize() {
			return studentNodesSize;
		}

		/**
		 * @param studentNodesSize the studentNodesSize to set
		 */
		public void setStudentNodesSize(String studentNodesSize) {
			this.studentNodesSize = studentNodesSize;
		}

		/**
		 * @return the msCount
		 */
		public String getMsCount() {
			return msCount;
		}

		/**
		 * @param msCount the msCount to set
		 */
		public void setMsCount(String msCount) {
			this.msCount = msCount;
		}


		// find all students
		private String orgNodeName;
		private Integer orgNodeId;
		private Integer selectedOrgNodeId;
		private String selectedOrgNodeName;

		// org pager
		private String orgSortColumn;
		private String orgSortOrderBy;
		private Integer orgPageRequested;
		private Integer orgMaxPage;

		// student pager
		private String studentSortColumn;
		private String studentSortOrderBy;
		private Integer studentPageRequested;
		private Integer studentMaxPage;

		private Message message;

		private String accommodationOperand;        
		private List gradeList = null;    
		private List selectedStudents;
		private String selectedOrganization;
		private String[] selectedAccommodations;
		private String selectedAccommodationElements;
		private String selectedGrade;
		private String selectedDemo1 = null;
		private String selectedDemo2 = null;
		private String selectedDemo3 = null;
		private String selectedDemoValue1 = null;
		private String selectedDemoValue2 = null;
		private String selectedDemoValue3 = null;

		private Integer selectedStudentWithAccommodationCount;
		private Integer selectedStudentCount;

		private Boolean showAccommodations;
		private Integer[] selectedStudentIds;

		private String creatorOrgNodeName;
		private String action="action_bulk_student";
		private Integer creatorOrgNodeId;

		public ManageBulkAccommodationForm()
		{
		}

		public void init()
		{
			this.actionElement = ACTION_DEFAULT;
			this.currentAction = ACTION_DEFAULT;

			this.filterVisible = Boolean.FALSE;

			this.selectedStudentId = null;

			this.orgNodeName = "Top";
			this.orgNodeId = new Integer(0);
			this.selectedOrgNodeId = null;
			this.selectedOrgNodeName = null;

			this.orgSortColumn = FilterSortPageUtils.ORGNODE_DEFAULT_SORT_COLUMN;
			this.orgSortOrderBy = FilterSortPageUtils.ASCENDING;      
			this.orgPageRequested = new Integer(1);                
			this.orgMaxPage = new Integer(1);      

			this.studentSortColumn = FilterSortPageUtils.STUDENT_DEFAULT_SORT_COLUMN_LAST_NAME;
			this.studentSortOrderBy = FilterSortPageUtils.ASCENDING;      
			this.studentPageRequested = new Integer(1);       
			this.studentMaxPage = new Integer(1);      


		}   

		public ActionErrors validate( ActionMapping mapping, HttpServletRequest request )
		{
			ActionErrors errs = super.validate(mapping, request);

			if (!errs.isEmpty()) {
				request.setAttribute("hasAlert", Boolean.TRUE);
			}
			return errs;
		}

		public void validateValues()
		{
			if (this.orgSortColumn == null)
				this.orgSortColumn = FilterSortPageUtils.ORGNODE_DEFAULT_SORT_COLUMN;

			if (this.orgSortOrderBy == null)
				this.orgSortOrderBy = FilterSortPageUtils.ASCENDING;

			if (this.orgPageRequested == null)
				this.orgPageRequested = new Integer(1);

			if (this.orgPageRequested.intValue() <= 0) {
				this.orgPageRequested = new Integer(1);
				this.selectedOrgNodeId = null;
			}

			if (this.orgMaxPage == null)
				this.orgMaxPage = new Integer(1);

			if (this.orgPageRequested.intValue() > this.orgMaxPage.intValue()) {
				this.orgPageRequested = new Integer(this.orgMaxPage.intValue());
				this.selectedOrgNodeId = null;
			}

			if (this.studentSortColumn == null)
				this.studentSortColumn = FilterSortPageUtils.STUDENT_DEFAULT_SORT_COLUMN_LAST_NAME;

			if (this.studentSortOrderBy == null)
				this.studentSortOrderBy = FilterSortPageUtils.ASCENDING;

			if (this.studentPageRequested == null) {
				this.studentPageRequested = new Integer(1);
			}

			if (this.studentPageRequested.intValue() <= 0)            
				this.studentPageRequested = new Integer(1);

			if (this.studentMaxPage == null)
				this.studentMaxPage = new Integer(1);

			if (this.studentMaxPage.intValue() <= 0)            
				this.studentMaxPage = new Integer(1);

			if (this.studentPageRequested.intValue() > this.studentMaxPage.intValue()) {
				this.studentPageRequested = new Integer(this.studentMaxPage.intValue());                
				this.selectedStudentId = null;
			}
		}     


		public void resetValuesForAction(String actionElement) 
		{
			if (actionElement.equals("{actionForm.orgSortOrderBy}")) {
				this.orgPageRequested = new Integer(1);
			}
			if (actionElement.equals("{actionForm.studentSortOrderBy}")) {
				this.studentPageRequested = new Integer(1);
			}
			if (actionElement.equals("ButtonGoInvoked_studentTableAnchor") ||
					actionElement.equals("EnterKeyInvoked__studentTableAnchor")) {
				this.selectedStudentId = null;
			}
			if (actionElement.equals("ButtonGoInvoked_tablePathListAnchor") ||
					actionElement.equals("EnterKeyInvoked_tablePathListAnchor")) {
				this.selectedOrgNodeId = null;

			}
		}


		public void resetValuesForPathList()
		{
			this.orgSortColumn = FilterSortPageUtils.ORGNODE_DEFAULT_SORT_COLUMN;
			this.orgSortOrderBy = FilterSortPageUtils.ASCENDING;      
			this.orgPageRequested = new Integer(1);    
			this.orgMaxPage = new Integer(1);      

			this.studentSortColumn = FilterSortPageUtils.STUDENT_DEFAULT_SORT_COLUMN_LAST_NAME;
			this.studentSortOrderBy = FilterSortPageUtils.ASCENDING;      
			this.studentPageRequested = new Integer(1);    
			this.studentMaxPage = new Integer(1);      
		}     

		public ManageBulkAccommodationForm createClone()
		{
			ManageBulkAccommodationForm copied = new ManageBulkAccommodationForm();

			copied.setActionElement(this.actionElement);
			copied.setCurrentAction(this.currentAction);



			copied.setSelectedOrgLevel(this.selectedOrgLevel);            
			copied.setSelectedStudentId(this.selectedStudentId);

			copied.setOrgNodeName(this.orgNodeName);
			copied.setOrgNodeId(this.orgNodeId);
			copied.setSelectedOrgNodeId(this.selectedOrgNodeId);
			copied.setSelectedOrgNodeName(this.selectedOrgNodeName);

			copied.setOrgSortColumn(this.orgSortColumn);
			copied.setOrgSortOrderBy(this.orgSortOrderBy);
			copied.setOrgPageRequested(this.orgPageRequested);
			copied.setOrgMaxPage(this.orgMaxPage);

			copied.setStudentSortColumn(this.studentSortColumn);
			copied.setStudentSortOrderBy(this.studentSortOrderBy);
			copied.setStudentPageRequested(this.studentPageRequested);      
			copied.setStudentMaxPage(this.studentMaxPage);

			return copied;                    
		} 


		public void setActionElement(String actionElement)
		{
			this.actionElement = actionElement;
		}        
		public String getActionElement()
		{
			return this.actionElement != null ? this.actionElement : ACTION_DEFAULT;
		}
		public void setCurrentAction(String currentAction)
		{
			this.currentAction = currentAction;
		}
		public String getCurrentAction()
		{
			return this.currentAction != null ? this.currentAction : ACTION_DEFAULT;
		}

		public void setSelectedStudentId(Integer selectedStudentId)
		{
			this.selectedStudentId = selectedStudentId;
		}
		public Integer getSelectedStudentId()
		{
			return this.selectedStudentId;
		}
		public void setOrgNodeName(String orgNodeName)
		{
			this.orgNodeName = orgNodeName;
		}
		public String getOrgNodeName()
		{
			return this.orgNodeName;
		}
		public void setOrgNodeId(Integer orgNodeId)
		{
			this.orgNodeId = orgNodeId;
		}
		public Integer getOrgNodeId()
		{
			return this.orgNodeId;
		}
		public void setSelectedOrgNodeId(Integer selectedOrgNodeId)
		{
			this.selectedOrgNodeId = selectedOrgNodeId;
		}
		public Integer getSelectedOrgNodeId()
		{
			return this.selectedOrgNodeId;
		}
		public void setSelectedOrgNodeName(String selectedOrgNodeName)
		{
			this.selectedOrgNodeName = selectedOrgNodeName;
		}
		public String getSelectedOrgNodeName()
		{
			return this.selectedOrgNodeName;
		}        


		public void setOrgSortColumn(String orgSortColumn)
		{
			this.orgSortColumn = orgSortColumn;
		}
		public String getOrgSortColumn()
		{
			return this.orgSortColumn != null ? this.orgSortColumn : FilterSortPageUtils.ORGNODE_DEFAULT_SORT_COLUMN;
		}       
		public void setOrgSortOrderBy(String orgSortOrderBy)
		{
			this.orgSortOrderBy = orgSortOrderBy;
		}
		public String getOrgSortOrderBy()
		{
			return this.orgSortOrderBy != null ? this.orgSortOrderBy : FilterSortPageUtils.ASCENDING;
		}       
		public void setOrgPageRequested(Integer orgPageRequested)
		{
			this.orgPageRequested = orgPageRequested;
		}
		public Integer getOrgPageRequested()
		{
			return this.orgPageRequested != null ? this.orgPageRequested : new Integer(1);
		}        
		public void setOrgMaxPage(Integer orgMaxPage)
		{
			this.orgMaxPage = orgMaxPage;
		}
		public Integer getOrgMaxPage()
		{
			return this.orgMaxPage != null ? this.orgMaxPage : new Integer(1);
		}        


		public void setStudentSortColumn(String studentSortColumn)
		{
			this.studentSortColumn = studentSortColumn;
		}
		public String getStudentSortColumn()
		{
			return this.studentSortColumn != null ? this.studentSortColumn : FilterSortPageUtils.STUDENT_DEFAULT_SORT_COLUMN_LAST_NAME;
		}       
		public void setStudentSortOrderBy(String studentSortOrderBy)
		{
			this.studentSortOrderBy = studentSortOrderBy;
		}
		public String getStudentSortOrderBy()
		{
			return this.studentSortOrderBy != null ? this.studentSortOrderBy : FilterSortPageUtils.ASCENDING;
		}       
		public void setStudentPageRequested(Integer studentPageRequested)
		{
			this.studentPageRequested = studentPageRequested;
		}
		public Integer getStudentPageRequested()
		{
			return this.studentPageRequested != null ? this.studentPageRequested : new Integer(1);
		}        
		public void setStudentMaxPage(Integer studentMaxPage)
		{
			this.studentMaxPage = studentMaxPage;
		}
		public Integer getStudentMaxPage()
		{
			return this.studentMaxPage != null ? this.studentMaxPage : new Integer(1);
		}        

		public String getStringAction()
		{
			String action = "addStudent";
			if ((this.selectedStudentId != null) && (this.selectedStudentId.intValue() > 0))
				action = "editStudent";
			return action; 
		}
		public Message getMessage()
		{
			return this.message != null ? this.message : new Message();
		}       
		public void setMessage(Message message)
		{
			this.message = message;
		}
		public void setMessage(String title, String content, String type)
		{
			this.message = new Message(title, content, type);
		}


		public void setSelectedOrgLevel(String selectedOrgLevel)
		{
			this.selectedOrgLevel = selectedOrgLevel;
		}
		public String getSelectedOrgLevel()
		{
			return this.selectedOrgLevel;
		}       

		/**
		 * @return the accommodationOperand
		 */
		public String getAccommodationOperand() {
			return accommodationOperand;
		}

		/**
		 * @param accommodationOperand the accommodationOperand to set
		 */
		public void setAccommodationOperand(String accommodationOperand) {
			this.accommodationOperand = accommodationOperand;
		}

		/**
		 * @return the gradeList
		 */
		public List getGradeList() {
			return gradeList;
		}

		/**
		 * @param gradeList the gradeList to set
		 */
		public void setGradeList(List gradeList) {
			this.gradeList = gradeList;
		}

		/**
		 * @return the selectedStudents
		 */
		public List getSelectedStudents() {
			return selectedStudents;
		}

		/**
		 * @param selectedStudents the selectedStudents to set
		 */
		public void setSelectedStudents(List selectedStudents) {
			this.selectedStudents = selectedStudents;
		}

		/**
		 * @return the selectedOrganization
		 */
		public String getSelectedOrganization() {
			return selectedOrganization;
		}

		/**
		 * @param selectedOrganization the selectedOrganization to set
		 */
		public void setSelectedOrganization(String selectedOrganization) {
			this.selectedOrganization = selectedOrganization;
		}

		/**
		 * @return the selectedAccommodations
		 */
		public String[] getSelectedAccommodations() {
			return selectedAccommodations;
		}

		/**
		 * @param selectedAccommodations the selectedAccommodations to set
		 */
		public void setSelectedAccommodations(String[] selectedAccommodations) {
			this.selectedAccommodations = selectedAccommodations;
		}

		/**
		 * @return the selectedAccommodationElements
		 */
		public String getSelectedAccommodationElements() {
			return selectedAccommodationElements;
		}

		/**
		 * @param selectedAccommodationElements the selectedAccommodationElements to set
		 */
		public void setSelectedAccommodationElements(
				String selectedAccommodationElements) {
			this.selectedAccommodationElements = selectedAccommodationElements;
		}

		/**
		 * @return the selectedGrade
		 */
		public String getSelectedGrade() {
			return selectedGrade;
		}

		/**
		 * @param selectedGrade the selectedGrade to set
		 */
		public void setSelectedGrade(String selectedGrade) {
			this.selectedGrade = selectedGrade;
		}

		/**
		 * @return the selectedStudentWithAccommodationCount
		 */
		public Integer getSelectedStudentWithAccommodationCount() {
			return selectedStudentWithAccommodationCount;
		}

		/**
		 * @param selectedStudentWithAccommodationCount the selectedStudentWithAccommodationCount to set
		 */
		public void setSelectedStudentWithAccommodationCount(
				Integer selectedStudentWithAccommodationCount) {
			this.selectedStudentWithAccommodationCount = selectedStudentWithAccommodationCount;
		}

		/**
		 * @return the selectedStudentCount
		 */
		public Integer getSelectedStudentCount() {
			return selectedStudentCount;
		}

		/**
		 * @param selectedStudentCount the selectedStudentCount to set
		 */
		public void setSelectedStudentCount(Integer selectedStudentCount) {
			this.selectedStudentCount = selectedStudentCount;
		}

		/**
		 * @return the showAccommodations
		 */
		public Boolean getShowAccommodations() {
			return showAccommodations;
		}

		/**
		 * @param showAccommodations the showAccommodations to set
		 */
		public void setShowAccommodations(Boolean showAccommodations) {
			this.showAccommodations = showAccommodations;
		}

		/**
		 * @return the selectedStudentIds
		 */
		public Integer[] getSelectedStudentIds() {
			return selectedStudentIds;
		}

		/**
		 * @param selectedStudentIds the selectedStudentIds to set
		 */
		public void setSelectedStudentIds(Integer[] selectedStudentIds) {
			this.selectedStudentIds = selectedStudentIds;
		}

		/**
		 * @return the filterVisible
		 */
		public Boolean getFilterVisible() {
			return filterVisible;
		}


		/**
		 * @param filterVisible the filterVisible to set
		 */
		public void setFilterVisible(Boolean filterVisible) {
			this.filterVisible = filterVisible;
		}


		/**
		 * @return the creatorOrgNodeName
		 */
		public String getCreatorOrgNodeName() {
			return creatorOrgNodeName;
		}

		/**
		 * @param creatorOrgNodeName the creatorOrgNodeName to set
		 */
		public void setCreatorOrgNodeName(String creatorOrgNodeName) {
			this.creatorOrgNodeName = creatorOrgNodeName;
		}

		/**
		 * @return the action
		 */
		public String getAction() {
			return action;
		}

		/**
		 * @param action the action to set
		 */
		public void setAction(String action) {
			this.action = action;
		}

		/**
		 * @return the creatorOrgNodeId
		 */
		public Integer getCreatorOrgNodeId() {
			return creatorOrgNodeId;
		}


		/**
		 * @param creatorOrgNodeId the creatorOrgNodeId to set
		 */
		public void setCreatorOrgNodeId(Integer creatorOrgNodeId) {
			this.creatorOrgNodeId = creatorOrgNodeId;
		}

		/**
		 * @return the selectedDemoValue1
		 */
		public String getSelectedDemoValue1() {
			return selectedDemoValue1;
		}

		/**
		 * @param selectedDemoValue1 the selectedDemoValue1 to set
		 */
		public void setSelectedDemoValue1(String selectedDemoValue1) {
			this.selectedDemoValue1 = selectedDemoValue1;
		}

		/**
		 * @return the selectedDemoValue2
		 */
		public String getSelectedDemoValue2() {
			return selectedDemoValue2;
		}

		/**
		 * @param selectedDemoValue2 the selectedDemoValue2 to set
		 */
		public void setSelectedDemoValue2(String selectedDemoValue2) {
			this.selectedDemoValue2 = selectedDemoValue2;
		}

		/**
		 * @return the selectedDemoValue3
		 */
		public String getSelectedDemoValue3() {
			return selectedDemoValue3;
		}

		/**
		 * @param selectedDemoValue3 the selectedDemoValue3 to set
		 */
		public void setSelectedDemoValue3(String selectedDemoValue3) {
			this.selectedDemoValue3 = selectedDemoValue3;
		}

		/**
		 * @return the selectedDemo1
		 */
		public String getSelectedDemo1() {
			return selectedDemo1;
		}

		/**
		 * @param selectedDemo1 the selectedDemo1 to set
		 */
		public void setSelectedDemo1(String selectedDemo1) {
			this.selectedDemo1 = selectedDemo1;
		}

		/**
		 * @return the selectedDemo2
		 */
		public String getSelectedDemo2() {
			return selectedDemo2;
		}

		/**
		 * @param selectedDemo2 the selectedDemo2 to set
		 */
		public void setSelectedDemo2(String selectedDemo2) {
			this.selectedDemo2 = selectedDemo2;
		}

		/**
		 * @return the selectedDemo3
		 */
		public String getSelectedDemo3() {
			return selectedDemo3;
		}

		/**
		 * @param selectedDemo3 the selectedDemo3 to set
		 */
		public void setSelectedDemo3(String selectedDemo3) {
			this.selectedDemo3 = selectedDemo3;
		}


		/**
		 * @return the studentPagerSummary
		 */
		public PagerSummary getStudentPagerSummary() {
			return studentPagerSummary;
		}

		/**
		 * @param studentPagerSummary the studentPagerSummary to set
		 */
		public void setStudentPagerSummary(PagerSummary studentPagerSummary) {
			this.studentPagerSummary = studentPagerSummary;
		}

		/**
		 * @return the orgPagerSummary
		 */
		public PagerSummary getOrgPagerSummary() {
			return orgPagerSummary;
		}

		/**
		 * @param orgPagerSummary the orgPagerSummary to set
		 */
		public void setOrgPagerSummary(PagerSummary orgPagerSummary) {
			this.orgPagerSummary = orgPagerSummary;
		}






		/**
		 * @return the orgNodePath
		 */
		public List getOrgNodePath() {
			return orgNodePath;
		}






		/**
		 * @param orgNodePath the orgNodePath to set
		 */
		public void setOrgNodePath(List orgNodePath) {
			this.orgNodePath = orgNodePath;
		}






		/**
		 * @return the orgNodes
		 */
		public List getOrgNodes() {
			return orgNodes;
		}






		/**
		 * @param orgNodes the orgNodes to set
		 */
		public void setOrgNodes(List orgNodes) {
			this.orgNodes = orgNodes;
		}

		/**
		 * @return the orgCategoryName
		 */
		public String getOrgCategoryName() {
			return orgCategoryName;
		}

		/**
		 * @param orgCategoryName the orgCategoryName to set
		 */
		public void setOrgCategoryName(String orgCategoryName) {
			this.orgCategoryName = orgCategoryName;
		}



		/**
		 * @return the disableApply
		 */
		public String getDisableApply() {
			return disableApply;
		}

		/**
		 * @param disableApply the disableApply to set
		 */
		public void setDisableApply(String disableApply) {
			this.disableApply = disableApply;
		}

		/**
		 * @param selectedStudentOrgList the selectedStudentOrgList to set
		 */
		public void setSelectedStudentOrgList(Integer[] selectedStudentOrgList) {
			this.selectedStudentOrgList = selectedStudentOrgList;
		}


		/**
		 * @param appliedFilterFlag the appliedFilterFlag to set
		 */
		public void setAppliedFilterFlag(boolean appliedFilterFlag) {
			this.appliedFilterFlag = appliedFilterFlag;
		}

		/**
		 * @return the appliedFilterFlag
		 */
		public boolean isAppliedFilterFlag() {
			return appliedFilterFlag;
		}

		/**
		 * @return the customerConfigurations
		 */
		public CustomerConfiguration[] getCustomerConfigurations() {
			return customerConfigurations;
		}

		/**
		 * @param customerConfigurations the customerConfigurations to set
		 */
		public void setCustomerConfigurations(
				CustomerConfiguration[] customerConfigurations) {
			this.customerConfigurations = customerConfigurations;
		}
	}

	//Added getter method for all pageFlow attribute in weblogic 10.3
	public String getPageTitle() {
		return pageTitle;
	}

	public String[] getGradeOptions() {
		return gradeOptions;
	}



	/**
	 * @return the realDemographicOptions
	 */
	public String[] getRealDemographicOptions() {
		return realDemographicOptions;
	}




	/**
	 * @param realDemographicOptions the realDemographicOptions to set
	 */
	public void setRealDemographicOptions(String[] realDemographicOptions) {
		this.realDemographicOptions = realDemographicOptions;
	}



	public String getPageMessage() {
		return pageMessage;
	}



	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}




	/**
	 * @return the selectedFormOperand
	 */
	public String getSelectedFormOperand() {
		return selectedFormOperand;
	}




	/**
	 * @param selectedFormOperand the selectedFormOperand to set
	 */
	public void setSelectedFormOperand(String selectedFormOperand) {
		this.selectedFormOperand = selectedFormOperand;
	}




	/**
	 * @return the selectedAccommodations
	 */
	public String[] getSelectedAccommodations() {
		return selectedAccommodations;
	}




	/**
	 * @param selectedAccommodations the selectedAccommodations to set
	 */
	public void setSelectedAccommodations(String[] selectedAccommodations) {
		this.selectedAccommodations = selectedAccommodations;
	}




	/**
	 * @return the selectedGrade
	 */
	public String getSelectedGrade() {
		return selectedGrade;
	}




	/**
	 * @param selectedGrade the selectedGrade to set
	 */
	public void setSelectedGrade(String selectedGrade) {
		this.selectedGrade = selectedGrade;
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




	/**
	 * @return the studentsHashtable
	 */
	public Hashtable getStudentsHashtable() {
		return studentsHashtable;
	}




	/**
	 * @param studentsHashtable the studentsHashtable to set
	 */
	public void setStudentsHashtable(Hashtable studentsHashtable) {
		this.studentsHashtable = studentsHashtable;
	}




	/**
	 * @return the accommodationOperandOptions
	 */
	public String[] getAccommodationOperandOptions() {
		return accommodationOperandOptions;
	}




	/**
	 * @param accommodationOperandOptions the accommodationOperandOptions to set
	 */
	public void setAccommodationOperandOptions(String[] accommodationOperandOptions) {
		this.accommodationOperandOptions = accommodationOperandOptions;
	}




	/**
	 * @return the selectedAccommodationsOptions
	 */
	public String[] getSelectedAccommodationsOptions() {
		return selectedAccommodationsOptions;
	}




	/**
	 * @param selectedAccommodationsOptions the selectedAccommodationsOptions to set
	 */
	public void setSelectedAccommodationsOptions(
			String[] selectedAccommodationsOptions) {
		this.selectedAccommodationsOptions = selectedAccommodationsOptions;
	}




	/**
	 * @return the demographic1
	 */
	public String[] getDemographic1() {
		return demographic1;
	}




	/**
	 * @param demographic1 the demographic1 to set
	 */
	public void setDemographic1(String[] demographic1) {
		this.demographic1 = demographic1;
	}




	/**
	 * @return the demographic2
	 */
	public String[] getDemographic2() {
		return demographic2;
	}




	/**
	 * @param demographic2 the demographic2 to set
	 */
	public void setDemographic2(String[] demographic2) {
		this.demographic2 = demographic2;
	}




	/**
	 * @return the demographic3
	 */
	public String[] getDemographic3() {
		return demographic3;
	}




	/**
	 * @param demographic3 the demographic3 to set
	 */
	public void setDemographic3(String[] demographic3) {
		this.demographic3 = demographic3;
	}




	/**
	 * @return the selectedDemoValue1
	 */
	public String getSelectedDemoValue1() {
		return selectedDemoValue1;
	}




	/**
	 * @param selectedDemoValue1 the selectedDemoValue1 to set
	 */
	public void setSelectedDemoValue1(String selectedDemoValue1) {
		this.selectedDemoValue1 = selectedDemoValue1;
	}




	/**
	 * @return the selectedDemoValue2
	 */
	public String getSelectedDemoValue2() {
		return selectedDemoValue2;
	}




	/**
	 * @param selectedDemoValue2 the selectedDemoValue2 to set
	 */
	public void setSelectedDemoValue2(String selectedDemoValue2) {
		this.selectedDemoValue2 = selectedDemoValue2;
	}




	/**
	 * @return the selectedDemoValue3
	 */
	public String getSelectedDemoValue3() {
		return selectedDemoValue3;
	}




	/**
	 * @param selectedDemoValue3 the selectedDemoValue3 to set
	 */
	public void setSelectedDemoValue3(String selectedDemoValue3) {
		this.selectedDemoValue3 = selectedDemoValue3;
	}




	/**
	 * @return the selectedDemo1
	 */
	public String getSelectedDemo1() {
		return selectedDemo1;
	}




	/**
	 * @param selectedDemo1 the selectedDemo1 to set
	 */
	public void setSelectedDemo1(String selectedDemo1) {
		this.selectedDemo1 = selectedDemo1;
	}




	/**
	 * @return the selectedDemo2
	 */
	public String getSelectedDemo2() {
		return selectedDemo2;
	}




	/**
	 * @param selectedDemo2 the selectedDemo2 to set
	 */
	public void setSelectedDemo2(String selectedDemo2) {
		this.selectedDemo2 = selectedDemo2;
	}




	/**
	 * @return the selectedDemo3
	 */
	public String getSelectedDemo3() {
		return selectedDemo3;
	}




	/**
	 * @param selectedDemo3 the selectedDemo3 to set
	 */
	public void setSelectedDemo3(String selectedDemo3) {
		this.selectedDemo3 = selectedDemo3;
	}




	/**
	 * @return the demographicValues1
	 */
	public String[] getDemographicValues1() {
		return demographicValues1;
	}




	/**
	 * @param demographicValues1 the demographicValues1 to set
	 */
	public void setDemographicValues1(String[] demographicValues1) {
		this.demographicValues1 = demographicValues1;
	}



	/**
	 * @return the demographicValues2
	 */
	public String[] getDemographicValues2() {
		return demographicValues2;
	}




	/**
	 * @param demographicValues2 the demographicValues2 to set
	 */
	public void setDemographicValues2(String[] demographicValues2) {
		this.demographicValues2 = demographicValues2;
	}




	/**
	 * @return the demographicValues3
	 */
	public String[] getDemographicValues3() {
		return demographicValues3;
	}




	/**
	 * @param demographicValues3 the demographicValues3 to set
	 */
	public void setDemographicValues3(String[] demographicValues3) {
		this.demographicValues3 = demographicValues3;
	}




	/**
	 * @return the islaslinkCustomer
	 */
	public boolean isIslaslinkCustomer() {
		return islaslinkCustomer;
	}




	/**
	 * @param islaslinkCustomer the islaslinkCustomer to set
	 */
	public void setIslaslinkCustomer(boolean islaslinkCustomer) {
		this.islaslinkCustomer = islaslinkCustomer;
	}
}

