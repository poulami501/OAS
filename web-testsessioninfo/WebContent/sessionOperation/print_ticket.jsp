<%@ page import="java.io.*, java.util.*"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<netui-data:declareBundle bundlePath="webResources" name="web" />
<% 
 //Integer scheduleUserOrgNode =  (Integer) session.getAttribute("schedulerUserOrgIds");
 //String schedulerUserId = (String) session.getAttribute("schedulerUserId");
%>
<input type="hidden" id="scheduleUserOrgNode" name = "scheduleUserOrgNode"/>
<input type="hidden" id="selectedTestSessionId" name = "selectedTestSessionId" />

<div id="printTestTicket"
	style="display: none; border:10px solid #D4ECFF; font-family: Arial, Verdana, Sans Serif; font-size: 12px; font-style: normal; font-weight: normal;">
<br>	
<p><netui:content value="${bundle.web['testTicket.subtitle']}" /></p>
<h3 style="border-width: 0px"><span><netui:content value="${bundle.web['testTicket.testInformation']}" /></span></h3>
			
<table class="transparent">
    <tr class="transparent">
        <td class="transparent" nowrap><span style="font-weight:bold;"><netui:content value="${bundle.web['testTicket.testName']}" /></span></td>
        <td class="transparent" ><span id = "testName_val" style="background-color:white;"></span></td>
        <td class="transparent" nowrap><span style="font-weight:bold;"><netui:content value="${bundle.web['testTicket.testSessionName']}" /></span></td>
        <td class="transparent" > <span id = "adminTestName_val" style="background-color:white;"></span></td>
	</tr>
</table>
</p>
<br/>

<div style="width:100%;clear:both;float:left;margin-bottom: 20px;" id="studentExists">
	<div  id= "ticktSearchheader" class="ui-corner-tl ui-corner-tr ui-widget-header treeCtrlHeader" style="text-align:left;">
		<div style="float:left;" >&nbsp;<netui:content value="${bundle.web['testTicket.organizations']}"/></div> 
		<div style="clear:both;"></div>     
	</div>
	<div style="width:49%;float:left; padding-right: 0px;height:255px; margin-bottom:10px;" id="orgTktTreeDiv" class="ticketTreeCtrl">		
	</div>
	
	<div style="width:48%;float:right;">
	<h3 style="border-width: 0px; margin-bottom:15px;"><netui:content value="${bundle.web['testTicket.testSessionDocs']}" /></h3>
	<p>	
	<netui:content value="${bundle.web['testTicket.sessionDocsText']}" /><br>
	</p>
	<p>
	<a href="#" style="text-decoration:underline;color:blue;" onclick="return openTestTicketIndividual(this, document.getElementById('selectedTestSessionId').value, document.getElementById('scheduleUserOrgNode').value);"><netui:content value="${bundle.web['testTicket.sessionIndividualTestTicket']}" /></a>
	&nbsp;
	<img src="/SessionWeb/resources/images/logo_pdf.gif" border="0">
	<br/>
	<netui:content value="${bundle.web['testTicket.sessionDocsIndividual']}" />
	<br>
	
	</p>
	
	<!--START - Added For CR ISTEP2011CR007 (Multiple Test Ticket)-->
	<p>
	<a href="#" style="text-decoration:underline;color:blue;" onclick="return openTestTicketMultiple(this, document.getElementById('selectedTestSessionId').value, document.getElementById('scheduleUserOrgNode').value);"><netui:content value="${bundle.web['testTicket.sessionMultipleTestTicket']}" /></a>
	&nbsp;
	<img src="/SessionWeb/resources/images/logo_pdf.gif" border="0">
	<br/>
	<netui:content value="${bundle.web['testTicket.sessionDocsMultiple']}" />
	</p>
	<!--END - Added For CR ISTEP2011CR007 (Multiple Test Ticket)-->
	
	<p>
	<a href="#" style="text-decoration:underline;color:blue;" onclick="return openTestTicketSummary(this, document.getElementById('selectedTestSessionId').value, document.getElementById('scheduleUserOrgNode').value);"><netui:content value="${bundle.web['testTicket.sessionSummaryTestTicket']}" /></a>
	&nbsp;<img src="/SessionWeb/resources/images/logo_pdf.gif" border="0">
	<br/>
	<netui:content value="${bundle.web['testTicket.sessionDocsSummary']}" />
	</p>
	</div>		
</div>
<br/>
<div style="width:97%;display:none; margin-bottom: 30px;" id="noStudent" class="ticketTreeCtrl">
	<table width='95%' height="60" cellpadding="2" cellspacing="5">
	<tbody>
	<tr>
	<th align="left" style="padding-left: 5px;padding-top: 5px; width:25px;" ><img height='23' src='/SessionWeb/resources/images/messaging/icon_info.gif'></th>
	<th align="left"><netui:content value="${bundle.web['testTicket.noStudentErrorHead']}" /></th>
	</tr>
	<tr>
	<td>&nbsp;</td>
	<td align="left" style="padding-bottom: 5px;" ><netui:content value="${bundle.web['testTicket.noStudentErrorSubhead']}" /></td></tr>
	</tbody>
	</table>
</div>
<br/>
<div style="width:97%;clear:both; float:left;">	
			<table cellspacing="0" cellpadding="0" border="0" width='95%' height="20">
					<tbody>
						<tr id="Act_Buttons" align="center">
							<td  width="100%">
								<center>
								<input type="button"  id="cData" value="&nbsp;Cancel&nbsp;&nbsp;" onclick="javascript:closePopUp('printTestTicket'); return false;" class="ui-widget-header">
								</center>
								<br>
							</td>
						</tr>
						<tr class="binfo" style="display: none;">
							<td colspan="2" class="bottominfo"></td>
						</tr>
					</tbody>					
			</table>
</div>		
</div>