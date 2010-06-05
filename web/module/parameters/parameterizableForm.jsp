<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>
<%@ include file="../localHeader.jsp"%>
<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>

<script type="text/javascript" charset="utf-8">
var $j = jQuery.noConflict();
$j(document).ready(function() {


	// ======  Tabs: Cohort Definition Tabs  ================================================

	$j('#cohort-definition-tabs').tabs();
	$j('#cohort-definition-tabs').show();


	// ======  DataTable: Cohort Definition Parameter  ======================================
	
	$j('#cohort-definition-parameter-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );			


	$j('#cancel-button').click(function(event){
		// To prevent the submit
		event.preventDefault();

		// Redirect to the listing page
		window.location.href='<c:url value="/module/reporting/manageCohortDefinitions.list"/>';
	});


	$j('#save-button').click(function(event){
		// To prevent the submit
		//event.preventDefault();
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
			<spring:hasBindErrors name="parameterizable">  
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
	
		<h1>Cohort Definition Editor</h1>

		<br/>


		<div id="cohort-definition-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#cohort-definition-basic-tab"><span>Basic</span></a></li>
                <li><a href="#cohort-definition-preview-tab"><span>Preview</span></a></li>
            </ul>
		</div>		
	
		<div id="cohort-definition-basic-tab">
			
			<springform:form id="saveForm" commandName="parameterizable" method="POST">
				<ul>				
					<li>
						<label class="desc" for="uuid">ID</label>				
						<div>							
							<c:choose>
								<c:when test="${!empty parameterizable.uuid}">${parameterizable.uuid}</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${!empty parameterizable.id}">${parameterizable.id}</c:when>
										<c:otherwise>(new)</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>	
						</div>
					</li>				
					<li>
						<label class="desc" for="type">Type</label>			
						<div>
							${parameterizable.class.simpleName}
							<!-- <input type="hidden" name="type" value="${parameterizable.class.name}" tabindex="1" />  -->
							<springform:hidden path="class.name" />							
							
						</div>
					</li>
					<li>
						<label class="desc" for="name">Returns</label>			
						<div>
							${parameterizable}					
						</div>
					</li>
					<li>
						<label class="desc" for="name">Name</label>			
						<div>
							<springform:input path="name" tabindex="1" cssClass="field text medium"/>														
						</div>
					</li>
					<li>
						<label class="desc" for="description">Description</label>			
						<div>
							<springform:textarea path="description" tabindex="2" cols="70" cssClass="field text long"/> 
						</div>
					</li>
	
	
					<li>
						<label class="desc" for="parameter">Parameters</label>			
						
						<div>
							<table id="cohort-definition-parameter-table" class="display">
								<thead>
									<tr>
										<th align="left">Name</th>
										<th align="left">Old Default Value</th>
										<th align="left">New Default Value</th>
										<th width="1%" align="center">Required?</th>
										<th width="1%" align="center">User<br/>Specified</th>
									</tr>	
								</thead>
								<tbody>			
									<c:forEach items="${parameterizable.parameters}" var="p" varStatus="varStatus">
										<tr>
											<td>${p.name}</td>
											<td>${p.defaultValue}</td>
											<td>					
												<springform:input path="${p.name}" tabindex="${(varStatus.index*5)+1}"/>
											</td>
											<td>
												<springform:checkbox path="parameters[${varStatus.index}].required" tabindex="${(varStatus.index*5)+2}" />
											</td>
											<td>
												<springform:checkbox path="parameters[${varStatus.index}].allowUserInput" tabindex="${(varStatus.index*5)+2}" />
											</td>
										</tr>
									</c:forEach>
								</tbody>
		
								
<%-- 			
								<tbody>			
									<c:forEach items="${parameterizable.parameters}" var="p" varStatus="varStatus">
										<tr>
											<td>${p.name}</td>
											<td>${p.defaultValue}</td>
											<td><openmrs:fieldGen type="${p.type}" formFieldName="parameters[${varStatus.index}].defaultValue" val="${p.defaultValue}" parameters="" /></td>
											<td><input type="checkbox" name="parameters[${varStatus.index}].allowAtEvaluation" /></td>
											<td>
												<c:choose>
													<c:when test="${p.required}">
														<input type="hidden" name="parameters[${varStatus.index}].required" value="true" tabindex="${varStatus.index+3}"/>
														<input type="checkbox" name="parameters[${varStatus.index}].required" value="true" disabled/>
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="parameters[${varStatus.index}].required" tabindex="${varStatus.index+3}"/></td>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
--%>								
								<tfoot>
								</tfoot>								
							</table>
						</div>
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
		<div id="cohort-definition-preview-tab">
			<springform:form id="saveForm" commandName="parameterizable" method="POST">
			<!-- <form method="post" action="saveCohortDefinition.form"> -->			
				<ul>				
					<li>
						<p>
							<i>Cohort definition evaluation has not been implemented yet</i>
						</p>
					</li>
					<li>					
						<div align="center">				
							<input id="evaluate-button" disabled name="evaluate" type="submit" value="Evaluate" tabindex="101" />
							<button id="cancel-button" name="cancel">Cancel</button>
						</div>					
					</li>
				</ul>
			</springform:form>
		</div>
		
	</div>	
</div>
	
