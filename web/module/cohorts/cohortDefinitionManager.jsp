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
	
	function deleteCohortDefinition(name, uuid) {
		if (confirm("Are you sure you want to delete " + name + "?")) {
			document.location.href = '${pageContext.request.contextPath}/module/reporting/purgeCohortDefinition.form?uuid='+uuid;
		}
	}

</script>

<div id="page">

	<div id="container">
	
		<h1>Cohort Manager</h1>

		<form method="get" action="editCohortDefinition.form">
			<strong>Create a new cohort definition</strong>
			<select name="type">
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
					<th>Type</th>
					<th>Description</th>
					<th align="center" width="1%"></th>
					<th align="center" width="1%"></th>
					<th align="center" width="1%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${cohortDefinitions}" var="cohortDefinition" varStatus="status">
					<tr>
						<td>${cohortDefinition.name}</td>
						<td>${cohortDefinition.class.simpleName}</td>
						<td>${cohortDefinition.description}</td>
						<td align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/editCohortDefinition.form?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}">
								<img src="<c:url value='/images/edit.gif'/>" border="0"/>
							</a>
						</td>
						<td align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/evaluateCohortDefinition.form?uuid=${cohortDefinition.uuid}">
								<img src="<c:url value='/images/play.gif'/>" border="0"/>
							</a>
						</td>
						<td align="center">
							<a href="javascript:deleteCohortDefinition('${cohortDefinition.name}','${cohortDefinition.uuid}');">
								<img src="<c:url value='/images/trash.gif'/>" border="0"/>
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot></tfoot>
		</table>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>