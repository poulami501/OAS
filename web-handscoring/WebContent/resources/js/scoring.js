function formSubmit(itemId, itemType, itemSetId, itemNumber, rowno) {

var isHidden = $('#dialogID').is(':hidden');  
if(isHidden){		
			var param = "&itemId="+itemId+"&itemType="+itemType+"&itemSetId="+itemSetId+"&rosterId="+$("#rosterId").val();
			document.getElementById("itemId").value = itemId;
			document.getElementById("itemSetId").value = itemSetId;
			document.getElementById("itemNumber").value = itemNumber;
			document.getElementById("message").style.display = 'none';
			document.getElementById("rowNo").value = rowno;

			$.ajax(
				{
						async:		true,
						beforeSend:	function(){
										blockUI();
										$("#audioPlayer").hide();
										$("#crText").hide();
										
										
									},
						url:		'beginCRResponseDisplay.do',
						type:		'POST',
						data:		param,
						dataType:	'json',
						success:	function(data, textStatus, XMLHttpRequest){	
										var crTextResponse = "";
										var isAudioItem = data.answer.isAudioItem;
										var linebreak ="\n\n";
										
										if(isAudioItem){
											document.getElementById("itemType").value = "AI";
											var audioResponseString = data.answer.audioItemContent;
											audioResponseString = audioResponseString.substr(13);
											audioResponseString = audioResponseString.split("%3C%2F");
											document.getElementById("audioResponseString").value = audioResponseString[0];
											document.getElementById("pointsDropDown").setAttribute("disabled",true);
											document.getElementById("Question").setAttribute("disabled",true);
											//$("#crText").hide();
											$("#audioPlayer").show();
											getAudioPlayer('audioPlayer');
											openPopup(rowno, itemNumber);
											
											updateScore(rowno);
										}
										else{
										document.getElementById("itemType").value = "CR";								
											var crResponses =data.answer.cRItemContent.string.length;
											for(var i = 0; i < crResponses; i++){
											if( i == (crResponses-1)){
											linebreak ="";
											document.getElementById("pointsDropDown").removeAttribute("disabled");
											document.getElementById("Question").removeAttribute("disabled");
										}
										 crTextResponse = crTextResponse + data.answer.cRItemContent.string[i] + linebreak;

										}

										openPopup(rowno, itemNumber);
										//$("#audioPlayer").hide();
										$("#crText").show();
										$("#crText").val(crTextResponse);
										updateScore(rowno);
										}									
									},
						error  :    function(XMLHttpRequest, textStatus, errorThrown){
										//changes for defect #66003
										window.location.href="/TestSessionInfoWeb/logout.do";
										
									},
						complete :  function(){
										//alert('after complete....');
										unblockUI();
									}
				}
			);
			}
			}
	
	function viewRubric (itemIdRubric, itemNumber) {
	
	var param = "&itemId="+itemIdRubric+"&itemNumber="+itemNumber;
	$("#itemId").val(itemIdRubric);
	$("#itemNumber").val(itemNumber);
	
	var isHidden = $('#rubricDialogID').is(':hidden');  
	if(isHidden){
		$.ajax(
		{
				async:		true,
				beforeSend:	function(){
								blockUI();

							},
				url:		'rubricViewDisplay.do',
				type:		'POST',
				data:		param,
				dataType:	'json',
				success:	function(data, textStatus, XMLHttpRequest){	
										
								var questionNumber = $("#itemNumber").val();	
								var counter = 0;
								var rowCountRubric = $('#rubricTable tr').length;
								var rowCountExemplar = $('#exemplarsTable tr').length;								
								var cellCountExemplar = $('#exemplarsTable tr td').length;
																
								//Rows needs to be deleted, since dynamically new rows are added everytime
								$('#rubricTable tr:not(:first)').remove();
								$('#exemplarsTable tr:not(:first)').remove();
								$("#rubricDialogID").css('height',350);
								 
								 if(cellCountExemplar == 1)	 {
								 	$('#rubricExemplarId').hide();
								 	$("#exemplarNoDataId").show();
								 } else {
								 	$('#rubricExemplarId').show();
								 	$("#exemplarNoDataId").hide();
								 }
								 								 
								 if(data.rubricData.entry) {
								 	$("#rubricNoDataId").hide();
								 	$("#rubricTableId").show();								 								 
								 	for(var i=0;i<data.rubricData.entry.length;i++) {									
										var description = handleSpecialCharacters(data.rubricData.entry[i].rubricDescription);								
										$('#rubricTable tr:last').
											after('<tr><td><center><small>'+
												data.rubricData.entry[i].score+
													'</small></center></td><td><small>'+description+'</small></td></tr>');

										if(data.rubricData.entry[i].rubricExplanation){
											var explanation = handleSpecialCharacters(data.rubricData.entry[i].rubricExplanation);
											var response = handleSpecialCharacters(data.rubricData.entry[i].sampleResponse);
											$('#exemplarsTable tr:last').
												after('<tr><td><center><small>'+
													data.rubricData.entry[i].score+
														'</small></center></td><td><small>'+
															response+
																'</small></td><td><small>'+
																	explanation+
																		'</small></td></tr>');																		
										} else {
											counter++;
											if(counter==data.rubricData.entry.length) {
												$("#exemplarNoDataId").show();
												$("#rubricExemplarId").hide();
											}
										}
									}
								} else {
									$("#exemplarNoDataId").show();
									$("#rubricNoDataId").show();
									$("#rubricExemplarId").hide();
									$("#rubricTableId").hide();									
								}								
								 $("#rubricDialogID").dialog({title:"Question "+questionNumber, resizable:false});
								 if(data.rubricData.entry)
								 $("#rubricDialogID").css('height',350);
								 else
								 $("#rubricDialogID").css("height",'auto');								 						
							},
				error  :    function(XMLHttpRequest, textStatus, errorThrown){
							},
				complete :  function(){
								unblockUI();
							}
				}
			);
		}	
	}
	
	function handleSpecialCharacters(s) {
		s= s.replace(/&nbsp;/g,' ').split('');
		var k;
		for(var i= 0; i<s.length; i++){
			k= s[i];
			c= k.charCodeAt(0);
			s[i]= (function(){
				switch(c){
					case 60: return "&lt;";
					case 62: return "&gt;";
					case 34: return "&quot;";
					case 38: return "&amp;";
					case 39: return "&#39;";
					//For IE
					case 65535: {
						if(!((s[i-1].charCodeAt(0)>=65 && s[i-1].charCodeAt(0)<=90) || (s[i-1].charCodeAt(0)>=97 && s[i-1].charCodeAt(0)<=122)) || !((s[i+1].charCodeAt(0)>=65 && s[i+1].charCodeAt(0)<=90) || (s[i+1].charCodeAt(0)>=97 && s[i+1].charCodeAt(0)<=122)))
							return "&quot;";
						else
							return "&#39;";
					}
					//For Mozila and Safari
					case 65533: {
						if(!((s[i-1].charCodeAt(0)>=65 && s[i-1].charCodeAt(0)<=90) || (s[i-1].charCodeAt(0)>=97 && s[i-1].charCodeAt(0)<=122)) || !((s[i+1].charCodeAt(0)>=65 && s[i+1].charCodeAt(0)<=90) || (s[i+1].charCodeAt(0)>=97 && s[i+1].charCodeAt(0)<=122)))
							return "&quot;";
						else
							return "&#39;";
					}
					default: return k;
				}
			})();
		}
		return s.join('');
	}
	
		function formSave() {
			var itemId =  document.getElementById("itemId").value ;
			var itemSetId = document.getElementById("itemSetId").value  ;
			var itemNumber = document.getElementById("itemNumber").value;
			var rowno = document.getElementById("rowNo").value;
			var param = "&itemId="+itemId+"&itemSetId="+itemSetId+"&rosterId="+$("#rosterId").val() + "&score="+$("#pointsDropDown option:selected").val();    
			var optionValue = $("#pointsDropDown option:selected").val();
			
			if(optionValue == null || optionValue == "" ){
				document.getElementById("message").style.display = 'inline';
				var spanElement = document.getElementById("messageSpan");
				spanElement.innerHTML = "Please select a valid Score";
			}
		
			if($("#pointsDropDown option:selected").val() != ''){
					$.ajax(
						{
								async:		true,
								beforeSend:	function(){
											
												blockUI();
												//alert('before send....');
											},
								url:		'saveDetails.do',
								type:		'POST',
								data:		param,
								dataType:	'json',
								success:	function(data, textStatus, XMLHttpRequest){	
													
													var isSuccess = data.boolean;	
													var spanElement = document.getElementById("messageSpan");
													var scorePointsElement = document.getElementById("scorePoints"+rowno);
													var scoreStatusElement = document.getElementById("scoreStatus"+rowno);
													
													if(isSuccess){
														scorePointsElement.firstChild.nodeValue = $("#pointsDropDown option:selected").val();
														scoreStatusElement.innerHTML = "Complete"; 
														document.getElementById("messageStatus").value = isSuccess;
														document.getElementById("message").style.display = 'inline';	
														spanElement.innerHTML = "<b> Item Scored Successfully </b>";
													}
													else{				
														spanElement.innerHTML = "<b> Item Not Scored </b>";
													}
												
											},
								error  :    function(XMLHttpRequest, textStatus, errorThrown){
												//changes for defect #66003
												window.location.href="/TestSessionInfoWeb/logout.do";
											},
								complete :  function(){
												//alert('after complete....');
												unblockUI();
											}
						}
					);
				}
			}

		function openPopup(rowno, itemNumber) {
				var maxPointsElement = document.getElementById("maxPoints"+rowno);
		        var scoreCutOff = maxPointsElement.firstChild.nodeValue;
		        var titleString = "Item Scoring For Item No "+ itemNumber ;
		      $("#dialogID").dialog({title:titleString, resizable:false, beforeclose: function(event, ui) { stopAudio(); showScoreSelect("true");} });
		        //$("#dialogID").dialog({title:titleString});
		        updateMaxPoints(scoreCutOff);
				
		}
		function blockUI()
		{	
			$("body").append('<div id="blockDiv" style="background:url(/HandScoringWeb/resources/images/transparent.gif);position:fixed;top:0;left:0;width:100%;height:100%;z-index:9999"><img src="/HandScoringWeb/resources/images/loading.gif" style="left:50%;top:40%;position:absolute;"/></div>');
			$("#blockDiv").css("cursor","wait");
			
		}
			
		function unblockUI()
		{
			$("#blockDiv").css("cursor","normal");
			$("#blockDiv").remove();
		}
		function closePopUp(){
			//alert("closed");
			stopAudio();
			//document.getElementById("pointsDropDown").setAttribute("disabled",true);
			$("#dialogID").dialog("close");
		}
			
	function updateMaxPoints(scoreCutOff){
		var select = document.getElementById('pointsDropDown');
		 select.options.length = 0; 
		 addOption(select , "Please Select", "" );
		
		  for(var i=0; i <= scoreCutOff; i++) {  
		    addOption(select,i,i);
		     } 
		}
	
		function updateScore(rowno){
		
			var scoreStatusElement = document.getElementById("scoreStatus"+rowno);
			var scorePointsElement = document.getElementById("scorePoints"+rowno);
			var complete = scoreStatusElement.firstChild.nodeValue;
				complete  = trim(complete);
			if(complete =="Complete"){
		
				var select = document.getElementById('pointsDropDown');
				
				for(var i=0; i< select.options.length; i++){
				if(select.options[i].value == trim(scorePointsElement.firstChild.nodeValue)){
					select.options[i].selected = 'true';
					}
				
				}
			}
		}
	
		function addOption(selectbox,text,value )
		{
			var optn = document.createElement("OPTION");
			optn.text = text;
			optn.value = value;
			selectbox.options.add(optn);
		}
		
		
			 
	function trim(str, chars) {
		return ltrim(rtrim(str, chars), chars);
	}
 
	function ltrim(str, chars) {
		chars = chars || "\\s";
		return str.replace(new RegExp("^[" + chars + "]+", "g"), "");
	}
 
	function rtrim(str, chars) {
		chars = chars || "\\s";
		return str.replace(new RegExp("[" + chars + "]+$", "g"), "");
	}
	
	function hideMessage(){
		document.getElementById("messageSpan").innerHTML = "";
	}