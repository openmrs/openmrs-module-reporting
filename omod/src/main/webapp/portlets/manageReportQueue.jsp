<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:set var="returnUrl" value="/module/reporting/reports/manageReportQueue.htm"/>

<script type="text/javascript">
	var reportRequestsTable;
	jQuery(document).ready(function() {
		reportRequestsTable = jQuery('#reportRequestsTable').dataTable({
		    "bPaginate": true,
		    "iDisplayLength": 15,
		    "bLengthChange": false,
		    "bFilter": true,
		    "bInfo": true,
		    "bAutoWidth": false,
		    "bSortable": true
		});
	});
	
	function cancelReportRequest(uuid){
		if(uuid && confirm('<spring:message code="reporting.reportRequest.cancel.confirm" />')){
			document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/deleteReportRequest.form?uuid=' + uuid + '&returnUrl=${returnUrl}';
		}
	}
</script>

<style>
span.cancel{
	cursor: pointer;
}
</style>

<table id="reportRequestsTable" class="reporting-data-table display">
	<thead>
		<tr>
			<th><spring:message code="reporting.reportRequest.reportName"/></th>
			<th><spring:message code="reporting.reportRequest.parameters"/></th>
			<th><spring:message code="reporting.reportRequest.requestedOn"/></th>
			<th><spring:message code="reporting.reportRequest.priority"/></th>
			<th><spring:message code="reporting.reportRequest.status"/></th>
			<th><spring:message code="reporting.reportRequest.actions"/></th>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="reportRequest" items="${model.reportRequests}">
		<tr id="reportRequest_${reportRequest.id}">
			<td valign="top">${reportRequest.reportDefinition.parameterizable.name}</td>
			<td valign="top">
				<table class="small" cellspacing="0" cellpadding="0">
					<c:forEach var="parameter" items="${reportRequest.reportDefinition.parameterizable.parameters}">
						<tr valign="top">
							<td class="faded" align="right">
								${parameter.labelOrName}:
							</td>
							<td><rpt:format object="${reportRequest.reportDefinition.parameterMappings[parameter.name]}"/></td>
						</tr>
					</c:forEach>
				</table>
			</td>
			<td valign="top">
				<openmrs:formatDate date="${reportRequest.requestDate}" format="dd/MMM/yyyy h:mm a" /><br />
				${reportRequest.requestedBy.personName}
			</td>
			<td valign="top">${reportRequest.priority}</td>
			<td valign="top">
				${reportRequest.status}
				<c:if test="${reportRequest.status == 'REQUESTED'}">
				<br />
				<c:choose>
					<c:when test="${model.reportPositionMap[reportRequest.id] != null && model.reportPositionMap[reportRequest.id] > 0}">
						<spring:message code="reporting.reportRequest.position"/> ${model.reportPositionMap[reportRequest.id]}
					</c:when>
					<c:otherwise><spring:message code="general.unknown"/></c:otherwise>
				</c:choose>
				</c:if>
			</td>
			<td valign="top" style="text-align: center;">
				<c:if test="${reportRequest.status == 'REQUESTED'}">
				<span class="cancel" onclick="cancelReportRequest('${reportRequest.uuid}')">
					<img src="<c:url value='/images/delete.gif'/>" border="0" /><br />
					<u><spring:message code="general.cancel"/></u>
				</span>
				</c:if>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>