<%@ include file="../../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {

    	$j('#cancelButton').click(function(event){
    		document.location.href = '${pageContext.request.contextPath}${cancelUrl}';
    	});

    	$j('#submitButton').click(function(event){
      		$j('#reportDesignForm').submit();
    	});
    			
		$j( '#reportDesignForm td.customOutputSection input.addPropertyButton' ).click( function( event ) {
			var $this = $j( this ),
				queryTemplate = 'reportDesignForm td.customOutputSection table.propTemplate tr.' + $this.attr( 'id' ),
				$trBttnContainer = $this.parents( 'tr:first' );
			$newRow = cloneAndInsertBefore( queryTemplate, $trBttnContainer );
			hideOrShowTableHeaders( $trBttnContainer );
		} );
		
		$j( '#reportDesignForm td.customOutputSection input.deleteResource' ).click(function( event ) {
			var $this = $j(this);
			$this.siblings().remove();
			$this.before('<input type="file" name="resource"/>');
		} );
		
		$j( '#reportDesignForm td.customOutputSection input.deletePropertyButton' ).click(function() {
			var $trParent = $j( this ).parents( 'tr:first' ),
				$trBttnContainer = $trParent.siblings( 'tr.addBttnContainer' );
			$trParent.remove();
			hideOrShowTableHeaders( $trBttnContainer );
		} );
		
		$j( '#reportDesignForm' ).submit( function() {
			buildPropertiesStr();			
		} );
		
		<c:if test="${not empty design.resources}">	
		addResourceButtonsEvnt();
		</c:if>
		
		addRadioButtonEvntOutputOption();
		<c:if test="${not empty design.properties.repeatingSections}">	
		setRSProperties( '${design.properties.repeatingSections}' );
		</c:if>
		hideOrShowTableHeaders( $j( '#reportDesignForm td.customOutputSection table.propertiesContainer input#propertiesInputs' ).parents( 'tr:first' ) );
  	});
	
	function addResourceButtonsEvnt() {
		$j( '#reportDesignForm td.customOutputSection input#changeResourceButton' ).click(function() {
			var $parent = $j( findParentWithClass( this, 'currentResourceSection') );
			$parent.hide();
			$parent.siblings( 'span.resourceChangeSection' ).show();
		} );
		
		$j( '#reportDesignForm td.customOutputSection input#cancelResourceButton' ).click(function() {
			var $parent = $j( findParentWithClass( this, 'resourceChangeSection') );
			$parent.hide();
			$parent.siblings( 'span.currentResourceSection' ).show();
		} );
	}
	
	function addRadioButtonEvntOutputOption() {
		var $radioButtons = $j('#reportDesignForm input:radio[name="rendererType"]'),
			$checkedRadioButton = $radioButtons.filter( '[value="${design.rendererType.name}"]' );
		$checkedRadioButton.attr( 'checked', true );
		$radioButtons.change(function() {
			if ( $j( this ).attr( 'value' ) == 'org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer' ) {
				$j( '#reportDesignForm table.formTable td.customOutputSection' ).show();
				$j( '#xlsReportRendererProperties').hide();
			} else {
				$j( '#reportDesignForm table.formTable td.customOutputSection' ).hide();
				$j( '#xlsReportRendererProperties').show();
			}
		} );
		$checkedRadioButton.change();		
	}
	
	function hideOrShowTableHeaders( $referenceElement ) {
		if ( $referenceElement.siblings( 'tr' ).length == 1 ) {
			$referenceElement.prevAll( 'tr.tableHeaders' ).hide();
		} else {
			$referenceElement.prevAll( 'tr.tableHeaders' ).show();
		}
	}
		
	function setRSProperties( RSValue ) {
		var sectionsArr = RSValue.split( '|' ),
			$addButton = $j( "#reportDesignForm td.customOutputSection table.RSContainer input#RSInputs" ),
			$addBttnParent = $j( findParentWithClass( $addButton[ 0 ], 'addBttnContainer' ) );
		for( var i = 0; i < sectionsArr.length; i++ ) {
			$addButton.click();
			var $inputs = $addBttnParent.prev( 'tr.RSInputs' ).find( 'input' ),
				parameters = sectionsArr[ i ].split( ',' );
			for( var j = 0; j < parameters.length; j++ ) {
				var paramName = parameters[j].split( ':' )[ 0 ],
					paramValue = parameters[j].split( ':' )[ 1 ];
				$inputs.filter( "[name='" + paramName + "']" ).val( paramValue );
			}
		}
		hideOrShowTableHeaders( $addButton, 'RSInputs' );
	}
	
	function buildRSStr( propertiesArr ) {
		var $trRSInputs = $j( '#reportDesignForm td.customOutputSection table.RSContainer tr.RSInputs' );
		if ( $trRSInputs.length ) {
			var sections = [];
			$trRSInputs.each( function( index ) {
				var $inputs = $j( this ).find( 'input' ),
					$sheetInput = $inputs.filter( "[name='sheet']" ),
					$rowInput = $inputs.filter( "[name='row']" ),
					$colInput = $inputs.filter( "[name='column']" ),
					$datasetInput = $inputs.filter( "[name='dataset']" ),
					parameters = [];
				
				if ( jQuery.trim( $sheetInput.val() ).length ){
					parameters.push('sheet:' + jQuery.trim( $sheetInput.val() ));
				}
				if ( jQuery.trim( $rowInput.val() ).length ){
					parameters.push('row:' + jQuery.trim( $rowInput.val() ));
				}
				if ( jQuery.trim( $colInput.val() ).length ){
					parameters.push('column:' + jQuery.trim( $colInput.val() ));
				}
				if ( jQuery.trim( $datasetInput.val() ).length ){
					parameters.push('dataset:' + jQuery.trim( $datasetInput.val() ));
				}
				
				if ( parameters.length ) {
					sections.push( parameters.join( ',' ) );
				}
				
			} );
			
			if ( sections.length ) {
				propertiesArr.push( 'repeatingSections=' + sections.join( '|' ) );
			}
		}
	}
	
	function buildKeyValuePropertiesStr( propertiesArr ) {
		var $trRSInputs = $j( '#reportDesignForm td.customOutputSection table.propertiesContainer tr.propertiesInputs' );
		if ( $trRSInputs.length ) {
			$trRSInputs.each( function() {
				var $inputs = $j( this ).find( 'input' ),
					$key = $inputs.filter( "[name='key']" ),
					$value = $inputs.filter( "[name='value']" );
				if (  jQuery.trim( $key.val() ).length &&  jQuery.trim( $value.val() ).length ) {
					propertiesArr.push( jQuery.trim( $key.val() ) + '=' + jQuery.trim( $value.val() ) );
				}
			} ); 
		}
	}
	
	function buildPropertiesStr() {
		var propertiesStr = [];
		buildRSStr( propertiesStr );
		buildKeyValuePropertiesStr( propertiesStr );
		if ( propertiesStr.length ) {
			$j( '#reportDesignForm td.customOutputSection input#properties' ).val( propertiesStr.join( '\\n' ) );
		}
	}	
  	
</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
	.buttonsContainer { width:100%; text-align:left; }
	.RSContainer, .propertiesContainer, .expressionsTable { margin:0; padding:0; font-size:small; }
	.customOutputSection { padding-left:15px; }
	.propTemplate, .tableHeaders { display:none;}
	.description { padding:0 0 10px 0; border:none; color:#222; display:block; vertical-align:top;  white-space:nowrap; font-size:65%; }
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveExcelReportRenderer.form" enctype="multipart/form-data">
	<input type="hidden" name="uuid" value="${design.uuid}" />
  	<input type="hidden" name="successUrl" value="${successUrl}"/>
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
        		<span class="metadataField"><spring:message code="reporting.ExcelTemplateRenderer.selectOutput"/></span>
        		<input type="radio" name="rendererType" value="org.openmrs.module.reporting.report.renderer.XlsReportRenderer">
        		<span class=""><spring:message code="reporting.ExcelTemplateRenderer.defaultOutput"/></span>
        		<br/>
        		<input type="radio" name="rendererType" value="org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer">
        		<span class=""><spring:message code="reporting.ExcelTemplateRenderer.customOutput"/></span>  
      		</td>
			<td align="left" valign="top" id="xlsReportRendererProperties" style="padding-left:15px;">
				<table padding="5">
					<tr>
						<td>
							<input type="checkbox" name="includeDataSetNameAndParameters" value="true" ${includeDataSetNameAndParameters ? 'checked' : ''}/>
							<spring:message code="reporting.ExcelReportRenderer.includeDataSetHeader"/>
						</td>
					</tr>
				</table>
			</td>
      		<td align="left" valign="top" class="customOutputSection">
      			<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.expressions"/></span>
      			<span class="description"><spring:message code="reporting.ExcelReportRenderer.expressions.description"/></span>
      			<table padding="5" class="expressionsTable">
					<tr>
						<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.expressions.prefix"/></span></td>
						<td><input type="text" id="expressionPrefix" name="expressionPrefix" value='<c:out value="${configurableExpressions.expressionPrefix}"/>'/></td>
						<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.expressions.suffix"/></span></td>
						<td><input type="text" id="expressionSuffix" name="expressionSuffix" value='<c:out value="${configurableExpressions.expressionSuffix}"/>'/></td>
					</tr>
      			</table>
      			<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.resourceFile"/></span>
      			<span class="description"><spring:message code="reporting.ExcelReportRenderer.resourceFile.description"/></span>			
				<div id="resourcesMultiFieldDiv">
					<c:choose>
						<c:when test="${!empty design.resources}">
							<c:forEach items="${design.resources}" var="resource" varStatus="resourceStatus">
								<span class="multiFieldInput">
									<span class="fileUploadWidget">
										<span class="currentResourceSection">
											<a href="${pageContext.request.contextPath}/module/reporting/reports/viewReportDesignResource.form?designUuid=${design.uuid}&resourceUuid=${resource.uuid}">${resource.name}.${resource.extension}</a>
											<input type="button" value="Change" id="changeResourceButton"/>
										</span>
										<span class="resourceChangeSection" style="display:none;">
											<input type="file" name="resource"/>
											<input type="hidden" name="resourceId" value="${resource.uuid}"/>
											<input type="button" value="Cancel" id="cancelResourceButton"/>
										</span>
									</span>
									<input type="button" class="deleteResource" value="X" size="1"/>
								</span>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<input type="file" name="resource"/>
							<input type="button" class="deleteResource" value="X" size="1"/>
						</c:otherwise>
					</c:choose>
				</div>
				<input type="hidden" id="properties" name="properties" value=""> 
      			<table class="propTemplate">
      				<tr class="RSInputs">
      					<td><input type="text" name="sheet" size="10"/></td>
      					<td><input type="text" name="row" size="10"/></td>
      					<td><input type="text" name="column" size="10"/></td>
      					<td><input type="text" name="dataset" size="10"/></td>
      					<td><input type="button" class="deletePropertyButton" value="X" size="1"/></td>
      				</tr>
      				<tr class="propertiesInputs">
      					<td><input type="text" name="key" size="10"/></td>
      					<td><input type="text" name="value" size="10"/></td>
      					<td><input type="button" class="deletePropertyButton" value="X" size="1"/></td>
      				</tr>
      			</table>
      			<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections"/></span>
      			<span class="description"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.description"/></span>
      			<table class="RSContainer">
      				<tr class="tableHeaders">
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.sheet"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.row"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.column"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.dataset"/></span></td>
      					<td></td>
	      			</tr>
      				<tr class="addBttnContainer">
      					<td colspan="5"><input id="RSInputs" class="addPropertyButton" type="button" value="+" size="1"/></td>
      				</tr>
      			</table>
      			<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.designProperties"/></span>
      			<span class="description"><spring:message code="reporting.ExcelReportRenderer.designProperties.description"/></span>
      			<table class="propertiesContainer">
      				<tr class="tableHeaders">
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.designProperties.key"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.designProperties.value"/></span></td>
      					<td></td>
	      			</tr>
	      			<c:if test="${not empty design.properties}">	
						<c:forEach items="${design.properties}" var="property" >
							<c:if test="${ property.key != 'repeatingSections' }">
								<tr class="propertiesInputs">
	      							<td><input type="text" name="key" size="10"  value="${property.key}"/></td>
	      							<td><input type="text" name="value" size="10" value="${property.value}"/></td>
	      							<td><input type="button" class="deletePropertyButton" value="X" size="1"/></td>
	      						</tr>
							</c:if>
						</c:forEach>
					</c:if>
      				<tr class="addBttnContainer">
      					<td colspan="3"><input id="propertiesInputs" class="addPropertyButton" type="button" value="+" size="1"/></td>
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
