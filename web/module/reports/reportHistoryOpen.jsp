<%@ include file="/WEB-INF/template/include.jsp"%>
<%@page import="org.openmrs.module.reporting.report.ReportRequest"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<c:url var="iconFilename" value="/images/file.gif"/>

<script type="text/javascript" charset="utf-8">
	var i=0;
	function loadReportStatus() {
	    $.getJSON('${pageContext.request.contextPath}/module/reporting/reports/loadReportStatus.form?uuid=${request.uuid}', function(data) {
	    	var statusText = '';
	    	for (var i=0; i< data.logEntries.length; i++) {
	    		statusText += data.logEntries[i] + "<br/>"
            }	    	
	    	$("#reportStatusDiv").html(statusText);
	    	if (data.status == 'COMPLETED' || data.status == 'SAVED') {
	    		if (data.action == 'download') {
	    			$("#downloadReportDiv").show();
	    			$("#runAgainDiv").show();
	    		}
	    		else if (data.action == 'view') {
	    			$("#viewReportDiv").show();
	    			$("#runAgainDiv").show();
	    		}
	    	}
	    	else if (data.status == 'FAILED') {
	    		$("#errorDiv").html(data.errorDetails).show();
	    	}
	    	else {
	    		setTimeout("loadReportStatus()", 3000);
	    	}
	    });
	}

	$(document).ready(function() {
		loadReportStatus();
	} );	
</script>

<h1>
	${request.reportDefinition.parameterizable.name}
</h1>

<table style="width:100%; padding:10px;">
	<tr>
		<td valign="top">
			<b><spring:message code="general.parameters"/></b><br/>
			<c:forEach var="p" items="${request.reportDefinition.parameterMappings}">
				${p.key}: <rpt:format object="${p.value}"/><br/>
			</c:forEach>
			<br/><br/>
			<b><spring:message code="reporting.reportHistory.baseCohort"/></b><br/>
			<c:choose>
				<c:when test="${!empty request.baseCohort}">
					${request.baseCohort.parameterizable.name}
					<table class="small" cellspacing="0" cellpadding="0">
						<c:forEach var="p" items="${request.baseCohort.parameterMappings}">
							<tr valign="top">
								<td class="faded" align="right">
									${p.key}:
								</td>
								<td>
									<rpt:format object="${p.value}"/>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:when>
				<c:otherwise><spring:message code="reporting.allPatients"/></c:otherwise>
			</c:choose>
			<br/><br/><br/>
			<div id="downloadReportDiv" style="display:none; padding:5px;">
				<button onClick="window.location='reportHistoryDownload.form?uuid=${request.uuid}';" style="width:100px; height:40px;">
					<b><spring:message code="general.download"/></b><br/>
					<img src="${iconFilename}" border="0" width="16" height="16"/>
				</button>
			</div>
			<div id="viewReportDiv" style="display:none; padding:5px;">
				<button onClick="window.location='reportHistoryView.form?uuid=${request.uuid}';" style="width:100px; height:40px;">
					<b><spring:message code="general.view"/></b><br/>
					<img src="${iconFilename}" border="0" width="16" height="16"/>
				</button>
			</div>
			<div id="runAgainDiv" style="display:none; padding:5px;">
				<button onClick="window.location='../run/runReport.form?copyRequest=${request.uuid}';" style="width:100px; height:40px;">
					<b><spring:message code="reporting.reportHistory.runAgain"/></b><br/>
					<img src="<c:url value="/images/play.gif"/>" border="0" width="16" height="16"/>
				</button>
			</div>
		</td>
		<td valign="top">
			<b><spring:message code="reporting.reportHistory.runDetails"/></b><br/>
			<div id="reportStatusDiv"></div>
			<br/><br/>
			<div id="errorDiv"></div>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>