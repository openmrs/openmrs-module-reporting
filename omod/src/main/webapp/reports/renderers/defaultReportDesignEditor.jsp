<%@ include file="../../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />

<openmrs:portlet url="reportDesignForm" id="reportDesignPortlet" moduleId="reporting" parameters="${parameters}" />

<%@ include file="/WEB-INF/template/footer.jsp"%>
