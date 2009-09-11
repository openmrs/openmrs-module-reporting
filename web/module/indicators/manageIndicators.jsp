<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/manageIndicators.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('.reporting-data-table').dataTable( {
		"bPaginate": true,
		"iDisplayLength": 25,
		"bLengthChange": false,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );			

	$("#period-indicator-form").click(function(event){ 
		showReportingDialog({ 
			title: 'Add Period Indicator',
			url: '<c:url value="/module/reporting/indicators/periodIndicator.form"/>',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});
	
	$("#cohort-indicator-wizard-form").click(function(event){ 
		showReportingDialog({ 
			title: 'Add Cohort Indicator',
			url: '<c:url value="/module/reporting/indicators/indicatorWizard.form"/>',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});


} );
</script>

<div id="page">
	<div id="container">
		<h1>Indicator Manager</h1>

		<spring:message code="reporting.manage.createNew"/>:
		<input type="button" id="period-indicator-form" value="Period Indicator"/>
		<!-- <input type="button" onClick="window.location = 'editIndicator.form';" value="Custom Indicator (Advanced)"/>  -->

		<table class="reporting-data-table display" >
			<thead>
				<tr>
					<th>Indicator</th>
					<th>Cohort Definition</th>
					<th>Parameters</th>
					<th>Author</th>
					<th>Created</th>
					<th>Edit</th>
					<th>Run</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${indicators}" var="indicator" varStatus="status">
				
					<c:set var="editUrl">
						<c:choose>
							<c:when test="${indicator.class.simpleName == 'PeriodCohortIndicator'}">
								${pageContext.request.contextPath}/module/reporting/indicators/periodIndicator.form?uuid=${indicator.uuid}
							</c:when>
							<c:when test="${indicator.class.simpleName == 'CohortIndicator'}">
								${pageContext.request.contextPath}/module/reporting/indicators/cohortIndicator.form?uuid=${indicator.uuid}
							</c:when>
							<c:otherwise>
								${pageContext.request.contextPath}/module/reporting/indicators/editIndicator.form?uuid=${indicator.uuid}
							</c:otherwise>							
						</c:choose>
					</c:set>						
					<script>
					$(document).ready(function() {
						$("#period-indicator-form-${indicator.uuid}").click(function(event){ 
							showReportingDialog({ 
								title: 'Edit Indicator',
								url: '${editUrl}',
								successCallback: function() { 
									window.location.reload(true);
								} 
							});
						});
					});
					</script>
					<script>					
					$(document).ready(function() {
						$("#preview-indicator-${indicator.uuid}").click(function(event){ 
							showReportingDialog({ 
								title: 'Preview Indicator', 
								url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${indicator.uuid}&type=${indicator.class.name}',
								successCallback: function() { 
									window.location = window.location; //.reload(true);
								} 
							});
						});
					} );
					</script>					
				
				
					<tr>
						<td width="30%">
							<a href="javascript:void(0)" id="period-indicator-form-${indicator.uuid}">
								${indicator.name}
							</a>
						</td>					
						<td width="30%">
							${indicator.cohortDefinition.parameterizable.name}<br/>					
						</td>
						<td width="20%">
							<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="status">
								${parameter.name}<c:if test="${!status.last}">,</c:if>
							</c:forEach>
						</td>
						<td nowrap width="5%">
							${indicator.creator}
						</td>
						<td nowrap>
							<rpt:timespan then="${indicator.dateCreated}"/>
						</td>
						<td align="center" width="1%">							
							<a href="javascript:void(0)" id="period-indicator-form-${indicator.uuid}">
								<img src="<c:url value='/images/edit.gif'/>" border="0"/>
							</a>
						</td>
						<td align="center">
							<a href="javascript:void(0)" id="preview-indicator-${indicator.uuid}">
								<img src="<c:url value='/images/play.gif'/>" border="0"/>
							</a>
						</td>					
						<td align="center" width="1%">							
							<a href="${pageContext.request.contextPath}/module/reporting/indicators/purgeIndicator.form?uuid=${indicator.uuid}">
								<img src="<c:url value='/images/trash.gif'/>" border="0"/>
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
				<!--  
				<tr>
					<th colspan="6" align="center" height="50">
						
					</th>			
				</tr>
				-->	
			</tfoot>
		</table>
	</div>
</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>