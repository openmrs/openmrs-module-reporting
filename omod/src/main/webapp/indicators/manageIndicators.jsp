<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/indicators/manageIndicators.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		$j('.reporting-data-table').dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false,
			"aoColumns": [{ "bSortable": false },
			              { "bSortable": true },
			      		  { "bSortable": true },
			              { "bSortable": false }]
		} );
        $j("#indicatorNameColumn").click();
	} );
	
	function confirmDelete(uuid) {
		if (confirm("Are you sure you want to delete it?")) {
			document.location.href = '${pageContext.request.contextPath}/module/reporting/indicators/purgeIndicator.form?uuid=' + uuid;
		}
	}
</script>

<style>
	#main { 
		padding-bottom: 100px;
	}
</style>


<div id="page">

	<div id="container">
		
		<h1>Indicator Manager</h1>

		<div id="navigation">
			<c:url var="cohortIndicatorImage" value="/moduleResources/reporting/images/indicator-interval-period.png"/>
			<c:url var="sqlIndicatorImage" value="/moduleResources/reporting/images/db_add.png"/>	
		 	<span>
				<img src="${cohortIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>				
				<a href="<c:url value="/module/reporting/indicators/editCohortIndicator.form"/>"><spring:message code="reporting.createCohortIndicator" /></a>
			</span>
			&nbsp;&nbsp;
		 	<span>
				<img src="${sqlIndicatorImage}" width="24" height="24" border="0" alt="sql indicator" style="vertical-align:middle"/>				
				<a href="<c:url value="/module/reporting/indicators/editSqlIndicator.form"/>"><spring:message code="reporting.createSqlIndicator" /></a>
			</span>
		</div>

		<div id="main">		
			<table id="table-indicator-list" class="reporting-data-table display">
				<thead>
					<tr>
						<th nowrap><spring:message code="reporting.actions" /></th>
						<th id="indicatorNameColumn" nowrap><spring:message code="reporting.name" /></th>
						<th nowrap><spring:message code="reporting.createdBy" /></th>
						<th nowrap><spring:message code="reporting.lastModified" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${indicators}" var="indicator" varStatus="status">					
						<c:set var="editUrl">
							<c:choose>
								<c:when test="${indicator['class'].simpleName == 'CohortIndicator'}">
									${pageContext.request.contextPath}/module/reporting/indicators/editCohortIndicator.form?uuid=${indicator.uuid}
								</c:when>
								<c:otherwise>
									${pageContext.request.contextPath}/module/reporting/indicators/editSqlIndicator.form?uuid=${indicator.uuid}
								</c:otherwise>							
							</c:choose>
						</c:set>
						<script>					
							$j(document).ready(function() {
								$j("#preview-indicator-${indicator.uuid}").click(function(event){
									showReportingDialog({ 
										title: 'Preview Indicator', 
										url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${indicator.uuid}&type=${indicator['class'].name}',
										successCallback: function() { 
											window.location = window.location; //.reload(true);
										} 
									});
								});
							} );
						</script>					
						<tr>
							<td align="center" width="5%" style="white-space:nowrap;">
								<span style="padding-left: 10px;">
									<a href="${editUrl}">
										<img src="<c:url value='/images/edit.gif'/>" border="0" style="vertical-align:middle;"/>
									</a>
								</span>
								<span style="padding-left: 10px;">
									<a href="javascript:confirmDelete('${indicator.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0" style="vertical-align:middle;" /></a>
								</span>
								<span style="padding-left: 10px;">
									<a href="javascript:void(0)" id="preview-indicator-${indicator.uuid}">
										<img src="<c:url value='/images/play.gif'/>" border="0" style="vertical-align:middle;"/>
									</a>
								</span>
							</td>
							<td width="65%">
                                <span style="display:none"><c:out value="${indicator.name}"/></span>
								<c:url var="simpleIndicatorImage" value="/moduleResources/reporting/images/indicator-type-simple.png"/>
								<c:url var="fractionIndicatorImage" value="/moduleResources/reporting/images/indicator-type-fraction.png"/>
								<c:url var="customIndicatorImage" value="/moduleResources/reporting/images/indicator-type-custom.png"/>
								<c:url var="sqlIndicatorImage" value="/moduleResources/reporting/images/db.png"/>
								<c:choose>
									<c:when test="${indicator['class'].simpleName == 'CohortIndicator'}">
										<c:choose>						
											<c:when test="${indicator.type=='FRACTION'}">
												<img src="${fractionIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>										
											</c:when>
											<c:when test="${indicator.type=='LOGIC'}">
												<img src="${customIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>																				
											</c:when>
											<c:otherwise>
												<img src="${simpleIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>			
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:when test="${indicator['class'].simpleName == 'SqlIndicator'}">
										<img src="${sqlIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>										
									</c:when>
									<c:otherwise>
										<img src="${simpleIndicatorImage}" width="24" height="24" border="0" alt="period indicator" style="vertical-align:middle"/>												
									</c:otherwise>
								</c:choose>						
								<a href="${editUrl}"><c:out value="${indicator.name}"/></a>
							</td>	
							<td nowrap width="10%" align="center">
								${indicator.creator}
							</td>
							<td nowrap width="10%" align="center">
								<rpt:timespan then="${indicator.dateCreated}"/>
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
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>
