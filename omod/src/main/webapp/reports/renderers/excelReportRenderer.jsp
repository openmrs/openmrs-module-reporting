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
    	
		$('#resourcesAddButton').click(function(event){
			var count = parseInt($('#resourcesCount').html()) + 1;
			$('#resourcesCount').html(count);
			var $newRow = cloneAndInsertBefore('resourcesTemplate', this);
			$newRow.attr('id', 'resources' + count);
			var newRowChildren = $newRow.children();
			for (var i=0; i<newRowChildren.length; i++) {
				newRowChildren[i].id = newRowChildren[i].id + count;
			}
			var resourceInput = $newRow.find("input[name='resources']")[0];
			$(resourceInput).attr('name', 'resources.new'+count);
		});
		
		$( '#reportDesignForm td.propertiesSection table.RSContainer input#RSInputs,' +
				' #reportDesignForm td.propertiesSection table.propertiesContainer input#propertiesInputs' ).click( function( event ) {
			addPropertyInputs( $( this ), $( this ).attr( 'id' ) );
		} );
		
		$( '#reportDesignForm' ).submit( function() {
			buildPropertiesStr();			
		} );
		
		<c:if test="${not empty design.properties.repeatingSections}">	
			setRSProperties( '${design.properties.repeatingSections}' );
		</c:if>
		addRemoveEvntKeyValueProperty();
  	});
	
	function removeInputs() {
		removeParentWithClass( this, trClass );
		hideOrShowTableHeaders( $referenceButton, trClass );		
	}
	
	function hideOrShowTableHeaders( $referenceElement, trClass ) {
		var $parentElement = $( findParentWithClass( $referenceElement[0], "addBttnContainer" ) );
		if ( $parentElement.siblings( 'tr.' + trClass ).length == 0 ) {
			 $parentElement.prevAll( 'tr.tableHeaders' ).hide();
		} else {
			$parentElement.prevAll( 'tr.tableHeaders' ).show();
		}
	}
	
	function addRemoveEvntKeyValueProperty() {
		var $trPropInputs = $( '#reportDesignForm td.propertiesSection table.propertiesContainer tr.propertiesInputs' );
		if ( $trPropInputs.length ) {
			var $addButton = $( "#reportDesignForm td.propertiesSection table.propertiesContainer input#propertiesInputs" );
			hideOrShowTableHeaders( $addButton, 'propertiesInputs' );
			$trPropInputs.each(function() {
				$( this ).find( 'input:last' ).click(function() {
					removeParentWithClass( this, 'propertiesInputs' );
					hideOrShowTableHeaders( $addButton, 'propertiesInputs' );
				} );
			} );			
		}
	}
	
	function setRSProperties( RSValue ) {
		var sectionsArr = RSValue.split( '|' ),
			$addButton = $( "#reportDesignForm td.propertiesSection table.RSContainer input#RSInputs" ),
			$addBttnParent = $( findParentWithClass( $addButton[ 0 ], 'addBttnContainer' ) );
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
		var $trRSInputs = $( '#reportDesignForm td.propertiesSection table.RSContainer tr.RSInputs' );
		if ( $trRSInputs.length ) {
			var sections = [];
			$trRSInputs.each( function( index ) {
				var $inputs = $( this ).find( 'input' ),
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
	
	function buildKeyValueProperties( propertiesArr ) {
		var $trRSInputs = $( '#reportDesignForm td.propertiesSection table.propertiesContainer tr.propertiesInputs' );
		if ( $trRSInputs.length ) {
			$trRSInputs.each( function() {
				var $inputs = $( this ).find( 'input' ),
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
		buildKeyValueProperties( propertiesStr );
		if ( propertiesStr.length ) {
			$( '#reportDesignForm td.propertiesSection input#properties' ).val( propertiesStr.join( '\\n' ) );
		}
	}
	
	function addPropertyInputs( $referenceButton, trClass  ) {
		var queryTemplate = '#reportDesignForm td.propertiesSection table.propTemplate tbody.' + trClass,
			$template = $( queryTemplate ).html(),
			$trBttnContainer = $referenceButton.parents( 'tr:first' );
		$trBttnContainer.before( $template );
		hideOrShowTableHeaders( $referenceButton, trClass );
		$trBttnContainer.prev( 'tr.' + trClass ).find( "input[type='button']" ).click(function() {
			removeParentWithClass( this, trClass );
			hideOrShowTableHeaders( $referenceButton, trClass );
		} );
	}
		
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
	.RSContainer, .propertiesContainer { margin:0; padding:0; font-size:small; }
	.propertiesSection { padding-left:15px; }
	.propTemplate, .tableHeaders { display:none;}
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveExcelReportRenderer.form" enctype="multipart/form-data">
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
				<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.resourceFiles"/></span>			
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
      		<td align="left" valign="top" class="propertiesSection">
      			<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections"/></span>
      			<input type="hidden" id="properties" name="properties" value="">
      			<table class="propTemplate">
      				<tbody class="RSInputs">
	      				<tr class="RSInputs">
	      					<td><input type="text" name="sheet" size="10"/></td>
	      					<td><input type="text" name="row" size="10"/></td>
	      					<td><input type="text" name="column" size="10"/></td>
	      					<td><input type="text" name="dataset" size="10"/></td>
	      					<td><input type="button" value="X" size="1"/></td>
	      				</tr>
	      			</tbody>
	      			<tbody class="propertiesInputs">
	      				<tr class="propertiesInputs">
	      					<td><input type="text" name="key" size="10"/></td>
	      					<td><input type="text" name="value" size="10"/></td>
	      					<td><input type="button" value="X" size="1"/></td>
	      				</tr>
      				</tbody>
      			</table>
      			<table class="RSContainer">
      				<tr class="tableHeaders">
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.sheet"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.row"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.column"/></span></td>
      					<td><span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.repeatingSections.dataset"/></span></td>
      					<td></td>
	      			</tr>
      				<tr class="addBttnContainer">
      					<td colspan="5"><input id="RSInputs" type="button" value="+" size="1"/></td>
      				</tr>
      			</table>
      			<span class="metadataField"><spring:message code="reporting.ExcelReportRenderer.designProperties"/></span>
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
	      							<td><input type="button" value="X" size="1"/></td>
	      						</tr>
							</c:if>
						</c:forEach>
					</c:if>
      				<tr class="addBttnContainer">
      					<td colspan="3"><input id="propertiesInputs" type="button" value="+" size="1"/></td>
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
