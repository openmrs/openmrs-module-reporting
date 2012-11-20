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
							<th align="left"><spring:message code="${p.label}"/>:</th>
							<td><rpt:format object="${__openmrs_report_data.context.parameterValues[p.name]}"/></td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</table>
</div>