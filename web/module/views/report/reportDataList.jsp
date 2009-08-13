<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Patient Cohorts" otherwise="/login.htm" redirect="/reportData.list" />

<%@ include file="localHeader.jsp" %>

<h2>
	<a href="reportData.form">
		${reportData.reportDefinition.name}
	</a>
</h2>
<i>${reportData.reportDefinition.description }</i>

<br/><br/>

<h3>
	Indicator:
	${param.indicator}
	<c:forEach var="column" items="${reportData.dataSets[param.dataSet].definition.columns}">
		<c:if test="${param.indicator == column.key }">
			... ${column.description}
        </c:if>
    </c:forEach>
    <a style="margin-left: 3em" href="${pageContext.request.contextPath}/admin/reports/reportData.form"><spring:message code="Report.cohortReport.backToReport"/></a>
</h3>

<br/><br/>

<b class="boxHeader"><spring:message code="Report.cohortReport.patients"/></b>
<div class="box">
    <openmrs:portlet url="cohort" parameters="linkUrl=${pageContext.request.contextPath}/patientDashboard.form|target=_blank" />
</div>
 
<script type="text/javascript">
    cohort_refreshDisplay();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>