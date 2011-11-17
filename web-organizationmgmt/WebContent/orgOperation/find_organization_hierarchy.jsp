<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
  
<span style = "font-family: Arial, Verdana, Sans Serif; font-size: 12px; font-style: normal;">&nbsp;&nbsp;&nbsp;Open the Organization Search to view the organization hierarchy. Select an organization to view,  edit, delete, or add a suborganization.</span>
<br/>
<br>
<table class="transparent">

    <tr class="transparent">
        <td style="border-color : #2E6E9E;">
        <table width="100%">
        <tr width="100%" >
		   <td align="left" style="padding: 0 0 0 8px;">
		      	<div id="displayMessageMain" style="display:none; width:99.5%; background-color: #FFFFFF; font-family: Arial, Verdana, Sans Serif; font-size: 12px; font-style: normal; font-weight: bold;">
					<div id= "contentMain" style="padding-bottom: 5px"> </div>
				</div>
			</td>
		</tr>
		</table>
		<table>  
			<!-- <tr class="transparent">
	        	<td  colspan="12" style="height:5px; color: #336699; font-family: Arial,Verdana,Sans Serif; font-size: 13px;  font-style: normal;  font-weight: bold;">&nbsp;</td>
   	 		</tr> -->
		  
	      <tr class="transparent">
	        <td class="transparent" valign="middle">
		      	<div  id= "searchheader" class="ui-corner-tl ui-corner-tr ui-widget-header treeCtrlHeader">&nbsp;Organization Search</div>
		    	  <script>populateTree();</script>
		    	  <div id = "orgNodeHierarchy"  class="treeCtrl">
				</div> 
		    </td>
		    
		 	<td class="transparent" width="8px">&nbsp;</td>
		 	 
		 	<td >	      
		    	<table id="list2" class="gridTable"></table>
				<div id="pager2" class="gridTable" ></div>
			</td>
			
	      </tr>
	      <!-- 
	      <tr class="transparent">
	        <td  colspan="12" style="height:5px; color: #336699; font-family: Arial,Verdana,Sans Serif; font-size: 13px;  font-style: normal;  font-weight: bold;">&nbsp;</td>
   	 	  </tr>
   	 	   -->
	    </table>
        </td>
    </tr>
</table>

<jsp:include page="/orgOperation/add_edit_org_detail.jsp" />
			
<div id="confirmationPopup"
	style="display: none; background-color: #D4ECFF; font-family: Arial, Verdana, Sans Serif; font-size: 12px; font-style: normal; font-weight: normal;">
	<table>
		<tr>
			<td colspan="2">
			<p>You have not saved the organization record. Are you sure you want to leave this page?</p>
			</td>
		</tr>
		<tr>
		<td >
				<center>
					<input type="button"  value="&nbsp;Yes&nbsp;" onclick="javascript:closeConfirmationPopup(); return false;" class="ui-widget-header">
					<input type="button"  value="&nbsp;No&nbsp;&nbsp;" onclick="javascript:closePopUp('confirmationPopup'); return false;" class="ui-widget-header">
				</center>
			<br>
		</td>
		
		</tr>
		
	</table>
</div>

<div id="confirmationPopupNavigation"
	style="display: none; background-color: #D4ECFF; font-family: Arial, Verdana, Sans Serif; font-size: 12px; font-style: normal; font-weight: normal;">
	<table>
		<tr>
			<td colspan="2">
			<br/>
			<p>You have not saved the organization record. Are you sure you want to leave this page?</p>
			<br/>
			</td>
		</tr>
		<tr>
		<td >
				<center>
					<input type="button"  value="&nbsp;Yes&nbsp;" onclick="javascript:closeConfirmationPopup(); return false;" class="ui-widget-header">&nbsp;
					<input type="button"  value="&nbsp;No&nbsp;&nbsp;" onclick="javascript:closePopUp('confirmationPopupNavigation'); return false;" class="ui-widget-header">
				</center>
			<br>
		</td>
		
		</tr>
		
	</table>
</div>
				