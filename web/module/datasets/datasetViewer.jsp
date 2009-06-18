<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>

<!-- Autocomplete -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/localdata.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>

<!-- Other -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#dataset-preview-table').dataTable( {
		"bPaginate": true,
		//"sPaginationType": "full_numbers",
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );
} );
</script>


<div id="page">
	<div id="container">
	
		<div id="datasetPreview" >
			<h1>Dataset Viewer</h1>

			
			<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
				method="post" action="${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form">

				<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
				<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
				<input type="hidden" id="className" name="className" value="${dataSetDefinition.class.name}"/>

				<ul>
					<li>
						<label class="desc">Dataset Definition</label>
						<span>				
							${dataSetDefinition.name} (<i>${dataSetDefinition.class.simpleName}</i>)		
						</span>
					</li>
					<li>
						<label class="desc">Cohort Definition</label>
						<span>			
							<c:choose>
								<c:when test="${cohortDefinition != null}">	
									${cohortDefinition.name} (<i>${cohortDefinition.class.simpleName}</i>)
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${cohort != null}">	
											${cohort.name} (<i>StaticCohortDefinition</i>)
										</c:when>
										<c:otherwise>
											&lt; choose one below &gt;								
										</c:otherwise>
									</c:choose>									
								</c:otherwise>
							</c:choose>
						</span>
					</li>
					<li>
						<hr/>
					</li>										
					<li>
						<label class="desc">Choose a Cohort Definition</label>
						<div>
							<span><i>please use 'Random Cohort' for now -- other cohort definitions will be supported soon</i></span>
							<select name="cohortName">
								<option value="">Random Cohort</option>						
								<c:forEach items="${cohortDefinitions}" var="cohortDefinition">
									<option value="${cohortDefinition.uuid}">${cohortDefinition.name} <i>(${cohortDefinition.class.simpleName})</i></option>
								</c:forEach>								
							</select>
							<input type="submit" name="action" value="Preview"/>
						</div>
					</li>
				</ul>				
			</form>	
			
			<div id="datasetPreviewTable" style="overflow:auto;">
				<table id="dataset-preview-table" class="display">
					<thead>
						<tr>
							<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="varStatus">				
								<c:if test="${varStatus.index < 7}">
									<th>
										${column.columnName}
									</th>
								</c:if>
							</c:forEach>
							<c:if test="${dataSetDefinition.columnCount > 7}">
								<th>
									Showing 7 out of ${dataSetDefinition.columnCount} columns
									<a href="">show more...</a>
								</th>
							</c:if>						
						</tr>
					</thead>
					<tbody>						
						<c:if test="${!empty dataSet}">

							<%-- 
							
							
								TODO Integrate the simple html renderer so we can get rid of these scriptlets 
								
								
							--%>

							<% 
		
								org.openmrs.module.dataset.DataSet dataSetTemp = 
									(org.openmrs.module.dataset.DataSet) request.getAttribute("dataSet");
		
								java.util.List<org.openmrs.module.dataset.column.DataSetColumn> columns = 
									dataSetTemp.getDataSetDefinition().getColumns();
		
							
								for(Object object : dataSetTemp) { 							
									java.util.Map dataSetRow = (java.util.Map) object;
							%>
							<tr>
							<% 
									int columnCount = 0;
									for(org.openmrs.module.dataset.column.DataSetColumn column : columns) { 
										if(columnCount++<7) { 
							%>					
								<td>
									<%= dataSetRow.get(column) %>
								</td>
							<% 			
										}
									}
							%>
							
								<td>...</td>
							</tr>
							<%									
								}
							%>		
						</c:if>	
											
					</tbody>
					<tfoot>
						<tr>			
							<td colspan="${dataSetDefinition.columnCount}" align="center">
							</td>
						</tr>
					</tfoot>
				</table>
			</div>
			<div>			
				<button name="button1" disabled>Export .csv</button>
				<button name="button1" disabled>Export .tsv</button>
				<button name="button1" disabled>Export .xsl</button>
			</div>
			
		</div>			
	</div>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>