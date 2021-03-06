package com.ctb.control.db; 

import com.bea.control.*;
import org.apache.beehive.controls.system.jdbc.JdbcControl; 
import com.ctb.bean.studentManagement.CustomerConfiguration;
import com.ctb.bean.studentManagement.CustomerConfigurationValue;
import com.ctb.bean.studentManagement.CustomerDemographic;
import com.ctb.bean.studentManagement.CustomerDemographicValue;
import com.ctb.bean.studentManagement.ManageStudent;
import com.ctb.bean.studentManagement.OrganizationNode;
import com.ctb.bean.studentManagement.StudentDemographic;
import com.ctb.bean.studentManagement.StudentDemographicData;
import com.ctb.bean.studentManagement.StudentDemographicValue;
import com.ctb.bean.testAdmin.RosterElement;
import com.ctb.bean.testAdmin.Student;
import com.ctb.bean.testAdmin.StudentAccommodations;

import java.sql.SQLException;
import java.util.Date; 
import org.apache.beehive.controls.api.bean.ControlExtension;

/** 
 * Defines a new database control. 
 * 
 * The @jc:connection tag indicates which WebLogic data source will be used by 
 * this database control. Please change this to suit your needs. You can see a 
 * list of available data sources by going to the WebLogic console in a browser 
 * (typically http://localhost:7001/console) and clicking Services, JDBC, 
 * Data Sources. 
 *
 * @author John_Wang
 * 
 * @jc:connection data-source-jndi-name="oasDataSource" 
 */ 
@ControlExtension()
@JdbcControl.ConnectionDataSource(jndiName = "oasDataSource")
public interface StudentManagement extends JdbcControl
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
     * select 
     *      stu.student_id as studentId,
     *      stu.user_Name as userName,
     *      stu.password as password,
     *      stu.first_Name as firstName,
     *      stu.middle_Name as middleName,
     *      stu.last_Name as lastName,
     *      stu.preferred_Name as preferredName,
     *      stu.prefix as prefix,
     *      stu.suffix as suffix,
     *      stu.birthdate as birthdate,
     *      stu.gender as gender,
     *      stu.ethnicity as ethnicity,
     *      stu.email as email,
     *      stu.grade as grade,
     *      stu.ext_Elm_Id as extElmId,
     *      stu.ext_Pin1 as extPin1,
     *      stu.ext_Pin2 as extPin2,
     *      stu.ext_Pin3 as extPin3,
     *      stu.ext_School_Id as extSchoolId,
     *      stu.active_Session as activeSession,
     *      stu.potential_Duplicated_Student as potentialDuplicatedStudent,
     *      stu.created_By as createdBy,
     *      stu.created_Date_Time as createdDateTime,
     *      stu.updated_By as updatedBy,
     *      stu.updated_Date_Time as updatedDateTime,
     *      stu.activation_Status as activationStatus,
     *      stu.data_import_history_id as dataImportHistoryId
     * from
     *      student stu
     * where
     * 	 stu.student_id = {studentId}::
     */
    @JdbcControl.SQL(statement = "select  stu.student_id as studentId,  stu.user_Name as userName,  stu.password as password,  stu.first_Name as firstName,  stu.middle_Name as middleName,  stu.last_Name as lastName,  stu.preferred_Name as preferredName,  stu.prefix as prefix,  stu.suffix as suffix,  stu.birthdate as birthdate,  stu.gender as gender,  stu.ethnicity as ethnicity,  stu.email as email,  stu.grade as grade,  stu.ext_Elm_Id as extElmId,  stu.ext_Pin1 as extPin1,  stu.ext_Pin2 as extPin2,  stu.ext_Pin3 as extPin3,  stu.ext_School_Id as extSchoolId,  stu.active_Session as activeSession,  stu.potential_Duplicated_Student as potentialDuplicatedStudent,  stu.created_By as createdBy,  stu.created_Date_Time as createdDateTime,  stu.updated_By as updatedBy,  stu.updated_Date_Time as updatedDateTime,  stu.activation_Status as activationStatus,  stu.data_import_history_id as dataImportHistoryId from  student stu where \t stu.student_id = {studentId}")
    Student getStudent(int studentId) throws SQLException;
    
    /**
     * @jc:sql statement::
     * select 
     *      stu.student_id as id,
     *      stu.user_name as loginId,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     * 	    concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,
     *      stu.gender as gender,
     *      stu.birthdate as birthDate,
     *      stu.grade as grade,
     *      stu.ext_pin1 as studentIdNumber,
     *      stu.ext_pin2 as studentIdNumber2,
     *      stu.created_by as createdBy
     * from
     *      student stu
     * where
     * 	 stu.student_id = {studentId}::
     */
    @JdbcControl.SQL(statement = "select  stu.student_id as id,  stu.user_name as loginId,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName, \t  concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,  stu.gender as gender,  stu.birthdate as birthDate,  stu.grade as grade,  stu.ext_pin1 as studentIdNumber,  stu.ext_pin2 as studentIdNumber2,  stu.created_by as createdBy from  student stu where \t stu.student_id = {studentId}")
    ManageStudent getManageStudent(int studentId) throws SQLException;
    


    /**
     * @jc:sql statement::
     * select   
     *   customer_configuration_id as id,
     *   customer_configuration_name as customerConfigurationName,
     *   customer_id as customerId,
     *   editable as editable,
     *   default_value as defaultValue
     * from customer_configuration
     * where customer_id = {customerId}::
     */
    @JdbcControl.SQL(statement = "select  customer_configuration_id as id,  customer_configuration_name as customerConfigurationName,  customer_id as customerId,  editable as editable,  default_value as defaultValue from customer_configuration where customer_id = {customerId}")
    CustomerConfiguration [] getCustomerConfigurations(int customerId) throws SQLException;

    /**
     * @jc:sql statement::
     * select 
     *   customer_configuration_value as customerConfigurationValue,
     *   customer_configuration_id as customerConfigurationId,
     *   sort_order sortOrder
     * from customer_configuration_value
     * where customer_configuration_id = {customerConfigurationId}
     * order by sort_order, customer_configuration_value
     * ::
     */
    @JdbcControl.SQL(statement = "select  customer_configuration_value as customerConfigurationValue,  customer_configuration_id as customerConfigurationId,  sort_order sortOrder from customer_configuration_value where customer_configuration_id = {customerConfigurationId} order by sort_order, customer_configuration_value")
    CustomerConfigurationValue [] getCustomerConfigurationValues(int customerConfigurationId) throws SQLException;

    /**
     * @jc:sql statement::
     * select distinct customer_configuration_value as customerConfigurationValue,
     *      customer_configuration_id as customerConfigurationId,
     *      sort_order sortOrder
     * from customer_configuration_value
     * where customer_configuration_id in (
     * 	select   
     * 	  customer_configuration_id
     * 	from customer_configuration
     * 	where customer_id = {customerId}
     * 	  and upper(customer_configuration_name) = upper('Grade'))
     * order by sort_order, customer_configuration_value	  ::
     */
    @JdbcControl.SQL(statement = "select distinct customer_configuration_value as customerConfigurationValue,  customer_configuration_id as customerConfigurationId,  sort_order sortOrder from customer_configuration_value where customer_configuration_id in ( \tselect  \t  customer_configuration_id \tfrom customer_configuration \twhere customer_id = {customerId} \t  and upper(customer_configuration_name) = upper('Grade')) order by sort_order, customer_configuration_value\t  ")
    CustomerConfigurationValue [] getCustomerConfigurationValuesForGrades(int customerId) throws SQLException;

    /**
     * @jc:sql statement::
     * select customer_demographic_id as id,
     *   customer_id as customerId,
     *   label_name as labelName,
     *   label_code as labelCode,
     *   value_cardinality as valueCardinality,
     *   sort_order as sortOrder,
     *   import_editable as importEditable,
     *   visible as visible
     * from customer_demographic
     * where customer_id = {customerId}
     * order by sort_order, label_name::
     */
    @JdbcControl.SQL(statement = "select customer_demographic_id as id,  customer_id as customerId,  label_name as labelName,  label_code as labelCode,  value_cardinality as valueCardinality,  sort_order as sortOrder,  import_editable as importEditable,  visible as visible from customer_demographic where customer_id = {customerId} order by sort_order, label_name")
    CustomerDemographic [] getCustomerDemographics(int customerId) throws SQLException;

    /**
     * @jc:sql statement::
     * select customer_demographic_id as customerDemographicId,
     *   value_name as valueName,
     *   value_code as valueCode,
     *   sort_order as sortOrder,
     *   visible as visible
     * from customer_demographic_value sdv
     * where customer_demographic_id = {customerDemographicId} 
     * order by sort_order, value_name
     * ::
     */
    @JdbcControl.SQL(statement = "select customer_demographic_id as customerDemographicId,  value_name as valueName,  value_code as valueCode,  sort_order as sortOrder,  visible as visible from customer_demographic_value sdv where customer_demographic_id = {customerDemographicId}  order by sort_order, value_name")
    CustomerDemographicValue [] getCustomerDemographicValues(int customerDemographicId) throws SQLException;


    /**
     * @jc:sql statement::
     * select customer_demographic_id as customerDemographicId,
     *   value_name as valueName,
     *   value_code as valueCode,
     *   sort_order as sortOrder,
     *   visible as visible,
     *   (select decode(count(*),0, 'false', 'true') as sddcount from student_demographic_data sdd
     *   where sdd.CUSTOMER_DEMOGRAPHIC_ID = sdv.CUSTOMER_DEMOGRAPHIC_ID
     *    and sdd.VALUE_NAME = sdv.value_name
     *    and sdd.STUDENT_ID = {studentId}) as selectedFlag
     * from customer_demographic_value sdv
     * where customer_demographic_id = {customerDemographicId} 
     * order by sort_order, value_name
     * ::
     */
    @JdbcControl.SQL(statement = "select customer_demographic_id as customerDemographicId,  value_name as valueName,  value_code as valueCode,  sort_order as sortOrder,  visible as visible,  (select decode(count(*),0, 'false', 'true') as sddcount from student_demographic_data sdd  where sdd.CUSTOMER_DEMOGRAPHIC_ID = sdv.CUSTOMER_DEMOGRAPHIC_ID  and sdd.VALUE_NAME = sdv.value_name  and sdd.STUDENT_ID = {studentId}) as selectedFlag from customer_demographic_value sdv where customer_demographic_id = {customerDemographicId}  order by sort_order, value_name")
    StudentDemographicValue [] getStudentDemographicValues(int customerDemographicId, int studentId) throws SQLException;

    /**
     * @jc:sql statement::
     * select customer_demographic_id as id,
     *   customer_id as customerId,
     *   label_name as labelName,
     *   label_code as labelCode,
     *   value_cardinality as valueCardinality,
     *   sort_order as sortOrder,
     *   import_editable as importEditable,
     *   visible as visible
     * from customer_demographic
     * where customer_id = {customerId}
     *   and visible = 'T'
     * order by sort_order, label_name::
     */
    @JdbcControl.SQL(statement = "select customer_demographic_id as id,  customer_id as customerId,  label_name as labelName,  label_code as labelCode,  value_cardinality as valueCardinality,  sort_order as sortOrder,  import_editable as importEditable,  visible as visible from customer_demographic where customer_id = {customerId}  and visible = 'T' order by sort_order, label_name")
    CustomerDemographic [] getVisibleCustomerDemographics(int customerId) throws SQLException;

    /**
     * @jc:sql statement::
     * select customer_demographic_id as customerDemographicId,
     *   value_name as valueName,
     *   value_code as valueCode,
     *   sort_order as sortOrder,
     *   visible as visible
     * from customer_demographic_value sdv
     * where customer_demographic_id = {customerDemographicId} 
     *    and visible = 'T'
     * order by sort_order, value_name
     * ::
     */
    @JdbcControl.SQL(statement = "select customer_demographic_id as customerDemographicId,  value_name as valueName,  value_code as valueCode,  sort_order as sortOrder,  visible as visible from customer_demographic_value sdv where customer_demographic_id = {customerDemographicId}  and visible = 'T' order by sort_order, value_name")
    CustomerDemographicValue [] getVisibleCustomerDemographicValues(int customerDemographicId) throws SQLException;


    /**
     * @jc:sql statement::
     * select customer_demographic_id as customerDemographicId,
     *   value_name as valueName,
     *   value_code as valueCode,
     *   sort_order as sortOrder,
     *   visible as visible,
     *   (select decode(count(*),0, 'false', 'true') as sddcount from student_demographic_data sdd
     *   where sdd.CUSTOMER_DEMOGRAPHIC_ID = sdv.CUSTOMER_DEMOGRAPHIC_ID
     *    and sdd.VALUE_NAME = sdv.value_name
     *    and sdd.STUDENT_ID = {studentId}) as selectedFlag
     * from customer_demographic_value sdv
     * where customer_demographic_id = {customerDemographicId} 
     *    and visible = 'T'
     * order by sort_order, value_name
     * ::
     */
    @JdbcControl.SQL(statement = "select customer_demographic_id as customerDemographicId,  value_name as valueName,  value_code as valueCode,  sort_order as sortOrder,  visible as visible,  (select decode(count(*),0, 'false', 'true') as sddcount from student_demographic_data sdd  where sdd.CUSTOMER_DEMOGRAPHIC_ID = sdv.CUSTOMER_DEMOGRAPHIC_ID  and sdd.VALUE_NAME = sdv.value_name  and sdd.STUDENT_ID = {studentId}) as selectedFlag from customer_demographic_value sdv where customer_demographic_id = {customerDemographicId}  and visible = 'T' order by sort_order, value_name")
    StudentDemographicValue [] getVisibleStudentDemographicValues(int customerDemographicId, int studentId) throws SQLException;


    /**
     * @jc:sql statement::
     * select distinct
     *      stu.student_id as id,
     *      stu.user_name as loginId,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     * 	    concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,
     *      stu.gender as gender,
     *      stu.birthdate as birthDate,
     *      stu.grade as grade,
     *      stu.ext_pin1 as studentIdNumber,
     *      stu.ext_pin2 as studentIdNumber2,
     *      stu.created_by as createdBy
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona,
     *      user_role urole,
     *      users
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *   and ons.org_node_id = ona.org_node_id 
     * 	 and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id
     * 	 and users.user_id = urole.user_id
     * 	 and urole.activation_Status = 'AC'
     * 	 and users.user_name = {username}
     * 	 and onc.org_node_category_id = node.org_node_category_id
     * ::
     * array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  stu.student_id as id,  stu.user_name as loginId,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName, \t  concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,  stu.gender as gender,  stu.birthdate as birthDate,  stu.grade as grade,  stu.ext_pin1 as studentIdNumber,  stu.ext_pin2 as studentIdNumber2,  stu.created_by as createdBy from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona,  user_role urole,  users where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id = ona.org_node_id  \t and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id \t and users.user_id = urole.user_id \t and urole.activation_Status = 'AC' \t and users.user_name = {username} \t and onc.org_node_category_id = node.org_node_category_id",
                     arrayMaxLength = 100000)
    ManageStudent [] getStudentsAtAndBelowTopOrgNodes(String username) throws SQLException;


    /**
     * @jc:sql statement::
     * select distinct
     *      stu.student_id as id,
     *      stu.user_name as loginId,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     * 	    concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,
     *      stu.gender as gender,
     *      stu.birthdate as birthDate,
     *      stu.grade as grade,
     *      stu.ext_pin1 as studentIdNumber,
     *      stu.ext_pin2 as studentIdNumber2,
     *      stu.created_by as createdBy
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *   and ons.org_node_id  = ona.org_node_id 
     * 	 and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}
     *   and onc.org_node_category_id = node.org_node_category_id
     * ::
     * array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  stu.student_id as id,  stu.user_name as loginId,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName, \t  concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,  stu.gender as gender,  stu.birthdate as birthDate,  stu.grade as grade,  stu.ext_pin1 as studentIdNumber,  stu.ext_pin2 as studentIdNumber2,  stu.created_by as createdBy from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id  = ona.org_node_id  \t and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}  and onc.org_node_category_id = node.org_node_category_id",
                     arrayMaxLength = 100000)
    ManageStudent [] getStudentsAtAndBelowOrgNode(Integer orgNodeId) throws SQLException;


    /**
     * @jc:sql statement::
     * select distinct
     *      stu.student_id as id,
     *      stu.user_name as loginId,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     * 	    concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,
     *      stu.gender as gender,
     *      stu.birthdate as birthDate,
     *      stu.grade as grade,
     *      stu.ext_pin1 as studentIdNumber,
     *      stu.ext_pin2 as studentIdNumber2,
     *      stu.created_by as createdBy
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *   and ons.org_node_id = ona.org_node_id 
     * 	 and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}
     * 	 and onc.org_node_category_id = node.org_node_category_id
     *   {sql: searchCriteria}
     * ::
     * array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  stu.student_id as id,  stu.user_name as loginId,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName, \t  concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,  stu.gender as gender,  stu.birthdate as birthDate,  stu.grade as grade,  stu.ext_pin1 as studentIdNumber,  stu.ext_pin2 as studentIdNumber2,  stu.created_by as createdBy from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id = ona.org_node_id  \t and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId} \t and onc.org_node_category_id = node.org_node_category_id  {sql: searchCriteria}",
                     arrayMaxLength = 100000)
    ManageStudent [] getStudentsAtAndBelowOrgNodeWithSearchCriteria(Integer orgNodeId, String searchCriteria) throws SQLException;

    /**
     * @jc:sql statement::
     * select distinct
     *      stu.student_id as id,
     *      stu.user_name as loginId,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     * 	    concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,
     *      stu.gender as gender,
     *      stu.birthdate as birthDate,
     *      stu.grade as grade,
     *      stu.ext_pin1 as studentIdNumber,
     *      stu.ext_pin2 as studentIdNumber2,
     *      stu.created_by as createdBy
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona,
     *      user_role urole,
     *      users
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *   and ons.org_node_id = ona.org_node_id 
     * 	 and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id
     * 	 and urole.activation_status = 'AC'
     * 	 and users.user_id = urole.user_id
     * 	 and users.user_name = {userName}
     * 	 and onc.org_node_category_id = node.org_node_category_id
     *   {sql: searchCriteria}
     * ::
     * array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  stu.student_id as id,  stu.user_name as loginId,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName, \t  concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,  stu.gender as gender,  stu.birthdate as birthDate,  stu.grade as grade,  stu.ext_pin1 as studentIdNumber,  stu.ext_pin2 as studentIdNumber2,  stu.created_by as createdBy from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona,  user_role urole,  users where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id = ona.org_node_id  \t and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id \t and urole.activation_status = 'AC' \t and users.user_id = urole.user_id \t and users.user_name = {userName} \t and onc.org_node_category_id = node.org_node_category_id  {sql: searchCriteria}",
                     arrayMaxLength = 100000)
    ManageStudent [] getStudentsAtAndBelowUserTopNodeWithSearchCriteria(String userName, String searchCriteria) throws SQLException;


    /**
     * @jc:sql statement::
     * select count(distinct stu.student_id) 
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *   and ons.org_node_id = ona.org_node_id 
     * 	 and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}
     *   and onc.org_node_category_id = node.org_node_category_id
     * ::
     */
    @JdbcControl.SQL(statement = "select count(distinct stu.student_id)  from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id = ona.org_node_id  \t and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}  and onc.org_node_category_id = node.org_node_category_id")
    Integer getStudentCountAtAndBelowOrgNode(Integer orgNodeId) throws SQLException;
    
    /**
     * @jc:sql statement::
     * select count(distinct stu.student_id) 
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona,
     *      user_role urole,
     *      users
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *   and ons.org_node_id = ona.org_node_id 
     * 	 and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id
     * 	 and urole.user_id = users.user_id
     * 	 and urole.activation_Status = 'AC'
     * 	 and users.user_name = {userName}
     *   and onc.org_node_category_id = node.org_node_category_id
     * ::
     */
    @JdbcControl.SQL(statement = "select count(distinct stu.student_id)  from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona,  user_role urole,  users where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id = ona.org_node_id  \t and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id \t and urole.user_id = users.user_id \t and urole.activation_Status = 'AC' \t and users.user_name = {userName}  and onc.org_node_category_id = node.org_node_category_id")
    Integer getStudentCountAtAndBelowUserTopNodes(String userName) throws SQLException;


    /**
     * @jc:sql statement::
     * select distinct node.org_node_id as orgNodeId
     * from org_node node, user_role role, users
     * where role.org_node_id = node.org_node_id
     * and role.activation_status = 'AC'
     * and node.activation_status = 'AC'
     * and role.user_id = users.user_id
     * and users.user_name = {username}::
     */
    @JdbcControl.SQL(statement = "select distinct node.org_node_id as orgNodeId from org_node node, user_role role, users where role.org_node_id = node.org_node_id and role.activation_status = 'AC' and node.activation_status = 'AC' and role.user_id = users.user_id and users.user_name = {username}")
    Integer [] getTopOrgNodeIdsForUser(String username) throws SQLException;

    /**
     * @jc:sql statement::
     * select distinct
     *     node.org_node_id as orgNodeId,
     *     node.customer_id as customerId,
     *     node.org_node_category_id as orgNodeCategoryId,
     *     node.org_node_name as orgNodeName,
     *     node.ext_qed_pin as extQedPin,
     *     node.ext_elm_id as extElmId,
     *     node.ext_org_node_type as extOrgNodeType,
     *     node.org_node_description as orgNodeDescription,
     *     node.created_by as createdBy,
     *     node.created_date_time as createdDateTime,
     *     node.updated_by as updatedBy,
     *     node.updated_date_time as updatedDateTime,
     *     node.activation_status as activationStatus,
     *     node.data_import_history_id as dataImportHistoryId,
     *     node.parent_state as parentState,
     *     node.parent_region as parentRegion,
     *     node.parent_county as parentCounty,
     *     node.parent_district as parentDistrict,
     *     node.org_node_code as orgNodeCode
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona
     * where
     *      ons.student_id = stu.student_id
     * 	 and stu.student_id = {studentId}
     * 	 and ons.org_node_id = node.org_node_id
     * 	 and ons.activation_status = 'AC'
     *   and ons.org_node_id = ona.org_node_id 
     *   and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}
     *   and onc.org_node_category_id = node.org_node_category_id
     *   order by node.org_node_name asc
     * ::
     */
    @JdbcControl.SQL(statement = "select distinct  node.org_node_id as orgNodeId,  node.customer_id as customerId,  node.org_node_category_id as orgNodeCategoryId,  node.org_node_name as orgNodeName,  node.ext_qed_pin as extQedPin,  node.ext_elm_id as extElmId,  node.ext_org_node_type as extOrgNodeType,  node.org_node_description as orgNodeDescription,  node.created_by as createdBy,  node.created_date_time as createdDateTime,  node.updated_by as updatedBy,  node.updated_date_time as updatedDateTime,  node.activation_status as activationStatus,  node.data_import_history_id as dataImportHistoryId,  node.parent_state as parentState,  node.parent_region as parentRegion,  node.parent_county as parentCounty,  node.parent_district as parentDistrict,  node.org_node_code as orgNodeCode from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona where  ons.student_id = stu.student_id \t and stu.student_id = {studentId} \t and ons.org_node_id = node.org_node_id \t and ons.activation_status = 'AC'  and ons.org_node_id = ona.org_node_id  and ona.ANCESTOR_ORG_NODE_ID = {orgNodeId}  and onc.org_node_category_id = node.org_node_category_id  order by node.org_node_name asc")
    OrganizationNode [] getAssignedOrganizationNodesForStudentAtAndBelowOrgNode(int studentId, Integer orgNodeId) throws SQLException;

    /**
     * @jc:sql statement::
     * select distinct
     *     node.org_node_id as orgNodeId,
     *     node.customer_id as customerId,
     *     node.org_node_category_id as orgNodeCategoryId,
     *     node.org_node_name as orgNodeName,
     *     node.ext_qed_pin as extQedPin,
     *     node.ext_elm_id as extElmId,
     *     node.ext_org_node_type as extOrgNodeType,
     *     node.org_node_description as orgNodeDescription,
     *     node.created_by as createdBy,
     *     node.created_date_time as createdDateTime,
     *     node.updated_by as updatedBy,
     *     node.updated_date_time as updatedDateTime,
     *     node.activation_status as activationStatus,
     *     node.data_import_history_id as dataImportHistoryId,
     *     node.parent_state as parentState,
     *     node.parent_region as parentRegion,
     *     node.parent_county as parentCounty,
     *     node.parent_district as parentDistrict,
     *     node.org_node_code as orgNodeCode
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc,
     *      org_node_ancestor ona,
     *      user_role urole,
     *      users
     * where
     *      ons.student_id = stu.student_id
     * 	 and stu.student_id = {studentId}
     * 	 and ons.org_node_id = node.org_node_id
     * 	 and ons.activation_status = 'AC'
     *   and ons.org_node_id = ona.org_node_id 
     *   and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id
     *   and urole.activation_Status = 'AC'
     *   and users.user_id = urole.user_id
     *   and users.user_name = {userName} 
     *   and onc.org_node_category_id = node.org_node_category_id
     *   order by node.org_node_name asc
     * ::
     */
    @JdbcControl.SQL(statement = "select distinct  node.org_node_id as orgNodeId,  node.customer_id as customerId,  node.org_node_category_id as orgNodeCategoryId,  node.org_node_name as orgNodeName,  node.ext_qed_pin as extQedPin,  node.ext_elm_id as extElmId,  node.ext_org_node_type as extOrgNodeType,  node.org_node_description as orgNodeDescription,  node.created_by as createdBy,  node.created_date_time as createdDateTime,  node.updated_by as updatedBy,  node.updated_date_time as updatedDateTime,  node.activation_status as activationStatus,  node.data_import_history_id as dataImportHistoryId,  node.parent_state as parentState,  node.parent_region as parentRegion,  node.parent_county as parentCounty,  node.parent_district as parentDistrict,  node.org_node_code as orgNodeCode from  org_node_student ons,  student stu,  org_node node,  org_node_category onc,  org_node_ancestor ona,  user_role urole,  users where  ons.student_id = stu.student_id \t and stu.student_id = {studentId} \t and ons.org_node_id = node.org_node_id \t and ons.activation_status = 'AC'  and ons.org_node_id = ona.org_node_id  and ona.ANCESTOR_ORG_NODE_ID = urole.org_node_id  and urole.activation_Status = 'AC'  and users.user_id = urole.user_id  and users.user_name = {userName}  and onc.org_node_category_id = node.org_node_category_id  order by node.org_node_name asc")
    OrganizationNode [] getAssignedOrganizationNodesForStudentAtAndBelowUserTopNodes(int studentId, String userName) throws SQLException;
    
    /**
     * @jc:sql statement::
     * select distinct
     *      stu.student_id as id,
     *      stu.user_name as loginId,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     * 	    concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,
     *      stu.gender as gender,
     *      stu.birthdate as birthDate,
     *      stu.grade as grade,
     *      stu.ext_pin1 as studentIdNumber,
     *      stu.ext_pin2 as studentIdNumber2,
     *      stu.created_by as createdBy
     * from
     *      org_node_student ons,
     *      student stu,
     *      org_node node,
     *      org_node_category onc
     * where
     *      ons.student_id = stu.student_id
     * 	 and ons.activation_status= 'AC'
     * 	 and stu.activation_status= 'AC'
     * 	 and ons.org_node_id = node.org_node_id
     *      and ons.org_node_id = {orgNodeId} 
     * 	 and onc.org_node_category_id = node.org_node_category_id::
     * 	 array-max-length="all"
     */
    @JdbcControl.SQL(statement = "select distinct  stu.student_id as id,  stu.user_name as loginId,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName, \t  concat(concat(stu.last_name, ', '), concat(stu.first_name, concat(' ', stu.MIDDLE_NAME))) as studentName,  stu.gender as gender,  stu.birthdate as birthDate,  stu.grade as grade,  stu.ext_pin1 as studentIdNumber,  stu.ext_pin2 as studentIdNumber2,  stu.created_by as createdBy from  org_node_student ons,  student stu,  org_node node,  org_node_category onc where  ons.student_id = stu.student_id \t and ons.activation_status= 'AC' \t and stu.activation_status= 'AC' \t and ons.org_node_id = node.org_node_id  and ons.org_node_id = {orgNodeId}  \t and onc.org_node_category_id = node.org_node_category_id",
                     arrayMaxLength = 100000)
    ManageStudent [] getStudentsForOrgNode(String username, Integer orgNodeId) throws SQLException;
    

    /**
     * @jc:sql statement::
     * select org_node_name
     * from org_node
     * where org_node_id = {orgNodeId}
     * ::
     */
    @JdbcControl.SQL(statement = "select org_node_name from org_node where org_node_id = {orgNodeId}")
    String getOrgNodeNameById(Integer orgNodeId);

    /**
     * @jc:sql statement::
     * select onc.org_node_category_id 
     * from org_node_category onc
     * where onc.customer_id = {customerId}
     *    and onc.activation_status = 'AC'
     * 	  and onc.category_level = 
     * 	  (select max(onc1.category_level) 
     * 	  from org_node_category onc1 
     * 	  where onc.customer_id = onc1.customer_id
     * 	  and onc1.activation_status = 'AC')::
     */
    @JdbcControl.SQL(statement = "select onc.org_node_category_id  from org_node_category onc where onc.customer_id = {customerId}  and onc.activation_status = 'AC' \t  and onc.category_level =  \t  (select max(onc1.category_level)  \t  from org_node_category onc1  \t  where onc.customer_id = onc1.customer_id \t  and onc1.activation_status = 'AC')")
    Integer getBottomOrgNodeCategoryIdForCustomer(Integer customerId);
    
    /**
     * @jc:sql statement::
     * select distinct
     *     node.org_node_id as orgNodeId,
     *     node.customer_id as customerId,
     *     node.org_node_category_id as orgNodeCategoryId,
     *     node.org_node_name as orgNodeName,
     *     node.ext_qed_pin as extQedPin,
     *     node.ext_elm_id as extElmId,
     *     node.ext_org_node_type as extOrgNodeType,
     *     node.org_node_description as orgNodeDescription,
     *     node.created_by as createdBy,
     *     node.created_date_time as createdDateTime,
     *     node.updated_by as updatedBy,
     *     node.updated_date_time as updatedDateTime,
     *     node.activation_status as activationStatus,
     *     node.data_import_history_id as dataImportHistoryId,
     *     node.parent_state as parentState,
     *     node.parent_region as parentRegion,
     *     node.parent_county as parentCounty,
     *     node.parent_district as parentDistrict,
     *     node.org_node_code as orgNodeCode,
     *     cat.category_name as orgNodeCategoryName,
     * 	   ona.number_of_levels as numberOfLevels
     * from 
     *      org_node node, org_node_category cat, org_node_ancestor ona
     * where 
     * 	 cat.org_node_category_id = node.org_node_category_id
     * 	 and ona.ancestor_org_node_id = node.org_node_id
     * 	 and ona.org_node_id = {orgNodeId}
     * order by ona.number_of_levels desc::
     */
    @JdbcControl.SQL(statement = "select distinct  node.org_node_id as orgNodeId,  node.customer_id as customerId,  node.org_node_category_id as orgNodeCategoryId,  node.org_node_name as orgNodeName,  node.ext_qed_pin as extQedPin,  node.ext_elm_id as extElmId,  node.ext_org_node_type as extOrgNodeType,  node.org_node_description as orgNodeDescription,  node.created_by as createdBy,  node.created_date_time as createdDateTime,  node.updated_by as updatedBy,  node.updated_date_time as updatedDateTime,  node.activation_status as activationStatus,  node.data_import_history_id as dataImportHistoryId,  node.parent_state as parentState,  node.parent_region as parentRegion,  node.parent_county as parentCounty,  node.parent_district as parentDistrict,  node.org_node_code as orgNodeCode,  cat.category_name as orgNodeCategoryName, \t  ona.number_of_levels as numberOfLevels from  org_node node, org_node_category cat, org_node_ancestor ona where  \t cat.org_node_category_id = node.org_node_category_id \t and ona.ancestor_org_node_id = node.org_node_id \t and ona.org_node_id = {orgNodeId} order by ona.number_of_levels desc")
    OrganizationNode [] getAncestorOrganizationNodesForOrgNode(Integer orgNodeId) throws SQLException;
 
    /**
     * @jc:sql statement::
     * select distinct
     *     node.org_node_id as orgNodeId,
     *     node.customer_id as customerId,
     *     node.org_node_category_id as orgNodeCategoryId,
     *     node.org_node_name as orgNodeName,
     *     node.ext_qed_pin as extQedPin,
     *     node.ext_elm_id as extElmId,
     *     node.ext_org_node_type as extOrgNodeType,
     *     node.org_node_description as orgNodeDescription,
     *     node.created_by as createdBy,
     *     node.created_date_time as createdDateTime,
     *     node.updated_by as updatedBy,
     *     node.updated_date_time as updatedDateTime,
     *     node.activation_status as activationStatus,
     *     node.data_import_history_id as dataImportHistoryId,
     *     node.parent_state as parentState,
     *     node.parent_region as parentRegion,
     *     node.parent_county as parentCounty,
     *     node.parent_district as parentDistrict,
     *     node.org_node_code as orgNodeCode,
     *     cat.category_name as orgNodeCategoryName,
     * 	   ona.number_of_levels as numberOfLevels
     * from 
     *      org_node node, org_node_category cat, org_node_ancestor ona
     * where 
     * 	 cat.org_node_category_id = node.org_node_category_id
     * 	 and ona.ancestor_org_node_id = node.org_node_id
     * 	 and ona.org_node_id = {orgNodeId}
     *   and node.org_node_id in 
     * 	 	 (select distinct ona1.org_node_id 
     * 	 	 from org_node_ancestor ona1 where {sql: searchCriteria}
     * 	 )
     * order by ona.number_of_levels desc::
     */
    @JdbcControl.SQL(statement = "select distinct  node.org_node_id as orgNodeId,  node.customer_id as customerId,  node.org_node_category_id as orgNodeCategoryId,  node.org_node_name as orgNodeName,  node.ext_qed_pin as extQedPin,  node.ext_elm_id as extElmId,  node.ext_org_node_type as extOrgNodeType,  node.org_node_description as orgNodeDescription,  node.created_by as createdBy,  node.created_date_time as createdDateTime,  node.updated_by as updatedBy,  node.updated_date_time as updatedDateTime,  node.activation_status as activationStatus,  node.data_import_history_id as dataImportHistoryId,  node.parent_state as parentState,  node.parent_region as parentRegion,  node.parent_county as parentCounty,  node.parent_district as parentDistrict,  node.org_node_code as orgNodeCode,  cat.category_name as orgNodeCategoryName, \t  ona.number_of_levels as numberOfLevels from  org_node node, org_node_category cat, org_node_ancestor ona where  \t cat.org_node_category_id = node.org_node_category_id \t and ona.ancestor_org_node_id = node.org_node_id \t and ona.org_node_id = {orgNodeId}  and node.org_node_id in  \t \t (select distinct ona1.org_node_id  \t \t from org_node_ancestor ona1 where {sql: searchCriteria} \t ) order by ona.number_of_levels desc")
    OrganizationNode [] getAncestorOrganizationNodesForOrgNodeAtAndBelowTopOrgNodes(Integer orgNodeId, String searchCriteria) throws SQLException;
 
 
 
 
    /**
     * @jc:sql statement::
     * update student
     * set 
     * 	user_name = {student.loginId},
     * 	first_name = {student.firstName},
     * 	middle_name = {student.middleName},
     * 	last_name = {student.lastName},
     * 	gender = {student.gender},
     * 	birthdate = {student.birthDate},
     * 	grade = {student.grade},
     * 	ext_pin1 = {student.studentIdNumber},
     * 	ext_pin2 = {student.studentIdNumber2},
     * 	updated_By = {updatedBy},
     * 	updated_Date_Time = {updatedDateTime}
     * where student_id = {student.id}::
     */
    @JdbcControl.SQL(statement = "update student set  \tuser_name = {student.loginId}, \tfirst_name = {student.firstName}, \tmiddle_name = {student.middleName}, \tlast_name = {student.lastName}, \tgender = {student.gender}, \tbirthdate = {student.birthDate}, \tgrade = {student.grade}, \text_pin1 = {student.studentIdNumber}, \text_pin2 = {student.studentIdNumber2}, \tupdated_By = {updatedBy}, \tupdated_Date_Time = {updatedDateTime} where student_id = {student.id}")
    void updateStudent(ManageStudent student, Integer updatedBy, Date updatedDateTime) throws SQLException;

    /**
     * @jc:sql statement::
     * select 
     * 	student_demographic_data_id as studentDemographicDataId,
     * 	student_id as studentId,
     * 	customer_demographic_id as customerDemographicId,	
     * 	value_name as valueName,
     * 	value as value,
     * 	created_by as createdBy,
     * 	created_date_time as createdDateTime,
     * 	updated_by as updatedBy,
     * 	updated_date_time as updatedDateTime
     * from student_demographic_data 		
     * where student_demographic_data_id= {studentDemographicDataId}::
     */
    @JdbcControl.SQL(statement = "select  \tstudent_demographic_data_id as studentDemographicDataId, \tstudent_id as studentId, \tcustomer_demographic_id as customerDemographicId,\t \tvalue_name as valueName, \tvalue as value, \tcreated_by as createdBy, \tcreated_date_time as createdDateTime, \tupdated_by as updatedBy, \tupdated_date_time as updatedDateTime from student_demographic_data \t\t where student_demographic_data_id= {studentDemographicDataId}")
    StudentDemographicData getStudentDemographicData(Integer studentDemographicDataId) throws SQLException;


    /**
     * @jc:sql statement::
     * insert into 
     *     student_demographic_data (
     *      student_demographic_data_id,
     * 		student_id,
     * 		customer_demographic_id,
     * 		value_name,
     * 		value,
     * 		created_by,
     * 		created_date_time,
     * 		updated_by,
     * 		updated_date_time
     * 	) values (
     * 	     {sdd.studentDemographicDataId},
     *       {sdd.studentId},
     *       {sdd.customerDemographicId},
     *       {sdd.valueName},
     *       {sdd.value},
     *       {sdd.createdBy},
     *       {sdd.createdDateTime},
     *       {sdd.updatedBy},
     *       {sdd.updatedDateTime}
     * 	)
     * 	::
     */
    @JdbcControl.SQL(statement = "insert into  student_demographic_data (  student_demographic_data_id, \t\tstudent_id, \t\tcustomer_demographic_id, \t\tvalue_name, \t\tvalue, \t\tcreated_by, \t\tcreated_date_time, \t\tupdated_by, \t\tupdated_date_time \t) values ( \t  {sdd.studentDemographicDataId},  {sdd.studentId},  {sdd.customerDemographicId},  {sdd.valueName},  {sdd.value},  {sdd.createdBy},  {sdd.createdDateTime},  {sdd.updatedBy},  {sdd.updatedDateTime} \t)")
    void createStudentDemographicData(StudentDemographicData sdd) throws SQLException;
 
    /**
     * @jc:sql statement::
     * update 
     *      student_demographic_data 
     * set 	
     *      student_id={sdd.studentId},
     *      customer_demographic_id= {sdd.customerDemographicId},	
     * 		value_name={sdd.valueName},
     * 		value={sdd.value},
     * 		updated_by={sdd.updatedBy},
     * 		updated_date_time={sdd.updatedDateTime}
     * where student_demographic_data_id= {sdd.studentDemographicDataId}
     * ::
     */
    @JdbcControl.SQL(statement = "update  student_demographic_data  set \t  student_id={sdd.studentId},  customer_demographic_id= {sdd.customerDemographicId},\t \t\tvalue_name={sdd.valueName}, \t\tvalue={sdd.value}, \t\tupdated_by={sdd.updatedBy}, \t\tupdated_date_time={sdd.updatedDateTime} where student_demographic_data_id= {sdd.studentDemographicDataId}")
    void updateStudentDemographicData(StudentDemographicData sdd) throws SQLException;

    /**
     * @jc:sql statement::
     * delete from 
     *      student_demographic_data 
     * where 	student_id={studentId}
     * ::
     */
    @JdbcControl.SQL(statement = "delete from  student_demographic_data  where \tstudent_id={studentId}")
    void deleteStudentDemographicDataForStudent(Integer studentId) throws SQLException;



    /**
     * @jc:sql statement::
     * delete  
     * from 
     *   student_demographic_data 
     * where student_demographic_data_id in (
     *   select sdd.student_demographic_data_id from student_demographic_data sdd, customer_demographic cd, customer_demographic_value cdv
     *   where sdd.customer_demographic_id = cd.customer_demographic_id
     *   and cd.customer_demographic_id = cdv.customer_demographic_id
     *   and sdd.value_name = cdv.value_name
     *   and cd.visible = 'T' and cdv.visible = 'T'
     *   and sdd.student_id = {studentId})  ::
     */
    @JdbcControl.SQL(statement = "delete  from  student_demographic_data  where student_demographic_data_id in (  select sdd.student_demographic_data_id from student_demographic_data sdd, customer_demographic cd, customer_demographic_value cdv  where sdd.customer_demographic_id = cd.customer_demographic_id  and cd.customer_demographic_id = cdv.customer_demographic_id  and sdd.value_name = cdv.value_name  and cd.visible = 'T' and cdv.visible = 'T'  and sdd.student_id = {studentId})  ")
    void deleteVisibleStudentDemographicDataForStudent(Integer studentId) throws SQLException;

    /**
     * @jc:sql statement::
     * delete from 
     *      student_demographic_data 
     * where student_demographic_data_id= {studentDemographicDataId}
     * ::
     */
    @JdbcControl.SQL(statement = "delete from  student_demographic_data  where student_demographic_data_id= {studentDemographicDataId}")
    void deleteStudentDemographicData(Integer studentDemographicDataId) throws SQLException;
        
    /**
     * @jc:sql statement::
     * delete from 
     *      student_demographic_data 
     * where 	student_id={studentId}
     * 	and customer_demographic_id= {customerDemographicId}
     * ::
     */
    @JdbcControl.SQL(statement = "delete from  student_demographic_data  where \tstudent_id={studentId} \tand customer_demographic_id= {customerDemographicId}")
    void deleteStudentDemographicDataForStudentAndCustomerDemographic(Integer studentId, Integer customerDemographicId) throws SQLException;


    /**
     * @jc:sql statement::
     * select count(*) from 
     *      student_demographic_data 
     * where 	student_id={studentId}
     * ::
     */
    @JdbcControl.SQL(statement = "select count(*) from  student_demographic_data  where \tstudent_id={studentId}")
    Integer getCountStudentDemographicDataForStudent(Integer studentId) throws SQLException;

    /**
     * @jc:sql statement::
     * select count(*) from 
     *      student_demographic_data 
     * where 	student_id={studentId}
     * 	and customer_demographic_id= {customerDemographicId}
     * ::
     */
    @JdbcControl.SQL(statement = "select count(*) from  student_demographic_data  where \tstudent_id={studentId} \tand customer_demographic_id= {customerDemographicId}")
    Integer getCountStudentDemographicDataForStudentAndCustomerDemographic(Integer studentId, Integer customerDemographicId) throws SQLException;

    /**
     * @jc:sql statement::
     * select decode(count(*), 0, 0, 1) from 
     *      student 
     * where upper(user_name)=upper({userName})
     * ::
     */
    @JdbcControl.SQL(statement = "select decode(count(*), 0, 0, 1) from  student  where upper(user_name)=upper({userName})")
    boolean isExistingStudentUserName(String userName) throws SQLException;


    /**
     * @jc:sql statement::
     * select SEQ_STUDENT_DEMOGRAPHIC_ID.nextval from dual
     */
    @JdbcControl.SQL(statement = "select SEQ_STUDENT_DEMOGRAPHIC_ID.nextval from dual")
    Integer getNextPKForStudentDemographicData() throws SQLException;
    
    /**
     * @jc:sql statement::
     * select
     *      ros.TEST_ROSTER_ID as testRosterId,
     *      ros.TEST_ADMIN_ID as testAdminId,
     *      ros.CREATED_DATE_TIME as createdDateTime,
     *      ros.START_DATE_TIME as startDateTime,
     *      ros.COMPLETION_DATE_TIME as completionDateTime,
     *      ros.TEST_COMPLETION_STATUS as testCompletionStatus,
     *      ros.VALIDATION_STATUS as validationStatus,
     *      ros.VALIDATION_UPDATED_BY as validationUpdatedBy,
     *      ros.VALIDATION_UPDATED_DATE_TIME as validationUpdatedDateTime,
     *      ros.VALIDATION_UPDATED_NOTE as validationUpdatedNote,
     *      ros.OVERRIDE_TEST_WINDOW as overrideTestWindow,
     *      ros.PASSWORD as password,
     *      ros.STUDENT_ID as studentId,
     *      ros.CREATED_BY as createdBy,
     *      ros.UPDATED_BY as updatedBy,
     *      ros.ACTIVATION_STATUS as activationStatus,
     *      ros.UPDATED_DATE_TIME as updatedDateTime,
     *      ros.CUSTOMER_ID as customerId,
     *      ros.TUTORIAL_TAKEN_DATE_TIME as tutorialTakenDateTime,
     *      ros.CAPTURE_METHOD as captureMethod,
     *      ros.SCORING_STATUS as scoringStatus,
     *      ros.ORG_NODE_ID as orgNodeId,
     *      ros.FORM_ASSIGNMENT as formAssignment,
     *      stu.first_name as firstName,
     *      stu.middle_name as middleName,
     *      stu.last_name as lastName,
     *      stu.ext_pin1 as extPin1,
     *      stu.user_name as userName
     * from
     *      test_roster ros, student stu
     * where
     *      ros.student_Id = {studentId}
     *      and stu.student_id = ros.student_id::
    */ 
    @JdbcControl.SQL(statement = "select  ros.TEST_ROSTER_ID as testRosterId,  ros.TEST_ADMIN_ID as testAdminId,  ros.CREATED_DATE_TIME as createdDateTime,  ros.START_DATE_TIME as startDateTime,  ros.COMPLETION_DATE_TIME as completionDateTime,  ros.TEST_COMPLETION_STATUS as testCompletionStatus,  ros.VALIDATION_STATUS as validationStatus,  ros.VALIDATION_UPDATED_BY as validationUpdatedBy,  ros.VALIDATION_UPDATED_DATE_TIME as validationUpdatedDateTime,  ros.VALIDATION_UPDATED_NOTE as validationUpdatedNote,  ros.OVERRIDE_TEST_WINDOW as overrideTestWindow,  ros.PASSWORD as password,  ros.STUDENT_ID as studentId,  ros.CREATED_BY as createdBy,  ros.UPDATED_BY as updatedBy,  ros.ACTIVATION_STATUS as activationStatus,  ros.UPDATED_DATE_TIME as updatedDateTime,  ros.CUSTOMER_ID as customerId,  ros.TUTORIAL_TAKEN_DATE_TIME as tutorialTakenDateTime,  ros.CAPTURE_METHOD as captureMethod,  ros.SCORING_STATUS as scoringStatus,  ros.ORG_NODE_ID as orgNodeId,  ros.FORM_ASSIGNMENT as formAssignment,  stu.first_name as firstName,  stu.middle_name as middleName,  stu.last_name as lastName,  stu.ext_pin1 as extPin1,  stu.user_name as userName from  test_roster ros, student stu where  ros.student_Id = {studentId}  and stu.student_id = ros.student_id")
    RosterElement [] getRosterElementsForStudent(Integer studentId) throws SQLException;
    
    /**
     * @jc:sql statement::
     * select 
     *      distinct accom.STUDENT_ID as studentId,
     *      accom.SCREEN_MAGNIFIER as screenMagnifier,
     *      accom.SCREEN_READER as screenReader,
     *      accom.CALCULATOR as calculator,
     *      accom.TEST_PAUSE as testPause,
     *      accom.UNTIMED_TEST as untimedTest,
     *      accom.QUESTION_BACKGROUND_COLOR as questionBackgroundColor,
     *      accom.QUESTION_FONT_COLOR as questionFontColor,
     *      accom.QUESTION_FONT_SIZE as questionFontSize,
     *      accom.ANSWER_BACKGROUND_COLOR as answerBackgroundColor,
     *      accom.ANSWER_FONT_COLOR as answerFontColor,
     *      accom.ANSWER_FONT_SIZE as answerFontSize
     * from
     *      student_accommodation accom,
     *      student stu
     * where
     *      accom.student_id = stu.student_id
     *      and stu.activation_status = 'AC'
     * 	 and stu.student_id = {studentId}::
     */
    @JdbcControl.SQL(statement = "select  distinct accom.STUDENT_ID as studentId,  accom.SCREEN_MAGNIFIER as screenMagnifier,  accom.SCREEN_READER as screenReader,  accom.CALCULATOR as calculator,  accom.TEST_PAUSE as testPause,  accom.UNTIMED_TEST as untimedTest,  accom.QUESTION_BACKGROUND_COLOR as questionBackgroundColor,  accom.QUESTION_FONT_COLOR as questionFontColor,  accom.QUESTION_FONT_SIZE as questionFontSize,  accom.ANSWER_BACKGROUND_COLOR as answerBackgroundColor,  accom.ANSWER_FONT_COLOR as answerFontColor,  accom.ANSWER_FONT_SIZE as answerFontSize from  student_accommodation accom,  student stu where  accom.student_id = stu.student_id  and stu.activation_status = 'AC' \t and stu.student_id = {studentId}")
    StudentAccommodations getStudentAccommodations(Integer studentId) throws SQLException;


    /**
     * @jc:sql statement::
     * delete from 
     * TEST_ROSTER r
     * WHERE r.STUDENT_ID = {studentId}
     * and r.org_node_id in 
     * 	 	 (select distinct ona.org_node_id 
     * 	 	 from org_node_ancestor ona where {sql: searchCriteria} 
     * 	     )
     * ::
     */
    @JdbcControl.SQL(statement = "delete from  TEST_ROSTER r WHERE r.STUDENT_ID = {studentId} and r.org_node_id in  \t \t (select distinct ona.org_node_id  \t \t from org_node_ancestor ona where {sql: searchCriteria}  \t  )")
    void deleteRostersByStudentId(Integer studentId, String  searchCriteria) throws SQLException;
    

    /**
     * @jc:sql statement::
     * select count(*) 
     * from item_response 
     * where test_roster_id = {rosterId}::
     */
    @JdbcControl.SQL(statement = "select count(*)  from item_response  where test_roster_id = {rosterId}")
    Integer getResponseCountForRoster(Integer rosterId) throws SQLException;


    /**
     * @jc:sql statement::
     * delete from 
     *      student_item_set_status 
     * where 
     *      test_roster_id in (
     *           select distinct r.test_roster_id from 
     *              TEST_ROSTER r
     *    	  WHERE r.STUDENT_ID = {studentId}
     * 	     and r.org_node_id in 
     * 	 	 (select distinct ona.org_node_id 
     * 	 	  from org_node_ancestor ona where {sql: searchCriteria}
     * 	         )
     *      )::
     */
    @JdbcControl.SQL(statement = "delete from  student_item_set_status  where  test_roster_id in (  select distinct r.test_roster_id from  TEST_ROSTER r  \t  WHERE r.STUDENT_ID = {studentId} \t  and r.org_node_id in  \t \t (select distinct ona.org_node_id  \t \t  from org_node_ancestor ona where {sql: searchCriteria} \t  )  )")
    void deleteStudentItemSetStatusesForRoster(Integer studentId, String  searchCriteria) throws SQLException;


    /**
     * @jc:sql statement::
     * delete from student_tutorial_status
     * where student_id = {studentId}::
     */
	@JdbcControl.SQL(statement = "delete from student_tutorial_status where student_id = {studentId}")
    void deleteStudentTutorialStatus(Integer studentId);
}