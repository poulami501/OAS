package manageCustomer;
import org.apache.beehive.netui.pageflow.Forward;
import org.apache.beehive.netui.pageflow.PageFlowController;
import com.ctb.bean.request.FilterParams;
import com.ctb.bean.request.PageParams;
import com.ctb.bean.request.SortParams;
import com.ctb.bean.testAdmin.Customer;
import com.ctb.bean.testAdmin.CustomerData;
import com.ctb.bean.testAdmin.CustomerLicense;
import com.ctb.bean.testAdmin.Node;
import com.ctb.bean.testAdmin.OrgNodeCategory;
import com.ctb.bean.testAdmin.USState;
import com.ctb.exception.CTBBusinessException;
import com.ctb.util.web.sanitizer.SanitizedFormData;
import com.ctb.widgets.bean.PagerSummary;
import dto.CustomerProfileInformation;
//added for LM12
import dto.LicenseNode;
import dto.Level;
import dto.Message;
import dto.NavigationPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.beehive.controls.api.bean.Control;
import org.apache.beehive.netui.pageflow.annotations.Jpf;
import utils.CustomerFormUtils;
import utils.CustomerSearchUtils;
import utils.FilterSortPageUtils;
import utils.MessageResourceBundle;
import utils.PermissionsUtils;
import com.ctb.util.CTBConstants;
import global.Global;
import java.io.IOException;
import utils.LicenseFormUtils;

/**
 * @jpf:controller
 *  */
@Jpf.Controller()
public class ManageCustomerController extends PageFlowController
{
    protected global.Global globalApp;
    
    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.customerManagement.CustomerManagement customerManagement;
    
    /**
     * @common:control
     */
    @Control()
    private com.ctb.control.licensing.Licensing license;

    static final long serialVersionUID = 1L;
    
        
    private String userName = null;
    
    
    public String pageTitle = null;

   
    private ArrayList frameWork = null;        
    private boolean createNewFramework = false;
    private boolean createAdministrator = false;
    
    
    //Temporary list to store searched/added customerProfiles
    public List customerList = new ArrayList(0);        
    
    private ManageCustomerForm savedForm = null;
    
    private static final String ACTION_FORM_ELEMENT   = "{actionForm.actionElement}";
    private static final String ACTION_CURRENT_ELEMENT   = "{actionForm.currentAction}";
    private static final String INVALID_REQUEST   = "invalidRequest";
    
     // navigation
    public boolean clearCurrentMessage = true;
    private boolean searchApplied = false;
    private boolean viewCustomerFromSearch = false;
    private CustomerProfileInformation customerSearch = null;
    public Integer currentCustomerPageRequested = null;
    public LinkedHashMap stateOptions = null;
    public LinkedHashMap mailingStateOptions = null;
    public LinkedHashMap billingStateOptions = null;
    public LinkedHashMap customerOptions = null;
    public String pageMessage = null;
    private boolean isEmptyProfileSearch = false;
    public Integer customerMaxPage = null;
    public String webTitle = null;
    public Boolean frommanageOrg = Boolean.FALSE;
    
    
    // help
    public String helpLink = null;
    
    /**
     * This method represents the point of entry into the pageflow - beginFindCustomer.do
     * @jpf:action
     * @jpf:forward name="success" path="defaultAction.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "defaultAction.do")
    })
    protected Forward begin()
    {
        return new Forward("success");
    }
   
    /**
     * @jpf:action
     * @jpf:forward name="success" path = "beginFindCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "beginFindCustomer.do")
    })
    protected Forward defaultAction(ManageCustomerForm form)
    {   
        initialize(globalApp.ACTION_FIND_CUSTOMER);
        return new Forward("success");
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="findCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "findCustomer.do")
    })
    protected Forward beginFindCustomer()
    {
        ManageCustomerForm form = initialize(globalApp.ACTION_FIND_CUSTOMER);
        form.setSelectedCustomerName(null); 
        form.setSelectedCustomerId(null);
        clearMessage(form);
        this.searchApplied = false;
        initStateOptions(globalApp.ACTION_FIND_CUSTOMER);
        resetCustomerList();
        this.globalApp.navPath.reset(globalApp.ACTION_FIND_CUSTOMER);
        
        if (INVALID_REQUEST.equals(this.pageMessage))
        {
            form.setMessage(Message.FIND_CUSTOMER_ERROR, Message.UNKNOWN_REQUEST, Message.ERROR);
        }
        
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="find_customer.jsp"
     * @jpf:forward name="error" path="find_customer.jsp"
     * @jpf:forward name="gotoNextAction" path="gotoNextAction.do" redirect="true" 
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "find_customer.jsp"), 
        @Jpf.Forward(name = "error",
                     path = "find_customer.jsp"), 
        @Jpf.Forward(name = "gotoNextAction", 
                     path = "gotoNextAction.do", 
                     redirect = true)
    })
    protected Forward findCustomer(ManageCustomerForm form)
    {   
        /*if(this.savedForm != null) {
           returnToFindCustomer(form);
        }*/
        form.validateValues();
        this.createNewFramework = false;
        this.frameWork = null;
        
        String currentAction = form.getCurrentAction();
        String actionElement = form.getActionElement();
        
        form.resetValuesForAction(actionElement, globalApp.ACTION_FIND_CUSTOMER);
        
        if (currentAction.equals(globalApp.ACTION_VIEW_CUSTOMER) || currentAction.equals(globalApp.ACTION_EDIT_CUSTOMER))
        {
                    
            CustomerProfileInformation customerProfile = 
                 CustomerSearchUtils.getCustomerNameFromList(form.getSelectedCustomerId(), this.customerList);
                 
            form.setSelectedCustomerName(customerProfile.getName());
            form.setSelectedCustomerId(Integer.valueOf(customerProfile.getId()));
            this.viewCustomerFromSearch = true;
            this.savedForm = form.createClone();
            this.customerSearch = form.getCustomerProfile().createClone();
            return new Forward("gotoNextAction");
        }
        
        if (currentAction.equals(globalApp.ACTION_APPLY_SEARCH))
        {
        
            this.customerSearch = form.getCustomerProfile().createClone();
        
        }
        
        initPagingSorting(form);
        boolean applySerach = initSearch(form);
        CustomerData customerData = null;
                
        if (this.createAdministrator)
        {
            
            form.setCustomerProfile(this.savedForm.getCustomerProfile());
            form.setSelectedCustomerId(this.savedForm.getSelectedCustomerId());
            form.setCustomerPageRequested(this.savedForm.getCustomerPageRequested());
            form.setCustomerSortColumn(this.savedForm.getCustomerSortColumn());
            form.setCustomerSortOrderBy(this.savedForm.getCustomerSortOrderBy());
            form.setCustomerMaxPage(this.savedForm.getCustomerMaxPage());
            form.setMessage(this.savedForm.getMessage());
            this.createAdministrator = false;
         
        }   
        
        if (applySerach)
        {
            customerData = findCustomerProfile(form);
                
            if (customerData != null && (customerData.getFilteredCount().intValue() == 0))
            {
                
                this.getRequest().setAttribute("searchResultEmpty",
                                                Message.FIND_NO_RESULT);
                isEmptyProfileSearch = true; 
                   
            }
        }
        
        this.searchApplied = false;
        
        if (isEmptyProfileSearch)
        {
       
            this.searchApplied = true;
            isEmptyProfileSearch = false;
        
        }
       
        if (customerData != null && (customerData.getFilteredCount().intValue() > 0))
        {
        
            this.searchApplied = true;
            this.customerList = CustomerSearchUtils.buildCustomerList(customerData);
            PagerSummary customerPagerSummary = 
                    CustomerSearchUtils.buildUserPagerSummary(customerData, form.getCustomerPageRequested()); 
            form.setCustomerMaxPage(customerData.getFilteredPages());
            customerMaxPage = customerData.getFilteredPages();   
            this.getRequest().setAttribute("customerResult", "true");        
            this.getRequest().setAttribute("customerPagerSummary", customerPagerSummary);
            
            if (form.getSelectedCustomerId() != null)
            {
                
                //Added for LM12
                if (form.getEnableLicense() == null && this.savedForm.
                        getEnableLicense() != null)
                {
                    
                    form.setEnableLicense(this.savedForm.
                        getEnableLicense());
                    
                }
                
                if ("T".equals(form.getEnableLicense()))
                {
                    
                    PermissionsUtils.setLicensePermission(this.getRequest(), Boolean.FALSE);
                }
                else
                {
                    
                    PermissionsUtils.setLicensePermission(this.getRequest(), Boolean.TRUE);
                    
                }
               
                PermissionsUtils.setPermissionRequestAttribute(this.getRequest(), Boolean.FALSE);
            
            }
            else
            {
            
                PermissionsUtils.setPermissionRequestAttribute(this.getRequest(), Boolean.TRUE);
                
                //Added for LM12
                PermissionsUtils.setLicensePermission(this.getRequest(), Boolean.TRUE);
            
            }                   
        }
        
        form.setCurrentAction(globalApp.ACTION_DEFAULT);
        this.getRequest().setAttribute("isFindCustomer", Boolean.TRUE);
        this.pageTitle = buildPageTitle(globalApp.ACTION_FIND_CUSTOMER, form);
        
        if (this.globalApp.navPath != null)
        {
            
            this.globalApp.navPath.addCurrentAction(globalApp.ACTION_FIND_CUSTOMER);    
       
        }
        else
        {
        //    this.navPath = new NavigationPath();
       //     this.navPath.addCurrentAction(ACTION_FIND_CUSTOMER);
            
           // form.setMessage(Message.FIND_CUSTOMER_ERROR, Message.UNKNOWN_REQUEST, Message.ERROR);
          /* String msg = MessageResourceBundle.getMessage(
                                        "CustomerManagement.Failed");                                        
            form.setMessage(Message.FIND_CUSTOMER_ERROR, msg,
                                Message.ERROR);*/
            form.clearMessage(); 
            return new Forward("error");
                
        }
        setFormInfoOnRequest(form);
        return new Forward("success");
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="addCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "addCustomer.do")
    })
    protected Forward beginAddCustomer()
    {
        ManageCustomerForm form = initializeAddCustomer();
        clearMessage(form);
        this.helpLink = "/help/index.html#adding_new_customers.htm";
        this.createNewFramework = false;
        this.frameWork = null;
        
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="add_customer.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "add_customer.jsp")
    })
    protected Forward addCustomer(ManageCustomerForm form)
    {                   
        if (this.createNewFramework)
        {
            //for storing framework info if canele from add framework and again next
            String names = form.getActionElement();
                
            if (!(names.equals(globalApp.ACTION_DEFAULT)) && !(names.equals(globalApp.ACTION_ADD_FRAMEWORK)) && !("".equals(names)))
            { // check if user has deleted the default framework name
                    
                String separator = "#";
                StringTokenizer st = new StringTokenizer(names, separator);
                OrgNodeCategory[] orgNodeCategories = null;
                this.frameWork = new ArrayList(); 
                Integer count = new Integer(1);                 
                while (st.hasMoreTokens())
                {
                    
                    String token = st.nextToken();
                    Level level = buildLevel(token);
                    level.setOrder(count);
                    if (level.getOrder().intValue() == 1)
                    {
            
                        level.setDeletable(Boolean.FALSE); 
                        level.setBeforeInsertable(Boolean.FALSE); 
                    }
            
                    this.frameWork.add(level);
                    count = new Integer(count.intValue() + 1);
                }
            }
            //for stroing customer profile info if cancel press in create framework  
            form = this.savedForm.createClone(); 
            clearMessage(form);
        }
        
        handleAddEdit(form);
        this.pageTitle = buildPageTitle(globalApp.ACTION_ADD_CUSTOMER, form);
        this.getRequest().setAttribute("isAddCustomer", Boolean.TRUE);  
        setFormInfoOnRequest(form);
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="editCustomer.do"
     * @jpf:forward name="error" path="beginFindCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "editCustomer.do"), 
        @Jpf.Forward(name = "error",
                     path = "beginFindCustomer.do")
    })
    protected Forward beginEditCustomer(ManageCustomerForm form)
    { 
        CustomerProfileInformation customerProfile = null;
    
   
        customerProfile = setCustomerProfileToForm(form, Message.EDIT_CUSTOMER_TITLE);
        if (customerProfile.getName() != null && customerProfile.getName().length() > 0)
        {
    
            this.getRequest().getSession().setAttribute("CustomerName", 
                                                            customerProfile.getName()); 
        }
            
        
        
        initCustomerTypeOptions(globalApp.ACTION_EDIT_CUSTOMER);
        initStateOptions(globalApp.ACTION_EDIT_CUSTOMER);
        clearMessage(form);
        form.clearSectionVisibility();
        form.setByCustomerProfileVisible(Boolean.TRUE);
     /*   if ( this.navPath  != null ) {
            
            this.navPath.addCurrentAction(ACTION_EDIT_CUSTOMER);
            
        } else {
            
            this.pageMessage = INVALID_REQUEST;
            return new Forward("error");
            
        }*/
        
        this.globalApp.navPath.addCurrentAction(globalApp.ACTION_EDIT_CUSTOMER);        
        
        this.helpLink = "/help/index.html#editing_or_deleting_users.htm";

        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="edit_customer.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "edit_customer.jsp")
    })
    protected Forward editCustomer(ManageCustomerForm form)
    {
        this.savedForm.setSelectedCustomerId(form.getSelectedCustomerId());
        handleAddEdit(form);
        this.getRequest().setAttribute("isEditCustomer", Boolean.TRUE);
        this.pageTitle = buildPageTitle(globalApp.ACTION_EDIT_CUSTOMER, form);
        form.setCurrentAction(globalApp.ACTION_EDIT_CUSTOMER);
        form.setActionElement(globalApp.ACTION_EDIT_CUSTOMER);
        form.setSelectedCustomerId(this.savedForm.getSelectedCustomerId());
        setFormInfoOnRequest(form);
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="createFramework.do"
     * @jpf:forward name="error" path="addCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "createFramework.do"), 
        @Jpf.Forward(name = "error",
                     path = "addCustomer.do")
    })
    protected Forward beginCreateFramework(ManageCustomerForm form)
    {   
        Integer selectedCustomerId = form.getSelectedCustomerId();   
        boolean validInfo = CustomerFormUtils.verifyCustomerInformation(form, selectedCustomerId);	
        if (!validInfo)
        {

            form.setActionElement(globalApp.ACTION_DEFAULT);
            form.setCurrentAction(globalApp.ACTION_DEFAULT); 
            this.createNewFramework = false;
            return new Forward("error", form);
           
        }
         
        this.savedForm = form.createClone();
        this.createNewFramework = true;
        
        if (this.frameWork == null || this.frameWork.size() == 0)
        {
        
            this.frameWork = (ArrayList)loadFrameWork(null);
        
        }
        
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="create_framework.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "create_framework.jsp")
    })
    protected Forward createFramework(ManageCustomerForm form)
    {
        getRequest().setAttribute("levelList", this.frameWork);
        this.pageTitle = buildPageTitle(globalApp.ACTION_ADD_FRAMEWORK, form);
        setFormInfoOnRequest(form);        
        return new Forward("success", form);
    }

    private void checkUserState()
    {
        if (this.userName == null)
            getUserDetails();
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="returnToPreviousAction.do"
     * @jpf:forward name="error"   path="beginEditFramework.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "returnToPreviousAction.do"), 
        @Jpf.Forward(name = "error",
                     path = "beginEditFramework.do")
    })
    protected Forward saveFramework(ManageCustomerForm form)
    {
        
    	checkUserState();
        OrgNodeCategory[] orgNodeCategories = null;
        ArrayList oldFramework = new ArrayList();
        
        if (this.frameWork == null)
        {
        
            this.frameWork = new ArrayList();  
        
        }
        oldFramework = (ArrayList)this.frameWork.clone();
        String names = form.getActionElement();
        
        if (!names.equals(globalApp.ACTION_EDIT_FRAMEWORK))
        {
            
            this.frameWork = new ArrayList();  
            String separator = "#";
            StringTokenizer st = new StringTokenizer(names, separator);
                             
            while (st.hasMoreTokens())
            {
            
                String token = st.nextToken();
                Level level = buildLevel(token);
                for (int i = 0; i < oldFramework.size(); i++)
                {
                    
                    Level oldLevel = (Level)oldFramework.get(i);
                    if (level.getId().equals(oldLevel.getId()))
                    {
                        
                        level.setDeletable(oldLevel.getDeletable());
                        level.setAfterInsertable(oldLevel.getAfterInsertable());
                        break;
                    }
                }
                this.frameWork.add(level);
            
            }
        }
        
        boolean validFramework =CustomerFormUtils.verifyFramewok(form, frameWork);
        
        if (validFramework)
        {   
            orgNodeCategories = CustomerSearchUtils.
                                     setFrameworkDetailsFromList(this.frameWork);
        }
        else
        {
            form.setActionElement(globalApp.ACTION_EDIT_FRAMEWORK);
            form.setCurrentAction(globalApp.ACTION_EDIT_FRAMEWORK);
            for (int i = 0; i < this.frameWork.size(); i++)
            {
        
                Level tempLevel =(Level)this.frameWork.get(i);
                tempLevel.setOrder(new Integer(i + 1));
                
                if (tempLevel.getOrder().intValue() == 1)
                {
            
                    tempLevel.setDeletable(Boolean.FALSE); 
                    tempLevel.setBeforeInsertable(Boolean.FALSE);
                     
                }
                
                this.frameWork.remove(i);
                this.frameWork.add(i, tempLevel);
            }
            return new Forward("error", form);  
        }
        
        CustomerProfileInformation customerProfile = form.getCustomerProfile();
        Customer customer = customerProfile.makeCopy(this.userName, orgNodeCategories); 
        customer.setCustomerId(form.getSelectedCustomerId());           
        
        boolean isUpdateSuccessful = updateFrameWork(this.userName, customer, form);
        if (isUpdateSuccessful)
        {     
            if (this.savedForm == null)
            {
                initialize(globalApp.ACTION_DEFAULT);
            }
            this.savedForm.setMessage(Message.EDIT_FRAMEWORK_TITLE, 
                                      Message.EDIT_FRAMEWORK_SUCCESSFUL, 
                                      Message.INFORMATION);                    
            this.frameWork = null;
            //added for pagination
            this.savedForm.setSelectedCustomerId(form.getSelectedCustomerId());
            this.savedForm.setCustomerPageRequested(form.getCustomerPageRequested());
            this.savedForm.setCustomerSortColumn(form.getCustomerSortColumn());
            this.savedForm.setCustomerSortOrderBy(form.getCustomerSortOrderBy());
            this.savedForm.setCustomerMaxPage(form.getCustomerMaxPage());     
        }
        else
        {
                
            return new Forward("error", form);
        
        }
                         
        this.globalApp.navPath.setReturnActions(globalApp.ACTION_FIND_CUSTOMER, globalApp.ACTION_FIND_CUSTOMER);
               
        return new Forward("success", form);
    }

    
    /**
     * @jpf:action
     * @jpf:forward name="success" path="editFramework.do"
     * @jpf:forward name="error" path="beginFindCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "editFramework.do"), 
        @Jpf.Forward(name = "error",
                     path = "beginFindCustomer.do")
    })
    protected Forward beginEditFramework(ManageCustomerForm form)
    {
        CustomerProfileInformation customerProfile = null;
        customerProfile = setCustomerProfileToForm(form, Message.EDIT_FRAMEWORK_TITLE);
        
        if (customerProfile.getName() != null && customerProfile.getName().length() > 0)
        {
    
            this.getRequest().getSession().setAttribute("CustomerName",
                                                             customerProfile.getName()); 
        }
              
        if (form.getCurrentAction().equals(globalApp.ACTION_DEFAULT) && (form.getMessage().getType() != null && !form.getMessage().getType().equals(Message.ERROR)))
        {
        
            clearMessage(form);
        
        }
        
        form.clearSectionVisibility();
        
        this.createNewFramework = false;
        if (this.frameWork == null)
        {
            
            this.frameWork = (ArrayList)loadFrameWork(customerProfile.getCategoryList());
            
        }
        
        this.globalApp.navPath.addCurrentAction(globalApp.ACTION_EDIT_FRAMEWORK);
                
        this.helpLink = "/help/index.html#editing_or_deleting_users.htm";
        
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="edit_framework.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "edit_framework.jsp")
    })
    protected Forward editFramework(ManageCustomerForm form)
    {
        getRequest().setAttribute("levelList", this.frameWork);
        form.validateValues();
        String actionElement = form.getActionElement();
       // form.resetValuesForAction(actionElement, globalApp.ACTION_EDIT_CUSTOMER);      
        form.setCustomerMaxPage(customerMaxPage);
        this.getRequest().setAttribute("isEditFrameWork", Boolean.TRUE);
        this.pageTitle = buildPageTitle(globalApp.ACTION_EDIT_FRAMEWORK, form);
        form.setCurrentAction(globalApp.ACTION_EDIT_FRAMEWORK);
        form.setActionElement(globalApp.ACTION_EDIT_FRAMEWORK);
        setFormInfoOnRequest(form);
        return new Forward("success", form);
    }

    /**
     * @jpf:action
     * @jpf:forward name="error" path="findCustomer.do"
     * @jpf:forward name="errorFlow" path="beginFindCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "error",
                     path = "findCustomer.do"), 
        @Jpf.Forward(name = "errorFlow",
                     path = "beginFindCustomer.do")
    })
    protected Forward createAdministrator(ManageCustomerForm form)
    {
        
        String contextPath = "/UserManagementWeb/manageUser/beginAddAdministrator.do";
        Integer orgNodeId = new Integer(0);
        String orgNodeName = "";
        if (!"".equals(form.getSelectedCustomerId()))
        {
            Node topNodeForCustomer= CustomerSearchUtils.getTopNodeForCustomer(this.customerManagement, form, Message.CREATE_ADMIN_ERROR);
            if (topNodeForCustomer == null)
            {
                
                String msg = MessageResourceBundle.getMessage("CustomerManagement.Failed");                                        
                form.setMessage(Message.CREATE_ADMIN_ERROR, msg, Message.ERROR);
                return new Forward("error", form);
            
            }
            orgNodeId = topNodeForCustomer.getOrgNodeId();
            orgNodeName = topNodeForCustomer.getOrgNodeName();
        }
        
        this.createAdministrator = true;
         
        //for pagination
        if (form.getSelectedCustomerId() != null)
        {
        
            this.savedForm = form.createClone();
            this.savedForm.setMessage(Message.CREATE_ADMIN_TITLE, 
                                  Message.CREATE_ADMIN_SUCCESSFUL, 
                                  Message.INFORMATION); 
            
        }
        else
        {
           
            return new Forward("error"); 
            
        }
    
        String orgNodeParam = "orgNodeName=" + 
                              orgNodeName +
                              "&" +
                              "orgNodeId=" +
                              orgNodeId.toString();
        String url = contextPath + "?" + orgNodeParam ;         
         //Added LM12
        this.savedForm.setEnableLicense(form.getEnableLicense());   
        try
        {
            getResponse().sendRedirect(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
       
        return null;        
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="/manageOrganization/beginManageOrganization.do"
     * @jpf:forward name="error" path="findCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "/manageOrganization/beginManageOrganization.do"), 
        @Jpf.Forward(name = "error",
                     path = "findCustomer.do")
    })
    protected Forward manageOrganization(ManageCustomerForm form)
    {
            //String contextPath = "/OrganizationManagementWeb/"+
            //                     "manageOrganization/beginManageOrganization.do";
        Integer orgNodeId = new Integer(0);
        String orgNodeName = "";
        if (!"".equals(form.getSelectedCustomerId()))
        {
            
            Node topNodeForCustomer= CustomerSearchUtils.getTopNodeForCustomer(this.customerManagement, form, Message.MANAGE_ORG_ERROR);
            
            if (topNodeForCustomer != null)
            {
                this.savedForm = form.createClone();
                orgNodeId = topNodeForCustomer.getOrgNodeId();
                orgNodeName = topNodeForCustomer.getOrgNodeName();
            
            }
            else
            {
                
                String msg = MessageResourceBundle.getMessage("CustomerManagement.Failed");                                        
                form.setMessage(Message.MANAGE_ORG_ERROR, msg, Message.ERROR);
                return new Forward("error", form);
                
            }
        }
            //String orgNodeParam = "orgNodeName=" + orgNodeName + "&" + 
             //                     "orgNodeId=" + orgNodeId.toString();
            //String url = contextPath + "?" + orgNodeParam;            
        
        this.getRequest().setAttribute("orgNodeName", orgNodeName);
        this.getRequest().setAttribute("orgNodeId", orgNodeId.toString());
        
        this.frommanageOrg = Boolean.TRUE;
            
            //try {
            //   getResponse().sendRedirect(url);
            //} catch (IOException e) {
            //e.printStackTrace();
            //}
        
        return new Forward("success");
       
        
            //return null;        
    }
   
    /**
     * forward to the action that stored in the saved form
     * @jpf:action
     * @jpf:forward name="editCustomer" path="beginEditCustomer.do"
     * @jpf:forward name="editFramework" path="beginEditFramework.do"
     * @jpf:forward name="createAdministrator" path="createAdministrator.do"
     * @jpf:forward name ="manageOrganization" path="manageOrganization.do"
     * @jpf:forward name="defaultAction" path="defaultAction.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "editCustomer",
                     path = "beginEditCustomer.do"), 
        @Jpf.Forward(name = "editFramework",
                     path = "beginEditFramework.do"), 
        @Jpf.Forward(name = "createAdministrator",
                     path = "createAdministrator.do"), 
        @Jpf.Forward(name = "manageOrganization",
                     path = "manageOrganization.do"), 
        @Jpf.Forward(name = "defaultAction",
                     path = "defaultAction.do")
    })
    protected Forward gotoNextAction(ManageCustomerForm form)
    {
        String currentAction = this.savedForm.getCurrentAction();
        if (currentAction == null)
        {
         
            currentAction = globalApp.ACTION_DEFAULT;
        
        }
        currentCustomerPageRequested = this.savedForm.getCustomerPageRequested(); 
       
         
        return new Forward(currentAction, this.savedForm.createClone());
    }
   
    /**
     * @jpf:action
     * @jpf:forward name="addCustomer" path="addCustomer.do"
     * @jpf:forward name="editCustomer" path="beginEditCustomer.do"
     * @jpf:forward name="defaultAction" path="defaultAction.do"
     * @jpf:forward name="findCustomer" path ="returnToFindCustomer.do"
     * @jpf:forward name="viewCustomer" path="returnToViewCustomer.do" redirect="true"
     * @jpf:forward name="editFramework" path="beginEditFramework.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "addCustomer",
                     path = "addCustomer.do"), 
        @Jpf.Forward(name = "editCustomer",
                     path = "beginEditCustomer.do"), 
        @Jpf.Forward(name = "defaultAction",
                     path = "defaultAction.do"), 
        @Jpf.Forward(name = "findCustomer",
                     path = "returnToFindCustomer.do"), 
        @Jpf.Forward(name = "viewCustomer", 
                     path = "returnToViewCustomer.do", 
                     redirect = true), 
        @Jpf.Forward(name = "editFramework",
                     path = "beginEditFramework.do")
    })
    protected Forward returnToPreviousAction(ManageCustomerForm form)
    {
        String action = this.globalApp.navPath.resetToPreviousAction();
        
        if (action.equals(globalApp.ACTION_ADD_CUSTOMER))
        {
            
            this.savedForm = initializeAddCustomer();     
        
        } 
        else if (action.equals(globalApp.ACTION_EDIT_CUSTOMER))
        {
        
            this.savedForm = form.createClone();
        
        } 
        else if (action.equals(globalApp.ACTION_FIND_CUSTOMER))
        {
        
            initStateOptions(globalApp.ACTION_FIND_CUSTOMER);
        
        } 
        else if (action.equals(globalApp.ACTION_VIEW_CUSTOMER))
        {
        
            if (form.getByCustomerProfileVisible() != null)
            {
                
                this.savedForm.setByCustomerProfileVisible(
                        form.getByCustomerProfileVisible());
                this.savedForm.setByCustomerBillingVisible(
                        form.getByCustomerBillingVisible());
                this.savedForm.setByCustomerMailingVisible(
                        form.getByCustomerMailingVisible());
                //Added for LM12
                this.savedForm.setEnableLicense(form.getEnableLicense()); 
            }
            return new Forward(action);
        }
        else
        {
            action = globalApp.ACTION_DEFAULT;
            initStateOptions(globalApp.ACTION_FIND_CUSTOMER);
            initialize(action);
        }
        
        if (this.savedForm == null)
            initialize(action);
    
        this.savedForm.setCurrentAction(globalApp.ACTION_DEFAULT);
       
        if (currentCustomerPageRequested != null)
        {
            this.savedForm.setCustomerPageRequested(currentCustomerPageRequested);
        
        }
        //Added for LM12
        this.savedForm.setEnableLicense(form.getEnableLicense()); 
        return new Forward(action, this.savedForm);
    }
    
    /**
     * return to find user module
     * @jpf:action
     * @jpf:forward name="success" path="findCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "findCustomer.do")
    })
    protected Forward returnToFindCustomer(ManageCustomerForm form)
    {
    	String msg = (String)this.getRequest().getParameter("SuccessfullyAdded");
    	
        if (this.savedForm == null)
        {
            initialize(globalApp.ACTION_DEFAULT);
        }
        this.savedForm.setCustomerProfile(this.customerSearch);
        this.savedForm.setCurrentAction(globalApp.ACTION_DEFAULT);
        
        if (! this.createAdministrator)
        {
        
            this.savedForm.setMessage(form.getMessage());
        
        }
        else
        {
        	if (msg != null && "SuccessfullyAdded".equals(msg)) 
        		this.savedForm.setMessage(Message.CREATE_ADMIN_TITLE, Message.CREATE_ADMIN_SUCCESSFUL, Message.INFORMATION);
        	else	
        		savedForm.clearMessage();
        
        }
        
        return new Forward("success", this.savedForm);
    }
    
    
    /**
     * @jpf:action
     * @jpf:forward name="errorEdit" path="editCustomer.do"
     * @jpf:forward name="errorAdd" path="addCustomer.do"
     * @jpf:forward name="success" path="returnToPreviousAction.do"
     * @jpf:forward name= "error" path="beginCreateFramework.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "errorEdit",
                     path = "editCustomer.do"), 
        @Jpf.Forward(name = "errorAdd",
                     path = "addCustomer.do"), 
        @Jpf.Forward(name = "success",
                     path = "returnToPreviousAction.do"), 
        @Jpf.Forward(name = "error",
                     path = "beginCreateFramework.do")
    })
    protected Forward saveAddEditCustomer(ManageCustomerForm form)
    {
        OrgNodeCategory[] orgNodeCategories = null;
        CustomerProfileInformation customerProfile = null;
        Integer customerId = form.getSelectedCustomerId();
        boolean isCreateNew = (customerId == null || customerId.intValue() == 0) ? true : false;
       
     
        if (isCreateNew)
        {
            //After add customer complelete old serach criteria should not be persist
            this.customerSearch = null;
            //customer profile info need to be taken from previous page
            customerProfile = this.savedForm.getCustomerProfile();
            form.setCustomerProfile(customerProfile); 
            //create framewok only for create customer
            this.frameWork = new ArrayList(); 
            String actionElement = form.getActionElement();
            
            if (!(actionElement.equals(globalApp.ACTION_DEFAULT) ) && !(actionElement.equals(globalApp.ACTION_ADD_FRAMEWORK)))
            {
                        
                String separator = "#";
                StringTokenizer st = new StringTokenizer(actionElement, separator);
                
                while (st.hasMoreTokens())
                {
                    String token = st.nextToken();
                    Level level = buildLevel(token);
                    this.frameWork.add(level);
                }
            }
            
            boolean validFramework =CustomerFormUtils.verifyFramewok(form, frameWork);
            if (validFramework)
            {   
                
                orgNodeCategories = CustomerSearchUtils.setFrameworkDetailsFromList(this.frameWork);

                this.frameWork = null;
            }
            else
            {
            
                form.setActionElement(globalApp.ACTION_ADD_FRAMEWORK);
                form.setCurrentAction(globalApp.ACTION_ADD_FRAMEWORK);
                this.createNewFramework = false; 
                for (int i = 0; i < this.frameWork.size(); i++)
                {
                    
                    Level tempLevel =(Level)this.frameWork.get(i);
                    tempLevel.setOrder(new Integer(i + 1));
                    
                    if (tempLevel.getOrder().intValue() == 1)
                    {
                    
                        tempLevel.setDeletable(Boolean.FALSE); 
                        tempLevel.setBeforeInsertable(Boolean.FALSE); 
                    
                    }
                    
                    this.frameWork.remove(i);
                    this.frameWork.add(i, tempLevel);
                
                }
                return new Forward("error", form);  
            }
                     
        }
        else
        { 
            //validation for edit only as add validation already done
            boolean validInfo = CustomerFormUtils.verifyCustomerInformation(form, customerId);
            if (!validInfo)
            {
                
                form.setActionElement(globalApp.ACTION_DEFAULT);
                form.setCurrentAction(globalApp.ACTION_DEFAULT); 
                return new Forward("errorEdit", form);
            
            }
            form.getCustomerProfile().setId(String.valueOf(customerId)); 
            form.setByCustomerProfileVisible(Boolean.TRUE);
        
        }
        
        Customer customer = saveCustomerProfileInformation(isCreateNew, form, this.userName, orgNodeCategories);                                                        
        
        customerId = customer.getCustomerId();
        
        if (customerId == null || customerId.intValue() == 0)
        {
         
            if (isCreateNew)
            {
                
                return new Forward("errorAdd", form);
            
            }
            else
            {
                
                return new Forward("errorEdit", form);
            
            }
        
        }
        
        form.setSelectedCustomerId(customerId);
        String title = Message.EDIT_CUSTOMER_TITLE;
        if (isCreateNew)
        {
    
            title = Message.ADD_CUSTOMER_TITLE;  
        
        } 
        customerProfile = setCustomerProfileToForm(form, title);
        this.viewCustomerFromSearch = false; 
        
        if (isCreateNew)
        {
            CustomerSearchUtils.addCustomerInCustomerList(customerProfile, this.customerList);
            if (customerId.intValue() > 0)
            {
                form.setMessage(Message.ADD_CUSTOMER_TITLE, Message.ADD_CUSTOMER_SUCCESSFUL, Message.INFORMATION);
            }
            else
            {
                form.setMessage(Message.ADD_CUSTOMER_TITLE, Message.ADD_CUSTOMER_ERROR, Message.INFORMATION);
            }
        }
        else
        {
            
            if (customerId.intValue() > 0)
            {
                
                form.setMessage(Message.EDIT_CUSTOMER_TITLE, Message.EDIT_CUSTOMER_SUCCESSFUL, Message.INFORMATION);
            }
            else
            { 
                
                form.setMessage(Message.EDIT_CUSTOMER_TITLE, Message.EDIT_CUSTOMER_ERROR, Message.INFORMATION);
            }
        }
        if (this.savedForm == null)
        {
            initialize(globalApp.ACTION_DEFAULT);
        }
        this.savedForm.setMessage(form.getMessage());
        this.savedForm.setCustomerProfile(form.getCustomerProfile());
        this.savedForm.setSelectedCustomerName(form.getSelectedCustomerName());
        this.savedForm.setSelectedCustomerId(form.getSelectedCustomerId());
        //added for pagination
        this.savedForm.setCustomerPageRequested(form.getCustomerPageRequested());
        this.savedForm.setCustomerSortColumn(form.getCustomerSortColumn());
        this.savedForm.setCustomerSortOrderBy(form.getCustomerSortOrderBy());
        this.savedForm.setCustomerMaxPage(form.getCustomerMaxPage());

        if (isCreateNew)
        {
        
            this.globalApp.navPath.setReturnActions(globalApp.ACTION_VIEW_CUSTOMER, globalApp.ACTION_ADD_CUSTOMER);
        
        }
        else
        {
        
            this.globalApp.navPath.setReturnActions(globalApp.ACTION_VIEW_CUSTOMER, globalApp.ACTION_FIND_CUSTOMER);
        
        }
        setFormInfoOnRequest(form);    
        return new Forward("success");
    }
   
    /**
     * @jpf:action
     * @jpf:forward name="success" path="edit_customer_license.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "edit_customer_license.jsp")
    })
    protected Forward addEditCustomerLicense(ManageCustomerForm form)
    {   
       // check for required fields
        boolean validInfo = true;
        validInfo = LicenseFormUtils.verifyLicenseInformation(form);
        if (!validInfo)
        {           
                //validation failed
        	setFormInfoOnRequest(form);
            return new Forward("success", form);
        }
         
        LicenseNode licenseNode = form.getLicenseNode();
        boolean result = saveOrUpdateCustomerLicenses(licenseNode, form);
        if (result)
        {
            
            String msg = MessageResourceBundle.getMessage("ManageLicense.license.AddUpdateSuccessfully");
            String title = Message.ADD_UPDATED_LICENSE;
            form.setMessage(title, msg, Message.INFORMATION); 
            //Changed for Defect 59260 and 59262
            setLicenseNodeToForm(form);
            
                        
        }
        else
        {
            String msg = MessageResourceBundle.getMessage("ManageLicense.license.AddUpdateError");
            String title = Message.ADD_UPDATED_LICENSE;
            form.setMessage(title, msg, Message.ERROR);
                 
        }
        setFormInfoOnRequest(form);
        return new Forward("success", form);
    }

    
    
    
    
    /**
     * @jpf:action
     * @jpf:forward name="success" path="returnToPreviousAction.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "returnToPreviousAction.do")
    })
    protected Forward handleBackButton(ManageCustomerForm form)
    {
        this.clearCurrentMessage = true;
        this.createNewFramework = false;
        this.frameWork = null;
        clearMessage(form);
        this.savedForm.setActionElement(ACTION_CURRENT_ELEMENT);
        //added for pagination
        this.savedForm.setSelectedCustomerId(form.getSelectedCustomerId()); 
        this.savedForm.setCustomerPageRequested(form.getCustomerPageRequested());
        this.savedForm.setCustomerSortColumn(form.getCustomerSortColumn());
        this.savedForm.setCustomerSortOrderBy(form.getCustomerSortOrderBy());
        this.savedForm.setCustomerMaxPage(form.getCustomerMaxPage());
  
        return new Forward("success", form);
    }
    
    /**
     * @jpf:action
     * @jpf:forward name="success" path="viewCustomer.do"
     * @jpf:forward name="error" path="beginFindCustomer.do"
     */
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success",
                         path = "viewCustomer.do"), 
            @Jpf.Forward(name = "error",
                         path = "beginFindCustomer.do")
        })
    protected Forward beginViewCustomer(ManageCustomerForm form)
    {
        clearMessage(form);
        form.clearSectionVisibility();
        form.setByCustomerProfileVisible(Boolean.TRUE);
        this.globalApp.navPath.addCurrentAction(globalApp.ACTION_VIEW_CUSTOMER);
    
        return new Forward("success", form);
    }
    

    /**
     * @jpf:action
     * @jpf:forward name = "success" path ="view_customer.jsp"
     * @jpf:validation-error-forward name="failure" path="viewCustomer.do"   
     */
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success",
                         path = "view_customer.jsp")
        }, 
                    validationErrorForward = @Jpf.Forward(name = "failure",
                                                          path = "viewCustomer.do"))
    protected Forward viewCustomer(ManageCustomerForm form)
    {
        CustomerProfileInformation customerProfile = null;
        customerProfile = setCustomerProfileToForm(form, Message.VIEW_TITLE);
        PermissionsUtils.setPermissionRequestAttribute(this.getRequest(), Boolean.FALSE);
        form.setCurrentAction(globalApp.ACTION_DEFAULT);
        this.getRequest().setAttribute("viewOnly", Boolean.TRUE);       
        this.pageTitle = buildPageTitle(globalApp.ACTION_VIEW_CUSTOMER, form); 
        setFormInfoOnRequest(form);        
        return new Forward("success", form);
    }
    
    /**
     * returnToViewCustomer
     * @jpf:action
     * @jpf:forward name="success" path="viewCustomer.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "viewCustomer.do")
    })
    protected Forward returnToViewCustomer()
    {   
        
        return new Forward("success", this.savedForm);                                                                                                                                                                                                    
    }
    
    /**
     * setup and return to the last action
     * @jpf:action
     * @jpf:forward name="success" path="returnToPreviousAction.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "returnToPreviousAction.do")
    })
    protected Forward cancelCurrentAction(ManageCustomerForm form)
    {   
        
    	this.frameWork = null;
        this.clearCurrentMessage = true;
        clearMessage(form);
        if (this.savedForm == null)
            this.savedForm = new ManageCustomerForm();
        this.savedForm.setActionElement(ACTION_CURRENT_ELEMENT);
        //added for pagination
        this.savedForm.setSelectedCustomerId(form.getSelectedCustomerId());
        this.savedForm.setCustomerPageRequested(form.getCustomerPageRequested());
        this.savedForm.setCustomerSortColumn(form.getCustomerSortColumn());
        this.savedForm.setCustomerSortOrderBy(form.getCustomerSortOrderBy());
        this.savedForm.setCustomerMaxPage(form.getCustomerMaxPage());
        //Added LM12
        this.savedForm.setEnableLicense(form.getEnableLicense());         
        return new Forward("success", form);
    }
    
    /**
     * Add for LM12
     * @jpf:action
     * @jpf:forward name="success" path="edit_customer_license.jsp"
     */
	@Jpf.Action(
		forwards = { 
			@Jpf.Forward(name = "success", path = "edit_customer_license.jsp")
		}
	)
    protected Forward manageLicense(ManageCustomerForm form)
    {   
        
        setLicenseNodeToForm(form);
        this.globalApp.navPath.addCurrentAction(globalApp.ACTION_ADD_EDIT_LICENSE);
        setFormInfoOnRequest(form);
        return new Forward("success", form);
    }

    
    
/////////////////////////////////////////////////////////////////////////////////////////////
/////// *********************** Private methods ************* ///////////////////////////////    
/////////////////////////////////////////////////////////////////////////////////////////////   
    /**
     * initialize
     */
    private ManageCustomerForm initialize(String action)
    {        
        getUserDetails();
        this.savedForm = new ManageCustomerForm();
        this.savedForm.init(action);
        this.globalApp.navPath = new NavigationPath();
        this.globalApp.navPath.reset(globalApp.ACTION_FIND_CUSTOMER);
        
        return this.savedForm;
    }

    /**
     * getUserDetails
     */
    private void getUserDetails()
    {
        java.security.Principal principal = getRequest().getUserPrincipal();
        if ( principal != null )  {
         
            this.userName = principal.toString();
        
        } else {
                       
            this.userName = (String)getSession().getAttribute("userName");
        }
        
        getSession().setAttribute("userName", this.userName);
    }

    /**
     * loadFrameWork
     */
    private List loadFrameWork(Level[] categoryList)
    {
        List levels = new ArrayList();        
        Level level = null;
        
        if ( categoryList != null ) {
        
            for( int i = 0 ; i < categoryList.length ; i++ ) {
                levels.add(categoryList[i]);
            }
        
        } else {
        
            level = new Level(new Integer(0), "State", new Integer(1), 
                            Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);            
            levels.add(level);
        
        }
        return levels;
    }

    /**
     * buildLevel
     */
    private Level buildLevel(String token)
    {
        String separator = "_";
        StringTokenizer st = new StringTokenizer(token, separator);
        String name = st.nextToken();
        String id = st.nextToken();
        String order = st.nextToken();
        Level level = new Level(Integer.valueOf(id), name, Integer.valueOf(order));        
        return level;
    }
    
       
    /**
     * clearMessage
     */
    private void clearMessage(ManageCustomerForm form)
    {        
        if ( this.clearCurrentMessage ) {    
                
            form.clearMessage();
            if ( this.savedForm != null ) {
               
                this.savedForm.clearMessage();
            
            }
        }
        this.clearCurrentMessage = true;
    }
    
     /**
     * initStateOptions
     */
    private void initStateOptions(String action)
    {        
        this.stateOptions = new LinkedHashMap();
        this.mailingStateOptions = new LinkedHashMap();
        this.billingStateOptions = new LinkedHashMap();
        TreeMap territoriesOptions = new TreeMap();
        
        if ( action.equals(globalApp.ACTION_ADD_CUSTOMER)) {
            
            this.stateOptions.put("", Message.SELECT_STATE);
            this.mailingStateOptions.put("", Message.SELECT_STATE);
            this.billingStateOptions.put("", Message.SELECT_STATE);
        
        }
        
        if(action.equals(globalApp.ACTION_EDIT_CUSTOMER)) {
        
            this.mailingStateOptions.put("", Message.SELECT_STATE);
            this.billingStateOptions.put("",Message.SELECT_STATE);
        
        }
        
        if ( action.equals(globalApp.ACTION_FIND_CUSTOMER) )  {
        
            this.stateOptions.put("", Message.ANY_STATE);
        }
        
        try {
            USState[] state = this.customerManagement.getStates();
            if ( state != null ) {
                
                for ( int i = 0 ; i < state.length ; i++ ) {
                    
                    if (!isContains (state[i].getStatePrDesc())) {
                        
                        this.stateOptions.put(state[i].getStatePr(), 
                                state[i].getStatePrDesc());
                        this.mailingStateOptions.put(state[i].getStatePr(), 
                                state[i].getStatePrDesc());  
                        this.billingStateOptions.put(state[i].getStatePr(),
                                state[i].getStatePrDesc());                                
                                                              
                    } else {
                        
                        territoriesOptions.put(state[i].getStatePrDesc(),state[i]);
                        
                    }
                }
            }
            orderedStateTerritories(territoriesOptions); 
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
     /**
     * initCustomerTypeOptions
     */
    private void initCustomerTypeOptions(String action)
    {        
        this.customerOptions = new LinkedHashMap();
        
        if ( action.equals(globalApp.ACTION_ADD_CUSTOMER) ) {
        
            customerOptions.put("",Message.SELECT_CUSTOMER_ENTITY);
        
        }
        //not sure if this will be hard coded or from db?????
        customerOptions.put(CTBConstants.TABE_CUSTOMER,CTBConstants.TABE_CUSTOMER);
        customerOptions.put(CTBConstants.TERRANOVA_CUSTOMER,CTBConstants.TERRANOVA_CUSTOMER);
        customerOptions.put(CTBConstants.OTHER_CUSTOMER,CTBConstants.OTHER_CUSTOMER);          
    }
    
     private boolean isContains (String stateDesc) {
        
        String []USterritories = {"Virgin Islands","Puerto Rico",
                                  "Palau","North Mariana Islands",
                                  "Marshall Islands","Guam","F.S. of Micronesia",
                                  "American Samoa"};
        
        for ( int i = 0; i < USterritories.length; i++ ) {
            
            if (USterritories[i].equals(stateDesc)) {
                return true;
            } 
        }
        return false;
        
    } 
    
     private void orderedStateTerritories (TreeMap territoriesOptions) {
        
        Collection territories = territoriesOptions.values();
        
        Iterator iterate = territories.iterator();
        while ( iterate.hasNext() ) {
            
            USState state = (USState) iterate.next();
            this.stateOptions.put(state.getStatePr(), 
                                            state.getStatePrDesc());
            this.mailingStateOptions.put(state.getStatePr(), 
                                            state.getStatePrDesc());                                            
            this.billingStateOptions.put(state.getStatePr(),
                                            state.getStatePrDesc());                                            
        }
    }
    
    private void resetCustomerList() {
        this.customerList.clear();
    }
    
     /**
     * initPagingSorting
     */
    private void initPagingSorting(ManageCustomerForm form)
    {
        String actionElement = form.getActionElement();
        
        if ( (actionElement.indexOf("customerPageRequested") > 0) 
                    || (actionElement.indexOf("customerSortOrderBy") > 0) ) {
           
            form.setSelectedCustomerId(null);
            form.setSelectedCustomerName(null);
        
        }
    }
    
    /**
     * initSearch
     */
    private boolean initSearch(ManageCustomerForm form)
    {
        boolean applySearch = false;
        String currentAction = form.getCurrentAction();
        
        if ( (currentAction != null) 
                    && currentAction.equals(globalApp.ACTION_APPLY_SEARCH) ) {
        
            applySearch = true;
            this.searchApplied = false;
            form.setCustomerSortColumn(FilterSortPageUtils.CUSTOMER_DEFAULT_SORT_COLUMN);
            form.setCustomerSortOrderBy(FilterSortPageUtils.ASCENDING);      
            form.setCustomerPageRequested(new Integer(1));    
            form.setCustomerMaxPage(new Integer(1));                  
        
        }
        
        if ( (currentAction != null) 
                    && currentAction.equals(globalApp.ACTION_CLEAR_SEARCH) ) {
           
            applySearch = false;
            this.searchApplied = false;
            form.clearSearch();
        
        }

        if ( this.searchApplied ) {
        
            applySearch = true;
        
        }
        else {
        
            form.setSelectedCustomerId(null);
            form.setSelectedCustomerName(null);
        
        }
                
        return applySearch;
    }
    
    /**
     * findByCustomerProfile
     */
    private CustomerData findCustomerProfile(ManageCustomerForm form) {
        
        String actionElement = form.getActionElement();
        form.resetValuesForAction(actionElement, globalApp.ACTION_FIND_CUSTOMER);        
        String customerName = form.getCustomerProfile().getName().trim();
        String customerState = form.getCustomerProfile().getState().trim();
        String invalidString = "";            
        String invalidCharFields = CustomerFormUtils.verifyFindCustomerInfo(
                                           form.getCustomerProfile()); 
              

        if ( invalidCharFields.length() > 0 ) {
            
            invalidString = invalidCharFields + ("<br/>" 
                    + Message.INVALID_NAME_CHARS);
        
        }
  
        if ( invalidString!=null && invalidString.length() > 0 ) {    
        
            form.setMessage(Message.INVALID_CHARS_TITLE, 
                    invalidString, Message.ERROR);
            return null;
        
        }
        
        String extCustomerId = "";
        
        if ( !"".equals(form.getCustomerProfile().getCode()) ) {
        
            extCustomerId = form.getCustomerProfile().getCode();    
        
        }
            
        PageParams page = FilterSortPageUtils.buildPageParams(
                form.getCustomerPageRequested(), 
                FilterSortPageUtils.PAGESIZE_10);
        SortParams sort = FilterSortPageUtils.buildUserSortParams(
                form.getCustomerSortColumn(), 
                form.getCustomerSortOrderBy());
        FilterParams filter = FilterSortPageUtils.buildFilterParams(
                customerName, extCustomerId, customerState);
        
        CustomerData customerData = null;
        
        try {
            
            if ( filter == null ) {
            
                customerData = CustomerSearchUtils.searchAllCustomers(
                                       this.customerManagement, 
                                       this.userName, 
                                       page, sort);   
                this.pageMessage = Message.FIND_FOUND_CUSTOMER;
            
            } else {
                customerData = CustomerSearchUtils.searchCustomersByCriteria(
                                       this.customerManagement,
                                       this.userName,
                                       filter, page, sort);   
                this.pageMessage = Message.FIND_FOUND_CUSTOMER;                                    
            }
        } catch(CTBBusinessException be){
                be.printStackTrace();
                String msg = MessageResourceBundle.getMessage(be.getMessage());
                form.setMessage(Message.FIND_TITLE, msg, Message.ERROR);
        }
    
        return customerData;
    }
    
    /**
     * buildPageTitle
     */
    private String buildPageTitle(String action, ManageCustomerForm form)
    {
        String title = "";
        CustomerProfileInformation customerProfile = null;
        
        if ( action.equals(globalApp.ACTION_FIND_CUSTOMER) ) {
            
            title = Message.FIND_TITLE;
            
        }
        else if ( action.equals(globalApp.ACTION_ADD_CUSTOMER) ) {
            
            title = Message.ADD_CUSTOMER_TITLE;
            webTitle = "CTB/McGraw-Hill OAS - Add Customer";
        
        }
        else if ( action.equals(globalApp.ACTION_ADD_FRAMEWORK) ) {
            
            title = Message.ADD_FRAMEWORK_TITLE;
            webTitle = "CTB/McGraw-Hill OAS - Create Framework";
        
        }
        else if ( action.equals(globalApp.ACTION_VIEW_CUSTOMER) ) {
         
            title = Message.VIEW_TITLE;
            customerProfile = form.getCustomerProfile();            
        
        }
        else if ( action.equals(globalApp.ACTION_VIEW_CUSTOMER_FRAMEWORK) ) {
        
            title = Message.VIEW_FRAMEWORK_TITLE;
            customerProfile = form.getCustomerProfile();           
        
        }
        else if ( action.equals(globalApp.ACTION_EDIT_CUSTOMER) ) {
        
            title = Message.EDIT_CUSTOMER_TITLE;
            webTitle = "CTB/McGraw-Hill OAS - Edit Customer";
            String oldCustomerName = (String)this.getRequest().getSession().
                                            getAttribute("CustomerName");
            
            if ( oldCustomerName!=null && oldCustomerName.length() > 0 ) {
            
                title =  Message.buildPageTitle(title, oldCustomerName);
            
            } else {
                
                customerProfile = form.getCustomerProfile();
            
            }
        } else if ( action.equals(globalApp.ACTION_EDIT_FRAMEWORK) ) {
            
            title = Message.EDIT_FRAMEWORK_TITLE;
            webTitle = "CTB/McGraw-Hill OAS - Edit Framework";
            String oldCustomerName = (String)this.getRequest().getSession().
                                                    getAttribute("CustomerName");
            
            if ( oldCustomerName != null && oldCustomerName.length() > 0 ) {
            
                 title =  Message.buildPageTitle(title, oldCustomerName);
            
            }
            else {
            
                customerProfile = form.getCustomerProfile();
            
            }
        }
               
        if ( customerProfile != null ) {
      
            title =  Message.buildPageTitle(title, customerProfile.getName());
      
        }
        
        return title;            
    }    
    
    /**
     * initializeAddCustomer
     */
    private ManageCustomerForm initializeAddCustomer()
    {        
        ManageCustomerForm form = initialize(globalApp.ACTION_ADD_CUSTOMER);     
        form.setSelectedCustomerId(null);
        form.setSelectedCustomerName(null);
        form.setCurrentAction(globalApp.ACTION_ADD_CUSTOMER);
        form.clearSectionVisibility();
        form.setByCustomerProfileVisible(Boolean.TRUE);
        initStateOptions(globalApp.ACTION_ADD_CUSTOMER);
        initCustomerTypeOptions(globalApp.ACTION_ADD_CUSTOMER);
        this.searchApplied = false;
        this.globalApp.navPath.reset(globalApp.ACTION_ADD_CUSTOMER);
        
        return form;
    }
    
    /**
     * save or update license information
     */
    private boolean saveOrUpdateCustomerLicenses(LicenseNode licenseNode,ManageCustomerForm form)
    {
        CustomerLicense customerLicense = null;
        boolean licensevalue = false;
        customerLicense = licenseNode.makeCopy(form);
         try {
            
            licensevalue = license.saveOrUpdateCustomerLicenses(customerLicense);  
            
        } catch (CTBBusinessException e) { 
            e.printStackTrace();
            String msg = MessageResourceBundle.getMessage(e.getMessage());                                        
            form.setMessage(Message.ADD_UPDATED_LICENSE, msg, Message.ERROR);
        }
        
        return licensevalue;
    }
     /**
     * handleAddOrEdit
     */
    protected void handleAddEdit(ManageCustomerForm form)
    {        
        addEditCustomerProfile(form);    
        form.setCurrentAction(globalApp.ACTION_DEFAULT);
        form.setActionElement(ACTION_FORM_ELEMENT);
    }
    
    /*
     * addEditCustomerProfile
    */
    protected void addEditCustomerProfile(ManageCustomerForm form) 
    {
        form.validateValues();
        String actionElement = form.getActionElement();
        form.resetValuesForAction(actionElement, globalApp.ACTION_EDIT_CUSTOMER);      
        form.setCustomerMaxPage(customerMaxPage);                    
    }
    
    /**
     * saveUserProfileInformation
     */
    private Customer saveCustomerProfileInformation(boolean isCreateNew, 
                                                    ManageCustomerForm form, 
                                                    String userName,
                                                    OrgNodeCategory[] orgNodeCategories)
    {       
        checkUserState();
         
        CustomerProfileInformation customerProfile = form.getCustomerProfile();
        Customer customer = customerProfile.makeCopy(userName, orgNodeCategories);
        String title = null;
        
        try {                    
            if ( isCreateNew ) {
    
                title = Message.ADD_CUSTOMER_TITLE;
                customer = this.customerManagement.createCustomer(userName, customer);
                this.customerManagement.setSDSConfiguration(customer.getCustomerId(), customer.getCustomerName());
    
            } else {
    
                title = Message.EDIT_CUSTOMER_TITLE;
                this.customerManagement.updateCustomer(this.userName, customer);
    
            }
        } catch (CTBBusinessException be) {
            be.printStackTrace();
            String msg = MessageResourceBundle.getMessage(be.getMessage());
            form.setMessage(title, msg, Message.ERROR);
            customer.setCustomerId(null);
        } catch (Exception e) {
            e.printStackTrace();
            form.setMessage(title, Message.EDIT_CUSTOMER_ERROR, Message.ERROR);
             customer.setCustomerId(null);
        }
                
        return customer;
    }
    
     /**
     * setCustomerProfileToForm
     */
    private CustomerProfileInformation setCustomerProfileToForm(ManageCustomerForm form,
                                                                String title) {
        Integer selectedCustomerId = form.getSelectedCustomerId();
        CustomerProfileInformation customerProfile = null;
       
        
        Customer selectedCustomer = new Customer();
        selectedCustomer.setCustomerId(selectedCustomerId);
        try{     
            customerProfile = CustomerSearchUtils.getCustomerProfileInformation(
                                    this.customerManagement, this.userName, 
                                    selectedCustomer);
        } catch(CTBBusinessException be){
            be.printStackTrace();
            String msg = MessageResourceBundle.getMessage(be.getMessage());                                        
            form.setMessage(title, msg, Message.ERROR);
        }                     
        form.setCustomerProfile(customerProfile);
        form.setSelectedCustomerName(customerProfile.getName());
        form.setSelectedCustomerId(Integer.valueOf(customerProfile.getId()));
        
        return customerProfile;
    }
    
    /**
     * setLicenseNodeToForm
     */
    private void setLicenseNodeToForm(ManageCustomerForm form) {
                                                               
        Integer selectedCustomerId = form.getSelectedCustomerId();
        LicenseNode licenseNode = null;
        CustomerLicense customerLicense = new CustomerLicense();
        
        try {   
              
            customerLicense = license.getCustomerLicenses(selectedCustomerId);
            licenseNode = new LicenseNode ();
            licenseNode.setAvailable(licenseNode.
                    ConvertIntegerToString(customerLicense.getAvailable()));
            licenseNode.setLicenseAfterLastPurchase(licenseNode.
                    ConvertIntegerToString(customerLicense.getAvailable()));
            licenseNode.setConsumed(licenseNode.
                    ConvertIntegerToString(customerLicense.getConsumedLicense()));
            licenseNode.setReserved(licenseNode.
                    ConvertIntegerToString(customerLicense.getReservedLicense()));
            licenseNode.setProductId(customerLicense.getProductId());
            licenseNode.setProductName(customerLicense.getProductName());
            form.setLicenseNode(licenseNode);
            form.getCustomerProfile().setName(customerLicense.getCustomerName());
            
        } catch(CTBBusinessException be){
            be.printStackTrace();
            String msg = MessageResourceBundle.getMessage(be.getMessage());                                        
            form.setMessage(Message.ADD_UPDATED_LICENSE, msg, Message.ERROR);
        }                     
       
    }        
       
   /*
    * updateFrameWork
    * It will update the framework edited by user
   */
    private boolean updateFrameWork(String userName, 
                                    Customer customer,
                                    ManageCustomerForm form) {
        
        try {
            this.customerManagement.updateFrameWork(this.userName, customer);
       } catch (CTBBusinessException be) {
            be.printStackTrace();
            String msg = MessageResourceBundle.getMessage(be.getMessage());
            form.setMessage(Message.EDIT_FRAMEWORK_TITLE, msg, Message.ERROR);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            form.setMessage(Message.EDIT_FRAMEWORK_ERROR, 
                    Message.EDIT_FRAMEWORK_ERROR, Message.ERROR);
            return false;
        }
        
        return true;
    }
    
    private void setFormInfoOnRequest(ManageCustomerForm form) {
    	this.getRequest().setAttribute("pageMessage", form.getMessage());
    	this.getRequest().setAttribute("customerProfileData", form.getCustomerProfile());
    	this.getRequest().setAttribute("licneseNodeData" , form.getLicenseNode());
    }


 /////////////////////////////////////////////////////////////////////////////////////////////
    /////// *********************** ManageUploadForm ************* ////////////////////////////////    
    /////////////////////////////////////////////////////////////////////////////////////////////    



    
    /**
     * FormData get and set methods may be overwritten by the Form Bean editor.
     */
    public static class ManageCustomerForm extends SanitizedFormData
    {
        private String selectedCustomerName;
        private Integer selectedCustomerId;
        private String actionElement;
        private String currentAction;
        // collapsible sections        
        private Boolean byCustomerProfileVisible = new Boolean ("true");
        private Boolean byCustomerBillingVisible;
        private Boolean byCustomerMailingVisible;
        private Message message;
        private CustomerProfileInformation customerProfile;
        // customer pager
        private String customerSortColumn;
        private String customerSortOrderBy;
        private Integer customerPageRequested;
        private Integer customerMaxPage;
       
        
        //added for LM12
        private  LicenseNode licenseNode;
        private String enableLicense;

        public ManageCustomerForm()
        {
        }
        
        public void init(String action) {
            this.actionElement = global.Global.ACTION_DEFAULT;
            this.currentAction = global.Global.ACTION_DEFAULT;
            clearSearch();
            this.selectedCustomerId = null;
            this.selectedCustomerName = null;
            this.customerSortColumn = FilterSortPageUtils.CUSTOMER_DEFAULT_SORT_COLUMN;
            this.customerSortOrderBy = FilterSortPageUtils.ASCENDING;      
            this.customerPageRequested = new Integer(1);       
            this.customerMaxPage = new Integer(1);   
            this.byCustomerProfileVisible = Boolean.TRUE;
            this.byCustomerBillingVisible = Boolean.FALSE;
            this.byCustomerMailingVisible = Boolean.FALSE;
            this.customerProfile = new CustomerProfileInformation();
            this.message = new Message();           
           
            if (!action.equals(global.Global.ACTION_FIND_CUSTOMER)) {
         
                this.customerProfile.setState(Message.SELECT_STATE);            
         
            }
                         
        }
        
         // validate values
        public void validateValues() {
            if ( this.customerSortColumn == null ) {
                
                this.customerSortColumn = 
                                FilterSortPageUtils.CUSTOMER_DEFAULT_SORT_COLUMN;
            
            }

            if ( this.customerSortOrderBy == null ) {
                
                this.customerSortOrderBy = FilterSortPageUtils.ASCENDING;
            
            }

            if ( this.customerPageRequested == null ) {
            
                this.customerPageRequested = new Integer(1);
            
            }
                
            if ( this.customerPageRequested.intValue() <= 0 ) {            
               
                this.customerPageRequested = new Integer(1);
            
            }

            if ( this.customerMaxPage == null ) {
                
                this.customerMaxPage = new Integer(1);
            
            }

            if ( this.customerPageRequested.intValue() > this.customerMaxPage.intValue() ) {
                
                this.customerPageRequested = new Integer(this.customerMaxPage.intValue());                
                this.selectedCustomerId = null;
                this.selectedCustomerName=null;
            
            }
        }    
        
         // reset values based on action
        public void resetValuesForAction(String actionElement, 
                                        String fromAction) {
            if ( actionElement.equals("{actionForm.customerSortOrderBy}") ) {
                
                this.customerPageRequested = new Integer(1);
            
            }
            
            if ( actionElement.equals("ButtonGoInvoked_customerSearchResult") 
                    || actionElement.equals("EnterKeyInvoked_customerSearchResult") ) {
                
                this.selectedCustomerId = null;
                this.selectedCustomerName = null;
            
            }
            if ( actionElement.equals("ButtonGoInvoked_tablePathListAnchor") 
                    || actionElement.equals("EnterKeyInvoked_tablePathListAnchor") ) {
            
                if ( fromAction.equals(global.Global.ACTION_FIND_CUSTOMER) ) {
                
                    this.selectedCustomerId = null;
                    this.selectedCustomerName = null;
                
                }
            }
        }
        
        
        public ManageCustomerForm createClone() {   
            ManageCustomerForm copied = new ManageCustomerForm();
            copied.setActionElement(this.actionElement);
            copied.setCurrentAction(this.currentAction);
            copied.setSelectedCustomerId(this.selectedCustomerId);
            copied.setSelectedCustomerName(this.selectedCustomerName);
            copied.setCustomerSortColumn(this.customerSortColumn);
            copied.setCustomerSortOrderBy(this.customerSortOrderBy);
            copied.setCustomerPageRequested(this.customerPageRequested);
            copied.setCustomerMaxPage(this.customerMaxPage);
            copied.setCustomerProfile(this.customerProfile);
            copied.setByCustomerProfileVisible(this.byCustomerProfileVisible);
            copied.setByCustomerBillingVisible(this.byCustomerBillingVisible);
            copied.setByCustomerMailingVisible(this.byCustomerMailingVisible);
            copied.setMessage(this.message);
            
            return copied;
        }
        
        
        
        // clear search 
        public void clearSearch() {   
            this.customerProfile = new CustomerProfileInformation();
            this.customerProfile.setState("");
        }
        
        public String getEnableLicense() {
            return enableLicense;
        }
        
        public void setEnableLicense(String enableLicense) {
            this.enableLicense = enableLicense;
        }
        
         // clear message
        public void clearMessage() {   
            this.message = null;
        }
        
        public void setActionElement(String actionElement)
        {
            this.actionElement = actionElement;
        }   
             
        public String getActionElement()
        {
            return this.actionElement != null ? this.actionElement : global.Global.ACTION_DEFAULT;
        }
        
        public void setCurrentAction(String currentAction)
        {
            this.currentAction = currentAction;
        }
        
        public String getCurrentAction()
        {
            return this.currentAction != null ? this.currentAction : global.Global.ACTION_DEFAULT;
        }
        
        public void setByCustomerProfileVisible(Boolean byCustomerProfileVisible)
        {
            this.byCustomerProfileVisible = byCustomerProfileVisible;
        }
        
        public Boolean getByCustomerProfileVisible()
        {
            return this.byCustomerProfileVisible != null ? 
                                        this.byCustomerProfileVisible : Boolean.FALSE; 
        }  
              
        public void setByCustomerBillingVisible(Boolean byCustomerBillingVisible)
        {
            this.byCustomerBillingVisible = byCustomerBillingVisible;
        }
        
        public Boolean getByCustomerBillingVisible()
        {
            return this.byCustomerBillingVisible != null ?
                                        this.byCustomerBillingVisible : Boolean.FALSE; 
        }        
        
        public void setByCustomerMailingVisible(Boolean byCustomerMailingVisible)
        {
            this.byCustomerMailingVisible = byCustomerMailingVisible;
        }
        
        public Boolean getByCustomerMailingVisible()
        {
            return this.byCustomerMailingVisible != null ? 
                                        this.byCustomerMailingVisible : Boolean.FALSE; 
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

        public void setSelectedCustomerId(Integer selectedCustomerId)
        {
            this.selectedCustomerId = selectedCustomerId;
        }

        public Integer getSelectedCustomerId()
        {
            return this.selectedCustomerId;
        }

        public void setSelectedCustomerName(String selectedCustomerName)
        {
            this.selectedCustomerName = selectedCustomerName;
        }

        public String getSelectedCustomerName()
        {
            return this.selectedCustomerName;
        }
         // user profile
        public void setCustomerProfile(CustomerProfileInformation customerProfile)
        {
            this.customerProfile = customerProfile;
        }
        
        public CustomerProfileInformation getCustomerProfile()
        {
            if (this.customerProfile == null) {
                this.customerProfile = new CustomerProfileInformation();
            }
            
            return this.customerProfile;
        }
        
        public void setCustomerPageRequested(Integer customerPageRequested)
        {
            this.customerPageRequested = customerPageRequested;
        }
        
        public Integer getCustomerPageRequested()
        {
            return this.customerPageRequested != null 
                    ? this.customerPageRequested 
                    : new Integer(1);
        }
        
        public void setCustomerSortColumn(String customerSortColumn)
        {
            this.customerSortColumn = customerSortColumn;
        }
        
        public String getCustomerSortColumn()
        {
            return this.customerSortColumn != null 
                    ? this.customerSortColumn 
                    : FilterSortPageUtils.CUSTOMER_DEFAULT_SORT_COLUMN;
        }
        
        public void setCustomerSortOrderBy(String customerSortOrderBy)
        {
            this.customerSortOrderBy = customerSortOrderBy;
        }
        
        public String getCustomerSortOrderBy() {
            return this.customerSortOrderBy != null 
                    ? this.customerSortOrderBy 
                    : FilterSortPageUtils.ASCENDING;
        }
        
        public void setCustomerMaxPage(Integer customerMaxPage)
        {
            this.customerMaxPage = customerMaxPage;
        }
        
        public Integer getCustomerMaxPage() {
            return this.customerMaxPage != null 
                    ? this.customerMaxPage : new Integer(1);
        }
        
        public void clearSectionVisibility() {   
            this.byCustomerProfileVisible = Boolean.FALSE;
            this.byCustomerBillingVisible = Boolean.FALSE;
            this.byCustomerMailingVisible = Boolean.FALSE;
            
        }
        
        public String getStringAction() {
            String action = global.Global.ACTION_ADD_CUSTOMER;
           
            if ( (this.selectedCustomerName != null) 
                     && (!this.selectedCustomerName.equals("")) ) {
                
                action = global.Global.ACTION_EDIT_CUSTOMER;
            }
            return action; 
                
        } 
        /**
         * @return the licenseNode
         */
        public LicenseNode getLicenseNode() {
            
            if (licenseNode == null) {
                
                this.licenseNode = new LicenseNode ();
            }
            return this.licenseNode;
        }
    
        /**
         * @param licenseNode the licenseNode to set
         */
        public void setLicenseNode(LicenseNode licenseNode) {
            this.licenseNode = licenseNode;
        }                 
    }


	public String getPageTitle() {
		return pageTitle;
	}

	public LinkedHashMap getStateOptions() {
		return stateOptions;
	}

	public String getPageMessage() {
		return pageMessage;
	}

	public LinkedHashMap getBillingStateOptions() {
		return billingStateOptions;
	}

	public LinkedHashMap getMailingStateOptions() {
		return mailingStateOptions;
	}

	public LinkedHashMap getCustomerOptions() {
		return customerOptions;
	}
    
    
    
}
