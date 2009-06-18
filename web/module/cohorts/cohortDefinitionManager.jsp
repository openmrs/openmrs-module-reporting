<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet" />

<!--  Javascript -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery-1.3.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js"></script>


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#cohort-definition-table').dataTable( {
		"bPaginate": true,
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );			
} );

</script>


<div id="page">
<div id="container">

<h1>Cohort Manager</h1>

<table id="cohort-definition-table" class="display" >
	<thead>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Preview</th>
			<th>Delete</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${cohortDefinitions}" var="cohortDefinition" varStatus="status">
			<tr>
				<td><a href="${pageContext.request.contextPath}/module/reporting/editCohortDefinition.form?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}">${cohortDefinition.name}</a></td>
				<td>${cohortDefinition.description}</td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/editCohortDefinition.form?uuid=${cohortDefinition.uuid}&action=preview">preview</a></td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/editCohortDefinition.form?uuid=${cohortDefinition.uuid}&action=delete">delete</a></td>
			</tr>
		</c:forEach>	
	</tbody>
	<tfoot>
		<tr>
			<th colspan="4" align="center">
				<button onclick="location.href='${pageContext.request.contextPath}/module/reporting/editCohortDefinition.form'">Add Indicator</button>
			</th>			
		</tr>	
	</tfoot>
</table>

</div>

</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>