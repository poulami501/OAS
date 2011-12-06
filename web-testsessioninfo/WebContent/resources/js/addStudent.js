var selectStudentgridLoaded = false;
var allRowSelected = false;
var selectedSession = [];
var unCheckedSession = [];
var categoriesStr = ":All;JV:JV;AD:AD"; 
var AccommOption = ":Any;T:Yes;F:No";

var AddStudentLocaldata ={};

var stuForSelectedOrg;
var preSelectedOrg;
var stuIdObjArray = [];
var delStuIdObjArray = [];


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
	$("#Student_Tab").css('display', 'block');
	$("#Select_Student_Tab").css('display', 'none');
}

function loadInnerStuOrgTree() {
	
	createinnSingleNodeSelectedTree (orgTreeHierarchy);
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
	   $("#stuOrgNodeHierarchy").empty().jstree({
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
/*
function imageFormat( cellvalue, options, rowObject ){
	if(cellvalue == 'T')
		return "<select id='Selection'>     <option>Show All</option>   <option>with</option>     <option>without</option>     </select> " ;
	else
		return "No";
		
}*/



function populateSelectStudentGrid() {
 		UIBlock();
 		selectStudentgridLoaded = true;
 		var studentIdTitle = $("#studentIdLabelName").val();
 		var calculator= '<img src="/SessionWeb/resources/images/calc.PNG" title="Calculator"/>';
 		var colorFont= '<img src="/SessionWeb/resources/images/colorfont.PNG" title="Color/Font"/>';
 		var testPause= '<img src="/SessionWeb/resources/images/pause.PNG" title="Pause"/>';
 		var screenReader= '<img src="/SessionWeb/resources/images/screenreader.PNG" title="Reader"/>';
 		var untimedTest= '<img src="/SessionWeb/resources/images/untimed.PNG" title="Untimed"/>';
 		reset();
       $("#selectStudent").jqGrid({         
          url: 'getStudentForList.do?q=2&stuForOrgNodeId='+$("#stuForOrgNodeId").val()+'&selectedTestId='+$("#selectedTestId").val(), 
		  type:   'POST',
		  datatype: "json",          
          colNames:[ 'Last Name','First Name', 'M.I', studentIdTitle, 'Organization','orgName','Accommodation', 'Grade', 'Status', calculator, colorFont, testPause, screenReader, untimedTest],
		   	colModel:[
		   		{name:'lastName',index:'lastName', width:90, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'firstName',index:'firstName', width:90, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'middleName',index:'middleName', width:70, editable: true, align:"left",sorttype:'text',search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'userName',index:'userName', width:110, editable: true, align:"left", sortable:true, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'orgNodeId',index:'orgNodeId',editable: false, width:0, align:"left", sortable:false,search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'orgNodeName',index:'orgNodeName',editable: false, width:0, align:"left", sortable:false,search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'hasAccommodations',index:'hasAccommodations',editable: false, width:0, align:"left", sortable:false,search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'grade',index:'grade', width:50, editable: true, align:"left", sortable:true, search: true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, stype: 'select', searchoptions:{ sopt:['eq'], value: categoriesStr }},
		   		{name:'code',index:'code',editable: true, width:50, align:"left", sortable:true, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'calculator',index:'calculator', width:35, editable: true, align:"center", sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'hasColorFontAccommodations',index:'hasColorFontAccommodations',editable: true, width:35, align:"center", sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'testPause',index:'testPause',editable: true, width:35, align:"center", sortable:true,cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'screenReader',index:'screenReader',editable: true, width:35, align:"center", sortable:true,cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   		{name:'untimedTest',index:'untimedTest',editable: true, width:35, align:"center", sortable:true,cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' }, formatter:imageFormat, stype:'select', editoptions:{value:AccommOption} },
		   	],
		   		jsonReader: { repeatitems : false, root:"studentNode", id:"studentId",
		   	records: function(obj) { 
		   	 //sessionListPA = JSON.stringify(obj.testSessionPA);
		   	 } },
		   	loadui: "disable",
			rowNum:20,
			loadonce:true, 
			multiselect:true,
			pager: '#selectStudentPager', 
			sortname: 'lastName', 
			viewrecords: true, 
			sortorder: "asc",
			height: 370,  
			caption:"Student List",
			toolbar: [true,"top"],
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
			if(stuForSelectedOrg != preSelectedOrg) {
			var allRowsInGrid = $('#selectStudent').jqGrid('getDataIDs');
			if(allRowSelected) { 
			 	$('.cbox').attr('checked', true);
			 	if(unCheckedSession.length > 0) {
			 	for(var i=0; i<unCheckedSession.length; i++) {
			 	 	if(include(allRowsInGrid, unCheckedSession[i])){
			 	 			$("#"+unCheckedSession[i]+" td input").attr('checked', false); 
			 	 		}
			 		}
			 	}
			 } else {
			 	$('.cbox').attr('checked', false); 
			 	for(var i=0; i<allRowsInGrid.length; i++) {
				 	if(stuIdObjArray[allRowsInGrid[i]] != undefined){
				 		var stuObj = stuIdObjArray[allRowsInGrid[i]];
				 		var orgArray = 	stuObj.orgNodeId.split(",");
			 			if(include(orgArray, stuForSelectedOrg)) {
							$("#"+allRowsInGrid[i]+" td input").attr('checked', true); 
						} 
					}
			 	}
			 }
			}
			},
			onSelectAll: function (rowIds) {
				if(allRowSelected) {
					allRowSelected = false;
				} else {
					allRowSelected = true;
				}
			},
			onSelectRow: function (rowid, status) {
				var selectedRowId = rowid;
				if(status) {
					var selectedRowData = $("#selectStudent").getRowData(selectedRowId);
					if(stuIdObjArray[selectedRowId] == undefined){
						stuIdObjArray[selectedRowId]= selectedRowData;
					} else {
						var stuObj = stuIdObjArray[selectedRowId];
						var orgArray = 	stuObj.orgNodeId.split(",");
						if(orgArray.length > 0) {
							if(!include(orgArray, stuForSelectedOrg)) {
							orgArray =orgArray + "," +stuForSelectedOrg;
							stuObj.orgNodeId = orgArray;
							}
						}
						
					}
				} else {
					stuIdObjArray.splice(selectedRowId,1); 
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
	 $("#t_selectStudent").append("<select id='groupSelection'>     <option>Show All</option>   <option>Student with accommodation</option>     <option>Student without accommodation</option>     </select> ");
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
		
	});
	 
	 
	 
}

function include(arr,obj) {
     //return (arr.indexOf(obj) != -1);
    var indx =  jQuery.inArray(obj, arr); 
     if(indx != -1)
      return true;
     else
      return false;
     
}
	
	
function returnSelectedStudent() {
var val=[] ;
 for(var key in stuIdObjArray){ 
 	var stuObj = stuIdObjArray[key];
	var orgArray = 	stuObj.orgNodeId.split(",");
	if(orgArray.length > 0) {
		if(!include(orgArray, stuForSelectedOrg)) {
		orgArray =orgArray + "," +stuForSelectedOrg;
		stuObj.orgNodeId = orgArray;
		}
	}	
 	var objstr = stuIdObjArray[key];
 	objstr['studentId']= key;
 	val.push(objstr);
 }
 var mainObj = {};
  //mainObj['page']= '1';
 //mainObj['total']='2';
 //mainObj['records']='10';
 //mainObj['studentNode'] = val;
 mainObj = val;
 AddStudentLocaldata = mainObj;
openDuplicateStudentPopup();
 //hideSelectStudent();
 //gridReloadStu(false);
 
	
}


function openDuplicateStudentPopup(){
 	populateDuplicateStudentGrid();
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
	

	
function populateDuplicateStudentGrid() {
 		//UIBlock();
 		selectStudentgridLoaded = true;
 		var studentIdTitle = $("#studentIdLabelName").val();
 		reset();
       $("#dupStudentlist").jqGrid({         
          datatype: "local",          
          colNames:[ 'Last Name','First Name', 'M.I', studentIdTitle, leafNodeCategoryName],
		   	colModel:[
		   		{name:'lastName',index:'lastName', width:200, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'firstName',index:'firstName', width:150, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'middleName',index:'middleName', width:150, editable: true, align:"left",sorttype:'text',search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'userName',index:'userName', width:250, editable: true, align:"left", sortable:true, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'orgNodeName',index:'orgNodeName',editable: false, width:200, align:"left", sortable:true, search: false, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } }
		   	],
		   		jsonReader: { repeatitems : false, root:"rows", id:"studentId",
		   	records: function(obj) { 
		   	 //sessionListPA = JSON.stringify(obj.testSessionPA);
		   	 } },
		   	loadui: "disable",
			rowNum:20,
			loadonce:true, 
			multiselect:false,
			//pager: '#dupStudentpager', 
			sortname: 'lastName', 
			viewrecords: true, 
			sortorder: "asc",
			height: 370,  
			rowNum: -1 ,
			//caption:"Student List",
			loadComplete: function () {
				if ($('#dupStudentlist').getGridParam('records') === 0) {
					isPAGridEmpty = true;
            		$('#sp_1_dupStudentPager').text("1");
            		$('#next_dupStudentPager').addClass('ui-state-disabled');
            	 	$('#last_dupStudentPager').addClass('ui-state-disabled');
            	} else {
            		isPAGridEmpty = false;
            	}
            	//setEmptyListMessage('PA');
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
	// jQuery("#dupStudentlist").jqGrid('navGrid','#dupStudentPager',{edit:false,add:false,del:false,search:false,refresh:false});
	 
	
	
	 
	 
	 
}
	