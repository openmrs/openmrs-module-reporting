<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />
<%@ include file="../../manage/localHeader.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>


<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		$('#cancelButton').click(function(event){
			window.location.href='<c:url value="/module/reporting/reports/manageReportDesigns.form"/>';
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

<spring:nestedPath path="defaultDesign">
	<form method="post" enctype="multipart/form-data">
		<table style="margin:0; padding:0; font-size:small;" padding="5">
			<tr>
				<td valign="top" align="left">
					<span class="metadataField">Name</span>
					<spring:bind path="name">
						<wgt:widget id="${status.expression}" name="${status.expression}" object="${defaultDesign}" property="${status.expression}" attributes="size=50"/>
						<c:if test="${not empty status.errorMessage}">
							<br/>
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
					<br/>

					<span class="metadataField">Description</span>
					<wgt:widget id="description" name="description" object="${defaultDesign}" property="description" attributes="cols=38|rows=2"/>
					<br/>

					<span class="metadataField">Report Definition</span>
					<spring:bind path="reportDefinition">	
						<c:choose>
							<c:when test="${!empty defaultDesign.reportDefinition.uuid}">
								<span style="color:navy;">${defaultDesign.reportDefinition.name}</span>
								<input type="hidden" name="reportDefinition" value="${defaultDesign.reportDefinition.uuid}"/>
							</c:when>
							<c:otherwise>
								<wgt:widget id="${status.expression}" name="${status.expression}" object="${defaultDesign}" property="${status.expression}" attributes=""/>
							</c:otherwise>
						</c:choose>	
						<c:if test="${not empty status.errorMessage}">
							<br/>
							<span class="error">${status.errorMessage}</span>							
						</c:if>
					</spring:bind>	
					<br/>

					<span class="metadataField">Renderer Type</span>
					<spring:bind path="rendererType">			
						<wgt:widget id="${status.expression}" name="${status.expression}" object="${defaultDesign}" property="${status.expression}" attributes="type=org.openmrs.module.reporting.report.renderer.ReportRenderer|simple=true"/>
						<c:if test="${not empty status.errorMessage}">
							<br/>
							<span class="error">${status.errorMessage}</span>
						</c:if>
					</spring:bind>
					<br/>

					<span class="metadataField">Resource Files</span>			
					<div id="resourcesMultiFieldDiv">
						<c:forEach items="${defaultDesign.resources}" var="resource" varStatus="resourceStatus">
							<span class="multiFieldInput" id="resources_${resource.uuid}">
								<span class="fileUploadWidget">
									<span class="currentResourceSection">
										<a href="${pageContext.request.contextPath}/module/reporting/reports/viewReportDesignResource.form?designUuid=${defaultDesign.uuid}&resourceUuid=${resource.uuid}">${resource.name}.${resource.extension}</a>
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
						<span id="resourcesCount" style="display:none;">${fn:length(defaultDesign.resources)+1}</span>
					</div>

				</td>
				<td valign="top" align="left">	
					<span class="metadataField">Design Properties</span>	
					<wgt:widget id="properties" name="properties" object="${defaultDesign}" property="properties" attributes="rows=20|cols=50"/>
				</td>
			</tr>
		</table>

		
		<hr style="color:blue;"/>
		<div style="width:100%; text-align:left;">
			<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
			<input type="submit" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
		</div>
	</form>
</spring:nestedPath>

<%@ include file="/WEB-INF/template/footer.jsp"%>