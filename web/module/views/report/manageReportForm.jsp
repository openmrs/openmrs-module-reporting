<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/admin/reports/manageReport.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/dwr/interface/DWRCohortService.js" />
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.2.6.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jquery.ui-1.5/ui/packed/ui.core.packed.js" />
<openmrs:htmlInclude file="/scripts/jquery/jquery.ui-1.5/ui/packed/ui.tabs.packed.js" />
<openmrs:htmlInclude file="/scripts/jquery/jquery.ui-1.5/ui/packed/ui.draggable.packed.js" />
<openmrs:htmlInclude file="/scripts/jquery/jquery.ui-1.5/themes/flora/flora.tabs.css" />

<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/scripts/easyAjax.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRReportService.js" />

<script type="text/javascript">

	var $j = jQuery.noConflict();
	
	$j(document).ready(function() {
		$j("#shortcuts").draggable();
		setResizingTextArea(".rowQuery", 1, 60, 5, 60);
		$j("#reportTabs > ul").tabs();
 	});
 	
 	function setResizingTextArea(el, blurRows, blurCols, focusRows, focusCols) {
 		$j(el)
 			.each(function() { $(this).rows = blurRows; $(this).cols = blurCols; })
 			.focus(function() { $(this).rows = focusRows; $(this).cols = focusCols; })
 			.blur(function() { $(this).rows = blurRows; $(this).cols = blurCols; });
 	} 
	
	function deleteTableRow(tableRow) {
		tableRow.parentNode.removeChild(tableRow);
	}
	
	function addAnotherParameter() {
		var row = document.createElement("tr");
		var cell = document.createElement("td");
		var input = document.createElement("input");
		var toFocus = input;
		input.setAttribute("type", "text");
		input.setAttribute("name", "parameterName");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("size", "40");
		input.setAttribute("name", "parameterLabel");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		var sel = document.createElement("select");
		sel.setAttribute("name", "parameterClass");
		var opt = document.createElement("option");
		sel.appendChild(opt);
		<c:forEach var="clazz" items="${parameterClasses}">
			opt = document.createElement("option");
			<c:if test="${param.clazz == clazz}">
				opt.setAttribute("selected", "true");
			</c:if>
			opt.setAttribute("value", "${clazz.name}");
			opt.innerHTML = "${clazz.simpleName}";
			sel.appendChild(opt);
		</c:forEach>
		cell.appendChild(sel);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		var link = document.createElement("span");
		link.className = "voidButton";
		link.setAttribute("onClick", "deleteTableRow(this.parentNode.parentNode)");
		link.innerHTML = "X";
		cell.appendChild(link); 
		row.appendChild(cell);
		
		document.getElementById("parametersTable").appendChild(row);
		toFocus.focus();
	}
	
	function addAnotherRow() {
		var row = document.createElement("tr");
		row.setAttribute("valign", "top");
		var cell = document.createElement("td");
		var input = document.createElement("input");
		var toFocus = input;
		input.setAttribute("type", "text");
		input.setAttribute("name", "rowName");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("size", "40");
		input.setAttribute("name", "rowDescription");
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		input = document.createElement("textarea");
		input.setAttribute("class", "rowQuery");
		input.setAttribute("name", "rowQuery");
		input.setAttribute("onFocus", "showShortcuts(this)");
		setResizingTextArea(input, 1, 60, 5, 60);
		cell.appendChild(input);
		row.appendChild(cell);
		
		cell = document.createElement("td");
		var link = document.createElement("span");
		link.className = "voidButton";
		link.setAttribute("onClick", "deleteTableRow(this.parentNode.parentNode)");
		link.innerHTML = "X";
		cell.appendChild(link); 
		row.appendChild(cell);
		
		document.getElementById("rowsTable").appendChild(row);
		toFocus.focus();
	}
	
	var shortcutTarget = null;
	
	function showShortcuts(target) {
		shortcutTarget = target;
	}
	
	function handleShortcut(text) {
		if (shortcutTarget != null) {
			shortcutTarget.focus();
			if (shortcutTarget.value != '')
				shortcutTarget.value += ' ';
			shortcutTarget.value += text;
		}
	}
	
	function testQuery(button) {
		$j(button.parentNode.parentNode).find("textarea").each(
			function() {
				var el = document.getElementById('cohortResult');
				cohort_setPatientIds(null);
				showDiv('cohortResult');
				DWRCohortService.evaluateCohortDefinition($(this).value, null, showCohortResult);
			}
		);
	}
	
	function showCohortResult(cohort) {
		var el = document.getElementById('cohortResult');
		cohort_setPatientIds(cohort.commaSeparatedPatientIds);
		showDiv('cohortResult');
	}

	function highlightLinkAndShowSection(id) {
		hideAll();
		$j("#sectionLink"+id).css("background-color","#CCCCCC");
		$j("#editReport"+id).show();
	}
	function hideAllSections() {
		$j(".editReportSection").hide();
		$j(".sectionLink").css("background-color","");
	}

	$j(document).ready(function(){
		$j("#SaveDetailsButton").click( function() {
			DWRReportService.getReportSchema('${reportSchema.reportSchemaId}', 
					{ 	callback:function(rptSchema) {
							alert('Found'+rptSchema.name);
						},
						errorHandler:function(errorString, exception) {
							alert(errorString);
						}
					}
			);
		});
	});

</script>
<h2>
	<c:if test="${empty reportSchema.reportSchemaId}"><spring:message code="Report.new" /></c:if>
	<c:if test="${!empty reportSchema.reportSchemaId}"><spring:message code="Report.edit" />: ${reportSchema.name}</c:if>
</h2>

<spring:hasBindErrors name="reportSchema">
	<spring:message code="fix.error"/>
</spring:hasBindErrors>
<br/>


<div id="reportTabs">
	<ul>
		<li><a href="#reportDetailsTab"><span style="color:white;"><spring:message code="Report.details" /></span></a></li>
		<c:if test="${!empty reportSchema.reportSchemaId}">
			<li><a href="#reportParametersTab"><span style="color:white;"><spring:message code="Report.parameters" /></span></a></li>
			<li><a href="#reportDataTab"><span style="color:white;"><spring:message code="Report.data" /></span></a></li>
			<openmrs:extensionPoint pointId="org.openmrs.report.cohortReportFormTab" type="html">
				<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
					<li>
						<a href="#reportExtensionTab${extension.tabId}">
							<span style="color:white;"><spring:message code="${extension.tabName}"/></span>
						</a>
					</li>
				</openmrs:hasPrivilege>
			</openmrs:extensionPoint>
		</c:if>
	</ul>

	<div id="reportDetailsTab">
		<span style="color:blue;">
			<c:if test="${empty reportSchema.reportSchemaId}"><spring:message code="Report.cohortReport.help.newDetails" /></c:if>
			<c:if test="${!empty reportSchema.reportSchemaId}"><spring:message code="Report.cohortReport.help.existingDetails" /></c:if>
		</span><br/><br/>
		<table>
			<tr>
				<th><spring:message code="Report.id" /></th>
				<td>
					<c:choose>
						<c:when test="${empty reportSchema.reportSchemaId}">(<spring:message code="Report.new" />)</c:when>
						<c:otherwise>${reportSchema.reportSchemaId}</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th><spring:message code="Report.name" /></th>
				<td><input type="text" size="40" name="reportName" value="${reportSchema.name}"/></td>
			</tr>
			<tr valign="top">
				<th><spring:message code="Report.description" /></th>
				<td><textarea rows="3" cols="60" name="reportDescription">${reportSchema.description}</textarea></td>
			</tr>
		</table>
		<br/>
		<input type="button" id="SaveDetailsButton" name="SaveDetailsButton" value="<spring:message code="general.save"/>"/>
	</div>
	<c:if test="${!empty reportSchema.reportSchemaId}">
		<div id="reportParametersTab">
			<span style="color:blue;"><spring:message code="Report.cohortReport.help.parameters" /></span><br/><br/>
			<table id="parametersTable">
				<tr>
					<th><spring:message code="Report.parameter.name" /></th>
					<th><spring:message code="Report.parameter.label" /></th>
					<th><spring:message code="Report.parameter.type" /></th>
				</tr>
				<c:forEach var="parameter" items="${reportSchema.reportParameters}">
					<tr>
						<td><input type="text" name="parameterName" value="${parameter.name}"/></td>
						<td><input type="text" size="40" name="parameterLabel" value="${parameter.label}"/></td>
						<td>
							<select name="parameterClass">
								<option value=""></option>
								<c:forEach var="clazz" items="${parameterClasses}">
									<option <c:if test="${parameter.clazz == clazz}">selected="true"</c:if> value="${clazz.name}">
										${clazz.simpleName}
									</option>
								</c:forEach>
							</select>
						</td>
						<td>
							<span class="voidButton" onClick="deleteTableRow(this.parentNode.parentNode)">X</span>
						</td>
					</tr>
				</c:forEach>
			</table>
			<a onClick="addAnotherParameter()"><spring:message code="Report.parameter.add" /></a>
			<br/><br/>
			<input type="submit" value="<spring:message code="general.save"/>"/>
		</div>

		<div id="reportDataTab">
			<table>
				<tr>
					<td valign="top">
						<table>
							<tr><th><spring:message code="Report.data"/></th>
							<c:forEach var="dsd" items="${reportSchema.dataSetDefinitions}">
								<tr><td class="sectionLink" id="sectionLinkDataSetDefinition${reportSchema.reportSchemaId}-${dsd}">
									<a href="javascript:highlightLinkAndShowSection('DataSetDefinition${reportSchema.reportSchemaId}-${dsd}');">
										TBD
									</a>
								</td></tr>
							</c:forEach>
							<tr><td class="sectionLink" id="sectionLink">
								<a href="javascript:highlightLinkAndShowSection('');">
									<spring:message code="Report.dataSetDefinition.add.title" />
								</a>
							</td></tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</c:if>
	<c:if test="${!empty reportSchema.reportSchemaId}">	
		<openmrs:extensionPoint pointId="org.openmrs.report.cohortReportFormTab" type="html">
			<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
				<div id="reportExtensionTab${extension.tabId}">
					<c:choose>
						<c:when test="${extension.portletUrl == '' || extension.portletUrl == null}">
							portletId is null: '${extension.extensionId}'
						</c:when>
						<c:otherwise>
							<openmrs:portlet url="${extension.portletUrl}" id="${extension.tabId}" moduleId="${extension.moduleId}" parameters="reportSchemaId=${reportSchema.reportSchemaId}" />
						</c:otherwise>
					</c:choose>
				</div>
			</openmrs:hasPrivilege>
		</openmrs:extensionPoint>
	</c:if>
</div>
<br/>

<%@ include file="/WEB-INF/template/footer.jsp" %>