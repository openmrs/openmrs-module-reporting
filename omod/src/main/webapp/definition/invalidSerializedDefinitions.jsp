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
		<table class="reporting-data-table display">
			<thead>
				<tr>
					<th><spring:message code="reporting.name" /></th>
					<th><spring:message code="reporting.createdBy" /></th>
					<th><spring:message code="reporting.created" /></th>
					<th align="center" width="1%"><spring:message code="reporting.actions" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${serializedDefinitions}" var="e" varStatus="status">
					<c:set var="editUrl" value="editInvalidSerializedDefinition.form?uuid=${e.key.uuid}&type=${e.key['class'].name}"/>
					<tr>
						<td><a href="${editUrl}">${e.key.name}</a></td>
						<td width="5%" style="white-space:nowrap;">${e.key.creator}</td>
						<td width="5%" style="white-space:nowrap;">
							<rpt:timespan then="${e.key.dateCreated}"/>
						</td>
						<td align="left" style="white-space:nowrap;">
							<c:if test="${!empty e.value}">
								&nbsp;
								<input type="button" value="Fix Automatically" onclick="document.location.href='convertDefinition.form?uuid=${e.key.uuid}&converter=${e.value.name}';"/>
							</c:if>
							&nbsp;
							<input type="button" value="Fix Manually" onclick="document.location.href='${editUrl}';"/>
							&nbsp;
							<input type="button" value="Delete" onclick="confirmDelete('${e.key.name}','${e.key.uuid}');"/>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>

	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
