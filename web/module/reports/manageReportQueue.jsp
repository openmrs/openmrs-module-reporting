<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportQueue.htm" />

<%@ include file="../run/localHeader.jsp"%>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.manageReportQueue.header" /></h1>
		<openmrs:portlet url="manageReportQueue" id="manageReportQueuePortlet" moduleId="reporting" />
	</div>
</div>