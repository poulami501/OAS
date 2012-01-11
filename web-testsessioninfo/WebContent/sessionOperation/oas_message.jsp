<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>

<netui-data:declareBundle bundlePath="webResources" name="web"/>


<input type="hidden" id="noCompletedTestSessions" name = "noCompletedTestSessions" value="${bundle.web['common.message.user.noCompletedTestSessions.title']}"/>
<input type="hidden" id="noCurrentOrFutureTestSessions" name = "noCurrentOrFutureTestSessions" value="${bundle.web['common.message.user.noCurrentOrFutureTestSessions.title']}"/>
<input type="hidden" id="noTestSessions" name = "noTestSessions" value="${bundle.web['common.message.browse.noTestSessions.message']}"/>
<input type="hidden" id="noStudentTitle" name = "noStudentTitle" value="${bundle.web['sessionList.studentTab.noStuSelected.title']}"/>
<input type="hidden" id="noStudentMsg" name = "noStudentMsg" value="${bundle.web['sessionList.studentTab.noStuSelected.message']}"/>
<input type="hidden" id="noTestMsg" name = "noStudentMsg" value="${bundle.web['sessionList.selectTest.noTestSelected.message']}"/>
<input type="hidden" id="noProctorTitle" name = "noProctorTitle" value="${bundle.web['sessionList.studentTab.noProctorTitle.title']}"/>
<input type="hidden" id="noProctorMsg" name = "noProctorMsg" value="${bundle.web['sessionList.studentTab.noProctorSelected.message']}"/>