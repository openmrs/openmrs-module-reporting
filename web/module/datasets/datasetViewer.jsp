<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>

<!-- Autocomplete -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>

<!-- Other -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>

<!-- JQuery UI -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui-1.6/ui.all.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui-1.6/jquery-ui-1.6.custom.min.js"></script>

<!-- 
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>
 -->
 
 
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


	$('#dataset-tabs').tabs();
	$('#dataset-tabs').show();
	
} );
</script>


<div id="page">
	<div id="container">
	
		<div id="datasetPreview" >

			<h1>Dataset Viewer</h1>
			
			
			<div id="dataset-tabs" class="ui-tabs-hide">			
				<ul>
	                <li><a href="#dataset-viewer-tab"><span>Viewer</span></a></li>
	            </ul>
			</div>
		
		
			<div id="dataset-viewer-tab">
	
				<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
					method="post" action="${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form">
	
					<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="className" name="className" value="${dataSetDefinition.class.name}"/>
	
	
					<ul>
						<li>
						
							<label class="desc" for="uuid">Data Set:</label>
							<h2>${dataSetDefinition.name} (<i>${dataSetDefinition.class.simpleName}</i>)</h2>
	
							<label class="desc" for="uuid">Cohort:</label>
							<div>						
								<select id="cohort-select" name="cohortName">
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


				<div style="overflow:auto">							
					<table id="dataset-preview-table" class="display" width="50%">
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
									<c:if test="${dataSetDefinition.columnCount > 7}">
										<td>...</td>
									</c:if>
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
				<div align="center" style="padding:10px;">

					<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
						method="post" action="${pageContext.request.contextPath}/module/reporting/downloadDataset.form">
	
						<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
						<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
						<input type="hidden" id="className" name="className" value="${dataSetDefinition.class.name}"/>
		
		
						<div align="center"">						
							<input class="button" type="submit" name="action" value="Download"/>							
						</div>
					</form>							

				</div>
			</div>
		</div>			
	</div>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>