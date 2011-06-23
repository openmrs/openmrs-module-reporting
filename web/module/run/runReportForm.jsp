<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../run/localHeader.jsp"%>

<openmrs:require privilege="Run Reports" otherwise="/login.htm" redirect="/admin/reports/runReport.list" />

<div id="page">
	<div id="container">
<div style="width: 60%">
	<h1><b><big>${report.reportDefinition.name}</big></b></h1>
	<h2><i>${report.reportDefinition.description}</i></h2>
	
	<spring:hasBindErrors name="report">
		<spring:message code="fix.error"/>
		<c:if test="${not empty errors.globalErrors}">
			<div class="error">
				<c:forEach items="${errors.globalErrors}" var="error">
					<spring:message code="${error.code}" text="${error.defaultMessage}"/><br/>
				</c:forEach>
			</div>
		</c:if>
	</spring:hasBindErrors>
		
	<spring:nestedPath path="report">
		<spring:bind path="reportDefinition">
			<c:if test="${not empty status.errorMessage}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
		</spring:bind>

		<form method="post">
			<table>
				<c:forEach var="parameter" items="${report.reportDefinition.parameters}">
	                <tr>
	                    <spring:bind path="userEnteredParams[${parameter.name}]">
				            <td align="right">
					           <spring:message code="${parameter.label}"/>:
		                    </td>
		                    <td>
								<wgt:widget id="${status.expression}" name="${status.expression}" type="${parameter.type.name}" property="${status.expression}" defaultValue="${status.value}" attributes="${parameter.widgetConfigurationAsString}"/>
		                        <c:if test="${not empty status.errorMessage}">
		                            <span class="error">${status.errorMessage}</span>
		                        </c:if>
		                    </td>
			            </spring:bind>
	                </tr>
	            </c:forEach>
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
							<c:if test="${not empty status.errorMessage}">
								<span class="error">${status.errorMessage}</span>
							</c:if>
						</spring:bind>
					</td>		
				</tr>
				<openmrs:globalProperty var="mode" key="reporting.runReportCohortFilterMode" defaultValue="showIfNull"/>
				<c:set var="showCohortFilter" value="${mode == 'hide' ? false : (mode == 'show' ? true : report.reportDefinition.baseCohortDefinition == null)}"/>
				<c:if test="${showCohortFilter}">
		            <tr valign="top">
		                <td align="right"><spring:message code="reporting.Report.run.optionalFilterCohort"/>:</td>
		                <td>
		                    <rptTag:mappedPropertyForObject id="baseCohort"
		                        formFieldName="baseCohort" object="${ report }"
		                        propertyName="baseCohort" label="Optional Filter Cohort"/>
		                </td>
		            </tr>
	            </c:if>
				<tr><td>&nbsp;</td></tr>			
				<tr>
					<td></td>
					<td>					
						<input type="submit" value="<spring:message code="reporting.Report.run.button"/>" />
	        		</td>
	        	</tr>
	        </table>
		</form>
	</spring:nestedPath>
		
</div>
</div>
</div>