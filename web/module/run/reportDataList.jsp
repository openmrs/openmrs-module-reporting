<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<openmrs:require privilege="View Patient Cohorts" otherwise="/login.htm" redirect="/reportData.list" />

<%@ include file="localHeader.jsp" %>

<h2>
	<a href="reportData.form">
		${reportData.definition.name}
	</a>
</h2>
<i>${reportData.definition.description }</i>

<br/><br/>

<h3>
	Indicator:
	${param.indicator}
	<c:forEach var="def" items="${reportData.definition.dataSetDefinitions}">
        <c:forEach var="description" items="${def.descriptions}" varStatus="dVarStat">
            <c:if test="${param.indicator == description.key }">
                ... ${description.value}
            </c:if>
        </c:forEach>
    </c:forEach>
    <a style="margin-left: 3em" href="${pageContext.request.contextPath}/admin/reports/reportData.form"><spring:message code="reporting.Report.cohortReport.backToReport"/></a>
</h3>

<br/><br/>

<b class="boxHeader"><spring:message code="reporting.Report.cohortReport.patients"/></b>
<div class="box">
    <openmrs:portlet url="cohort" parameters="linkUrl=${pageContext.request.contextPath}/patientDashboard.form|target=_blank" />
</div>
 
<script type="text/javascript">
    cohort_refreshDisplay();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>