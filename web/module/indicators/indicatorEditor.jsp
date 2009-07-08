<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>

<!-- JQuery Engine -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>

<!-- JQuery Data Tables -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>

<!-- JQuery UI -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui-1.6/ui.all.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui-1.6/jquery-ui-1.6.custom.min.js"></script>
<!-- 
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>
 -->
 
<!-- JQuery Autocomplete -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {


	// ======  Tabs: Indicator Tabs =======================================================

	$('#indicator-tabs').tabs();
	$('#indicator-tabs').show();

	
	// ======  DataTable: Indicator Dataset ===============================================

	$('#indicator-dataset-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );

	// ======  Autocomplete: Cohort Definition ===============================================

	function formatItem(row) {
		return row[0] + " (<strong>id: " + row[1] + "</strong>)";
	}
	function formatResult(row) {
		return row[0].replace(/(<.+?>)/gi, '');
	}

	if ($("#cohortDefinitionName").val() != '') { 
		$("#cohortDefinitionName").hide();
	}	
	
	// Set the cohort definition field to be an autocomplete field
	$("#cohortDefinitionName").autocomplete(cohortDefinitions, {
		minChars: 0,
		width: 600,
		scroll:true,
		matchContains: true,
		autoFill: false,
		formatItem: 
			function(row, i, max) {return row.name + " " + row.description;},
		formatMatch: 
			function(row, i, max) {return row.name + " " + row.description;},
		formatResult: 
			function(row) {return row.name;}
	});

	// Highlight the field whenever the user enters it
	$("#cohortDefinitionName").bind('focus', function() {
		this.select();
	}); 


	
	$("#cohortDefinitionSpan").click(function() {
		//this.hide();
		$("#cohortDefinitionSpan").hide();
		$("#cohortDefinitionName").show();
		$("#cohortDefinitionName").focus();
				
	}); 

	
	/*
	// Clear the cohort definition fields on re-focus (save values for 'onblur' event)
	$("#cohortDefinitionName").bind('focus', function() {
		currentCohortName = $("#cohortDefinitionName").val();
		currentCohortUuid = $("#cohortDefinitionUuid").val();		
		$("#cohortDefinitionName").val('');
		$("#cohortDefinitionUuid").val('');
	});
	*/ 
	
	/*
	// When leaving the field, we want to reset the value if nothing was selected
	$("#cohortDefinitionName").bind('blur', function(event) {
		if ($("#cohortDefinitionName").val() == '') { 
			$("#cohortDefinitionName").val(currentCohortName);
			$("#cohortDefinitionUuid").val(currentCohortUuid);
			event.preventDefault();	
		}
	}); 
	*/
	
	// Set the UUID after a cohort definition has been selected
	$("#cohortDefinitionName").result(function(event, data, formatted) {
		$("#cohortDefinitionUuid").val(data.name);
		$("#cohortDefinitionSpan").html(data.name + " " + data.description);
		$("#cohortDefinitionSpan").show();
		$("#cohortDefinitionName").hide();
		
	});

	

	
	// ======  Modal Dialog: Logic Criteria Query ========================================
	
	// Define the dialog window to edit the logic query
	//$('#logic-criteria-dialog-content').load("${pageContext.request.contextPath}/module/reporting/logicQuery.form",{});}

	// Define logic criteria dialog box 
	$('#logic-criteria-dialog').dialog({
		bgiframe: true,			
		autoOpen: false,
		width: 600,
		modal: true,
		buttons: {
			"Cancel": function() { $(this).dialog("close"); }, 
			"Save": function() { $(this).dialog("close"); } 
		}
	});

	$('logic-criteria-popup').hide();
        
	//$('#logic-criteria-dialog').dialog('open');		

	// Add content to Logic Criteria dialog
	//$('#dialog-content').tabs();  
    //$('#logic-criteria-dialog-content').remove();
    //$('#logic-criteria-dialog').append(
    //        '<div id="dialog-content"><ul><li><a href="editCohortDefinition.form?uuid=&type=org.openmrs.module.cohort.definition.StaticCohortDefinition">1000 patient cohort</a></li><li><a href="editCohortDefinition.form?uuid=&type=org.openmrs.module.cohort.definition.StaticCohortDefinition">1000 pt eligible Oct 08</a></li><li><a href="editCohortDefinition.form?uuid=&type=org.openmrs.module.cohort.definition.StaticCohortDefinition">1000pt eligible, no abstraction</a></li></ul></div>'
    //);
    
    //hover states on the static widgets
	//$('#logic-criteria-dialog-link, ul#icons li').hover(
	//	function() { $(this).addClass('ui-state-hover'); }, 
	//	function() { $(this).removeClass('ui-state-hover'); }
	//);	
	
	$('#cancel-button').click(function(event){
		// To prevent the submit
		event.preventDefault();

		// Redirect to the listing page
		window.location.href='<c:url value="/module/reporting/manageIndicators.list"/>';
	});	
	
});


</script>

<script type="text/javascript">

var cohortDefinitions = [
		<c:forEach var="cohortDefinition" items="${cohortDefinitions}" varStatus="varStatus">
			{ 	
				id: ${cohortDefinition.id}, 
				name: "${cohortDefinition.name}", 
				description: "(<i>${cohortDefinition.class.simpleName}</i>)",
				parameters: "${cohortDefinition.parameters}"
			}
			<c:if test="${!varStatus.last}">,</c:if>
		</c:forEach>	            	
	];
</script>



<div id="page">
	<div id="container">
	
	
		<h1>Indicator Editor</h1>
	
		<br/>
	
		<div id="indicator-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#indicator-tabs-details"><span>Details</span></a></li>
                <li><a href="#indicator-tabs-datasets"><span>Data Sets</span></a></li>
            </ul>
		</div>
		
	
		<div id="indicator-tabs-details">
			<form id="form65" name="form65" class="wufoo topLabel" autocomplete="off"
				method="post" action="${pageContext.request.contextPath}/module/reporting/saveIndicator.form">
			
				<input type="hidden" id="uuid" name="uuid" value="${indicator.uuid}"/>
			
				
				<div>
					<ul>		
						<li>
							<label class="desc" for="name">Name</label>
							<div>
								<input id="name" name="name" type="text" class="field text large" 
									value="${indicator.name}" size="20" tabindex="1" />
							</div>
						</li>					
						<li>		
							<label class="desc" for="description">Description</label>
							<div>
								<textarea id="description" name="description" 
									class="field textarea small" rows="10" cols="20"
									tabindex="2">${indicator.description}</textarea>				
							</div>					
						</li>
						<li>
							<label class="desc" for="cohortDefinitionName">Cohort Definition</label>
							<div>
								<span id="cohortDefinitionSpan">
									${indicator.cohortDefinition.parameterizable.name}
								</span>
								<input id="cohortDefinitionName" 
										type="text" class="field text large" name="cohortDefinition.name" tabindex="3"
										value="${indicator.cohortDefinition.parameterizable.name}"/>
								<input id="cohortDefinitionUuid" 
										type="hidden" name="cohortDefinition.uuid" tabindex="4" 
										value="${indicator.cohortDefinition.parameterizable.uuid}"/>							
							</div>		
						</li>
						<li>
							<label class="desc" for="logicQuery">Logic Criteria</label>
							<div>
								<textarea id="logicQuery" 
									name="logicQuery" 
									class="field textarea small" 
									rows="50" cols="20"
									tabindex="5"></textarea>
							</div>					
						</li>
						
						<li>
							<label class="desc" for="aggregator">Aggregation Method</label>
							<div>
								<c:set var="aggregators" value="CountAggregator,DistinctAggregator,MaxAggregator,MeanAggregator,MedianAggregator,MinAggregator,SumAggregator" />
								<select id="aggregator" name="aggregator" class="field select medium" tabindex="6" > 
									<c:forEach var="aggregator" items="${aggregators}" varStatus="status">
										<option value="${aggregator}" <c:if test="${aggregator == indicator.aggregator.simpleName}">selected</c:if>>${aggregator}</option>
									</c:forEach>
								</select>
							</div>
						</li>
						<li class="buttons">
							<input id="saveForm" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<input id="cancel-button" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
						</li>
					</ul>
				</div>
			</form>
		</div>
		
		
		<div id="indicator-tabs-datasets">
			<div id="indicatorDatasetTable">

				<table id="indicator-dataset-table" class="display">
					<thead>
						<tr>
							<th>Key</th>
							<th>Data Set</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
					<tfoot>
						<tr>			
							<td colspan="2" align="center">
								<button name="button1" disabled>Add Mapping</button>
							</td>
						</tr>
					</tfoot>
				</table>
			
			</div>
		</div>
	</div><!--#container-->
</div><!-- #page -->


<div class="logic-criteria-popup">
	<div id="logic-criteria-dialog" title="Edit Logic Criteria Query">
		<div id="logic-criteria-dialog-content">
			<form>
				<ul>
					<li id="foli308" class="">
						<label class="desc" id="title308" for="Field308">Logic Criteria</label>
						<div>
							<textarea id="logicCriteria" 
								name="logicCriteria" 
								class="field textarea small" 
								rows="50" cols="20"
								tabindex="5"></textarea>
						</div>
					</li>
				</ul>	
			</form>
		</div>
	</div>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>