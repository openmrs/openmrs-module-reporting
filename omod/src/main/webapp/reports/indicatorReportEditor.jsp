<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>
<%@ include file="../localHeader.jsp"%>


<style type="text/css">

form ul { margin:0; padding:0; list-style-type:none; width:100%; }
form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }

.errors { 
	margin-left:200px; 
	margin-top:20px; 
	margin-bottom:20px;	
	font-family:Verdana,Arial,sans-serif; 
	font-size:12px;
}
.result { font-size: 3em; font: bold; font-color:red; background-color: #FFFFD6; padding-left: 10px; padding-right: 10px; } 
.title { font-size: 2.5em; font: bold,underline; } 
.description { font-size: 2.0em; }
.descriptionEmpty { font-size: 2.0em; font:italic; }

.edit:hover { background-color: #FFFFD6; text-decoration:none; padding: 5px; border: 1px solid black; }


</style>



<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<!-- 
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
 -->
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {


	// ======  Tabs: Cohort Definition Tabs  ================================================

	$('#report-schema-tabs').tabs();
	$('#report-schema-tabs').show();


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

	
    $('.edit').editable('${pageContext.request.contextPath}/module/reporting/reports/editIndicatorReport.form', { 
        type      : 'textarea',
        rows	  : '2',
        cols	  : '30',
        cancel    : 'Cancel',
        submit    : 'Save',
        indicator : 'Saving...',
        tooltip   : 'Click to edit...'
    });


    <c:forEach var="indicator" items="${indicatorReport.indicators}">
	    $("#result-${indicator.uuid}").click(function(){
	    	$("#result-${indicator.uuid}").load("${pageContext.request.contextPath}/module/reporting/reports/evaluateIndicator.form?uuid=${indicator.uuid}");
	    });    
    </c:forEach>
	
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
	
		<h1>Indicator Report Editor</h1>

		<div id="report-schema-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#report-schema-basic-tab"><span>Basic</span></a></li>
                <!-- 
                <li><a href="#report-schema-advanced-tab"><span>Advanced</span></a></li>
                <li><a href="#report-schema-preview-tab"><span>Preview</span></a></li>
                 -->
            </ul>
		
			<div id="report-schema-basic-tab">			
				<springform:form id="saveForm" commandName="indicatorReport" method="POST">						

					<c:if test="${empty indicatorReport.reportDefinition.uuid}">
						<ul>
							<li>
							
								<label class="desc" for="name">Name</label>	
								<div>								
									<springform:input path="reportDefinition.name" tabindex="1" cssClass="field text medium" size="100"/>								
								</div>
							</li>
							<li>
								<label class="desc" for="description">Description</label>	
								<div>
									<springform:textarea path="reportDefinition.description" tabindex="2" cssClass="field text medium" cols="120"/> 
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
								<div nowrap="" id="name:${indicatorReport.reportDefinition.uuid}" class="edit title">${indicatorReport.reportDefinition.name}</div>
							</li>
							<li>
								<c:choose>
									<c:when test="${empty indicatorForm.reportDefinition.description}">
										<div nowrap="" id="description:${indicatorReport.reportDefinition.uuid}" class="edit descriptionEmpty">Enter a description</div>
									</c:when>
									<c:otherwise>
										<div nowrap="" id="description:${indicatorReport.reportDefinition.uuid}" class="edit description">${indicatorForm.reportDefinition.description}</div>
									</c:otherwise>
								</c:choose>
							</li>
							<li>														
								<div>
									<table id="report-schema-indicator-table">
										<thead>
											<tr>
												<th>Key</th>
												<th>Display Name</th>
												<th>Indicator</th>
												<th>Result</th>
											</tr>
										</thead>
										<tbody>																				
											<c:forEach var="indicator" items="${indicatorReport.indicators}">
												<tr>
													<td width="5%">${indicator.name}</td>
													<td width="10%">${indicator.name}</td>												
													<td width="20%">
														<strong>${indicator.name}</strong><br/>
														<c:forEach var="property" items="${indicator.cohortDefinition.parameterizable.configurationProperties}">
															<c:if test="${!empty property.value}"> 															
																${property.field.name}=${property.value.name} (${property['class'].simpleName})
															</c:if>									
														</c:forEach>
														<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}">
															<c:if test="${!empty parameter.defaultValue}"> 															
																${parameter.name}=${parameter.defaultValue} (${parameter['class'].simpleName})
															</c:if>
														</c:forEach>
													</td>
													<td wdith="5%" align="center">
													   <span id="result-${indicator.uuid}" class="result">?</span>
													</td>
												</tr>									
											</c:forEach>
										</tbody>
									</table>
								</div>	
							</li>					
							<li>
								<div>
									<input id="add-indicator-button" type="button" value="Add an indicator"/>
									<input id="back-button" type="button" value="<spring:message code="reporting.Report.backToReports"/>"/>									
								</div>						
							</li>
						</ul>
					</c:if>
				</springform:form>
			</div>
	
	
	
	
			<!-- report-schema-advanced-tab -->
			<div id="" style="display:none">			
				<h2>${indicatorReport.reportDefinition.name}</h2>
				<p>
					Design your report by adding new indicators below.
				</p>
				
				<springform:form id="saveForm" commandName="indicatorReport.reportDefinition" method="POST">						
					<ul>
						<li>
							<label class="desc" for="description">Selected indicators</label>	
							<div>
								<ul>
									<c:forEach var="mappedDataset" items="${indicatorReport.reportDefinition.dataSetDefinitions}">
										<c:forEach var="mappedIndicator" items="${mappedDataset.parameterizable.indicators}">
											<li>
												<strong>${mappedIndicator.value.parameterizable.name}</strong>
												${mappedIndicator.value.parameterizable.description}
											</li>
										</c:forEach>
									</c:forEach>
								</ul>
							</div>
						</li>							
						<li>
							<label class="desc" for="description">Available indicators</label>			
							<input type="hidden" name="action" value="addIndicators"/>
							<c:forEach var="indicator" items="${indicators}">					
								<p>
									<input type="checkbox" name="indicatorUuid" value="${indicator.uuid}"/>					
									<strong>${indicator.name}</strong> ${indicator.description}
									<%--   
									<i>[cohort definition='${indicator.cohortDefinition.parameterizable.name}', 
										parameters='${indicator.cohortDefinition.parameterizable.parameters}']</i>
									--%>					
								<!-- Hide the parameter mapping behind a modal dialog window -->
								<!-- 
								<br/><strong>Parameter Mapping</strong>
								<c:if test="${empty indicator.parameters}"><i>There are no parameters for this indicator</c:if>
								<c:forEach var="parameter" items="${indicator.parameters}">
									${parameter.label}	(${parameter.name})				
								</c:forEach>
								 -->
							</c:forEach>
						</li>
 						
						<li>					
							<div align="center">				
								<input id="save-button" name="save" type="submit" value="Save" />
								<button id="cancel-button" name="cancel">Cancel</button>
							</div>					
						</li>
					</ul>
				</springform:form>
			</div>
			
			<!-- report-schema-preview-tab -->
			<div id="" style="display:none">

				<c:choose>				
					<c:when test="${!empty indicatorReport.reportDefinition.uuid}">
						<h2>${indicatorReport.reportDefinition.name}</h2>					
						<form action="${pageContext.request.contextPath}/module/reporting/evaluateReport.form" method="POST">
							<input type="hidden" name="uuid" value="${indicatorReport.reportDefinition.uuid}"/>
							<input type="hidden" name="action" value="evaluate"/>
							<ul>				
								<li>
									<label class="desc" for="renderAs">Render as</label>	

									<input type="radio" name="renderAs" value="CSV" checked> CSV
									<input type="radio" name="renderAs" value="TSV"> TSV
									<input type="radio" name="renderAs" value="XLS"> XLS

								</li>
														
<%-- 														
								<li>
									<label class="desc" for="description">Selected indicators</label>	
									<div>
										<ul>
											<c:forEach var="mappedDataset" items="${indicatorReport.reportDefinition.dataSetDefinitions}">
												<c:forEach var="mappedIndicator" items="${mappedDataset.parameterizable.indicators}">
													<li>
														<strong>${mappedIndicator.value.parameterizable.name}</strong>
														${mappedIndicator.value.parameterizable.description}
													</li>
												</c:forEach>
											</c:forEach>
										</ul>
									</div>
								</li>
--%>								
								
								<li>
									<label class="desc" for="description">Parameters</label>	
									<div>
										<ul>
											<c:forEach var="parameter" items="${indicatorReport.reportDefinition.parameters}">
												<li>
													<label class="desc" for="${parameter.name}">${parameter.label}</label>
													<span>
														<input type="${parameter.name}" />
													</span>
												</li>
											</c:forEach>
										</ul>
									</div>
								</li>							
										
										
								<li>			
									<div align="center">				
										<input id="evaluate-button" name="evaluate" type="submit" value="Evaluate"/>
										<button id="cancel-button" name="cancel">Cancel</button>
									</div>					
								</li>
							</ul>				
							
						</form>
					</c:when>
					<c:otherwise>
						Please create your report first.
					</c:otherwise>
					
				</c:choose>
			</div>
		</div>
		
	</div>
</div>

