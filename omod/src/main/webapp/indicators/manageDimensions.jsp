<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Dimension Definitions" otherwise="/login.htm" redirect="/module/reporting/indicators/manageDimensions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		$j(".reporting-data-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

		<c:forEach items="${dimensions}" var="dimension" varStatus="status">	
			$j("#preview-${dimension.uuid}").click(function(event){
				showReportingDialog({ 
					title: 'Preview Dimension', 
					url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${dimension.uuid}&type=${dimension['class'].name}',
					successCallback: function() { 
						window.location = window.location; //.reload(true);
					} 
				});
			});
		</c:forEach>
	} );

	function confirmDelete(name, uuid) {
		if (confirm("Are you sure you want to delete " + name + "?")) {
			document.location.href = '${pageContext.request.contextPath}/module/reporting/indicators/purgeDimension.form?uuid=' + uuid;
		}
	}
	
</script>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.dimensionsManager" /></h1>
		
		<spring:message code="reporting.manage.createNew"/>:
		<input type="button" value="Dimension" onClick="window.location='editCohortDefinitionDimension.form';"/>

		<table class="reporting-data-table display" >
			<thead>
				<tr>
					<th><spring:message code="reporting.name" /></th>
					<th><spring:message code="reporting.description" /></th>
					<th><spring:message code="reporting.options" /></th>
					<th><spring:message code="reporting.remove" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${dimensions}" var="dimension" varStatus="status">
					<c:url var="editUrl" value="editCohortDefinitionDimension.form">
						<c:param name="uuid" value="${dimension.uuid}"/>
					</c:url>
					<tr>
						<td width="20%" nowrap="">
							<a href="${editUrl}">
								<c:out value="${dimension.name}" />
							</a>
						</td>
						<td width="20%">
							<span class="small">
								<c:out value="${dimension.description}" />
							</span>
						</td>
						<td width="10%">
							<c:forEach var="opt" items="${dimension.optionKeys}" varStatus="optStatus">
								<c:if test="${optStatus.index > 0}">,</c:if>
								${opt}
							</c:forEach>
						</td>
						<td width="1%" align="center">
							<a href="javascript:confirmDelete('${dimension.name}','${dimension.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
							<span style="padding-left: 10px;">
								<a href="javascript:void(0)" id="preview-${dimension.uuid}">
									<img src="<c:url value='/images/play.gif'/>" border="0" style="vertical-align:middle;"/>
								</a>
							</span>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
			</tfoot>
		</table>
	
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
