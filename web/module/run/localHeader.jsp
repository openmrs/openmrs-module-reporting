<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/dialogSupport.jsp" %>

<div style="float: right; border: 1px solid #ccc; background-color: #8FABC7; padding: 10px; position: absolute; absolute; right: 10px; bottom: 10px;">
	<c:if test="${__openmrs_report_data!=null}">
		<script>
			$(document).ready(function() {
				$("#currentReportDetails").draggable();
				$("#currentReportDetails").hide();
				$("#currentReportDetailsLink").click(function(event){
					var details = $("#currentReportDetails");
					var visible = details.is(':visible'); 
					if (visible) details.hide();
					else details.show();
				});
			});
		</script>
	
		<div id="currentReportHeader">
			Current Report: 
		</div>
		<div id="currentReportDetails">
			<c:url var="runReportUrl" value="/module/reporting/run/runReport.form?reportId=${__openmrs_report_data.definition.uuid}"/>
			<ul>
				<c:forEach var="parameterMapEntry" items="${__openmrs_report_data.context.parameterValues}">
					<li>${parameterMapEntry.key} = ${parameterMapEntry.value}</li> 
				</c:forEach>
			</ul>		
		</div>
		<div id="currentReportFooter" style="text-align: right;">
			<strong>${__openmrs_report_data.definition.name}</strong>
			<a id="currentReportDetailsLink" href="#">Details</a> | <a href="${runReportUrl}">Re-Run</a> | <a href="${runReportUrl}">View</a>
		</div>
	</c:if>
</div>
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
