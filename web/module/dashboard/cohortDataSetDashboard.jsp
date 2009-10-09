<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../run/localHeader.jsp"%>

<style type="text/css">
	#page { margin: 0px; } 
	#cohortHeader { padding-left: 5px; background-color: #003366; height: 2em; border-bottom: 1px solid black;
	vertical-align:middle; width: 100%; 
	line-height:2em; font-size: 1.5em; font-weight: bold; color: white; } 	
	#cohortResultsColumn { width: 100%; height: 90%; text-align: left; margin: 0px; padding-left: 5px; padding-top: 5px; } 
	#accordion { width: 100%; } 
	table { width: 100%; } 
	.profileImage { width: 75px; height: 86px; }
	#cohort-details-table_wrapper { width: 75%; } 
	#cohort-details-table { border: 0px; } 
</style>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#dataset-preview-table').dataTable( {
		"bPaginate": true,
		//"sPaginationType": "full_numbers",
		"bLengthChange": false,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );	

	$("#show-columns").click(function(event){ 
		// eventually will show/hide appropriate columns based on selected dataset definition
	});


} );
</script>
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

<style>
	input,select { font-size: medium; } 
	legend { font-size: large; } 
	label { font-size: medium; font-weight: bold; } 
	.desc { display: block; }
</style>


<!-- 
<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>
 -->

<div id="page" style="display:block;">
	<div id="container">
		<div id="portal">
			<div id="cohortResultsColumn">				
				<div>
					<b>${selectedColumn}</b>

					<c:if test="${!empty dataSet}">

						<table id="dataset-preview-table" class="display">
							<thead>
								<tr>
									<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="varStatus">				
										<th>
											${column.displayName}
										</th>
									</c:forEach>
								</tr>
							</thead>
							<tbody>						
									<c:forEach var="dataSetRow" items="${dataSet.rows}" varStatus="varStatus">
										<tr>
										
											<c:forEach var="column" items="${dataSetDefinition.columns}" varStatus="varStatus">				
												<td>
													${dataSetRow.columnValues[column]}
												</td>
											</c:forEach>										
										</tr>
									</c:forEach>
							</tbody>
							<tfoot>
							</tfoot>
						</table>
					</c:if>
				</div>					
			</div><!-- column -->
			
			<br clear="both"/>
			
		
		</div><!-- portal -->
	</div><!-- container -->
</div><!-- page -->


</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
