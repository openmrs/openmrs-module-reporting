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
$j(document).ready(function() {
	$j('#dataset-preview-table').dataTable( {
		"bPaginate": true,
		//"sPaginationType": "full_numbers",
		"bLengthChange": false,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true,
		"oLanguage": {
			"oPaginate": {
				"sPrevious": "<spring:message code="general.previous" javaScriptEscape="true"/>",
				"sNext": "<spring:message code="general.next" javaScriptEscape="true"/>",
			},
			"sInfo": "<spring:message code="SearchResults.viewing" javaScriptEscape="true"/> _START_ - _END_ <spring:message code="SearchResults.of" javaScriptEscape="true"/> _TOTAL_ ",
			"sSearch": "<spring:message code="general.search" javaScriptEscape="true"/>"
		}		
		
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );	

	$j("#show-columns").click(function(event){
		// eventually will show/hide appropriate columns based on selected dataset definition
	});


} );
</script>
<script type="text/javascript">
$j(function() {

	$j('#cohort-breakdown-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
	
	$j("#accordion").accordion();
	$j('#cohort-tabs').tabs();
	$j('#cohort-tabs').show();

	$j('#cohort-details-table').dataTable(
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

<openmrs:portlet url="currentReportHeader" moduleId="reporting"/>

<div id="page" style="display:block;">
	<div id="container">
		<div id="portal">
			<div id="cohortResultsColumn">				
				<div>				
					<h3>${selectedColumn.name}: <spring:message code="${selectedColumn.label}"/> (${fn:length(dataSet.rows)} <spring:message code="Patient.header"/>)</h3>
					
					<c:if test="${!empty dataSet}">

						<table id="dataset-preview-table" class="display">
							<thead>
								<tr>
									<c:forEach var="column" items="${dataSet.metaData.columns}" varStatus="varStatus">				
										<th>
											${column.label}
										</th>
									</c:forEach>
								</tr>
							</thead>
							<tbody>						
									<c:forEach var="dataSetRow" items="${dataSet.rows}" varStatus="varStatus">
										<c:set var="patId" value="${dataSetRow.columnValuesByKey['patientId']}"/>
										<tr>
											<c:forEach var="column" items="${dataSet.metaData.columns}" varStatus="varStatus">
												<td>
													<c:if test="${!empty patId}"><a href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${patId}"></c:if>
														${dataSetRow.columnValues[column]}
													<c:if test="${!empty patId}"></a></c:if>
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
