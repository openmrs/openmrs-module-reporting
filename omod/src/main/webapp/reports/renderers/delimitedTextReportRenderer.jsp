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
	
</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
	.delimiterTittle { text-align:left; }
	.delimitersTable, .formTable { margin:0; padding:0; font-size:small; }
	.buttonsContainer { width:100%; text-align:left; }
	.delimitersTable input { width:100px;}
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveDelimitedTextReportDesign.form">
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
      		<td align="left" valign="top" style="padding-left:15px;">
				<span class="metadataField">
					<spring:message code="reporting.${design.rendererType.simpleName}.filenameExtension"/>
				</span>
				<wgt:widget id="filenameExtension" name="filenameExtension" type="java.lang.String" defaultValue="${configurableProperties.filenameExtension}"/>
				<table padding="5" class="delimitersTable">
					<tr>
						<td class="delimiterTittle" colspan="4">
							<span class="metadataField">
								<spring:message code="reporting.${design.rendererType.simpleName}.columnDelimiters"/>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class="metadataField">
								<spring:message code="reporting.${design.rendererType.simpleName}.beforeColumnDelimiter"/>
							</span>
						</td>
						<td>
							<wgt:widget id="beforeColumnDelimiter" name="beforeColumnDelimiter" type="java.lang.String" defaultValue="${configurableProperties.beforeColumnDelimiter}"/>
						</td>
						<td>
							<span class="metadataField">
								<spring:message code="reporting.${design.rendererType.simpleName}.afterColumnDelimiter"/>
							</span>
						</td>
						<td>
							<wgt:widget id="afterColumnDelimiter" name="afterColumnDelimiter" type="java.lang.String" defaultValue="${configurableProperties.afterColumnDelimiter}"/>
						</td>
					</tr>
					<tr>
						<td class="delimiterTittle" colspan="4">
							<span class="metadataField">
								<spring:message code="reporting.${design.rendererType.simpleName}.rowDelimiters"/>
							</span>
						</td>
					</tr>
					<tr>
						<td>
							<span class="metadataField">
								<spring:message code="reporting.${design.rendererType.simpleName}.beforeRowDelimiter"/>
							</span>
						</td>
						<td>
							<wgt:widget id="beforeRowDelimiter" name="beforeRowDelimiter" type="java.lang.String" defaultValue="${configurableProperties.beforeRowDelimiter}"/>
						</td>
						<td>
							<span class="metadataField">
								<spring:message code="reporting.${design.rendererType.simpleName}.afterRowDelimiter"/>
							</span>
						</td>
						<td>
							<wgt:widget id="afterRowDelimiter" name="afterRowDelimiter" type="java.lang.String" defaultValue="${configurableProperties.afterRowDelimiter}"/>
						</td>
					</tr>
				</table>
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
