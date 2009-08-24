<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!-- Wufoo Forms -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"/>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {

	$('#dataset-tabs').tabs();
	$('#dataset-tabs').show();


	$('#dataset-column-tabs').tabs();
	$('#dataset-column-tabs').show();
	
	$('#dataset-column-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

	$('#dataset-preview-table').dataTable( {
		"bPaginate": true,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );
	
	// Redirect to the listing page
	$('#cancel-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/manageDatasets.list"/>';
	});

	
	$("#dataset-column-dialog").dialog({
		bgiframe: true,			
		autoOpen: false,
		title:"Add Column",
		modal:true,
		draggable:false,
		width:"50%"
	});

	$("#close-button").click(function(){
		$("#dataset-column-dialog").dialog('close');
	});
	
	$("#add-column-button").click(function(event){ 
		$("#dataset-column-dialog").dialog('open')	  
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
                <li><a href="#dataset-preview-tab"><span>Preview</span></a></li>
            </ul>
		
			<div id="dataset-basic-tab">
		
				<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
					method="post" action="${pageContext.request.contextPath}/module/reporting/saveDataset.form">
			
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
		
					<div id="datasetDetails">
						<ul>		
							<li>
								<label class="desc" for="uuid">ID</label>
								<span>
									<c:choose>
										<c:when test="${!empty dataSetDefinition.uuid}">${dataSetDefinition.uuid}</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${!empty dataSetDefinition.id}">${dataSetDefinition.id}</c:when>
												<c:otherwise>(new)</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>	
								</span>
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
								<button id="cancel-button" name="cancel" class="btTxt submit">Cancel</button>
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
									<td><input type="checkbox" name="deleteColumn" value="${column.columnKey}"></td>
									<td>${column.displayName}</td>
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
					<button name="button1" id="add-column-button">Add Column</button>
					<button name="button1" disabled>Remove Selected</button>
				</div>
			</div>
	
			<div id="dataset-preview-tab">	

				<div id=datasetPreview style="overflow: auto;">
				
					<h1>Dataset Download</h1>

					<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
							method="post" action="${pageContext.request.contextPath}/module/reporting/showDataset.form#dataset-preview-tab">
						<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
						<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
						<input type="hidden" id="className" name="className" value="${dataSetDefinition.class.name}"/>

						
						Show a random cohort of <select name="cohortSize">
							<option value="5">5</option>							
							<option value="10">10</option>							
							<option value="25">25</option>							
							<option value="50">50</option>							
							<option value="100">100</option>							
						</select> patients 
						<input type="submit" name="action" value="Preview"/>	<br/>						
					</form>
	
					<c:if test="${!empty dataSet}">
						<div id="datasetPreviewTable" style="overflow:auto;">
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
		
									<%-- 
									
									
										TODO Integrate the simple html renderer so we can get rid of these scriptlets 
										
										
									--%>
		
									<% 
				
										org.openmrs.module.dataset.DataSet<Object> dataSetTemp = 
											(org.openmrs.module.dataset.DataSet) request.getAttribute("dataSet");
				
										java.util.List<org.openmrs.module.dataset.column.DataSetColumn> columns = 
											dataSetTemp.getDefinition().getColumns();
				
									
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
													
								</tbody>
							</table>
						</div>
						<div>					
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
					</c:if>					
				</div>
				<span width="100%" align="right">
					<a href="${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form?uuid=${dataSetDefinition.uuid}&id=${dataSetDefinition.id}&className=${dataSetDefinition.class.name}">Data Set Viewer</a>
				</span>					
			</div>
		</div>
	</div>
</div>
		
		
<div id="dataset-column-dialog" title="Add Column">
	<div id="dataset-column-tabs" class="ui-tabs-hide">			
		<ul>
			<li><a href="#dataset-column-concept-tab"><span>Concept</span></a></li>
			
<%-- 		
			
			<li><a href="#dataset-column-simple-tab"><span>Simple</span></a></li>
			<li><a href="#dataset-column-calculated-tab"><span>Calculated</span></a></li>
			<li><a href="#dataset-column-cohort-tab"><span>Cohort</span></a></li>
--%>

		</ul>           
		<div id="dataset-column-concept-tab">	
			<%@ include file="columns/conceptColumn.jsp" %>
		</div>
		
<%-- 		
		<div id="dataset-column-simple-tab">
			<%@ include file="columns/simpleColumn.jsp" %>
		</div>
		<div id="dataset-column-calculated-tab">	
			<%@ include file="columns/calculatedColumn.jsp" %>
		</div>
		<div id="dataset-column-cohort-tab">	
			<%@ include file="columns/cohortColumn.jsp" %>		
		</div>
--%>		
		
	</div>
</div>




		



<%@ include file="/WEB-INF/template/footer.jsp"%>