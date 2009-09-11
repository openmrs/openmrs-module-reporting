<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/createInitial.form" />
<%@ include file="../manage/localHeader.jsp"%>

<h2>Create Initial Queries</h2>

	<form method="post">
		<div class="portlet">
			<div class="portlet-header">
				Available to create
			</div>
			<div class="portlet-content">
				<c:choose>
					<c:when test="${fn:length(toCreate) > 0}">
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
					</c:when>
					<c:otherwise>
						None
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</form>
	
	<span class="faded">
		<b>Already created:</b>
		<br/>
		<c:forEach var="e" items="${already}">
			${e.clazz.simpleName}:${e.name}<br/>
		</c:forEach>
	</span>

<%@ include file="/WEB-INF/template/footer.jsp"%>