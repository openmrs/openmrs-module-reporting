<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/reporting/resources/reporting.tld" %>
<%@ taglib prefix="rptTag" tagdir="/WEB-INF/tags/module/reporting" %>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/reporting.css"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/css/jquery.autocomplete.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/page.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/table.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/custom.css"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.dataTables.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js"/>

<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.autocomplete.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.ajaxQueue.js'/>
<%--
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/thickbox-compressed.js'/>
--%>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/jgcharts/jgcharts.js'/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/editable/jquery.jeditable.js"/>

<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/reporting.js'/>