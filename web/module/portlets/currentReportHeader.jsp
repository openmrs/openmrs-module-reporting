<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/reporting/resources/reporting.tld" %>

<div style="border-bottom: 1px black solid; margin-bottom: 1em">
	<table width="100%">
		<tr>
			<td align="left" valign="top">
				<h2>${__openmrs_report_data.definition.name}</h2>
				<table>
					<c:forEach items="${__openmrs_report_data.definition.parameters}" var="p" varStatus="paramStatus">
						<tr>
							<th align="left">${p.label}:</th>
							<td><rpt:format object="${__openmrs_report_data.context.parameterValues[p.name]}"/></td>
						</tr>
					</c:forEach>
				</table>
			</td>
			<td align="right">
				<c:if test="${model.showDiscardButton == 'true'}">
					<button class="small" onClick="window.location='<c:url value="/module/reporting/run/currentReportDiscard.form"/>'">
						<img src="<c:url value="/images/delete.gif"/>" border="0"/><br/>
						Discard
					</button>
				</c:if>
				<%-- TODO put save button here, but we need to have a Report or ReportRequest in the session for that --%>
			</td>
		</tr>
	</table>
</div>