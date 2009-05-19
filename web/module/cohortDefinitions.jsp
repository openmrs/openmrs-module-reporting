<%@ include file="/WEB-INF/template/include.jsp"%>

CohortDefinitions<br/>
<ul>
	<c:forEach items="${cohortDefinitions}" var="cd">
		<li>${cd.name}</li>
	</c:forEach>
</ul>
<hr/>
Types<br/>
<c:forEach items="${types}" var="type">
	${type}<br/>
</c:forEach>
