var selectProctorGridLoaded = false;
var allRowSelectedPro = false;
var selectedSessionPro = [];
var unCheckedSessionPro = [];
//var categoriesStr = ":All;JV:JV;AD:AD"; 
//var AccommOption = ":Any;T:Yes;F:No";

var addProctorLocaldata ={};
var proctorForSelectedOrg;
var preSelectedOrgPro;
var proctorIdObjArray = [];
var delProctorIdObjArray = [];
var isOnBackProctor = false;

function showSelectProctor(){
	$("#Proctor_Tab").css('display', 'none');
	$("#Select_Proctor_Tab").css('display', 'block');
	if(orgTreeHierarchy == "" || orgTreeHierarchy ==undefined) {
		populateProctorTree();
	} else {
		loadInnerProctorOrgTree();
	}
	
}
function hideSelectedProctor (){

	isOnBackProctor = true;
	$("#Proctor_Tab").css('display', 'block');
	$("#Select_Proctor_Tab").css('display', 'none');	
	
}

function loadInnerProctorOrgTree() {
	
	if(!isOnBackProctor) {
		createSingleNodeSelectedTreeForProctor (orgTreeHierarchy);
	} else{
		$("#proctorOrgNodeHierarchy").jstree("close_all");
		$("#selectProctor").GridUnload();
		selectProctorGridLoaded = false;
	}
	$("#innerProctorSearchheader").css("visibility","visible");	
	$("#proctorOrgNodeHierarchy").css("visibility","visible");	

}

function populateProctorTree() {
	
	$.ajax({
		async:		true,
		beforeSend:	function(){
						UIBlock()
					},
		url:		'userOrgNodeHierarchyList.do',
		type:		'POST',
		dataType:	'json',
		success:	function(data, textStatus, XMLHttpRequest){	
						$.unblockUI(); 
						orgTreeHierarchy = data;
						createSingleNodeSelectedTreeForProctor (orgTreeHierarchy);
						$("#innerProctorSearchheader").css("visibility","visible");	
						$("#proctorOrgNodeHierarchy").css("visibility","visible");							
					},
		error  :    function(XMLHttpRequest, textStatus, errorThrown){
						$.unblockUI();
						window.location.href="/SessionWeb/logout.do";
						
					},
		complete :  function(){
						 $.unblockUI(); 
					}
	});

}

function createSingleNodeSelectedTreeForProctor(jsondata) {
	   $("#proctorOrgNodeHierarchy").jstree({
	        "json_data" : {	             
	            "data" : jsondata.data,
				"progressive_render" : true,
				"progressive_unload" : false
	        },
	        "ui" : {  
	           "select_limit" : 1
         	},
         	"themes" : {
				"theme" : "apple",
				"dots" : false,
				"icons" : false
			},  
				"plugins" : [ "themes", "json_data", "ui"]  
				
	    });
	    
	    $("#proctorOrgNodeHierarchy").delegate("a","click", function(e) {
			 	 
  			proctorForSelectedOrg = $(this).parent().attr("id");
 		    $("#proctorOrgNodeId").val(proctorForSelectedOrg);
 		    UIBlock();
 		    if(!selectProctorGridLoaded) {
 		        populateSelectProctorGrid();
			}
			else
				gridReloadSelectProctor();
		});
}

function populateSelectProctorGrid() {
 		UIBlock();
 		selectProctorGridLoaded = true;
 		//reset();
       $("#selectProctor").jqGrid({         
          url: 'getProctorList.do?q=2&proctorOrgNodeId='+$("#proctorOrgNodeId").val(), 
		  type:   'POST',
		  datatype: "json",          
          colNames:[ 'Last Name','First Name','Default SCheduler'],
		   	colModel:[
		   		{name:'lastName',index:'lastName', width:90, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'firstName',index:'firstName', width:90, editable: true, align:"left",sorttype:'text',search: false, sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } },
		   		{name:'defaultScheduler',index:'defaultScheduler', hidden: true, width:130, editable: false, align:"left",sorttype:'text',sortable:true, cellattr: function (rowId, tv, rawObject, cm, rdata) { return 'style="white-space: normal;' } }
		   	],

		   	jsonReader: { repeatitems : false, root:"userProfileInformation", id:"userId", records: function(obj) { userList = JSON.stringify(obj.userProfileInformation);return obj.userProfileInformation.length; } },

		   	loadui: "disable",
			rowNum:20,
			loadonce:true, 
			multiselect:true,
			pager: '#selectProctorPager', 
			sortname: 'lastName', 
			viewrecords: true, 
			sortorder: "asc",
			height: 370,  
			caption:"Proctor List",
			onPaging: function() {
				var reqestedPage = parseInt($('#selectProctor').getGridParam("page"));
				var maxPageSize = parseInt($('#sp_1_selectProctor').text());
				var minPageSize = 1;
				if(reqestedPage > maxPageSize){
					$('#selectProctor').setGridParam({"page": maxPageSize});
				}
				if(reqestedPage <= minPageSize){
					$('#selectProctor').setGridParam({"page": minPageSize});
				}
				
				if(allRowSelectedPro) {
					var sessions  = jQuery('#selectProctor').jqGrid('getGridParam','selarrrow');	
					if(selectedSessionPro.length > 0) {
					var existlen = selectedSessionPro.length;
					for(var i=0; i<sessions.length; i++) {
						if(!include(selectedSessionPro, sessions[i])) {
							selectedSessionPro[existlen] = sessions[i] ;
							existlen++;
						}
					}
					 var allRowsInGrid = $('#selectProctor').jqGrid('getDataIDs');
					
					} else {
						selectedSessionPro = sessions;
					} 
				}
			},
			gridComplete: function() { 
				if(proctorForSelectedOrg != preSelectedOrgPro) {
					var allRowsInGrid = $('#selectProctor').jqGrid('getDataIDs');
					if(allRowSelectedPro) { 
					 	$('.cbox').attr('checked', true);
					 	if(unCheckedSessionPro.length > 0) {
					 	for(var i=0; i<unCheckedSessionPro.length; i++) {
					 	 	if(include(allRowsInGrid, unCheckedSessionPro[i])){
					 	 			$("#"+unCheckedSessionPro[i]+" td input").attr('checked', false); 
					 	 		}
					 		}
					 	}
					 } else {
					 	$('.cbox').attr('checked', false); 
					 	for(var i=0; i<allRowsInGrid.length; i++) {
						 	if(proctorIdObjArray[allRowsInGrid[i]] != undefined){
						 		var stuObj = proctorIdObjArray[allRowsInGrid[i]];
						 		//var orgArray = 	stuObj.orgNodeId.split(",");
					 			//if(include(orgArray, proctorForSelectedOrg)) {
									//$("#"+allRowsInGrid[i]+" td input").attr('checked', true); 
								//} 
							}
					 	}
					 }
				} 
			},
			onSelectAll: function (rowIds) {
				if(allRowSelectedPro) {
					allRowSelectedPro = false;
				} else {
					allRowSelectedPro = true;
				}
			},
			onSelectRow: function (rowid, status) {
				var selectedRowId = rowid;
				if(status) {
					var selectedRowData = $("#selectProctor").getRowData(selectedRowId);
					if(proctorIdObjArray[selectedRowId] == undefined){
						proctorIdObjArray[selectedRowId]= selectedRowData;
					} 
				} else {
					proctorIdObjArray.splice(selectedRowId,1); 
				} 
			},
			loadComplete: function () {
				if ($('#selectProctor').getGridParam('records') === 0) {
					isPAGridEmpty = true;
            		$('#sp_1_selectProctorPager').text("1");
            		$('#next_selectProctorPager').addClass('ui-state-disabled');
            	 	$('#last_selectProctorPager').addClass('ui-state-disabled');
            	} else {
            		isPAGridEmpty = false;
            	}
            	//setEmptyListMessage('PA');
            	$.unblockUI();  
				$("#selectProctor").setGridParam({datatype:'local'});
				var tdList = ("#selectProctorPager_left table.ui-pg-table  td");
				for(var i=0; i < tdList.length; i++){
					$(tdList).eq(i).attr("tabIndex", i+1);
				}
				var width = jQuery("#scheduleSession").width();
		    	width = width - 280; // Fudge factor to prevent horizontal scrollbars
		    	jQuery("#selectProctor").setGridWidth(width,true);
			},
			loadError: function(XMLHttpRequest, textStatus, errorThrown){
				$.unblockUI();  
				window.location.href="/SessionWeb/logout.do";
						
			}
	 });
	  jQuery("#selectProctor").jqGrid('navGrid','#selectProctorPager',{edit:false,add:false,del:false,search:false,refresh:false});
	  //jQuery("#selectProctor").jqGrid('filterToolbar');

}

function include(arr,obj) {
     //return (arr.indexOf(obj) != -1);
    var indx =  jQuery.inArray(obj, arr); 
     if(indx != -1)
      return true;
     else
      return false;
     
}
	
	
function returnSelectedProctor() {

	var val=[] ;
	 for(var key in proctorIdObjArray){ 
	 	var objstr = proctorIdObjArray[key];
	 	objstr['userId']= key;
	 	val.push(objstr);
	 }
	 var mainObj = {};
	  //mainObj['page']= '1';
	 //mainObj['total']='2';
	 //mainObj['records']='10';
	 //mainObj['studentNode'] = val;
	 mainObj = val;
	 addProctorLocaldata = mainObj;
	 noOfProctorAdded = addProctorLocaldata.length;
	 hideSelectedProctor();
	 gridReloadProctor(false);
	 $("#totalAssignedProctors").text(noOfProctorAdded);
	 //$("#jqg_listProctor_1").attr("disabled", true);
	 
}
