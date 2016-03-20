<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.form" />
<%@ include file="../manage/localHeader.jsp"%>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.createRowPerPatient" /></h1>
		<form method="post" action="logicReportCreate.form">
			<table>
				<tr>
					<th><spring:message code="general.name"/></th>
					<td><input type="text" name="name" size="40"/></td>
				</tr>
				<tr valign="top">
					<th><spring:message code="general.description"/></th>
					<td><textarea name="description" rows="8" cols="50"></textarea></td>
				</tr>
				<tr>
					<th></th>
					<td><input type="submit" value="<spring:message code="general.save"/>"/></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
