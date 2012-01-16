<%@ taglib uri="http://beehive.apache.org/netui/tags-html-1.0" prefix="netui"%>
<%@ taglib uri="http://beehive.apache.org/netui/tags-databinding-1.0" prefix="netui-data"%>
<%@ taglib uri="label.tld" prefix="lb" %>
<lb:bundle baseName="testsessionApplicationResource" />

<netui-data:declareBundle bundlePath="webResources" name="web"/>
<div id = "displayMessageViewTestSubtest" 
		style="display: none; font-family: Arial, Verdana, Sans Serif; font-size: 12px; font-style: normal; font-weight: normal;padding-bottom:5px;">	
<table>
	<tbody>
		<tr width='100%'>
			<th style='padding-right: 6px; text-align: right;' rowspan='2'>
				<img height='23' src="<%=request.getContextPath()%>/resources/images/messaging/icon_info.gif">
			</th>
		</tr>
		<tr width='100%'>
			<td>
				<span id = 'subtestMessage'></span>
			</td>
		</tr>
	</tbody>
</table>
</div>
<table width="928px" style="margin-bottom: 10px;">
	<tr>
		<td><lb:label key="viewStatus.subtest.loginName"/></td>
		<td><span id = "loginName"></span></td>
	</tr>
	<tr>
		<td><lb:label key="viewStatus.subtest.password"/></td>
		<td><span id = "password"></span></td>
	</tr>
	<tr>
		<td><lb:label key="viewStatus.subtest.testSessionName"/></td>
		<td><span id = "testAdminName"></span></td>
	</tr>
	<tr>
		<td><lb:label key="viewStatus.subtest.testName"/></td>
		<td><span id = "subTestName"></span></td>
	</tr>
	<tr>
		<td><lb:label key="viewStatus.subtest.testStatus"/></td>
		<td><span id = "testStatus"></span></td>
	</tr>
	<tr id="testGradeRow" style="display: none;">
		<td><lb:label key="viewStatus.subtest.testGrade"/></td>
		<td><span id = "testGrade"></span></td>
	</tr>
	<tr id="testLevelRow" style="display: none;">
		<td><lb:label key="viewStatus.subtest.testLevel"/></td>
		<td><span id = "testLevel"></span></td>
	</tr>
</table>
<table width="928px" style="margin-bottom: 10px;">
	<tr>
		<td>
			<div id="toggleValidationSubTest" style="float:left;padding-top:5px;display:none">
				<a href="#" id="toggleValidationSubtestButton" onclick="javascript:toggleSubtestValidationStatus(); return false;" class="rounded {transparent} button"><lb:label key="viewStatus.toggleValidation.button"/></a>
			</div> 
		</td>
	</tr>
</table>

<table id="subtestList" class="rosterSubtestTable"></table>
<input type="hidden" name="itemsSelectLbl" id="itemsSelectLbl" value=<lb:label key="common.column.select" prefix="'" suffix="'"/>/>
<input type="hidden" name="subtestNameLbl" id="subtestNameLbl" value=<lb:label key="ViewSubtestDetails.text.subtestName" prefix="'" suffix="'"/>/>
<input type="hidden" name="subtestLevelLbl" id="subtestLevelLbl" value=<lb:label key="ViewSubtestDetails.text.subtestLevel" prefix="'" suffix="'"/>/>
<input type="hidden" name="subtestStatusLbl" id="subtestStatusLbl" value=<lb:label key="ViewSubtestDetails.text.subtestStatus" prefix="'" suffix="'"/>/>
<input type="hidden" name="validationStatusLbl" id="validationStatusLbl" value=<lb:label key="ViewSubtestDetails.text.validationStatus" prefix="'" suffix="'"/>/>
<input type="hidden" name="startDateLbl" id="startDateLbl" value=<lb:label key="homepage.grid.startDate" prefix="'" suffix="'"/>/>
<input type="hidden" name="completionDateLbl" id="completionDateLbl" value=<lb:label key="ViewSubtestDetails.text.completionDate" prefix="'" suffix="'"/>/>
<input type="hidden" name="totalItemsLbl" id="totalItemsLbl" value=<lb:label key="ViewSubtestDetails.text.totalItems" prefix="'" suffix="'"/>/>
<input type="hidden" name="itemsCorrectLbl" id="itemsCorrectLbl" value=<lb:label key="ViewSubtestDetails.text.itemCorrect" prefix="'" suffix="'"/>/>
<input type="hidden" name="itemsScoredLbl" id="itemsScoredLbl" value=<lb:label key="ViewSubtestDetails.text.itemToBeScored" prefix="'" suffix="'"/>/>

<div id="subtestPager" class="gridTable"></div>