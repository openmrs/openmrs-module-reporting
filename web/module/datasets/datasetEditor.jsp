<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Data Sets" otherwise="/login.htm" redirect="/module/reporting/datasets/manageDataSets.form" />
<%@ include file="../manage/localHeader.jsp"%>

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
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );

	// Redirect to the listing page
	$('#cancel-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/datasets/manageDataSets.list"/>';
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
		event.preventDefault();
		$("#dataset-column-dialog").dialog('open');  
	});

	$("#dataset-preview-dialog").dialog({
		bgiframe: true,			
		autoOpen: false,
		title:"Preview Data Set",
		modal:true,
		draggable:false,
		width:"90%",
	});
	$("#preview-dataset-button").click(function(event){ 
		event.preventDefault();
		$("#dataset-preview-dialog").dialog('open');
		 
	});


	
	
} );
</script>


<style>

#dataset-preview-table { width: 85%; }


</style>

<div id="page">
	<div id="container">

		<div>		
				<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
					method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/saveDataSet.form">
			
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
		
					<div id="datasetDetails">
						<ul>		
							<li>
								<h1>Dataset Details</h1>
								<label class="desc" for="name">Name</label>
								<input id="name" name="name" type="text" class="field text large" 
									value="${dataSetDefinition.name}" size="20" tabindex="1" />
							</li>				
							<li>		
								<label class="desc" for="description">Description</label>
								<textarea id="description" name="description" class="field textarea small" 
									rows="10" cols="20" tabindex="2">${dataSetDefinition.description}</textarea>				
							</li>														
							<li>
								<div id="datasetColumns">
									<h1>Dataset Columns</h1>
									<table id="dataset-column-table" class="display">
										<thead>
											<tr>
												<th>Column Key</th>
												<th>Display Name</th>
												<th>Data Type</th>
											</tr>
										</thead>
										<tbody>					
											<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="status">				
												<tr>
													<td>${column.columnKey}</td>
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
								<div align="left">
									<a href="#" id="add-column-button">Add Column</a>
								</div>
							
							</li>
							
							<li class="buttons" align="center">
								<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="3" />
								<button id="preview-dataset-button">Preview</button>
								<button id="cancel-button" name="cancel" class="btTxt submit">Cancel</button>
							</li>
						</ul>
					</div>
				</form>
			</div>

			
		</div>
	</div>


<div id="dataset-preview-dialog" title="Preview Data Set">

	<div>
		<div id="datasetPreview" style="height: 500px;">
<%-- 	
			<div>
				<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
						method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/editDataSet.form">
					<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
		
					<span>Show </span>
					<select name="cohortSize">
						<option value="5">5</option>							
						<option value="10">10</option>							
						<option value="25">25</option>							
						<option value="50">50</option>							
						<option value="100">100</option>							
					</select> patients 
					<input type="submit" name="action" value="Preview"/>	<br/>	
				</form>
			</div>
--%>		
			<div id="datasetPreviewTable" style="overflow:auto;">									
				<c:if test="${!empty dataSet}">
					<rpt:dataSet id="dataset-preview-table" dataSet="${dataSet}" cssClass="display"/> 				
				</c:if>	
			</div>
			
<%-- 
			<div>					
				<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
					method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/downloadDataSet.form">
	
					<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
	
					<div align="center"">						
						<input type="submit" name="action" value="Download"/>						
					</div>
				</form>							
			</div>
--%>			
		</div>
		
		<span width="100%" align="right">
			<a href="${pageContext.request.contextPath}/module/reporting/datasets/viewDataset.form?uuid=${dataSetDefinition.uuid}&type=${dataSetDefinition.class.name}">View Full Data Set</a>
		</span>					
	
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