<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<c:choose>
    <c:when test="${model.dialog != 'false'}">
	   <%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
	</c:when>
	<c:otherwise>
	   <%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>
	</c:otherwise>
</c:choose>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		$('#cancelButton').click(function(event){
			<c:choose>
				<c:when test="${model.dialog != 'false'}">
					closeReportingDialog(false);
				</c:when>
				<c:otherwise>
					document.location.href = '${model.cancelUrl}';
				</c:otherwise>
			</c:choose>
		});

		$('#submitButton').click(function(event){
			$('#reportDesignForm').submit();
		});

		$("#resourcesAddButton").click(function(event){
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
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/saveReportDesign.form" enctype="multipart/form-data">
	<input type="hidden" name="uuid" value="${model.design.uuid}" />
	<input type="hidden" name="successUrl" value="${model.successUrl}"/>
	
	<table style="margin:0; padding:0; font-size:small;" padding="5">
		<tr>
			<td valign="top" align="left">
				<span class="metadataField">Name</span>
				<wgt:widget id="name" name="name" object="${model.design}" property="name" attributes="size=50"/>
				<br/>
				<span class="metadataField">Description</span>			
				<wgt:widget id="description" name="description" object="${model.design}" property="description" attributes="cols=38|rows=2"/>
				<br/>
				<span class="metadataField">Report Definition</span>
				<c:choose>
					<c:when test="${!empty model.reportDefinitionUuid}">
						<span style="color:navy;">${model.design.reportDefinition.name}</span>
						<input type="hidden" name="reportDefinition" value="${model.reportDefinitionUuid}"/>
					</c:when>
					<c:otherwise>
						<wgt:widget id="reportDefinition" name="reportDefinition" object="${model.design}" property="reportDefinition" attributes=""/>
					</c:otherwise>
				</c:choose>		
				<br/>
				<span class="metadataField">Renderer Type</span>			
				<wgt:widget id="rendererType" name="rendererType" object="${model.design}" property="rendererType" attributes="type=org.openmrs.module.reporting.report.renderer.ReportRenderer|simple=true"/>
				<br/>
				<span class="metadataField">Resource Files</span>			
				<div id="resourcesMultiFieldDiv">
					<c:forEach items="${model.design.resources}" var="resource" varStatus="resourceStatus">
						<span class="multiFieldInput" id="resources_${resource.uuid}">
							<span class="fileUploadWidget">
								<span class="currentResourceSection">
									<a href="${pageContext.request.contextPath}/module/reporting/reports/viewReportDesignResource.form?designUuid=${model.design.uuid}&resourceUuid=${resource.uuid}">${resource.name}.${resource.extension}</a>
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
			<td valign="top" align="left">
				<span class="metadataField">Design Properties</span>	
				<wgt:widget id="properties" name="properties" object="${model.design}" property="properties" attributes="rows=20|cols=50"/>
			</td>
		</tr>
	</table>
	<hr style="color:blue;"/>
	<div style="width:100%; text-align:left;">
		<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
		<input type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
	</div>
</form>

