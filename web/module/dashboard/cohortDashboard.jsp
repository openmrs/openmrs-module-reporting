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
	
	$("#accordion").accordion();


	$('#cohort-tabs').tabs();
	$('#cohort-tabs').show();	

	var api = new jGCharts.Api(); 
	$('<img>') 
	.attr('src', api.make({
			data : [[153, 1, 52], [113, 2, 60]],  
			type : 'p'//default bvg 
	})) 
	.appendTo("#barChart");
	
	$('#cohort-details-table').dataTable(
			 {
					"bPaginate": true,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": true,
					"bAutoWidth": true
				} 

	);

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
	
		<h1>Cohort Analysis Dashboard</h1>
	
	
		<div id="portal">


			<div class="column">	
				<div class="portlet">
					<div class="portlet-header">Add a filter</div>
					<div class="portlet-content">		
					
					
						<div id="accordion">
							<h3><a href="#">Demographic</a></h3>
							<div>
								<p>
									<label class="desc">Age</label>
									<input type="checkbox"/>Adult<br/>
									<input type="checkbox"/>Child
								</p>
								
								<p>
									<label class="desc">Gender</label>
									<input type="checkbox"/>Male<br/>
									<input type="checkbox"/>Female
								</p>
								<p align="right">
									<input type="submit" value="Filter"/>
								</p>
								
							</div>
							<h3><a href="#">Program Enrollment</a></h3>
							<div>
								<p>
								</p>
							</div>
							<h3><a href="#">Drug</a></h3>
							<div>
								<p>
								</p>
							</div>
							<h3><a href="#">Health Center</a></h3>
							<div>
								<p>
								</p>
								
							</div>
						</div>
					
					
					
					
					
					</div><!-- portlet-content -->
				</div><!-- portlet -->
			</div><!-- column -->


		
			<div class="column">	
	
	
				<div class="portlet">
					<div class="portlet-header">Filters applied:</div>
					<div class="portlet-content">	
				
						<div id="filters" style="margin: 10px; padding:10px;">
							<span style="border: 1px solid #ccc; padding: 5px; background-color: #DDECF7; ">
								Gender <img src="${pageContext.request.contextPath}/images/delete.gif" align="absmiddle"/></span>
							<span style="border: 1px solid #ccc; padding: 5px; background-color: #DDECF7; ">
								Age <img src="${pageContext.request.contextPath}/images/delete.gif" align="absmiddle"/></span>
							<span style="border: 1px solid #ccc; padding: 5px; background-color: #DDECF7; ">
								Health Center <img src="${pageContext.request.contextPath}/images/delete.gif" align="absmiddle"/></span>
						</div>
					</div>	
				</div>
	
	
	
				<!--  Cohort Breakdown Portlet -->		
				<div class="portlet">
					<div class="portlet-header">Cohort Breakdown</div>
					<div class="portlet-content">
					
						<span>
							There are <strong>${cohort.size}</strong> patients in the select cohort
						</span>
						
						<div align="center">
							
							<div id="cohort-tabs" class="ui-tabs-hide">			
								<ul>
					                <li><a href="#cohort-summary-tab"><span>Summary</span></a></li>
					                <li><a href="#cohort-details-tab"><span>Details</span></a></li>
					            </ul>
							
								<div id="cohort-summary-tab">							
									<form method="post" action="">					
										<ul>				
											<li>
												<div id="barChart"></div>
											</li>
											<li>
												
											
												
											</li>
										</ul>
									</form>
								</div>
								<div id="cohort-details-tab" width="100%"		
									<table id="cohort-details-table" class="display" width="100%">
										<thead>
											<tr>
												<th>Patient ID</th>
												<th>Patient Name</th>
												<th>Gender</th>
												<th>Age</th>
											</tr>
										</thead>
										<tbody>					
											<c:forEach var="memberId" items="${cohort.memberIds}" varStatus="status">
												<tr>
													<td>${memberId}</td>
													<td></td>
													<td></td>
													<td></td>
												</tr>							
											</c:forEach>
										</tbody>
										<tfoot></tfoot>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>							
				
			</div><!-- column -->
			
			
			<div class="column">	

				<div class="portlet">
					<div class="portlet-header">Actions</div>
					<div class="portlet-content">			
								
						<form>
							<ul>
								<li>
									<a href="/module/reporting/viewDemographicData.form">View demographic data</a>
									(
									<a href="/module/reporting/downloadDemographicData.form?format=csv">csv</a> |
									<a href="/module/reporting/downloadDemographicData.form?format=tsv">tsv</a> |
									<a href="/module/reporting/downloadDemographicData.form?format=xls">xls</a> 
									)
								</li>
								<li><a href="/module/reporting/viewHivProgramData.form">View HIV program data</a></li>
								<li><a href="/module/reporting/viewTbProgramData.form">View TB program data</a></li>
								<li>----------------------------</li>							
								<li><a href="/module/reporting/runReport.form">Run a report ...</a></li>
								<li>----------------------------</li>							
								<li><a href="/module/reporting/saveCohortQuery.form">Save cohort query</a></li>
								<li><a href="/module/reporting/saveCohort.form">Save cohort</a></li>
							</ul>
						</form>							
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
