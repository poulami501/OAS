<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="ctb-widgets.tld" prefix="ctb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-template-1.0" prefix="netui-template"%>
 
<netui-data:declareBundle bundlePath="oasResources" name="oas"/>
<netui-data:declareBundle bundlePath="webResources" name="web"/>
<netui-data:declareBundle bundlePath="widgetResources" name="widgets"/>
<netui-data:declareBundle bundlePath="helpResources" name="help"/>

<netui-template:template templatePage="/resources/jsp/template_find_customer.jsp">
<netui-template:setAttribute name="title" value="${bundle.web['findcustomer.window.title']}"/>
<netui-template:setAttribute name="helpLink" value="${bundle.help['help.topic.findCustomer']}"/>
<netui-template:section name="bodySection">

<!-- ********************************************************************************************************************* -->
<!-- Start Page Content -->
<!-- ********************************************************************************************************************* -->

<!-- start form -->
<netui:form action="findCustomer">

<input type="hidden" name="firstFocusId" id="firstFocusId" value="{actionForm.customerProfile.name}" />

<netui:hidden dataSource="actionForm.enableLicense" tagId="actionPermission"/>
<h1>
    <netui:content value="${pageFlow.pageTitle}"/>
</h1>

<!-- title -->
<p>
    <netui:content value="To see a list of all customers, click Search. To find specific customers, enter the known information on which to search."/><br/>
</p>

<!-- hidden parameters-->
<netui:hidden  dataSource="actionForm.actionElement"/> 
<netui:hidden  dataSource="actionForm.currentAction"/>
<netui:hidden  dataSource="actionForm.customerMaxPage"/> 


<!--  include message page -->
<jsp:include page="/manageCustomer/show_message.jsp" />

<table class="sortable">
    <tr class="sortable">
        <td class="sortableControls">
<br/>        
<table class="tableFilter">
    <tr class="tableFilter">
        <td class="tableFilter" width="100" align="right">Customer Name:</td>
        <td class="tableFilter" width="200"><netui:textBox tagId="customerName" dataSource="actionForm.customerProfile.name" style="width:180px" tabindex="1" maxlength="32"/></td>
        <td class="tableFilter" width="100" align="right">State:</td>
        <td class="tableFilter" width="*">
            <netui:select optionsDataSource="${pageFlow.stateOptions}" dataSource="actionForm.customerProfile.state" size="1" style="width:180px" tabindex="3" defaultValue="${actionForm.customerProfile.state}"/>
        </td>
    </tr>
    <tr class="tableFilter">
        <td class="tableFilter" width="100" align="right">Customer ID:</td>
        <td class="tableFilter" width="200"><netui:textBox tagId="code" dataSource="actionForm.customerProfile.code" style="width:180px" tabindex="2" maxlength="32"/></td>
        <td class="tableFilter" width="100" align="right">&nbsp;</td>
        <td class="tableFilter" width="*">
            <netui:button styleClass="button" value="Search" type="submit" onClick="setElementValue('{actionForm.currentAction}', 'applySearch');" tabindex="4"/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <netui:button styleClass="button" value="Clear All" type="button" onClick="setElementValueAndSubmit('{actionForm.currentAction}', 'clearSearch');" tabindex="5"/>&nbsp;
        </td>
    </tr>
</table>    
<br/>
        </td>
    </tr>
</table>




<br/>
<!--  customer search result -->
<a name="customerSearchResult"><!-- customerSearchResult --></a>   
<c:if test="${customerResult != null}">     
    <p><netui:content value="${pageFlow.pageMessage}"/></p>
    <p> 
    
    <table class="sortable">

    <tr class="sortable">
      <%--  <td class="sortableControls" colspan="12" height="30" >&nbsp;
            <netui:button tagId="beginEditCustomer" type="submit" value="Edit Profile" onClick="setElementValue('{actionForm.currentAction}', 'editCustomer');" disabled="{request.disableEditProfileButton}"/>
            <netui:button tagId="beginEditFramework" type="submit" value="Edit Framework" onClick="setElementValue('{actionForm.currentAction}', 'beginEditFramework');" disabled="{request.disableEditFrameworkButton}"/>
            <netui:button tagId="createAdministrator" type="submit" value="Create Administrator" onClick="setElementValue('{actionForm.currentAction}', 'createAdministrator');"  disabled="{request.disableCreateAdministratorButton}" />
            <netui:button  tagId="manageOrganization" type="submit" value="Manage Organization" onClick="setElementValue('{actionForm.currentAction}', 'manageOrganization');" disabled="{request.disableManageOrganizationButton}"/>
        </td>--%>
        <td class="sortableControls" colspan="12" height="30">&nbsp;
            <netui:button type="submit" value="View Customer" action="beginViewCustomer" tagId="view" disabled="${requestScope.disableViewProfileButton}"/>
            <netui:button type="submit" value="Edit Customer" action="beginEditCustomer" tagId="editProfile" disabled="${requestScope.disableEditProfileButton}"/>
            <netui:button type="submit" value="Edit Framework" action="beginEditFramework" tagId="editFramework" disabled="${requestScope.disableEditFrameworkButton}"/>  
            <netui:button type="submit" value="Manage Organization" action="manageOrganization" tagId="manageOrg" disabled="${requestScope.disableManageOrganizationButton}"/>
            <netui:button type="submit" value="Add Administrator" action="createAdministrator" tagId="createAdmin" disabled="${requestScope.disableCreateAdministratorButton}"/>
            <netui:button type="submit" value="Manage Licenses" action="manageLicense" tagId="manageLicense" disabled="${requestScope.disableManageLicenseButton}"/>
        </td>
    </tr>
    
    <netui-data:repeater dataSource="pageFlow.customerList">
        <netui-data:repeaterHeader>
        <tr class="sortable">
        <ctb:tableSortColumnGroup columnDataSource="actionForm.customerSortColumn" orderByDataSource="actionForm.customerSortOrderBy" anchorName="customerSearchResult">
            <th class="sortable alignCenter">&nbsp;<netui:content value="${bundle.web['common.column.select']}"/>&nbsp;</th>                
            <th class="sortable alignLeft" width="35%" nowrap><ctb:tableSortColumn value="CustomerName">Customer Name</ctb:tableSortColumn></th>
            <th class="sortable alignLeft" width="25%" nowrap><ctb:tableSortColumn value="ExtCustomerId">Customer ID</ctb:tableSortColumn></th>
            <th class="sortable alignLeft" width="30%" nowrap><ctb:tableSortColumn value="StateDesc">State</ctb:tableSortColumn></th>
        </ctb:tableSortColumnGroup>
    </tr>
    
    </netui-data:repeaterHeader>
    <netui-data:repeaterItem>
       <tr class="sortable">
            <td class="sortable alignCenter">
                <netui:radioButtonGroup dataSource="actionForm.selectedCustomerId">
                    &nbsp;<netui:radioButtonOption value="${container.item.id}" alt="${container.item.actionPermission}" onClick="enableButton(this.alt,'manageLicense');   return enableAllButtons(); ">&nbsp;</netui:radioButtonOption>  
                  
                </netui:radioButtonGroup>
            </td>        
            <td class="sortable">
                <netui:span value="${container.item.name}"/>
            </td>
            <td class="sortable">
                <netui:span value="${container.item.code}"/>
            </td>
            <td class="sortable">
                <netui:span value="${container.item.state}"/>
            </td>
       </tr>
    </netui-data:repeaterItem>
    <netui-data:repeaterFooter>
        <tr class="sortable">
            <td class="sortableControls" colspan="7">
                <ctb:tablePager dataSource="actionForm.customerPageRequested" summary="request.customerPagerSummary" objectLabel="${bundle.oas['object.customers']}" foundLabel="Found" id="customerSearchResult" anchorName="customerSearchResult"/>
            </td>
        </tr>    
    </netui-data:repeaterFooter>
    </netui-data:repeater>
    
   </table>
    
    </p>
</c:if>

<c:if test="${searchResultEmpty != null}">     
    <ctb:message title="Search Result" style="informationMessage" >
          <netui:content value="${requestScope.searchResultEmpty}"/>
    </ctb:message>
</c:if>

    
    

</netui:form>
        
<!-- ********************************************************************************************************************* -->
<!-- End Page Content -->
<!-- ********************************************************************************************************************* -->
</netui-template:section>
</netui-template:template>
