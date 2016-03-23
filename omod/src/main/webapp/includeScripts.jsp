<!-- Include css from core -->
<link href="<openmrs:contextPath/>/scripts/jquery-ui/css/<spring:theme code='jqueryui.theme.name' />/jquery-ui.custom.css" type="text/css" rel="stylesheet" />

<!-- Include css from reporting module -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/css/jquery.autocomplete.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/page.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/table.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/custom.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/reporting.css"/>

<!-- Include javascript from core. Not needed on most pages unless they don't include the header -->
<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-timepicker-addon.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-datepicker-i18n.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-timepicker-i18n.js" />

<!-- Include javascript from reporting module -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.autocomplete.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/autocomplete/jquery.ajaxQueue.js'/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/reporting/scripts/reporting.js'/>
