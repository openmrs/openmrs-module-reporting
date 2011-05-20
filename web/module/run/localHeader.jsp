<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%@ include file="/WEB-INF/view/module/reporting/dialogSupport.jsp" %>

<script type="text/javascript">
	jqUiDecoration();
</script>

<c:if test="${ __openmrs_report_data != null && __openmrs_hide_report_link == null }">
	<div style="float: right; border: 1px solid #8FABC7; background-color: white; padding: 10px; position: absolute; right: 0px; top: 50px; z-index: 1">
		<div align="center">
			<a href="${__openmrs_last_report_url}">
				<img src="<c:url value='/moduleResources/reporting/images/report_icon.gif'/>" width=45 height=45 border="0"/>
				<br/><span class="small">back to report</span>
			</a>						
		</div>
	</div>
</c:if>
<div style="border-bottom: 1px solid black;">
	<openmrs:extensionPoint pointId="org.openmrs.module.reporting.run.localheader" type="html">
		<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
			<ul id="menu">
				<c:forEach items="${extension.links}" var="link" varStatus="status">
					<c:set var="linkSelected" value="${fn:contains(pageContext.request.requestURI, fn:substringBefore(link.key, '.'))}"/>
					<li class="<c:if test="${status.index == 0}">first</c:if> <c:if test="${linkSelected}">active</c:if>">
						<c:choose>
							<c:when test="${fn:startsWith(link.key, 'module/')}">
								<%-- Added for backwards compatibility for most links --%>
								<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>
							</c:when>
							<c:otherwise>
								<%-- Allows for external absolute links  --%>
								<a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a>
							</c:otherwise>
						</c:choose>
					</li>
				</c:forEach>
			</ul>
		</openmrs:hasPrivilege>
	</openmrs:extensionPoint>
</div>
