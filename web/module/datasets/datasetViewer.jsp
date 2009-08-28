<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../localHeader.jsp"%>


 
 
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
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
	
	
					<div>						
						<h2>Choose Columns:</h2>
						${dataSetDefinition.name} (<i>${dataSetDefinition.class.simpleName}</i>)
					</div>	
					<div>				
						<h2>Choose Patients:</h2>													
						<select id="cohort-select" name="cohortUuid">
							<option value="">Random Cohort</option>						
							<c:forEach items="${cohortDefinitions}" var="cohortDefinition">
								<option value="${cohortDefinition.uuid}">${cohortDefinition.name} <i>(${cohortDefinition.class.simpleName})</i></option>
							</c:forEach>								
						</select>						
						<input type="submit" name="action" value="Preview"/>
					</div>
				</form>	


				<div>							
					<table id="dataset-preview-table" class="display">
						<thead>
							<tr>
								<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="varStatus">				
									<c:if test="${varStatus.index < 7}">
										<th>
											${column.displayName}
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
								<c:forEach var="row" items="${dataSet}">${row}</c:forEach>	
								<%-- 
								
								
									TODO Integrate the simple html renderer so we can get rid of these scriptlets 
									
									
								--%>
	
								<% 
			
									org.openmrs.module.dataset.DataSet dataSetTemp = 
										(org.openmrs.module.dataset.DataSet) request.getAttribute("dataSet");
			
									org.openmrs.module.dataset.definition.DataSetDefinition dataSetDefinition = 
										(org.openmrs.module.dataset.definition.DataSetDefinition) dataSetTemp.getDefinition();
									
									java.util.List<org.openmrs.module.dataset.column.DataSetColumn> columns = 
										dataSetDefinition.getColumns();
			
								
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
							<input type="submit" name="action" value="Download"/>							
						</div>
					</form>							

				</div>
			</div>
		</div>			
	</div>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>