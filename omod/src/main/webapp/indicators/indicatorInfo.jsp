<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/dialogSupport.jsp" %>

<style type="text/css">
	.indInfo td { text-align: right; font-size: 0.8em; }
	.indInfo td + td { text-align: left; font-size: 1.0em; }
</style>

<div style="float:left">
	<table class="indInfo">
		<tr>
			<td><spring:message code ="reporting.indicatorInfo.title" /></td>
			<td><h2>${indicator.name}</h2></td>
		</tr>
		<tr>
			<td><spring:message code ="reporting.description" /></td>
			<td>
				<h4>
					<c:if test="${empty indicator.description}">
						<spring:message code ="reporting.none" />
					</c:if>
					${indicator.description}
				</h4>
			</td>
		</tr>
	</table>
	
	<br/>
	
	<table cellpadding="10">
		<tr valign="top">
			<td>
				<fieldset>
					<legend><spring:message code ="reporting.cohortDefinition" /></legend>
					${indicator.cohortDefinition.parameterizable.name}<br/>
					<small>
						<c:forEach var="m" items="${indicator.cohortDefinition.parameterMappings}">
							&nbsp;&nbsp;&nbsp;&nbsp;${m.key}->${m.value}<br/>
						</c:forEach>
					</small>
				</fieldset>
			</td>
			<td>
				<c:if test="${not empty indicator.aggregator}">
						<legend><spring:message code ="reporting.aggregation" /></legend>
						${indicator.aggregator.name}
					</fieldset>
				</c:if>
				<fieldset>
					<legend><spring:message code ="reporting.dimensions" /></legend>
					<small><spring:message code ="reporting.toDo" /></small>
				</fieldset>
			</td>
		</tr>
	</table>
</div>

<div style="float:left; margin-top: 2em; margin-left: 3em">
	<a href="#" onClick="navigateParent('${pageContext.request.contextPath}/module/reporting/indicators/indicatorHistory.form?indicators=${indicator.uuid}&location=${location.locationId}')">
		[<spring:message code ="reporting.ViewLas6Months" />]
	</a>
</div>


<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>
