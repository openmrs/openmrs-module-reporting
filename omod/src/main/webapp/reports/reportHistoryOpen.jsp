<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportHistory.form" />

<%@ include file="../run/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	function loadReportStatus() {
	    $.getJSON('${pageContext.request.contextPath}/module/reporting/reports/loadReportStatus.form?uuid=${request.uuid}', function(data) {
	    	$(".status").hide();
	    	$(".status"+data.status).show();
	    	if (data.status != 'COMPLETED' && data.status != 'SAVED' && data.status != 'FAILED' && data.status != 'SCHEDULE_COMPLETED') {
	    		setTimeout("loadReportStatus()", 3000);
	    	}
	    });
	}
	
	function deleteRequest(uuid) {
		if (confirm('<spring:message code="reporting.reportHistory.confirmDelete"/>')) {
			document.location.href='${pageContext.request.contextPath}/module/reporting/reports/deleteReportRequest.form?uuid='+uuid+'&returnUrl=/module/reporting/dashboard/index.form';
		}
	}
	
	$(document).ready(function() {
		
		loadReportStatus();
		
		$("#errorDetailsLink").click(function(event) {
			showReportingDialog({
				title: '<spring:message code="reporting.errorDetails"/>',
				url: '${pageContext.request.contextPath}/module/reporting/reports/viewErrorDetails.form?uuid=${request.uuid}'
			});
		});
	} );	
</script>

<style>
	table.requestTable {padding:10px;}
	table.requestTable td {padding-right:10px;}
	.reportAction {padding:10px;}
	.status {display:none;}
</style>
<c:set var="reportDefinition" value="${request.reportDefinition.parameterizable}"/>

<div id="page">
	<div id="container">
		<h1>${reportDefinition.name}</h1>
		<small>${reportDefinition.description}</small>
		<br/>
		<table style="width:100%; padding:10px;">
			<tr>
				<td valign="top" style="width:50%; padding-right:20px;">
					<fieldset>
						<legend><b><spring:message code="reporting.reportHistory.requestDetails"/></b></legend>
						<table class="requestTable">
							<tr>
								<td><spring:message code="reporting.reportHistory.requestedOn"/>: </td>
								<td><openmrs:formatDate date="${request.requestDate}" format="dd/MMM/yyyy HH:mm"/></td>
							</tr>
							<tr>
								<td><spring:message code="reporting.reportHistory.requestedBy"/>: </td>
								<td><rpt:format object="${request.requestedBy}"/></td>
							</tr>
						</table>
					</fieldset>
					<br/>
					<fieldset>
						<legend><b><spring:message code="reporting.reportRequest.parameters"/></b></legend>
						<table class="requestTable">
							<c:forEach var="p" items="${reportDefinition.parameters}">
								<tr>
									<td>${p.label}: </td>
									<td><rpt:format object="${request.reportDefinition.parameterMappings[p.name]}"/></td>
								</tr>
							</c:forEach>
							<tr>
								<td><spring:message code="reporting.reportRequest.baseCohort"/>:</td>
								<td>
									<c:choose>
										<c:when test="${!empty request.baseCohort}">
											${request.baseCohort.parameterizable.name}
											<table class="small" cellspacing="0" cellpadding="0">
												<c:forEach var="p" items="${request.baseCohort.parameterizable.parameters}">
													<tr valign="top">
														<td class="faded" align="right">
															${p.label}:
														</td>
														<td>
															<rpt:format object="${report.baseCohort.parameterMappings[p.name]}"/>
														</td>
													</tr>
												</c:forEach>
											</table>
										</c:when>
										<c:otherwise><spring:message code="reporting.allPatients"/></c:otherwise>
									</c:choose>								
								</td>
							</tr>
							<tr>
								<td><spring:message code="reporting.reportRequest.outputFormat"/>: </td>
								<td>${request.renderingMode.label}</td>
							</tr>
						</table>		
					</fieldset>
					<c:if test="${!empty automaticProcessors}">
						<br/>
						<fieldset>
							<legend><b><spring:message code="reporting.reportRequest.additionalProcessing"/></b></legend>
							<table class="requestTable">
								<c:forEach items="${automaticProcessors}" var="processor">
									<tr>
										<td valign="top"><li>${processor.name}</li></td>
										<td>
											<small>
												<c:forEach items="${processor.configuration}" var="entry">
													${entry.key}: ${entry.value}<br/>
												</c:forEach>
											</small>
										</td>
								</c:forEach>
							</table>
						</fieldset>
					</c:if>
				</td>
				<td valign="top" style="width:50%;">
					<fieldset>
						<legend><b><spring:message code="reporting.reportRequest.status"/></b></legend>
						<div id="statusDiv" style="padding:10px;">
							<span class="status statusREQUESTED">
								<img src="<c:url value="/images/loading.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.queued"/></span><br/><br/>
								<spring:message code="reporting.reportRequest.position"/>: ${positionInQueue}						
							</span>
							<span class="status statusSCHEDULED">
								<img src="<c:url value="/moduleResources/reporting/images/calendar.png"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.scheduled"/></span><br/><br/>
								<spring:message code="reporting.manageTasks.scheduleDescription"/>: 
								<rptTag:cronDisplay id="${request.id}Schedule" expression="${request.schedule}"/><br/><br/>
								<spring:message code="reporting.manageTasks.nextExecutionTime"/>: 
								${rpt:nextExecutionTime(request.schedule)}
							</span>
							<span class="status statusSCHEDULE_COMPLETED">
								<img src="<c:url value="/images/checkmark.png"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.status.SCHEDULE_COMPLETED"/></span><br/><br/>
								<spring:message code="reporting.manageTasks.scheduleDescription"/>: 
								<rptTag:cronDisplay id="${request.id}ScheduleCompleted" expression="${request.schedule}"/><br/><br/>
							</span>
							<span class="status statusPROCESSING">
								<img src="<c:url value="/images/loading.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.status.PROCESSING"/></span><br/>
							</span>
							<span class="status statusFAILED">
								<img src="<c:url value="/images/error.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.status.FAILED"/></span><br/>
							</span>
							<span class="status statusCOMPLETED">
								<img src="<c:url value="/images/checkmark.png"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.status.COMPLETED"/></span><br/>
							</span>
							<span class="status statusSAVED">
								<img src="<c:url value="/images/save.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<span style="font-size:large"><spring:message code="reporting.status.SAVED"/></span><br/>
							</span>
						</div>
						<div class="logDiv"></div>
					</fieldset>
					<br/>

					<div class="reportAction status statusCOMPLETED statusSAVED">
						<a href="${pageContext.request.contextPath}/module/reporting/reports/viewReport.form?uuid=${request.uuid}" target="__new">
							<img src="<c:url value="/moduleResources/reporting/images/report_icon.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
							<spring:message code="reporting.viewReport"/>
						</a>
					</div>
					
					<div class="reportAction status statusCOMPLETED">
						<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistorySave.form?uuid=${request.uuid}" id="saveReportLink">
							<img src="<c:url value="/images/save.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
							<spring:message code="reporting.saveReport"/>
						</a>
					</div>

					<c:forEach var="processor" items="${onDemandProcessors}">
						<div class="reportAction status statusCOMPLETED statusSAVED">
							<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistoryProcess.form?processorUuid=${processor.uuid}&uuid=${request.uuid}">
								<img src="<c:url value="/images/play.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
								<spring:message code="reporting.processReport"/>: <spring:message code="${processor.name}"/>
							</a>
						</div>
					</c:forEach>
					
					<div class="reportAction status statusSCHEDULED">
						<a href="${pageContext.request.contextPath}/module/reporting/run/runReport.form?requestUuid=${request.uuid}">
							<img src="<c:url value="/images/edit.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
							<spring:message code="reporting.editScheduledReport"/>
						</a>
					</div>
					
					<div class="reportAction status statusFAILED">
						<a href="#" id="errorDetailsLink">
							<img src="<c:url value="/images/error.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
							<spring:message code="reporting.errorDetails"/>
						</a>
					</div>

					<div class="reportAction status statusCOMPLETED statusSAVED statusFAILED">
						<a href="${pageContext.request.contextPath}/module/reporting/run/runReport.form?copyRequest=${request.uuid}">
							<img src="<c:url value="/images/play.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
							<spring:message code="reporting.reportHistory.runAgain"/>
						</a>
					</div>

					<div class="reportAction">
						<a href="javascript:deleteRequest('${request.uuid}');">
							<img src="<c:url value="/images/delete.gif"/>" width="24" height="24" border="0" style="vertical-align:middle"/>
							<spring:message code="reporting.reportRequest.deleteOrCancel"/>
						</a>
					</div>

				</td>
			</tr>
		</table>

	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>