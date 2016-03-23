<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />

<script>
	$j(document).ready(function() {
		var tokens = [ <c:forEach var="token" items="${tokens}" varStatus="status">
			"<spring:message javaScriptEscape='true' text='${token}'/>" <c:if test='${not status.last}'>,</c:if>
		</c:forEach> ];
		$j('#accordion').accordion({
			active: <c:choose><c:when test="${definition.uuid==null}">0</c:when><c:otherwise>1</c:otherwise></c:choose>
		});
		$j('#sortable tbody.sortable-content').sortable();
		$j('#sortable tbody.sortable-content').disableSelection();
		$j('.suggestTokens').autocomplete(tokens, {
			formatResult: function(row) {
				var data = row[0];
				if (data.indexOf(' ') >= 0)
					return '"' + data + '"';
				else
					return data;
			}
		});
		<c:if test="${!empty definition.id}">
			$j('#previewButton').click(function(event) {
				showReportingDialog({ 
					title: 'Preview ${definition.name}', 
					url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${definition.uuid}&type=${definition['class'].name}'
				});
			}).height(32);
		</c:if>
	});
</script>

<div id="page">
	<div id="container">
		<h1>
			<c:choose>
				<c:when test="${definition.id == null}">
					<spring:message code="reporting.createNewLogicDataSet" />
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
								<th>Format</th>
							</tr>
						</thead>
						<tbody class="sortable-content">
							<c:forEach var="col" items="${definition.columns}">
								<tr>
									<td><span class="ui-icon ui-icon-arrowthick-2-n-s"></span></td>
									<td><input type="text" name="columnName" value="<spring:message htmlEscape='true' text='${col.name}'/>"/></td>
									<td><input type="text" name="columnLabel" value="<spring:message htmlEscape='true' text='${col.label}'/>"/></td>
									<td>
										<input class="suggestTokens" type="text" name="columnLogic" value="<spring:message htmlEscape='true' text='${col.logic}'/>" size="50"/>
										<c:if test="${not empty logicErrors[col]}">
											<br/>
											<span class="error">${logicErrors[col].message}</span>
										</c:if>
									</td>
									<td>
										<select name="columnFormat">
											<option value="">Value</option>
											<option value="date" <c:if test="${col.format == 'date'}">selected="true"</c:if>>Date</option>
											<option value="boolean" <c:if test="${col.format == 'boolean'}">selected="true"</c:if>>Boolean (X)</option>
											<c:forEach items="${conceptNameTags}" var="tag">
												<c:set var="formatValue" value="concept:${tag.tag}"/>
												<option value="${formatValue}" <c:if test="${col.format == formatValue}">selected="true"</c:if>>Concept (${tag.tag})</option>
											</c:forEach>
										</select>
									</td>
								</tr>
							</c:forEach>
							<c:forEach begin="1" end="5">
								<tr>
									<td><span class="ui-icon ui-icon-arrowthick-2-n-s"></span></td>
									<td><input type="text" name="columnName" /></td>
									<td><input type="text" name="columnLabel" /></td>
									<td><input class="suggestTokens" type="text" name="columnLogic" size="50"/></td>
									<td>
										<select name="columnFormat">
											<option value="">Value</option>
											<option value="date">Date</option>
											<option value="boolean">Boolean (X)</option>
											<c:forEach items="${conceptNameTags}" var="tag">
												<option value="concept:${tag.tag}">Concept (${tag.tag})</option>
											</c:forEach>
										</select>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<input type="submit" value="<spring:message code='general.save'/>"/>
		</form>
		<c:if test="${!empty definition.id}">
			<br/>
			<button id="previewButton">
				<img src="<c:url value="/images/play.gif"/>" border="0"/>
				<spring:message code="reporting.preview"/>
			</button>
		</c:if>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
