<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<div style="border-bottom: 1px solid black;">
	<ul id="menu">
		<li class="first">
			<a href="index.htm" style="text-decoration:none;"><spring:message code="@MODULE_ID@.title" /></a>
		</li>
		<openmrs:extensionPoint pointId="org.openmrs.module.@MODULE_ID@.localHeader" type="html">
				<c:forEach items="${extension.links}" var="link">
					<li <c:if test="${fn:endsWith(pageContext.request.requestURI, link.key)}">class="active"</c:if> >
						<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>
					</li>
				</c:forEach>
		</openmrs:extensionPoint>
	</ul>
</div>