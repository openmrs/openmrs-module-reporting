<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%--
	available properties:
		showDecoration -> (boolean, default true) whether or not to show this in a decorated portlet.
		showDescription -> (boolean, default false) whether or not to show the description
		title -> (String, default "Run a Report") title to put in the decoration
--%>

<c:set var="showDecoration" value="${model.showDecoration}"/>
<c:set var="showDescription" value="${model.showDescription}"/>
<c:set var="portletTitle" value="${model.title}"/>
<c:if test="${showDecoration == null}">
	<c:set var="showDecoration" value="true"/>
</c:if>
<c:if test="${showDescription == null}">
	<c:set var="showDescription" value="false"/>
</c:if>
<c:if test="${empty portletTitle}">
	<spring:message var="portletTitle" code="reporting.Report.run.title" />
</c:if>

<c:if test="${showDecoration}">
	<div class="portlet">
		<div class="portlet-header">
			${portletTitle}
		</div>
		<div class="portlet-content">
</c:if>

<table cellspacing="0" cellpadding="2">
	<c:forEach var="r" items="${model.reportDefinitions}" varStatus="status">
		<tr>
			<td width="100%">
				<a href="${pageContext.request.contextPath}/module/reporting/run/runReport.form?reportId=${r.uuid}">
					<c:out value="${r.name}" />
				</a>
			</td>
			<c:if test="${showDescription}">
				<td class="small">
					<c:out value="${r.description}" />
				</td>
			</c:if>
		</tr>
	</c:forEach>
</table>

<c:if test="${showDecoration}">
		</div>
	</div>
</c:if>