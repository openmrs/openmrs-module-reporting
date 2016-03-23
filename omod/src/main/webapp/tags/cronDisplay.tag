<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="expression" required="true" type="java.lang.String" %>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/cron-editing.js"/>

<script type="text/javascript" charset="utf-8">
	jQuery(document).ready(function() {
		jQuery("#${id}").each(function() {
			var val = $j(this).html();
			try {
				val = getScheduleDescription(val, '<openmrs:datePattern/>');
				$j(this).html(val);
			} catch(e) {
				console.log(e);
			}
		});
	});
</script>

<span id="${id}">${expression}</span>