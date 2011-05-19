<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/dashboard/index.form" />

<%@ include file="../run/localHeader.jsp"%>

	<div>
		<table>
			<tr>
				<td valign="top">
					<openmrs:portlet url="savedReports" moduleId="reporting"/>
					<openmrs:portlet url="reportHistory" moduleId="reporting" parameters="includeSaved=false|includeError=false|title=Unsaved Reports"/>
				</td>
				<td valign="top">
					<openmrs:portlet url="runReport" moduleId="reporting"/>
					<openmrs:portlet url="queuedReports" moduleId="reporting"/>
				</td>
				<td valign="top">
					<openmrs:portlet url="errorReports" moduleId="reporting"/>
				</td>
			</tr>
		</table>
	</div>
<script>
jqUiDecoration();
</script>
<%@ include file="/WEB-INF/template/footer.jsp"%>