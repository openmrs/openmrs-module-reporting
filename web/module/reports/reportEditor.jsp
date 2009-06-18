<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>

<!-- Autocomplete -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/localdata.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>

<!-- Other -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>

<!--  Javascript 
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery-1.3.1.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>
-->

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#report-dataset-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

	$('#report-parameter-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

} );
</script>

<div id="page"><!-- -->
	<div id="container"><!--  -->
		<h1>Report Details</h1>


		<div>
			<form id="form65" name="form65" class="wufoo topLabel" autocomplete="off"
				method="post" action="${pageContext.request.contextPath}/module/reporting/saveReportSchema.form">	
	
					<input type="hidden" id="uuid" name="uuid" value="${reportSchema.uuid}"/>
								
					<ul>		
						<li id="uuid" class="">
							<label class="desc" id="nameLabel" for="uuid">ID</label>
							<span><i>${reportSchema.uuid}</i></span>
						</li>
						<li id="name" class="">
							<label class="desc" id="nameLabel" for="name">Name</label>
							<span>
								<input id="name" 	
									name="name" 
									type="text" 
									class="field text" 
									value="${reportSchema.name}" 
									size="20" tabindex="1" />
							</span>
						</li>
						
						<li id="description" class="">		
							<label class="desc" id="descriptionLabel" for="description">Description</label>
							<div>
								<textarea id="description" 
									name="description" 
									class="field textarea small" 
									rows="10" cols="20"
									tabindex="2">${reportSchema.description}</textarea>				
							</div>
						</li>
						<li class="buttons">
							<input id="saveForm" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<input id="cancelForm" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
						</li>
					</ul>
			</form>
		</div>
	<div>
	


	<div class="info">
		<h2>Report Datasets</h2>
	</div>
	<div>
		<table id="report-dataset-table" class="display">
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>Columns</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="mapped" items="${reportSchema.dataSetDefinitions}" varStatus="status">
					<tr>
						<td>${mapped.parameterizable.name}</td>
						<td>${mapped.parameterizable.description}</td>
						<td>${mapped.parameterizable.columns}</td>
					</tr>					
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>			
					<th colspan="3" align="center">
						<button name="button1" disabled>Add</button>
					</th>
				</tr>
			</tfoot>
		</table>
	</div>


	<div class="info">
		<h2>Report Parameters</h2>
	</div>
	<div>
		<table id="report-parameter-table" class="display">
			<thead>
				<tr>
					<th>Name</th>
					<th>Label</th>
					<th>Class</th>
					<th>Default Value</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="parameter" items="${reportSchema.parameters}" varStatus="status">
					<tr>
						<td>${parameter.name}</td>
						<td>${parameter.label}</td>
						<td>${parameter.clazz}</td>
						<td>${parameter.defaultValue}</td>
					</tr>					
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>			
					<th colspan="4" align="center">
						<button name="button1" disabled>Add</button>
					</th>
				</tr>
			</tfoot>
		</table>
	</div>
</div><!--container-->

</div>
</div>

	
	
<%@ include file="/WEB-INF/template/footer.jsp"%>