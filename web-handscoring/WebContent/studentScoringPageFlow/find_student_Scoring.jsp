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

<netui-template:template templatePage="/resources/jsp/template.jsp">
<!-- 
template_find_student.jsp
-->

<netui-template:setAttribute name="title" value="${bundle.web['findstudentscoring.window.title']}"/>
<netui-template:setAttribute name="helpLink" value="${bundle.help['help.topic.findStudent']}"/>
<netui-template:section name="bodySection">

<!-- ********************************************************************************************************************* -->
<!-- Start Page Content -->
<!-- ********************************************************************************************************************* -->
<h1>
    <netui:content value="${pageFlow.pageTitle}"/>
</h1>


<!-- title -->
    
    <p>
        <netui:content value="To see a list of all students, click Search. To find specific students, enter the known information on which to search."/><br/>
        <netui:content value="If you know the student's organization assignment, use the Organization tab to locate him or her."/> 
    </p>




<!-- start form -->
<netui:form action="findStudent">

<!--<input type="hidden" name="firstFocusId" id="firstFocusId" value="{actionForm.studentProfile.studentNumber}" />-->
 <!-- Added tagId to resolve javascript isssue  for webLogic 10.3-->       

<netui:hidden tagId="actionElement" dataSource="actionForm.actionElement"/> 
<netui:hidden tagId="currentAction" dataSource="actionForm.currentAction"/>


<netui:hidden  dataSource="actionForm.studentMaxPage"/> 



<!-- message -->
<jsp:include page="/studentScoringPageFlow/show_message.jsp" />


<!-- tabs -->      
      <jsp:include page="/studentScoringPageFlow/find_student_by_profile.jsp" />
 

<br/>
<!--  student search result -->
<a name="studentSearchResult"><!-- studentSearchResult --></a>    
<c:if test="${studentList != null}">     
    <p><netui:content value="${pageFlow.pageMessage}"/></p>
    <p><jsp:include page="/studentScoringPageFlow/find_student_result.jsp" /></p>
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
