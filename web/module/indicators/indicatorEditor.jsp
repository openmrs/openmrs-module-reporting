<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>
<%@ include file="../dialogSupport.jsp"%>


<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<!-- 
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
	$("#add-parameter-button").click(function(event){ 
		//$("#indicator-parameter-dialog").dialog('open');

		showReportingDialog({ 
			title: 'Add Parameter', 
			url: '<c:url value="/module/reporting/parameter.form?uuid=${indicator.uuid}&type=${indicator.class.name}&redirectUrl=${redirectUrl}"/>',
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
		$("#saveIndicatorForm").submit();
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


	// ======  Button: Cancel Button ========================================
	
	// Redirect to the listing page
	$('#cancel-indicator-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/manageIndicators.list"/>';
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
				description: "(<i>${cohortDefinition.class.simpleName}</i>)"
			}
			<c:if test="${!varStatus.last}">,</c:if>
		</c:forEach>	            	
	];
</script>

<style>
.indicator-parameter-table { 
	width: 100%;
}
.ui-tabs .ui-tabs-hide {
     display: none;
}
</style>

<div id="page">
	<div id="container">
	
	
		<h1>Indicator Editor</h1>
	
		<br/>
	
		<div id="indicator-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#indicator-basic-tab"><span>Basic</span></a></li>
                <li><a href="#indicator-advanced-tab"><span>Advanced</span></a></li>
            </ul>
			<div id="indicator-basic-tab">
				<form method="post" id="saveIndicatorForm" 
						action="${pageContext.request.contextPath}/module/reporting/saveIndicator.form">			
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
										class="field textarea small" rows="3" cols="20"
										tabindex="2">${indicator.description}</textarea>				
								</div>					
							</li>
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
							<li>
								<div>
								
									<label class="desc" for="cohortDefinition">Map parameters</label>
								
									<div>(hidden)</div>
									
									<%-- 
									<table id="indicator-parameter-mapping-table">
										<thead>
											<tr>
												<th></th>
												<th></th>
											</tr>
										</thead>
										<tbody>										
											<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="varstatus">
												<tr>
													<td>
														<strong>${parameter.name}</strong>
													</td>
													<td>							
														<input type="radio" name="source_${parameter.name}" "value="inherit"/>
														map <strong>${parameter.name}</strong> to default <strong>indicator.${parameter.name}</strong> parameter
														<br/>
														<input type="radio" name="source_${parameter.name}" value="choose"/>
														<select>
															<c:if test="${empty indicator.parameters}">
																<option>(map to an existing parameter)</option>
															</c:if>
															<c:forEach var="mappedParameter" items="${indicator.parameters}">										
																<option>${mappedParameter.name}</option>
															</c:forEach>
														</select>
														<img id="add-parameter-button" src="<c:url value='/images/add.gif'/>" border='0'/>
													</td>									
					
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>			
										</tfoot>
									</table>
									--%>
								</div>
								
								
							</li>


							<li>
								<label class="desc">Parameters</label>	
	
	
								<c:set var="redirectUrl" value='/module/reporting/manageIndicators.list' />								
								<%-- <c:url var="redirectUrl" value='/module/reporting/editCohortDefinition.form?uuid=${param.uuid}&type=${param.type}'/> --%>
								<c:url var="addParameterUrl" value='/module/reporting/parameter.form?uuid=${indicator.uuid}&type=${indicator.class.name}&redirectUrl=${redirectUrl}'/>
	

								

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
												<c:url var="editParameterUrl" value='/module/reporting/parameter.form?uuid=${indicator.uuid}&type=${indicator.class.name}&parameterName=${parameter.name}&redirectUrl=${redirectUrl}'/>																
												<c:url var="deleteParameterUrl" value='/module/reporting/deleteParameter.form?uuid=${indicator.uuid}&type=${indicator.class.name}&parameterName=${parameter.name}&redirectUrl=${redirectUrl}'/>
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
														${parameter.clazz}																									
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
								<span id="testTarget">Does it work?</span>
							</li>



							
							<li class="buttons">
								<input id="save-indicator-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
								<input type="button" id="add-parameter-button" value="Add Parameter"/>	
								<input id="cancel-indicator-button" type="button" name="cancel" value="Cancel">
							</li>
						</ul>
					</div>
				</form>								
			</div>
			
			
			<div id="indicator-advanced-tab">
	
	
				To be determined ... 
	
	
	
			</div>
			
		</div><!--  #tabs -->
	</div><!--#container-->
</div><!-- #page -->


<div id="indicator-parameter-dialog" title="Add Parameter">

</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>