<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<%@ include file="../run/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		$j("#report-history-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bJQueryUI": true,
			"oLanguage": {
				"oPaginate": {
					"sPrevious": "<spring:message code="general.previous" javaScriptEscape="true"/>",
					"sNext": "<spring:message code="general.next" javaScriptEscape="true"/>",
				},
				"sInfo": "<spring:message code="SearchResults.viewing" javaScriptEscape="true"/> _START_ - _END_ <spring:message code="SearchResults.of" javaScriptEscape="true"/> _TOTAL_ ",
				"sSearch": "<spring:message code="general.search" javaScriptEscape="true"/>"
			},
			"aaSorting": [[ 1, "desc" ]]
		} );
	} );
	
	function showErrorDetails(uuid) {
		showReportingDialog({
			title: '<spring:message code="reporting.errorDetails"/>',
			url: '${pageContext.request.contextPath}/module/reporting/reports/viewErrorDetails.form?uuid='+uuid
		});
	}
</script>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.reportHistory.title"/></h1>
		<form method="get">
			<table>
				<tr>
					<td><spring:message code="reporting.reportHistory.reportName"/>: </td>
					<td>
						<spring:message code="reporting.allReports" var="allReportLabel"/>
						<wgt:widget id="reportField" name="reportDefinition" type="org.openmrs.module.reporting.report.definition.ReportDefinition" defaultValue="${reportDefinition}" attributes="emptyLabel=${allReportLabel}"/>	
					</td>
					<td style="padding-left:20px;"><spring:message code="reporting.reportHistory.requestedBy"/>: </td>
					<td><wgt:widget id="requestedByField" name="requestedBy" type="org.openmrs.User" defaultValue="${requestedBy}" format="select" attributes="emptyCode=reporting.allUsers"/></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><spring:message code="reporting.reportHistory.status"/>: </td>
					<td>
						<c:forEach items="${historyStatuses}" var="status">
							<c:set var="selected" value="${statuses == null || fn:contains(statuses, status)}"/>
							<input type="checkbox" name="statuses" value="${status}"<c:if test="${selected}"> checked="true"</c:if>/> 
							<spring:message code="reporting.status.${status}"/>&nbsp;&nbsp;
						</c:forEach>
					</td>
					<td style="padding-left:20px;"><spring:message code="reporting.reportHistory.requestedFrom"/>: </td>
					<td>
						<wgt:widget id="requestOnOrAfterField" name="requestOnOrAfter" type="java.util.Date" defaultValue="${requestOnOrAfter}"/>
						-
						<wgt:widget id="requestOnOrBeforeField" name="requestOnOrBefore" type="java.util.Date" defaultValue="${requestOnOrBefore}"/>
					</td>
					<td><input type="submit" value="<spring:message code="general.search"/>"/></td>
				</tr>
			</table>
		</form>
		
		<hr/>

		<table id="report-history-table" class="display" width="99%" style="padding:3px;">
			<thead>
				<tr>
					<th style="display:none"></th>
					<th><spring:message code="reporting.reportRequest.reportName"/></th>
					<th><spring:message code="reporting.reportRequest.parameters"/></th>
					<th><spring:message code="reporting.reportRequest.outputFormat"/></th>
					<th><spring:message code="reporting.reportRequest.requestedOn"/></th>
					<th><spring:message code="reporting.reportRequest.status"/></th>
					<th style="text-align:center;"><spring:message code="reporting.reportRequest.actions"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="r" items="${history}">
					<tr valign="baseline">
						<td style="display:none"><openmrs:formatDate date="${r.requestDate}" format="yyyy-MM-dd HH:mm:ss"/></td>
						<td>
							<a href="reportHistoryOpen.form?uuid=${r.uuid}">
								<c:out value="${r.reportDefinition.parameterizable.name}" />
							</a>
						</td>
						<td style="white-space:nowrap;">
							<table class="small">
								<c:forEach var="p" items="${r.reportDefinition.parameterizable.parameters}">
									<tr>
										<td>${p.label}:</td>
										<td><rpt:format object="${r.reportDefinition.parameterMappings[p.name]}"/></td>
									</tr>
								</c:forEach>
							</table>
		 				</td>
		 				<td>
		 					${r.renderingMode.label}
		 				</td>
						<td style="white-space:nowrap;">
							<openmrs:formatDate date="${r.requestDate}" format="dd/MMM/yyyy HH:mm"/><br/>
							<small>
								<rpt:format object="${r.requestedBy}"/>
							</small>
						</td>
						<td style="white-space:nowrap;">
							<spring:message code="reporting.status.${r.status}"/>
						</td>
						<td style="text-align:center; vertical-align:middle;">
							<c:choose>
								<c:when test="${r.status == 'FAILED'}">
									<a href="javascript:showErrorDetails('${r.uuid}');">
										<img src='<c:url value="/images/error.gif"/>' border="0" width="16" height="16"/><br/>
										<small><spring:message code="reporting.viewError"/></small>
									</a>
								</c:when>
								<c:otherwise>
									<a href="viewReport.form?uuid=${r.uuid}">
										<img src='<c:url value="/moduleResources/reporting/images/report_icon.gif"/>' border="0" width="16" height="16"/><br/>
										<small><spring:message code="reporting.viewReport"/></small>
									</a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>