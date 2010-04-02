<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Cohort Definitions" otherwise="/login.htm" redirect="/module/reporting/cohorts/manageCohortDefinitions.form" />
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
			"fnDrawCallback": function() {
				<c:forEach items="${cohortDefinitions}" var="cohortDefinition" varStatus="status">
					$("#cohort-${cohortDefinition.uuid}").click(function(event){ 
						showReportingDialog({ 
							title: 'Preview Cohort Definition', 
							url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}',
							successCallback: function() { 
								window.location = window.location; //.reload(true);
							} 
						});
					});
				</c:forEach>
			}
		});

		$("#createButton").click(function(event){ 
			document.location.href = $("#createTypeSelector").val();
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
	
		<h1>Cohort Queries</h1>
	
		<spring:message code="reporting.manage.createNew"/>:
		<select name="type" id="createTypeSelector">
			<option value="">&nbsp;</option>
			<c:forEach items="${types}" var="type">
				<c:choose>
					<c:when test="${customPages[type] == null}">
						<option value="editCohortDefinition.form?type=${type.name}"><rpt:displayLabel type="${type.name}"/></option>
					</c:when>
					<c:otherwise>
						<option value="${customPages[type]}"><rpt:displayLabel type="${type.name}"/></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		<input type="button" id="createButton" value="Create"/>
		
		<br/>
		<table class="reporting-data-table display" >
			<thead>
				<tr>
					<th align="center" width="1%">Actions</th>
					<th>Name</th>
					<th>Type</th>
					<th>Creator</th>
					<th>Created</th>
				</tr>
			</thead>
			<tbody>
				${customPages}
			
				<c:forEach items="${cohortDefinitions}" var="cohortDefinition" varStatus="status">				

					<c:choose>
						<c:when test="${customPages[cohortDefinition.class] == null}">
							<c:set var="editUrl" value="editCohortDefinition.form?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}"/>
						</c:when>
						<c:otherwise>
							<c:set var="editUrl" value="${customPages[cohortDefinition.class]}?uuid=${cohortDefinition.uuid}"/>
						</c:otherwise>
					</c:choose>
				
					<tr>
						<td align="left" nowrap="nowrap">
							&nbsp;
							<a href="${editUrl}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:confirmDelete('${cohortDefinition.name}','${cohortDefinition.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:void(0);" id="cohort-${cohortDefinition.uuid}"><img src="<c:url value='/images/play.gif'/>" border="0"/></a>
						</td>
						<td>
							<a href="${editUrl}">
								${cohortDefinition.name}
							</a>
						</td>
						<td width="10%">
							${cohortDefinition.class.simpleName}
						</td>
						<td width="5%" nowrap="nowrap">
							${cohortDefinition.creator}
						</td>
						<td width="5%" nowrap="nowrap">
							<rpt:timespan then="${cohortDefinition.dateCreated}"/>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>
	</div>
</div>

<!-- RPT-371: Simple fix for footer creep issue -->
<div style="clear:both"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>