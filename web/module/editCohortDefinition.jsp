<%@ include file="/WEB-INF/template/include.jsp"%>

<h4>Create / Edit Cohort Definition</h4>
<hr/>

${cohortDefinition.id}
${cohortDefinition.name}
${cohortDefinition.description}
<c:forEach items="${cohortDefinition.availableParameters}" var="p">
	${p.name} ${p.defaultValue} (${p.clazz})
</c:forEach>
