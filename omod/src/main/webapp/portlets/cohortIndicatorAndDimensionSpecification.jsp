<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:if test="${model.dialog != 'false'}">
	<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
</c:if>

<c:choose>
	<c:when test="${model.mode == 'details'}">
		<table style="width:100%" class="box">
			<thead>
				<tr>
					<th nowrap style="border-bottom:1px solid black;">Indicator #</th>
					<th style="width:100%; border-bottom:1px solid black;">Label</th>
				</tr>
			</thead>
			<c:forEach items="${model.dsd.columnsBySpecification[model.specification]}" var="c" varStatus="colStatus">
			<tbody>
				<tr>
					<td nowrap>${c.name}</td>
					<td style="width:100%">${c.label}</td>
				</tr>
			</tbody>			
			</c:forEach>
		</table>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" charset="utf-8">
			
			function checkAll(className) {
				$j(".checkbox" + className).each(function() {
					$j(this).attr('checked', 'checked');
				});
			}
			
			function uncheckAll(className) {
				$j(".checkbox" + className).each(function() {
					$j(this).removeAttr('checked');
				});
			}
			
			$j(document).ready(function() {
		
				$j('#mapParametersFormCancelButton_${model.id}').click(function(event){
					closeReportingDialog(false);
				});
		
				$j('.dimensionBox').each(function() {
					var val = $j(this).val();
					if ('${model.newDimensions}'.indexOf(val) != -1) {
						$j(this).attr('checked', 'checked');
					}
				});
		
				$j('#indicator${model.id}').change(function(event){
					var currVal = $j(this).val();
					if (currVal != '') {
						var indNum = $j('#indicatorNumber${model.id}').val();
						var label = $j('#label${model.id}').val();
						var dims = '';
						$j('.dimensionBox').each(function(){
							if ($j(this).attr('checked') != '') {
								dims += (dims == '' ? '' : ',') + $j(this).val();
							}
						});
						document.location.href='<c:url value="/module/reporting/viewPortlet.htm?id=editInd${model.id}&url=cohortIndicatorAndDimensionSpecification&parameters.dsdUuid=${model.dsdUuid}&parameters.index=${model.index}&parameters.newIndNum='+indNum+'&parameters.newLabel='+label+'&parameters.newDimensions='+dims+'&parameters.mappedUuid='+currVal+'"/>';
					}
					else {
						$j("#mapParameterSection${model.id}").html('');
					}
				});
		
				<c:forEach var="p" items="${model.mappedObj.parameters}" varStatus="varstatus">
					$j('#typeSelector_${p.name}_${model.id}').change(function(event) {
						$j('#typeSelector_${p.name}_fixed_${model.id}').hide();
						$j('#typeSelector_${p.name}_mapped_${model.id}').hide();
						$j('#typeSelector_${p.name}_complex_${model.id}').hide();
						var val = $j('#typeSelector_${p.name}_${model.id}').val();
						$j('#typeSelector_${p.name}_'+val+'_${model.id}').show();
					});
					$j('#typeSelector_${p.name}_${model.id}').trigger('change');
				</c:forEach>
		
				$j('#mapParametersFormSubmitButton_${model.id}').click(function(event){
					var existingKeys = [<c:forEach items="${model.existingKeys}" var="c" varStatus="cStat">'${c}'<c:if test="${!cStat.last}">,</c:if></c:forEach>];
					var initialNum = '<spring:message javaScriptEscape="true" text="${model.specification.indicatorNumber}"/>';
					var newNum = $j('#indicatorNumber${model.id}').val();
					if (initialNum != newNum) {
						for (var i=0; i<existingKeys.length; i++) {
							if (existingKeys[i] == newNum) {
								alert('That Indicator Number is already in use, please choose another.')
								return false;
							}
						}
					}
					$j('#mapParametersForm${model.id}').submit();
				});
		
			});
		</script>
		
		<form id="mapParametersForm${model.id}" method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/cohortIndicatorAndDimensionAddSpecification.form">
			<input type="hidden" name="dsdUuid" value="${model.dsd.uuid}"/>
			<input type="hidden" name="index" value="${model.index}"/>
			
			<table width="100%">
				<tr>
					<td style="padding-right:10px; white-space:nowrap;">Indicator #</td>
					<td width="100%">
						<input type="text" id="indicatorNumber${model.id}" tabindex="1" name="indicatorNumber" value="${model.newIndNum}" size="6"/>
					</td>
				</tr>
				<tr>
					<td style="padding-right:10px; white-space:nowrap;">Label</td>
					<td width="100%"><input type="text" id="label${model.id}" tabindex="2" name="label" value="${model.newLabel}" size="50"/></td>
				</tr>
				<tr>
					<td style="padding-right:10px; white-space:nowrap; vertical-align:top;">Indicator</td>
					<td width="100%">
						<wgt:widget id="indicator${model.id}" name="indicator" type="org.openmrs.module.reporting.indicator.CohortIndicator" defaultValue="${model.mappedObj}" attributes="tag=${model.tag}"/>
						
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
													<wgt:widget id="fixedValue_${p.name}_${model.id}" name="fixedValue_${p.name}" type="${p.type.name}" defaultValue="${model.fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
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
														<wgt:widget id="fixedValue_${p.name}_${model.id}" name="fixedValue_${p.name}" type="${p.type.name}" defaultValue="${model.fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
													</span>
													<span id="typeSelector_${p.name}_mapped_${model.id}" style="display:none;">
														<select name="mappedValue_${p.name}">
															<option value="" <c:if test="${model.mappedParams[p.name] == null}">selected</c:if>>Choose...</option>
															<c:forEach var="parentParam" items="${model.allowedParams[p.name]}">
																<option value="${parentParam.key}" <c:if test="${model.mappedParams[p.name] == parentParam.key}">selected</c:if>>
																	${parentParam.value}
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
					</td>
				</tr>
			</table>
			
			<c:if test="${fn:length(model.dsd.dimensions) > 0}">			
				<table>
					<tr><td style="padding:15px;"colspan="${fn:length(model.dsd.dimensions)}">Which dimensions do you want to include?</td></tr>
					<tr valign="top">
						<c:forEach var="dim" varStatus="dimStatus" items="${model.dsd.dimensions}">
							<td style="padding-right:20px;">
								<span style="font-weight:bold; text-decoration:underline;">${dim.key}</span>
								&nbsp;&nbsp;<a href="javascript:checkAll('${dimStatus.index}');"><spring:message code="reporting.all"/></a>
								&nbsp;&nbsp;<a href="javascript:uncheckAll('${dimStatus.index}');"><spring:message code="reporting.none"/></a>
								<br/>
								<c:forEach var="dimOpt" items="${dim.value.parameterizable.cohortDefinitions}">
									<input type="checkbox" class="dimensionBox checkbox${dimStatus.index}" name="dimensions" value="${dim.key}^${dimOpt.key}">&nbsp;${dimOpt.key}<br/>
								</c:forEach>
								<input type="checkbox" class="dimensionBox checkbox${dimStatus.index}" name="dimensions" value="${dim.key}^?">&nbsp;<spring:message code="reporting.dimension.unclassified"/>
							</td>
						</c:forEach>
					</tr>
				</c:if>
			</table>
		
			<hr style="color:blue;"/>
			<div style="width:100%; text-align:left;">
				<input type="button" id="mapParametersFormSubmitButton_${model.id}" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
				<input type="button" id="mapParametersFormCancelButton_${model.id}" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
			</div>
		</form>
	</c:otherwise>
</c:choose>