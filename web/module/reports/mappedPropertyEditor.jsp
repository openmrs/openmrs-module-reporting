<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		$('#parameterizableSelector').change(function(event){
			var currVal = $(this).val();
			if (currVal != '') {
				document.location.href='mappedPropertyEditor.form?parentType=${parentType.name}&parentUuid=${parent.uuid}&mappedProperty=${mappedProperty}&collectionKey=${collectionKey}&childUuid='+currVal;
			}
			else {
				$("#mapParameterSection").html('');
				$("#childUuidField").val('');
			}
		});

		<c:forEach var="p" items="${child.parameters}" varStatus="varstatus">
			$('#typeSelector_${p.name}').change(function(event) {
				$('#typeSelector_${p.name}_fixed').hide();
				$('#typeSelector_${p.name}_mapped').hide();
				$('#typeSelector_${p.name}_complex').hide();
				var val = $('#typeSelector_${p.name}').val();
				$('#typeSelector_${p.name}_'+val).show();
			});
			$('#typeSelector_${p.name}').trigger('change');
		</c:forEach>

		$('#mapParametersFormCancelButton').click(function(event){
			closeReportingDialog(false);
		});

		$('#mapParametersFormSubmitButton').click(function(event){
			$('#mapParametersForm').submit();
		});

	});
</script>

${childType.simpleName}: <rpt:widget id="parameterizableSelector" name="parameterizableSelector" type="${childType.name}" defaultValue="${child}"/>

<form id="mapParametersForm" method="post" action="saveMappedParameters.form">
	<input type="hidden" name="parentType" value="${parentType.name}"/>
	<input type="hidden" name="parentUuid" value="${parent.uuid}"/>
	<input type="hidden" name="mappedProperty" value="${mappedProperty}"/>
	<input type="hidden" name="collectionKey" value="${collectionKey}"/>
	<input type="hidden" id="childUuidField" name="childUuid" value="${child.uuid}"/>
	
	<div id="mapParameterSection">
		<table>								
			<c:forEach var="p" items="${child.parameters}" varStatus="varstatus">
				<tr>
					<td  valign="top" align="right">
						${p.name}:&nbsp;
					</td>
					<td valign="top">
						<c:choose>
							<c:when test="${empty allowedParams[p.name]}">
								<input type="hidden" name="valueType_${p.name}" value="fixed"/>
								<rpt:widget id="fixedValue_${p.name}" name="fixedValue_${p.name}" type="${p.clazz.name}" defaultValue="${fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
							</c:when>
							<c:otherwise>
								<select id="typeSelector_${p.name}" name="valueType_${p.name}">
									<option value="fixed" <c:if test="${mappedParams[p.name] == null && complexParams[p.name] == null}">selected</c:if>>
										Value:
									</option>
									<option value="mapped" <c:if test="${mappedParams[p.name] != null}">selected</c:if>>
										Parameter:
									</option>
									<option value="complex" <c:if test="${complexParams[p.name] != null}">selected</c:if>>
										Expression:
									</option>
								</select>
								<span id="typeSelector_${p.name}_fixed" style="display:none;">
									<rpt:widget id="fixedValue_${p.name}" name="fixedValue_${p.name}" type="${p.clazz.name}" defaultValue="${fixedParams[p.name]}" attributes="style=vertical-align:top;"/>
								</span>
								<span id="typeSelector_${p.name}_mapped" style="display:none;">
									<select name="mappedValue_${p.name}">
										<option value="" <c:if test="${mappedParams[p.name] == null}">selected</c:if>>Choose...</option>
										<c:forEach var="parentParam" items="${allowedParams[p.name]}">
											<option value="${parentParam}" <c:if test="${mappedParams[p.name] == parentParam}">selected</c:if>>
												${parentParam}
											</option>
										</c:forEach>
									</select>
								</span>
								<span id="typeSelector_${p.name}_complex" style="display:none;">
									<input type="text" name="complexValue_${p.name}" size="40" value="${complexParams[p.name]}"/>
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
		<input type="button" id="mapParametersFormCancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
		<input type="button" id="mapParametersFormSubmitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
	</div>
</form>