<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui-1.6/ui.all.css" rel="stylesheet"/>

<!-- Autocomplete -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>

<!-- Other -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui-1.6/jquery-ui-1.6.custom.min.js"></script>

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

	$('#report-tabs').tabs();
	$('#report-tabs').show();
	
} );
</script>

<div id="page"><!-- -->
	<div id="container"><!--  -->
		<h1>Report Editor</h1>


		
		<div id="report-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#report-tabs-details"><span>Details</span></a></li>
                <li><a href="#report-tabs-datasets"><span>Data Sets</span></a></li>
                <li><a href="#report-tabs-parameters"><span>Parameters</span></a></li>
            </ul>
		</div>
		
			
		<div id="report-tabs-details" class="ui-tabs-hide">
			<div class="info">
				<h2>Report Details</h2>
			</div>		
			<form id="form65" name="form65" class="wufoo topLabel" autocomplete="off"
				method="post" action="${pageContext.request.contextPath}/module/reporting/saveReportSchema.form">	
				<input type="hidden" id="uuid" name="uuid" value="${reportSchema.uuid}"/>
							
				<ul>		
					<li id="uuid" class="myown">
						<label class="desc" id="nameLabel" for="uuid">ID</label>
						<div><i>${reportSchema.uuid}</i></div>
					</li>
					<li id="name" class="myown">
						<label class="desc" id="nameLabel" for="name">Name</label>
						<div>
							<input id="name" 	
								name="name" 
								type="text" 
								class="field text large" 
								value="${reportSchema.name}" 
								size="20" tabindex="1" />
						</div>
					</li>
					
					<li id="description" class="myown">		
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
						<div>
							<input id="saveForm" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<input id="cancelForm" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
						</div>
					</li>
				</ul>
			</form>				
		</div>

		<div id="report-tabs-datasets" class="ui-tabs-hide">
		
			<div class="info">
				<h2>Report Datasets</h2>
			</div>
		
		
			<table id="report-dataset-table" class="display">
				<thead>
					<tr>
						<th>Name</th>
						<th>Description</th>
						<th>Columns</th>
						<th>Actions</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="mapped" items="${reportSchema.dataSetDefinitions}" varStatus="varStatus">
						<tr>
							<td>${mapped.parameterizable.name}</td>
							<td>${mapped.parameterizable.description}</td>
							<td>
								<c:forEach var="column" items="${mapped.parameterizable.columns}" varStatus="varStatus">
									${column.columnName} ${column.description}<c:if test="${!varStatus.last}">,</c:if>
								</c:forEach>
							</td>
							<td width="22%" align="center">
								<c:choose>
									<c:when test="${!empty mapped.parameterizable.uuid}">						
										<a href="${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form?uuid=${mapped.parameterizable.uuid}&id=${mapped.parameterizable.id}&className=${mapped.parameterizable.class.name}">					
											<img src="${pageContext.request.contextPath}/images/play.gif" alt="preview dataset" border="0"/>
										</a> &nbsp;&nbsp;
										<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${mapped.parameterizable.uuid}&action=edit">
											<img src="${pageContext.request.contextPath}/images/edit.gif" alt="edit dataset" border="0"/>
										</a> &nbsp;&nbsp;
										<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${mapped.parameterizable.uuid}&action=duplicate">
											<img src="${pageContext.request.contextPath}/images/file.gif" alt="duplicate dataset" border="0"/>					
										</a> &nbsp;&nbsp;
										<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${mapped.parameterizable.uuid}&action=delete">
											<img src="${pageContext.request.contextPath}/images/trash.gif" alt="delete dataset" border="0"/>					
										</a> &nbsp;&nbsp;
									</c:when>
									<c:otherwise>
										<a href="${pageContext.request.contextPath}/module/reporting/saveDataSet.form?report.uuid=${reportSchema.uuid}&dataSet.name=${mapped.parameterizable.name}&action=save">
											<img src="${pageContext.request.contextPath}/images/save.gif" alt="save dataset definition" border="0"/>
										</a>
									</c:otherwise>
								</c:choose>							
							</td>						
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
	
	
		<div id="report-tabs-parameters" class="ui-tabs-hide">

			<div class="info">
				<h2>Report Parameters</h2>
			</div>


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
		
			
</div>

</div>


	
	
<%@ include file="/WEB-INF/template/footer.jsp"%>