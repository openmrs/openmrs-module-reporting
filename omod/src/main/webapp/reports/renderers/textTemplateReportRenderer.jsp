<%@ include file="../../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />

<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/reportDesigns/codemirror.js"></script>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {

    	$j('#cancelButton').click(function(event){
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


		$j("#textTemplate").tabs( {
			selected: 0,
			select: function( event, ui ) {
				if ( ui.tab.id == "previewLink" ) {
					var reportInput = $j( '#reportDesignForm input#reportDefinition' ).val() || $j( '#reportDesignForm select#reportDefinition option:selected' ).val();
					if ( reportInput.length ) {
		        		$j( "#templateContents" ).val( editor.getCode() );
		        		var $form = $j( "#reportDesignForm" );
		        		$form.attr( "target", "previewFrame" );
		        		$form.attr( "method", "GET" );
		        		$form.attr( "action", '<c:url value="/module/reporting/reports/renderers/previewTextTemplateReportRenderer.form" />')
		    			$form.submit();
		    			
		    			$form.attr( "target", "");
		    			$form.attr( "method", "POST" );
		    			$form.attr( "action", "${pageContext.request.contextPath}/module/reporting/reports/renderers/saveTextTemplateReportRendererDesign.form" );
		    			return true;
		            }
		            
		        	alert( '<spring:message code="reporting.PreviewTextTemplateRenderer.reportDefinitionMsg"/>' );
		        	return false; 
				} else if ( ui.tab.id == "editLink" ) {

					// enable the Report Definition select again
					var reportDefSelect = $j( "#reportDesignForm select#reportDefinition" );
					if ( reportDefSelect.length ) {
						reportDefSelect.prev().hide();
						reportDefSelect.show();
					}

					// clear the frame
					$j("#previewFrame").attr("src", "about:blank");
				}
			}
        } );

  	});

</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
	.buttonsContainer { width:100%; text-align:left; }
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveTextTemplateReportRendererDesign.form">
	<input type="hidden" id="uuid" name="uuid" value="${design.uuid}" />
  	<input type="hidden" name="successUrl" value="${successUrl}"/>
  	<input type="hidden" name="rendererType" value="${design.rendererType.name}"/>
  	<input type="hidden" name="iframe" value="true" />
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
          				<span style="color:navy; display:hide"></span>
	            		<wgt:widget id="reportDefinition" name="reportDefinition" object="${design}" property="reportDefinition" />
          			</c:otherwise>
        		</c:choose>    
        		<br/>        
      		</td>
      		<td align="left" valign="top" style="padding-left:15px; width:100%">
      			<div id="textTemplate">
					<ul>
						<li><a id="editLink" href="#edit"><spring:message code="reporting.TextTemplateRenderer.edit" /></a></li>
						<li><a id="previewLink" href="#preview"><spring:message code="reporting.TextTemplateRenderer.preview" /></a></li>
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
					<div id="preview">
						<iframe id="previewFrame" name="previewFrame" style="width: 99%; height: 400px"></iframe>
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
