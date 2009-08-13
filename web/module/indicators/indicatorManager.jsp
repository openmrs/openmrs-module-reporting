<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#indicator-table').dataTable( {
		"bPaginate": true,
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
		<h1>Indicator Manager</h1>

		<div id="inline-list">
			<p>
				<ul>
					<li class="first"><a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form">Add Indicator</a></li>
					<li><a href="${pageContext.request.contextPath}/module/reporting/newIndicator.form">Add Cohort Indicator</a></li>
					<li class="last">more to come...</li>
				</ul>
			</p>
		</div>	

		<table id="indicator-table" class="display" >
			<thead>
				<tr>
					<th width="1%"></th>
					<th width="40%">Indicator</th>
					<th width="10%">Aggregator</th>
					<th width="10%">Cohort Definition</th>
					<th width="15%">Created</th>
					<th width="1%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${indicators}" var="indicator" varStatus="status">
					<tr>
						<td align="center">							
							<a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form?uuid=${indicator.uuid}">
								<img src="<c:url value='/images/edit.gif'/>" border="0"/>
							</a>
						</td>
						<td>
							<a href="${pageContext.request.contextPath}/module/reporting/editIndicator.form?uuid=${indicator.uuid}">
								${indicator.name}
							</a>
						</td>					
						<td width="10%">
							${indicator.aggregator.simpleName}		
						</td>
						<td width="10%">
							${indicator.cohortDefinition.parameterizable.name}
						</td>
						<td width="15%">
							Created by ${indicator.creator.username} on ${indicator.dateCreated}
						</td>
						<td align="center">							
							<a href="${pageContext.request.contextPath}/module/reporting/purgeIndicator.form?uuid=${indicator.uuid}">
								<img src="<c:url value='/images/trash.gif'/>" border="0"/>
							</a>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
				<!--  
				<tr>
					<th colspan="6" align="center" height="50">
						
					</th>			
				</tr>
				-->	
			</tfoot>
		</table>
	</div>
</div>



<%@ include file="/WEB-INF/template/footer.jsp"%>