<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<openmrs:htmlInclude file="/dwr/interface/DWRReportingService.js"/>

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
	
	function cancelReportRequest(id){
		if(id && confirm('<spring:message code="reporting.manageReportQueue.cancel.confirm" />')){
			DWRReportingService.purgeReportRequest(id, function(success){
				if(success == true){
					pos = reportRequestsTable.fnGetPosition(document.getElementById('reportRequest_'+id));
					if(pos != undefined)
						reportRequestsTable.fnDeleteRow(pos);
				}else{
					alert('<spring:message code="reporting.manageReportQueue.error.failedToCancel" />');
				}
			});
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
			<th><spring:message code="reporting.manageReportQueue.reportname"/></th>
			<th><spring:message code="reporting.manageReportQueue.parameters"/></th>
			<th><spring:message code="reporting.manageReportQueue.requestedOn"/></th>
			<th><spring:message code="reporting.manageReportQueue.priority"/></th>
			<th><spring:message code="reporting.manageReportQueue.status"/></th>
			<th><spring:message code="reporting.manageReportQueue.actions"/></th>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="reportRequest" items="${model.reportRequests}">
		<tr id="reportRequest_${reportRequest.id}">
			<td valign="top">${reportRequest.reportDefinition.parameterizable.name}</td>
			<td valign="top">
			<c:forEach var="entry" items="${reportRequest.reportDefinition.parameterMappings}">
				${entry.key}: ${entry.value}<br />
			</c:forEach>
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
						<spring:message code="reporting.manageReportQueue.position"/> ${model.reportPositionMap[reportRequest.id]}
					</c:when>
					<c:otherwise><spring:message code="general.unknown"/></c:otherwise>
				</c:choose>
				</c:if>
			</td>
			<td valign="top" style="text-align: center;">
				<c:if test="${reportRequest.status == 'REQUESTED'}">
				<span class="cancel" onclick="cancelReportRequest(${reportRequest.id})">
					<img src="<c:url value='/moduleResources/reporting/images/cancel.gif'/>" border="0" /><br />
					<u><spring:message code="general.cancel"/></u>
				</span>
				</c:if>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>