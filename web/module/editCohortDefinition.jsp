<%@ include file="/WEB-INF/template/include.jsp"%>

<h4>Create / Edit Cohort Definition</h4>
<hr/>

Id: ${cohortDefinition.id}<br/>
Name: ${cohortDefinition.name}<br/>
Description: ${cohortDefinition.description}<br/>
Available Parameters:<br/>
<ul>
	<c:forEach items="${cohortDefinition.availableParameters}" var="p">
		<li>${p.name} ${p.defaultValue} (${p.clazz})</li>
	</c:forEach>
</ul>