<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<c:choose>
    <c:when test="${model.dialog != 'false'}">
	   <%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
	</c:when>
	<c:otherwise>
	   <%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>
	</c:otherwise>
</c:choose>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {

		$j('#cancelButton').click(function(event){
			<c:choose>
				<c:when test="${model.dialog != 'false'}">
					closeReportingDialog(false);
				</c:when>
				<c:otherwise>
					document.location.href = '${model.cancelUrl}';
				</c:otherwise>
			</c:choose>
		});

		$j('#submitButton').click(function(event){
			$j('#reportProcessorForm').submit();
		});
		
	});
</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
</style>

<c:set var="pc" value="${model.reportProcessorConfiguration}"/>

<form id="reportProcessorForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/saveReportProcessor.form">
	<input type="hidden" name="uuid" value="${pc.uuid}" />
	<input type="hidden" name="successUrl" value="${model.successUrl}"/>
	
	<table style="margin:0; padding:0; font-size:small;" padding="5">
		<tr>
			<td valign="top" align="left">
				<span class="metadataField"><spring:message code="reporting.name"/></span>
				<wgt:widget id="name" name="name" object="${pc}" property="name" attributes="size=50"/>
				<br/>
				<span class="metadataField"><spring:message code="reporting.description"/></span>			
				<wgt:widget id="description" name="description" object="${pc}" property="description" attributes="cols=38|rows=2"/>
				<br/>
				<span class="metadataField"><spring:message code="reporting.reportProcessor.type"/></span>
				<select id="processorType" name="processorType">
					<option value=""></option>
					<c:forEach items="${model.reportProcessorTypes}" var="processorType">
						<option value="${processorType.name}"<c:if test="${processorType.name == pc.processorType}"> selected</c:if>>${processorType.simpleName}</option>
					</c:forEach>
				</select>
				<br/>
				<span class="metadataField"><spring:message code="reporting.reportProcessor.runOnSuccess"/></span>			
				<wgt:widget id="runOnSuccess" name="runOnSuccess" object="${pc}" property="runOnSuccess"/>
				<br/>
				<span class="metadataField"><spring:message code="reporting.reportProcessor.runOnError"/></span>			
				<wgt:widget id="runOnError" name="runOnError" object="${pc}" property="runOnError"/>
				<br/>
				<span class="metadataField"><spring:message code="reporting.reportProcessor.processorMode"/></span>			
				<wgt:widget id="processorMode" name="processorMode" object="${pc}" property="processorMode"/>
				<br/>
				<span class="metadataField"><spring:message code="reporting.reportProcessor.reportDesign"/></span>
				<select id="reportDesign" name="reportDesignUuid">
					<option value=""></option>
					<c:forEach items="${model.reportDesigns}" var="reportDesign">
						<option value="${reportDesign.uuid}"<c:if test="${reportDesign == pc.reportDesign}"> selected</c:if>>${reportDesign.name}</option>
					</c:forEach>
				</select>
			</td>
			<td valign="top" align="left">
				<span class="metadataField"><spring:message code="reporting.reportProcessor.configuration"/></span>	
				<wgt:widget id="configuration" name="configuration" object="${pc}" property="configuration" attributes="rows=20|cols=50"/>
			</td>
		</tr>
	</table>
	<hr style="color:blue;"/>
	<div style="width:100%; text-align:left;">
		<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
		<input type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
	</div>
</form>

