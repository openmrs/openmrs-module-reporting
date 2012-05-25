<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/module/reporting/run/runReport.list" />

<br/>

<openmrs:portlet url="runReport" moduleId="reporting" parameters="showDescription=true"/>

<%@ include file="/WEB-INF/template/footer.jsp" %>