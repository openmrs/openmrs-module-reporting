<%@ include file="/WEB-INF/template/include.jsp"%>
<%@page import="org.openmrs.module.reporting.report.ReportRequest"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<c:url var="iconFilename" value="/images/file.gif"/>

<h1>
	${request.reportDefinition.name}
</h1>

<table>
	<c:forEach var="p" items="${request.parameterValues}">
		<tr>
			<td align="right" class="faded">${p.key}:</td>
			<td><rpt:format object="${p.value}"/></td>
		</tr>
	</c:forEach>
</table>

<c:if test="${not empty downloadFilename}">
	<div style="clear: left; float: left; margin-right: 2em; text-align: center;">
		<button onClick="window.location='reportHistoryDownload.form?uuid=${request.uuid}';">
			<h4>Download</h4>
			<img src="${iconFilename}" border="0" width="64" height="64"/>
		</button>
		<br/>
		${downloadFilename}
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