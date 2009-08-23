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
			<input type="hidden" name="uuid" value="${model.uuid}"/>
			<input type="hidden" name="type" value="${model.type}"/>
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
					<select name="type">
						<option value="" <c:if test="${model.parameter.type == null}">selected</c:if>></option>
						<c:forEach var="supportedType" items="${model.supportedTypes}">
							<option value="${supportedType.value}" <c:if test="${model.parameter.type.name == supportedType.value}">selected</c:if>>${supportedType.labelText}</option>
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
				<c:forEach items="${model.obj.parameters}" var="p" varStatus="paramStatus">
					$('#${model.portletUUID}EditLink${paramStatus.index}').click(function(event){
						showReportingDialog({
							title: 'Parameter: ${p.name}',
							url: '<c:url value="/module/reporting/viewPortlet.htm?id=parameterPortlet&url=parameter&parameters=type=${model.type}|uuid=${model.uuid}|name=${p.name}|mode=edit"/>',
							successCallback: function() { window.location.reload(true); }
						});
					});
					$('#${model.portletUUID}RemoveLink${paramStatus.index}').click(function(event){					
						if (confirm('Please confirm you wish to remove parameter ${p.name}')) {
							document.location.href='${pageContext.request.contextPath}/module/reporting/parameters/deleteParameter.form?type=${model.type}&uuid=${model.uuid}&name=${p.name}&returnUrl=${model.parentUrl}';
						}
					});
				</c:forEach>
				$('#${model.portletUUID}AddLink').click(function(event){
					showReportingDialog({
						title: 'New Parameter',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=parameterPortlet&url=parameter&parameters=type=${model.type}|uuid=${model.uuid}|mode=edit"/>',
						successCallback: function() { window.location.reload(true); }
					});
				});
			} );
		</script>
		
		<div <c:if test="${model.size != null}">style="width:${model.size};"</c:if>>
			<b class="boxHeader">${model.label}</b>
			
			<div class="box">
				<c:if test="${!empty model.obj.parameters}">
					<table width="100%" style="margin-bottom:5px;">
						<tr>
							<th style="text-align:left; border-bottom:1px solid black;">Name</th>
							<th style="text-align:left; border-bottom:1px solid black;">Label</th>
							<th style="text-align:left; border-bottom:1px solid black;">Type</th>
							<th style="border-bottom:1px solid black;">[X]</th>
						</tr>
						<c:forEach items="${model.obj.parameters}" var="p" varStatus="paramStatus">
							<tr>
								<td nowrap><a href="#" id="${model.portletUUID}EditLink${paramStatus.index}">${p.name}</a></td>
								<td width="100%">${p.label}</td>
								<td nowrap>
									<c:choose>
										<c:when test="${p.collectionType != null}">
											${p.collectionType.simpleName}&lt;${p.type.simpleName}&gt;
										</c:when>
										<c:otherwise>
											${p.type.simpleName}
										</c:otherwise>
									</c:choose>
								</td>
								<td nowrap align="center"><a href="#" id="${model.portletUUID}RemoveLink${paramStatus.index}">[X]</a></td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
				<a style="font-weight:bold;" href="#" id="${model.portletUUID}AddLink">[+] Add</a>
			</div>
		</div>
		
	</c:otherwise>
	
</c:choose>
