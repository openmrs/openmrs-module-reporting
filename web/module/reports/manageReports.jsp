<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#report-schema-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		} );
	} );
</script>
<style>
.small { font-size: x-small; }
</style>


<div id="page">
	<div id="container">
		<h1>Report Manager</h1>
		
		<spring:message code="reporting.manage.createNew"/>:
		<c:forEach var="createLink" items="${createLinks}">
			<input type="button" value="${createLink.key}" onClick="window.location='${createLink.value}';"/>
		</c:forEach>

		<table id="report-schema-table" class="display" >
			<thead>
				<tr>
					<th>Name</th>
					<th>Type</th>
					<th>Description</th>
					<th>Author</th>
					<th>Design</th>
					<th>Preview</th>
					<th>Remove</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportDefinitions}" var="reportDefinition" varStatus="status">
					<c:set var="editUrl">
						<c:choose>
							<c:when test="${reportDefinition.class.simpleName == 'PeriodIndicatorReportDefinition'}">
								${pageContext.request.contextPath}/module/reporting/reports/periodIndicatorReportEditor.form?uuid=${reportDefinition.uuid}
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${reportDefinition.class.simpleName == 'IndicatorReportDefinition'}">
										${pageContext.request.contextPath}/module/reporting/reports/indicatorReportEditor.form?uuid=${reportDefinition.uuid}
									</c:when>
									<c:otherwise>
										${pageContext.request.contextPath}/module/reporting/reports/reportEditor.form?uuid=${reportDefinition.uuid}
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:set>						

					<tr>
						<td width="20%" nowrap="">
							<a href="${editUrl}">
								${reportDefinition.name}
							</a>
						</td>
						<td width="10%" nowrap="">
							${reportDefinition.class.simpleName}
						</td>
						<td width="20%">
							<span class="small">${reportDefinition.description}</span>
						</td>
						<td width="10%">
							<span class="small">${reportDefinition.creator}</span>
						</td>
						<td width="1%" nowrap="" align="center">
							<a href="${editUrl}">
								<img src='<c:url value="/images/edit.gif"/>' border="0"/>
							</a>
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