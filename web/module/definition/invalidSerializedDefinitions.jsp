<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/definition/invalidSerializedDefinitions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	function confirmDelete(name, uuid) {
		if (confirm("Are you sure you want to delete " + name + "?")) {
			document.location.href = 'purgeSerializedDefinition.form?uuid='+uuid;
		}
	}
</script>

<div id="page">
	<div id="container">	
		<table class="reporting-data-table display" style="size:smaller;">
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
				<c:forEach items="${serializedDefinitions}" var="e" varStatus="status">
					<c:set var="editUrl" value="editInvalidSerializedDefinition.form?uuid=${e.key.uuid}&type=${e.key.class.name}"/>
					<tr>
						<td align="left" style="white-space:nowrap;">
							<c:if test="${!empty e.value}">
								&nbsp;
								<a href="convertDefinition.form?uuid=${e.key.uuid}&converter=${e.value.name}"><img src="<c:url value='/images/play.gif'/>" border="0"/></a>
							</c:if>
							&nbsp;
							<a href="${editUrl}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:confirmDelete('${e.key.name}','${e.key.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
						</td>
						<td><a href="${editUrl}">${e.key.name}</a></td>
						<td>${e.key.description}</td>
						<td width="5%" style="white-space:nowrap;">${e.key.creator}</td>
						<td width="5%" style="white-space:nowrap;">
							<rpt:timespan then="${e.key.dateCreated}"/>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>

	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>