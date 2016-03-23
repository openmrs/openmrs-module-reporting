<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require anyPrivilege="Manage Cohort Definitions,Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/reportDesigns/codemirror.js"></script>

<c:url value="/module/reporting/definition/scriptedCompositionPatientDataDefinition.form" var="pageUrlWithUuid">
	<c:param name="uuid" value="${definition.uuid}" />
</c:url>

<c:set var="pageUrl" value="/module/reporting/definition/scriptedCompositionPatientDataDefinition.form?uuid=uuid"/>

<c:choose>
	<c:when test="${definition.id == null}">

		<b class="boxHeader"><spring:message code="reporting.createComposition" /></b>
		<div class="box">
			<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.data.patient.definition.ScriptedCompositionPatientDataDefinition|size=380|mode=edit|dialog=false|cancelUrl=${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition|successUrl=${pageUrl}" />
		</div>
		
	</c:when>		
	<c:otherwise>
	
	<script>
			$j(document).ready(function() {
				makeDialog('saveAsDialog');
				$j('#saveAsButton').click(function(event) {
					showDialog('saveAsDialog', 'Save a Copy');
				});

				var editor = CodeMirror.fromTextArea('scriptCode', {
					height: "350px",
					parserfile: ["tokenizejavascript.js", "parsejavascript.js"],
					stylesheet: "${pageContext.request.contextPath}/moduleResources/reporting/css/reportDesigns/jscolors.css",
					path: "${pageContext.request.contextPath}/moduleResources/reporting/scripts/reportDesigns/",
					continuousScanning: 500,
					lineNumbers: true,
					textWrapping: false,
					autoMatchParens: true,
					tabMode: "spaces"
				});
			});
		</script>

		<table width="100%"><tr>
		<td width="34%" style="padding:5px; vertical-align: top;">
	
			<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Basic Details" />
			
			<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Parameters|parentUrl=${pageUrl}" />
			
			<br/>

			<b class="boxHeader"><spring:message code="reporting.definitionsToCombine" /></b>
			<div class="box" style="border: none">
			
				<c:forEach items="${definition.containedDataDefinitions}" var="h" varStatus="hStatus">
					<openmrs:portlet url="mappedProperty" id="definition${hStatus.index}" moduleId="reporting" 
						parameters="type=${definition['class'].name}|uuid=${definition.uuid}|property=containedDataDefinitions|currentKey=${h.key}|label=${h.key}|parentUrl=${pageUrlWithUuid}" />
				</c:forEach>
			
				<openmrs:portlet url="mappedProperty" id="newDefinition" moduleId="reporting" 
					 parameters="type=${definition['class'].name}|uuid=${definition.uuid}|property=containedDataDefinitions|mode=add|label=Add Definitions to Combine" />
			</div>		
		
		</td>
		<td width="66%" style="padding:5px; vertical-align: top;">
		<b class="boxHeader">
				<spring:message code="reporting.ScriptedCohortDefinition.scriptType" />
			</b>		
			<div class="box">
				<form method="post" action="scriptedCompositionPatientDataDefinitionSetComposition.form">
					<input type="hidden" name="uuid" value="${definition.uuid}"/>					
			<select id="scriptType" name="scriptType">
				 <option value="" <c:if test="${definition.scriptType == null}">selected</c:if>><spring:message code="reporting.choose" /></option>
						<c:forEach var="type" items="${scriptTypes}">
							<option value="${type}" <c:if test="${definition.scriptType.language == type}">selected</c:if>>
							   ${type}
				            </option>
						</c:forEach>
			</select>	
					<br/>
					<textarea id="scriptCode" name="scriptCode" cols="140" rows="25">${definition.scriptCode}</textarea>
					<br/>
					<input type="submit" value="Save"/>
					<input type="button" value="Close" onClick="window.location='/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition';"/>
					
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					
					<input type="button" id="saveAsButton" value="Save as new"/>
				</form>
			</div>			
		</td>
		</tr></table>
		
		<div id="saveAsDialog" style="display:none">
			<form method="get" action="scriptedCompositionPatientDataDefinitionClone.form">
				<input type="hidden" name="copyFromUuid" value="${definition.uuid}"/>
				<table>
					<tr>
						<th align="right"><spring:message code="reporting.name" /></th>
						<td><input type="text" name="name"/></td>
					</tr>
					<tr valign="top">
						<th align="right"><spring:message code="reporting.description" /></th>
						<td><textarea name="description"></textarea></td>
					</tr>
					<tr>
						<td></td>
						<td><input type="submit" value="Save As"/></td>
					</tr>
				</table>
			</form>
		</div>

	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>
