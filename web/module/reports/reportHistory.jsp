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
			<th>Description</th>
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
						<c:choose>
							<c:when test="${r.status == 'FAILED'}">
								<img src='<c:url value="/images/error.gif"/>' border="0" width="16" height="16"/>
							</c:when>
							<c:otherwise>
								<img src='<c:url value="${openImageFilename}"/>' border="0" width="16" height="16"/>
								<br/>
								${shortNames[r]}
							</c:otherwise>
						</c:choose>
					</button>
				</td>
				<td>
					<rpt:timespan then="${r.requestDate}"/><br/>
					<small>
						by ${r.requestedBy.username}
					</small>
				</td>
				<td>
					${r.description}
				</td>
				<td>
					<c:choose>
						<c:when test="${r.status == 'SAVED'}">
							<img src='<c:url value="/images/checkmark.png"/>' border="0"/>
						</c:when>
						<c:when test="${fn:contains(cached, r)}">
							<a href="reportHistorySave.form?uuid=${r.uuid}"><img src='<c:url value="/images/save.gif"/>' border="0"/></a>
						</c:when>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				</td>				
				<td>
					<a href="reportHistoryDelete.form?uuid=${r.uuid}"><img src='<c:url value="/images/trash.gif"/>' border="0"/></a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>