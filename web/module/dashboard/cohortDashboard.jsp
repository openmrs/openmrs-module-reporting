<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../localHeaderMinimal.jsp"%>

<style type="text/css">
	#page { margin: 0px; } 
	#cohortHeader { padding-left: 5px; background-color: #003366; height: 2em; border-bottom: 1px solid black;
	vertical-align:middle; width: 100%; 
	line-height:2em; font-size: 1.5em; font-weight: bold; color: white; } 
	#cohortFilterColumn { width: 15%; float: left; height: 90%; padding-left: 5px; padding-right: 5px; }
	#cohortResultsColumn { width: 85%; float: right; height: 90%; text-align: left; margin: 0px; padding-left: 5px; padding-top: 5px; border-left: 1px solid black; } 
	#accordion { width: 100%; } 
	table { width: 100%; } 
	.profileImage { width: 75px; height: 86px; }
	#cohort-details-table_wrapper { width: 75%; } 
	#cohort-details-table { border: 0px; } 
</style>
<script type="text/javascript">
$(document).ready(function() {

	$('#cohort-breakdown-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	
	$("#accordion").accordion();
	$('#cohort-tabs').tabs();
	$('#cohort-tabs').show();	
	
	$('#cohort-details-table').dataTable({
				"iDisplayLength": 3,				 
				"bPaginate": true,
				"bLengthChange": false,
				"bFilter": true,
				"bSort": false,
				"bInfo": true,
				"bAutoWidth": true
	});

});
</script>

<!-- 
<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>
 -->
<div id="cohortHeader">
	<table width="100%">
		<tr>
			<td width="33%" valign="middle">
				<span style="color: white; font-size: 1.5em;"><strong>Cohort Analysis Dashboard</strong></span></td>
			<td align="right" valign="middle">
				<span style="color: white;"><strong>(returned ${fn:length(patients)} patients)</strong></span>			
			</td>
		</tr>
	</table>
</div>
<div id="page" style="display:block;">
	<div id="container">
		<div id="portal">
			<div id="cohortFilterColumn">					
				<form action="${pageContext.request.contextPath}/module/reporting/dashboard/manageCohortDashboard.form"
					<div style="height: 95%; overflow: auto;">
						<h3><a href="#">Age</a></h3>
						<div>
						
							<p>
								<!--  org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition:minAge=0:maxAge=2:effectiveDate=today -->
								<input type="checkbox" name="ageCohort" value="infant" <c:if test="${param.ageCohort=='infant'}">checked</c:if>/>Infant (0 - 2 yrs)<br/>
								<input type="checkbox"  name="ageCohort" value="child" <c:if test="${param.ageCohort=='child'}">checked</c:if>/>Child (2 - 15 yrs)<br/>				
								<input type="checkbox" name="ageCohort" value="adult" <c:if test="${param.ageCohort=='adult'}">checked</c:if>/>Adult (15+ yrs)<br/>
							</p>
						</div>		
						<h3><a href="#">Gender</a></h3>
						<div>
							<p>
								<!--  org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition:minAge=0:maxAge=2:effectiveDate=today -->
								<input type="checkbox" name="genderCohort" value="male" <c:if test="${param.genderCohort=='male'}">checked</c:if>/>Male<br/>
								<input type="checkbox" name="genderCohort" value="female" <c:if test="${param.genderCohort=='female'}">checked</c:if>/>Female
							</p>					
						</div>		

						<h3><a href="#">Currently enrolled in ...</a></h3>
						<div>
							<p>
								<c:forEach var="program" items="${programs}">
									<input type="checkbox" name="programCohort" value="${program.name}"/> ${program.name}<br/>							
								</c:forEach>						
							</p>
						</div>
<!--  						
						<h3><a href="#">Health Center</a></h3>
						<div>
							<p>
								<c:forEach var="location" items="${locations}">
									<input type="checkbox" name="locationCohort" value="${location.name}"/> ${location.name}<br/>							
								</c:forEach>						
							</p>						
						</div>
-->						
						
						
					</div>

					<div align="center" valign="middle" style="margin-top: 25px; margin-bottom:25px;" >
						<input type="submit" value="Filter"/>
					</div>

<!-- 
					<div style="border-top: 1px solid black; height: 25%;" >				
						<h3><a href="#">Cohort Summary</a></h3>
						<span>
							There are <strong>${selectedCohort.size}</strong> patients in the current cohort.
						</span>
						<div id="summary"></div>					
					</div>
					
 -->					
				</form>				
			</div><!-- column -->


		
			<div id="cohortResultsColumn">	
			
				<div style="overflow: auto" align="center">
					<table id="cohort-details-table" class="display" width="50%">
						<thead>
							<tr>
								<th>
									<div id="filters" style="margin: 10px; padding:10px;">									
										<span style="color: black;">Filters applied:</span>
										<c:forEach var="sc" items="${selectedCohorts}">
											<span style="border: 1px solid #ccc; margin: 5px; padding: 5px; background-color: #DDECF7; ">
												${sc} 											  
												<img src="${pageContext.request.contextPath}/images/delete.gif" align="absmiddle"/>												
											</span>										
										</c:forEach>
									</div>																	
								</th>
							</tr>
						</thead>
						<tbody>										
							<c:forEach var="patient" items="${patients}" varStatus="status">
							
							
							
					<script>					
					$(document).ready(function() {
						$("#view-patient-dashboard-${patient.patientId}").click(function(event){ 
							showReportingDialog({ 
								title: 'Patient Dashboard', 
								url: "<c:url value='/patientDashboard.form?patientId=${patient.patientId}'/>"
							});
						});
					} );
					</script>									
							
							
							
								<tr height="25px">
									<td>
										<table width="50%" cellspacing="0" cellpadding="0">
											<tr>
												<td rowspan="2" width="5%" align="center">
													<img class="profileImage" src="<c:url value='/images/patient_${patient.gender}.gif'/>"/>
												</td>
												<td width="90%" valign="top" align="left" >
													<div style="border-bottom: 1px solid black;">
														<strong>${patient.givenName} ${patient.familyName}</strong>													
													</div>																										
													<table border="0">
														<tr>
															<td nowrap>HIV Program:</td>
															<td><strong>ON ARVs </strong></td>
														</tr>
														<tr>
															<td width="15%" nowrap>
																Born:
															</td>
															<td>													
																<strong>
																	<c:choose>
																		<c:when test="${patient.birthdate!=null}">
																			<rpt:timespan then="${patient.birthdate}"/>
																		</c:when>
																		<c:otherwise>unknown</c:otherwise>
																	</c:choose>
																</strong>
															</td>
														</tr>
														<tr>
															<td nowrap>Gender:</td>
															<td>
																<strong>															
																	<c:choose>
																		<c:when test="${patient.gender=='M'}">male</c:when>
																		<c:when test="${patient.gender=='F'}">female</c:when>
																		<c:otherwise>Other</c:otherwise>
																	</c:choose>															
																</strong>
															</td>
														</tr>
														<tr>
															<td colspan="2">
																<button id="view-patient-dashboard-${patient.patientId}"><strong>more ...</strong></button>
															</td>
														</tr>
													</table>
												</td>																
											</tr>
											<tr><td colspan="2">&nbsp;</td></tr>
										</table>
									</td>
								</tr>							
							</c:forEach>
						</tbody>
						<tfoot></tfoot>
					</table>
					
					
					
				</div>
			</div><!-- column -->
			
			<br clear="both"/>
			
		
		</div><!-- portal -->
	</div><!-- container -->
</div><!-- page -->


</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
