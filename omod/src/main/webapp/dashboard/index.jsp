<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/dashboard/index.form" />

<%@ include file="../run/localHeader.jsp"%>

<style>
	.datatables_info {
		font-weight:normal;
		font-size: 8pt;
	}
</style>

<div id="page">
	<div id="container">
		<table style="width:100%; padding:10px;">
			<tr>
				<td valign="top" style="width:35%; padding-right:10px;">
				
					<div id="availableReportSection">
						<fieldset>
							<legend>
								<b><spring:message code="reporting.availableReports"/></b>
							</legend>
							<openmrs:portlet url="reportList" moduleId="reporting" parameters="numOnPage=15"/>
						</fieldset>
					</div>
					<br/>
					<div id="queuedReportSection">
						<fieldset>
							<legend><b><spring:message code="reporting.Report.inProgress.title"/></b></legend>
							<div style="padding:5px;">
								<openmrs:portlet url="reportRequests" id="queuedRequests" moduleId="reporting" parameters="status=REQUESTED,PROCESSING|numOnPage=10"/>
							</div>
						</fieldset>
					</div>
					
				</td>
				<td valign="top" style="width:65%;">
					<fieldset>
						<legend>
							<b><spring:message code="reporting.Report.mostRecentlyCompletedReport"/></b>
							&nbsp;&nbsp;
							<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistory.form">
								(<spring:message code="reporting.viewAll"/>)
							</a>
						</legend>
						<openmrs:portlet url="reportRequests" id="completedRequests" moduleId="reporting" parameters="status=SAVED,COMPLETED,FAILED|mostRecentNum=10|numOnPage=10"/>
					</fieldset>
				</td>
			</tr>
		</table>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>