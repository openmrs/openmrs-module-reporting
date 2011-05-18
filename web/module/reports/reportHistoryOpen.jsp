<%@ include file="/WEB-INF/template/include.jsp"%>
<%@page import="org.openmrs.module.reporting.report.ReportRequest"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<c:url var="iconFilename" value="/images/file.gif"/>

<h1>
	${request.reportDefinition.parameterizable.name}
</h1>

<table>
	<c:forEach var="p" items="${request.reportDefinition.parameterMappings}">
		<tr>
			<td align="right" class="faded">${p.key}:</td>
			<td><rpt:format object="${p.value}"/></td>
		</tr>
	</c:forEach>
</table>

<c:if test="${!empty action}">
	<div style="clear: left; float: left; margin-right: 2em; text-align: center;">
		<c:if test="${action == 'download'}">
			<button onClick="window.location='reportHistoryDownload.form?uuid=${request.uuid}';">
				Download
				<img src="${iconFilename}" border="0" width="64" height="64"/>
			</button>
		</c:if>
		<c:if test="${action == 'view'}">
			<button onClick="window.location='reportHistoryView.form?uuid=${request.uuid}';">
				View
				<img src="${iconFilename}" border="0" width="64" height="64"/>
			</button>
		</c:if>
		<br/>
		<table>
			<tr class="faded">
				<td align="right">Run by:</td>
				<td><rpt:format user="${request.requestedBy }"/></td>
			</tr>
			<tr class="faded">
				<td align="right">Run on:</td>
				<td><rpt:format date="${request.requestDate}"/></td>
			</tr>
		</table>
	</div>
</c:if>

<div style="float: left">
	<button onClick="window.location='../run/runReport.form?copyRequest=${request.uuid}';">
		<h4>Run again</h4>
		<img src="<c:url value="/images/play.gif"/>" border="0" width="64" height="64"/> <br/>
	</button>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>