<%@ include file="/WEB-INF/template/include.jsp"%>

<h4>Create / Edit Cohort Definition</h4>
<hr/>

<form method="post" action="saveCohortDefinition.form">
	<input type="hidden" name="uuid" value="${cohortDefinition.uuid}" />
	<input type="hidden" name="type" value="${cohortDefinition.class.name}" />
	<table>
		<tr>
			<td>Type:</td>
			<td>${cohortDefinition.class.name} (${cohortDefinition.id})</td>
		</tr>
		<tr>
			<td>Name:</td>
			<td><input type="text" name="name" value="${cohortDefinition.name}" size="40" /></td>
		</tr>
		<tr>
			<td>Description:</td>
			<td><textarea name="description" rows="3" cols="40">${cohortDefinition.description}</textarea></td>
		</tr>
		<tr>
			<td colspan="2">
				<table cellpadding="5">
					<tr><th colspan="4">Parameters</th></tr>
					<tr><th align="left">Name</th><th align="left">Default Value</th><th align="left">Param?</th><th align="left">Required?</th></tr>
					<c:forEach items="${cohortDefinition.availableParameters}" var="p">
						<tr>
							<td>${p.name}</td>
							<td><openmrs:fieldGen type="${p.clazz}" formFieldName="parameter.${p.name}.defaultValue" val="${p.defaultValue}" parameters="" /></td>
							<td><input type="checkbox" name="parameter.${p.name}.allowAtEvaluation" /></td>
							<td>
								<c:choose>
									<c:when test="${p.required}">
										<input type="hidden" name="parameter.${p.name}.required" value="true" />[x]
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="parameter.${p.name}.required" /></td>
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
		<tr><td colspan="2"><input type="submit" value="Save" /></td></tr>
	</table>
</form>