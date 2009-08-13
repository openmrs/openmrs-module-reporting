<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/reporting.css"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/css/jquery.autocomplete.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/page.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/table.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/custom.css"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.dataTables.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js"/>

<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.bgiframe.min.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.ajaxQueue.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.autocomplete.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/thickbox-compressed.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/jgcharts/jgcharts.js'/>

<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/reporting.js'/>

<div style="border-bottom: 1px solid black;">
	<ul id="menu">
		<li class="first">
			<a href="index.htm" style="text-decoration:none;"><spring:message code="@MODULE_ID@.title" /></a>
		</li>
		<li class="">
			<a href="${pageContext.request.contextPath}/module/reporting/manageCohortDefinitions.list">Manage Cohorts</a>
		</li>	
		<li class="">
			<a href="${pageContext.request.contextPath}/module/reporting/manageDatasets.list">Manage Datasets</a>
		</li>
		<li class="">
			<a href="${pageContext.request.contextPath}/module/reporting/manageIndicators.list">Manage Indicators</a>
		</li>	
		<li class="last">
			<a href="${pageContext.request.contextPath}/module/reporting/reports/reportManager.list">Manage Reports</a>
		</li>
	</ul>
</div>

