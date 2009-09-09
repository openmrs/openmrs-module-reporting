<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/dialogSupport.jsp" %>

<style type="text/css">
	.indInfo td { text-align: right; font-size: 0.8em; }
	.indInfo td + td { text-align: left; font-size: 1.0em; }
</style>

<h2>${indicator.name}</h2>
<table class="indInfo" cellpadding="5">
	<tr valign="baseline">
		<td>Description</td>
		<td><span style="font-size: 1.5em">${indicator.description}</span></td>
	</tr>
	<tr valign="baseline">
		<td>Parameters</td>
		<td>
			<c:forEach var="p" items="${indicator.parameters}">
				${ p.name } (${p.type.simpleName})<br/>
			</c:forEach>
		</td>
	</tr>
	<c:if test="${not empty indicator.aggregator}">
		<tr valign="baseline">
			<td>Aggregation</td>
			<td>${indicator.aggregator.name}</td>
		</tr>
	</c:if>
	<c:if test="${not empty indicator.cohortDefinition}">
		<tr valign="baseline">
			<td>Cohort Definition</td>
			<td>
				${indicator.cohortDefinition.parameterizable.name}<br/>
				<small>
					<c:forEach var="m" items="${indicator.cohortDefinition.parameterMappings}">
						${m.key}->${m.value}
					</c:forEach>
				</small>
			</td>
		</tr>
	</c:if>
	<tr valign="baseline">
		<td></td>
		<td>
			<br/>
			<a href="#" onClick="navigateParent('${pageContext.request.contextPath}/module/reporting/indicators/indicatorHistory.form?indicators=${indicator.uuid}&location=${location.locationId}')">
				[View History]
			</a>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>