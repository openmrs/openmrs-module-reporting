<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		
		$j(".reporting-data-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

	} );

	function confirmDelete(name, uuid) {
		if (confirm("Are you sure you want to delete " + name + "?")) {
			document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/purgeReport.form?uuid=' + uuid;
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
					<th><spring:message code="reporting.name" /></th>
					<th><spring:message code="reporting.description" /></th>
					<th><spring:message code="reporting.type" /></th>
					<th><spring:message code="reporting.createdBy" /></th>
					<th><spring:message code="reporting.created" /></th>
					<th align="center" width="1%"><spring:message code="reporting.actions" /></th>
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
							<c:out value="${reportDefinition.name}" />
						</td>
						<td width="20%">
							<c:out value="${reportDefinition.description}" />
						</td>
						<td width="10%" nowrap>
							<rpt:displayLabel type="${reportDefinition['class'].name}"/>
						</td>
						<td width="5%" nowrap>
							<c:out value="${reportDefinition.creator}" />
						</td>
						<td width="5%" nowrap>
							<rpt:timespan then="${reportDefinition.dateCreated}"/>
						</td>
						<td width="1%" align="center" nowrap>
							&nbsp;
							<a href="${editUrl}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:confirmDelete('<c:out value="${rpt:getSafeJsString(reportDefinition.name)}" />','${reportDefinition.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
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
