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

	// Set the UUID after a cohort definition has been selected
	$("#cohortDefinitionName").result(function(event, data, formatted) {
		$("#cohortDefinitionUuid").val(data.name);
		$("#cohortDefinitionSpan").html(data.name + " " + data.description);
		$("#cohortDefinitionSpan").show();
		$("#cohortDefinitionName").hide();
		
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
	
	// Cancel button
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
				name: "${cohortDefinition}", 
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
		</div>
		
	
		<div id="indicator-basic-tab">
			<form method="post" action="${pageContext.request.contextPath}/module/reporting/saveIndicator.form">			
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
							<label class="desc" for="parameters">Parameters</label>
							<div>
								<table id="indicator-parameter-table" class="display">
									<thead>
										<tr>
											<th width="1px">Prompt</th>
											<th width="1px">Required</th>
											<th>Label</th>
											<th>Name</th>
										</tr>
									</thead>
									<tbody>										
										<c:forEach var="parameter" items="${indicator.parameters}">
											<tr>
												<td>yes</td>
												<td>yes</td>
												<td>${parameter.label}</td> 
												<td>${parameter.name}</td>													
											</tr>
										</c:forEach>
									</tbody>
									<tfoot>
										<tr>
											<th colspan="4" align="center">
												<a href="<c:url value='/module/reporting/indicatorParameter.form?uuid=${indicator.uuid}'/>">add parameter</a>
											</th>
										</tr>									
									</tfoot>
								</table>						
							</div>
						</li>
						<li class="buttons">
							<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<input id="cancel-button" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
						</li>
					</ul>
				</div>
			</form>
		</div>
		
		
		<div id="indicator-advanced-tab">

			<form method="post" action="${pageContext.request.contextPath}/module/reporting/saveIndicator.form">			
				<input type="hidden" id="uuid" name="uuid" value="${indicator.uuid}"/>

				<h2>Map the parameters of the indicator to those in the cohort definition: </h2>


				<ul>
					<li>
						<div>
							<table id="indicator-parameter-mapping-table" class="display">
								<thead>
									<tr>
										<th>parameters to map</th>
										<th>inherit from cohort definition</th>
										<th>choose from indicator</th>
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
												${parameter.name}
											</td>
											<td>
												<input type="radio" name="source_${parameter.name}" value="choose"/>
												<select>
													<option></option>
													<c:if test="${empty indicator.parameters}">
														<option>(add more parameters)</option>
													</c:if>
													<c:forEach var="mappedParameter" items="${indicator.parameters}">										
														<option>${mappedParameter.name}</option>
													</c:forEach>
												</select>							
												<a href="<c:url value='/module/reporting/indicatorParameter.form?uuid=${indicator.uuid}'/>">
													<img src="<c:url value='/images/add.gif'/>" border='0'/>
												</a>
											</td>									
			
										</tr>
									</c:forEach>
								</tbody>
								<tfoot>			
								</tfoot>
							</table>
						</div>
					</li>
					<li class="buttons">
						<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
						<input id="cancel-button" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
					</li>
				</ul>

			</form>
			
		



	
		</div>
	</div><!--#container-->
</div><!-- #page -->


<%@ include file="/WEB-INF/template/footer.jsp"%>