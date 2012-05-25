<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:if test="${empty model.requests}">
	<spring:message code="general.none"/>
</c:if>

<c:forEach items="${model.requests}" var="rr" varStatus="rrStatus">

	<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistoryOpen.form?uuid=${rr.uuid}">
		<c:choose>
			<c:when test="${rr.status == 'SAVED' || rr.status == 'COMPLETED'}">
				<spring:message code="reporting.completedOn"/>
				<openmrs:formatDate date="${rr.evaluateCompleteDatetime}" format="dd/MMM/yyyy HH:mm"/> (<rpt:format object="${rr.requestedBy}"/>)
			</c:when>
			<c:when test="${rr.status == 'FAILED'}">
				<spring:message code="reporting.failedOn"/>
				<openmrs:formatDate date="${rr.evaluateCompleteDatetime}" format="dd/MMM/yyyy HH:mm"/> (<rpt:format object="${rr.requestedBy}"/>)
			</c:when>
			<c:when test="${rr.status == 'PROCESSING'}">
				<spring:message code="reporting.startedOn"/> <openmrs:formatDate date="${rr.evaluateStartDatetime}" format="dd/MMM/yyyy HH:mm"/> (<rpt:format object="${rr.requestedBy}"/>)
			</c:when>
			<c:when test="${rr.status == 'SCHEDULED'}">
				<rptTag:cronDisplay id="${rr.id}Schedule" expression="${rr.schedule}"/>
			</c:when>
			<c:otherwise>
				<spring:message code="reporting.requestedOn"/> <openmrs:formatDate date="${rr.requestDate}" format="dd/MMM/yyyy hh:mm"/> (<rpt:format object="${rr.requestedBy}"/>)
			</c:otherwise>
		</c:choose>
	</a>
	<div style="padding-left:10px; font-size:smaller;">
		<table>
			<c:forEach var="p" items="${rr.reportDefinition.parameterizable.parameters}">
				<tr valign="top">
					<td class="faded" align="right">
						${p.label}:
					</td>
					<td>
						<rpt:format object="${rr.reportDefinition.parameterMappings[p.name]}"/>
					</td>
				</tr>
			</c:forEach>
		</table>		
	</div>
	<c:if test="${!rrStatus.last}">
		<br/>
	</c:if>
</c:forEach>
