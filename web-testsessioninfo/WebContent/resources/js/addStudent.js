var selectStudentgridLoaded = false;
var allRowSelected = false;
var selectedSession = [];
var unCheckedSession = [];
var categoriesStr = ":All;JV:JV;AD:AD"; 
var AccommOption = ":Any;T:Yes;F:No";

var AddStudentLocaldata ={};
var isOnBack = false;
var stuForSelectedOrg;
var preSelectedOrg;
var delStuIdObjArray = [];
var orgForDupStu = [];
var studentWithaccommodation = 0;
var allStudentIds = [];
var studentGradesCustomerConfig = [];
var allSelectOrg = {};
var countAllSelect = 0;
var studentMap = new Map();
var studentIndexMap = new Map();
var studentTempMap = new Map();
var studentTempIndexMap = new Map();
var studentIndexCount = 0;
var studentTempIndexCount = 0;
var deleteStudentCounter = 0;

function showSelectStudent(){
	$("#Student_Tab").css('display', 'none');
	$("#Select_Student_Tab").css('display', 'block');
	if(orgTreeHierarchy == "" || orgTreeHierarchy ==undefined) {
		populateStuTree();
	} else {
		loadInnerStuOrgTree();
	}
	
}
function hideSelectStudent (){
	isOnBack = true;
	cloneStudentMapToTemp();
	hideSelectStudentPopup();
}

function hideSelectStudentPopup() {
	$('#totalStudent').text(AddStudentLocaldata.length);
	if($("#supportAccommodations").val() != 'false')
	 	 $('#stuWithAcc').text(studentWithaccommodation);
	$("#Student_Tab").css('display', 'block');
	$("#Select_Student_Tab").css('display', 'none');
}

function loadInnerStuOrgTree() {
	if(!isOnBack) {
		createinnSingleNodeSelectedTree (orgTreeHierarchy);
	} else{
		$("#stuOrgNodeHierarchy").jstree("close_all");
		$("#selectStudent").GridUnload();
		selectStudentgridLoaded = false;
	}
	$("#innerSearchheader").css("visibility","visible");	
	$("#stuOrgNodeHierarchy").css("visibility","visible");	

}

function populateStuTree() {
	
	$.ajax({
		async:		true,
		beforeSend:	function(){
						UIBlock();
					},
		url:		'userOrgNodeHierarchyList.do',
		type:		'POST',
		dataType:	'json',
		success:	function(data, textStatus, XMLHttpRequest){	
						orgTreeHierarchy = data;
						createinnSingleNodeSelectedTree (orgTreeHierarchy);
						$("#innerSearchheader").css("visibility","visible");	
						$("#stuOrgNodeHierarchy").css("visibility","visible");	
						$.unblockUI(); 						
					},
		error  :    function(XMLHttpRequest, textStatus, errorThrown){
						//$.unblockUI();  
						window.location.href="/TestSessionInfoWeb/logout.do";
						
					}
		
	});

}

function createinnSingleNodeSelectedTree(jsondata) {
	   $("#stuOrgNodeHierarchy").jstree({
	        "json_data" : {	             
	            "data" : jsondata.data,
				"progressive_render" : true,
				"progressive_unload" : false
	        },
            "themes" : {
			    "theme" : "apple",
			    "dots" : false,
			    "icons" : false
			},       
	        "ui" : {  
	           "select_limit" : 1
         	}, 
				"plugins" : [ "themes", "json_data", "ui"]  
				
	    });
	     
		    $("#stuOrgNodeHierarchy").delegate("a","click", function(e) {
		    	//clearMessage();
		    	
		    	//loadSessionGrid = false;
		    	preSelectedOrg = stuForSelectedOrg;
		    	stuForSelectedOrg = $(this).parent().attr("id");
	 		    $("#stuForOrgNodeId").val(stuForSelectedOrg);
		    	var topNodeSelected = $(this).parent().attr("categoryId");
		    	if(topNodeSelected == leafNodeCategoryId) {
	    		if(!selectStudentgridLoaded) {
		    		populateSelectStudentGrid();
		    	} else  
		    		gridReloadSelectStu();
		    	}
		    	 if(selectStudentgridLoaded) {
			    	var width = jQuery("#scheduleSession").width();
				   	width = width - 72; // Fudge factor to prevent horizontal scrollbars
				   
					var showAccommodations = $("#supportAccommodations").val();
					if(showAccommodations  == 'false') {
						$("#selectStudent").jqGrid("hideCol",["calculator","hasColorFontAccommodations","testPause","screenReader","untimedTest"]); 
					} else {
						$("#selectStudent").jqGrid("showCol",["calculator","hasColorFontAccommodations","testPause","screenReader","untimedTest"]); 
					}
					$("#selectStudent").jqGrid("hideCol",["orgNodeId","orgNodeName","hasAccommodations"]);
					jQuery("#selectStudent").setGridWidth(width,true);
				}
				
		   });
	  
	  	  
}



function imageFormat( cellvalue, options, rowObject ){
	if(cellvalue == 'T')
		return "Yes";
	else
		return "No";
		
} 

function selectFormat( cellvalue, options, rowObject ){
		var orgArrayList = 	orgForDupStu[rowObject.studentId];
		var optList = "<select id='dupStu"+rowObject.studentId +"'>" ;
		for(var key in orgArrayList){
		if(key != undefined)
           optList= optList + "<option value='"+key+"'>"+$.trim(orgArrayList[key])+"</option>" 
		}    
		optList = optList + "</select> " ;
		return optList;
}



function populateSelectStudentGrid() {
 		UIBlock();
 		if(blockOffGradeTesting != null && blockOffGradeTesting)		
 			populateGradeLevelFilter();
 		selectStudentgridLoaded = true;
 		var studentIdTitle = $("#studentIdLabelName").val();
 		var calculator= '<img src="/SessionWeb/resources/images/calc.PNG" title="Calculator"/>';
 		var colorFont= '<img src="/SessionWeb/resources/images/colorfont.PNG" title="Color/Font"/>';
 		var testPause= '<img src="/SessionWeb/resources/images/pause.PNG" title="Pause"/>';
 		var screenReader= '<img src="/SessionWeb/resources/images/screenreader.PNG" title="Reader"/>';
 		var untimedTest= '<img src="/SessionWeb/resources/images/untimed.PNG" title="Untimed"/>';
 		reset();
       $("#selectStudent").jqGrid({         
          url: 'getStudentForList.do?q=2&stuForOrgNodeId='+$("#stuForOrgNodeId").val()+'&selectedTestId='+$("#selectedTestId").val()+'&blockOffGradeTesting='+blockOffGradeTesting+'&selectedLevel='+selectedLevel, 
		  type:   'POST',
		  datatype: "json",          
          colNames:[ 'Last Name','First Name', 'M.I', studentIdTitle, 'Organization','orgName','Accommodation', 'Grade', 'Status', calculator, colorFont, testPause, screenReader, untimedTest, "StatusCopyable", "ItemSetForm","ExtendedTimeAccom"],
		   	colModel:[
		   		{name:'lastName',index:'lastName', width:90, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'firstName',index:'firstName', width:90, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'middleName',index:'middleName', width:35, editable: true, align:"left",sorttype:'text',search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'userName',index:'userName', width:110, editable: true, align:"left", sortable:true, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'orgNodeId',index:'orgNodeId',editable: false, width:0, align:"left", sortable:false,search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'orgNodeName',index:'orgNodeName',editable: false, width:0, align:"left", sortable:false,search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'hasAccommodations',index:'hasAccommodations',editable: false, width:0, align:"left", sortable:false,search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'grade',index:'grade', width:50, editable: true, align:"left", sortable:true, search: true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, stype: 'select', searchoptions:{ sopt:['eq'], value: categoriesStr }},
		   		{name:'code',index:'code',editable: true, width:50, align:"left", sortable:true, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'calculator',index:'calculator', width:38, editable: true, align:"center", sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'hasColorFontAccommodations',index:'hasColorFontAccommodations',editable: true, width:38, align:"center", sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'testPause',index:'testPause',editable: true, width:38, align:"center", sortable:true,cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'screenReader',index:'screenReader',editable: true, width:38, align:"center", sortable:true,cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'untimedTest',index:'untimedTest',editable: true, width:38, align:"center", sortable:true,cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'status.copyable',index:'statusCopyable',hidden:true,editable: true, width:38, editable: true, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'itemSetForm',index:'itemSetForm',hidden:true,editable: true, width:38, editable: true, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'extendedTimeAccom',index:'extendedTimeAccom',hidden:true,editable: true, width:38, editable: true, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } }
		   	],
		   		jsonReader: { repeatitems : false, root:"studentNode", id:"studentId",
		   	records: function(obj) { 
		   	 //sessionListPA = JSON.stringify(obj.testSessionPA);
		   	 if(obj.studentNode != null && obj.studentNode != undefined && obj.studentNode.length > 0) {
		   	 	allStudentIds = [];
			   	 for(var i = 0; i <obj.studentNode.length; i++) {
			   	 	allStudentIds.push(obj.studentNode[i]);
			   	 }
		   	 }
		   	 if(blockOffGradeTesting == null || blockOffGradeTesting == undefined || blockOffGradeTesting == "" || !blockOffGradeTesting) {
		   	 	studentGradesCustomerConfig = obj.gradeList;
 			 	populateGradeLevelFilter();
 			 }
		   	 } },
		   	loadui: "disable",
			rowNum:10,
			loadonce:true, 
			multiselect:true,
			pager: '#selectStudentPager', 
			sortname: 'lastName', 
			viewrecords: true, 
			sortorder: "asc",
			height: 151,  
			caption:"Student List",
			//toolbar: [true,"top"],
			onPaging: function() {
				var reqestedPage = parseInt($('#selectStudent').getGridParam("page"));
				var maxPageSize = parseInt($('#sp_1_selectStudent').text());
				var minPageSize = 1;
				if(reqestedPage > maxPageSize){
					$('#selectStudent').setGridParam({"page": maxPageSize});
				}
				if(reqestedPage <= minPageSize){
					$('#selectStudent').setGridParam({"page": minPageSize});
				}
				
				if(allRowSelected) {
					var sessions  = jQuery('#selectStudent').jqGrid('getGridParam','selarrrow');	
					if(selectedSession.length > 0) {
					var existlen = selectedSession.length;
					for(var i=0; i<sessions.length; i++) {
						if(!include(selectedSession, sessions[i])) {
							selectedSession[existlen] = sessions[i] ;
							existlen++;
						}
					}
					 var allRowsInGrid = $('#selectStudent').jqGrid('getDataIDs');
					
					} else {
						selectedSession = sessions;
					} 
				}
			},
			gridComplete: function() { 
			var allRowChecked = false;
			if(allSelectOrg != null && allSelectOrg.length > 0) {
				for(var i = 0; i < allSelectOrg.length; i++) {
					if(allSelectOrg[i] != null && allSelectOrg[i] == stuForSelectedOrg)
						allRowChecked = true;
				}
			}
			if(allRowChecked) { 
				 $("#cb_selectStudent").attr("checked", true);
				 $("#cb_selectStudent").trigger('click');
				 $("#cb_selectStudent").attr("checked", true);
				 allRowSelected = true;
			 } else {			 				
				if(AddStudentLocaldata != null && AddStudentLocaldata.length > 0) {
					$('.cbox').attr('checked', false); 
					for(var i = 0; i < AddStudentLocaldata.length; i++) {
						var stuObj = AddStudentLocaldata[i];
						if(stuObj != null && stuObj != undefined) {
							var orgArray = 	String(stuObj.orgNodeId).split(",");
							if(include(orgArray, stuForSelectedOrg)) {
								$("#"+stuObj.studentId+" td input").attr("checked", true);
								$("#"+stuObj.studentId).trigger('click');
								$("#"+stuObj.studentId+" td input").attr("checked", true); 
							}
						} 					
					}
				} else { 
					$('.cbox').attr('checked', false); 
					
					var allRowsInGridHere = $('#selectStudent').jqGrid('getDataIDs');
					for(var i = 0; i < allRowsInGridHere.length; i++) {
						var stdData = studentTempMap.get(allRowsInGridHere[i]);
						if(stdData != null && stdData != undefined) {
							var orgArray = 	String(stdData.orgNodeId).split(",");
							if(include(orgArray, stuForSelectedOrg)) {
								$("#"+allRowsInGridHere[i]+" td input").attr("checked", true);
								$("#"+allRowsInGridHere[i]).trigger('click');
								$("#"+allRowsInGridHere[i]+" td input").attr("checked", true);
							}
						}
					}
				}
			 }
			},
			onSelectAll: function (rowIds, status) {
				if(status) {
					allRowSelected = true;
					for(var i = 0; i < allStudentIds.length; i++) {						
						if(studentTempMap.length() == 0) {
							studentTempMap.put(allStudentIds[i].studentId,allStudentIds[i]);
							studentTempIndexMap.put(studentTempIndexCount,allStudentIds[i].studentId);
							studentTempIndexCount++;
						} else {
							if (studentTempMap.get(allStudentIds[i].studentId) == null || studentTempMap.get(allStudentIds[i].studentId) == undefined) {
								studentTempMap.put(allStudentIds[i].studentId,allStudentIds[i]);
								if(!isPresentIndex(allStudentIds[i].studentId)) {
									//alert("Added again");
									studentTempIndexMap.put(studentTempIndexCount,allStudentIds[i].studentId);
									studentTempIndexCount++;
								}
							} else {
								// Added to handle duplicate students
								var studentData = studentTempMap.get(allStudentIds[i].studentId);
								var orgList = String(studentData.orgNodeId);
								var orgListName = studentData.orgNodeName;
								var orgListAll = String(allStudentIds[i].orgNodeId);
								var orgListAllName = String(allStudentIds[i].orgNodeName);
								if(orgList.indexOf(orgListAll) == -1) {
									orgList = orgList + "," + orgListAll;
									orgListName = orgListName + "," + orgListAllName;
									studentData.orgNodeId = orgList;
									studentData.orgNodeName = orgListName;
									studentTempMap.put(studentData.studentId, studentData);
								}
							}
						}
					}	
					// Added to handle multiple organization select All	
					var present = false;
					if(countAllSelect > 0) {
						for(var i = 0; i < allSelectOrg.length; i++) {
							if(allSelectOrg[i] != null && allSelectOrg[i] == stuForSelectedOrg)
								present = true;
						}
						if(!present) {
							allSelectOrg[countAllSelect] = stuForSelectedOrg;
							countAllSelect++;
						}
					} else {
						allSelectOrg[countAllSelect] = stuForSelectedOrg;
						countAllSelect++;
					}			
				} else {
					allRowSelected = false;	
					for(var i = 0; i < allStudentIds.length; i++) {
						var studIdVal = studentTempMap.get(allStudentIds[i].studentId);
						if(studIdVal != null && studIdVal != undefined) {
							studentTempMap.put(allStudentIds[i].studentId,null);
						}
					}
					for(var i = 0; i < allSelectOrg.length; i++) {
						if(allSelectOrg[i] != null && allSelectOrg[i] == stuForSelectedOrg)
							allSelectOrg.splice(i,1);
					}									
				}
			},
			onSelectRow: function (rowid, status) {
				var selectedRowId = rowid;
				var selectedRowData = $("#selectStudent").getRowData(selectedRowId);
				
				if(status) {
					if(studentTempMap.length() == 0) {
						studentTempMap.put(selectedRowId,selectedRowData);
						studentTempIndexMap.put(studentTempIndexCount,selectedRowId);
						studentTempIndexCount++;
					} else {					
						var studentDataVal = studentTempMap.get(selectedRowId);
						if(studentDataVal == null || studentDataVal == undefined) {
							studentTempMap.put(selectedRowId,selectedRowData);
							if(!isPresentIndex(selectedRowId)) {
								//alert("Added twice");
								studentTempIndexMap.put(studentTempIndexCount,selectedRowId);
								studentTempIndexCount++;
							}
						} else {
							// Added to handle duplicate students
							var orgList = String(studentDataVal.orgNodeId);
							var orgListName = studentDataVal.orgNodeName;
							var orgListAll = String(selectedRowData.orgNodeId);
							var orgListAllName = String(selectedRowData.orgNodeName);
							if(orgList.indexOf(orgListAll) == -1) {
								orgList = orgList + "," + orgListAll;
								orgListName = orgListName + "," + orgListAllName;
								studentDataVal.orgNodeId = orgList;
								studentDataVal.orgNodeName = orgListName;
								studentTempMap.put(selectedRowId, studentDataVal);
							}
						}
					}
				} else {
					var studentIdVal = studentTempMap.get(selectedRowId);
					if(studentIdVal != null && studentIdVal != undefined) {
						var orgList = studentIdVal.orgNodeId;
						var orgListName = studentIdVal.orgNodeName;
						var finalOrgList = "";
						var finalOrgListName = "";
						if(orgList.indexOf(",") == -1) {
							studentTempMap.put(selectedRowId,null);
						} else {
							var orgListArray = orgList.split(",");
							var orgListNameArray = orgListName.split(",");
							for(var j = 0; j < orgListArray.length; j++) {
								if(orgListArray[j] == stuForSelectedOrg) {
								
								} else {
									finalOrgList = finalOrgList + orgListArray[j] + ",";
									finalOrgListName = finalOrgListName + orgListArray[j] + ",";
								}
							}
							finalOrgList = finalOrgList.substr(0,finalOrgList.length - 1);
							finalOrgListName = finalOrgListName.substr(0,finalOrgListName.length - 1);
							studentIdVal.orgNodeId = finalOrgList;
							studentIdVal.orgNodeName = finalOrgListName;
							studentTempMap.put(selectedRowId,studentIdVal);
						}
					}
					for(var i = 0; i < allSelectOrg.length; i++) {
						if(allSelectOrg[i] != null && allSelectOrg[i] == stuForSelectedOrg)
							allSelectOrg.splice(i,1);
					}
				}
			},
			loadComplete: function () {
				if ($('#selectStudent').getGridParam('records') === 0) {
					isPAGridEmpty = true;
            		$('#sp_1_selectStudentPager').text("1");
            		$('#next_selectStudentPager').addClass('ui-state-disabled');
            	 	$('#last_selectStudentPager').addClass('ui-state-disabled');
            	} else {
            		isPAGridEmpty = false;
            	}
            	//setEmptyListMessage('PA');
            	$.unblockUI();  
				$("#selectStudent").setGridParam({datatype:'local'});
				var tdList = ("#selectStudentPager_left table.ui-pg-table  td");
				for(var i=0; i < tdList.length; i++){
					$(tdList).eq(i).attr("tabIndex", i+1);
				}
			},
			loadError: function(XMLHttpRequest, textStatus, errorThrown){
				$.unblockUI();  
				window.location.href="/SessionWeb/logout.do";
						
			}
	 });
	 jQuery("#selectStudent").jqGrid('navGrid','#selectStudentPager',{edit:false,add:false,del:false,search:false,refresh:false});
	 jQuery("#selectStudent").jqGrid('filterToolbar');
	/* $("#t_selectStudent").append("<select id='groupSelection'>     <option>Show All</option>   <option>Student with accommodation</option>     <option>Student without accommodation</option>     </select> ");
	$("select","#t_selectStudent").change(function( val){
		if($('#groupSelection').val() == 'Student with accommodation') {
			$('#gs_calculator').val('T');
			$('#gs_hasColorFontAccommodations').val('T');
			$('#gs_testPause').val('T');
			$('#gs_screenReader').val('T');
			$('#gs_untimedTest').val('T');
		} else if ($('#groupSelection').val() == 'Student without accommodation'){
			$('#gs_calculator').val('F');
			$('#gs_hasColorFontAccommodations').val('F');
			$('#gs_testPause').val('F');
			$('#gs_screenReader').val('F');
			$('#gs_untimedTest').val('F');
		} else if ($('#groupSelection').val() == 'Show All'){
			$('#gs_calculator').val('');
			$('#gs_hasColorFontAccommodations').val('');
			$('#gs_testPause').val('');
			$('#gs_screenReader').val('');
			$('#gs_untimedTest').val('');
		}
		
	});*/
	 
	 //Changes to block off grade testing
		if(blockOffGradeTesting) {
			if(selectedLevel.length > 2)
				$("#gs_grade").attr('disabled', false);
			else
				$("#gs_grade").attr('disabled', true);
		}
	 
	 
}

function include(arr,obj) {
     //return (arr.indexOf(obj) != -1);
    var indx =  jQuery.inArray(obj, arr); 
     if(indx != -1)
      return true;
     else
      return false;
     
}
	
function updateDupStudent(){
	var dupData = $("#dupStudentlist").jqGrid('getGridParam','data');
	for(var key in dupData){
		var objstr = dupData[key];
		var orgId = $("#dupStu"+objstr.studentId).val(); 
		var OrgName = orgForDupStu[objstr.studentId][orgId];
		var studentDataTe = studentMap.get(objstr.studentId);
		var studentTempData = studentTempMap.get(objstr.studentId);
		if(studentDataTe == null || studentDataTe == undefined) {
			studentMap.put(objstr.studentId,studentTempData);
			studentIndexMap.put(studentIndexCount,objstr.studentId);
			studentIndexCount++;
			studentDataTe = studentMap.get(objstr.studentId);
		}
		studentDataTe.orgNodeId = orgId;
		studentDataTe.orgNodeName = $.trim(OrgName);
		studentMap.put(objstr.studentId,studentDataTe);
	}
	 updateAddStudentLocaldata();
	 hideSelectStudentPopup();
	 gridReloadStu(false);
	 $("#duplicateStudent").dialog("close");
}
	
function returnSelectedStudent() {
	var duplicateStuArray=[];
	orgForDupStu = [];
	var dupStuPresent = false;
	var duplicateStuArraydata ={};
	studentWithaccommodation = 0;
	studentMap = new Map();
	studentIndexCount = 0;
	studentIndexMap = new Map();
	for(var j = 0; j < studentTempIndexCount; j++) {
		var stdId = studentTempIndexMap.get(j);
		var objstr = studentTempMap.get(stdId);
		if(objstr != null && objstr != undefined) {
			var hasAccom = objstr.hasAccommodations;
			if(hasAccom == 'Yes') {
	 	 		studentWithaccommodation = studentWithaccommodation + 1;
	 	 	}
	 	 	var orgArray = String(objstr.orgNodeId).split(",");
	 	 	if(orgArray.length > 1) {
				dupStuPresent = true;
				objstr.studentId = stdId;
				duplicateStuArray.push(objstr);
				var orgNameArray = objstr.orgNodeName.split(",");
				var orgIdNameMap = {};
				for(var i=0;i<orgArray.length; i++) {
					orgIdNameMap[orgArray[i]] = orgNameArray[i];
				}
				orgForDupStu[objstr.studentId] = orgIdNameMap;
			} else {	
		 		studentMap.put(stdId,objstr);
				studentIndexMap.put(studentIndexCount,stdId);
				studentIndexCount++;
	 		}
		}
	}
 if(dupStuPresent) {
 duplicateStuArraydata = duplicateStuArray;
 	$('#dupStudentlist').GridUnload();	
 	openDuplicateStudentPopup(duplicateStuArraydata, orgForDupStu);
 } else {
	 updateAddStudentLocaldata();
	 hideSelectStudentPopup();
	 gridReloadStu(false);
	 $("#duplicateStudent").dialog("close");
 }
 
}

function updateAddStudentLocaldata() {
	AddStudentLocaldata = [];
	for(var i = 0; i < studentIndexCount; i++) {
		var studeId = studentIndexMap.get(i);
		AddStudentLocaldata[i] = studentMap.get(studeId);
		AddStudentLocaldata[i].studentId = studeId;
	}
}

function openDuplicateStudentPopup(duplicateStuArray, orgForDupStu){
	populateDuplicateStudentGrid(duplicateStuArray, orgForDupStu);
	$("#duplicateStudent").dialog({  
		title:"Confirmation Alert",  
	 	resizable:false,
	 	autoOpen: true,
	 	width: '1024px',
	 	modal: true,
	 	open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); }
		});	
		 var toppos = ($(window).height() - 650) /2 + 'px';
		 var leftpos = ($(window).width() - 1024) /2 + 'px';
		 $("#scheduleSession").parent().css("top",toppos);
		 $("#scheduleSession").parent().css("left",leftpos);	
		 
	}	
	

	
function populateDuplicateStudentGrid(duplicateStuArray, orgForDupStu) {
 		//UIBlock();
 		
 		var studentIdTitle = $("#studentIdLabelName").val();
 		reset();
       $("#dupStudentlist").jqGrid({
      	  data: duplicateStuArray,         
          datatype: 'local',          
          colNames:[ 'Last Name','First Name', 'M.I', studentIdTitle, leafNodeCategoryName],
		   	colModel:[
		   		{name:'lastName',index:'lastName', width:200, editable: true, align:"left",sorttype:'text',search: false, sortable:false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'firstName',index:'firstName', width:150, editable: true, align:"left",sorttype:'text',search: false, sortable:false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'middleName',index:'middleName', width:150, editable: true, align:"left",sorttype:'text',search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'userName',index:'userName', width:250, editable: true, align:"left", sortable:false, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'orgNodeName',index:'orgNodeName',editable: false, width:200, align:"left", sortable:false, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' },formatter:selectFormat }
		   	],
		   		jsonReader: { repeatitems : false, root:"rows", id:"studentId",
		   	records: function(obj) { 
		   	 } },
		   	loadui: "disable",
			rowNum:10000,
			loadonce:true, 
			multiselect:false,
			sortname: 'lastName',
			pager: '#dupStudentPager', 
			viewrecords: true, 
			sortorder: "asc",
			height: 370,  
			loadComplete: function () {
				if ($('#dupStudentlist').getGridParam('records') === 0) {
					
            		$('#sp_1_dupStudentPager').text("1");
            		$('#next_dupStudentPager').addClass('ui-state-disabled');
            	 	$('#last_dupStudentPager').addClass('ui-state-disabled');
            	} else {
            		
            	}
            	$.unblockUI();  
				$("#dupStudentlist").setGridParam({datatype:'local'});
				var tdList = ("#dupStudentPager_left table.ui-pg-table  td");
				for(var i=0; i < tdList.length; i++){
					$(tdList).eq(i).attr("tabIndex", i+1);
				}
				
			},
			loadError: function(XMLHttpRequest, textStatus, errorThrown){
				$.unblockUI();  
				window.location.href="/SessionWeb/logout.do";
						
			}
	 });
	 jQuery("#dupStudentlist").jqGrid('navGrid','#dupStudentPager',{edit:false,add:false,del:false,search:false,refresh:false});
	 
	
	
	 
	 
	 
}


function populateGradeLevelFilter() {

	var init = 0;
	var final = 0;	
	var splitArray = [];
	if(blockOffGradeTesting) {
		categoriesStr = "";
		if(selectedLevel.indexOf("-") >= 0 || selectedLevel.indexOf("\/") >= 0)
			categoriesStr = ":"+selectedLevel+";";
		dropListToDisplay = [{"id":selectedLevel,"name":selectedLevel}];
	}
	else {
		dropListToDisplay = [];
		for (var i = 0; i < studentGradesCustomerConfig.length; i++)
			dropListToDisplay[i] = {"id":studentGradesCustomerConfig[i],"name":studentGradesCustomerConfig[i]};
		categoriesStr = ":All;";
	}
	for (var i = 0; i < dropListToDisplay.length; i++) {
		if(dropListToDisplay[i].id == "Show all")
			continue;
		else {
			if(dropListToDisplay[i].id.indexOf("-") < 0) {				
				if(dropListToDisplay[i].id.indexOf("\/") >= 0) { // For handling terranova product
					splitAttay = dropListToDisplay[i].id.split("/");
					categoriesStr = categoriesStr + splitAttay[0] + ":" + splitAttay[0] + ";";
					categoriesStr = categoriesStr + splitAttay[1] + ":" + splitAttay[1] + ";";
				} else {
					categoriesStr = categoriesStr + dropListToDisplay[i].id + ":" + dropListToDisplay[i].name + ";";
				}
			} else { // For handling laslink products
				splitArray = dropListToDisplay[i].id.split("-");
				init = parseInt(splitArray[0]);
				final = parseInt(splitArray[1]);
				for(var j = init; j <= final; j++) {
					categoriesStr = categoriesStr + j + ":" + j + ";";
				}
			}
		}
	}
	categoriesStr = categoriesStr.substr(0,categoriesStr.length - 1);
	if(blockOffGradeTesting == null || blockOffGradeTesting == undefined || blockOffGradeTesting == "" || !blockOffGradeTesting) {
		var optionList = [];
		optionList[0] = {"id":'',"name":'All'};		
		var gradeArray = categoriesStr.split(";");
		for(var i = 1; i < gradeArray.length; i++) {
			var splitGrade = gradeArray[i].split(":");
			optionList[i] = {"id":splitGrade[0],"name":splitGrade[1]};
		}
		fillDropDown('gs_grade',optionList);
	}
}



function getStudentListArray(studentArray) {
	  var resultStdArray = [];
	  if (studentArray==undefined) {
	  	return resultStdArray;
	  }
	  var l = 0;
	  for (var i=0; i<studentArray.length; i++) {
	    if(studentArray[i]!= undefined && studentArray[i] != null) {
	    	var val = new SessionStudent(studentArray[i].studentId, studentArray[i].orgNodeId, studentArray[i].extendedTimeAccom, studentArray[i]["status.copyable"],  studentArray[i].itemSetForm );
	        resultStdArray[l]= val.toString();
	        ++l;
	    }
	  		
	  	
	  }
	  return resultStdArray;
	}
	function validateIdenticalStudent (studentArray) {
    	var accessCodes=new Array();
    	var validStatus = true;
		var resultStdArray = [];
		var duplicateStudentArray = [];
		var k = 0;
		if (studentArray==undefined) {
		  	return true;
		 }
		  
		for (i=0; i<studentArray.length; i++) {
		  var val = studentArray[i].studentId;
		  resultStdArray[i]= val;
		}
    
		var sorted_arr = resultStdArray.sort(); 
		for (var i = 0; i < resultStdArray.length - 1; i++) {
			if (sorted_arr[i + 1] == sorted_arr[i]) {
				duplicateStudentArray[k]=sorted_arr[i]; 
		    	validStatus = false; 
		    	k++;
		 	} 
		} 
    	return validStatus;
    }

    function SessionStudent(studentId, orgNodeId, extendedTimeAccom, statusCopyable, itemSetForm) {
	   this.studentId = studentId;
	   this.orgNodeId = orgNodeId;
	   this.extendedTimeAccom = "";
	   if(extendedTimeAccom != undefined)
	   		this.extendedTimeAccom = extendedTimeAccom;
	   
	   this.statusCopyable = "";
	   if(statusCopyable != undefined)
	   		this.statusCopyable = statusCopyable;

	   this.itemSetForm ="";
	   if(itemSetForm != undefined)
	   		this.itemSetForm = itemSetForm;
	}
	
	SessionStudent.prototype.toString = function () {
  		return ( ""+"studentId="+this.studentId +":orgNodeId=" +this.orgNodeId + ":extendedTimeAccom="+this.extendedTimeAccom + ":statusCopyable=" +this.statusCopyable +":itemSetForm=" +this.itemSetForm+":");
	};
	
	function cloneStudentMapToTemp() {
		studentTempIndexCount = studentIndexCount;
		studentTempMap = new Map();
		studentTempIndexMap = new Map();
		for(var i = 0; i < studentIndexCount; i++) {
			var studentIdCur = studentIndexMap.get(i);
			studentTempIndexMap.put(i,studentIdCur);
			studentTempMap.put(studentIdCur,studentMap.get(studentIdCur));
		}
	}
	
	function isPresentIndex(studentIdForCheck) {
		var found = false;
		for(var i = 0; i < studentTempIndexCount ; i++) {
			if(studentTempIndexMap.get(i) != null && studentTempIndexMap.get(i) != undefined && String(studentTempIndexMap.get(i)) == String(studentIdForCheck))
				found = true;
		}
		return found;
	}
	