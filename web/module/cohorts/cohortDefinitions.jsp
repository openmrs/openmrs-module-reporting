<%@ include file="/WEB-INF/template/include.jsp"%>

CohortDefinitions<br/>
<ul>
	<c:forEach items="${cohortDefinitions}" var="cd">
		<li>
			<a href="editCohortDefinition.form?uuid=${cd.uuid}&type=${cd.class.name}">${cd.name}</a>
		</li>
	</c:forEach>
</ul>
<hr/>
<form method="get" action="editCohortDefinition.form">
	New:
	<select name="type">
		<option value="">&nbsp;</option>
		<c:forEach items="${types}" var="type">
			<option value="${type.name}">${type.name}</option>
		</c:forEach>
	</select>
	<input type="submit" value="Create"/>
</form>