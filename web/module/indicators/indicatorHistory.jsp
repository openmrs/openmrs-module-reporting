<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/indicators/indicatorHistoryOptions.form" />
<%@ include file="../run/localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/reporting/scripts/flot/jquery.flot.js"/>

<h1>Indicator History</h1>

<div style="float: right; border: 1px black solid; padding: 20px">
	<form:form method="get" commandName="query" action="indicatorHistoryOptions.form">
		<form:hidden path="location"/>
		<form:hidden path="lastMonths"/>
		<c:forEach var="ind" items="${query.indicators}">
			<input type="hidden" name="indicators" value="${ind.uuid}"/>
		</c:forEach>
		<input type="submit" value="Change Options"/>
	</form:form>
	<table>
		<tr>
			<td align="right">Location:</td>
			<td>${query.location.name}</td>
		</tr>
		<tr>
			<td align="right">Indicators:</td>
			<td><div id="indicatorHistoryLegend"></div></td>
		</tr>
	</table>
</div>

<div id="indicatorHistoryGraph" style="margin: 20px; width: 640px; height: 400px"></div>

<form:form method="get" action="indicatorHistory.form" commandName="query">
	<form:hidden path="location"/>
	<c:forEach var="ind" items="${query.indicators}">
		<input type="hidden" name="indicators" value="${ind.uuid}"/>
	</c:forEach>
	When?
	<form:select path="lastMonths" onchange="submit()">
		<form:option value="6">Last 6 months</form:option>
		<form:option value="12">Last 12 months</form:option>
		<form:option value="24">Last 24 months</form:option>
	</form:select>
</form:form>

<c:if test="${dataSet != null}">
	<script type="text/javascript">
		var series = [];
		<c:forEach var="col" items="${dataSet.definition.columns}">
			<c:if test="${col.columnKey != 'startDate' && col.columnKey != 'endDate' && col.columnKey != 'location'}">
				series.push({ label: "${col.indicator.parameterizable.name}", data: [] });
			</c:if>
		</c:forEach>
		<c:forEach var="row" items="${dataSet.iterator}">
			<c:set var="startDate" value="${row.columnValuesByKey['startDate'].time}"/>
			<c:set var="ind" value="0"/>
			<c:forEach var="column" items="${row.columnValues}">
				<c:if test="${column.key.columnKey != 'startDate' && column.key.columnKey != 'endDate' && column.key.columnKey != 'location'}">
					series[${ind}].data.push([ ${startDate} , ${column.value.value} ]);
					<c:set var="ind" value="${ind + 1}"/>
				</c:if>
			</c:forEach>
		</c:forEach>
		
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