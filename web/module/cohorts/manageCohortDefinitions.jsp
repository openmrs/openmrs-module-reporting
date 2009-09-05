<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#cohort-definition-table').dataTable( {
		"bPaginate": true,
		"iDisplayLength": 25,
		"bLengthChange": false,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	});
});

function confirmDelete(name, uuid) {
	if (confirm("Are you sure you want to delete " + name + "?")) {
		document.location.href = 'purgeCohortDefinition.form?uuid='+uuid;
	}
}

</script>

<div id="page">

	<div id="container">
	
		<h1>Cohort Definitions</h1>
	
		<spring:message code="reporting.manage.createNew"/>:
		<form method="get" action="editCohortDefinition.form" style="display:inline">
			<select name="type" style="font-size: 1.5em;">
				<option value="">&nbsp;</option>
				<c:forEach items="${types}" var="type">
					<option value="${type.name}">${type.simpleName}</option>
				</c:forEach>
			</select>
			<input type="submit" value="Create"/>
		</form>
		
		<br/>
		<table id="cohort-definition-table" class="display" >
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th align="center" width="1%">Run</th>
					<th align="center" width="1%">Remove</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${cohortDefinitions}" var="cohortDefinition" varStatus="status">
				
					<script>					
					$(document).ready(function() {
						$("#cohort-${cohortDefinition.uuid}").click(function(event){ 
							showReportingDialog({ 
								title: 'Preview Cohort Definition', 
								url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}',
								successCallback: function() { 
									window.location = window.location; //.reload(true);
								} 
							});
						});
					} );
					</script>					
				
					<tr>
						<td>
							<a href="editCohortDefinition.form?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}">
								${cohortDefinition.name}
							</a>
						</td>
						<td>
							${cohortDefinition.description}
						</td>
<!--  						
						<td align="center">
							<a href="evaluateCohortDefinition.form?uuid=${cohortDefinition.uuid}">
								<img src="<c:url value='/images/play.gif'/>" border="0"/>
							</a>
						</td>
-->						
						<td align="center">
							<a href="#" id="cohort-${cohortDefinition.uuid}">
								<img src="<c:url value='/images/play.gif'/>" border="0"/>
							</a>
						</td>
						<td align="center">
							<a href="javascript:confirmDelete('${cohortDefinition.name}','${cohortDefinition.uuid}');">
								<img src="<c:url value='/images/trash.gif'/>" border="0"/>
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>

	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>