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
			title: 'Add a new period indicator',
			url: '<c:url value="/module/reporting/indicators/periodIndicator.form"/>',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});

	$("#snapshot-indicator-form").click(function(event){ 
		showReportingDialog({ 
			title: 'Add a new snapshot indicator',
			url: '<c:url value="/module/reporting/indicators/snapshotIndicator.form"/>',
			successCallback: function() { 
				window.location.reload(true);
			} 
		});
	});

	
	$("#cohort-indicator-wizard-form").click(function(event){ 
		showReportingDialog({ 
			title: 'Add cohort indicator',
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
		<!-- 
			<spring:message code="reporting.manage.createNew"/>:
		 -->
		<!--  
			<input type="button" id="period-indicator-form" value="Period Indicator"/>
		 -->
		 <!-- <input type="button" onClick="window.location = 'editIndicator.form';" value="Custom Indicator (Advanced)"/>  -->		
		<c:url var="snapshotIndicatorImage" value="/moduleResources/reporting/images/indicator-interval-snapshot.png"/>
		<c:url var="periodIndicatorImage" value="/moduleResources/reporting/images/indicator-interval-period.png"/>
		
		
		<div align="left">
			<span style="font-size:1.5em; font-weight: bold;">Actions:</span> &nbsp; 
		 	<span id="period-indicator-form">
				<img src="${periodIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>
				<a href="javascript:void(0);">Create period indicator</a> 
			</span>
			<span> &nbsp; | &nbsp; </span>
		 	<span id="snapshot-indicator-form">
			 	<img src="${snapshotIndicatorImage}" width="24" height="24" border="0" alt="snapshot indicator" style="vertical-align:middle"/> 
			 	<a href="javascript:void(0);"></a> Create snapshot indicator 
			</span>
		</div>


		<table class="reporting-data-table display" >
			<thead>
				<tr>
					<th nowrap>Name</th>
					<!-- 
					<th>Cohort Definition</th>
					<th>Parameters</th>
					 -->
					<th nowrap>Created by</th>
					<th nowrap></th>
					<th nowrap>Actions</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${indicators}" var="indicator" varStatus="status">
				
					<c:set var="editUrl">
						<c:choose>
							<c:when test="${indicator.class.simpleName == 'CohortIndicator'}">
								${pageContext.request.contextPath}/module/reporting/indicators/periodIndicator.form?uuid=${indicator.uuid}
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
						<td width="65%">
							<a href="javascript:void(0)" id="period-indicator-form-${indicator.uuid}">
								${indicator.name}
							</a>
						</td>	
<%-- 						
										
						<td width="30%">
							${indicator.cohortDefinition.parameterizable.name}<br/>					
						</td>						
						<td width="20%">
							<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="status">
								${parameter.name}<c:if test="${!status.last}">,</c:if>
							</c:forEach>
						</td>
--%>
						<td nowrap width="10%" align="center">
							${indicator.creator}
						</td>
						<td nowrap width="10%" align="center">
							<rpt:timespan then="${indicator.dateCreated}"/>
						</td>
						<td align="center" width="5%">
							<span width="33%" align="left">
								<a href="javascript:void(0)" id="period-indicator-form-${indicator.uuid}">
									<img src="<c:url value='/images/edit.gif'/>" border="0"/>
								</a>
							</span>
							<span width="33%" align="center">
								<a href="javascript:void(0)" id="preview-indicator-${indicator.uuid}">
									<img src="<c:url value='/images/play.gif'/>" border="0"/>
								</a>
							</span>
							<span width="33%" align="right">
								<a href="${pageContext.request.contextPath}/module/reporting/indicators/purgeIndicator.form?uuid=${indicator.uuid}">
									<img src="<c:url value='/images/trash.gif'/>" border="0"/>
								</a>
							</span>
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