<%@ include file="../../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

    	$('#cancelButton').click(function(event){
    		document.location.href = '${pageContext.request.contextPath}${cancelUrl}';
    	});

    	$('#submitButton').click(function(event){
      		$('#reportDesignForm').submit();
    	});

  	});

  	function showResourceChange(element) {
    	$(element).parent().parent().children('.currentResourceSection').hide();
    	$(element).parent().parent().children('.resourceChangeSection').show();
  	}
  	function hideResourceChange(element) {    
		$(element).parent().parent().children('.currentResourceSection').show();    
    	$(element).parent().parent().children('.resourceChangeSection').hide();  
  	}
</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
	.buttonsContainer { width:100%; text-align:left; }
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveNonConfigurableReportRenderer.form">
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
				<span class="metadataField">Resource Files</span>			
				<div id="resourcesMultiFieldDiv">
					<c:forEach items="${design.resources}" var="resource" varStatus="resourceStatus">
						<span class="multiFieldInput" id="resources_${resource.uuid}">
							<span class="fileUploadWidget">
								<span class="currentResourceSection">
									<a href="${pageContext.request.contextPath}/module/reporting/reports/viewReportDesignResource.form?designUuid=${design.uuid}&resourceUuid=${resource.uuid}">${resource.name}.${resource.extension}</a>
									<input type="button" value="Change" onclick="showResourceChange(this);"/>
								</span>
								<span class="resourceChangeSection" style="display:none;">
									<input type="file" name="resources.${resource.uuid}"/>
									<input type="button" value="Cancel" onclick="hideResourceChange(this);"/>
								</span>
							</span>
							<input type="button" value="X" size="1" onclick="removeParentWithClass(this,'multiFieldInput');"/><br/>
						</span>
					</c:forEach>
					<span class="multiFieldInput" id="resourcesTemplate" style="display:none;">
						<span class="fileUploadWidget">
							<span class="currentResourceSection" style="display:none;">
								<input type="button" value="Change" onclick="showResourceChange(this);"/>
							</span>
							<span class="resourceChangeSection;">
								<input type="file" name="resources"/>
							</span>
						</span>
						<input type="button" value="X" size="1" onclick="removeParentWithClass(this,'multiFieldInput');"/><br/>
					</span>
					<input id="resourcesAddButton" type="button" value="+" size="1"/>
					<span id="resourcesCount" style="display:none;">${fn:length(model.design.resources)+1}</span>
				</div>   
      		</td>
    	</tr>
  	</table>
  	<br/>
  	<div class="buttonsContainer">
    	<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
    	<input type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
  	</div>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
