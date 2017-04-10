<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportProcessors.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		
		$j("#report-processor-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

		<c:forEach items="${reportProcessorConfigurations}" var="config" varStatus="configStatus">
			$j('#${config.uuid}EditLink').click(function(event){
				showReportingDialog({
					title: 'Edit Report Processor',
					url: '<c:url value="/module/reporting/viewPortlet.htm?id=reportProcessorPortlet&url=reportProcessorForm&parameters.processorUuid=${config.uuid}"/>',
					successCallback: function() { window.location.reload(true); }
				});
			});
			$j('#${config.uuid}RemoveLink').click(function(event){
				if (confirm('Please confirm you wish to permanantly delete ${config.name}')) {
					document.location.href='${pageContext.request.contextPath}/module/reporting/reports/deleteReportProcessor.form?uuid=${config.uuid}';
				}
			});
		</c:forEach>
		$j('#addLink').click(function(event){
			showReportingDialog({
				title: 'Add New Report Processor',
				url: '<c:url value="/module/reporting/viewPortlet.htm?id=reportProcessorPortlet&url=reportProcessorForm&parameters="/>',
				successCallback: function() { window.location.reload(true); }
			});
		});
	});
</script>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.manageReportProcessors.title"/></h1>
		
		<input id="addLink" href="#"" type="button" value="<spring:message code="reporting.manage.createNew"/>"/>
		<br/>

		<table id="report-processor-table" class="display" style="width:99%;">
			<thead>
				<tr>
					<th><spring:message code="reporting.name"/></th>
					<th><spring:message code="reporting.reportProcessor.type"/></th>
					<th><spring:message code="reporting.reportProcessor.reportDesign"/></th>
					<th><spring:message code="reporting.reportProcessor.runOnSuccess"/></th>
					<th><spring:message code="reporting.reportProcessor.runOnError"/></th>
					<th><spring:message code="reporting.reportProcessor.processorMode"/></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportProcessorConfigurations}" var="config" varStatus="configStatus">
					<tr>
						<td nowrap="true">
							<a id="${config.uuid}EditLink" href="#">${config.name}</a>
						</td>
						<td>${config.processorType}</td>
						<td>${config.reportDesign}</td>
						<td>${config.runOnSuccess}</td>
						<td>${config.runOnError}</td>
						<td>${config.processorMode}</td>
						<td align="center"><a id="${config.uuid}RemoveLink" href="#">
							<img src='<c:url value="/images/trash.gif"/>' border="0"/>
						</a></td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
			</tfoot>
		</table>
	
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>