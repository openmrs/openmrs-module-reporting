<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/dashboard/index.form" />

<%@ include file="../run/localHeader.jsp"%>

	<div style="float: left">
		<openmrs:portlet url="reportHistory" moduleId="reporting"/>
	</div>
	<div style="float: left">
		<openmrs:portlet url="runReport" moduleId="reporting"/>
	</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>