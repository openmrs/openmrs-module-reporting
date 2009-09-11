<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/admin/reports/runReport.list" />

<br/>

<openmrs:portlet url="runReport" moduleId="reporting" parameters="showDescription=true"/>

<%@ include file="/WEB-INF/template/footer.jsp" %>