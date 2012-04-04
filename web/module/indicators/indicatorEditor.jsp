<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/indicators/manageIndicators.form" />


<!-- Wufoo Form CSS and Javascript
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
 -->

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {


	// ======  Tabs: Indicator Tabs =======================================================

	$('#indicator-tabs').tabs();
	//$('#indicator-tabs').show();

	
	// ======  Dialog: Add Parameter =======================================================
	
	$("#indicator-parameter-dialog").dialog({
		bgiframe: true,			
		autoOpen: false,
		title:"Add Parameter",
		modal:true,
		draggable:false,
		width:"50%"
	});

	$("#close-button").click(function(){
		$("#indicator-parameter-dialog").dialog('close');
	});

	// ======  Dialog : Indicator Dataset ===============================================


	$("#add-parameter-button").click(function(event){ 
		//$("#indicator-parameter-dialog").dialog('open');

		showReportingDialog({ 
			title: 'Add Parameter', 
			url: '<c:url value="/module/reporting/parameters/parameter.form?uuid=${indicator.uuid}&type=${indicator['class'].name}&redirectUrl=${redirectUrl}"/>',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});

	
	// ======  DataTable: Indicator Dataset ===============================================

	/*
	$('#indicator-dataset-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	*/

	// ======  DataTable: Indicator Parameters ===============================================
	
	$('#indicator-parameter-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	
	// ======  DataTable: Indicator Parameter Mapping ===============================================

	$('#indicator-parameter-mapping-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

	
	
	// ======  Autocomplete: Cohort Definition ===============================================

	function formatItem(row) {
		return row[0] + " (<strong>id: " + row[1] + "</strong>)";
	}
	function formatResult(row) {
		return row[0].replace(/(<.+?>)/gi, '');
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

	// Hide the field if it's populated
	if ($("#cohortDefinitionName").val() != '') { 
		$("#cohortDefinitionName").hide();
	}
	
	// Highlight the field whenever the user enters it
	$("#cohortDefinitionName").bind('focus', function() {
		this.select();
	}); 
	
	// Show the field and hide the name 
	$("#cohortDefinitionSpan").click(function() {
		//this.hide();
		$("#cohortDefinitionSpan").hide();
		$("#cohortDefinitionName").show();
		$("#cohortDefinitionName").focus();
				
	}); 

	// Set the UUID after a cohort definition has been selected
	$("#cohortDefinitionName").result(function(event, data, formatted) {
		$("#cohortDefinitionUuid").val(data.uuid);
		$("#cohortDefinitionSpan").html(data.name + " " + data.description);
		$("#cohortDefinitionSpan").show();
		$("#cohortDefinitionName").hide();

		// Submit the page to allow it to refresh
		$("#saveIndicatorForm").submit();
	});

	// ======  Button: Cancel Button ========================================
	
	// Redirect to the listing page
	$('#cancel-indicator-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/indicators/manageIndicators.list"/>';
	});	

	$('#show-parameter-mapping').click(function(event){
		$('#parameter-mapping').show();
	});
	$('#hide-parameter-mapping').click(function(event){
		$('#parameter-mapping').hide();
	});
	
});


</script>

<script type="text/javascript">

var cohortDefinitions = [
		<c:forEach var="cohortDefinition" items="${cohortDefinitions}" varStatus="varStatus">
			{ 	
				id: ${cohortDefinition.id}, 
				uuid: "${cohortDefinition.uuid}", 
				name: "${cohortDefinition.name}", 
				description: "(<i>${cohortDefinition['class'].simpleName}</i>)"
			}
			<c:if test="${!varStatus.last}">,</c:if>
		</c:forEach>	            	
	];
</script>

<style>
#parameter-mapping { 
	padding:10px;
	width:400px;
	margin:10px;
}

.indicator-parameter-table { 
	width: 100%;
}
.ui-tabs .ui-tabs-hide {
     display: none;
}

form ul { margin:0; padding:0; list-style-type:none; width:100%; }
form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }

</style>

<div id="page">
	<div id="container">
	
	
		<h1>Indicator Editor</h1>
	
		<br/>
	
		<div id="indicator-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#indicator-basic-tab"><span>Basic</span></a></li>
            </ul>
			<div id="indicator-basic-tab">
				<form method="post" id="saveIndicatorForm" 
						action="${pageContext.request.contextPath}/module/reporting/indicators/saveIndicator.form">			
					<input type="hidden" id="uuid" name="uuid" value="${indicator.uuid}"/>				
					<div>
						<ul>		
						
						
							<%-- ===============================================================
												B a s i c   I n f o r m a t i o n 
							   	 =============================================================== --%>
						
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
										class="field textarea small" rows="3" cols="20"
										tabindex="2">${indicator.description}</textarea>				
								</div>					
							</li>
							<li>								
								<div>

									<input type="hidden" id="aggregator" name="aggregator" value="CountAggregator">
								
									<%-- 								
									<label class="inline" for="cohortDefinition">How do you want to aggregate this cohort?</label>
									<c:set var="aggregatorLabels" value="Count,Distinct,Max,Mean,Median,Min,Sum" />
									<c:set var="aggregators" value="CountAggregator,DistinctAggregator,MaxAggregator,MeanAggregator,MedianAggregator,MinAggregator,SumAggregator" />									
									<select id="aggregator" name="aggregator" class="field select"> 
										<c:forEach var="aggregator" items="${aggregators}" varStatus="status">
											<option value="${aggregator}" <c:if test="${aggregator == indicator.aggregator.simpleName}">selected</c:if>>${aggregator}</option>
										</c:forEach>
									</select>
									--%>
								</div>
							</li>
							
							<%-- ===============================================================
												C o h o r t   D e f i n i t i o n
							   	 =============================================================== --%>
							
							<li>
								<label class="desc" for="indicator">Define your indicator</label>
								<div>								
									<span id="cohortDefinitionSpan">
										${indicator.cohortDefinition.parameterizable.name}
									</span>
									
									<input id="cohortDefinitionName" name="cohortDefinition.name" type="text" class="field text large" tabindex="3"
											value="${indicator.cohortDefinition.parameterizable.name}"/>
									
									<input id="cohortDefinitionUuid" name="cohortDefinition.uuid" type="hidden"  tabindex="4" 
											value="${indicator.cohortDefinition.parameterizable.uuid}"/>							
								</div>	
								<br/>
								

							<%-- ===============================================================
												P a r a m e t e r   M a p p i n g
							   	 =============================================================== --%>

								<div>
									<rptTag:mappedField id="indicatorPararameterMapping" label="Parameter Mapping" 
														parentType="${indicator['class'].name}" parentObj="${indicator}" 
														mappedProperty="cohortDefinition" 
														defaultValue="${indicator.cohortDefinition}" nullValueLabel="None" 
														width="375"/>
								</div>
	
								<a id="show-parameter-mapping" href="#">Show</a>
								<a id="hide-parameter-mapping" href="#">Hide</a>
								<div id="parameter-mapping" style="display:none;">
									<fieldset>								
										<legend>Parameter Mapping</legend>									
										<div>
											<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="varstatus">
												<strong>${parameter.name}</strong>:
												<select>
													<option>(choose a parameter mapping)</option>
													<c:forEach var="mappedParameter" items="${indicator.parameters}">										
														<option>${mappedParameter.name}</option>
													</c:forEach>
												</select>
												<br/><br/>
											</c:forEach>
										</div>
									</fieldset>
								</div>
									
								
							</li>


							<%-- ===============================================================
													P a r a m e t e r s
							   	 =============================================================== --%>

							<li>
								<label class="desc">Parameters</label>	
	
	
								<c:set var="redirectUrl" value='/module/reporting/indicators/manageIndicators.list' />								
								<%-- <c:url var="redirectUrl" value='/module/reporting/editCohortDefinition.form?uuid=${param.uuid}&type=${param.type}'/> --%>
								<c:url var="addParameterUrl" value='/module/reporting/parameters/parameter.form?uuid=${indicator.uuid}&type=${indicator['class'].name}&redirectUrl=${redirectUrl}'/>
	

								

								<!--  
								<a href="${addParameterUrl}">Add Parameter</a><br/>
								<a href="#" id="add-parameter-button">Add Parameter</a> <i>(modal window)</i>
								-->
								<div>
									<table id="cohort-definition-parameter-table" class="display">
										<thead>
											<tr>
												<th align="left">Edit</th>
												<th align="left">Name</th>
												<th align="left">Label</th>
												<th align="left">Type</th>
												<th align="left">Collection Type</th>
												<th align="left">Default Value</th>
												<th align="left">Delete</th>
											</tr>	
										</thead>
										<tbody>
											<c:forEach items="${indicator.parameters}" var="parameter" varStatus="varStatus">												
												<c:url var="editParameterUrl" value='/module/reporting/parameters/parameter.form?uuid=${indicator.uuid}&type=${indicator['class'].name}&parameterName=${parameter.name}&redirectUrl=${redirectUrl}'/>																
												<c:url var="deleteParameterUrl" value='/module/reporting/parameters/deleteParameter.form?uuid=${indicator.uuid}&type=${indicator['class'].name}&parameterName=${parameter.name}&redirectUrl=${redirectUrl}'/>
												<tr <c:if test="${varStatus.index % 2 == 0}">class="odd"</c:if>>
													<td valign="top" nowrap="true">
														<a href="${editParameterUrl}"><img src='<c:url value="/images/edit.gif"/>' border="0"/></a>
													</td>
													<td valign="top" nowrap="true">
														${parameter.name}
													</td>
													<td valign="top">
														${parameter.label}
													</td>
													<td valign="top">
														${parameter.type}																									
													</td>
													<td valign="top">
														${parameter.collectionType}
													</td>
													<td valign="top">
														${parameter.defaultValue}
													</td>
													<td valign="top" nowrap="true">
														<a href="${deleteParameterUrl}"><img src='<c:url value="/images/trash.gif"/>' border="0"/></a>
													</td>
												</tr>	
											</c:forEach>
										</tbody>
									</table>
								</div>

							</li>


							<input type="button" id="add-parameter-button" value="Add Parameter"/>
							
							<li class="buttons">
								<input id="save-indicator-button" class="btTxt submit" name="action" type="submit" value="Save" tabindex="7" />
								<input id="cancel-indicator-button" type="button" name="action" value="Cancel">
							</li>
						</ul>
					</div>
				</form>								
			</div>
			
		
			
		</div><!--  #tabs -->
	</div><!--#container-->
</div><!-- #page -->


<div id="indicator-parameter-dialog" title="Add Parameter">

</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>