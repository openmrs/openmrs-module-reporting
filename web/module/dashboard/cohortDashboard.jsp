<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../localHeaderMinimal.jsp"%>

<style type="text/css">
	#page { margin: 0px; } 
	#cohortHeader { padding-left: 5px; background-color: #003366; height: 2em; border-bottom: 1px solid black;
	vertical-align:middle; width: 100%; 
	line-height:2em; font-size: 1.5em; font-weight: bold; color: white; } 
	#cohortFilterColumn { width: 25%; float: left; height: 90%; padding-left: 5px; padding-right: 5px; }
	#cohortResultsColumn { width: 75%; float: right; height: 90%; text-align: left; margin: 0px; padding-left: 5px; padding-top: 5px; border-left: 1px solid black; } 
	#accordion { width: 100%; } 
	table { width: 100%; } 
	.profileImage { width: 75px; height: 86px; }
	#cohort-details-table_wrapper { width: 75%; } 
	#cohort-details-table { border: 0px; } 
</style>
<script type="text/javascript">
$(function() {

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

	var api = new jGCharts.Api(); 
	$('<img>').attr('src', api.make({
			data : [[0], [${children.size}], [${adults.size}]],  
			axis_labels : ['Infants','Children','Adults'], 
			type : 'p'//default bvg 
	})).appendTo("#summary");

	/*
	$('<img>').attr('src', api.make({
		data : [[${females.size}],[${males.size}]],  
		axis_labels : ['Females','Males'], 
		type : 'p'//default bvg 
	})).appendTo("#summary");	
	*/
	
	/*
	$('<img>').attr('src', api.make({
			data : [	
				<c:forEach var="entry" items="${programCohortMap}" varStatus="varstatus">
					[${entry.value.size}]<c:if test="${!varstatus.last}">,</c:if>				                                    				
				</c:forEach>
			],  
			axis_labels : [	
			   	<c:forEach var="entry" items="${programCohortMap}" varStatus="varstatus">
					'${entry.key.name}'<c:if test="${!varstatus.last}">,</c:if>	
				</c:forEach>
			],				
			size : '350x225',
			type : 'p'//default bvg 
	})).appendTo("#summary");
	*/
	$('#cohort-details-table').dataTable(
			 {
				"iDisplayLength": 5,				 
				"bPaginate": true,
				"bLengthChange": false,
				"bFilter": true,
				"bSort": false,
				"bInfo": true,
				"bAutoWidth": true
				} 
	);

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
	<span>Cohort Analysis Dashboard</span>
</div>
<div id="page" style="display:block;">
	<div id="container">
		
	
		<div id="portal">


			<div id="cohortFilterColumn">	
				<div style="height: 65%; overflow: auto;">
					<h3><a href="#">Demographic</a></h3>
					<div>
						<p>
						
							<input type="checkbox"/>Infant (0 - 2 yrs)<br/>
							<input type="checkbox"/>Child (2 - 15 yrs)<br/>				
							<input type="checkbox"/>Adult (15+ yrs)<br/><br/>
							<input type="checkbox"/>Male<br/>
							<input type="checkbox"/>Female	<br/>						
					</div>							
					<h3><a href="#">Program Enrollment</a></h3>
					<div>
						<p>
							<c:forEach var="program" items="${programs}">
								<input type="checkbox"/> ${program.name}<br/>							
							</c:forEach>						
						</p>
					</div>
					<h3><a href="#">Health Center</a></h3>
					<div>
						<p>
							<c:forEach var="location" items="${locations}">
								<input type="checkbox"/> ${location.name}<br/>
							
							</c:forEach>						
						
						
						</p>
						
					</div>
				</div>
				<div style="border-top: 1px solid black; height: 25%;" >				
					<h3><a href="#">Summary</a></h3>
					<span>
						There are <strong>${cohort.size}</strong> patients in the current cohort.
					</span>
					<div id="summary"></div>
				
				</div>
				
			</div><!-- column -->


		
			<div id="cohortResultsColumn">	
			
				<div style="overflow: auto" align="center">
					<table id="cohort-details-table" class="display" width="100%">
						<thead>
							<tr>
								<th>
									<div id="filters" style="margin: 2px; padding:2px;">
										<span style="color: black;">Filters Applied:</span>
										<span style="border: 1px solid #ccc; margin-left: 5px; padding-left: 5px; background-color: #DDECF7; ">
											${selected} <img src="${pageContext.request.contextPath}/images/delete.gif" align="absmiddle"/>
										</span>										 										
									</div>																	
								</th>
							</tr>
						</thead>
						<tbody>										
							<c:forEach var="patient" items="${patients}" varStatus="status">
								<tr height="25px">
									<td>
										<table width="100%" cellspacing="0" cellpadding="0">
											<tr>
												<td rowspan="2" "width="5%" align="center">
													<img class="profileImage" src="<c:url value='/images/patient_${patient.gender}.gif'/>"/>
												</td>
												<td width="90%" valign="top" align="left">
													<strong>${patient.givenName} ${patient.familyName}</strong><br/>
													Age:
													<strong>
														<c:choose>
															<c:when test="${patient.birthdate!=null}">
																<rpt:timespan then="${patient.birthdate}"/>
															</c:when>
															<c:otherwise>unknown</c:otherwise>
														</c:choose>
													</strong>
													<br/>
												</td>																
											</tr>
											<tr>
												<td></td>
												<td align="right">
													<a href="#">details</a></td>
											</tr>
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
