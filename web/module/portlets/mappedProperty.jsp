<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<c:choose>

	<c:when test="${model.mode == 'edit'}">
	
		<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
		
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
		
				$('#parameterizableSelector${model.id}').change(function(event){
					var currVal = $(this).val();
					if (currVal != '') {
						document.location.href='<c:url value="/module/reporting/viewPortlet.htm?id=editMappedPropertyPortlet${model.id}&url=mappedProperty&parameters=type=${model.type}|uuid=${model.uuid}|property=${model.property}|collectionKey=${model.collectionKey}|mode=edit|mappedUuid='+currVal+'"/>';
					}
					else {
						$("#mapParameterSection${model.id}").html('');
						$("#mappedUuidField${model.id}").val('');
					}
				});
		
				<c:forEach var="p" items="${model.mappedObj.parameters}" varStatus="varstatus">
					$('#typeSelector_${p.name}_${model.id}').change(function(event) {
						$('#typeSelector_${p.name}_fixed_${model.id}').hide();
						$('#typeSelector_${p.name}_mapped_${model.id}').hide();
						$('#typeSelector_${p.name}_complex_${model.id}').hide();
						var val = $('#typeSelector_${p.name}_${model.id}').val();
						$('#typeSelector_${p.name}_'+val+'_${model.id}').show();
					});
					$('#typeSelector_${p.name}_${model.id}').trigger('change');
				</c:forEach>
		
				$('#mapParametersFormCancelButton_${model.id}').click(function(event){
					closeReportingDialog(false);
				});
		
				$('#mapParametersFormSubmitButton_${model.id}').click(function(event){
					$('#mapParametersForm${model.id}').submit();
				});
		
			});
		</script>
		
		${model.mappedType.simpleName}: <rpt:widget id="parameterizableSelector${model.id}" name="parameterizableSelector" type="${model.mappedType.name}" defaultValue="${model.mappedObj}"/>
		
		<form id="mapParametersForm${model.id}" method="post" action="reports/saveMappedParameters.form">
			<input type="hidden" name="type" value="${model.type}"/>
			<input type="hidden" name="uuid" value="${model.uuid}"/>
			<input type="hidden" name="property" value="${model.property}"/>
			<input type="hidden" name="collectionKey" value="${model.collectionKey}"/>
			<input type="hidden" id="mappedUuidField${model.id}" name="mappedUuid" value="${model.mappedObj.uuid}"/>
			
			<div id="mapParameterSection${model.id}">
				<table>								
					<c:forEach var="p" items="${model.mappedObj.parameters}" varStatus="varstatus">
						<tr>
							<td  valign="top" align="right">
								${p.name}:&nbsp;
							</td>
							<td valign="top">
								<c:choose>
									<c:when test="${empty model.allowedParams[p.name]}">
										<input type="hidden" name="valueType_${p.name}" value="fixed"/>
										<rpt:widget id="fixedValue_${p.name}_${model.id}" name="fixedValue_${p.name}" type="${p.clazz.name}" defaultValue="${model.fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
									</c:when>
									<c:otherwise>
										<select id="typeSelector_${p.name}_${model.id}" name="valueType_${p.name}">
											<option value="fixed" <c:if test="${model.mappedParams[p.name] == null && model.complexParams[p.name] == null}">selected</c:if>>
												Value:
											</option>
											<option value="mapped" <c:if test="${model.mappedParams[p.name] != null}">selected</c:if>>
												Parameter:
											</option>
											<option value="complex" <c:if test="${model.complexParams[p.name] != null}">selected</c:if>>
												Expression:
											</option>
										</select>
										<span id="typeSelector_${p.name}_fixed_${model.id}" style="display:none;">
											<rpt:widget id="fixedValue_${p.name}_${model.id}" name="fixedValue_${p.name}" type="${p.clazz.name}" defaultValue="${model.fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
										</span>
										<span id="typeSelector_${p.name}_mapped_${model.id}" style="display:none;">
											<select name="mappedValue_${p.name}">
												<option value="" <c:if test="${model.mappedParams[p.name] == null}">selected</c:if>>Choose...</option>
												<c:forEach var="parentParam" items="${model.allowedParams[p.name]}">
													<option value="${parentParam}" <c:if test="${model.mappedParams[p.name] == parentParam}">selected</c:if>>
														${parentParam}
													</option>
												</c:forEach>
											</select>
										</span>
										<span id="typeSelector_${p.name}_complex_${model.id}" style="display:none;">
											<input type="text" name="complexValue_${p.name}" size="40" value="${model.complexParams[p.name]}"/>
										</span>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<hr style="color:blue;"/>
			<div style="width:100%; text-align:left;">
				<input type="button" id="mapParametersFormCancelButton_${model.id}" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
				<input type="button" id="mapParametersFormSubmitButton_${model.id}" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
			</div>
		</form>	
	
	
	</c:when>
	
	<c:otherwise>

		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$('#${model.id}EditLink').click(function(event){
					showReportingDialog({
						title: '${model.label}',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=editMappedPropertyPortlet${model.id}&url=mappedProperty&parameters=type=${model.type}|uuid=${model.uuid}|property=${model.property}|collectionKey=${model.collectionKey}|mode=edit"/>',
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
						<table>
							<tr><th colspan="3" align="left">
								<c:choose>
									<c:when test="${model.mappedObj != null}">
										${model.mappedObj.name}
									</c:when>
									<c:otherwise>
										${model.nullValueLabel}
									</c:otherwise>
								</c:choose>
							</th></tr>
							<c:forEach items="${model.mappedObj.parameters}" var="p">
								<tr>
									<td align="right">${p.name}</td>
									<td align="left">--&gt;</td>
									<td align="left" width="100%">
										<c:choose>
											<c:when test="${model.mappings[p.name] == null}">
												<span style="color:red; font-style:italic;">Undefined</span>
											</c:when>
											<c:otherwise>${model.mappings[p.name]}</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
				
			</c:otherwise>
			
		</c:choose>
		
	</c:otherwise>
	
</c:choose>