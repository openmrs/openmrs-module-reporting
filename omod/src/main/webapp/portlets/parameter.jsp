<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:choose>

	<c:when test="${model.mode == 'edit'}">
	
		<c:if test="${model.dialog != 'false'}">
			<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
		</c:if>
		<script type="text/javascript" charset="utf-8">
			$j(document).ready(function() {
		
				$j('#cancelButton').click(function(event){
					<c:choose>
						<c:when test="${model.dialog != 'false'}">
							closeReportingDialog(false);
						</c:when>
						<c:otherwise>
							document.location.href = '${model.cancelUrl}';
						</c:otherwise>
					</c:choose>
				});
		
				$j('#submitButton').click(function(event){
					$j('#editParameterForm').submit();
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
			<input type="hidden" id="shortcut" name="shortcut" value=""/>
			
			<div style="background-color: #f0f0f0">
				One-click shortcuts:
				<a href="javascript:void(0)" onClick="$j('#shortcut').val('date'); $j('#editParameterForm').submit();"><spring:message code="reporting.date" /></a>
				<a href="javascript:void(0)" onClick="$j('#shortcut').val('startDate'); $j('#editParameterForm').submit();"><spring:message code="reporting.parameter.startDate" /></a>
				<a href="javascript:void(0)" onClick="$j('#shortcut').val('endDate'); $j('#editParameterForm').submit();"><spring:message code="reporting.parameter.endDate" /></a>
				<a href="javascript:void(0)" onClick="$j('#shortcut').val('location'); $j('#editParameterForm').submit();"><spring:message code="reporting.parameter.location" /></a>
			</div>
			
			<div style="margin:0; padding:0; width:100%;">
			
				<div class="metadataField">
					<label class="desc"><spring:message code="reporting.parameter.type" /></label>
					<select name="collectionType">
						<option value="" <c:if test="${model.parameter.collectionType == null}">selected</c:if>>Single</option>
						<c:forEach var="supportedType" items="${model.supportedCollectionTypes}">
							<option value="${supportedType.value}" <c:if test="${model.parameter.collectionType.name == supportedType.value}">selected</c:if>>${supportedType.labelText} of</option>
						</c:forEach>
					</select>
					<select name="parameterType">
						<option value="" <c:if test="${model.parameter.type == null}">selected</c:if>></option>
						<c:forEach var="supportedType" items="${model.supportedTypes}">
							<option value="${supportedType.value}" <c:if test="${model.parameter.type.name == supportedType.value}">selected</c:if>>${supportedType.labelText}</option>
						</c:forEach>
					</select>
				</div>

				<div class="metadataField">
					<label class="desc" for="name"><spring:message code="reporting.parameter.name" /></label>
					<input type="text" id="name" tabindex="1" name="newName" value="${model.parameter.name}" size="50"/>
				</div>
				<div class="metadataField">
					<label class="desc" for="label"><spring:message code="reporting.parameter.label" /></label>			
					<textarea id="label" cols="50" rows="2" tabindex="2" name="label">${model.parameter.label}</textarea>
				</div>
				<div class="metadataField">
					<label class="desc" for="widgetConfiguration"><spring:message code="reporting.advancedConfig" /></label>			
					<textarea id="widgetConfiguration" cols="50" rows="5" tabindex="2" name="widgetConfiguration">${model.parameter.widgetConfiguration}</textarea>
				</div>
			</div>
			<hr style="color:blue;"/>
			<div style="width:100%; text-align:left;">
				<input type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
				<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
			</div>
		</form>
	</c:when>
	
	<c:otherwise>
	
		<script type="text/javascript" charset="utf-8">
			$j(document).ready(function() {
				<c:forEach items="${model.obj.parameters}" var="p" varStatus="paramStatus">
					$j('#${model.portletUUID}EditLink${paramStatus.index}').click(function(event){
						showReportingDialog({
							title: 'Parameter: ${p.name}',
							url: '<c:url value="/module/reporting/viewPortlet.htm?id=parameterPortlet&url=parameter&parameters.type=${model.type}&parameters.uuid=${model.uuid}&parameters.name=${p.name}&parameters.mode=edit"/>',
							successCallback: function() { window.location.reload(true); }
						});
					});
					$j('#${model.portletUUID}RemoveLink${paramStatus.index}').click(function(event){
						if (confirm('Please confirm you wish to remove parameter ${p.name}')) {
							document.location.href='${pageContext.request.contextPath}/module/reporting/parameters/deleteParameter.form?type=${model.type}&uuid=${model.uuid}&name=${p.name}&returnUrl=${model.parentUrl}';
						}
					});
				</c:forEach>
				$j('#${model.portletUUID}AddLink').click(function(event){
					showReportingDialog({
						title: 'New Parameter',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=parameterPortlet&url=parameter&parameters.type=${model.type}&parameters.uuid=${model.uuid}&parameters.mode=edit"/>',
						successCallback: function() { window.location.reload(true); }
					});
				});
			} );
		</script>
		
		<div <c:if test="${model.size != null}">style="width:${model.size};"</c:if>>			
			<b class="boxHeader" style="font-weight:bold; text-align:right;">
				<span style="float:left;">${model.label}</span>
				<a style="color:lightyellow; font-weight:bold;" href="#" id="${model.portletUUID}AddLink">[+] Add</a>
			</b>			
			<div class="box">
				<div align="center" style="padding:10px;">
				<c:if test="${empty model.obj.parameters}">
					<span ><spring:message code="reporting.Report.parameters.empty"/></span>
				</c:if>
				<c:if test="${!empty model.obj.parameters}">
					<table width="100%" style="margin-bottom:5px;">
						<tr>
							<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.Report.parameter.name" /></th>
							<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.Report.parameter.label" /></th>
							<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.Report.parameter.type" /></th>
							<th style="border-bottom:1px solid black;"></th>
						</tr>
						<c:forEach items="${model.obj.parameters}" var="p" varStatus="paramStatus">
							<tr>
								<td nowrap>
									<c:out value="${p.name}" />
								</td>
								<td width="100%">
									<c:out value="${p.label}" />
								</td>
								<td nowrap>
									<c:choose>
										<c:when test="${p.collectionType != null}">
											<c:out value="${p.collectionType.simpleName}" /> < <c:out value="${p.type.simpleName}" /> >;
										</c:when>
										<c:otherwise>
										<c:out value="${p.type.simpleName}" />
										</c:otherwise>
									</c:choose>
								</td>
								<td nowrap align="center">
									&nbsp;
									<a href="#" id="${model.portletUUID}EditLink${paramStatus.index}">
										<img src='<c:url value="/images/edit.gif"/>' border="0"/>
									</a>
									&nbsp;
									<a href="#" id="${model.portletUUID}RemoveLink${paramStatus.index}">
										<img src='<c:url value="/images/trash.gif"/>' border="0"/>
									</a>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
			</div>
		</div>
		
	</c:otherwise>
	
</c:choose>
