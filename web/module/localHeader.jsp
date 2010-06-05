<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%-- Since OpenMRS 1.7 jquery is included in no-conflict mode by header.jsp. We need to negate that --%>
<script type="text/javascript">
	if ($j != null) {
		$ = $j;
	}
</script>

<%@ include file="/WEB-INF/view/module/reporting/dialogSupport.jsp" %>

<div style="border-bottom: 1px solid black;">
	<ul id="menu">
		<li class="first">
			<a href="${pageContext.request.contextPath}/module/reporting/manageDashboard.form" style="text-decoration:none;"><spring:message code="@MODULE_ID@.title" /></a>
		</li>
		<li class="">
			<a href="${pageContext.request.contextPath}/module/reporting/manageCohortDefinitions.list">Manage Cohorts</a>
		</li>	
		<li class="">
			<a href="${pageContext.request.contextPath}/module/reporting/manageDatasets.list">Manage Datasets</a>
		</li>
		<li class="">
			<a href="${pageContext.request.contextPath}/module/reporting/indicators/manageIndicators.list">Manage Indicators</a>
		</li>	
		<li class="last">
			<a href="${pageContext.request.contextPath}/module/reporting/reports/manageReports.list">Manage Reports</a>
		</li>
	</ul>
</div>
