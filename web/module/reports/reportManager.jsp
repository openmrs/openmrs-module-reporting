<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportManager.form" />
<%@ include file="../localHeader.jsp"%>

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
	} );
</script>

<div id="page">
	<div id="container">
		<h1>Report Manager</h1>
		
		<form method="get" action="reportEditor.form" style="display:inline">
			<strong>Create a new report:</strong>
			<select name="type">
				<option value="">&nbsp;</option>
				<c:forEach items="${types}" var="type">
					<option value="${type.key.name}">${type.value}</option>
				</c:forEach>
			</select>
			<input type="submit" value="Create"/>
		</form>
		| &nbsp;
		<a href="${pageContext.request.contextPath}/module/reporting/reports/indicatorReportEditor.form">Add Indicator Report</a>

		<table id="report-schema-table" class="display" >
			<thead>
				<tr>
					<th width="1%"></th>
					<th width="1%"></th>
					<th width="10%">Name</th>
					<th width="20%">Type</th>
					<th width="30%">Description</th>
					<th width="10%">Created</th>
					<th width="1%"></th>
					<th width="1%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportDefinitions}" var="reportDefinition" varStatus="status">
					<tr>
						<td width="1%">
							<a href="${pageContext.request.contextPath}/module/reporting/reports/indicatorReportEditor.form?uuid=${reportDefinition.uuid}">
								<img src='<c:url value="/images/edit.gif"/>' border="0"/>
							</a>
						</td>
						<td width="1%" align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/reports/reportEditor.form?uuid=${reportDefinition.uuid}">
								<img src='<c:url value="/images/edit.gif"/>' border="0"/>
							</a>
						</td>
						<td width="10%">
							<a href="${pageContext.request.contextPath}/module/reporting/reports/reportEditor.form?uuid=${reportDefinition.uuid}">
								${reportDefinition.name}
							</a>
						</td>
						<td width="20%">
							${reportDefinition.class.simpleName}
						</td>
						<td width="30%">
							${reportDefinition.description}
						</td>
						<td width="10%">
							Created by ${reportDefinition.creator.username} on ${reportDefinition.dateCreated}
						</td>
						<td width="1%" align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/evaluateReport.form?uuid=${reportDefinition.uuid}">
								<img src='<c:url value="/images/play.gif"/>' border="0"/>
							</a>
						</td>
						<td width="1%" align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/purgeReport.form?uuid=${reportDefinition.uuid}">
								<img src='<c:url value="/images/trash.gif"/>' border="0"/>							
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
			</tfoot>
		</table>
	
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>