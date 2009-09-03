<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/manageIndicators.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#indicator-table').dataTable( {
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
<style>
.small { text-decoration: italic; font-size: x-small;  }
</style>


<div id="page">
	<div id="container">
		<h1>Indicator Manager</h1>

		<spring:message code="reporting.manage.createNew"/>:
		<input type="button" id="period-indicator-form" value="Period Indicator"/>
		<!-- <input type="button" onClick="window.location = 'editIndicator.form';" value="Custom Indicator (Advanced)"/>  -->

		<table id="indicator-table" class="display" >
			<thead>
				<tr>
					<th>Indicator</th>
					<th>Cohort Definition</th>
					<th>Parameters</th>
					<th>Author</th>
					<th nowrap>Last modified</th>
					<th>Created</th>
					<th>Edit</th>
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
				
				
					<tr>
						<td width="30%">
							<a href="#" id="period-indicator-form-${indicator.uuid}">
								${indicator.name}
							</a>
						</td>					
						<td width="30%">
							${indicator.cohortDefinition.parameterizable.name}<br/>					
						</td>
						<td width="20%">
							<span nowrap="" class="small">
								<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="status">
									${parameter.name}<c:if test="${!status.last}">,</c:if>
								</c:forEach>
							</span>						
						</td>
						<td width="5%">
							<span class="small">${indicator.creator}</span>
						</td>
						<td width="5%" nowrap>
							<span class="small"><rpt:timespan then="${indicator.dateChanged}"/></span>
						</td>
						<td width="5%">
							<span class="small"><rpt:timespan then="${indicator.dateCreated}"/></span>
						</td>
						<td align="center" width="1%">							
							<a href="#" id="period-indicator-form-${indicator.uuid}">
								<img src="<c:url value='/images/edit.gif'/>" border="0"/>
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