package com.ctb.control.licensing; 

import java.io.Serializable;
import java.sql.SQLException;

import org.apache.beehive.controls.api.bean.ControlImplementation;

import com.ctb.bean.testAdmin.Customer;
import com.ctb.bean.testAdmin.CustomerLicense;
import com.ctb.bean.testAdmin.OrgNodeLicenseInfo;
import com.ctb.bean.testAdmin.TestProduct;
import com.ctb.exception.CTBBusinessException;
import com.ctb.exception.licensing.LicenseCreationException;
import com.ctb.exception.licensing.LicenseUpdationException;
import com.ctb.exception.licensing.OrgLicenseDataNotFoundException;
import com.ctb.exception.validation.ValidationException;
import com.ctb.util.licensing.CTBConstants;


/**
 * @editor-info:code-gen control-interface="true"
 */
@ControlImplementation()
public class LicensingImpl implements Licensing, Serializable
{ 
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.validation.Validator validator;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.Customer customers;

    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.License license;
   
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.Users users;
    

    static final long serialVersionUID = 1L;

    /**
     * @common:operation
     */
    public int reserveLicense(Integer testRosterId) throws CTBBusinessException
    {
        return 0;
    }
    
    /**
     * @common:operation
     */
    public int releaseLicense(Integer testRosterId) throws CTBBusinessException
    {
        return 0;
    }
    
    /**
     * @common:operation
     */
    public int retrieveLicense(Integer testRosterId) throws CTBBusinessException
    {
        return 0;
    }

    /**
     * @common:operation
     */
    public OrgNodeLicenseInfo getLicenseQuantitiesByOrg(String userName, Integer orgNodeId, Integer productId, String subtestFlag) throws CTBBusinessException
    {
        try {
            validator.validateNode(userName, orgNodeId, "LicesingImpl.getLicenseQuantitiesByOrg");
        } catch (ValidationException ve) {
            throw ve;
        }
        try{
            OrgNodeLicenseInfo orgNodeLicenseInfo = new OrgNodeLicenseInfo();
            Integer reservedCount = null;
            Integer consumedCount = null;
            
            orgNodeLicenseInfo.setOrgNodeId(orgNodeId);
            
            if(subtestFlag.equals(CTBConstants.TRUE_INITIAL)){
                reservedCount = license.getReservedQuantityForOrgBySubTest(orgNodeId, productId);
                consumedCount = license.getConsumedQuantityforOrgBySubTest(orgNodeId, productId);
                
                orgNodeLicenseInfo.setLicReserved(reservedCount);
                orgNodeLicenseInfo.setLicUsed(consumedCount);
            }
            else if(subtestFlag.equals(CTBConstants.FALSE_INITIAL)){
                reservedCount = license.getReservedQuantityForOrgByRoster(orgNodeId, productId);
                consumedCount = license.getConsumedQuantityForOrgByRoster(orgNodeId, productId);
                orgNodeLicenseInfo.setLicReserved(reservedCount);
                orgNodeLicenseInfo.setLicUsed(consumedCount);  
            }
            return orgNodeLicenseInfo;
        }
        catch(SQLException se){
            OrgLicenseDataNotFoundException lde = new OrgLicenseDataNotFoundException("LicensingImpl:getLicenseQuantitiesByOrg failed: " + se.getMessage());
            throw lde;
        }
    }
    
     /**
     * This method is resposible to retrive customer license details by
     * passing userName and productId
     * @common:operation
     * @param String - userName
     * @param Integer - productId
     * @return CustomerLicense[]
     * @throws CTBBusinessException
     */
     
    public CustomerLicense[] getCustomerLicenseData(String userName, Integer productId) 
            throws CTBBusinessException {
         try {   
            
             Customer customer = users.getCustomer(userName);  
             Integer customerId = customer.getCustomerId();
                
             CustomerLicense[] customerLicense  = null;  
             validator.validateCustomer(userName, customerId, 
                        "LicensingImpl.getCustomerLicenseData");      
            
            if ( productId != null && productId.intValue() != 0 ) {
                        
                validator.validateProduct(userName, productId, 
                        "LicensingImpl.getCustomerLicenseData" );
                        
                customerLicense =  this.license
                        .getProductLicenseDetails(customerId,productId);
                        
                
            } else  {
                
                customerLicense =  this.license.getCustomerLicenseDetails(customerId);
            }
            
            return customerLicense;  
                      
        } catch (SQLException e ) {
            
            OrgLicenseDataNotFoundException lde = 
                    new OrgLicenseDataNotFoundException("platformlicence.getCustomerLicenseData.E001");
                    
            throw lde;
            
        }
        
    }
    
    /**
     * This method is resposible to retrive customer license details by
     * passing customerId and productId
     * @common:operation
     * @param Integer - customerId
     * @param Integer - productId
     * @return CustomerLicense
     * @throws CTBBusinessException
     */
     
    public CustomerLicense getCustomerLicenses (Integer customerId) 
            throws CTBBusinessException {
                
         try {   
            
                             
             CustomerLicense []customerLicense  = null;  
             Customer customer = customers.getCustomerDetails(customerId);
             String customerName = customer.getCustomerName();
             TestProduct product = this.license.getParentProductId(customerId)[0]; 
             Integer productId =  product.getProductId();     
             String productName = product.getProductName();   
              
            if ( productId != null && productId.intValue() != 0 ) {
                        
                customerLicense =  this.license
                        .getProductLicenseDetails(customerId,productId);
             
            } 
            
            if (customerLicense != null && customerLicense.length > 0 ) {
                customerLicense[0].setCustomerName(customerName);
                return customerLicense[0];
                
            } else {
                
                CustomerLicense customerLic = new CustomerLicense ();
                customerLic.setCustomerId(customerId);
                customerLic.setProductId(productId);
                customerLic.setProductName(productName);
                customerLic.setCustomerName(customerName);
                return customerLic;
                
            }
                    
        } catch (SQLException e ) {
            
            OrgLicenseDataNotFoundException lde = 
                    new OrgLicenseDataNotFoundException("platformlicence.addCustomerLicenses.E0014");
                    
            throw lde;
            
        }
        
    }
    
    /**
     * This method is resposible to save or update customer license details.
     * If the available license value has been changed from UI then
     * corrensponding EMAIL_NOTIFY_FLAG will be changed to 'T' otherwise will be 
     * unchanged and will be added the available license into LICENSE_AFTER_LAST_PURCHASE. 
     * @common:operation
     * @param CustomerLicense - customerLicense
     * @return boolean
     * @throws CTBBusinessException
     */
     
    public boolean saveOrUpdateCustomerLicenses (CustomerLicense customerLicense) 
            throws CTBBusinessException {
         
        if (isCustomerLicenseExist (customerLicense.getCustomerId(),
                customerLicense.getProductId())) {
            
            return updateCustomerLicenses (customerLicense);
            
        } else {
            
            return addCustomerLicenses (customerLicense);
            
        }             
        
    }
    
    /**
     * This method is resposible to check the existance of customer license 
     * by passing customerId and productId
     * @return boolean
     * @param Integer - customerId
     * @param Integer - productId
     * @throws CTBBusinessException 
     */
    
    private boolean  isCustomerLicenseExist (Integer customerId, Integer productId)
            throws CTBBusinessException {
         try {
            
            return this.license.isCustomerLicenseExist (customerId, productId);
            
         } catch (SQLException se) {
            
            OrgLicenseDataNotFoundException lde = 
                    new OrgLicenseDataNotFoundException("platformlicence.isCustomerLicenseExist.E0012");
                    
            throw lde;
                     
         }       
                
    }
    
    /**
     * This method is resposible to update the customer license details by 
     * passing CustomerLicense
     * @return boolean
     * @param CustomerLicense - customerLicense
     * @throws CTBBusinessException
     */
    
    private boolean updateCustomerLicenses (CustomerLicense customerLicense) 
            throws CTBBusinessException {
        
        try {
            
            if (customerLicense.getAvailableLicenseChange()) {
                
                this.license.updateCustomerLicensewithAvailableChange(customerLicense);
                
            } else {
                
                this.license.updateCustomerLicensewithoutAvailableChange(customerLicense);
            }
           
         } catch (SQLException se) {
            
            LicenseUpdationException lue = 
                    new LicenseUpdationException("platformlicence.updateCustomerLicenses.E0014");
                    
            throw lue;
                     
         }    
         
         return true;
        
    }
    
    /**
     * This method is resposible to insert the customer license details by 
     * passing CustomerLicense
     * @return boolean
     * @param CustomerLicense - customerLicense
     * @throws CTBBusinessException
     */
    
    private boolean addCustomerLicenses (CustomerLicense customerLicense)
            throws CTBBusinessException {
                
        try {
            this.license.addCustomerLicense(customerLicense);
            
         } catch (SQLException se) {
            
            LicenseCreationException lce = 
                    new LicenseCreationException("platformlicence.addCustomerLicenses.E0014");
                    
            throw lce;
            
         }
         
         return true;    
                
    }
    
   

   
    
} 
