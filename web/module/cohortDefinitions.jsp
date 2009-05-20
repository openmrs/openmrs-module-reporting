<%@ include file="/WEB-INF/template/include.jsp"%>

CohortDefinitions<br/>
<ul>
	<c:forEach items="${cohortDefinitions}" var="cd">
		<li>${cd.name}</li>
	</c:forEach>
</ul>
<hr/>
<form method="post" action="editCohortDefinition.form">
	New:
	<select name="type">
		<option value="">&nbsp;</option>
		<c:forEach items="${types}" var="type">
			<option value="${type.name}">${type.name}</option>
		</c:forEach>
	</select>
	<input type="submit" value="Create"/>
</form>