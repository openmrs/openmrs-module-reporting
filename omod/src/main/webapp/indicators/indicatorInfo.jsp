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
			<td>Title</td>
			<td><h2>${indicator.name}</h2></td>
		</tr>
		<tr>
			<td>Description</td>
			<td>
				<h4>
					<c:if test="${empty indicator.description}">
						None
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
					<legend>Cohort Definition</legend>
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
						<legend>Aggregation</legend>
						${indicator.aggregator.name}
					</fieldset>
				</c:if>
				<fieldset>
					<legend>Dimensions</legend>
					<small>To Do</small>
				</fieldset>
			</td>
		</tr>
	</table>
</div>

<div style="float:left; margin-top: 2em; margin-left: 3em">
	<a href="#" onClick="navigateParent('${pageContext.request.contextPath}/module/reporting/indicators/indicatorHistory.form?indicators=${indicator.uuid}&location=${location.locationId}')">
		[View last 6 months]
	</a>
</div>


<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>