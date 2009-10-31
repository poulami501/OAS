<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="dto.PathNode"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="ctb-widgets.tld" prefix="ctb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="netui-tags-html.tld" prefix="netui-compat"%>
 
<netui-data:declareBundle bundlePath="oasResources" name="oas"/>
<netui-data:declareBundle bundlePath="webResources" name="web"/>
<netui-data:declareBundle bundlePath="widgetResources" name="widgets"/>


<table class="simple">
    <tr class="transparent">
        

<!-- column 1 -->
<td class="transparent-top" width="50%">
<table class="transparent">
    <tr class="transparent">
        <td class="transparent" width="100" nowrap><netui:content value="Address Line 1:"/></td>
        <td class="transparent"><netui-compat:label value="{actionForm.customerProfile.billingContact.addressLine1}"/></td>
    </tr>
    <tr class="transparent">
        <td class="transparent" width="100" nowrap><netui:content value="Address Line 2:"/></td>
        <td class="transparent"><netui-compat:label value="{actionForm.customerProfile.billingContact.addressLine2}"/></td>
    </tr>
    <tr class="transparent">
        <td class="transparent" width="100" nowrap><netui:content value="Address Line 3:"/></td>
        <td class="transparent"><netui-compat:label value="{actionForm.customerProfile.billingContact.addressLine3}"/></td>
    </tr>
    
</table>
</td>
   
<!-- column 2 -->
<td class="transparent-top" width="50%">
<table class="transparent">
    <tr class="transparent">
        <td class="transparent" width="100" nowrap><netui:content value="City:"/></td>
        <td class="transparent"><netui-compat:label value="{actionForm.customerProfile.billingContact.city}"/></td>
    </tr>
    <tr class="transparent">
        <td class="transparent" width="100" nowrap><netui:content value="State:"/></td>
        <td class="transparent"><netui-compat:label value="{actionForm.customerProfile.billingContact.stateDesc}"/></td>
    </tr>
    <tr class="transparent">
        <td class="transparent" width="100" nowrap><netui:content value="Zip:"/></td>
        <td class="transparent"><netui-compat:label value="{actionForm.customerProfile.billingContact.zipCode}"/></td>
    </tr>
</table>
</td>

</tr>
</table>

