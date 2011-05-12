package com.ctb.control.db; 

import java.sql.SQLException;

import org.apache.beehive.controls.api.bean.ControlExtension;
import org.apache.beehive.controls.system.jdbc.JdbcControl;

import com.ctb.bean.testAdmin.CustomerLicense;
import com.ctb.bean.testAdmin.TestProduct;



/** 
 * Defines a new database control. 
 * 
 * The @jc:connection tag indicates which WebLogic data source will be used by 
 * this database control. Please change this to suit your needs. You can see a 
 * list of available data sources by going to the WebLogic console in a browser 
 * (typically http://localhost:7001/console) and clicking Services, JDBC, 
 * Data Sources. 
 * 
 * @jc:connection data-source-jndi-name="oasDataSource" 
 */ 
@ControlExtension()
@JdbcControl.ConnectionDataSource(jndiName = "oasDataSource")
public interface License extends JdbcControl
{ 
    // Sample database function.  Uncomment to use 

    // static public class Customer 
    // { 
    //   public int id; 
    //   public String name; 
    // } 
    // 
    // /** 
    //  * @jc:sql statement="SELECT ID, NAME FROM CUSTOMERS WHERE ID = {id}" 
    //  */ 
    // Customer findCustomer(int id);

    // Add "throws SQLException" to request that SQLExeptions be thrown on errors.

    static final long serialVersionUID = 1L;

    /**
     * @jc:sql statement::
     * SELECT COUNT(tr.test_roster_id)
    
     * FROM 
     * test_roster tr,
     * org_node_ancestor ona,
     * test_admin adm,
     * product prod
 
     * WHERE
     * tr.org_node_id = ona.org_node_id
     * AND ona.ancestor_org_node_id = {orgId}
     * AND (prod.product_id = {productId} OR prod.parent_product_id = {productId})
     * AND prod.license_enabled = 'T'
     * AND tr.test_admin_id = adm.test_admin_id
     * AND adm.product_id = prod.product_id
     * AND tr.test_completion_status NOT IN ('NT','SC')
     * AND exists (select siss.* from item_set iset, student_item_Set_status siss
     *  where iset.sample = 'F' and iset.item_set_level != 'L' and iset.item_Set_id = siss.item_set_id and siss.test_Roster_id = tr.test_roster_id
     *  and siss.completion_status not in ('SC', 'NT'))::
     */
    @JdbcControl.SQL(statement = "SELECT COUNT(tr.test_roster_id)  FROM  test_roster tr, org_node_ancestor ona, test_admin adm, product prod  WHERE tr.org_node_id = ona.org_node_id AND ona.ancestor_org_node_id = {orgId} AND (prod.product_id = {productId} OR prod.parent_product_id = {productId}) AND prod.license_enabled = 'T' AND tr.test_admin_id = adm.test_admin_id AND adm.product_id = prod.product_id AND tr.test_completion_status NOT IN ('NT','SC') AND exists (select siss.* from item_set iset, student_item_Set_status siss where iset.sample = 'F' and iset.item_set_level != 'L' and iset.item_Set_id = siss.item_set_id and siss.test_Roster_id = tr.test_roster_id and siss.completion_status not in ('SC', 'NT'))")
    Integer getConsumedQuantityForOrgByRoster(Integer orgId, Integer productId) throws SQLException;

    /**
     * @jc:sql statement::
     * SELECT COUNT(tr.test_roster_id)
     * FROM 
     * test_roster tr,
     * org_node_ancestor ona,
     * test_admin adm,
     * product prod
     * WHERE
     * tr.org_node_id = ona.org_node_id
     * AND ona.ancestor_org_node_id = {orgId}
     * AND (prod.product_id = {productId} OR prod.parent_product_id = {productId})
     * AND prod.license_enabled = 'T'
     * AND tr.test_admin_id = adm.test_admin_id
     * AND adm.product_id = prod.product_id
     * AND (TR.test_completion_status = 'SC' OR (TR.test_completion_Status in ('IP', 'IN', 'IS') and not exists (select siss.* from item_set iset, student_item_Set_status siss
     *  where iset.sample = 'F' and iset.item_set_level != 'L' and iset.item_Set_id = siss.item_set_id and siss.test_Roster_id = tr.test_roster_id
     *  and siss.completion_status not in ('SC', 'NT'))))::
     */
    @JdbcControl.SQL(statement = "SELECT COUNT(tr.test_roster_id) FROM  test_roster tr, org_node_ancestor ona, test_admin adm, product prod WHERE tr.org_node_id = ona.org_node_id AND ona.ancestor_org_node_id = {orgId} AND (prod.product_id = {productId} OR prod.parent_product_id = {productId}) AND prod.license_enabled = 'T' AND tr.test_admin_id = adm.test_admin_id AND adm.product_id = prod.product_id AND (TR.test_completion_status = 'SC' OR (TR.test_completion_Status in ('IP', 'IN', 'IS') and not exists (select siss.* from item_set iset, student_item_Set_status siss where iset.sample = 'F' and iset.item_set_level != 'L' and iset.item_Set_id = siss.item_set_id and siss.test_Roster_id = tr.test_roster_id and siss.completion_status not in ('SC', 'NT'))))")
    Integer getReservedQuantityForOrgByRoster(Integer orgId, Integer productId) throws SQLException;

    /**
     * @jc:sql statement::
     *     SELECT COUNT(1) FROM( 
     *       SELECT DISTINCT tr.test_roster_id,isp.parent_item_set_id
     *       FROM 
     *       test_roster tr,
     *       org_node_ancestor ona,
     *       test_admin adm,
     *       product prod,
     *       student_item_set_status siss,
     *       item_set iset,
     *       item_set_parent isp
     *       WHERE
     *       tr.org_node_id = ona.org_node_id
     *       AND ona.ancestor_org_node_id = {orgId}
     *       AND (prod.product_id = {productId} OR prod.parent_product_id = {productId})
     *       AND prod.license_enabled = 'T'
     *       AND tr.test_admin_id = adm.test_admin_id
     *       AND adm.product_id = prod.product_id
     *       AND tr.test_completion_status NOT IN ('NT','SC')
     *       AND tr.test_roster_id = siss.test_roster_id
     *       AND siss.completion_status != 'SC'
     *       AND siss.item_set_id = iset.item_set_id
     *       AND iset.item_set_level != 'L'
     *       AND iset.SAMPLE = 'F'
     *       AND iset.item_set_id = isp.item_set_id)::
     */
    @JdbcControl.SQL(statement = "SELECT COUNT(1) FROM(  SELECT DISTINCT tr.test_roster_id,isp.parent_item_set_id  FROM  test_roster tr,  org_node_ancestor ona,  test_admin adm,  product prod,  student_item_set_status siss,  item_set iset,  item_set_parent isp  WHERE  tr.org_node_id = ona.org_node_id  AND ona.ancestor_org_node_id = {orgId}  AND (prod.product_id = {productId} OR prod.parent_product_id = {productId})  AND prod.license_enabled = 'T'  AND tr.test_admin_id = adm.test_admin_id  AND adm.product_id = prod.product_id  AND tr.test_completion_status NOT IN ('NT','SC')  AND tr.test_roster_id = siss.test_roster_id  AND siss.completion_status != 'SC'  AND siss.item_set_id = iset.item_set_id  AND iset.item_set_level != 'L'  AND iset.SAMPLE = 'F'  AND iset.item_set_id = isp.item_set_id)")
    Integer getConsumedQuantityforOrgBySubTest(Integer orgId, Integer productId) throws SQLException;

    /**
     * @jc:sql statement::
     *     SELECT count(1) FROM 
     *    (SELECT DISTINCT isp.parent_item_set_id, tr.test_roster_id
     *     
     *       FROM 
     *       test_roster tr,
     *       org_node_ancestor ona,
     *       test_admin adm,
     *       product prod,
     *       student_item_set_status siss,
     *       item_set iset,
     *       item_set_parent isp
     *     
     *       WHERE
     *       tr.org_node_id = ona.org_node_id
     *       AND ona.ancestor_org_node_id = {orgId}
     *       AND (prod.product_id = {productId} OR prod.parent_product_id = {productId})
     *       AND prod.license_enabled = 'T'
     *       AND tr.test_admin_id = adm.test_admin_id
     *       AND adm.product_id = prod.product_id
     *       AND tr.test_completion_status NOT IN ('NT','IC','CO')
     *       AND tr.test_roster_id = siss.test_roster_id
     *       AND siss.completion_status = 'SC'
     *       AND siss.item_set_id = iset.item_set_id
     *       AND iset.item_set_level != 'L'
     *       AND iset.SAMPLE = 'F'
     *       AND iset.item_set_id = isp.item_set_id)::
     */
    @JdbcControl.SQL(statement = "SELECT count(1) FROM  (SELECT DISTINCT isp.parent_item_set_id, tr.test_roster_id  FROM  test_roster tr,  org_node_ancestor ona,  test_admin adm,  product prod,  student_item_set_status siss,  item_set iset,  item_set_parent isp  WHERE  tr.org_node_id = ona.org_node_id  AND ona.ancestor_org_node_id = {orgId}  AND (prod.product_id = {productId} OR prod.parent_product_id = {productId})  AND prod.license_enabled = 'T'  AND tr.test_admin_id = adm.test_admin_id  AND adm.product_id = prod.product_id  AND tr.test_completion_status NOT IN ('NT','IC','CO')  AND tr.test_roster_id = siss.test_roster_id  AND siss.completion_status = 'SC'  AND siss.item_set_id = iset.item_set_id  AND iset.item_set_level != 'L'  AND iset.SAMPLE = 'F'  AND iset.item_set_id = isp.item_set_id)")
    Integer getReservedQuantityForOrgBySubTest(Integer orgId, Integer productId) throws SQLException;
     
    /**
     * @jc:sql statement::
     * select cplicense.CUSTOMER_ID           as customerId,
     *         cplicense.PRODUCT_ID           as productId,
     *         cplicense.AVAILABLE            as available,
     *         cplicense.RESERVED             as reservedLicense,
     *         cplicense.CONSUMED             as consumedLicense,
     *         cplicense.SUBTEST_MODEL        as subtestModel, 
     *         cplicense.LICENSE_PERIOD_START as licenseperiodStartdate,
     *         cplicense.LICENSE_PERIOD_END as licenseperiodEnd,
     *         cplicense.LICENSE_AFTER_LAST_PURCHASE  AS licenseAfterLastPurchase,
     *         prod.product_description as productName
     *   from customer_product_license cplicense , product prod
     *   where  prod.product_id = cplicense.PRODUCT_ID and 
     *   cplicense.CUSTOMER_ID = {customerId}::
     */
    @JdbcControl.SQL(statement = "select cplicense.CUSTOMER_ID  as customerId,  cplicense.PRODUCT_ID  as productId,  cplicense.AVAILABLE  as available,  cplicense.RESERVED  as reservedLicense,  cplicense.CONSUMED  as consumedLicense,  cplicense.SUBTEST_MODEL  as subtestModel,  cplicense.LICENSE_PERIOD_START as licenseperiodStartdate,  cplicense.LICENSE_PERIOD_END as licenseperiodEnd,  cplicense.LICENSE_AFTER_LAST_PURCHASE  AS licenseAfterLastPurchase,  prod.product_description as productName  from customer_product_license cplicense , product prod  where  prod.product_id = cplicense.PRODUCT_ID and  cplicense.CUSTOMER_ID = {customerId}")
    CustomerLicense[] getCustomerLicenseDetails(Integer customerId) throws SQLException;

    /**
     * @jc:sql statement::
     * SELECT cplicense.CUSTOMER_ID          AS customerId,
     *        cplicense.PRODUCT_ID           AS productId,
     *        cplicense.AVAILABLE            AS available,
     *        cplicense.RESERVED             AS reservedLicense,
     *        cplicense.CONSUMED             AS consumedLicense,
     *        cplicense.SUBTEST_MODEL        AS subtestModel,
     *        cplicense.LICENSE_PERIOD_START AS licenseperiodStartdate,
     *        cplicense.LICENSE_PERIOD_END   AS licenseperiodEnd,
     *        cplicense.LICENSE_AFTER_LAST_PURCHASE  AS licenseAfterLastPurchase,
     *        prod.product_description       AS productName
     *   FROM customer_product_license cplicense,
     *        product                  prod
     *  WHERE prod.product_id = cplicense.PRODUCT_ID
     *    AND cplicense.CUSTOMER_ID = {customerId}
     *    AND cplicense.PRODUCT_ID = {productId}
     *    AND prod.LICENSE_ENABLED='T'
     * 
     * UNION
     * 
     * SELECT cplicense.CUSTOMER_ID          AS customerId,
     *        cplicense.PRODUCT_ID           AS productId,
     *        cplicense.AVAILABLE            AS available,
     *        cplicense.RESERVED             AS reservedLicense,
     *        cplicense.CONSUMED             AS consumedLicense,
     *        cplicense.SUBTEST_MODEL        AS subtestModel,
     *        cplicense.LICENSE_PERIOD_START AS licenseperiodStartdate,
     *        cplicense.LICENSE_PERIOD_END   AS licenseperiodEnd,
     *        cplicense.LICENSE_AFTER_LAST_PURCHASE  AS licenseAfterLastPurchase,
     *        prod.product_description       AS productName
     *   FROM customer_product_license cplicense,
     *        product                  prod
     *  WHERE prod.product_id = cplicense.PRODUCT_ID
     *    AND cplicense.PRODUCT_ID =
     *        (SELECT pr.parent_product_id
     *           FROM product pr
     *          WHERE pr.product_id = {productId}
	 *			AND pr.LICENSE_ENABLED='T')
     *    AND cplicense.CUSTOMER_ID = {customerId}
     *    AND prod.LICENSE_ENABLED='T'
     *    AND NOT EXISTS (SELECT cplicense.CUSTOMER_ID          AS customerId,
     *                           cplicense.PRODUCT_ID           AS productId
     *                    FROM customer_product_license cplicense,
     *                          product                  prod
     *                    WHERE prod.product_id = cplicense.PRODUCT_ID
     *                          AND cplicense.CUSTOMER_ID = {customerId}
     *                          AND cplicense.PRODUCT_ID = {productId})::
     */
    @JdbcControl.SQL(statement = "SELECT cplicense.CUSTOMER_ID  AS customerId,  cplicense.PRODUCT_ID  AS productId,  cplicense.AVAILABLE  AS available,  cplicense.RESERVED  AS reservedLicense,  cplicense.CONSUMED  AS consumedLicense,  cplicense.SUBTEST_MODEL  AS subtestModel,  cplicense.LICENSE_PERIOD_START AS licenseperiodStartdate,  cplicense.LICENSE_PERIOD_END  AS licenseperiodEnd,  cplicense.LICENSE_AFTER_LAST_PURCHASE  AS licenseAfterLastPurchase,  prod.product_description  AS productName  FROM customer_product_license cplicense,  product  prod  WHERE prod.product_id = cplicense.PRODUCT_ID  AND cplicense.CUSTOMER_ID = {customerId}  AND cplicense.PRODUCT_ID = {productId}  AND prod.LICENSE_ENABLED='T'  UNION  SELECT cplicense.CUSTOMER_ID  AS customerId,  cplicense.PRODUCT_ID  AS productId,  cplicense.AVAILABLE  AS available,  cplicense.RESERVED  AS reservedLicense,  cplicense.CONSUMED  AS consumedLicense,  cplicense.SUBTEST_MODEL  AS subtestModel,  cplicense.LICENSE_PERIOD_START AS licenseperiodStartdate,  cplicense.LICENSE_PERIOD_END  AS licenseperiodEnd,  cplicense.LICENSE_AFTER_LAST_PURCHASE  AS licenseAfterLastPurchase,  prod.product_description  AS productName  FROM customer_product_license cplicense,  product  prod  WHERE prod.product_id = cplicense.PRODUCT_ID  AND cplicense.PRODUCT_ID =  (SELECT pr.parent_product_id  FROM product pr  WHERE pr.product_id = {productId} \t\t\tAND pr.LICENSE_ENABLED='T')  AND cplicense.CUSTOMER_ID = {customerId}  AND prod.LICENSE_ENABLED='T'  AND NOT EXISTS (SELECT cplicense.CUSTOMER_ID  AS customerId,  cplicense.PRODUCT_ID  AS productId  FROM customer_product_license cplicense,  product  prod  WHERE prod.product_id = cplicense.PRODUCT_ID  AND cplicense.CUSTOMER_ID = {customerId}  AND cplicense.PRODUCT_ID = {productId})")
    CustomerLicense[] getProductLicenseDetails(Integer customerId, Integer productId) throws SQLException;


    /**
     * @jc:sql statement::
     *  select    
     *  decode( count(cpl.customer_id),0,0,1) 
     *  from customer_product_license cpl
     *  where cpl.customer_id = {customerId}
     *  and cpl.product_id = {productId}::
     */
    @JdbcControl.SQL(statement = "select  decode( count(cpl.customer_id),0,0,1)  from customer_product_license cpl where cpl.customer_id = {customerId} and cpl.product_id = {productId}")
    boolean isCustomerLicenseExist(Integer customerId, Integer productId) throws SQLException;

    // TABE BAUM 10:  checks whether license exists for a particular orgnode of that customer
    
    @JdbcControl.SQL(statement = "select  decode( count(col.customer_id),0,0,1)  from customer_orgnode_license col where col.customer_id = {customerId} and col.product_id = {productId} and col.org_node_id = {orgNodeId}")
    boolean isCustomerOrgNodeLicenseExist(Integer orgNodeId, Integer customerId, Integer productId) throws SQLException;

    /**
     * @jc:sql statement:: 
     * insert into customer_product_license
     *    (customer_id,
     *    product_id,
     *    available,
     *    reserved,
     *    consumed,
     *    email_notify_flag,
     *    license_after_last_purchase
     *    ) values (
     *    {customerLicense.customerId},
     *    {customerLicense.productId},
     *    {customerLicense.available},
     *    {customerLicense.reservedLicense},
     *    {customerLicense.consumedLicense},
     *    'T',
     *    {customerLicense.available}
     *    )::
     */
    @JdbcControl.SQL(statement = "insert into customer_product_license  (customer_id,  product_id,  available,  reserved,  consumed,  email_notify_flag,  license_after_last_purchase  ) values (  {customerLicense.customerId},  {customerLicense.productId},  {customerLicense.available},  {customerLicense.reservedLicense},  {customerLicense.consumedLicense},  'T',  {customerLicense.available}  )")
    void addCustomerLicense(CustomerLicense customerLicense) throws SQLException;

    /**
     * @jc:sql statement::
     * update customer_product_license
     * set available = {customerLicense.available},
     * consumed = {customerLicense.consumedLicense},
     * email_notify_flag = 'T',
     * license_after_last_purchase = {customerLicense.available}
     * where customer_id = {customerLicense.customerId} and 
     * product_id  = {customerLicense.productId}::
     */
    @JdbcControl.SQL(statement = "update customer_product_license set available = {customerLicense.available}, consumed = {customerLicense.consumedLicense}, email_notify_flag = 'T', license_after_last_purchase = {customerLicense.available} where customer_id = {customerLicense.customerId} and  product_id  = {customerLicense.productId}")
    void updateCustomerLicensewithAvailableChange(CustomerLicense customerLicense) throws SQLException;

    /**
     * @jc:sql statement::
     * update customer_product_license
     * set consumed          = {customerLicense.consumedLicense}
     * where customer_id = {customerLicense.customerId} and 
     * product_id  = {customerLicense.productId}::
     */
    @JdbcControl.SQL(statement = "update customer_product_license set consumed  = {customerLicense.consumedLicense} where customer_id = {customerLicense.customerId} and  product_id  = {customerLicense.productId}")
    void updateCustomerLicensewithoutAvailableChange(CustomerLicense customerLicense) throws SQLException;

    /**
     * @jc:sql statement::
     * select distinct
     * parentProduct.product_id as productId,
     * parentProduct.Product_Description as productName,
     * parentProduct.license_enabled as productLicenseEnabled
     * from
     * product prod,
     * test_catalog cat,
     * org_node_test_catalog ontc,
     * product parentProduct
     * where
     * prod.activation_status = 'AC'
     * and prod.product_id = cat.product_id
     * and parentProduct.product_id = prod.parent_product_id
     * and cat.activation_status = 'AC'
     * and cat.test_catalog_id = ontc.test_catalog_id
     * and ontc.activation_status = 'AC'
     * and ontc.customer_id={customerId} 
     * ::
     */
    @JdbcControl.SQL(statement = "select distinct parentProduct.product_id as productId, parentProduct.Product_Description as productName, parentProduct.license_enabled as productLicenseEnabled from product prod, test_catalog cat, org_node_test_catalog ontc, product parentProduct where prod.activation_status = 'AC' and prod.product_id = cat.product_id and parentProduct.product_id = prod.parent_product_id and cat.activation_status = 'AC' and cat.test_catalog_id = ontc.test_catalog_id and ontc.activation_status = 'AC' and ontc.customer_id={customerId} ")
    TestProduct[] getParentProductId (Integer customerId) throws SQLException;
    
    // TABE BAUM 10: For calculation of total license 
    
     @JdbcControl.SQL(arrayMaxLength = 100000,statement = "select sum(col.available) as available,sum(col.consumed) as consumedLicense, sum(col.reserved) as reservedLicense,prd.product_id as productId,prd.product_name as productName,col.subtest_model as subtestModel from customer_orgnode_license col,product prd,org_node node,user_role role,users where prd.product_id = col.product_id and col.org_node_id = role.org_node_id and role.org_node_id = node.org_node_id and role.activation_status = 'AC' and node.activation_status = 'AC' and role.user_id = users.user_id and users.user_name = {username} group by prd.product_id, prd.product_name, col.subtest_model ")
     CustomerLicense [] getUserOrgNodeLicenseDetails(String username) throws SQLException;
     
  // TABE BAUM 10: For calculation of total license
     
     @JdbcControl.SQL(arrayMaxLength = 100000,statement = "select sum(col.available) as available,  sum(col.consumed) as consumedLicense,  sum(col.reserved) as reservedLicense,  prd.product_id as productId,  prd.product_name as productName,  col.subtest_model as subtestModel    from customer_orgnode_license col,  product prd,  org_node node,  user_role role,  users   where col.product_id = {productId}     and prd.product_id = col.product_id     and col.org_node_id = role.org_node_id     and role.org_node_id = node.org_node_id     and role.activation_status = 'AC'     and node.activation_status = 'AC'     and role.user_id = users.user_id     and users.user_name = {userName}   group by prd.product_id, prd.product_name, col.subtest_model   UNION   select sum(col.available) as available,  sum(col.consumed) as consumedLicense,  sum(col.reserved) as reservedLicense,  prd.product_id as productId,  prd.product_name as productName,  col.subtest_model as subtestModel    from customer_orgnode_license col,  product prd,  org_node node,  user_role role,  users   where col.product_id = (SELECT pr.parent_product_id FROM product pr WHERE pr.product_id = {productId}  AND pr.LICENSE_ENABLED = 'T')     and prd.product_id = col.product_id     and col.org_node_id = role.org_node_id     and role.org_node_id = node.org_node_id     and role.activation_status = 'AC'     and node.activation_status = 'AC'     and role.user_id = users.user_id     and users.user_name = {userName}      AND NOT EXISTS       (SELECT cplicense.CUSTOMER_ID AS customerId,       cplicense.PRODUCT_ID  AS productId  FROM customer_orgnode_license cplicense, product prod WHERE prod.product_id = cplicense.PRODUCT_ID   AND cplicense.CUSTOMER_ID = {customerId}   AND cplicense.PRODUCT_ID = {productId})   group by prd.product_id, prd.product_name, col.subtest_model")
     CustomerLicense [] getSelectedProductLicenseDetails(String userName,Integer productId,Integer customerId) throws SQLException;

  // TABE BAUM 10: For retriving  License Data from orgnodeid and productid
     
     @JdbcControl.SQL(arrayMaxLength = 100000,statement = "select available, consumed as consumedLicense ,reserved  as reservedLicense from customer_orgnode_license where org_node_id = {orgNodeId} and product_id= {productId}")
     CustomerLicense [] getOrgnodeLicenseDetails(Integer orgNodeId,Integer productId) throws SQLException;

  // TABE BAUM 10: For updating the edited available license field value in manage license page
     
     @JdbcControl.SQL(statement = "update customer_orgnode_license set available = {customerLicense.available} where org_node_id = {orgNodeId} and product_id = {customerLicense.productId} and customer_id = {customerLicense.customerId}")
     void updateAvailableLicenseChange(CustomerLicense customerLicense,Integer orgNodeId) throws SQLException;
  
   // TABE BAUM 10 : Inserting license details into database for a particular organization who's entry is not there in the database table
     
     @JdbcControl.SQL(statement = "insert into customer_orgnode_license  (org_node_id ,customer_id,  product_id,  available,  reserved,  consumed, subtest_model) values ({orgNodeId},{customerLicense.customerId},  {customerLicense.productId},  {customerLicense.available},  {customerLicense.reservedLicense},  {customerLicense.consumedLicense}, {customerLicense.subtestModel}  ) ")
     void addOrgNodeLicenses(CustomerLicense customerLicense,Integer orgNodeId) throws SQLException;
  
     // TABE BAUM 10:  For calculating the total consumed and reserved quantity by using ancestor orgnodeid 
     
     @JdbcControl.SQL(arrayMaxLength = 100000,statement = "select sum(col.consumed) as consumedLicense, sum(col.reserved) as reservedLicense from customer_orgnode_license col where  col.product_id = {productId}  and col.org_node_id in (select ona.org_node_Id    from org_node_ancestor ona, org_node org   where ona.ancestor_org_node_id = {orgNodeId} and org.org_node_id = ona.org_node_id)")
     CustomerLicense [] getTotalConsumedReservedQuantityByAncestorOrgNode(Integer orgNodeId,Integer productId) throws SQLException;

}

