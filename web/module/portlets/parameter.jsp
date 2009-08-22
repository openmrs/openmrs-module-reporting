<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<c:choose>

	<c:when test="${model.mode == 'edit'}">
	
		<c:if test="${model.dialog != 'false'}">
			<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
		</c:if>
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
					$('#editParameterForm').submit();
				});
		
			});
		</script>
		<style>
			div.metadataField { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
			div.metadataField label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
		</style>
		
		<form id="editParameterForm" method="post" action="${pageContext.request.contextPath}/module/reporting/parameters/saveParameter.form">
			<input type="hidden" name="parentUuid" value="${model.uuid}"/>
			<input type="hidden" name="parentType" value="${model.type}"/>
			<input type="hidden" name="currentName" value="${model.parameter.name}"/>
			<input type="hidden" name="successUrl" value="${model.successUrl}"/>
			<div style="margin:0; padding:0; width:100%;">
			
				<div class="metadataField">
					<label class="desc">Type</label>
					<select name="collectionType">
						<option value="" <c:if test="${model.parameter.collectionType == null}">selected</c:if>>Single</option>
						<c:forEach var="supportedType" items="${model.supportedCollectionTypes}">
							<option value="${supportedType.value}" <c:if test="${model.parameter.collectionType.name == supportedType.value}">selected</c:if>>${supportedType.labelText} of</option>
						</c:forEach>
					</select>
					<select name="clazz">
						<option value="" <c:if test="${model.parameter.clazz == null}">selected</c:if>></option>
						<c:forEach var="supportedType" items="${model.supportedTypes}">
							<option value="${supportedType.value}" <c:if test="${model.parameter.clazz.name == supportedType.value}">selected</c:if>>${supportedType.labelText}</option>
						</c:forEach>
					</select>
				</div>

				<div class="metadataField">
					<label class="desc" for="name">Name</label>
					<input type="text" id="name" tabindex="1" name="newName" value="${model.parameter.name}" size="50"/>
				</div>
				<div class="metadataField">
					<label class="desc" for="label">Label</label>			
					<textarea id="label" cols="50" rows="2" tabindex="2" name="label">${model.parameter.label}</textarea>
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
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=parameterPortlet&url=parameter&parameters=type=${model.type}|uuid=${model.uuid}|name=${model.parameter.name}|mode=edit"/>',
						successCallback: function() { window.location.reload(true); }
					});
				});
			} );
		</script>
		
		<c:choose>
		
			<c:when test="${model.mode == 'add'}">
				<a style="font-weight:bold;" href="#" id="${model.id}EditLink">[+] Add</a>
			</c:when>
			
			<c:otherwise>
		
				<div <c:if test="${model.size != null}">style="width:${model.size};"</c:if>>
					<b class="boxHeader" style="font-weight:bold; text-align:right;">
						<span style="float:left;">${model.label}</span>
						<a style="color:lightyellow;" href="#" id="${model.id}EditLink">Edit</a>
					</b>
					<div class="box">
						<div style="padding-bottom:5px;">
							Name: ${model.parameter.name}<br/>
							Type: ${model.parameter.clazz} ${model.parameter.collectionType}<br/>
							Label: ${model.parameter.label}
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
	
</c:choose>
