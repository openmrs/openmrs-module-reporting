<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="localHeader.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Dataset Manager</title>
<style type="text/css" title="currentStyle">
@import "${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/page.css";
@import "${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/css/table.css";
</style>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#dataset-table').dataTable( {
			"bPaginate": true,
			"bLengthChange": true,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		} );			
	} );

</script>
</head>
<body id="dt_example">
<h1>Dataset Manager</h1>

<strong>Create a new dataset for </strong>
<!--  TODO This should be an unordered list of links -->
<a href="#">patients</a> | <a href="#">visits</a> | <a href="#">observation</a> | <a href="#">programs</a> | <a href="#">orders</a> | <a href="#">more...</a>

<h2>Datasets</h2>
<table id="dataset-table" class="display" >
	<thead>
		<tr>
			<th>Dataset</th>
			<th>Author</th>
			<th>Modified</th>
			<th>Export</th>
			<th>Preview</th>
			<th>Delete</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${datasetList}" var="dataset" varStatus="status">
			<tr>
				<td>Dataset #${status.count} ${dataset.name} (<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789">edit</a>)</td>
				<td>Justin</td>
				<td>two days ago</td>
				<td>
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">csv</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">tsv</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">xls</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">xml</a> 
				</td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=preview">preview</a></td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=delete">delete</a></td>
			</tr>
		</c:forEach>	
		<c:forEach items="${moreDatasetDefinitions}" var="dataset" varStatus="status">
			<tr>
				<td>Other Dataset #${status.count} ${dataset.name} (<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789">edit</a>)</td>
				<td>Darius</td>
				<td>one day ago</td>
				<td>
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">csv</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">tsv</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">xls</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=export&type=csv">xml</a> 
				</td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=preview">preview</a></td>
				<td><a href="${pageContext.request.contextPath}/module/reporting/datasetEditor.form?uuid=123456789&action=delete">delete</a></td>
			</tr>
		</c:forEach>	
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" align="left">
				<!-- <a href="#">Add Column</a> --> 
				<button name="button1">Remove Selected</button>
				<button name="button1">Generate Selected</button>
			</td>
			
			
			</td>
		</tr>
	
	</tfoot>
</table>


</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>