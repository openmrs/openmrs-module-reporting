<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Indicators" otherwise="/login.htm" redirect="/module/reporting/indicators/manageDimensions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#dimensions-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": true
		} );
	} );
</script>
<style>
.small { font-size: x-small; }
</style>


<div id="page">
	<div id="container">
		<h1>Dimensions Manager</h1>
		
		<spring:message code="reporting.manage.createNew"/>:
		<input type="button" value="Dimension" onClick="window.location='editCohortDefinitionDimension.form';"/>

		<table id="dimensions-table" class="display" >
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>Options</th>
					<th>Remove</th>
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
								${dimension.name}
							</a>
						</td>
						<td width="20%">
							<span class="small">${dimension.description}</span>
						</td>
						<td width="10%">
							<c:forEach var="opt" items="${dimension.optionKeys}" varStatus="optStatus">
								<c:if test="${optStatus.index > 0}">,</c:if>
								${opt}
							</c:forEach>
						</td>
						<td width="1%" align="center">
							<a href="purgeDimension.form?uuid=${dimension.uuid}">
								<img src='<c:url value="/images/trash.gif"/>' border="0"/>							
							</a>
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