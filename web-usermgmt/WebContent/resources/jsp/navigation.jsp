<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-template-1.0" prefix="netui-template"%>
<%@ taglib uri="ctb-widgets.tld" prefix="ctb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="legacyNav">
    <table class="legacyNavLayout">
    <tr>
        <td>
           <table class="legacyNavLinks floatLeft">
            <tr>
                <td><netui:anchor action="homepage">home</netui:anchor></td>
                <c:if test="${ sessionScope.userHasReports}">                
                <td>| <a href="/TestSessionInfoWeb/homepage/viewReports.do" >reports</a></td>
                </c:if>
                <c:if test="${ requestScope.isMyProfile }"><td class="currentNav">| my profile</td></c:if>
                <c:if test="${ !requestScope.isMyProfile}"><td>| <a href="/UserManagementWeb/manageUser/beginMyProfile.do" >my profile</a></td></c:if>
                <ctb:auth roles="Administrator">
                    <td>| <a href="/OrganizationManagementWeb/administration/begin.do">system administration</a></td>
                </ctb:auth>
            </tr>
           </table>
        </td>
        <td>
            <table class="legacyNavLinks floatRight">
            <tr>
                <td><a href="<netui-template:attribute name="helpLink"/>" onClick="return showHelpWindow(this.href);">help</a></td>
                <td>| <netui:anchor action="logout">logout</netui:anchor></td>
            </tr>
            </table>
        </td>
    </tr>
    </table>
</div>

