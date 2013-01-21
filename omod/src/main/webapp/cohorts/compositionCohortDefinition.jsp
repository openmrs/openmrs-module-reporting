<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Cohort Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition" />
<%@ include file="../manage/localHeader.jsp"%>

<c:url value="/module/reporting/cohorts/compositionCohortDefinition.form" var="pageUrlWithUuid">
	<c:param name="uuid" value="${definition.uuid}" />
</c:url>

<c:set var="pageUrl" value="/module/reporting/cohorts/compositionCohortDefinition.form?uuid=uuid"/>

<style>
	textarea#compositionString { 
		width: 99%;
	}
</style>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#previewButton").click(function(event){ 
			showReportingDialog({ 
				title: 'Preview Cohort Query Results', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${definition.uuid}&type=${definition['class'].name}',
				successCallback: function() { 
					window.location = window.location; //.reload(true);
				} 
			});
			//event.preventDefault();			
		});
	});
</script>


<c:choose>
	<c:when test="${definition.id == null}">

		<b class="boxHeader">Create Composition Cohort Definition</b>
		<div class="box">
			<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition|size=380|mode=edit|dialog=false|cancelUrl=${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition|successUrl=${pageUrl}" />
		</div>
		
	</c:when>		
	<c:otherwise>
	
		<script>
			$(document).ready(function() {
				makeDialog('saveAsDialog');
				$('#andAllTogether').click(function(event) {
					$('#compositionString').val(combineAllSearches("AND"));
				});
				$('#orAllTogether').click(function(event) {
					$('#compositionString').val(combineAllSearches("OR"));
				});
				$('#saveAsButton').click(function(event) {
					showDialog('saveAsDialog', 'Save a Copy');
				});
			});

			function combineAllSearches(delimiter) {
				var temp = "";
				<c:forEach items="${definition.searches}" var="h" varStatus="searchesStatus">
					temp += '<spring:message javaScriptEscape="true" text="${h.key}"/>';
					<c:if test="${!searchesStatus.last}">
						temp += ' ' + delimiter + ' ';
					</c:if>
				</c:forEach>
				return temp;
			}
		</script>

		<table width="100%"><tr valign="top">
		<td width="34%">	
	
			<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Basic Details" />
			
			<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Parameters|parentUrl=${pageUrl}" />
			
			<b class="boxHeader">
				Composition string
				&nbsp;&nbsp;&nbsp;&nbsp;
				<small>Shortcuts:</small>
				<a id="andAllTogether" href="javascript:void(0)">
					AND-all
				</a>
				&nbsp;
				<a id="orAllTogether" href="javascript:void(0)">
					OR-all
				</a>
			</b>
			<div class="box">
				<form method="post" action="compositionCohortDefinitionSetComposition.form">
					<input type="hidden" name="uuid" value="${definition.uuid}"/>
					<textarea id="compositionString" rows="6" name="compositionString">${definition.compositionString}</textarea>
					<br/>
				<span>					
					<input type="submit" value="Save"/>
					<input type="button" value="Close" onClick="window.location='/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition';"/>
					<input type="button" id="saveAsButton" value="Save as new"/>
					<c:if test="${!empty definition.uuid}">
					    <input type="button" id="previewButton" value="Preview"/>
					</c:if>    
				</span>
					
				</form>
			</div>			
		
		</td>
		<td width="66%">
			<b class="boxHeader">Searches to combine</b>
			<div class="box" style="border: none">
			
				<c:forEach items="${definition.searches}" var="h" varStatus="hStatus">
					<openmrs:portlet url="mappedProperty" id="search${hStatus.index}" moduleId="reporting" 
						parameters="type=${definition['class'].name}|uuid=${definition.uuid}|property=searches|currentKey=${h.key}|label=${h.key}|parentUrl=${pageUrlWithUuid}" />
				</c:forEach>
			
				<openmrs:portlet url="mappedProperty" id="newSearch" moduleId="reporting" 
					 parameters="type=${definition['class'].name}|uuid=${definition.uuid}|property=searches|mode=add|label=Add Search to Combine" />
			</div>
		</td>
		</tr></table>
		
		<div id="saveAsDialog" style="display:none">
			<form method="get" action="compositionCohortDefinitionClone.form">
				<input type="hidden" name="copyFromUuid" value="${definition.uuid}"/>
				<table>
					<tr>
						<th align="right">Name:</th>
						<td><input type="text" name="name"/></td>
					</tr>
					<tr valign="top">
						<th align="right">Description:</th>
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