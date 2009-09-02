<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/admin/reports/runReport.list" />


<div style="width: 60%">
	<h2>
		<spring:message code="reporting.Report.run.title"/>:
		${report.reportDefinition.name}
	</h2>
	<i>${report.reportDefinition.description}</i>
	
	<br/><br/>
	
	<spring:hasBindErrors name="reportFromXml">
		<spring:message code="fix.error"/>
		<div class="error">
			<c:forEach items="${errors.allErrors}" var="error">
				<spring:message code="${error.code}" text="${error.code}"/><br/>
			</c:forEach>
		</div>
		<br />
	</spring:hasBindErrors>
	
	<br/><br/>
	
	<form method="post">
		<b><spring:message code="reporting.Report.parameters"/></b>
		
		<spring:nestedPath path="report">
			<table>
				<c:forEach var="parameter" items="${report.reportDefinition.parameters}">
	                <tr>
	                    <spring:bind path="userEnteredParams[${parameter.name}]">
				            <td>
					           <spring:message code="${parameter.label}"/>:
		                    </td>
		                    <td>
		                    	<openmrs:fieldGen type="${parameter.type.name}" formFieldName="${status.expression}" val="${status.value}"/>
		                        <c:if test="${status.errorMessage != ''}">
		                            <span class="error">${status.errorMessage}</span>
		                        </c:if>
		                    </td>
			            </spring:bind>
	                </tr>
	            </c:forEach>
	            <spring:bind path="userEnteredParams">
			        <c:if test="${status.errorMessage != ''}">
			            <span class="error">${status.errorMessage}</span>
			        </c:if>
	            </spring:bind>
	        </table>
	        
	        <br/><br/>
			
			<b><spring:message code="reporting.Report.run.outputFormat"/></b>
			<spring:bind path="selectedRenderer">
	            <select name="${status.expression}">
	                <c:forEach var="renderingMode" items="${report.renderingModes}">
	                	<c:set var="thisVal" value="${renderingMode.renderer.class.name}!${renderingMode.argument}"/>
	                    <option
	                        <c:if test="${status.value == thisVal}"> selected</c:if>
	                        value="${thisVal}">${renderingMode.label}
	                    </option>
	                </c:forEach>
	            </select>
	        </spring:bind>
		</spring:nestedPath>
		
		<br/>
		<br/>
		<input type="submit" value="<spring:message code="reporting.Report.run.button"/>" style="margin-left: 9em"/>
	</form>
</div>
