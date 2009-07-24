<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="View Orders" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#dataset-table').dataTable( {
		"bPaginate": true,
		"iDisplayLength": 10,
		"bLengthChange": true,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": true,
		"aoColumns": [{ "bSortable": false },
		      		  { "bSortable": true },
		              { "bSortable": false },
		              { "bSortable": false },
		              { "bSortable": false },
		              { "bSortable": false },
		              { "bSortable": false }]
	} );			
} );

</script>



<div id="page">

	<div id="container">

		<h1>Dataset Manager</h1>
		<div id="inline-list">	
			<p>	
				<ul>
					<li class="first"><a href="<c:url value='/module/reporting/indicatorDataset.form'/>">Create Indicator Dataset</a></li>
					<li class="last">more ...</li>
				</ul>
			</p>			
		</div>
		
				
		<table id="dataset-table" class="display" >
			<thead>
				<tr>
					<th align="center" width="1%"><img src='<c:url value="/images/trash.gif"/>' border="0"/></th>
					<th>Name</th>
					<th>Type</th>
					<th>Created</th>
					<th align="center" width="1%"><img src='<c:url value="/images/edit.gif"/>' border="0"/></th>
					<th align="center" width="1%"><img src='<c:url value="/images/play.gif"/>' border="0"/></th>
					<th align="center" width="1%"><img src='<c:url value="/images/copy.gif"/>' border="0"/></th>
					<th align="center" width="1%"><img src='<c:url value="/images/trash.gif"/>' border="0"/></th>
					<th align="center" width="1%"><img src='<c:url value="/images/save.gif"/>' border="0"/></th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${datasetDefinitions}" var="dataset" varStatus="status">
					<tr>
						<td align="center">
							<input type="checkbox" name="selectedUuid" value="${dataset.uuid}"/>
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&id=${dataset.id}&className=${dataset.class.name}&action=edit">				
								${dataset.name}
							</a>
						</td>
						<td>
							${dataset.class.simpleName}
						</td>
						<td>
							<!-- TODO This should be fixed!  All dataset definitions should have metadata -->
							<c:if test="${!dataset.class.simpleName eq 'DataExportDataSetDefinition'}">
								${dataset.createdDate}
							</c:if>
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
						<td align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&id=${dataset.id}&className=${dataset.class.name}&action=edit">				
								<img src='<c:url value="/images/edit.gif"/>' border="0"/>
							</a>
						</td>
						<td align="center">
							<a href="${pageContext.request.contextPath}/module/reporting/showDatasetPreview.form?uuid=${dataset.uuid}&id=${dataset.id}&className=${dataset.class.name}">					
								<img src="${pageContext.request.contextPath}/images/play.gif" alt="preview" border="0"/>
							</a> 
						</td>
						<td align="center">				
							<img src="${pageContext.request.contextPath}/images/copy.gif" alt="duplicate" border="0"/>					
							<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=duplicate">
								<!--  copy the above image here when feature has been implemented -->
							</a> 
						</td>
						<td align="center">				
							<img src="${pageContext.request.contextPath}/images/trash.gif" alt="delete" border="0"/>					
							<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=delete">
								<!--  copy the above image here when feature has been implemented -->
							</a> 
						</td>
						<td align="center">				
							<img src="${pageContext.request.contextPath}/images/save.gif" alt="export" border="0"/>
							<a href="${pageContext.request.contextPath}/module/reporting/showDataset.form?uuid=${dataset.uuid}&action=export">
								<!--  copy the above image here when feature has been implemented -->
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<!-- 
			<tfoot>
				<tr>
					<th colspan="8" align="center" height="50">
						<button name="button1" disabled>Remove Selected</button>
						<button name="button1" disabled>Generate Selected</button>											
					</th>			
				</tr>	
			</tfoot>
			-->
		</table>
	</div>

</div>




<%@ include file="/WEB-INF/template/footer.jsp"%>