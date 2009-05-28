<%@ include file="/WEB-INF/template/include.jsp"%>

<h1>Lab Encounter Report</h1>
<form method="get" action="generateReport.form">
	
	<div>
		Start Date:
		<input name="startDate" value="01/01/2009"
	</div>
	<div>
		End Date:
		<input name="endDate" value="01/31/2009"
	</div>
	<div>
	</div>
	<div>
		Location:
		<select name="locationId">
			<option value="0">ALL LOCATIONS</option>
			<option value="0">--------------------------------</option>
			<c:forEach var="location" items="${locations}">
				<option value="${location.locationId}">${location.name}</option>
			</c:forEach>
		</select>
	</div>
	<div>
		<input name="uuid" type="hidden" value="0123456789"
		<input type="submit" value="Generate"/>
	</div>	
	
	
</form>