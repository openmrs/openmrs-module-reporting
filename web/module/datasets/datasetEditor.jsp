<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm"
	redirect="/module/datasetbuilder/Dataset-Editor.htm" />
<%@ include file="localHeader.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Dataset Editor</title>
<style type="text/css" title="currentStyle">
@import "${pageContext.request.contextPath}/moduleResources/datasetbuilder/css/demo_page.css";
@import "${pageContext.request.contextPath}/moduleResources/datasetbuilder/css/demo_table.css";
</style>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/datasetbuilder/css/dataset/customStyle.css" rel="stylesheet" />
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/datasetbuilder/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/datasetbuilder/js/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#column-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
		//"sDom": '<"top"i>rt<"bottom"flp<"clear">'
	} );
} );
</script>


</head>

<body id="dt_example">


<h1>Dataset Editor</h1>

<div>
	<label for="name">Name:</label><br/>
	<input type="text" name="name" value="Patient Dataset #1" />
</div>
<div>
	<label for="name">Description:</label><br/>
	<textarea rows="7" cols="50">Description of this dataset.</textarea>
</div>
<div>
<div>
	<input type="submit" value="Save">
	<input type="submit" value="Cancel">
</div>



<h1>Dataset Columns</h1>
<table id="column-table" class="display">
	<thead>
		<tr>
			<th>Delete</th>
			<th>Column</th>
			<th>Data Type</th>
			<th>Preview</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td><input type="checkbox" name="option1" value="dt1"></td>
			<td>Patient Id</td>
			<td>Numeric</td>
			<td>1</td>
		</tr>
		<tr>
			<td><input type="checkbox" name="option1" value="dt1"></td>
			<td>Patient Identifier</td>
			<td>String</td>
			<td>123456789-A</td>
		</tr>
		<tr>
			<td><input type="checkbox" name="option1" value="dt1"></td>
			<td>First Name</td>
			<td>String</td>
			<td>Justin</td>
		</tr>
		<tr>
			<td><input type="checkbox" name="option1" value="dt1"></td>
			<td>Last Name</td>
			<td>String</td>
			<td>Miranda</td>
		<tr>
			<td><input type="checkbox" name="option1" value="dt1"></td>
			<td>Age</td>
			<td>Numeric</td>
			<td>31</td>
		</tr>
		<tr>
			<td><input type="checkbox" name="option1" value="dt1"></td>
			<td>Gender</td>
			<td>String</td>
			<td>Male</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>			
			<td colspan="6" align="left">
				<!-- <a href="#">Add Column</a> --> 
				<button name="button1">Add Column</button>
				<button name="button1">Remove Selected</button>
			</td>
		</tr>
	</tfoot>
</table>
</div>



</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp"%>