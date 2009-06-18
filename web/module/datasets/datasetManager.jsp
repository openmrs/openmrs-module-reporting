<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
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
	$('#dataset-table').dataTable( {
		"bPaginate": true,
		"iDisplayLength": 10,
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

<h1>Dataset Manager</h1>


<table id="dataset-table" class="display" >
	<thead>
		<tr>
			<th width="3%">Delete</th>
			<th>Name</th>
			<th>Actions</th>
		</tr>
	</thead>
	
	<tbody>
		<c:forEach items="${datasetDefinitions}" var="dataset" varStatus="status">
			<tr>
				<td align="center">
					<input type="checkbox" name="deleteDataset" value="${dataset.uuid}"/>
				</td>
				<td>
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&id=${dataset.id}&className=${dataset.class.name}&action=edit">				
						${dataset.name}
					</a>
				</td>
<!-- 
				<td>
					<c:choose>
						<c:when test="${fn:length(dataset.description)>55}">
							${fn:substring(dataset.description,0,55)}...
						</c:when>
						<c:otherwise>${dataset.description}</c:otherwise>
					</c:choose>				
				</td>
 -->				
<!--  
				<td width="20%">
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=export&type=csv">csv</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=export&type=csv">tsv</a> | 
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=export&type=csv">xls</a>
				</td>
-->				
				<td width="22%" align="center">
					<a href="${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form?uuid=${dataset.uuid}&id=${dataset.id}&className=${dataset.class.name}">					
						<img src="${pageContext.request.contextPath}/images/play.gif" alt="preview dataset" border="0"/>
					</a> &nbsp;&nbsp;
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=edit">
						<img src="${pageContext.request.contextPath}/images/edit.gif" alt="edit dataset" border="0"/>
					</a> &nbsp;&nbsp;
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=duplicate">
						<img src="${pageContext.request.contextPath}/images/file.gif" alt="duplicate dataset" border="0"/>					
					</a> &nbsp;&nbsp;
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=delete">
						<img src="${pageContext.request.contextPath}/images/trash.gif" alt="delete dataset" border="0"/>					
					</a> &nbsp;&nbsp;
					<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=export">
						<img src="${pageContext.request.contextPath}/images/save.gif" alt="export dataset" border="0"/>
					</a>
				</td>
			</tr>
		</c:forEach>	
	</tbody>
	<tfoot>
		<tr>
			<td colspan="4" align="center">
				<button name="button1" disabled>Add Dataset</button>
				<button name="button1" disabled>Remove Selected</button>
				<button name="button1" disabled>Generate Selected</button>			
			</td>
		</tr>
	
	</tfoot>
</table>

</div>

</div>




<%@ include file="/WEB-INF/template/footer.jsp"%>