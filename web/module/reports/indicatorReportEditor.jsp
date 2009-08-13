<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ include file="../localHeader.jsp"%>


<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>

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

	// Redirect to the listing page
	$('#cancel-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/manageReports.list"/>';
	});

	// Call client side validation method
	$('#save-button').click(function(event){
		// no-op
	});	
	
} );

</script>

<style type="text/css">
.errors { 
	margin-left:200px; 
	margin-top:20px; 
	margin-bottom:20px;	
	font-family:Verdana,Arial,sans-serif; 
	font-size:12px;
}
</style>


<div id="page">

	<div id="container">

		<div class="errors"> 
			<spring:hasBindErrors name="reportDefinition">  
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
                <li><a href="#report-schema-advanced-tab"><span>Advanced</span></a></li>
                <li><a href="#report-schema-preview-tab"><span>Preview</span></a></li>
            </ul>
		
			<div id="report-schema-basic-tab">			
				<form:form id="saveForm" commandName="reportDefinition" method="POST">
					<ul>				
						<li>
							<label class="desc" for="uuid">ID</label>				
							<div>							
								<c:choose>
									<c:when test="${!empty reportDefinition.uuid}">${reportDefinition.uuid}</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${!empty reportDefinition.id}">${reportDefinition.id}</c:when>
											<c:otherwise>(new)</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>	
							</div>
						</li>				
						<li>
							<label class="desc" for="type">Type</label>			
							<div>
								${reportDefinition.class.simpleName}
								<form:hidden path="class.name" />								
							</div>
						</li>
						<li>
							<label class="desc" for="name">Name</label>			
							<div>
								<form:input path="name" tabindex="1" cssClass="field text medium" />								
							</div>
						</li>
						<li>
							<label class="desc" for="description">Description</label>			
							<div>
								<form:textarea path="description" tabindex="2" cssClass="field text medium" cols="80"/> 						
							</div>
						</li>
						<li>					
							<div align="center">				
								<input id="save-button" name="save" type="submit" value="Save" />
								<button id="cancel-button" name="cancel">Cancel</button>
							</div>					
						</li>
					</ul>				
				</form:form>
			</div>
	
			<div id="report-schema-advanced-tab">			
				<h2>${reportDefinition.name}</h2>
				<p>
					Design your report by adding new indicators below.
				</p>
				
				<form:form id="saveForm" commandName="reportDefinition" method="POST">						
					<ul>
						<li>
							<label class="desc" for="description">Selected indicators</label>	
							<div>
								<ul>
									<c:forEach var="mappedDataset" items="${reportDefinition.dataSetDefinitions}">
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
				</form:form>
			</div>
			
	
			<div id="report-schema-preview-tab">

				<c:choose>				
					<c:when test="${!empty reportDefinition.uuid}">
						<h2>${reportDefinition.name}</h2>					
						<form action="${pageContext.request.contextPath}/module/reporting/evaluateReport.form" method="POST">
							<input type="hidden" name="uuid" value="${reportDefinition.uuid}"/>
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
											<c:forEach var="mappedDataset" items="${reportDefinition.dataSetDefinitions}">
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
											<c:forEach var="parameter" items="${reportDefinition.parameters}">
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

