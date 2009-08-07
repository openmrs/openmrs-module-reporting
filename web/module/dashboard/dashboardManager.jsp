<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../localHeader.jsp"%>

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

	$('#cohort-breakdown-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	
	$(function() {
		$("#accordion").accordion();
	});


	var api = new jGCharts.Api(); 
	$('<img>') 
	.attr('src', api.make({
			data : [[153, 60, 52], [113, 70, 60], [120, 80, 40], [120, 80, 40]],  
			type : 'p'//default bvg 
	})) 
	.appendTo("#barChart");
	


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
	
		<h1>Reporting Dashboard</h1>
	
	
		<div id="portal">


			<div class="column">	
				<div class="portlet">
					<div class="portlet-header">Program Enrollment Search</div>
					<div class="portlet-content">							
						<form method="post" action="">					
							<ul>				
								<li>
									<label class="desc" for="programId">Show patients that are enrolled in program(s) </label>
									<div style="overflow: auto; height: 80px">			
										<c:forEach var="program" items='${programs}'>
											<input type="checkbox" name="programId" value="${program.programId}"/>${program.name} <br/>
										</c:forEach>
									</div>
								</li>				
								<li>
									<label class="desc" for="programId">newly enrolled on/after </label>
									<div>			
										<openmrs:fieldGen type="java.util.Date" formFieldName="startDate" val="" parameters=""/>
									</div>
								</li>	
								<li>
									<label class="desc" for="programId">and on/before </label>
									<div>			
										<openmrs:fieldGen type="java.util.Date" formFieldName="endDate" val="" parameters=""/>
									</div>
								</li>													
								<li>
									<div>			
										<input type="submit" value="Search"/>
									</div>
								</li>									
																				
							</ul>			
						</form>
					
					
					</div><!-- portlet-content -->
				</div><!-- portlet -->
			</div><!-- column -->


		
			<div class="column">	
	
				<!--  Cohort Breakdown Portlet -->		
				<div class="portlet">
					<div class="portlet-header">Cohort Breakdown</div>
					<div class="portlet-content">
					
						<span>
							There are <strong><a href="${pageContext.request.contextPath}/module/reporting/manageCohortDashboard?cohort=all">${all.size}</a></strong> patients in the EMR.
						</span>
						
						<div align="center">
							<form method="post" action="">					
								<ul>				
									<li>
										<div id="barChart"></div>
										<span><em>This is just a sample graph -- there's no data behind it yet</em></span>
									</li>
									<li>
										
									
										<table id="cohort-breakdown-table" class="display">
											<thead>
												<tr>
													<th>Category</th>
													<th># of Patients</th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<td>Male</td>
													<td><a href="${pageContext.request.contextPath}/module/reporting/manageCohortDashboard.form?cohort=males">${males.size}</a></td>
												</tr>
												<tr>
													<td>Female</td>
													<td><a href="${pageContext.request.contextPath}/module/reporting/manageCohortDashboard.form?cohort=females">${females.size}</a></td>
												</tr>
												<tr>
													<td>Adult</td>
													<td><a href="${pageContext.request.contextPath}/module/reporting/manageCohortDashboard.form?cohort=adults">${adults.size}</a></td>
												</tr>
												<tr>
													<td>Child</td>
													<td><a href="${pageContext.request.contextPath}/module/reporting/manageCohortDashboard.form?cohort=children">${children.size}</a></td>
												</tr>
											</tbody>
										</table>																						
									
										<!-- 
										<table id="cohort-breakdown-table" class="display">
											<thead>
												<tr>
													<th></th>
													<th>Child</th>
													<th>Adult</th>						
													<th>Total</th>						
												</tr>
											</thead>
											<tbody>
												<tr>
													<td>Male</td>					
													<td></td>					
													<td></td>					
													<td>${males.size}</td>					
												</tr>
												<tr>
													<td>Female</td>					
													<td></td>					
													<td></td>					
													<td>${females.size}</td>					
												</tr>
											</tbody>
											<tfoot>
												<tr>
													<th>Total</th>					
													<th>${children.size}</th>					
													<th>${adults.size}</th>					
													<th>${all.size}</th>					
												</tr>											
											</tfoot>
										</table>
										 -->
									</li>
								</ul>
							</form>
						</div>
					</div>
				</div>							
		


<%--  
				<div class="portlet">
					<div class="portlet-header">Simple Logic Query</div>
					<div class="portlet-content">			
						<span>
							Execute a custom logic query.
						</span>
						
						<div>
							
							<form method="post" action="">					
								<ul>				
									<li>
									
										<label class="desc" for="query">Query</label>
										<div>																
											<textarea name="query" class="field textarea small"></textarea>
										</div>
									</li>														
									<li>
										<div>						
											<input type="submit" value="Execute"/>
										</div>
									</li>
								</ul>			
							</form>							
						</div>
					</div><!-- portlet-content -->
				</div><!-- portlet -->
--%>				
			</div><!-- column -->
			
			
			<div class="column">	


				<!--  Data Set Viewer -->		
				<div class="portlet">
					<div class="portlet-header">Data Set Viewer</div>
					<div class="portlet-content">			
						<span>
							Download a data snapshot for an existing dataset and cohort.
						</span>
						
						<div align="center">
							
							<form method="post" action="${pageContext.request.contextPath}/module/reporting/downloadDataset.form">					
								<ul>				
									<li>
										<div>						
											<select name='cohortUuid' disabled>
												<option value="">All patients</option>
											</select>
										</div>
									</li>														
									<li>
										<div>
											<select class="field select medium" id='uuid' name='uuid'>
												<option value="">Choose a dataset</option>
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
					<div class="portlet-header">Lab Report Viewer</div>
					<div class="portlet-content">			
						<span>
							Download the lab result report for a given period and location.
						</span>
			
						<div align="left" style="padding: 10px; margin-left:100px">					
							<form method="post" action="${pageContext.request.contextPath}/module/reporting/renderLabReport.form">		
								<input type="hidden" id="uuid" name="uuid" value="0123456789"/>									
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
</div><!-- page -->

</div>
</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
