<%@ include file="/WEB-INF/template/include.jsp"%>

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
	.small { font-size: x-small; }
	.report-label { border: 1px gray solid; padding: 0px; margin: 0.2em; }
</style>

<h2>Report History</h2>

<table id="report-history-table" width="75%">
	<thead>
		<tr>
			<th></th>
			<th></th>
			<th></th>
			<th></th>
			<th class="small">Labels</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="r" items="${complete}">
			<tr valign="baseline">
				<td>${r.reportDefinition.name}</td>
				<td>
					<table class="small">
						<c:forEach var="p" items="${r.parameterValues}">
							<tr>
								<td>${p.key}:</td>
								<td><rpt:format object="${p.value}"/></td>
							</tr>
						</c:forEach>
					</table>
 				</td>
				<td>
					<rpt:timespan then="${r.requestDate}"/><br/>
					<small>
						by ${r.requestedBy.username}
					</small>
				</td>
				<td>
					<input type="button" value="${shortNames[r]}" onClick="window.location='reportHistoryOpen.form?uuid=${r.uuid}';"/>
					[Other]
					<c:choose>
						<c:when test="${r.saved}">
							<i>Saved</i>
						</c:when>
						<c:otherwise>
							<a href="reportHistorySave.form?uuid=${r.uuid}"><img src='<c:url value="/images/save.gif"/>' border="0"/></a>
						</c:otherwise>
					</c:choose>
					<a href="reportHistoryDelete.form?uuid=${r.uuid}"><img src='<c:url value="/images/trash.gif"/>' border="0"/></a>
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
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>