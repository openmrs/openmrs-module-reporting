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

<div id="page">
	<div id="container">
	
		<h1>Reporting Dashboard</h1>
	
	
		<div id="portal">
		
			<div class="column">	
	
				<!--  Cohort Breakdown Portlet -->		
				<div class="portlet">
					<div class="portlet-header">Cohort Breakdown</div>
					<div class="portlet-content">
					
						<span>
							Display a cohort of patients broken down across age and gender dimensions.
						</span>
						
						<div align="center">
							<form method="post" action="">					
								<ul>				
									<li>
										<div>						
											<select>
												<option value="">Choose a group of patients</option>
											</select>
											<input type="submit" value="Update"/>
										</div>
									</li>
									<li>
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
													<td>102</td>					
													<td>932</td>					
													<td>1034</td>					
												</tr>
												<tr>
													<td>Female</td>					
													<td>231</td>					
													<td>1048</td>					
													<td>1279</td>					
												</tr>
											</tbody>
											<tfoot>
												<tr>
													<th>Total</th>					
													<th>333</th>					
													<th>1980</th>					
													<th>2313</th>					
												</tr>											
											</tfoot>
										</table>
									</li>
								</ul>
							</form>
						</div>
					</div>
				</div>							
		
				<!--  Data Set Viewer -->		
				<div class="portlet">
					<div class="portlet-header">Data Set Viewer</div>
					<div class="portlet-content">			
						<span>
							Download a data snapshot for an existing dataset and cohort.
						</span>
						
						<div align="center">
							
							<form method="post" action="module/reporting/downloadDataset.form">					
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
											<select>
												<option value="">Choose a dataset</option>
												<c:forEach var='datasetDefinition' items='datasetDefinitions'>
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

				
			</div><!-- column -->
			
		
			<div class="column">	
				<div class="portlet">
					<div class="portlet-header">Program Enrollment</div>
					<div class="portlet-content">							



<div id="accordion">
	<h3><a href="#">New Enrollments</a></h3>
	<div>
		<form method="post" action="">					
			<ul>				
				<li>
					<label class="desc" for="programId">Show patients that are newly enrolled in program(s) </label>
					<div style="overflow: auto; height: 80px">			
						<c:forEach var="program" items='${programs}'>
							<input type="checkbox" name="programId"/>${program.name} <br/>
						</c:forEach>
					</div>
				</li>														
				
<!--  				
				<li>
					<label class="desc" for="stateId">and currently has a</label>
					<c:forEach var="program" items='${programs}'>
						<c:forEach var="workflow" items='${program.allWorkflows}'>
							<strong>${workflow.concept.name}</strong> of <br/>
							<div style="overflow: auto; height: 60px">			
								<c:forEach var="state" items='${workflow.states}'>
									<input type="checkbox" name="stateId"/>${state.concept.name}<br/>
								</c:forEach>
							</div>
						</c:forEach>
					</c:forEach>
				</li>
-->				
				<li>
					<label class="desc" for="programId">on/before </label>
					<div>			
						<openmrs:fieldGen type="java.util.Date" formFieldName="startDate" val="" parameters=""/>
					</div>
				</li>													
				<li>
					<label class="desc" for="programId">and on/after </label>
					<div>			
						<openmrs:fieldGen type="java.util.Date" formFieldName="endDate" val="" parameters=""/>
					</div>
				</li>													
				<li>
					<div align="center">			
						<input type="submit" value="View Patients"/>
					</div>
				</li>													
			</ul>
		</form>
	</div>
									

	<h3><a href="#">Section 2</a></h3>
	<div>
		<p>
		Sed non urna. Donec et ante. Phasellus eu ligula. Vestibulum sit amet
		purus. Vivamus hendrerit, dolor at aliquet laoreet, mauris turpis porttitor
		velit, faucibus interdum tellus libero ac justo. Vivamus non quam. In
		suscipit faucibus urna.
		</p>
	</div>
	<h3><a href="#">Section 3</a></h3>
	<div>
		<p>
		Nam enim risus, molestie et, porta ac, aliquam ac, risus. Quisque lobortis.
		Phasellus pellentesque purus in massa. Aenean in pede. Phasellus ac libero
		ac tellus pellentesque semper. Sed ac felis. Sed commodo, magna quis
		lacinia ornare, quam ante aliquam nisi, eu iaculis leo purus venenatis dui.
		</p>
		<ul>
			<li>List item one</li>
			<li>List item two</li>
			<li>List item three</li>
		</ul>
	</div>
	<h3><a href="#">Section 4</a></h3>
	<div>
		<p>
		Cras dictum. Pellentesque habitant morbi tristique senectus et netus
		et malesuada fames ac turpis egestas. Vestibulum ante ipsum primis in
		faucibus orci luctus et ultrices posuere cubilia Curae; Aenean lacinia
		mauris vel est.
		</p>
		<p>
		Suspendisse eu nisl. Nullam ut libero. Integer dignissim consequat lectus.
		Class aptent taciti sociosqu ad litora torquent per conubia nostra, per
		inceptos himenaeos.
		</p>
	</div>
</div>











	
					</div><!-- portlet-content -->
				</div><!-- portlet -->
			</div><!-- column -->
			
			
			<div class="column">	
			
			</div>
		
		</div><!-- portal -->
	</div><!-- container -->
</div><!-- page -->

</div>
</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
