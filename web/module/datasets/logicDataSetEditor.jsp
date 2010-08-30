<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage DataSet Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />

<script>
	$(document).ready(function() {
		var tokens = [ <c:forEach var="token" items="${tokens}" varStatus="status">
			"<spring:message javaScriptEscape='true' text='${token}'/>" <c:if test='${not status.last}'>,</c:if>
		</c:forEach> ];
		$('#accordion').accordion({
			active: <c:choose><c:when test="${definition.uuid==null}">0</c:when><c:otherwise>1</c:otherwise></c:choose>
		});
		$('#sortable tbody.sortable-content').sortable();
		$('#sortable tbody.sortable-content').disableSelection();
		$('.suggestTokens').autocomplete(tokens);
	});
</script>

<div id="page">
	<div id="container">
		<h1>
			<c:choose>
				<c:when test="${definition.uuid == null}">
					Create New Logic DataSet
				</c:when>
				<c:otherwise>
					Edit Logic DataSet: ${definition.name}
				</c:otherwise>
			</c:choose>
		</h1>
		
		<form method="post" action="logicDataSetEditorSave.form">
			<input type="hidden" name="uuid" value="${definition.uuid}"/>
			<div id="accordion">
				<h3><a href="#">Name and Description</a></h3>
				<div>
					<table>
						<tr valign="top">
							<th>Name</th>
							<td><input type="text" name="name"
									value="<spring:message javaScriptEscape='true' text='${definition.name}' />"/></td>
						</tr>
						<tr valign="top">
							<th>Description</th>
							<td><textarea name="description"><spring:message javaScriptEscape='true' text='${definition.description}' /></textarea></td>
						</tr>
					</table>				
				</div>
	
				<h3><a href="#">Columns</a></h3>
				<div>
					<table id="sortable">
						<thead>
							<tr>
								<th></th>
								<th>Name</th>
								<th>Label</th>
								<th>Logic</th>
							</tr>
						</thead>
						<tbody class="sortable-content">
							<c:forEach var="col" items="${definition.columns}">
								<tr>
									<td><span class="ui-icon ui-icon-arrowthick-2-n-s"></span></td>
									<td><input type="text" name="columnName" value="<spring:message javaScriptEscape='true' text='${col.name}'/>"/></td>
									<td><input type="text" name="columnLabel" value="<spring:message javaScriptEscape='true' text='${col.label}'/>"/></td>
									<td><input class="suggestTokens" type="text" name="columnLogic" value="<spring:message javaScriptEscape='true' text='${col.logic}'/>" size="50"/></td>
								</tr>
							</c:forEach>
							<c:forEach begin="1" end="5">
								<tr>
									<td><span class="ui-icon ui-icon-arrowthick-2-n-s"></span></td>
									<td><input type="text" name="columnName" /></td>
									<td><input type="text" name="columnLabel" /></td>
									<td><input class="suggestTokens" type="text" name="columnLogic" size="50"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<input type="submit" value="<spring:message code='general.save'/>"/>
		</form>
	</div>
</div>