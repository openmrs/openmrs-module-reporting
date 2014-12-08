<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="../run/localHeader.jsp"%>

<style type="text/css">
	#page { margin: 0px; } 
	.column { width: 450px; float: left; padding-bottom: 100px; }
	.portlet { margin: 0 1em 1em 0; }
	.portlet-header { margin: 0.3em; padding-bottom: 4px; padding-left: 0.2em; }
	.portlet-header .ui-icon { float: right; }
	.portlet-content { padding: 0.4em; }
	.ui-sortable-placeholder { border: 1px dotted black; visibility: visible !important; height: 50px !important; }
	.ui-sortable-placeholder * { visibility: hidden; }
</style>
<script type="text/javascript">
$(function() {
	$('#cohort-breakdown-tabs').tabs();
	$('#cohort-breakdown-tabs').show();	

	$(".column").sortable({
		connectWith: '.column'
	});

	$(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
		.find(".portlet-header")
			.addClass("ui-widget-header ui-corner-all")
			.prepend('<span class="ui-icon ui-icon-plusthick"></span>')
			.end()
		.find(".portlet-content");

	$(".portlet-header .ui-icon").click(function() {
		$(this).toggleClass("ui-icon-minusthick");
		$(this).parents(".portlet:first").find(".portlet-content").toggle();
	});

	$(".column").disableSelection();

	$('#report-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	
	$('#gender-breakdown-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

	$('#age-breakdown-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	
	$('#program-breakdown-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

	
	$("#accordion").accordion();
});
</script>


<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<!-- 
<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>
 -->

<div id="page" style="display:block;">
	<div id="container">
	
		<h1><spring:message code="reporting.dashboard.reportingDashboard"/></h1>
	
	
		<div id="portal">


			<div class="column">	
				<div class="portlet">
					<div class="portlet-header"><spring:message code="reporting.dashboard.reports"/></div>
					<div class="portlet-content">							
						<form method="post" action="">	
						
							<c:if test="${empty reportDefinitions}">
								<spring:message code="reporting.dashboard.reportingDashboard.none"/>
							</c:if>	
							<table id="report-table" class="display">
								<thead>
									<tr>
										<th><spring:message code="reporting.dashboard.reportName"/></th>
										<th><spring:message code="reporting.dashboard.created"/></th>
									</tr>
								</thead>
								<tbody>					
									<c:forEach var="reportDefinition" items="${reportDefinitions}" varStatus="varStatus">								
										<c:if test="${varStatus.index < 5}">
											<tr>
												<td>
													<spring:message code="reporting.dashboard.reportDefinition.name"/><br/>
												</td>											
												<td nowrap>
													<rpt:timespan then="${reportDefinition.dateCreated}"/>
												</td>
											</tr>						
										</c:if>		
									</c:forEach>						
									<tr>
										<td>
											<c:if test="${fn:length(reportDefinitions) > 5}">
												<a href="<c:url value='/module/reporting/reports/manageReports.list'/>">more ...</a>
											</c:if>
										</td>
										<td></td>
									</tr>																								
								</tbody>
							</table>
						</form>
					</div><!-- portlet-content -->
				</div><!-- portlet -->
			</div><!-- column -->


		
			<div class="column">	
	
				<!--  Cohort Breakdown Portlet -->		
				<div class="portlet">
					<div class="portlet-header"><spring:message code="reporting.dashboard.cohortBreakdown"/></div>
					<div class="portlet-content">
					
						<span>
							<spring:message code="reporting.dashboard.patientsInEMR"/>
						</span>
					
						<div id="cohort-breakdown-tabs" class="ui-tabs-hide">			
							<ul>
				                <li><a href="#cohort-gender-breakdown-tab"><span><spring:message code="reporting.dashboardProperty.gender"/></span></a></li>
				                <li><a href="#cohort-age-breakdown-tab"><span><spring:message code="reporting.dashboardProperty.age"/></span></a></li>
				                <li><a href="#cohort-program-breakdown-tab"><span><spring:message code="reporting.dashboard.program"/></span></a></li>
				            </ul>
						
							<div id="cohort-gender-breakdown-tab">						
								<div align="center">
									<div id="genderBarChart"></div>
									<span><em><spring:message code="reporting.dashboard.genderBreakdown"/></em></span>
									<table id="gender-breakdown-table" class="display">
										<thead>
											<tr>
												<th width="80%">reporting.dashboardProperty.gender</th>
												<th>#</th>
												<th>%</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><spring:message code="reporting.dashboardProperty.gender.male"/></td>
												<td><a href="${pageContext.request.contextPath}/module/reporting/dashboard/manageCohortDashboard.form?cohort=males">${males.size}</a></td>
												<td>
													<fmt:formatNumber type="percent" maxFractionDigits="2" value="${males.size / all.size}"/>													
												</td>
											</tr>
											<tr>
												<td><spring:message code="reporting.dashboardProperty.gender.female"/></td>
												<td><a href="${pageContext.request.contextPath}/module/reporting/dashboard/manageCohortDashboard.form?cohort=females">${females.size}</a></td>
												<td>
													<fmt:formatNumber type="percent" maxFractionDigits="2" value="${females.size / all.size}"/>													
												</td>
											</tr>
										</tbody>
										<tfoot>
											<tr>
												<th><spring:message code="reporting.dashboard.total"/></th>													
												<th><spring:message code="reporting.dashboard.totalGender"/></th>
												<th>
													<fmt:formatNumber type="percent" maxFractionDigits="2" value="${(males.size + females.size) / all.size}"/>													
												</th>
											</tr>
										</tfoot>
									</table>	
								</div>
							</div>
							
							<div id="cohort-age-breakdown-tab">		
							
								<div align="center">				
									<div id="ageBarChart"></div>
									<span><em>Age breakdown</em></span>
	
									<table id="age-breakdown-table" class="display">
										<thead>
											<tr>
												<th width="80%"><spring:message code="reporting.dashboardProperty.age"/></th>
												<th>#</th>
												<th>%</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td><spring:message code="reporting.dashboardProperty.age.adult"/></td>
												<td><a href="${pageContext.request.contextPath}/module/reporting/dashboard/manageCohortDashboard.form?cohort=adults">${adults.size}</a></td>
												<td>
													<fmt:formatNumber type="percent" maxFractionDigits="2" value="${adults.size / all.size}"/>													
												</td>
											</tr>
											<tr>
												<td><spring:message code="reporting.dashboardProperty.age.child"/></td>
												<td><a href="${pageContext.request.contextPath}/module/reporting/dashboard/manageCohortDashboard.form?cohort=children">${children.size}</a></td>
												<td>
													<fmt:formatNumber type="percent" maxFractionDigits="2" value="${children.size / all.size}"/>
												</td>
											</tr>	
										</tbody>
										<tfoot>
											<tr>
												<th><spring:message code="reporting.dashboard.total"/></th>										
												<th><spring:message code="reporting.dashboard.totalAge"/></th>
												<th>
													<fmt:formatNumber type="percent" maxFractionDigits="2" value="${(children.size + adults.size) / all.size}"/>
												</th>
											</tr>
										</tfoot>
												
									</table>	
								</div>	
							</div>
							
							
							<div id="cohort-program-breakdown-tab">	
								<div align="center">	
									<div id="programBarChart"></div>
									<span><em><spring:message code="reporting.dashboard.programBreakdown"/></em></span>
									<table id="program-breakdown-table" class="display">
										<thead>
											<tr>
												<th width="80%"><spring:message code="reporting.dashboard.program"/></th>
												<th>#</th>
												<th>%</th>
											</tr>
										</thead>
										<tbody>																							
											<c:forEach var="entry" items="${programCohortMap}">
												<tr>
													<td><spring:message code="reporting.dashboard.entry.key.name"/></td>
													<td><a href="${pageContext.request.contextPath}/module/reporting/dashboard/manageCohortDashboard.form?cohort=${entry.key.name}">${entry.value.size}</a></td>
													<td>
														<fmt:formatNumber type="percent" maxFractionDigits="2" value="${entry.value.size / all.size}"/>
													</td>														
												</tr>
											</c:forEach>
										</tbody>
										
										<tfoot>
										
										</tfoot>
									</table>																						
								</div>										
							</div><!-- cohort-program-breakdown-tab -->
						</div><!-- cohort-breakdown-tab -->
					</div>
				</div>
			</div><!-- column -->
		</div>
			
			<div class="column">	


				<!--  Data Set Viewer -->		
				<div class="portlet">
					<div class="portlet-header">Data Set Viewer</div>
					<div class="portlet-content">			
						<span>
							<spring:message code="reporting.dashboard.downloadData"/>
						</span>
						
						<div align="center">
							
							<form method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/downloadDataSet.form">					
								<ul>				
									<li>
										<div>						
											<select name='cohortUuid' disabled>
												<option value=""><spring:message code="reporting.dashboard.allPatients"/></option>
											</select>
										</div>
									</li>														
									<li>
										<div>
											<select id='dataset-definition-uuid' name='uuid'>
												<option value=""><spring:message code="reporting.dashboard.chooseDataset"/>t</option>
												<c:forEach var='datasetDefinition' items='${datasetDefinitions}'>
													<option value="${datasetDefinition.uuid}">${datasetDefinition.name}</option>
												</c:forEach>												
											</select>
										</div>
									</li>							
									<li>
										<div>						
											<input type="radio" name="renderType" value="XLS" checked>XLS 
											<input type="radio" name="renderType" value="CSV">CSV 
											<input type="radio" name="renderType" value="TSV">TSV 
										</div>
									</li>							
									<li>
										<div>						
											<input type="submit" value="Download"/>
										</div>
									</li>
								</ul>			
							</form>							
						</div>
					</div><!-- portlet-content -->
				</div><!-- portlet -->
			
				<div class="portlet">
					<div class="portlet-header"><spring:message code="reporting.dashboard.labReportViewer"/></div>
					<div class="portlet-content">			
						<span>
							<spring:message code="reporting.dashboard.downloadResult"/>
						</span>
			
						<div align="left" style="padding: 10px; margin-left:100px">					
							<form method="post" action="${pageContext.request.contextPath}/module/reporting/renderLabReport.form">		
								<input type="hidden" id="lab-report-cohort-uuid" name="uuid" value="0123456789"/>									
								<input type="hidden" name="action" value="render"/>
								<div>
										<label class="desc" for="renderType">Download as:</label>
										<span>
											<input type="radio" name="renderType" value="XLS" checked tabindex="1"> XLS
											<input type="radio" name="renderType" value="CSV" tabindex="2"> CSV
										</span>
								</div>
								<div>
										<label class="desc" for="locationId">Location</label>
										<span>
											<select name="locationId"  tabindex="5">
												<option value="0">All Locations</option>									
												<c:forEach var="location" items="${locations}">
													<option value="${location.locationId}">${location.name}</option>
												</c:forEach>
											</select>		
										</span>
								</div>
								<div>
										<label class="desc" for="startDate">Start Date</label>
										<span>
											<openmrs:fieldGen type="java.util.Date" formFieldName="startDate" val="" parameters=""/>
										</span>
								</div>
								<div>
										<label class="desc" for="endDate">End Date</label>
										<span>
											<openmrs:fieldGen type="java.util.Date" formFieldName="endDate" val="" parameters=""/>							
										</span>
								</div>
								<div class="buttons">
									<span>
										<input id="save-button" class="btTxt submit" type="submit" value="Download" tabindex="6" />										
									</span>
								</div>
			
							</form>
						</div>			
					</div><!-- portlet-content -->
				</div><!-- portlet -->
			
			
			
			</div><!-- column -->
		
		</div><!-- portal -->
	</div><!-- container -->
	
	
	<div class="clear"></div>
	
	
</div><!-- page -->



<%@ include file="/WEB-INF/template/footer.jsp"%>
