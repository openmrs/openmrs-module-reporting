<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:choose>

	<c:when test="${model.mode == 'edit'}">
	
		<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
		
		<script type="text/javascript" charset="utf-8">
			$j(document).ready(function() {
		
				$j('#parameterizableSelector${model.id}').change(function(event){
					var currVal = $j(this).val();
					var currLabel = $j("option:selected", this).html().replace(/\s+/g, '-');
					if (currVal != '') {
						document.location.href='<c:url value="/module/reporting/viewPortlet.htm?id=editMappedPropertyPortlet${model.id}&url=mappedProperty&parameters.type=${model.type}&parameters.uuid=${model.uuid}&parameters.property=${model.property}&parameters.keyLabel=${model.keyLabel}&parameters.typeLabel=${model.typeLabel}&parameters.currentKey=${model.currentKey}&parameters.newKey='+currLabel+'&parameters.mode=edit&parameters.keyLabel=${model.keyLabel}&parameters.typeLabel=${model.typeLabel}&parameters.mappedUuid='+currVal+'"/>';
					}
					else {
                        $j('#${model.id}NewKey').val('');
						$j("#mapParameterSection${model.id}").html('');
						$j("#mappedUuidField${model.id}").val('');
					}
				});
		
				<c:forEach var="p" items="${model.mappedObj.parameters}" varStatus="varstatus">
					$j('#typeSelector_${varstatus.index}_${model.id}').change(function(event) {
						$j('#typeSelector_${varstatus.index}_fixed_${model.id}').hide();
						$j('#typeSelector_${varstatus.index}_mapped_${model.id}').hide();
						$j('#typeSelector_${varstatus.index}_complex_${model.id}').hide();
						var val = $j('#typeSelector_${varstatus.index}_${model.id}').val();
						$j('#typeSelector_${varstatus.index}_'+val+'_${model.id}').show();
					});
					$j('#typeSelector_${varstatus.index}_${model.id}').trigger('change');
				</c:forEach>
		
				$j('#mapParametersFormCancelButton_${model.id}').click(function(event){
					closeReportingDialog(false);
				});
		
				$j('#mapParametersFormSubmitButton_${model.id}').click(function(event){
					var existingKeys = [<c:forEach items="${model.existingKeys}" var="c" varStatus="cStat">'${c}'<c:if test="${!cStat.last}">,</c:if></c:forEach>];
					var initialKey = '${model.currentKey}';
					var newKey = $j('#${model.id}NewKey').val();
					if (initialKey != newKey) {
						for (var i=0; i<existingKeys.length; i++) {
							if (existingKeys[i] == newKey) {
								alert('That ${model.keyLabel} is already in use, please choose another.')
								return false;
							}
						}
					}
					$j('#mapParametersForm${model.id}').submit();
				});
		
			});
		</script>
		
		<br/>

		<form id="mapParametersForm${model.id}" method="post" action="reports/saveMappedProperty.form">
			<input type="hidden" name="type" value="${model.type}"/>
			<input type="hidden" name="uuid" value="${model.uuid}"/>
			<input type="hidden" name="property" value="${model.property}"/>
			<input type="hidden" name="currentKey" value="${model.currentKey}"/>
			
			<table>
				<c:choose>
					<c:when test="${model.multiType == 'map'}">
						<tr>
							<td>${model.keyLabel}: </td>
							<td><input type="text" size="20" id="${model.id}NewKey" name="newKey" value="${model.newKey == null ? model.currentKey : model.newKey}"/></td>
						</tr>
					</c:when>
					<c:otherwise>
						<input type="hidden" name="newKey" value="${model.currentKey}"/>
					</c:otherwise>
				</c:choose>
				<tr>
					<td>${model.typeLabel}: </td>
					<td><wgt:widget id="parameterizableSelector${model.id}" name="mappedUuid" type="${model.mappedType.name}" defaultValue="${model.mappedObj}" attributes="tag=${model.tag}"/></td>
				</tr>
			</table>		 
			
			<div id="mapParameterSection${model.id}">
				<table>								
					<c:forEach var="p" items="${model.mappedObj.parameters}" varStatus="varstatus">
						<tr>
							<td  valign="top" align="right">
								${p.labelOrName}:&nbsp;
							</td>
							<td valign="top">
								<c:choose>
									<c:when test="${empty model.allowedParams[p.name]}">
										<input type="hidden" name="valueType_${p.name}" value="fixed"/>
										<wgt:widget id="fixedValue_${varstatus.index}_${model.id}" name="fixedValue_${p.name}" type="${p.type.name}" defaultValue="${model.fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
									</c:when>
									<c:otherwise>
										<select id="typeSelector_${varstatus.index}_${model.id}" name="valueType_${p.name}">
											<option value="fixed" <c:if test="${model.mappedParams[p.name] == null && model.complexParams[p.name] == null}">selected</c:if>>
												<spring:message code="reporting.value" />:
											</option>
											<option value="mapped" <c:if test="${model.mappedParams[p.name] != null}">selected</c:if>>
												<spring:message code="reporting.parameter" />:
											</option>
											<option value="complex" <c:if test="${model.complexParams[p.name] != null}">selected</c:if>>
												<spring:message code="reporting.expression" />:
											</option>
										</select>
										<span id="typeSelector_${varstatus.index}_fixed_${model.id}" style="display:none;">
											<wgt:widget id="fixedValue_${varstatus.index}_${model.id}" name="fixedValue_${p.name}" type="${p.type.name}" defaultValue="${model.fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
										</span>
										<span id="typeSelector_${varstatus.index}_mapped_${model.id}" style="display:none;">
											<select name="mappedValue_${p.name}">
												<option value="" <c:if test="${model.mappedParams[p.name] == null}">selected</c:if>><spring:message code="reporting.choose" />...</option>
												<c:forEach var="parentParam" items="${model.allowedParams[p.name]}">
													<option value="${parentParam.key}" <c:if test="${model.mappedParams[p.name] == parentParam.key}">selected</c:if>>
														${parentParam.value}
													</option>
												</c:forEach>
											</select>
										</span>
										<span id="typeSelector_${varstatus.index}_complex_${model.id}" style="display:none;">
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
				<input type="button" id="mapParametersFormSubmitButton_${model.id}" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
				<input type="button" id="mapParametersFormCancelButton_${model.id}" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
			</div>
		</form>
	
	</c:when>
	
	<c:otherwise>

		<script type="text/javascript" charset="utf-8">
			$j(document).ready(function() {
				<c:if test="${!empty model.viewId}">
					$j('#${model.viewId}Template').html($j('#${model.viewId}').html());
				</c:if>
				$j('#${model.id}EditLink').click(function(event){
					showReportingDialog({
						title: '${model.label}',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=editMappedPropertyPortlet${model.id}&url=mappedProperty&parameters.type=${model.type}&parameters.uuid=${model.uuid}&parameters.property=${model.property}&parameters.currentKey=${model.currentKey}&parameters.keyLabel=${model.keyLabel}&parameters.typeLabel=${model.typeLabel}&parameters.tag=${model.tag}&parameters.mode=edit"/>',
						successCallback: function() { window.location.reload(true); }
					});
				});
				<c:if test="${model.currentKey != null}">
					$j('#${model.id}RemoveLink').click(function(event){
						if (confirm('Please confirm you wish to remove ${model.mappedObj.name}')) {
							document.location.href='<c:url value="/module/reporting/reports/removeMappedProperty.form?type=${model.type}&uuid=${model.uuid}&property=${model.property}&currentKey=${model.currentKey}&returnUrl=${model.parentUrl}"/>';
						}
					});
				</c:if>
			} );
		</script>
		
		<c:choose>
		
			<c:when test="${model.mode == 'add'}">
				<a style="font-weight:bold;" href="#" id="${model.id}EditLink">[+] ${model.label}</a>
			</c:when>
			
			<c:otherwise>
		
				<div <c:if test="${model.size != null}">style="width:${model.size};"</c:if>>
					
					<c:choose>
						<c:when test="${empty model.headerClass}">
							<b class="boxHeader" style="font-weight:bold; text-align:right;">
								<span style="float:left;">${model.label}</span>
								<a style="color:lightyellow;" href="#" id="${model.id}EditLink"><spring:message code="reporting.editMappings" /></a>
								<c:if test="${model.currentKey != null}">
									&nbsp;|&nbsp;
									<a style="color:lightyellow;" href="#" id="${model.id}RemoveLink"><spring:message code="reporting.delete" /></a>
								</c:if>
							</b>
						</c:when>
						<c:otherwise>
							<span class="${model.headerClass}" id="${model.id}Header">
								<span id="${model.id}HeaderLabel" style="font-weight:bold;">${model.label}</span>&nbsp;&nbsp;&nbsp;
								<span id="${model.id}HeaderLinks">
									<a href="#" id="${model.id}EditLink"><spring:message code="reporting.editMappings" /></a>
									<c:if test="${model.currentKey != null}">&nbsp;|&nbsp;
										<a href="#" id="${model.id}RemoveLink"><spring:message code="reporting.delete" /></a>
									</c:if>
								</span>
							</span>
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="${!empty model.viewId}">
							<div id="${model.viewId}Template"></div>
						</c:when>
						<c:otherwise>

							<div class="box">
								<c:choose>
									<c:when test="${model.mappedObj != null}">
										<c:choose>
											<c:when test="${!empty model.mapped.description}">
												${model.mapped.description}
											</c:when>
											<c:otherwise>
												<table>
													<tr>
														<th colspan="3" align="left">${model.mappedObj.name}</th>
													</tr>
													<c:forEach items="${model.mappedObj.parameters}" var="p">
														<tr>
															<td align="right">&nbsp;&nbsp;${p.name}</td>
															<td align="left" nowrap>--&gt;</td>
															<td align="left" width="100%">
																<c:choose>
																	<c:when test="${model.mappings[p.name] == null}">
																		<span style="color:red; font-style:italic;"><spring:message code="reporting.undefined" /></span>
																	</c:when>
																	<c:otherwise>${model.mappings[p.name]}</c:otherwise>
																</c:choose>
															</td>
														</tr>
													</c:forEach>
												</table>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										${model.nullValueLabel}
									</c:otherwise>
								</c:choose>
							</div>
						
						</c:otherwise>
					</c:choose>
					
				</div>
				
			</c:otherwise>
			
		</c:choose>
		
	</c:otherwise>
	
</c:choose>
