<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/createInitial.form" />
<%@ include file="../manage/localHeader.jsp"%>

<h2>Create Initial Queries</h2>

	<c:if test="${fn:length(toCreate) > 0}">
		<form method="post">
			<b class="boxHeader">Available</b>
			<div class="box">
				<table>
					<c:forEach var="e" items="${toCreate}">
						<tr valign="baseline">
							<td><i>${e.clazz.simpleName}:</i></td>
							<td><big>${e.name}</big></td>
						</tr>
						<input type="hidden" name="create" value="${e.clazz.name} ${e.name}"/>
					</c:forEach>
				</table>
				<input type="submit" value="Create"/>
			</div>
		</form>
	</c:if>
	
	<b class="boxHeader">Already Created</b>
	<div class="box">
		<table>
		<c:forEach var="e" items="${already}">
			<tr valign="baseline">
				<td><i>${e.clazz.simpleName}:</i></td>
				<td><big>${e.name}</big></td>
			</tr>
		</c:forEach>
		</table>
	</div>

<%--
<form method="post">
	<h2>Create Initial Queries</h2>
	
	<b class="boxHeader">Cohort Definitions</b>
	<table>
		<c:forEach var="def" items="${cohortDefinitions}">
			<tr>
				<td align="right"><input type="checkbox" name="cohortDefinitions" value="${def.name}"/></td>
				<td>${def.name}</td>
			</tr>
		</c:forEach>
		<c:forEach var="def" items="${cohortDefinitionsAlready}">
			<tr>
				<td align="right">[X]</td>
				<td>${def.name}</td>
			</tr>
		</c:forEach>
	</table>
	
	<br/>
	
	<b class="boxHeader">Dimensions</b>
	<table>
		<c:forEach var="dim" items="${dimensions}">
			<tr>
				<td align="right"><input type="checkbox" name="dimension" value="${dim.name}"/></td>
				<td>${dim.name}</td>
			</tr>
		</c:forEach>
		<c:forEach var="dim" items="${dimensionsAlready}">
			<tr>
				<td align="right">[X]</td>
				<td>${dim.name}</td>
			</tr>
		</c:forEach>
	</table>

	<br/>
	
	<b class="boxHeader">Indicators</b>
	<table>
		<c:forEach var="ind" items="${indicators}">
			<tr>
				<td align="right"><input type="checkbox" name="dimension" value="${ind.name}"/></td>
				<td>${ind.name}</td>
			</tr>
		</c:forEach>
		<c:forEach var="ind" items="${indicatorsAlready}">
			<tr>
				<td align="right">[X]</td>
				<td>${ind.name}</td>
			</tr>
		</c:forEach>
	</table>

		<br/>
	
	<input type="submit" value="Create"/>
</form>
--%>

<%@ include file="/WEB-INF/template/footer.jsp"%>