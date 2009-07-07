<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet" />

<!--  Javascript -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery-1.3.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js"></script>


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#indicator-table').dataTable( {
		"bPaginate": true,
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true
	} );			
} );

</script>

<div id="page">
	<div id="container">
		<h1>Indicator Manager</h1>
		
		<br/>
		<table id="indicator-table" class="display" >
			<thead>
				<tr>
					<th>Indicator</th>
					<th>Aggregator</th>
					<th>Description</th>
					<th>Cohort / Logic Rule</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${indicators}" var="indicator" varStatus="status">
					<tr>
						<td><a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form?uuid=${indicator.uuid}">${indicator.name}</a></td>
						<td>
							${indicator.aggregator.simpleName}<br/>						
						</td>
						<td>${indicator.description}</td>
						<td>
							<!-- one of the two of these should be populated
								TODO Add logic to test if they are null
							 -->
							${indicator.cohortDefinition.parameterizable.name}<br/>
							${indicator.logicCriteria}
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form?uuid=${indicator.uuid}&action=preview">preview</a> | 
							<a href="${pageContext.request.contextPath}/module/reporting/editIindicator.form?uuid=${indicator.uuid}&action=delete">delete</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
				<tr>
					<th colspan="5" align="center" height="50">
						<a class="button" href="${pageContext.request.contextPath}/module/reporting/editIndicator.form">Add Indicator</a>
					</th>			
				</tr>	
			</tfoot>
		</table>
	</div>
</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>