<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:choose>

	<c:when test="${empty model.reportId}">
	
		<script type="text/javascript">
			jQuery(document).ready(function() {
				jQuery('#${model.portletUUID}RequestTable').dataTable({
				    "bPaginate": true,
				    "iDisplayLength": ${model.numOnPage},
				    "bLengthChange": false,
				    "bFilter": false,
				    "bInfo": true,
				    "bAutoWidth": false,
				    "bSortable": false
				});
			});
		</script>

		<table id="${model.portletUUID}RequestTable" style="width:100%;" class="reporting-data-table display">
			<thead style="display:none"><tr><th colspan="5"></th></tr></thead>
			<tbody>
				<c:forEach items="${model.requests}" var="rr" varStatus="rrStatus">
					<tr>
						<td style="display:none;">${rrStatus.index}</td>
						<td align="center">
							<c:choose>
								<c:when test="${rr.status == 'FAILED'}">
									<img src="<c:url value="/images/error.gif"/>" width="12" height="12" border="0" style="vertical-align:middle"/>
								</c:when>
								<c:otherwise>
									<img src="<c:url value="/images/checkmark.png"/>" width="12" height="12" border="0" style="vertical-align:middle"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistoryOpen.form?uuid=${rr.uuid}">
								<c:out value="${rr.reportDefinition.parameterizable.name}" />
							</a>
						</td>
						<td>
							<rpt:format object="${rr.requestedBy}"/>
						</td>
						<td>
							<c:choose>
								<c:when test="${!empty rr.evaluateCompleteDatetime}">
									<openmrs:formatDate date="${rr.evaluateCompleteDatetime}" format="dd/MMM/yyyy HH:mm"/>
								</c:when>
								<c:otherwise>
									<openmrs:formatDate date="${rr.evaluateStartDatetime}" format="dd/MMM/yyyy HH:mm"/>
								</c:otherwise>
							</c:choose>
							
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	
	<c:otherwise>

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
		
	</c:otherwise>
	
</c:choose>
