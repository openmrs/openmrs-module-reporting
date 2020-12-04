<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require anyPrivilege="Manage Cohort Definitions,Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
		definitionTable = $j('.definition-table').dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false,
			"aoColumns": [
		                    null,
			                null,
			                null,
			                null,
			                {"bSortable": false}
		                ],
			"fnDrawCallback": function() {
				<c:forEach items="${allDefinitions}" var="definition" varStatus="status">
					$j("#preview-${definition.uuid}").click(function(event){
						showReportingDialog({ 
							title: 'Preview', 
							url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${definition.uuid}&type=${definition['class'].name}',
							successCallback: function() { 
								window.location = window.location;
							} 
						});
					});
				</c:forEach>
			}
		});

		<c:forEach items="${definitions}" var="entry">
			$j("#typeLink${entry.key.simpleName}").click(function(event){
				definitionTable.fnFilter('<rpt:displayLabel type="${entry.key.name}"/>', 1);
				$j('.typeRow').css('background-color', 'white');
				$j('#typeRow${entry.key.simpleName}').css('background-color', '#E6E6E6');
			});
		</c:forEach>
		$j("#typeLinkAll").click(function(event){
			definitionTable.fnFilter('', 1);
			$j('.typeRow').css('background-color', 'white');
			$j('#typeRowAll').css('background-color', '#E6E6E6');
		});
		$j('#typeRowAll').css('background-color', '#E6E6E6');
	});

	function confirmDelete(uuid, type) {
		if (confirm("Are you sure you want to delete it?")) {
			document.location.href = 'purgeDefinition.form?uuid='+uuid+'&type='+type;
		}
	}
</script>

<div id="page">

	<div id="container">

		<h1><spring:message code="reporting.${type.simpleName}"/></h1>
	
		<c:if test="${!empty allTypes}">
			<ul id="menu">
				<c:forEach items="${allTypes}" var="allType" varStatus="allTypeStatus">
					<li class="<c:if test="${status.index == 0}">first</c:if> <c:if test="${type == allType}">active</c:if>">
						<a href="${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=${allType.name}">
							<spring:message code="reporting.${allType.simpleName}"/>
						</a>
					</li>
				</c:forEach>
			</ul>		
		</c:if>
		
		<table>
			<tr>
				<td style="text-align:center; vertical-align: top;">
					<table style="border: 1px solid black; margin-bottom:10px;" id="summaryTable">
						<tr id="typeRowAll" class="typeRow">
							<td colspan="2" style="white-space:nowrap; border-bottom:3px solid black;">
								<a href="#" id="typeLinkAll" class="typeLink">
									<strong><spring:message code="Total"/> (${fn:length(allDefinitions)})</strong>
								</a>
							</td>
						</tr>
						<c:forEach items="${definitions}" var="entry">
							<tr id="typeRow${entry.key.simpleName}" class="typeRow" style="white-space:nowrap;">
								<td style="white-space:nowrap; border-bottom:1px solid black;">
									<a href="#" id="typeLink${entry.key.simpleName}" class="typeLink">
										<rpt:displayLabel type="${entry.key.name}"/> (${fn:length(entry.value)})
									</a>
								</td>
								<td style="white-space:nowrap; border-bottom:1px solid black;">
									<a href="editDefinition.form?type=${entry.key.name}">[+]</a>
								</td>
							</tr>
						</c:forEach>
					</table>
				</td>
				<td style="vertical-align:top; width:100%;">
					<table class="definition-table display" >
						<thead>
							<tr>
								<th><spring:message code="reporting.name" /></th>
								<th><spring:message code="reporting.type" /></th>
								<th><spring:message code="reporting.createdBy" /></th>
								<th><spring:message code="reporting.created" /></th>
								<th align="center" width="1%"><spring:message code="reporting.actions" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${definitions}" var="entry" varStatus="entryStatus">
								<c:forEach items="${entry.value}" var="definition" varStatus="definitionStatus">
									<tr class="definitionRow">
										<td><c:out value="${definition.name}"/></td>
										<td><rpt:displayLabel type="${entry.key.name}"/></td>
										<td width="5%" nowrap="nowrap">
											<c:out value="${definition.creator}"/>
										</td>
										<td width="5%" nowrap="nowrap">
										<openmrs:formatDate date="${definition.dateCreated}" format="yyyy-MM-dd HH:mm:ss" />	
										</td>
										<td align="left" nowrap="nowrap">
											&nbsp;
											<a href="editDefinition.form?uuid=${definition.uuid}&type=${definition['class'].name}"><img src="<c:url value='/images/edit.gif'/>" border="0"/></a>
											&nbsp;
											<a href="javascript:confirmDelete('${definition.uuid}','${type.name}');"><img src="<c:url value='/images/trash.gif'/>" border="0"/></a>
											&nbsp;
											<a href="javascript:void(0);" id="preview-${definition.uuid}"><img src="<c:url value='/images/play.gif'/>" border="0"/></a>
										</td>
									</tr>
								</c:forEach>
							</c:forEach>	
						</tbody>
					</table>
				</td>
			</tr>
		</table>
	</div>
</div>

<!-- RPT-371: Simple fix for footer creep issue -->
<div style="clear:both"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
