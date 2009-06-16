<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="localHeader.jsp"%>

<h1>Lab Encounter Report</h1>
<form method="get" action="generateReport.form">

<table>
	<tr>
		<td<label for="endDate">Start Date:</label></td>
		<td><input name="startDate" value="01/01/2009"/></td>
	</tr>
	<tr>
		<td><label for="endDate">End Date:</label></td>
		<td><input name="endDate" value="01/31/2009"/></td>
	</tr>
	<tr>
		<td><label for="locationId">Location:</label></td>
		<td>
			<select name="locationId">
				<option value="0">All Locations</option>
				<option value="0">--------------------------------</option>
				<c:forEach var="location" items="${locations}">
					<option value="${location.locationId}">${location.name}</option>
				</c:forEach>
			</select>		
		</td>
	</tr>
	<tr>
		<td>
			<label for="outputType">Renderer:</label>
		</td>
		<td>
			<input type="radio" name="renderType" value="XLS" checked> XLS
			<input type="radio" name="renderType" value="CSV"> CSV
		</td>
	</tr>
	<tr style="padding-top: 10px">
		<td></td>
		<td align="right">
			<input name="uuid" type="hidden" value="0123456789"/>			
			<input type="submit" value="Generate"/>
		</td>
	</tr>
</table>	
	
	
</form>