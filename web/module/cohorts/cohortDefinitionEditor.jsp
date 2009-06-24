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
		"bAutoWidth": true
	} );			
} );

</script>

<div id="page">

<div id="container">


		<div id="cohort-definition-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#cohort-definition-tabs-details"><span>Details</span></a></li>
            </ul>
		</div>
		
	
		<div id="cohort-definition-tabs-details">

			<div class="info">
				<h2>Cohort Definition Editor</h2>
			</div>

			<form method="post" action="saveCohortDefinition.form">
			
				<ul>
					<li>
						<label class="desc" for="type">Type</label>			
						<div>
							${cohortDefinition.class.name} (${cohortDefinition.id})
							<input type="hidden" name="uuid" value="${cohortDefinition.uuid}" />
							<input type="hidden" name="type" value="${cohortDefinition.class.name}" />
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
									tabindex="3"/>
						</div>
					</li>
					
					<li>
						<label class="desc" for="description">Description</label>			
						<div>
							<textarea id="description" 
									class="field text medium" 
									name="description" 
									rows="3" 
									cols="40">${cohortDefinition.description}</textarea>			
						</div>
					</li>
					<li>
						<div>
							<hr/>
						</div>
					</li>
					<li>
						<label class="desc">Parameters</label>			
						<div>
							<table id="cohort-definition-parameter-table" class="display">
								<thead>
									<tr>
										<th align="left">Name</th>
										<th align="left">Default Value</th>
										<th align="left">Param?</th>
										<th align="left">Required?</th>
									</tr>	
								</thead>
								<tbody>			
									<c:forEach items="${cohortDefinition.availableParameters}" var="p">
										<tr>
											<td>${p.name}</td>
											<td><openmrs:fieldGen type="${p.clazz}" formFieldName="parameter.${p.name}.defaultValue" val="${p.defaultValue}" parameters="" /></td>
											<td><input type="checkbox" name="parameter.${p.name}.allowAtEvaluation" /></td>
											<td>
												<c:choose>
													<c:when test="${p.required}">
														<input type="hidden" name="parameter.${p.name}.required" value="true" />[x]
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="parameter.${p.name}.required" /></td>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
						</div>
					</li>
					<li>
						<div>
							<input type="submit" value="Save" />
						</div>
					</li>
				</ul>
			</form>
		</div>
	</div>
	
</div>
	
