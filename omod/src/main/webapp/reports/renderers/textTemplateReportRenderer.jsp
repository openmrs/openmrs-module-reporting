<%@ include file="../../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />

<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/reportDesigns/codemirror.js"></script>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

    	$('#cancelButton').click(function(event){
    		document.location.href = '${pageContext.request.contextPath}${cancelUrl}';
    	});
    	
    	var editor = CodeMirror.fromTextArea('templateContents', {
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
    	
    	$("#textTemplate").tabs();

  	});

</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
	.buttonsContainer { width:100%; text-align:left; }
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveTextTemplateReportRendererDesign.form">
	<input type="hidden" name="uuid" value="${design.uuid}" />
  	<input type="hidden" name="successUrl" value="${successUrl}"/>
  	<input type="hidden" name="rendererType" value="${design.rendererType.name}"/>
  	<h2>
  		<spring:message code="reporting.${design.rendererType.simpleName}.title"/>
  	</h1>
  	<table class="formTable" padding="5">
    	<tr>
      		<td valign="top" align="left">
        		<span class="metadataField"><spring:message code="reporting.reportDesign.name"/></span>
        		<wgt:widget id="name" name="name" object="${design}" property="name" attributes="size=50"/>
        		<br/>
        		<span class="metadataField"><spring:message code="reporting.reportDesign.description"/></span>      
        		<wgt:widget id="description" name="description" object="${design}" property="description" attributes="cols=38|rows=2"/>
        		<br/>
        		<span class="metadataField"><spring:message code="reporting.reportDesign.reportDefinition"/></span>
        		<c:choose>
          			<c:when test="${!empty reportDefinitionUuid}">
            			<span style="color:navy;">${design.reportDefinition.name}</span>
            			<input type="hidden" name="reportDefinition" value="${reportDefinitionUuid}"/>
          			</c:when>
          			<c:otherwise>
	            		<wgt:widget id="reportDefinition" name="reportDefinition" object="${design}" property="reportDefinition" />
          			</c:otherwise>
        		</c:choose>    
        		<br/>        
      		</td>
      		<td align="left" valign="top" style="padding-left:15px; width:100%">
      			<div id="textTemplate">
					<ul>
						<li><a id="editLink" href="#edit"><spring:message code="reporting.TextTemplateRenderer.edit" /></a></li>
					</ul>
						
					<div id="edit" style="font-size:small;">
						<spring:message code="reporting.TextTemplateRenderer.scriptType" />:
						<select name="scriptType">
							<c:forEach var="type" items="${scriptTypes}">
								<c:set var="isSelected" value="${scriptType eq type ? ' selected' : ''}"/>
								<option value="${type}"${isSelected}>${type}</option>
							</c:forEach>
						</select>
						<br/><br/>
						<div id="textarea-container" class="border">
							<textarea id="templateContents" name="script" cols="140" rows="80">${script}</textarea>
						</div>
					</div>
				</div>
      		</td>
    	</tr>
  	</table>
  	<br/>
  	<div class="buttonsContainer">
    	<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
    	<input type="submit" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
  	</div>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
