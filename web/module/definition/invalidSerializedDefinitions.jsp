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
			document.location.href = 'purgeSerializedDefinition.form?uuid='+uuid;
		}
	}
</script>

<div id="page">

	<div id="container">
	
		<h1>Invalid Serialized Definitions of Type: ${type.simpleName}</h1>
		
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
				<c:forEach items="${serializedDefinitions}" var="serializedDefinition" varStatus="status">
					<c:set var="editUrl" value="editInvalidSerializedDefinition.form?uuid=${serializedDefinition.uuid}&type=${serializedDefinition.class.name}"/>
					<tr>
						<td align="left" style="white-space:nowrap;">
							&nbsp;
							<a href="${editUrl}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
							&nbsp;
							<a href="javascript:confirmDelete('${serializedDefinition.name}','${serializedDefinition.uuid}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
						</td>
						<td><a href="${editUrl}">${serializedDefinition.name}</a></td>
						<td>${serializedDefinition.description}</td>
						<td width="5%" style="white-space:nowrap;">${serializedDefinition.creator}</td>
						<td width="5%" style="white-space:nowrap;">
							<rpt:timespan then="${serializedDefinition.dateCreated}"/>
						</td>
					</tr>
				</c:forEach>	
			</tbody>
		</table>

	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>