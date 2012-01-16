package downloadOperation;

import java.awt.List;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import manageDownload.ManageDownloadController.ManageDownloadForm;

import org.apache.beehive.controls.api.bean.Control;
import org.apache.beehive.netui.pageflow.Forward;
import org.apache.beehive.netui.pageflow.PageFlowController;
import org.apache.beehive.netui.pageflow.annotations.Jpf;

import utils.Attribute;
import utils.Base;
import utils.MessageResourceBundle;
import utils.PermissionsUtils;
import utils.Row;
import utils.UploadDownload;
import utils.UploadDownloadFormUtils;

import com.ctb.bean.testAdmin.Customer;
import com.ctb.bean.testAdmin.CustomerConfiguration;
import com.ctb.bean.testAdmin.StudentFile;
import com.ctb.bean.testAdmin.StudentFileRow;
import com.ctb.bean.testAdmin.User;
import com.ctb.bean.testAdmin.UserFile;
import com.ctb.bean.testAdmin.UserFileRow;
import com.ctb.exception.CTBBusinessException;
import com.ctb.util.CTBConstants;
import com.ctb.util.web.sanitizer.SanitizedFormData;
import com.google.gson.Gson;

import dto.Message;

@Jpf.Controller()
public class DownloadOperationController extends PageFlowController {
	private static final long serialVersionUID = 1L;

    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.testAdmin.TestSessionStatus testSessionStatus;
	
    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.uploadDownloadManagement.UploadDownloadManagement downLoadManagement;

 // LLO- 118 - Change for Ematrix UI
	@org.apache.beehive.controls.api.bean.Control()
	private com.ctb.control.db.OrgNode orgnode;
    
    private String userName = null;
    private ManageDownloadForm savedForm = null;
    private User user = null;
	private Integer customerId = null;
    
    // LLO- 118 - Change for Ematrix UI
	private boolean isTopLevelUser = false;
	private boolean islaslinkCustomer = false;
  
    
    private static final String ACTION_DEFAULT = "defaultAction";

    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.userManagement.UserManagement userManagement;

    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.organizationManagement.OrganizationManagement organizationManagement;

    @Control()
    private com.ctb.control.db.Users users;
    


	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

    /**
     * @jpf:action
     * @jpf:forward name="success" path="manageDownload.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success", path = "manageDownload.do")
    })
    protected Forward begin()
    {    	    	
		getLoggedInUserPrincipal();
		
		getUserDetails();

		setupUserPermission();

        isTopLevelUser();
        
        this.savedForm = initialize();
		
   		return new Forward("success");
    }
	
    /**
     * @jpf:action
     * @jpf:forward name="success" path="manage_download.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",  path = "downloadData.jsp")
    })
    protected Forward manageDownload(ManageDownloadForm form)
    {  
    	isTopLevelUser(); //LLO- 118 - Change for Ematrix UI
        return new Forward("success", form);
    }
	
    /**
     * @jpf:action
     * @jpf:forward name="error" path="manage_download.jsp"
     */
    @Jpf.Action(
		forwards = { 
	        @Jpf.Forward(name = "success",  path = "downloadData.jsp"),
			@Jpf.Forward(name = "error", path = "downloadData.jsp")
		}
	)
    protected Forward downloadData(ManageDownloadForm form)
    {         
        form.clearMessage();
        
        byte []data = null;
        String fileContent = "";
        String fileName = (String)this.getRequest().getParameter("downloadFile");        
        System.out.println(fileName);
        
        if (true)
        	return new Forward("success",form);
        
        String userFileName = userName + "_User.xls"; 
        
        try {
            if ( fileName.equals(userFileName) ){
                
                UserFile userFile = downLoadManagement.getUserFile(this.userName);
                UserFileRow []userFileRow = userFile.getUserFileRows();
                
                if (userFileRow.length > CTBConstants.MAX_EXCEL_SIZE) {
                   
                    form.setMessage(Message.DOWNLOAD_TITLE, Message.DOWNLOAD_ERROR_MSG, Message.ERROR);
                    setFormInfoOnRequest(form);
                    return new Forward("error",form);
                    
                }
                
                //fileContent = UploadDownloadFormUtils.downLoadUserData(userFile);
                data = UploadDownloadFormUtils.downLoadUserDataFile
                        (userFile, this.userName, userManagement);
                
            } else {
                
                StudentFile studentFile = downLoadManagement.getStudentFile(this.userName);
                StudentFileRow []studentFileRow = studentFile.getStudentFileRows();
                
                if (studentFileRow.length > CTBConstants.MAX_EXCEL_SIZE) {
                    
                    form.setMessage(Message.DOWNLOAD_TITLE, Message.DOWNLOAD_ERROR_MSG, Message.ERROR);
                    setFormInfoOnRequest(form);
                    return new Forward("error",form);
                    
                }
                //fileContent = UploadDownloadFormUtils.downLoadStudentData(studentFile);
                
                data = UploadDownloadFormUtils.downLoadStudentDataFile
                        (studentFile, this.userName, userManagement);
                
            }
            
        setFormInfoOnRequest(form);
            
        HttpServletResponse resp = this.getResponse();        
        String bodypart = "attachment; filename=\"" + fileName + "\" ";

        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", bodypart);
        /*resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");*/
        
        resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        resp.setHeader("Cache-Control", "cache");
        resp.setHeader("Pragma", "public");
        resp.flushBuffer();
            /*PrintWriter pw = resp.getWriter();
            pw.write(fileContent);
            pw.close();*/
        OutputStream stream = resp.getOutputStream();
        stream.write(data);
        stream.close();
            
        } catch(Exception e) {
            e.printStackTrace();
            String msg = MessageResourceBundle.getMessage(e.getMessage());
            form.setMessage(Message.DOWNLOAD_TITLE, msg, Message.ERROR);
            //setFormInfoOnRequest(form);
        }
        
        //return null;
        return new Forward("success",form);
    }
    
    private void setFormInfoOnRequest(ManageDownloadForm form) {
    	this.getRequest().setAttribute("pageMessage", form.getMessage());
    }
    
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
    
    private ManageDownloadForm initialize()
    {                
        ManageDownloadForm form = new ManageDownloadForm();
        form.init();
        return form;
    }
    
	@Jpf.Action()
    protected Forward populateDownloadListGrid()
    {
        HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		
		Row row1 = new Row(1);		
		String[] atts1 = new String[2];
		atts1[0] = "User Data";
		atts1[1] = "Users in your organization located at or below your organizational assignment.";
		row1.setCell(atts1);

		Row row2 = new Row(2);		
		String[] atts2 = new String[2];
		atts2[0] = "Student Data";
		atts2[1] = "Students in your organization associated with your user login.";
		row2.setCell(atts2);
		
		ArrayList rows = new ArrayList();
		rows.add(row1);
		rows.add(row2);
		
		UploadDownload base = new UploadDownload();
		//base.setPage("1");
		//base.setRecords("2");
		//base.setTotal("2");
		base.setRows(rows);
		
        Gson gson = new Gson();
        String jsonData = gson.toJson(base);
		
        System.out.println(jsonData);
        
		try{
	       	try {
	   			resp.setContentType("application/json");
 	   			stream = resp.getOutputStream();
	   			stream.write(jsonData.getBytes("UTF-8"));
	   			resp.flushBuffer();
	   		} catch (IOException e) {
	   			e.printStackTrace();
	   		} 
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
        
        return null;
    }
    
    /**
     * FormData get and set methods may be overwritten by the Form Bean editor.
     */
    public static class ManageDownloadForm extends SanitizedFormData
    {
        private String userName;

        private String actionElement;
        private String currentAction;
        private String fileName;
        private Message message;
        

        public ManageDownloadForm()
        {
        }
        
        public void init()
        {
            this.actionElement = ACTION_DEFAULT;
            this.currentAction = ACTION_DEFAULT;
            this.message = new Message(); 
            this.fileName = "";
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
        public void setFileName(String fileName)
        {
            this.fileName = fileName;
        }
        public String getFileName()
        {
            return this.fileName;
        }
        
          // clear message
        public void clearMessage() {   
            this.message = null;
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
        
    }
    
	/////////////////////////////////////////////////////////////////////////////////////////////    
	///////////////////////////// BEGIN OF NEW NAVIGATION ACTIONS ///////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * ASSESSMENTS actions
     */    
    @Jpf.Action(forwards = { 
    		@Jpf.Forward(name = "sessionsLink", path = "assessments_sessionsLink.do"),
    		@Jpf.Forward(name = "studentScoringLink", path = "assessments_studentScoringLink.do"),
    		@Jpf.Forward(name = "programStatusLink", path = "assessments_programStatusLink.do")
    })   
    protected Forward assessments()
    {

    	String menuId = (String)this.getRequest().getParameter("menuId");    	
    	String forwardName = (menuId != null) ? menuId : "sessionsLink";

    	return new Forward(forwardName);	    
    }


    @Jpf.Action()
    protected Forward assessments_sessionsLink()
    {
    	try
    	{
    		String url = "/SessionWeb/sessionOperation/assessments_sessions.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action()
    protected Forward assessments_studentScoringLink()
    {
    	try
    	{
    		String url = "/SessionWeb/sessionOperation/assessments_studentScoring.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action()
    protected Forward assessments_programStatusLink()
    {
    	try
    	{
    		String url = "/SessionWeb/programOperation/assessments_programStatus.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }


    /**
     * ORGANIZATIONS actions
     */    
    @Jpf.Action(forwards = { 
    		@Jpf.Forward(name = "studentsLink", path = "organizations_manageStudents.do"),
    		@Jpf.Forward(name = "usersLink", path = "organizations_manageUsers.do"),
    		@Jpf.Forward(name = "organizationsLink", path = "organizations_manageOrganizations.do"),
    		@Jpf.Forward(name = "bulkAccomLink", path = "organizations_manageBulkAccommodation.do"),
	        @Jpf.Forward(name = "bulkMoveLink", path = "organizations_manageBulkMove.do")
    }) 
    protected Forward organizations()
    {
    	String menuId = (String)this.getRequest().getParameter("menuId");    	
    	String forwardName = (menuId != null) ? menuId : "studentsLink";

    	return new Forward(forwardName);
    }


    @Jpf.Action()
    protected Forward organizations_manageOrganizations()
    {
    	try
    	{
    		String url = "/OrganizationWeb/orgOperation/organizations_manageOrganizations.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action()
    protected Forward organizations_manageStudents()
    {
    	try
    	{
    		String url = "/StudentWeb/studentOperation/organizations_manageStudents.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action()
    protected Forward organizations_manageBulkAccommodation()
    {
    	try
    	{
    		String url = "/StudentWeb/bulkOperation/organizations_manageBulkAccommodation.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action() 
    protected Forward organizations_manageUsers()
    {
    	try
    	{
    		String url = "/UserWeb/userOperation/organizations_manageUsers.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;

    }    

    @Jpf.Action()
	protected Forward organizations_manageBulkMove()
	{
        try
        {
            String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do";
            getResponse().sendRedirect(url);
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
    
    /**
     * REPORTS actions
     */    
    @Jpf.Action()
    protected Forward reports()
    {
    	try
    	{
    		String url = "/SessionWeb/sessionOperation/reports.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }


    /**
     * SERVICES actions
     */    
    @Jpf.Action(forwards = { 
    		@Jpf.Forward(name = "manageLicensesLink", path = "services_manageLicenses.do"),
    		@Jpf.Forward(name = "installSoftwareLink", path = "services_installSoftware.do"),
    		@Jpf.Forward(name = "downloadTestLink", path = "services_downloadTest.do"),
    		@Jpf.Forward(name = "uploadDataLink", path = "services_uploadData.do"),
    		@Jpf.Forward(name = "downloadDataLink", path = "services_downloadData.do")
    }) 
    protected Forward services()
    {
    	String menuId = (String)this.getRequest().getParameter("menuId");    	
    	String forwardName = (menuId != null) ? menuId : "installSoftwareLink";

    	return new Forward(forwardName);
    }

    @Jpf.Action()
    protected Forward services_manageLicenses()
    {
    	try
    	{
    		String url = "/OrganizationWeb/licenseOperation/services_manageLicenses.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }



    @Jpf.Action()
    protected Forward services_installSoftware()
    {
    	try
    	{
    		String url = "/SessionWeb/softwareOperation/services_installSoftware.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action()
    protected Forward services_downloadTest()
    {
    	try
    	{
    		String url = "/SessionWeb/testContentOperation/services_downloadTest.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action()
    protected Forward services_uploadData()
    {
    	try
    	{
    		String url = "/OrganizationWeb/uploadOperation/services_uploadData.do";
    		getResponse().sendRedirect(url);
    	} 
    	catch (IOException ioe)
    	{
    		System.err.print(ioe.getStackTrace());
    	}
    	return null;
    }

    @Jpf.Action(forwards = { 
    		@Jpf.Forward(name = "success", path = "begin.do") 
    }) 
    protected Forward services_downloadData()
    {
    	return new Forward("success");
    }

	/**
	 * @jpf:action
	 */
	@Jpf.Action()
	protected Forward broadcastMessage()
	{
	    return null;
	}
	
	
	@Jpf.Action()
	protected Forward myProfile()
	{
	    return null;
	}


    /////////////////////////////////////////////////////////////////////////////////////////////    
    ///////////////////////////// SETUP USER PERMISSION ///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    private void getLoggedInUserPrincipal()
    {
        java.security.Principal principal = getRequest().getUserPrincipal();
        if (principal != null) {
            this.userName = principal.toString();
        }        
        getSession().setAttribute("userName", this.userName);
    }
    
    private void getUserDetails()
    {
        Boolean supportAccommodations = Boolean.TRUE;
        String userTimeZone = "GMT";
             	
        try
        {
            this.user = this.testSessionStatus.getUserDetails(this.userName, this.userName);
            Customer customer = this.user.getCustomer();
            this.customerId = customer.getCustomerId();
            getSession().setAttribute("customerId", customerId); 
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
    }
    
	private void setupUserPermission()
	{
        CustomerConfiguration [] customerConfigs = getCustomerConfigurations(this.customerId);
        boolean adminUser = isAdminUser();
        boolean TABECustomer = isTABECustomer(customerConfigs);
        boolean laslinkCustomer = isLaslinkCustomer(customerConfigs);
        
        this.getSession().setAttribute("showReportTab", 
        		new Boolean(userHasReports().booleanValue() || laslinkCustomer));

        this.getSession().setAttribute("hasUploadDownloadConfigured", 
        		new Boolean( hasUploadDownloadConfig().booleanValue() && adminUser));
        
        this.getSession().setAttribute("hasProgramStatusConfigured", 
        		new Boolean( hasProgramStatusConfig().booleanValue() && adminUser));
        
        this.getSession().setAttribute("hasScoringConfigured", 
        		new Boolean( customerHasScoring(customerConfigs).booleanValue() && adminUser));
        
        this.getSession().setAttribute("canRegisterStudent", canRegisterStudent(customerConfigs));
        
     	this.getSession().setAttribute("hasLicenseConfigured", hasLicenseConfiguration(customerConfigs).booleanValue() && adminUser);
     	
     	this.getSession().setAttribute("isBulkMoveConfigured",customerHasBulkMove(customerConfigs));
    	
     	this.getSession().setAttribute("isBulkAccommodationConfigured",customerHasBulkAccommodation(customerConfigs));    	
    	
        this.getSession().setAttribute("adminUser", new Boolean(adminUser));     	
	}

    private Boolean userHasReports() 
    {
        boolean hasReports = false;
        try
        {      
            Customer customer = this.user.getCustomer();
            Integer customerId = customer.getCustomerId();   
            hasReports = this.testSessionStatus.userHasReports(this.userName, customerId);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        
        return new Boolean(hasReports);           
    }

    private boolean isAdminUser() 
    {               
        String roleName = this.user.getRole().getRoleName();        
        return roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ADMINISTRATOR); 
    }
    
    private Boolean canRegisterStudent(CustomerConfiguration [] customerConfigs) 
    {               
        String roleName = this.user.getRole().getRoleName();        
        boolean validCustomer = false; 

        for (int i=0; i < customerConfigs.length; i++)
        {
            CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TABE_Customer"))
            {
                validCustomer = true; 
            }               
        }
        
        boolean validUser = (roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ADMINISTRATOR) || 
        		roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ACCOMMODATIONS_COORDINATOR));
        
        return new Boolean(validCustomer && validUser);
    }
    
    private Boolean hasLicenseConfiguration(CustomerConfiguration [] customerConfigs)
    {               
    	 boolean hasLicenseConfiguration = false;

        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Subscription") && 
            		cc.getDefaultValue().equals("T")	) {
            	hasLicenseConfiguration = true;
                break;
            } 
        }
       
        return new Boolean(hasLicenseConfiguration);
    }
    
    private Boolean customerHasScoring(CustomerConfiguration [] customerConfigs)
    {               
        Integer customerId = this.user.getCustomer().getCustomerId();
        boolean hasScoringConfigurable = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Hand_Scoring") && 
            		cc.getDefaultValue().equals("T")	) {
            	hasScoringConfigurable = true;
            } 
        }
        return new Boolean(hasScoringConfigurable);
    }

    private boolean isLaslinkCustomer(CustomerConfiguration [] customerConfigs)
    {               
        boolean laslinkCustomer = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Laslink_Customer")
					&& cc.getDefaultValue().equals("T")) {
            	laslinkCustomer = true;
            }
        }
        return laslinkCustomer;
    }

    private boolean isTABECustomer(CustomerConfiguration [] customerConfigs)
    {               
        boolean TABECustomer = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TABE_Customer")) {
            	TABECustomer = true;
            }
        }
        return TABECustomer;
    }
    
    private CustomerConfiguration [] getCustomerConfigurations(Integer customerId)
    {               
        CustomerConfiguration [] ccArray = null;
        try
        {      
            ccArray = this.testSessionStatus.getCustomerConfigurations(this.userName, customerId);       
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        
        return ccArray;
    }
    
    private Boolean hasUploadDownloadConfig()
    {
        Boolean hasUploadDownloadConfig = Boolean.FALSE;
        try {   
            hasUploadDownloadConfig = this.testSessionStatus.hasUploadDownloadConfig(this.userName);
        } 
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }
        return hasUploadDownloadConfig;
    }

    private Boolean hasProgramStatusConfig()
    {	
        Boolean hasProgramStatusConfig = Boolean.FALSE;
        try {   
            hasProgramStatusConfig = this.testSessionStatus.hasProgramStatusConfig(this.userName);
        } 
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }
        return hasProgramStatusConfig;
    }
    
    /**
	 * Bulk Accommodation
	 */
	private Boolean customerHasBulkAccommodation(CustomerConfiguration[] customerConfigurations) 
	{
		boolean hasBulkStudentConfigurable = false;
		if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Bulk_Accommodation") && 
						cc.getDefaultValue().equals("T")) {
					hasBulkStudentConfigurable = true; 
					break;
				}
			}
		}
		return new Boolean(hasBulkStudentConfigurable);           
	}
	
	/**
	 * Bulk Move
	 */
	private Boolean customerHasBulkMove(CustomerConfiguration[] customerConfigurations) 
	{
		boolean hasBulkStudentConfigurable = false;
		if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Bulk_Move_Students") && 
						cc.getDefaultValue().equals("T")) {
					hasBulkStudentConfigurable = true; 
					break;
				}
			}
		}
		return new Boolean(hasBulkStudentConfigurable);           
	}

    /////////////////////////////////////////////////////////////////////////////////////////////    
    ///////////////////////////// END OF SETUP USER PERMISSION ///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
	
}