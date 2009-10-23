<%@ page import="java.io.*, java.util.*"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-template-1.0" prefix="netui-template"%>
<%@ taglib uri="ctb-widgets.tld" prefix="ctb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
 
<netui-data:declareBundle bundlePath="oasResources" name="oas"/>
<netui-data:declareBundle bundlePath="webResources" name="web"/>
<netui-data:declareBundle bundlePath="widgetResources" name="widgets"/>
<netui-data:declareBundle bundlePath="helpResources" name="help"/>

<netui-template:template templatePage="/resources/jsp/template.jsp">
    <netui-template:setAttribute name="title" value="${bundle.web['installClient.window.title']}"/>
    <netui-template:setAttribute name="helpLink" value="${bundle.help['help.topic.installClient']}"/>
<netui-template:section name="bodySection">
 
<!-- ********************************************************************************************************************* -->
<!-- Start Page Content -->
<!-- ********************************************************************************************************************* -->

<h1><netui:content value="${bundle.web['installClient.title']}"/></h1>
<p><netui:content value="${bundle.web['installClient.message']}"/></p>
<p align="right">
    <a href="#" onClick="newWindow('/help/pdfs/min_sys_req_client_pc.pdf');return false;"><netui:content value="${bundle.web['installClient.viewSystemRequirements']}"/></a>
</p>    

<table class="transparent">
    <tr class="transparent">
        <td class="transparent-top" width="20"><font size="6" color="#999999">1</font></td>    
        <td class="transparent-top">

            <h2><netui:content value="${bundle.web['installClient.java.title']}"/></h2>
            <p><netui:content value="${bundle.web['installClient.java.message']}"/></p>
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top">
        </td>
        <td class="transparent-top">
            <table class="transparent">
                <tr id="installJavaRow" class="transparent">
                   <td class="transparent-top">
                        <netui:content value="<b>For PC</b>: Go to the Java website and download the current JRE update for free."/>
                        <br>
                        <a href="#" onclick="newWindow('http://www.java.com/en/');"><netui:content value="http://www.java.com/en/"/></a>
                        <br>
                    </td>
                </tr>
                <tr class="transparent">
                   <td class="transparent-top">
                        <netui:content value="<b>For Mac</b>: Open Software Update on the Mac workstation to update the JRE."/>
                        <br>
                    </td>
                </tr>
            </table>  
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top" colspan="2">
            <hr/>
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top" width="20"><font size="6" color="#999999">2</font></td>    
        <td class="transparent-top">
            <div id="showDownloadFlashMessage" style="display:block">                    
                <h2><netui:content value="${bundle.web['installClient.flash.title']}"/></h2>
                <p><netui:content value="${bundle.web['installClient.flash.message']}"/></p>
            </div>
            <div id="showDownloadFlashNonIEMessage" style="display:none">                    
                <h2><netui:content value="${bundle.web['installClient.flash.title']}"/></h2>
                <p><netui:content value="${bundle.web['installClient.flash.error.message']}"/></p>
            </div>
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top">
        </td>
        <td class="transparent-top">
            <div id="downloadFlashOk" style="display:block">            
                <table class="transparent">
                    <tr id="installFlash7Row" class="transparent">
                        <td class="transparent-top">
                            <netui:content value="Go to the Adobe website and download it for free."/>
                            <br>
                            <a href="#" onclick="newWindow('http://www.adobe.com/products/flashplayer/'); return false;"><netui:content value="http://www.adobe.com/products/flashplayer/"/></a>
                            <br>
                        </td>
                    </tr>
                </table>
            </div>
            <div id="downloadFlashError" style="display:none">            
                <table class="transparent">
                    <tr id="installFlash7Row" class="transparent">
                        <td class="transparent-top">
                            <p><netui:content value="${bundle.web['installClient.installFlash9.message']}"/></p>
                        </td>
                    </tr>
            </table>
            </div>
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top" colspan="2">
            <hr/>
        </td>
    </tr>


    <tr class="transparent">
        <td class="transparent-top" width="20"><font size="6" color="#999999">3</font></td>    
        <td class="transparent-top">
            <h2><netui:content value="${bundle.web['installClient.installClient.title']}"/></h2>
            <p><netui:content value="${bundle.web['installClient.title.message']}"/></p>
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top">
        </td>
        <td class="transparent-top">
            
            <table class="transparent">
            	<%-- PC --%>
                <tr id="installPCClientRow" class="transparent">
                    <td class="transparent-top" width="5%">
                        <img class="transparent-top" src="../resources/images/legacy/icon_pc.gif" width="52" height="33"/>
                    </td>
                   <td class="transparent-top" width="75%">
                        <b><netui:content value="${bundle.web['installClient.windows.clientName']}"/></b><br>
                        <netui:content value="${bundle.web['installClient.windows.version']}"/><br>
                        <i><netui:content value="${bundle.web['installClient.windows.OS']}"/></i><br>
                        <netui:content value="${bundle.web['installClient.windows.size']}"/>
                    </td>
                    <td class="transparent-top">
                            <% String href_PC = (String)request.getAttribute("downloadURI_PC"); %>                    
                            <netui:button styleClass="button" tagId="installPCClient" value="${bundle.web['installClient.windows.buttonText']}" type="button" onClick="<%= href_PC %>" disabled="false"/>        
                    </td>
                </tr>         
                
                <tr class="transparent">
                    <td class="transparent" height="25" colspan="3"/>
                </tr>
                
            	<%-- Mac OS --%>
		        <tr id="installPCClientRow" class="transparent">
		            <td class="transparent-top" width="5%">
		                <img class="transparent" src="../resources/images/legacy/icon_macX.gif"/>
		            </td>
		            <td class="transparent-top" width="75%">
		                <b><netui:content value="${bundle.web['installClient.mac.clientName']}"/></b><br>
		                <netui:content value="${bundle.web['installClient.mac.version']}"/><br>
		                <i><netui:content value="${bundle.web['installClient.mac.OS']}"/></i><br>
		                <netui:content value="${bundle.web['installClient.mac.size']}"/>
		            </td>
		            <td class="transparent-top">
                        <% String href_MAC = (String)request.getAttribute("downloadURI_MAC"); %>                    
		                <netui:button styleClass="button" tagId="installPCClient" value="${bundle.web['installClient.windows.buttonText']}" type="button" onClick="<%= href_MAC %>" disabled="false"/>        
		            </td>
		        </tr>     



            </table>
        </td>
    </tr>
    <tr class="transparent">
        <td class="transparent-top" colspan="2">
            <hr/>
        </td>
    </tr>

    <tr class="transparent">
        <td class="transparent-top" colspan="2">
            <netui:button value="Home" onClick="document.location.href='goto_homepage.do'"/>
        </td>
    </tr>

</table>

<script>
	checkDownloadFlashNonIE();
</script>

<!-- ********************************************************************************************************************* -->
<!-- End Page Content -->
<!-- ********************************************************************************************************************* -->
    </netui-template:section>
</netui-template:template>

