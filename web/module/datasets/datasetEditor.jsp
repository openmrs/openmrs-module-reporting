<base target="_self">
<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Sets" otherwise="/login.htm" redirect="/module/reporting/datasets/manageDataSets.form" />

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

	function noop() { 
		alert("this is a no op");
	}

	$('#save-sql-column-button').click(function(event){
		$("#add-sql-column-form").submit();
		$("#dataset-column-dialog").dialog('close');		
	});
	$('#cancel-sql-column-button').click(function(event){
		window.location=window.location;
	});

	
	$('#save-logic-column-button').click(function(event){
		$("#add-logic-column-form").submit();
		$("#dataset-column-dialog").dialog('close');		
	});
	$('#cancel-logic-column-button').click(function(event){
		window.location=window.location;
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
												<th>Remove</th>
												<th>Column Key</th>
												<th>Display Name</th>
												<th>Data Type</th>
											</tr>
										</thead>
										<tbody>					
											<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="status">				
												<tr>
													<c:url var="removeColumnUrl" value="/module/reporting/datasets/removeColumn.form?uuid=${dataSetDefinition.uuid}&columnKey=${column.columnKey}"/>												
													<td width="1%" align="center">
														<a href="${removeColumnUrl}"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
													</td>
													<td>${column.columnKey}</td>
													<td>${column.displayName}</td>
													<td>${column.dataType}</td>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>
												<td colspan="4"><a href="#" id="add-column-button">New Column</a></td>											
											</tr>
										</tfoot>
									</table>
								</div>
								<div align="left">
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
			<li><a href="#dataset-column-logic-tab"><span>Logic</span></a></li>
			<li><a href="#dataset-column-sql-tab"><span>SQL</span></a></li>			
			<li><a href="#dataset-column-concept-tab"><span>Concept</span></a></li>			
		</ul>
		<div id="dataset-column-logic-tab">	
			<div id="logicColumn">
				<c:url var="actionUrl" value="/module/reporting/datasets/addLogicColumn.form"/>
				<form id="add-logic-column-form" name="add-logic-column-form" action="${actionUrl}" class="wufoo topLabel" method="post">
					
					<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
				
					<ul>		
						<li>
							<label class="desc" for="columnName"><spring:message code="reporting.columnName"/></label>								
							<span>
								<input type="text" name="columnName" size="30" />
							</span> 							
						</li>
						<li>
							<label class="desc" for="logicQuery"><spring:message code="reporting.columnValue"/></label>
							<span>		
								<input type="text" name="logicQuery" size="30" />		
							</span> 
						</li>				
						<li>
							<div align="center">
								<input id="save-logic-column-button" type="button" value="Add"/>
								<input id="cancel-logic-column-button" type="button" value="Cancel"/>
							</div>
						</li>
					</ul>
				</form>			
			</div>
		</div>	
		<div id="dataset-column-sql-tab">	
			<div id="sqlColumn">
				<c:url var="actionUrl" value="/module/reporting/datasets/addSqlColumn.form"/>
				<form id="add-sql-column-form" name="add-sql-column-form" action="${actionUrl}" class="wufoo topLabel" method="post">
					
					<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
				
					<ul>		
						<li>
							<label class="desc" for="sqlQuery"><spring:message code="reporting.columnValue"/></label>
							<span>		
								<textarea type="text" name="sqlQuery" cols="60" rows="6"></textarea>	
							</span> 
						</li>				
						<li>
							<div align="center">
								<input id="save-sql-column-button" type="button" value="Add"/>
								<input id="cancel-sql-column-button" type="button" value="Cancel"/>
							</div>
						</li>
					</ul>
				</form>			
			</div>
		</div>	
		<div id="dataset-column-concept-tab">				
			<div id="conceptColumn">
				<form id="datasetColumnForm" name="datasetColumnForm" class="wufoo topLabel" method="post" 
					action="${pageContext.request.contextPath}/module/reporting/datasets/addConceptColumn.form">
					<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
					<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
					<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>
				
					<ul>		
						<li>
							<label class="desc" for="columnName"><spring:message code="reporting.columnName"/></label>								
							<span>
								<input type="text" name="columnName" size="30" />
							</span>
						</li>
						<li>
							<label class="desc" for="conceptId"><spring:message code="reporting.columnValue"/></label>
							<span>				

							
							</span>
							
							
						</li>				
						
						<!--  
						
							Need to add jquery binding for radio button label
							
								onclick="this.previousSibling.click()"
						
						 -->
			
						<li>
							<label class="desc" for="type"><spring:message code="reporting.columnModifier"/></label>								
							<div>
								<input class="field radio" type="radio" name="modifier" checked="checked" value="mostRecent"/>
								<label class="choice" for="modifier">
									<spring:message code="reporting.columnModifier.mostRecent"/>
								</label>						
							</div>
							<div>
								<input class="field radio" type="radio" name="modifier" value="first"/>
								<label class="choice" for="modifier">
									<spring:message code="reporting.columnModifier.first"/>
								</label>					
							</div>
							<div>
								<input class="field radio" type="radio" name="modifier" value="firstNum"/>
								<label class="choice" for="modifier">
									<spring:message code="reporting.columnModifier.firstNum"/>						
								</label>
							</div>
							<div>
								<input class="field radio" type="radio" name="modifier" value="mostRecentNum" />
								<label class="choice" for="modifier">
									<spring:message code="reporting.columnModifier.mostRecentNum"/>
									<input class="choice" type="text" name="modifierNum" size="3" />
								</label>					
									
							</div>
						</li>
						<li>
							<label class="desc" for="type"><spring:message code="reporting.conceptExtras"/></label>								
							<div>
								<input type="checkbox" name="extras" value="obsDatetime" class="field radio"/>
								<label class="choice" for="extras">
									<spring:message code="reporting.conceptExtra.obsDatetime"/>
								</label>
							</div>	
							<div>				
								<input type="checkbox" name="extras" value="location"  class="field radio"/>
								<label class="choice" for="extras">
									<spring:message code="reporting.conceptExtra.location"/>
								</label>
							</div>
							<div>
								<input type="checkbox" name="extras" value="comment"  class="field radio"/>
								<label class="choice" for="extras">
									<spring:message code="reporting.conceptExtra.comment"/>
								</label>					
							</div>
							<div>
								<input type="checkbox" name="extras" value="encounterType" class="field radio" />
								<label class="choice" for="extras">
									<spring:message code="reporting.conceptExtra.encounterType"/>
								</label>						
							</div>
							<div>
								<input type="checkbox" name="extras" value="provider" class="field radio" />
								<label class="choice" for="extras">
									<spring:message code="reporting.conceptExtra.provider"/>
								</label>						
							</div>			
						</li>
						<li>
							<div align="center">
								<input id="save-button" type="button" value="Add"/>
								<input id="cancel-button" type="button" value="Close"/>
							</div>
						</li>
					</ul>
				</form>
			</div>		
		</div>		
		
	</div>
</div>

		



<%@ include file="/WEB-INF/template/footer.jsp"%>