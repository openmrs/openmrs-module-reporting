<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/dialogSupport.jsp" %>

<script type="text/javascript">
	jqUiDecoration();
	$(document).ready(function() {
		//uiDecoration();
		
		<c:if test="${__openmrs_report_data!=null}">
			$("#currentReportDetails").hide();
			$("#currentReportHelper").show();
			$(".toggleReportDetails").click(function(event){
				var details = $("#currentReportDetails");
				var visible = details.is(':visible');
				if (visible) details.hide();
				else details.show();
	
				var helper = $("#currentReportHelper");
				var visible = helper.is(':visible'); 
				if (visible) helper.hide();
				else helper.show();					
			});
		</c:if>
	});
</script>

<c:if test="${__openmrs_report_data!=null}">
	<div style="float: right; border: 1px solid #8FABC7; background-color: white; padding: 10px; position: absolute; right: 0px; top: 50px; z-index: 1">
		<div id="currentReportHelper">
			<div align="center">
				<a class="toggleReportDetails" href="#">
					<img src="<c:url value='/moduleResources/reporting/images/report_icon.gif'/>" width=45 height=45 border="0"/>
					<br/><span class="small">back to report</span>
				</a>						
			</div>
		</div>
			
		<div id="currentReportDetails">
		
			<c:url var="archiveReportUrl" value="/module/reporting/run/archiveReport.form?reportId=${__openmrs_report_data.definition.uuid}"/>
			<c:url var="renderReportUrl" value="/module/reporting/run/renderReport.form?reportId=${__openmrs_report_data.definition.uuid}"/>
			<c:url var="runReportUrl" value="/module/reporting/run/runReport.form?reportId=${__openmrs_report_data.definition.uuid}"/>
			<c:url var="removeReportUrl" value="/module/reporting/run/removeReport.form?reportId=${__openmrs_report_data.definition.uuid}"/>
			
			<div style="border-bottom: 1px solid black; background-color: #8FABC7;">
				<table width="100%">
					<tr>
						<td align="left">
							<strong>${__openmrs_report_data.definition.name}</strong>
						</td>
						<td align="right">
							<a class="toggleReportDetails" href="#">close</a>
						</td>
					</tr>
				</table>
			</div>						
			<table width="100%" cellpadding="2" border="0">
			 
				<tr>	
					<td valign="top" colspan="3">
						<table width="100%">
							<tr>
								<td><em>Last ran:</em></td>
								<td align="right"><strong>today</strong></td>
							</tr>
							<tr>
								<td><em>Report context:</em></td>
							</tr>
							<c:forEach var="parameterMapEntry" items="${__openmrs_report_data.context.parameterValues}">
								<tr>
									<td align="left"><span style="font-size:.7em;margin-left: 10px;" >${parameterMapEntry.key}</span></td>
									<td align="right"> 
									<strong>
										<c:choose>
											<c:when test="${parameterMapEntry.value.class.simpleName=='Date'}">												
												<fmt:formatDate pattern="MMM dd yyyy" value="${parameterMapEntry.value}" />
											</c:when>
											<c:otherwise>
												${parameterMapEntry.value}
											</c:otherwise>
										</c:choose>
									</strong>
									</td>
								</tr>
							</c:forEach>
							<tr>
								<td align="left"><em>Actions:</em></td>
								<td align="right">
									<a href="${archiveReportUrl}"><img src="<c:url value='/images/save.gif'/>" border="0" valign="absmiddle"/></a>
									<a href="${renderReportUrl}"><img src="<c:url value='/images/note.gif'/>" border="0" valign="absmiddle"/></a>
									<a href="${runReportUrl}"><img src="<c:url value='/images/play.gif'/>" border="0" valign="absmiddle"/></a>
									<a href="${removeReportUrl}"><img src="<c:url value='/images/trash.gif'/>" border="0" valign="absmiddle"/></a>					
								</td>
							</tr>
						</table>													
					</td>
				</tr>
			</table>
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
