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


	$('#cancel-button').click(function(event){
		// To prevent the submit
		event.preventDefault();

		// Redirect to the listing page
		window.location.href='<c:url value="/module/reporting/manageReports.list"/>';
	});


	$('#save-button').click(function(event){
		// call a client side validation method
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
			<spring:hasBindErrors name="reportSchema">  
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
				<form:form id="saveForm" commandName="reportSchema" method="POST">
					<ul>				
						<li>
							<label class="desc" for="uuid">ID</label>				
							<div>							
								<c:choose>
									<c:when test="${!empty reportSchema.uuid}">${reportSchema.uuid}</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${!empty reportSchema.id}">${reportSchema.id}</c:when>
											<c:otherwise>(new)</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>	
							</div>
						</li>				
						<li>
							<label class="desc" for="type">Type</label>			
							<div>
								${reportSchema.class.simpleName}
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
							<label class="desc" for="description">Indicator Datasets</label>			
							<div>
								<!-- FIXME Should only need to look at the first dataset definition -->
								<ul>
									<c:forEach var="mappedDataset" items="${reportSchema.dataSetDefinitions}">
										<li>${mappedDataset.parameterizable.name}
											<ul>
												<c:forEach var="mappedIndicator" items="${mappedDataset.parameterizable.indicators}">
													<li>${mappedIndicator}</li>
												</c:forEach>
											</ul>
										</li>
									</c:forEach>
								</ul>
								<p>NOTE: Let's make Mapped and Parameterizable more difficult to work with</p>
								
							</div>
						</li>						
						<li>					
							<div align="center">				
								<input id="save-button" name="save" type="submit" class="button" value="Save" />
								<button id="cancel-button" name="cancel" class="button" >Cancel</button>
							</div>					
						</li>
					</ul>				
				</form:form>
			</div>
	
			<div id="report-schema-advanced-tab">			
				<h2>Available Indicators</h2>
				<form:form id="saveForm" commandName="reportSchema" method="POST">
						<li>
							<label class="desc" for="description">Description</label>			
	
							<input type="hidden" name="action" value="addIndicators"/>
								<c:forEach var="indicator" items="${indicators}">					
									<p>
										<input type="checkbox" name="indicatorUuid" value="${indicator.uuid}"/>					
										<strong>${indicator.name}</strong> ${indicator.description} 	
										<i>[cohort definition='${indicator.cohortDefinition.parameterizable.name}', 
											parameters='${indicator.cohortDefinition.parameterizable.parameters}']</i>
																		
									<!-- Hide the parameter mapping behind a modal dialog window -->
									<!-- 
									<br/><strong>Parameter Mapping</strong>
									<c:if test="${empty indicator.parameters}"><i>There are no parameters for this indicator</c:if>
									<c:forEach var="parameter" items="${indicator.parameters}">
										${parameter.label}	(${parameter.name})				
									</c:forEach>
									 -->
									<br/>								
								</c:forEach>
						</li>
						<li>					
							<div align="center">				
								<input id="save-button" name="save" type="submit" class="button" value="Save" tabindex="100" />
								<input id="cancel-button" name="cancel" type="submit" class="button" value="Cancel" tabindex="102"/>
							</div>					
						</li>
						
				</form:form>
			</div>
			
	
			<div id="report-schema-preview-tab">
					
				<c:choose>
				
					<c:when test="${!empty reportSchema.uuid}">
						<form:form id="saveForm" commandName="reportSchema" method="POST">
							<form:hidden path="uuid" />	
							<ul>				
								<li>
									<div>
										<i>Allows a user to evaluate a report.  (Not implemented yet).</i>
									</div>
								</li>				
								<li>
									<label class="desc" for="uuid">ID</label>				
									<div>
										${reportSchema.uuid}
									</div>
								</li>				
								<li>
									<label class="desc" for="name">Name</label>			
									<div>
										${reportSchema.name} (<i>${reportSchema.class.simpleName}</i>)
									</div>
								</li>
								<li>			
									<div align="center">				
										<input id="evaluate-button" name="evaluate" type="submit" class="button" value="Evaluate" tabindex="101" disabled />
										<input id="cancel-button" name="cancel" type="submit" class="button" value="Cancel" tabindex="102"/>
									</div>					
								</li>
							</ul>				
							
						</form:form>
					</c:when>
					<c:otherwise>
						Please create your report first.
					</c:otherwise>
					
				</c:choose>
			</div>
		</div>
		
	</div>
</div>

