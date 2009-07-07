<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Report Manager</title>

<!--  CSS  -->
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
	$('#report-schema-table').dataTable( {
		"bPaginate": true,
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );			

	$("#report-schema-table").css("width","100%");
	
} );
</script>
</head>

<body>
<div id="page">
	<div id="container">
		<h1>Manage Reports</h1>
		<div>		
			<strong>Render:</strong>
			<a href="renderLabReport.form">Simple Laboratory Report</a> |
			<a href="renderCohortReport.form">Sample Cohort Report</a> |
			<a href="renderIndicatorReport.form">Sample Indicator Report</a> 
		</div>
		<br/> <br/>				
		<table id="report-schema-table" class="display" >
			<thead>
				<tr>
					<th width="10px">Edit</th>
					<th>Name</th>
					<th>Description</th>
					<th width="10px">Preview</th>
					<th width="10px">Delete</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportSchemas}" var="reportSchema" varStatus="status">
					<tr>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form?uuid=${reportSchema.uuid}">edit</a>
						</td>
						<td>
							${reportSchema.name}
							<!-- Disabling link until the generic report schema form is complete -->
							<!-- 
							<a href="${pageContext.request.contextPath}/module/reporting/editReportSchema.form?uuid=${reportSchema.uuid}">${reportSchema.name}</a>
							-->
						</td>
						<td>
							${reportSchema.description}
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/editReportSchema.form?uuid=${reportSchema.uuid}&action=preview">preview</a>
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/editReportSchema.form?uuid=${reportSchema.uuid}&action=delete">delete</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
				<tr>
					<th colspan="5" align="center" height="50">
						<a class="button" href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form">Add Indicator Report</a>
					</th>			
				</tr>	
			</tfoot>
		</table>
	
	</div>
</div>


</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>