<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ include file="../manage/localHeader.jsp"%>


<style type="text/css">
	#wrapper { width: 60%; margin-left: 20%; margin-right: 20%;  }
	#container, #page { text-align:center; width: 100%; } 
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	fieldset { padding: 5px; margin:5px; }
	fieldset legend { font-weight: bold; background: #E2E4FF; padding: 6px; border: 1px solid black; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	.errors { margin-left:200px; margin-top:20px; margin-bottom:20px; font-family:Verdana,Arial,sans-serif; font-size:12px; }
	#report-schema-basic-tab { 
		margin: 50px; 
	}

</style>



<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<!-- 
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
 -->
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {

	// ======  Tabs: Cohort Definition Tabs  ================================================

	//$('#report-schema-tabs').tabs();
	//$('#report-schema-tabs').show();


	// ======  DataTable: Cohort Definition Parameter  ======================================
	
	$('#report-schema-parameter-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );			

	$('#report-schema-indicator-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );			
	
	// Redirect to the listing page
	$('#back-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/reports/manageReports.list"/>';
	});

	// Call client side validation method
	$('#save-button').click(function(event){
		// no-op
	});	

	// Disable the parameters 
	$('#startDate').attr('value','01/01/2009');
	$('#endDate').attr('value','31/01/2009');
	$('#startDate').attr('disabled','disabled');
	$('#endDate').attr('disabled','disabled');
	$('#location').attr('disabled','disabled');

	// ======  Editable  ===============================================
	
    $('.editable').editable('${pageContext.request.contextPath}/module/reporting/reports/editIndicatorReport.form', { 
        type      : 'text',
        height	  : '24px',
        width	  : '500px',
        cancel    : 'Cancel',
        submit    : 'Save',
        indicator : 'Saving...',
        tooltip   : 'Click to edit...'
    });


	// ======  Indicator Results  ===============================================

    //<c:forEach var="indicator" items="${indicatorReport.indicators}">
	//    $("#result-${indicator.uuid}").click(function(){
	//    	$("#result-${indicator.uuid}").load("${pageContext.request.contextPath}/module/reporting/indicators/evaluatePeriodIndicator.form?uuid=${indicator.uuid}");
	//    });    
    //</c:forEach>


	// ======  Dialog : Indicator Dataset ===============================================

	$("#add-indicator-button").click(function(event){ 
		showReportingDialog({ 
			title: 'Add Period Indicator', 
			url: '<c:url value="/module/reporting/indicators/addPeriodIndicator.form?reportUuid=${indicatorReport.reportDefinition.uuid}&action=add"/>',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});

	$(".preview-indicator-button").click(function(event){ 
		showReportingDialog({ 
			title: 'Preview Period Indicator', 
			url: '<c:url value="/module/reporting/indicators/previewPeriodIndicator.form"/>?uuid=' + this.id,
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});

	$("#preview-report-button").click(function(event){ 
		showReportingDialog({ 
			title: 'Preview Period Indicator Report', 
			url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${indicatorReport.reportDefinition.uuid}&type=${indicatorReport.reportDefinition.class.name}',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});


	
} );

</script>

<div id="page">

	<div id="container">

		<div class="errors"> 
			<spring:hasBindErrors name="indicatorReport">  
				<font color="red"> 
					<h3><u>Please correct the following errors</u></h3>   
					<ul class="none">
						<c:forEach items="${errors.allErrors}" var="error">
							<li><spring:message code="${error.code}" text="${error.defaultMessage}"/></li>
						</c:forEach> 
					</ul> 
				</font>  
			</spring:hasBindErrors>
		</div>
	
		<h1>Period Indicator Report Designer</h1>


			<div id="wrapper">
				<form:form id="saveForm" commandName="indicatorReport" method="POST">						



					<c:if test="${empty indicatorReport.reportDefinition.uuid}">
						<ul>
							<li>
							
								<label class="desc" for="name">Name</label>	
								<div>								
									<form:input path="reportDefinition.name" tabindex="1" cssClass="field text medium" />								
								</div>
							</li>
							<li>
								<label class="desc" for="description">Description</label>	
								<div>
									<form:input path="reportDefinition.description" tabindex="2" cssClass="field text medium" /> 
								</div>
							</li>

							<li>					
								<div align="center">				
									<input id="save-button" name="save" type="submit" value="Save" />
									<button id="cancel-button" name="cancel">Cancel</button>
								</div>					
							</li>
						</ul>
					</c:if>


					<c:if test="${!empty indicatorReport.reportDefinition.uuid}">
					
						<ul>
							<li>
							
								<label class="desc" for="name">Name</label>	
								<div href="#" nowrap="" id="name:${indicatorReport.reportDefinition.uuid}" 
									class="editable field text medium">${indicatorReport.reportDefinition.name}</div>
							</li>
							<li>
							
								<label class="desc" for="name">Description</label>	
								<c:choose>
									<c:when test="${empty indicatorReport.reportDefinition.description}">
										<div nowrap="" id="description:${indicatorReport.reportDefinition.uuid}" 
											class="editable field text medium">Enter a description</div>
									</c:when>
									<c:otherwise>
										<div href="" nowrap="" id="description:${indicatorReport.reportDefinition.uuid}" 
											class="editable field text medium">${indicatorReport.reportDefinition.description}</div>
									</c:otherwise>
								</c:choose>
							</li>
						</ul>

					<%-- 
					<fieldset>
						<legend>Parameters</legend>
						<ul>
							<li>
								<div>
									<label class="desc" for="startDate">Start Date</label>			
									<openmrs:fieldGen type="java.util.Date" 
										formFieldName="startDate" 
										val="" parameters="" />
								</div>
							</li>	
							<li>
								<div>
									<label class="desc" for="endDate">End Date</label>			
									<openmrs:fieldGen type="java.util.Date" 
										formFieldName="endDate" 
										val="" parameters="" />
								</div>																
							</li>	
							<li>
								<div>
									<label class="desc" for="location">Location</label>			
									<openmrs:fieldGen type="org.openmrs.Location" 
										formFieldName="location" 
										val="" parameters="" />
								</div>
							</li>
						</ul>
					</fieldset>
					--%>
					
					
						<ul>
							<li>														
								<label class="desc" for="name">Indicators</label>	
								<div>
									<table id="report-schema-indicator-table" class="display">
										<thead>
											<tr>
												<th>Key</th>
												<th>Display Name</th>
												<th>Indicator</th>
												<th>Design</th>
												<th>Preview</th>
												<th>Remove</th>
											</tr>
										</thead>
										<tbody>																				
											<c:forEach var="column" items="${indicatorReport.reportDefinition.columns}">
												<tr>
													<td width="5%">${column.columnKey}</td>
													<td width="45%">${column.displayName}</td>												
													<td width="40%">
														<c:choose>
															<c:when test="${column.indicator==null}">
																<strong>cannot get cohort indicator</strong>
															</c:when>
															<c:otherwise>
																<strong>${column.indicator.cohortDefinition.parameterizable.name}</strong>
															</c:otherwise>
														</c:choose>
														
														<%-- 
														<c:forEach var="property" items="${indicator.cohortDefinition.parameterizable.configurationProperties}">
															<c:if test="${!empty property.value}"> 															
																${property.field.name}=${property.value.name} (${property.class.simpleName})
															</c:if>									
														</c:forEach>
														<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}">
															<c:if test="${!empty parameter.defaultValue}"> 															
																${parameter.name}=${parameter.defaultValue} (${parameter.class.simpleName})
															</c:if>
														</c:forEach>
														--%>
													</td>
													<td width="1%" align="center">
														<a href="#" id="${column.indicator.uuid}" class="edit-indicator"><img src="<c:url value="/images/edit.gif"/>" border="0"/></a>
													</td>
													<td width="1%" align="center">
														<a href="#" id="${column.indicator.uuid}" class="preview-indicator-button"><img src="<c:url value="/images/play.gif"/>" border="0"/></a>
													</td>
													<td width="1%" align="center">
														<a href="#" id="${column.indicator.uuid}" class="remove-indicator"><img src="<c:url value="/images/trash.gif"/>" border="0"/></a>
													</td>
													<%-- 
													<td wdith="5%" align="center">
													   <span id="result-${indicator.uuid}" class="result">?</span>
													</td>
													--%>
												</tr>									
											</c:forEach>
										</tbody>
									</table>
								</div>	
							</li>		
						</ul>
					
						<ul>
							<li>
								<div align="center">
									<input id="add-indicator-button" type="button" value="Add an indicator"/>
									<input id="preview-report-button" type="button" value="Preview report"/>
									<input id="back-button" type="button" value="Back to reports"/>									
								</div>						
							</li>
						</ul>
						
					
					</c:if>
				</form:form>
			</div>
			
	</div>
</div>

