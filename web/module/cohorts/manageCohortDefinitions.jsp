<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
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
		"bAutoWidth": true
	});
});

function confirmDelete(name, uuid) {
	if (confirm("Are you sure you want to delete " + name + "?")) {
		document.location.href = 'purgeCohortDefinition.form?uuid='+uuid;
	}
}

</script>

<div id="page">

	<div id="container">
	
		<h1>Cohort Definitions</h1>
	
		<spring:message code="reporting.manage.createNew"/>:
		<c:forEach var="p" items="${customPages}">
			<button onClick="window.location = '${p.value}';">${p.key.simpleName}</button>
		</c:forEach>
		<form method="get" action="editCohortDefinition.form" style="display:inline">
			or other:
			<select name="type" style="font-size: 1.5em;">
				<option value="">&nbsp;</option>
				<c:forEach items="${types}" var="type">
					<c:if test="${customPages[type] == null}">
						<option value="${type.name}">${type.simpleName}</option>
					</c:if>
				</c:forEach>
			</select>
			<input type="submit" value="Create"/>
		</form>
		
		<br/>
		<table class="reporting-data-table display" >
			<thead>
				<tr>
					<th align="center" width="1%">Actions</th>
					<th>Name</th>
					<th>Description</th>
					<th>Creator</th>
					<th>Created</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${cohortDefinitions}" var="cohortDefinition" varStatus="status">
				
					<script>					
					$(document).ready(function() {
						$("#cohort-${cohortDefinition.uuid}").click(function(event){ 
							showReportingDialog({ 
								title: 'Preview Cohort Definition', 
								url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}',
								successCallback: function() { 
									window.location = window.location; //.reload(true);
								} 
							});
						});
					} );
					</script>					

					<c:choose>
						<c:when test="${customPages[cohortDefinition.class] == null}">
							<c:set var="editUrl" value="editCohortDefinition.form?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}"/>
						</c:when>
						<c:otherwise>
							<c:set var="editUrl" value="${customPages[cohortDefinition.class]}?uuid=${cohortDefinition.uuid}"/>
						</c:otherwise>
					</c:choose>
				
					<tr>
						<td align="left" nowrap>
							&nbsp;
							<a href="${editUrl}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:confirmDelete('${cohortDefinition.name}','${cohortDefinition.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
							&nbsp;
							<a href="#" id="cohort-${cohortDefinition.uuid}"><img src="<c:url value='/images/play.gif'/>" border="0"/></a>
						</td>
						<td>
							<a href="${editUrl}">
								${cohortDefinition.name}
							</a>
						</td>
						<td>
							${cohortDefinition.description}
						</td>
<!--  						
						<td align="center">
							<a href="evaluateCohortDefinition.form?uuid=${cohortDefinition.uuid}">
								<img src="<c:url value='/images/play.gif'/>" border="0"/>
							</a>
						</td>
-->						
						<td width="5%" nowrap>
							${cohortDefinition.creator}
						</td>
						<td width="5%" nowrap>
							<rpt:timespan then="${cohortDefinition.dateCreated}"/>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>

	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>