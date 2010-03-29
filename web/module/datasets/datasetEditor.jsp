<base target="_self">
<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Sets" otherwise="/login.htm" redirect="/module/reporting/datasets/manageDataSets.form" />

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
	
	$("#preview-dataset-button").click(function(event){ 
		event.preventDefault();
		showReportingDialog({ 
			title: 'Preview Data Set', 
			url: '<c:url value="/module/reporting/datasets/viewDataSet.form?dataSetId=${dataSetDefinition.uuid}&type=${dataSetDefinition.class.name}&mode=dialog&cohortId=all&limit=10"/>',
			successCallback: function() { window.location = window.location; } 
		});
	});
	
} );
</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>

<div id="page">

	<form id="datasetForm" name="datasetForm" class="wufoo topLabel" autocomplete="off"
		method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/saveDataSet.form">

		<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
		<input type="hidden" id="type" name="type" value="${dataSetDefinition.class.name}"/>

		<table style="font-size:small;" width="95%">
			<tr>
				<td valign="top">
					<ul>				
						<li>
							<label class="desc" for="name">Name</label>
							<input type="text" id="name"  tabindex="2" name="name" value="${dataSetDefinition.name}" size="30"/>
						</li>
						<li>
							<label class="desc" for="description">Description</label>
							<textarea id="description" class="field text short" cols="30" rows="10" tabindex="3" name="description">${dataSetDefinition.description}</textarea>
						</li>
						<li>
							<label class="desc" for="type">Type</label>
							${dataSetDefinition.class.simpleName}
						</li>
					</ul>
				</td>
				<td>
					<li>
						<label class="desc">Properties</label>
						<table id="dataset-definition-property-table" class="display">
							<thead>
								<tr>
									<th align="left">Name</th>
									<th align="left" width="100%">Type</th>
								</tr>	
							</thead>
							<tbody>
								<c:forEach items="${configurationProperties}" var="p" varStatus="varStatus">
									<tr <c:if test="${varStatus.index % 2 == 0}">class="odd"</c:if>>
										<td valign="top" nowrap="true">
											${p.field.name}
											<c:if test="${p.required}"><span style="color:red;">*</span></c:if>
										</td>
										<td style="vertical-align:top;">
											<c:choose>
												<c:when test="${p.field.name == 'serializedData'}">
													<wgt:widget id="${p.field.name}" name="parameter.${p.field.name}.value" object="${dataSetDefinition}" property="${p.field.name}" attributes="rows=20|cols=80"/>
												</c:when>
												<c:otherwise>
													<wgt:widget id="${p.field.name}" name="parameter.${p.field.name}.value" object="${dataSetDefinition}" property="${p.field.name}" attributes="size=80"/>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</li>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="3" />
					<button id="preview-dataset-button">Preview</button>
					<button id="cancel-button" name="cancel" class="btTxt submit">Cancel</button>
				</td>
			</tr>
		</table>			
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>