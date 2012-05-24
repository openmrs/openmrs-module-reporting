<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		
		$(".reporting-data-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

		<c:forEach items="${reportDefinitions}" var="reportDefinition" varStatus="status">
			$("#preview-report-${reportDefinition.uuid}").click(function(event){ 
				showReportingDialog({ 
					title: 'Run Report', 
					url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${reportDefinition.uuid}&type=${reportDefinition['class'].name}',
					successCallback: function() { 
						window.location = window.location; //.reload(true);
					} 
				});
			});
	
			$("#render-report-${reportDefinition.uuid}").click(function(event){ 
				showReportingDialog({ 
					title: 'Run Report', 
					url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${reportDefinition.uuid}&type=${reportDefinition['class'].name}&format=indicator&successView=redirect:/module/reporting/reports/renderReport.form',
					successCallback: function() { 
						window.location = window.location; //.reload(true);
					} 
				});
			});	
		</c:forEach>
	} );

	function confirmDelete(name, uuid) {
		if (confirm("Are you sure you want to delete " + name + "?")) {
			document.location.href = '${pageContext.request.contextPath}/module/reporting/purgeReport.form?uuid=' + uuid;
		}
	}

</script>

<style>
	.small { font-size: x-small; }
</style>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.manageReports.title"/></h1>
		
		<spring:message code="reporting.manage.createNew"/>:
		<c:forEach var="createLink" items="${createLinks}">
			<input type="button" value="${createLink.key}" onClick="window.location='${createLink.value}';"/>
		</c:forEach>

		<table class="reporting-data-table display">
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>Type</th>
					<th>Creator</th>
					<th>Created</th>
					<th align="center" width="1%">Actions</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportDefinitions}" var="reportDefinition" varStatus="status">
					<c:set var="editUrl">
						<c:choose>
							<c:when test="${reportDefinition['class'].simpleName == 'PeriodIndicatorReportDefinition'}">
								${pageContext.request.contextPath}/module/reporting/reports/periodIndicatorReport.form?uuid=${reportDefinition.uuid}
							</c:when>
							<c:otherwise>
								${pageContext.request.contextPath}/module/reporting/reports/reportEditor.form?uuid=${reportDefinition.uuid}
							</c:otherwise>							
						</c:choose>
					</c:set>

					<tr>
						<td width="20%" nowrap>
							${reportDefinition.name}
						</td>
						<td width="20%">
							${reportDefinition.description}
						</td>
						<td width="10%" nowrap>
							<rpt:displayLabel type="${reportDefinition['class'].name}"/>
						</td>
						<td width="5%" nowrap>
							${reportDefinition.creator}
						</td>
						<td width="5%" nowrap>
							<rpt:timespan then="${reportDefinition.dateCreated}"/>
						</td>
						<td width="1%" align="center" nowrap>
							&nbsp;
							<a href="${editUrl}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:confirmDelete('${reportDefinition.name}','${reportDefinition.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
							&nbsp;
							<a href="${pageContext.request.contextPath}/module/reporting/run/runReport.form?reportId=${reportDefinition.uuid}">
								<img src='<c:url value="/images/play.gif"/>' align="absmiddle" border="0"/>
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