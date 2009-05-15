<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="View Reports" otherwise="/login.htm" redirect="/dataSetViewer.form" />

<spring:message var="pageTitle" code="Data Set Viewer" scope="page"/>
<%@ include file="/WEB-INF/template/header.jsp" %>

<style>
	table.dataTable { border: 1px solid black; }
	th.dataTable {border: 2px solid black; background-color: #CCCCCC; text-align:center; }
	td.dataTable {border: 1px solid black; }
	th,td { text-align: left; vertical-align:top; padding: 5px; }
</style>

<div id="datasetViewer">
	<ul>
		<li><a href="${pageContext.request.contextPath}/reportDashboard.form">Report Dashboard</a></li>
		<li><a href="${pageContext.request.contextPath}/dataSetViewer.form">Data Set Viewer</a></li>
	</ul>
	<h1>Data Set Viewer</h1>
	<form>
		<br/>
		<table border="0">
			<tr>
				<th>Select Cohort</th>
				<td valign="top">
					<select name="cohortKey">
						<option value="">All Patients</option>
						<c:forEach items="${cohortDefinitions}" var="cd">
							<option value="${cd.class}:${cd.id}" <c:if test="${cd.id == param.cohortKey}">selected</c:if>>
								${cd.name}
							</option>					
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<th>Select DataSet Definition</th>
				<td>
					<select name="datasetKey">
						<option value="">Choose...</option>
						<c:forEach items="${datasetDefinitions}" var="datasetDefinition">
							<option value="${datasetDefinition.key}" <c:if test="${datasetDefinition.key == param.datasetKey}">selected</c:if>>
								${datasetDefinition.name}
						</option>					
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="hidden" name="autoSubmit" value="true"/>
					<input type="submit" value="Generate Data"/>
				</td>
			</tr>
		</table>
	</form>
	<hr/>
	<c:if test="${param.autoSubmit == 'true'}">
		<table cellspacing="0" cellpadding="0" class="dataTable">
			<tr>
				<c:forEach var="column" items="${dataset.definition.columns}">
					<th class="dataTable">${column.key}</th>
				</c:forEach>
			</tr>
			<openmrs:forEachRecord name="iterable" items="${dataset}">
				<tr>
					<c:forEach var="column" items="${dataset.definition.columns}">
						<td class="dataTable"><b>${record[column]}</b></td>
					</c:forEach>											
				</tr>
			</openmrs:forEachRecord>
		</table>
	</c:if>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>