<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/dashboard/index.form" />

<%@ include file="../run/localHeader.jsp"%>

	<div>
		<table>
			<tr>
				<td valign="top">
					<openmrs:portlet url="savedReports" moduleId="reporting"/>
					<spring:message var="portletTitle" code="reporting.Report.unsaved.title"/>
					<openmrs:portlet url="reportHistory" moduleId="reporting" parameters="includeSaved=false|includeError=false|title=${portletTitle}"/>
				</td>
				<td valign="top">
					<openmrs:portlet url="runReport" moduleId="reporting"/>
					<openmrs:portlet url="queuedReports" moduleId="reporting"/>
				</td>
				<td valign="top">
					<openmrs:portlet url="scheduledReports" moduleId="reporting"/>
					<openmrs:portlet url="errorReports" moduleId="reporting"/>
				</td>
			</tr>
		</table>
	</div>
<script>
jqUiDecoration();
</script>
<%@ include file="/WEB-INF/template/footer.jsp"%>