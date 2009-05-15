<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<openmrs:require privilege="View Patient Cohorts" otherwise="/login.htm" redirect="/reportData.form" />

<%@ include file="localHeader.jsp" %>

<h2>${reportData.reportSchema.name} </h2>
<i>${reportData.reportSchema.description }</i>

<br/>
<br/>

<b><spring:message code="Report.parameters"/></b>
<small>
	<table>
	    <c:forEach var="parameter" items="${reportData.reportSchema.reportParameters}" varStatus="varStatus">
	    <tr>
	        <td>
	            ${parameter.label}
	        </td>
	        <td>
	            <c:forEach var="ecParam" items="${reportData.evaluationContext.parameterValues}">
	                <c:forEach var="v" items="${ecParam.value}" varStatus="status">
	                    <c:if test="${v.key.name == parameter.name }">
	                        ${v.value}
	                    </c:if>
	                </c:forEach>
	            </c:forEach>
	        </td>
	    </tr>
	    </c:forEach>
	</table>
</small>

<c:if test="${fn:length(otherRenderingModes) > 0}">
	<br/>
	<form method="post">
		<input type="hidden" name="action" value="rerender"/>
		<spring:message code="Report.run.renderAgain"/>
		<select name="renderingMode">
			<c:forEach var="r" items="${otherRenderingModes}">
				<option value="${r.renderer.class.name}!${r.argument}">${r.label}</option>
			</c:forEach>
		</select>
		<input type="submit" value="<spring:message code="Report.renderAgain"/>"/>
	</form>
</c:if>

<br/>
<c:forEach var="dataSet" items="${reportData.dataSets}">
	<b class="boxHeader">${dataSet.key}</b>
	<div class="box">
	    <table>
	        <tr>
	            <th>
	                <spring:message code="Report.cohortReport.indicatorName"/>
	            </th>
				<th name="optional">&nbsp;</th>
	            <th name="optional">
	                <spring:message code="Report.cohortReport.indicatorDescription"/>
	            </th>
				<th name="optional">&nbsp;</th>
	            <th>
	                <spring:message code="Report.cohortReport.indicatorValue"/>
	            </th>
	        </tr>
            <c:forEach var="column" items="${dataSet.value.data}" varStatus="varStatus">
                <tr>
                    <th>${column.key.key}</th>
					<td>&nbsp;</td>
					<td>${column.key.key==column.key.description?"":column.key.description}</td>
					<td>&nbsp;</td>
                    <td>
                        <a href="reportData.list?dataSet=${dataSet.key}&indicator=${column.key.key}">
                        	${column.value.size}
						</a>
                    </td>
                </tr>
            </c:forEach>
		</table>
	</div>
</c:forEach>

<%@ include file="/WEB-INF/template/footer.jsp" %>