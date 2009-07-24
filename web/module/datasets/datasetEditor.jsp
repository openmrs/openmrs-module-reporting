<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!-- Wufoo Forms -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {

	$('#dataset-tabs').tabs();
	$('#dataset-tabs').show();
	
	$('#dataset-column-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );

	$('#cancel-button').click(function(event){
		// To prevent the submit
		event.preventDefault();

		// Redirect to the listing page
		window.location.href='<c:url value="/module/reporting/manageDatasets.list"/>';
	});

	
} );
</script>


<div id="page">
	<div id="container">

		<h1>Dataset Editor</h1>
	
	
		<div id="dataset-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#dataset-basic-tab"><span>Basic</span></a></li>
                <li><a href="#dataset-columns-tab"><span>Columns</span></a></li>
                <li><a href="#dataset-download-tab"><span>Download</span></a></li>
            </ul>
		
			<div id="dataset-basic-tab">
		
				<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
					method="post" action="${pageContext.request.contextPath}/module/reporting/saveDataset.form">
			
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
		
		
					<div id="datasetDetails">
						<ul>		
							<li>
								<label class="desc" for="uuid">ID</label>
								<span>${dataSetDefinition.uuid}</span>
							</li>				
							<li>
								<label class="desc" for="type">Type</label>
								<span>${dataSetDefinition.class.simpleName}</span>
							</li>				
							<li>
								<label class="desc" for="name">Name</label>
								<input id="name" name="name" type="text" class="field text large" 
									value="${dataSetDefinition.name}" size="20" tabindex="1" />
							</li>				
							<li>		
								<label class="desc" for="description">Description</label>
								<textarea id="description" name="description" class="field textarea small" 
									rows="10" cols="20" tabindex="2">${dataSetDefinition.description}</textarea>				
							</li>
							<li class="buttons">
								<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="3" />
								<input id="cancel-button" class="btTxt submit" type="submit" value="Cancel" tabindex="4"/>
							</li>
						</ul>
					</div>
				</form>
			</div>
			
			<div id="dataset-columns-tab">	
				<div id="datasetColumns" style="overflow: auto;">
					<h1>Dataset Columns</h1>
					<table id="dataset-column-table" class="display">
						<thead>
							<tr>
								<th width="5%">Delete</th>
								<th>Column</th>
								<th>Data Type</th>
							</tr>
						</thead>
						<tbody>					
							<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="status">				
								<tr>
									<td><input type="checkbox" name="deleteColumn" value="${column.columnName}"></td>
									<td>${column.columnName}</td>
									<td>${column.dataType}</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot>
							<tr></tr>
						</tfoot>
					</table>
				</div>
				<div align="center">
					<button name="button1" disabled>Add Column</button>
					<button name="button1" disabled>Remove Selected</button>
					<button name="button1" onclick="location.href='${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form?uuid=${dataSetDefinition.uuid}&id=${dataSetDefinition.id}&className=${dataSetDefinition.class.name}'">Preview Data</button>	
				</div>
			</div>
	
			<div id="dataset-download-tab">	
				<div id=datasetDownload style="overflow: auto;">
					<h1>Dataset Download</h1>
	
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
					<div>		
					
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
</div>
		



<%@ include file="/WEB-INF/template/footer.jsp"%>