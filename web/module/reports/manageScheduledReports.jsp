<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageScheduledReports.form" />
<%@ include file="../manage/localHeader.jsp"%>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/cron-editing.js"/>

<c:set var="returnUrl" value="/module/reporting/reports/manageScheduledReports.form"/>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		
		$(".scheduledReportsTable").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );
		
		$('.schedule').each(function() {
			var val = $(this).html();
			var quartz = true
			try{
				val = getScheduleDescription(val)
				$(this).html(val)
			} catch(e) {
				console.log(e)
			}
		});
	} );

	function confirmDelete(name, uuid) {
		if (confirm('<spring:message code="reporting.confirmDelete"/> ' + name + '?')) {
			document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/deleteReportRequest.form?uuid=' + uuid+"&returnUrl=${returnUrl}";
		}
	}

</script>

<style>
	.scheduledReportsTable th,td {padding-left:10px; padding-right:10px;}
</style>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.manageTasks.title"/></h1>

        <c:choose>
			<c:when test="${empty scheduledReports}">
				<spring:message code="reporting.none" />
			</c:when>
			<c:otherwise>
			    <table class="scheduledReportsTable display">
					<thead>
						<th><spring:message code="reporting.manageTasks.reportName"/></th>
						<th><spring:message code="reporting.Report.parameters"/></th>
						<th><spring:message code="reporting.manageTasks.outputTo"/></th>
						<th><spring:message code="reporting.manageTasks.scheduleDescription"/></th>
						<th><spring:message code="reporting.manageTasks.nextExecutionTime"/></th>
						<th><spring:message code="reporting.reportHistory.actions"/></th>
					</thead>
					<c:forEach var="scheduledReport" items="${scheduledReports}">
						<tr>
							<td>
								<a href="../run/runReport.form?requestUuid=${scheduledReport.uuid}" style="text-decoration:none;">${scheduledReport.reportDefinition.parameterizable.name}</a>
							</td>
							 <td>
								<table class="small" cellspacing="0" cellpadding="0">
			                        <c:forEach var="parameter" items="${scheduledReport.reportDefinition.parameterizable.parameters}">
			                            <tr valign="top">
			                                <td class="faded" align="right">
			                                    ${parameter.labelOrName}:
			                                </td>
			                                <td>
			                                	<c:forEach var="parameterMapping" items="${scheduledReport.reportDefinition.parameterMappings}">
			                                    	<c:if test="${ parameterMapping.key eq  parameter.name }">
			                                    		<rpt:format object="${parameterMapping.value}"/>
			                                    	</c:if>
			                                    </c:forEach>
			                                </td>
			                            </tr>
			                        </c:forEach>
		                    	</table>
							</td> 
							<td>
								<c:set var="found" value="${ false }"/>
								<c:forEach var="renderingMode" items="${renderingModes}">
				                    <c:if test="${scheduledReport.renderingMode.descriptor == renderingMode.descriptor && !found}">
				                    	${renderingMode.label}
				                    	<c:set var="found" value="${ true }"/>
				                    </c:if>
				                </c:forEach>
							</td>
							<td class="schedule">${scheduledReport.schedule}</td>
							<td>${rpt:nextExecutionTime(scheduledReport.schedule)}</td>
							<td>
								<a href="javascript:confirmDelete('${scheduledReport.reportDefinition.parameterizable.name}','${scheduledReport.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
							</td>
						</tr>
					</c:forEach>
				</table>    
	        </c:otherwise>
    	</c:choose>
	</div>
</div>
<br/>

<%@ include file="/WEB-INF/template/footer.jsp"%>