<%@ include file="/WEB-INF/template/include.jsp"%>

<!-- Include taglibs from core -->
<%@ taglib prefix="fn" uri="/WEB-INF/taglibs/fn.tld" %>

<!-- Include taglibs from reporting module -->
<%@ taglib prefix="wgt" uri="/WEB-INF/view/module/htmlwidgets/resources/htmlwidgets.tld" %>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/reporting/resources/reporting.tld" %>
<%@ taglib prefix="rptTag" tagdir="/WEB-INF/tags/module/reporting" %>

<!-- Include css from core -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css"/>

<!-- Include css from reporting module -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/reporting.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/css/jquery.autocomplete.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/page.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/table.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/custom.css"/>

<!-- Include javascript from core -->
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js"/>

<!-- Tell 1.7+ versions of core to not include JQuery themselves. Also, on 1.7+ we may get different jquery and jquery-ui versions than 1.3.2 and 1.7.2 -->
<c:set var="DO_NOT_INCLUDE_JQUERY" value="true"/>

<!-- Include javascript from reporting module -->
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.autocomplete.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.ajaxQueue.js'/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/editable/jquery.jeditable.js"/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/reporting.js'/>