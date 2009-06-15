<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="localHeader.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Indicator Editor</title>



<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>

<!-- Autocomplete -->
<!--  
-->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/localdata.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>

<!--  Javascript 
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery-1.3.1.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>
-->

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#indicator-dataset-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );
} );
</script>


<script type="text/javascript">

// TODO This needs to be dynamically populated.
var cohortDefinitions = [
            	{ id: 1, name: "All patients", description: "(5000 patients)" },
            	{ id: 2, name: "Male patients", description: "(2000 patients)" },
            	{ id: 3, name: "Female patients", description: "(3000 patients)" },
            	{ id: 4, name: "Patients in HIV Program", description: "(352 patients)" },
            	{ id: 5, name: "Patients on ARVs", description: "(646 patients)" },
            	{ id: 6, name: "Patients on second line ARV", description: "(234 patients)" },
            	{ id: 7, name: "Adult patients", description: "(4500 patients)" },
            	{ id: 8, name: "Pediatric patients", description: "(500 patients)" },
            	{ id: 9, name: "My patients", description: "(45 patients)" },
            	{ id: 10, name: "Your patients", description: "(23 patients)" },
            	{ id: 11, name: "Patients with co-infection TB and HIV", description: "(643 patients)" }
            ];
</script>

<script type="text/javascript">
$(document).ready(function() {

	function formatItem(row) {
		return row[0] + " (<strong>id: " + row[1] + "</strong>)";
	}
	function formatResult(row) {
		return row[0].replace(/(<.+?>)/gi, '');
	}

	// Set the UUID after a cohort definition has been selected
	$("#cohortDefinitionName").result(function(event, data, formatted) {
		$("#cohortDefinitionUuid").val(data.id);
    });

	$("cohortDefinitionName").click(function() {
		this.value=''
	}); 

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


	// ================================================================================================
	
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
});

function popup() { 	
	$('#logic-criteria-dialog').dialog('open');		
}


</script>



</head>


<body id="page">

<div id="container">
<form id="form65" name="form65" class="wufoo topLabel" autocomplete="off"
	method="post" action="${pageContext.request.contextPath}/module/reporting/saveIndicator.form">

<input type="hidden" id="uuid" name="uuid" value="${indicator.uuid}"/>

<div class="info">
	<h2>Indicator Editor</h2>
</div>


<ul>		
	<li id="name" class="">
		<label class="desc" id="nameLabel" for="name">Name</label>
		<span>
			<input id="name" 	
				name="name" 
				type="text" 
				class="field text" 
				value="${indicator.name}" 
				size="20" tabindex="1" />
		</span>
	</li>
	
	<li id="description" class="">		
		<label class="desc" id="descriptionLabel" for="description">Description</label>
		<div>
			<textarea id="description" 
				name="description" 
				class="field textarea small" 
				rows="10" cols="20"
				tabindex="2">${indicator.description}</textarea>				
		</div>
	</li>
	<li>
		<label class="desc" id="title308" for="Field308">Cohort Definition</label>
		<div>			
			<input type="text" 
					class="field text large" 
					id="cohortDefinitionName" 
					name="cohortDefinition.name" 
					tabindex="3"/>
			<br/>
			<input type="hidden" 
					id="cohortDefinitionUuid" 
					name="cohortDefinition.uuid" 
					tabindex="4"
					value=""/>
			
		</div>
	</li>
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
	
	<li id="foli308" class="">
		<label class="desc" id="title308" for="Field308">Aggregation Method</label>
		<div>
			<select id="Field308" name="Field308" class="field select medium" tabindex="6" > 
				<option></option>
				<option value="COUNT">COUNT</option>
				<option value="MAX">MAX</option>
				<option value="MIN">MIN</option>
				<option value="SUM">SUM</option>
			</select>
		</div>
	</li>
	<li class="buttons">
		<input id="saveForm" class="btTxt submit" type="submit" value="Save" tabindex="7" />
		<input id="saveForm" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
	</li>
</ul>
</form>

<div class="info">
	<h2>Indicator Datasets</h2>
</div>
<table id="indicator-dataset-table" class="display">
	<thead>
		<tr>
			<th>Key</th>
			<th>Data Set</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>1.a.</td>
			<td>Data Quality Dataset #1</td>
		</tr>
		<tr>
			<td>1.d.</td>
			<td>Data Quality Dataset #2</td>
		</tr>
		<tr>
			<td>1.c.</td>
			<td>Data Quality Dataset #3</td>
		</tr>
		<tr>
			<td>1.b.</td>
			<td>Data Quality Dataset #4</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>			
			<td colspan="6" align="left">
				<button name="button1" disabled>Add Mapping</button>
			</td>
		</tr>
	</tfoot>
</table>
</div>

</div><!--container-->


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


</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>