<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Data Sets" otherwise="/login.htm" redirect="/module/reporting/datasets/manageDataSets.list" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#dataset-table').dataTable( {
		"bPaginate": true,
		"iDisplayLength": 25,
		"bLengthChange": false,
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

	//$('#dataset-table').on

} );

</script>



<div id="page">

	<div id="container">

		<h1>Dataset Manager</h1>
		<%--
		<spring:message code="reporting.manage.createNew"/>:
		<c:forEach var="createLink" items="${types}">
			<form style="display: inline" action="../newDataset.form">
				<input type="hidden" name="type" value="${createLink.name}"/>
				<input type="submit" value="${createLink.simpleName}"/>
			</form>
		</c:forEach>
		--%>

		<div id="inline-list">	
			<p>	
				<ul>
					<li class="last">
						<form method="get" action="${pageContext.request.contextPath}/module/reporting/datasets/newDataSet.form" style="display:inline">					
							<strong>Create a New Dataset:</strong>
							<select name="type">
								<option value="">&nbsp;</option>
								<c:forEach items="${types}" var="type">
									<option value="${type.name}">${type.simpleName}</option>
								</c:forEach>
							</select>
							<input type="submit" value="Create"/>					
						</form>		
					</li>
				</ul>
			</p>			
		</div>
						
		<table id="dataset-table" class="display" >
			<thead>
				<tr>
					<th width="84%">Name</th>
					<th width="10%">Type</th>
					<th width="10%">Description</th>
					<th width="10%">Author</th>
					<th width="10%">Created</th>
					<th align="center" width="1%">View</th>
					<th align="center" width="1%">Edit</th>
					<th align="center" width="1%">Remove</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${dataSetDefinitions}" var="dataset" varStatus="status">
					<tr>
						<td width="54%">
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/editDataSet.form?uuid=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}&action=edit">				
								${dataset.name}
							</a>
						</td>
						<td width="10%">
							${dataset.class.simpleName}
						</td>
						<td nowrap>
							<c:choose>
								<c:when test="${fn:length(dataset.description)>55}">
									${fn:substring(dataset.description,0,55)}...
								</c:when>
								<c:otherwise>${dataset.description}</c:otherwise>
							</c:choose>				
						</td>
						<td width="5%" nowrap>
							${dataset.creator}
						</td>
						<td width="5%" nowrap>
							<c:if test="${dateset.dateCreated!=null}">
								<rpt:timespan then="${dataset.dateCreated}"/>
							</c:if>
						</td>
		 				<td align="center" width="1%">
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/viewDataSet.form?dataSetId=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}">					
								<img src="${pageContext.request.contextPath}/images/play.gif" alt="view" border="0"/>
							</a> 
						</td>
						<td align="center" width="1%">
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/editDataSet.form?uuid=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}">				
								<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
							</a>
						</td>
						<td align="center" width="1%">				
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/removeDataSet.form?uuid=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}">
								<img src="${pageContext.request.contextPath}/images/trash.gif" alt="delete" border="0"/>					
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