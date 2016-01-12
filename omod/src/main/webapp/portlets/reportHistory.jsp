<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%--
	available properties:
		howMany -> (integer, default 10) how many rows to show, defaults to 10
		showAllLink -> (boolean, default true) whether or not to show a link to [See All]
		showDecoration -> (boolean, default true) whether or not to show this in a decorated box.
--%>

<c:set var="title" value="${model.title}"/>
<c:set var="howManyRows" value="${model.howManyRows}"/>
<c:set var="showAllLink" value="${model.showAllLink}"/>
<c:set var="showDecoration" value="${model.showDecoration}"/>
<c:if test="${title == null}">
    <c:set var="title" value="Recent Reports"/>
</c:if>
<c:if test="${howManyRows == null}">
	<c:set var="howManyRows" value="10"/>
</c:if>
<c:if test="${showAllLink == null}">
	<c:set var="showAllLink" value="true"/>
</c:if>
<c:if test="${showDecoration == null}">
	<c:set var="showDecoration" value="true"/>
</c:if>

<c:if test="${showDecoration}">
	<div class="portlet">
		<div class="portlet-header">
			${ title }
		</div>
		<div class="portlet-content">
</c:if>

<table cellspacing="0" cellpadding="2">
	<c:forEach var="r" items="${model.completedRequests}" begin="0" end="${howManyRows}">
		<c:choose>
			<c:when test="${model.isWebRenderer[r]}">
				<c:set var="openImageFilename" value="/moduleResources/reporting/images/report_icon.gif"/>
			</c:when>
			<c:otherwise>
				<c:set var="openImageFilename" value="/images/file.gif"/>
			</c:otherwise>
		</c:choose>
		<tr valign="top">
			<td style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;">
				<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistoryOpen.form?uuid=${r.uuid}">
					<img src='<c:url value="${openImageFilename}"/>' border="0" width="16" height="16"/>
					${r.reportDefinition.parameterizable.name}
				</a>
				<br/>
				<c:if test="${r.description != null}">
					<span class="small">
						<span class="faded">
							<spring:message code="reporting.reportHistory.description" />
						</span>
						${r.description}
					</span>
				</c:if>
			</td>
			<td style="border-bottom: 1px #c0c0c0 solid">
				<table class="small" cellspacing="0" cellpadding="0">
					<c:forEach var="p" items="${r.reportDefinition.parameterMappings}">
						<tr valign="top">
							<td class="faded" align="right">
								${p.key}:
							</td>
							<td>
								<rpt:format object="${p.value}"/>
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
			<td style="border-bottom: 1px #c0c0c0 solid" class="small" align="right">
				<rpt:timespan then="${r.requestDate}"/>
			</td>
		<tr>
	</c:forEach>
	<c:if test="${showAllLink}">
		<tr>
			<td colspan="3" align="center">
				<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistory.form?">
					<spring:message code="reporting.Report.showAll"/>
				</a>
			</td>
		</tr>
	</c:if>
</table>

<c:if test="${showDecoration}">
		</div>
	</div>
</c:if>
