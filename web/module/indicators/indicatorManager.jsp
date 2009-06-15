plete
<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="localHeader.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Indicator Manager</title>

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
		$('#indicator-table').dataTable( {
			"bPaginate": true,
			"bLengthChange": true,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		} );			
	} );

</script>
</head>


<body id="page">

<h1>Indicator Manager</h1>

<strong>Show:</strong>
<!--  TODO This should be an unordered list of links -->
<a href="#">my indicators</a> |
<a href="#">all indicators</a>

<h2>All Indicators</h2>
<table id="indicator-table" class="display" >
	<thead>
		<tr>
			<th>Indicator</th>
			<th>Description</th>
			<th>Author</th>
			<th>Last Modified</th>
			<th>Status</th>
			<th>Preview</th>
			<th>Delete</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${indicators}" var="indicator" varStatus="status">
			<tr>
				<td><a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form?uuid=${indicator.uuid}">${indicator.name}</a></td>
				<td>${indicator.description}</td>
				<td>(unknown)</td>
				<td>(unknown)</td>
				<td>(unknown)</td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form?uuid=${indicator.uuid}&action=preview">preview</a></td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/editIindicator.form?uuid=${indicator.uuid}&action=delete">delete</a></td>
			</tr>
		</c:forEach>	
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" align="left">
				<a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form">Add Indicator</button>
			</td>			
		</tr>	
	</tfoot>
</table>


</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>