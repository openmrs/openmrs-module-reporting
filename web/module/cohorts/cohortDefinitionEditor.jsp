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


	// ======  Tabs: Cohort Definition Tabs  ================================================

	$('#cohort-definition-tabs').tabs();
	$('#cohort-definition-tabs').show();


	// ======  DataTable: Cohort Definition Parameter  ======================================
	
	$('#cohort-definition-parameter-table').dataTable( {
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
		window.location.href='<c:url value="/module/reporting/manageReportSchemas.list"/>';
	});
	
} );

</script>

<div id="page">

<div id="container">

	<h1>Cohort Definition Editor</h1>

		<div id="cohort-definition-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#cohort-definition-basic-tab"><span>Basic</span></a></li>
            </ul>
		</div>		
	
		<div id="cohort-definition-basic-tab">
			<form method="post" action="saveCohortDefinition.form">
			
				<ul>				
					<li>
						<label class="desc" for="id">ID</label>				
						<div>
							<input type="hidden" name="uuid" value="${cohortDefinition.uuid}" />
							<c:choose>
								<c:when test="${!empty cohortDefinition.uuid}">${cohortDefinition.uuid}</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${!empty cohortDefinition.id}">${cohortDefinition.id}</c:when>
										<c:otherwise>(new)</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>	
						</div>
					</li>				
					<li>
						<label class="desc" for="type">Type</label>			
						<div>
							${cohortDefinition.class.name}
							<input type="hidden" name="type" value="${cohortDefinition.class.name}" tabindex="1" />
						</div>
					</li>
					
					<li>
						<label class="desc" for="name">Name</label>			
						<div>
							<input type="text" 
									class="field text medium" 
									id="name" 
									name="name"
									value="${cohortDefinition.name}"
									size="50" 
									tabindex="2"/>
						</div>
					</li>
					<li>
						<label class="desc" for="description">Description</label>			
						<div>
							<textarea id="description" 
									class="field text short" cols="80" tabindex="3"
									name="description">${cohortDefinition.description}</textarea>			
						</div>
					</li>


<%--
					<li>
						<label class="desc" for="parameter">Parameters</label>			
						
						<div>
							<table id="cohort-definition-parameter-table" class="display">
								<thead>
									<tr>
										<th align="left">Name</th>
										<th align="left">Old Default Value</th>
										<th align="left">New Default Value</th>
										<th align="left">Param?</th>
										<th align="left">Required?</th>
									</tr>	
								</thead>
								<tbody>			
									<c:forEach items="${cohortDefinition.parameters}" var="p" varStatus="varStatus">
										<tr>
											<td>${p.name}</td>
											<td>${p.defaultValue}</td>
											<td><openmrs:fieldGen type="${p.clazz}" formFieldName="parameter.${p.name}.defaultValue" val="${p.defaultValue}" parameters="" /></td>
											<td><input type="checkbox" name="parameter.${p.name}.allowAtEvaluation" /></td>
											<td>
												<c:choose>
													<c:when test="${p.required}">
														<input type="hidden" name="parameter.${p.name}.required" value="true" tabindex="${varStatus.index+3}"/>[x]
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="parameter.${p.name}.required" tabindex="${varStatus.index+3}"/></td>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
								<tfoot>
								</tfoot>								
							</table>
						</div>
					</li>
					
--%>					
					<li>					
						<div align="center">				
							<input id="saveForm" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<input id="saveForm" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>				
						</div>					
					</li>
				</ul>				
			</form>
		</div>
	</div>	
</div>
	
