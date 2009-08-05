<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Report Manager</title>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#report-schema-table").dataTable( {
		"bPaginate": true,
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );			

	//$("#report-schema-table").css("width","100%");
	
} );
</script>
</head>

<body>
<div id="page">
	<div id="container">
		<h1>Manage Reports</h1>
		<div id="inline-list">	
			<p>	
				<ul>
					<li class="last">Create a new report:</li>
					<li class="first"><a href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form">Indicator Report</a></li>
					<li class="last"><a href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form">Some other report</a></li>
				</ul>
			</p>			
		</div>

		<table id="report-schema-table" class="display" >
			<thead>
				<tr>
					<th width="1%"></th>
					<th width="20%">Name</th>
					<th width="30%">Description</th>
					<th width="10%">Created</th>
					<th width="1%"></th>
					<th width="1%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportSchemas}" var="reportSchema" varStatus="status">
					<tr>
						<td width="1%" align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form?uuid=${reportSchema.uuid}">
								<img src='<c:url value="/images/edit.gif"/>' border="0"/>
							</a>
						</td>
						<td width="20%">
							<a href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form?uuid=${reportSchema.uuid}">
								${reportSchema.name}
							</a>
							<!-- Disabling link until the generic report schema form is complete -->
							<!-- 
							<a href="${pageContext.request.contextPath}/module/reporting/editReportSchema.form?uuid=${reportSchema.uuid}">${reportSchema.name}</a>
							-->
						</td>
						<td width="30%">
							${reportSchema.description}
						</td>
						<td width="10%">
							Created by ${reportSchema.creator.username} on ${reportSchema.dateCreated}
						</td>
						<td width="1%" align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/evaluateReport.form?uuid=${reportSchema.uuid}">
								<img src='<c:url value="/images/play.gif"/>' border="0"/>
							</a>
						</td>
						<td width="1%" align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/purgeReport.form?uuid=${reportSchema.uuid}">
								<img src='<c:url value="/images/trash.gif"/>' border="0"/>							
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
			<!--  
				<tr>
					<th colspan="5" align="center" height="50">
						<a href="${pageContext.request.contextPath}/module/reporting/indicatorReport.form">Add Indicator Report</a>
					</th>			
				</tr>
			-->	
			</tfoot>
		</table>
	
	</div>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>