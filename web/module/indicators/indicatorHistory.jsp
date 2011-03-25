<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/indicators/indicatorHistory.form" />
<%@ include file="../run/localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/reporting/scripts/flot/jquery.flot.js"/>

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

<!--  move the options form to the  right if we are displaying a graph -->
<c:choose>
	<c:when test="${dataSet != null}">
		<div style="position:absolute; left:700px; border: 1px black solid; padding: 20px">
	</c:when>
	<c:otherwise>
		<div style="border: 1px black solid; padding: 20px">
	</c:otherwise>
</c:choose>
	<c:if test="${error != null}"><span class="error">${error}</span></c:if>  <!-- quick hack error check -->
	<springform:form method="get" commandName="query" action="indicatorHistory.form">
	
	<table>
		<tr>
			<td align="right">
				Where?
			</td>
			<td colspan="2">
				<springform:select path="location">
					<springform:option value=""/>
					<springform:options items="${locations}" itemLabel="name" itemValue="locationId"/>
				</springform:select>
			</td>
		</tr>
		<tr>
			<td></td><td>Start date</td><td>End date</td>
		</tr>
		<tr>
			<td align="right">
				Date Range?
			</td>
			<td>
				<wgt:widget id="startDate" name="startDate" type="java.util.Date" defaultValue="${query.startDate}" />
				<springform:errors path="startDate" cssClass="error" />
			</td>
			<td>
				<wgt:widget id="endDate" name="endDate" type="java.util.Date" defaultValue="${query.endDate}" />
				<springform:errors path="endDate" cssClass="error" />
			</td>
		</tr>
		<tr valign="top">
			<td align="right">
				Which Indicators?
			</td>
			<td colspan="2">
				<wgt:widget id="indicators" name="indicators" type="java.util.List" genericTypes="org.openmrs.module.reporting.indicator.Indicator" defaultValue="${query.indicators}"/>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="2">
				<br/>
				<input type="submit" value="Calculate"/>
			</td>
		</tr>
	</table>

	</springform:form>
	
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