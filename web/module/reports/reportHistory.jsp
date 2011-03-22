<%@ include file="/WEB-INF/template/include.jsp"%>
<%@page import="org.openmrs.module.reporting.report.ReportRequest"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<%@ include file="../run/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#report-history-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": false,
			"bInfo": true,
			"bJQueryUI": true
		} );
	} );	
</script>

<style>
	.report-label {
		border: 1px gray solid;
		padding: 0px;
		margin: 0.2em;
	}
</style>

<h2>Report History</h2>

<table id="report-history-table" width="90%" cellspacing="0" cellpadding="3">
	<thead>
		<tr class="small">
			<th>Report</th>
			<th>Parameters</th>
			<th>Open</th>
			<th>Run</th>
			<th>Labels</th>
			<th width="0"></th>
			<th width="0"></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="r" items="${complete}">
			<c:choose>
				<c:when test="${isWebRenderer[r]}">
					<c:set var="openImageFilename" value="/moduleResources/reporting/images/report_icon.gif"/>
				</c:when>
				<c:otherwise>
					<c:set var="openImageFilename" value="/images/file.gif"/>
				</c:otherwise>
			</c:choose>
			<tr valign="baseline">
				<td>
					${r.reportDefinition.parameterizable.name}
				</td>
				<td>
					<table class="small">
						<c:forEach var="p" items="${r.reportDefinition.parameterMappings}">
							<tr>
								<td>${p.key}:</td>
								<td><rpt:format object="${p.value}"/></td>
							</tr>
						</c:forEach>
					</table>
 				</td>
				<td valign="middle">
					<button onClick="window.location='reportHistoryOpen.form?uuid=${r.uuid}';">
						<img src='<c:url value="${openImageFilename}"/>' border="0" width="32" height="32"/>
						<br/>
						${shortNames[r]}
					</button>
				</td>
				<td>
					<rpt:timespan then="${r.requestDate}"/><br/>
					<small>
						by ${r.requestedBy.username}
					</small>
				</td>
				<td>
					<form method="post" action="reportHistoryAddLabel.form">
						<c:forEach var="label" items="${r.labels}" varStatus="status">
							<span class="report-label">
								${label}
								<a href="reportHistoryRemoveLabel.form?uuid=${r.uuid}&label=${label}">[x]</a>
							</span>
						</c:forEach>
						<br/>
						<span class="small">
							Add:
						</span>
						<input type="text" size="6" name="label"/>
						<input class="small" type="submit" value="+"/>
						<input type="hidden" name="uuid" value="${r.uuid}"/>
					</form>
				</td>
				<td>
					<c:choose>
						<c:when test="${r.saved}">
							<img src='<c:url value="/images/checkmark.png"/>' border="0"/>
						</c:when>
						<c:otherwise>
							<a href="reportHistorySave.form?uuid=${r.uuid}"><img src='<c:url value="/images/save.gif"/>' border="0"/></a>
						</c:otherwise>
					</c:choose>
				</td>				<td>
					<a href="reportHistoryDelete.form?uuid=${r.uuid}"><img src='<c:url value="/images/trash.gif"/>' border="0"/></a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>