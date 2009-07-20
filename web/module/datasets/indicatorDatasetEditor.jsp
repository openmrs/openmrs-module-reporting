<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
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


	// ======  Tabs: Indicator Dataset Tabs  ================================================

	$('#indicator-dataset-tabs').tabs();
	$('#indicator-dataset-tabs').show();


	// ======  DataTable: Cohort Definition Parameter  ======================================
	
	$('#indicator-dataset-parameter-table').dataTable( {
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
		window.location.href='<c:url value="/module/reporting/manageDatasetDefinitions.list"/>';
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
			<spring:hasBindErrors name="datasetDefinition">  
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

		<div id="indicator-dataset-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#indicator-dataset-basic-tab"><span>Basic</span></a></li>
                <li><a href="#indicator-dataset-advanced-tab"><span>Advanced</span></a></li>
                <li><a href="#indicator-dataset-preview-tab"><span>Preview</span></a></li>
            </ul>
		</div>		
	
		<div id="indicator-dataset-basic-tab">			
			<form:form id="saveForm" commandName="datasetDefinition" method="POST">
			<!-- <form method="post" action="saveCohortDefinition.form"> -->			
				<ul>				
					<li>
						<label class="desc" for="uuid">ID</label>				
						<div>							
							<c:choose>
								<c:when test="${!empty datasetDefinition.uuid}">${datasetDefinition.uuid}</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${!empty datasetDefinition.id}">${datasetDefinition.id}</c:when>
										<c:otherwise>(new)</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>	
						</div>
					</li>				
					<li>
						<label class="desc" for="type">Type</label>			
						<div>
							${datasetDefinition.class.simpleName}
							<!-- <input type="hidden" name="type" value="${datasetDefinition.class.name}" tabindex="1" />  -->
							<form:hidden path="class.name" />							
							
						</div>
					</li>
					<li>
						<label class="desc" for="name">Name</label>			
						<div>
							<form:input path="name" tabindex="1" cssClass="field text medium" />							
							<!--  
							<input type="text" 
									class="field text medium" 
									id="name" 
									name="name"
									value="${datasetDefinition.name}"
									size="50" 
									/>
							-->
						</div>
					</li>
					<li>
						<label class="desc" for="description">Description</label>			
						<div>
							<form:textarea path="description" tabindex="2" cssClass="field text medium" cols="80"/> 
							<!--  	
								<textarea id="description" 
										class="field text short"  tabindex="3"
										name="description">${datasetDefinition.description}</textarea>			
							-->
						</div>
					</li>
					<li>					
						<div align="center">				
							<input id="save-button" name="save" type="submit" class="button" value="Save" tabindex="100" />
							<input id="cancel-button" name="cancel" type="submit" class="button" value="Cancel" tabindex="102"/>
						</div>					
					</li>
				</ul>				
			</form:form>
		</div>

		<div id="indicator-dataset-advanced-tab">			

			<h2>Parameters</h2>
			<form:form id="saveForm" commandName="datasetDefinition" method="POST">
				<ul>
					<li>
						<label class="desc" for="description">Description</label>
						<input type="hidden" name="action" value="addParameters"/>
						<form:hidden path="uuid"/>
							<c:forEach var="indicator" items="${indicators}">					
								<p>
									<input type="checkbox" name="indicatorUuid" value="${indicator.uuid}"/>					
									<strong>${indicator.name}</strong> ${indicator.description} <i>(${indicator.uuid})</i>	<br/>
									${indicator.cohortDefinition.parameterizable.name} ${indicator.cohortDefinition.parameterizable.parameters}
																	
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
				</ul>
			</form:form>



			<h2>Available Indicators</h2>
			<form:form id="saveForm" commandName="datasetDefinition" method="POST">
				<ul>
					<li>
						<label class="desc" for="description">Description</label>			

						<input type="hidden" name="action" value="addIndicators"/>
						<form:hidden path="uuid"/>
							<c:forEach var="indicator" items="${indicators}">					
								<p>
									<input type="checkbox" name="indicatorUuid" value="${indicator.uuid}"/>					
									<strong>${indicator.name}</strong> ${indicator.description} <i>(${indicator.uuid})</i>	<br/>
									${indicator.cohortDefinition.parameterizable.name} ${indicator.cohortDefinition.parameterizable.parameters}
																	
								<!-- Hide the parameter mapping behind a modal dialog window -->
								<br/><strong>Parameter Mapping</strong>
								<c:if test="${empty indicator.cohortDefinition.parameterizable.parameters}"><i>There are no parameters for this indicator</c:if>
								<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}">
									${parameter.label}	(${parameter.name})				
								</c:forEach>
								<br/>								
							</c:forEach>
					</li>
					<li>					
						<div align="center">				
							<input id="save-button" name="save" type="submit" class="button" value="Save" tabindex="100" />
							<input id="cancel-button" name="cancel" type="submit" class="button" value="Cancel" tabindex="102"/>
						</div>					
					</li>
				</ul>
			</form:form>
		</div>
		

		<div id="indicator-dataset-preview-tab">
				
			<c:choose>
			
				<c:when test="${!empty datasetDefinition.uuid}">
					<form:form id="saveForm" commandName="datasetDefinition" method="POST">
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
									${datasetDefinition.uuid}
								</div>
							</li>				
							<li>
								<label class="desc" for="name">Name</label>			
								<div>
									${datasetDefinition.name} (<i>${datasetDefinition.class.simpleName}</i>)
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

