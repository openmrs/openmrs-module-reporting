<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%--
	available properties:
		showDecoration -> (boolean, default true) whether or not to show this in a decorated box.
		showIfNone -> (boolean, default true) whether to display an empty box is there are no saved reports
--%>

<c:set var="showDecoration" value="${model.showDecoration}"/>
<c:set var="showIfNone" value="${model.showIfNone}"/>
<c:if test="${showDecoration == null}">
	<c:set var="showDecoration" value="true"/>
</c:if>
<c:if test="${showIfNone == null}">
	<c:set var="showIfNone" value="true"/>
</c:if>

<c:if test="${showIfNone || fn:length(model.savedRequests) > 0}">

	<c:if test="${showDecoration}">
		<div class="portlet">
			<div class="portlet-header">
				<spring:message code="reporting.Report.savedReports.title" />
			</div>
			<div class="portlet-content">
	</c:if>
	
	<c:if test="${fn:length(model.savedRequests) == 0}">
		<spring:message code="reporting.none" />
	</c:if>
	
	<table cellspacing="0" cellpadding="2">
		<c:forEach var="r" items="${model.savedRequests}" varStatus="iterstatus">
			<c:choose>
				<c:when test="${model.isWebRenderer[r]}">
					<c:set var="openImageFilename" value="/moduleResources/reporting/images/report_icon.gif"/>
				</c:when>
				<c:otherwise>
					<c:set var="openImageFilename" value="/images/file.gif"/>
				</c:otherwise>
			</c:choose>
			<tr valign="top">
				<td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
					<a href="${pageContext.request.contextPath}/module/reporting/reports/reportHistoryOpen.form?uuid=${r.uuid}">
						<img src='<c:url value="${openImageFilename}"/>' border="0" width="16" height="16"/>
						${r.reportDefinition.parameterizable.name}
					</a>
					<br/>
					<c:if test="${r.description != null}">
						<span class="small">
							<span class="faded">
								<spring:message code="reporting.description" />
							</span>
							${r.description}
						</span>
					</c:if>
				</td>
				<td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
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
			<tr>
		</c:forEach>
	</table>
	
	<c:if test="${showDecoration}">
			</div>
		</div>
	</c:if>
</c:if>
