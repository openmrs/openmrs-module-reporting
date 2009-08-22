<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ include file="../localHeaderMinimal.jsp"%>
<%@ include file="../dialogSupport.jsp"%>

<!-- Form 
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
-->
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	$('#cancel-indicator-button').click(function(event){
		closeReportingDialog(false);
	});

	$('#save-indicator-button').click(function(event){
		$('#saveIndicatorForm').submit();
		
	});
});

</script>

<style type="text/css">

form ul { margin:0; padding:0; list-style-type:none; width:100%; }
form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }

.errors { 
	margin-left:200px; 
	margin-top:20px; 
	margin-bottom:20px;	
	font-family:Verdana,Arial,sans-serif; 
	font-size:12px;
}
</style>


<div id="page">
	<div id="container">
		<div>		
			<c:url var="postUrl" value='/module/reporting/indicators/periodIndicator.form'/>
			<form:form id="saveForm" commandName="indicatorForm" action="${postUrl}" method="POST">
				<ul>								
					<spring:hasBindErrors name="indicatorForm">  
						<li>
							<div class="errors"> 
								<font color="red"> 
									<h3><u>Please correct the following errors</u></h3>   
									<ul class="none">
										<c:forEach items="${errors.allErrors}" var="error">
											<li><spring:message code="${error.code}" text="${error.defaultMessage}"/></li>
										</c:forEach> 
									</ul> 
								</font>  
							</div>
						</li>
					</spring:hasBindErrors>
					<li>
						<label class="desc" for="name">Name</label>
						<div>
							<form:input path="name" tabindex="1" cssClass="field text small"/>														
						</div>
					</li>		
					<li>		
						<label class="desc" for="description">Description</label>
						<div>
							<form:input path="displayName" tabindex="1" cssClass="field text medium"/>														
						</div>					
					</li>		
					<li>
						<label class="desc" for="cohortDefinition">Which patients?</label>
						<div>
							<form:select path="cohortDefinitionUuid">										
								<form:option value="" label="Select a cohort definition"/>
					            <form:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
							</form:select>					
						</div>					
					
					</li>
										
					<li>		

					</li>
					<li>					
						<fieldset>
							<legend>Parameter Mapping</legend>


<%-- 
							<form:select path="indicator.">										
								<form:option value="" label="Select an indicator"/>
					            <form:options items="${indicator.}" itemValue="uuid" itemLabel="name"/>
							</form:select>					
--%>							
							
							
						</fieldset>					
					<li>
						<div align="center">				
							
							<!-- hidden form fields from URL--> 
							<input type="hidden" name="reportUuid" value="${param.reportUuid}"/> 

							<!-- buttons -->
							<input id="save-indicator-button" type="submit" name="action" value="Save" />
							<input id="cancel-indicator-button" type="button" name="action" value="Cancel"/>								
						</div>					
					</li>
				</ul>				
		
			</form:form>

		</div>		
	</div>	
</div>
	
