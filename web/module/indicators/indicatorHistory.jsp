<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/indicators/indicatorHistoryOptions.form" />
<%@ include file="../run/localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/reporting/scripts/flot/jquery.flot.js"/>

<h1>Indicator History</h1>

<div style="float: right; border: 1px black solid; padding: 20px">
	<form:form commandName="query" action="indicatorHistoryOptions.form">
		<form:hidden path="location"/>
		<form:hidden path="lastMonths"/>
		<form:hidden path="indicatorUuid"/>
		<input type="submit" value="Change Options"/>
	</form:form>
	<br/>
	Location: ${query.location.name}
	<br/>
	<br/>
	<div id="indicatorHistoryLegend"></div>
</div>

<div id="indicatorHistoryGraph" style="margin: 20px; width: 640px; height: 400px"></div>

<form:form action="indicatorHistory.form" commandName="query">
	<form:hidden path="location"/>
	<form:hidden path="indicatorUuid"/>
	When?
	<form:select path="lastMonths">
		<form:option value="6">Last 6 months</form:option>
		<form:option value="12">Last 12 months</form:option>
		<form:option value="24">Last 24 months</form:option>
	</form:select>
	<input type="submit" value="Calculate"/>
</form:form>

<c:if test="${dataSet != null}">
	<script type="text/javascript">
		var series = [];
		var data = [];
		<c:forEach var="row" items="${dataSet.iterator}">
			data.push([${row.columnValuesByKey['startDate'].time}, ${row.columnValuesByKey['indicator'].value}]);
			<%-- terrible hack --%>
			<c:forEach var="col" items="${row.columnValues}">
				<c:if test="${col.key.columnKey == 'indicator'}">
					<c:set var="indicatorName" value="${col.key.displayName}"/>
				</c:if>
			</c:forEach>
		</c:forEach>
		series.push( { label: "${indicatorName}", data: data} );

		var options = {
			xaxis: { mode: "time" },
			points: { show: true },
			lines: { show: true },
			//yaxis: { min: 0 },
			legend: { container: $('#indicatorHistoryLegend') }
		};
		$.plot($("#indicatorHistoryGraph"), series, options);
	</script>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>