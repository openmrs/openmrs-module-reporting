<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/datasets/manageDataSets.list" />
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

		<div id="inline-list">	
			<p>	
				<ul>
					<li class="last">
						<strong>Create a New Dataset:</strong>
						<c:forEach items="${types}" var="t">
							&nbsp;&nbsp;
							<input type="button" value="<rpt:displayLabel type="${t.type.name}"/>" onclick="document.location.href='${pageContext.request.contextPath}${t.createPage}';">
						</c:forEach>
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
				<c:forEach items="${dataSetDefinitions}" var="d" varStatus="status">
					<tr>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/viewDataSet.form?dataSetId=${d.definition.uuid}&id=${d.definition.id}&type=${d.type.name}">					
								<img src="${pageContext.request.contextPath}/images/play.gif" alt="view" border="0"/>
							</a> 
							&nbsp;
							<c:choose>
								<c:when test="${!empty d.editPage}">
									<a href="${pageContext.request.contextPath}${d.editPage}">				
										<img src='<c:url value="/images/edit.gif"/>' alt="edit" border="0"/>
									</a>
								</c:when>
								<c:otherwise>
									&nbsp;&nbsp;&nbsp;
								</c:otherwise>
							</c:choose>
							&nbsp;
							<a href="${pageContext.request.contextPath}/module/reporting/datasets/removeDataSet.form?uuid=${d.definition.uuid}&type=${d.type.name}">
								<img src="${pageContext.request.contextPath}/images/trash.gif" alt="delete" border="0"/>					
							</a> 
						</td>
						<td>
							<c:if test="${!empty d.editPage}">
								<a href="${pageContext.request.contextPath}${d.editPage}">	
							</c:if>	
								${d.definition.name}
							<c:if test="${!empty d.editPage}"></a></c:if>
						</td>
						<td>
							${d.definition.creator}
						</td>
						<td>
							<c:if test="${d.definition.dateCreated!=null}">
								<rpt:timespan then="${d.definition.dateCreated}"/>
							</c:if>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>
		<br/>
	</div>
	<br/>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>