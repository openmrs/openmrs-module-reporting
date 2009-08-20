<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<c:choose>

	<c:when test="${model.mode == 'edit'}">
	
		<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
		
				$('#cancelButton').click(function(event){
					closeReportingDialog(false);
				});
		
				$('#submitButton').click(function(event){
					$('#baseParameterizableEditorForm').submit();
				});
		
			});
		</script>
		<style>
			div.metadataField { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
			div.metadataField label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
		</style>
		
		<form id="baseParameterizableEditorForm" method="post" action="reports/saveBaseParameterizable.form">
			<input type="hidden" name="uuid" value="${model.uuid}"/>
			<input type="hidden" name="type" value="${model.type}"/>
			<div style="margin:0; padding:0; width:100%; padd">
				<div class="metadataField">
					<label class="desc" for="name">Name</label>
					<input type="text" id="name" tabindex="1" name="name" value="${model.obj.name}" size="50"/>
				</div>
				<div class="metadataField">
					<label class="desc" for="description">Description</label>			
					<textarea id="description" cols="80" rows="10" tabindex="2" name="description">${model.obj.description}</textarea>
				</div>
			</div>
			<hr style="color:blue;"/>
			<div style="width:100%; text-align:left;">
				<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
				<input type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
			</div>
		</form>
	</c:when>
	
	<c:otherwise>
	
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$('#${model.id}EditLink').click(function(event){
					showReportingDialog({
						title: '${model.label}',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=editBaseMetadataPortlet&url=baseMetadata&parameters=type=${model.type}|uuid=${model.uuid}|mode=edit"/>',
						successCallback: function() { window.location.reload(true); }
					});
				});
			} );
		</script>
		
		<div <c:if test="${model.size != null}">style="width:${model.size};"</c:if>>
			<b class="boxHeader" style="font-weight:bold; text-align:right;">
				<span style="float:left;">${model.label}</span>
				<a style="color:lightyellow;" href="#" id="${model.id}EditLink">Edit</a>
			</b>
			<div class="box">
				<div style="padding-bottom:5px;">
					<b>Name:&nbsp;&nbsp;</b>${model.obj.name}
				</div>
				<b>Description:&nbsp;&nbsp;</b>${model.obj.description}
			</div>
		</div>
		
	</c:otherwise>
	
</c:choose>
