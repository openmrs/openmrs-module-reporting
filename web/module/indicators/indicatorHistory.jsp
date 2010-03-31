<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/indicators/indicatorHistory.form" />
<%@ include file="../run/localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/reporting/scripts/flot/jquery.flot.js"/>

<!-- Commenting this out for now because the blank indicator created by the HTML widget is messing it up
  <h2>
	<c:choose>
		<c:when test="${fn:length(query.indicators) == 1}">
			${query.indicators[0].name}
		</c:when>
		<c:otherwise>
			History of Indicators
		</c:otherwise>
	</c:choose>
</h2>
-->

<h2>History of Indicators</h2>

<!--  move the options form to the  right if we are displaying a graph -->
<c:choose>
	<c:when test="${dataSet != null}">
		<div style="position:absolute; left:700px; border: 1px black solid; padding: 20px">
	</c:when>
	<c:otherwise>
		<div style="border: 1px black solid; padding: 20px">
	</c:otherwise>
</c:choose>

	<form:form method="get" commandName="query" action="indicatorHistory.form">
	
	<table>
		<tr>
			<td align="right">
				Where?
			</td>
			<td>
				<form:select path="location">
					<form:option value=""/>
					<form:options items="${locations}" itemLabel="name" itemValue="locationId"/>
				</form:select>
			</td>
		</tr>
		<tr>
			<td align="right">
				When?
			</td>
			<td>
				<form:select path="lastMonths">
					<form:option value="6">Last 6 months</form:option>
					<form:option value="12">Last 12 months</form:option>
					<form:option value="24">Last 24 months</form:option>
				</form:select>
			</td>
		</tr>
		<tr valign="top">
			<td align="right">
				Which<br/>Indicators?
			</td>
			<td>
				<wgt:widget id="indicators" name="indicators" type="java.util.List" genericTypes="org.openmrs.module.reporting.indicator.Indicator" defaultValue="${query.indicators}"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<br/>
				<input type="submit" value="Calculate"/>
			</td>
		</tr>
	</table>

	</form:form>
	
</div>

<div id="indicatorHistoryGraph" style="margin: 20px; width: 640px; height: 400px"></div>

<c:if test="${dataSet != null}">

<div style="margin: 20px;">
<table>
	<tr>
		<td align="right" valign="top">Indicators:</td>
		<td><div id="indicatorHistoryLegend"></div></td>
	</tr>
</table>
</div>

	<script type="text/javascript">
		var series = [];
		<c:forEach var="col" items="${dataSet.definition.columns}">
			<c:if test="${col.name != 'startDate' && col.name != 'endDate' && col.name != 'location'}">
				series.push({ label: "${col.indicator.parameterizable.name}", data: [] });
			</c:if>
		</c:forEach>
		<rpt:forEach var="row" items="${dataSet}">
			<c:set var="startDate" value="${row.columnValuesByKey['startDate'].time}"/>
			<c:set var="ind" value="0"/>
			<c:forEach var="column" items="${row.columnValues}">
				<c:if test="${column.key.name != 'startDate' && column.key.name != 'endDate' && column.key.name != 'location'}">
					series[${ind}].data.push([ ${startDate} , ${column.value.value} ]);
					<c:set var="ind" value="${ind + 1}"/>
				</c:if>
			</c:forEach>
		</rpt:forEach>
		
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