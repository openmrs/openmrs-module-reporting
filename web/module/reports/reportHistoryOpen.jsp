<%@ include file="/WEB-INF/template/include.jsp"%>
<%@page import="org.openmrs.module.reporting.report.ReportRequest"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<c:url var="iconFilename" value="/images/file.gif"/>

<h1>
	${request.reportDefinition.parameterizable.name}
</h1>

<table style="width:100%; padding:10px;">
	<tr>
		<th style="text-align:left"><spring:message code="general.parameters"/></th>
		<th style="text-align:left"><spring:message code="reporting.reportHistory.runDetails"/> </th>
		<th style="text-align:left"><spring:message code="reporting.reportHistory.actions"/></th>
	</tr>
	<tr>
		<td valign="top">
			<c:forEach var="p" items="${request.reportDefinition.parameterMappings}">
				${p.key}: <rpt:format object="${p.value}"/><br/>
			</c:forEach>
		</td>
		<td valign="top">
			<spring:message code="reporting.reportHistory.requestedBy"/>: <rpt:format object="${request.requestedBy}"/><br/>
			<spring:message code="reporting.reportHistory.requestedOn"/>: <openmrs:formatDate date="${request.requestDate}" type="long"/><br/>
			<spring:message code="reporting.reportHistory.evaluationStart"/>: <openmrs:formatDate date="${request.evaluateStartDatetime}" type="long"/><br/>
			<spring:message code="reporting.reportHistory.status"/>: ${request.status}
			<c:if test="${request.status == 'COMPLETED'}">
				<rpt:timespan now="${request.evaluateCompleteDatetime}" then="${request.evaluateStartDatetime}" showAgoWord="false"/><br/>
			</c:if>
		</td>
		<td valign="top">
			<c:if test="${action == 'download'}">
				<button onClick="window.location='reportHistoryDownload.form?uuid=${request.uuid}';">
					<spring:message code="general.download"/>
					<img src="${iconFilename}" border="0" width="16" height="16"/>
				</button>
			</c:if>
			<c:if test="${action == 'view'}">
				<button onClick="window.location='reportHistoryView.form?uuid=${request.uuid}';">
					<spring:message code="general.view"/>
					<img src="${iconFilename}" border="0" width="16" height="16"/>
				</button>
			</c:if>
			<button onClick="window.location='../run/runReport.form?copyRequest=${request.uuid}';">
				<h4><spring:message code="reporting.reportHistory.runAgain"/></h4>
				<img src="<c:url value="/images/play.gif"/>" border="0" width="16" height="16"/> <br/>
			</button>
		</td>
</table>

<br/><br/>

<c:if test="${!empty errorDetails}">
	<div style="width:100%; height:200px; overflow: auto; border: 1px solid #666;">
		Failed due to error:<br/>
		<span>${errorDetails}</span>
	</div>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>