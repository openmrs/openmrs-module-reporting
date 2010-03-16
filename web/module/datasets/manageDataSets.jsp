<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Data Sets" otherwise="/login.htm" redirect="/module/reporting/datasets/manageDataSets.list" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('.reporting-data-table').dataTable( {
		"bPaginate": true,
		"iDisplayLength": 25,
		"bLengthChange": false,
		"bFilter": true,
		"bSort": true,
		"bInfo": true,
		"bAutoWidth": false,
		"aoColumns": [{ "bSortable": false },
		      		  { "bSortable": true },
		              { "bSortable": false },
		              { "bSortable": false }]
	} );			

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
						
		<table class="reporting-data-table display" >
			<thead>
				<tr>
					<th style="white-space:nowrap;">Actions</th>
					<th style="white-space:nowrap;">Name</th>
					<th>Author</th>
					<th>Created</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${dataSetDefinitions}" var="dataset" varStatus="status">
					<tr>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/viewDataSet.form?dataSetId=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}">					
								<img src="${pageContext.request.contextPath}/images/play.gif" alt="view" border="0"/>
							</a> 
							&nbsp;
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/editDataSet.form?uuid=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}">				
								<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
							</a>
							&nbsp;	
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/removeDataSet.form?uuid=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}">
								<img src="${pageContext.request.contextPath}/images/trash.gif" alt="delete" border="0"/>					
							</a> 
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/editDataSet.form?uuid=${dataset.uuid}&id=${dataset.id}&type=${dataset.class.name}&action=edit">				
								${dataset.name}
							</a>
						</td>
						<td>
							${dataset.creator}
						</td>
						<td>
							<c:if test="${dateset.dateCreated!=null}">
								<rpt:timespan then="${dataset.dateCreated}"/>
							</c:if>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>
	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>