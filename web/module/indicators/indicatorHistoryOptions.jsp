<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/module/reporting/indicators/indicatorHistoryOptions.form" />
<%@ include file="../run/localHeader.jsp"%>

<h1>Indicator History</h1>

<form:form commandName="query" action="indicatorHistory.form">
	
	<br/>
	
	<table>
		<tr>
			<td>
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
			<td>
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
		<tr>
			<td>
				Indicator:
			</td>
			<td>
				<%--
				<select name="indicators" multiple="true" size="10">
					<c:forEach var="ind" items="${indicators}">
					</c:forEach>
				</select>
				--%>
				<form:select path="indicatorUuid">
					<form:option value=""/>
					<form:options items="${indicators}" itemLabel="name" itemValue="uuid"/>
				</form:select>
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

<%@ include file="/WEB-INF/template/footer.jsp"%>