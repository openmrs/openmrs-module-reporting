<%@ include file="/WEB-INF/template/include.jsp" %>

<div style="border-bottom: 1px black solid; margin-bottom: 1em">
	<table width="100%">
		<tr>
			<td align="left">
				<h2>${__openmrs_report_data.definition.name}</h2>
			</td>
			<td align="right">
				<button class="small" onClick="window.location='<c:url value="/module/reporting/run/currentReportDiscard.form"/>'">
					<img src="<c:url value="/images/delete.gif"/>" border="0"/><br/>
					Discard
				</button>
				<%-- TODO put save button here, but we need to have a Report or ReportRequest in the session for that --%>
			</td>
		</tr>
	</table>
</div>