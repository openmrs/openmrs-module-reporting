<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>
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
.errors { margin-left:200px; margin-top:20px; margin-bottom:20px;	font-family:Verdana,Arial,sans-serif; font-size:12px; }

* { margin: 0; }
#container { height: 100%; border: 1px }
#wrapper { min-height: 100%; height: auto !important; height:100%; margin: 0 auto -6em; }
#buttonBar { height: 5em; background-color: #eee; padding: 2px; text-align:center;}
input, select, textarea, label, button { font-size: 2em; } 
</style>


<div id="page">
	<div id="container">
		<div id="wrapper">	
			<c:url var="postUrl" value='/module/reporting/indicators/managePeriodIndicator.form'/>
			<springform:form id="saveIndicatorForm" commandName="indicatorForm" action="${postUrl}" method="POST">
				<!-- hidden form fields from URL--> 

				<input type="hidden" name="reportUuid" value="${param.reportUuid}"/> 
				<input type="hidden" name="action" value="add"/> 

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
						<label class="desc" for="indicator">Indicator</label>
						<div>
							<springform:select path="uuid">										
								<springform:option value="" label="Select an indicator"/>
					            <springform:options items="${indicators}" itemValue="uuid" itemLabel="name"/>
							</springform:select>
						</div>					
					</li>							
					<li>
						<label class="desc" for="name">Indicator Key</label>
						<div>
							<springform:input path="columnKey" tabindex="1" cssClass="field text small" size="10"/>														
						</div>
						<span class="small"><em>(ex. "1.a")</em></span>
					</li>		
					<li>		
						<label class="desc" for="displayName">Display Name</label>						
						<div>
							<springform:input path="displayName" tabindex="1" cssClass="field text medium" size="30"/>														
						</div>					
						<span class="small"><em>(ex. "Enrolled in HIV Program between dates")</em></span>
					</li>							
				</ul>						
			</springform:form>
		</div>
			
		<div id="buttonbar">
			<input id="save-indicator-button" type="button" name="action" value="Save" />
			<input id="cancel-indicator-button" type="button" name="action" value="Cancel"/>								
		</div>	
			
	</div>	
</div>
	
