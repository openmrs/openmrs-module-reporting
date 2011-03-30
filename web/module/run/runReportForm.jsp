<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../run/localHeader.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/admin/reports/runReport.list" />

<div id="page">
	<div id="container">
<div style="width: 60%">
	<h1><b><big>${report.reportDefinition.name}</big></b></h1>
	<h2><i>${report.reportDefinition.description}</i></h2>
	
	<spring:hasBindErrors name="report">
		<br /><br/>		
		<spring:message code="fix.error"/>
		<div class="error">
			<c:forEach items="${errors.allErrors}" var="error">
				<spring:message code="${error.code}" text="${error.defaultMessage}"/><br/>
			</c:forEach>
		</div>
		<br />	
	</spring:hasBindErrors>
	<br/>	
	<form method="post">
		
		<spring:nestedPath path="report">
			<table>
				<c:forEach var="parameter" items="${report.reportDefinition.parameters}">
	                <tr>
	                    <spring:bind path="userEnteredParams[${parameter.name}]">
				            <td align="right">
					           <spring:message code="${parameter.label}"/>:
		                    </td>
		                    <td>
								<wgt:widget id="${status.expression}" name="${status.expression}" type="${parameter.type.name}" property="${status.expression}" defaultValue="${status.value}"/>
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
				<tr>				
					<td align="right"><spring:message code="reporting.Report.run.outputFormat"/>:</td>					
					<td>
						<spring:bind path="selectedRenderer">
				            <select name="${status.expression}">
				                <c:forEach var="renderingMode" items="${report.renderingModes}">
				                	<c:set var="thisVal" value="${renderingMode.descriptor}"/>
				                    <option
				                        <c:if test="${status.value == thisVal}"> selected</c:if>
				                        value="${thisVal}">${renderingMode.label}
				                    </option>
				                </c:forEach>
				            </select>
				        </spring:bind>
					</td>		
				</tr>
	            <tr valign="top">
	                <td align="right"><spring:message code="reporting.Report.run.optionalFilterCohort"/>:</td>
	                <td>
	                    <rptTag:mappedPropertyForObject id="baseCohort"
	                        formFieldName="baseCohort" object="${ report }"
	                        propertyName="baseCohort" label="Optional Filter Cohort"/>
	                </td>
	            </tr>
				<tr><td>&nbsp;</td></tr>			
				<tr>
					<td></td>
					<td>					
						<input type="submit" value="<spring:message code="reporting.Report.run.button"/>" />
	        		</td>
	        	</tr>
	        </table>
		</spring:nestedPath>
	</form>
</div>
</div>
</div>