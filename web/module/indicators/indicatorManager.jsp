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
<style>
.small { text-decoration: italic; font-size: x-small;  }
</style>


<div id="page">
	<div id="container">
		<h1>Indicator Manager</h1>

		<div id="inline-list">
			<p>
				<ul>
					<li class="first"><a href="${pageContext.request.contextPath}/module/reporting/indicators/editIndicator.form">Add Indicator</a></li>
					<li><a href="${pageContext.request.contextPath}/module/reporting/indicators/indicatorWizard.form">Add Cohort Indicator</a></li>
					<li class="last">more to come...</li>
				</ul>
			</p>
		</div>	

		<table id="indicator-table" class="display" >
			<thead>
				<tr>
					<th>Indicator</th>
					<th>Cohort Definition</th>
					<th>Edit</th>
					<th>Created</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${indicators}" var="indicator" varStatus="status">
					<tr>
						<td align="center" width="1%">							
							<a href="${pageContext.request.contextPath}/module/reporting/indicators/editIndicator.form?uuid=${indicator.uuid}">
								<img src="<c:url value='/images/edit.gif'/>" border="0"/>
							</a>
						</td>
						<td width="20%">
							<a href="${pageContext.request.contextPath}/module/reporting/indicators/editIndicator.form?uuid=${indicator.uuid}">
								${indicator.name}
							</a>
						</td>					
						<td width="40%">
							${indicator.cohortDefinition.parameterizable.name}<br/>					
							<span nowrap="" class="small">defined by parameters (
								<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="status">
									${parameter.name}<c:if test="${!status.last}">,</c:if>
								</c:forEach>
							)</span>
						</td>
						<td width="15%">
							<span class="small">Created by ${indicator.creator.username} on ${indicator.dateCreated}</span>
						</td>
						<td align="center" width="1%">							
							<a href="${pageContext.request.contextPath}/module/reporting/indicators/purgeIndicator.form?uuid=${indicator.uuid}">
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