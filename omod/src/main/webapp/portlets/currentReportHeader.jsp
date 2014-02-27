<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/reporting/resources/reporting.tld" %>

<div style="border-bottom: 1px black solid; margin-bottom: 1em">
	<table width="100%" style="padding:5px;">
		<tr>
			<td align="left" valign="top">
				<b>${__openmrs_report_data.definition.name}</b>
				<c:if test="${!empty __openmrs_report_data.definition.description}">
					<br/><span style="font-size:8pt;">${__openmrs_report_data.definition.description}</span>
				</c:if>
			</td>
			<td align="right" valign="top">
				<table>
					<c:forEach items="${__openmrs_report_data.definition.parameters}" var="p" varStatus="paramStatus">
						<c:set var="paramVal" value="${__openmrs_report_data.context.parameterValues[p.name]}"/>
						<c:if test="${!empty paramVal}">
							<tr>
								<th align="left"><spring:message code="${p.label}"/>:</th>
								<td><rpt:format object="${paramVal}"/></td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</td>
		</tr>
	</table>
</div>