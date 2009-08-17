<%@ include file="../include.jsp"%>

<form id="mapParametersForm" method="post" action="saveMappedParameters.form">
	<input type="hidden" name="parentUuid" value="${parentValue.uuid}"/>
	<input type="hidden" name="parentType" value="${parentType.name}"/>
	<input type="hidden" name="childUuid" value="${childValue.uuid}"/>
	<input type="hidden" name="childType" value="${childType.name}"/>
	<div>
		<table>								
			<c:forEach var="p" items="${childValue.parameters}" varStatus="varstatus">
				<tr>
					<td  valign="top" align="right">
						${p.name} = 
					</td>
					<td valign="top">
						<select id="${p.name}_linkedParameter" name="${p.name}_linkedParameter">
							<option value="">Set Fixed Value: </option>
							<c:forEach var="parentParam" items="${parentValue.parameters}">
								<c:if test="${parentParam.clazz.name == p.clazz.name}">								
									<option value="${parentParam.name}">Set From Parameter - ${parentParam.name}</option>
								</c:if>
							</c:forEach>
						</select>
						<rpt:widget id="${p.name}_value" name="${p.name}_value" type="${p.clazz.name}" defaultValue="${p.defaultValue}" attributes="style=vertical-align:top;"/>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<input type="submit"/>
</form>