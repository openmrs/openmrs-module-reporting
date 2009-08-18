<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		$('#parameterizableSelector').change(function(event){
			var currVal = $(this).val();
			if (currVal != '') {
				document.location.href='mapParameters.form?parentType=${parentType.name}&parentUuid=${parentValue.uuid}&childType=${childType.name}&childUuid='+currVal;
			}
			else {
				$("#mapParameterSection").html('');
			}
		});

		$('#mapParametersFormCancelButton').click(function(event){
			closeReportingDialog(false);
		});

		$('#mapParametersFormSubmitButton').click(function(event){
			$('#mapParametersForm').submit();
			closeReportingDialog(true);
		});

	});
</script>

${childType.simpleName}: <rpt:widget id="parameterizableSelector" name="parameterizableSelector" type="${childType.name}" defaultValue="${childValue}"/>

<form id="mapParametersForm" method="post" action="saveMappedParameters.form">
	<input type="hidden" name="parentUuid" value="${parentValue.uuid}"/>
	<input type="hidden" name="parentType" value="${parentType.name}"/>
	<input type="hidden" name="childUuid" value="${childValue.uuid}"/>
	<input type="hidden" name="childType" value="${childType.name}"/>
	<div id="mapParameterSection">
		<table>								
			<c:forEach var="p" items="${childValue.parameters}" varStatus="varstatus">
				<tr>
					<td  valign="top" align="right">
						${p.name} = 
					</td>
					<td valign="top">
						<select id="linkedParameter_${p.name}" name="linkedParameter_${p.name}">
							<option value="">Set Fixed Value: </option>
							<c:forEach var="parentParam" items="${parentValue.parameters}">
								<c:if test="${parentParam.clazz.name == p.clazz.name}">								
									<option value="${parentParam.name}">Set From Parameter - ${parentParam.name}</option>
								</c:if>
							</c:forEach>
						</select>
						<rpt:widget id="fixedValue_${p.name}" name="fixedValue_${p.name}" type="${p.clazz.name}" defaultValue="${p.defaultValue}" attributes="style=vertical-align:top;"/>
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