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
	$('#cancel-indicator-button').click(function(event){ closeReportingDialog(false); });
	$('#preview-indicator-button').click(function(event){ $('#preview-indicator-form').submit(); });

});

</script>


<style type="text/css">
	* { margin: 0; }
	#container { height: 99%; border: 1px }
	#wrapper { min-height: 99%; height: auto !important; height:99%; margin: 0 auto -4em; }
	.button { margin: 5px; width: 10%; } 
	.buttonBar { height: 4em; background-color: #eee; vertical-align: middle; text-align:center;}
	input, select, textarea, label, button, span { font-size: 2em; } 
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	fieldset { padding: 25px; margin:25px; }
	fieldset legend { font-weight: bold; background: #E2E4FF; padding: 6px; border: 1px solid black; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	.errors { 
		width: 50%;
		border:1px dashed darkred; 
		margin-left:4px; 
		margin-right:4px; 
		margin-top:20px; margin-bottom:20px;
		padding:1px 6px; 
		vertical-align:middle; 
		background-color: lightpink;
	}
	.radio { margin-right: 20px; } 	
	
	
</style>

<div id="page">
	<div id="container">
		<div id="wrapper">		

			<span><h2>${indicatorForm.cohortIndicator.name}</h2></span>

			<c:url var="postUrl" value='/module/reporting/indicators/previewPeriodIndicator.form'/>
			<springform:form id="preview-indicator-form" commandName="indicatorForm" action="${postUrl}" method="POST">
			
				<input type="hidden" name="uuid" value="${indicatorForm.cohortIndicator.uuid}"/>
			
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
				</ul>		
								
				
				<ul>				
				
					<c:forEach var="parameter" items="${indicatorForm.cohortIndicator.parameters}">				
						<li>
							<label class="desc" for="${parameter.name}">${parameter.label}</label>
							<div>						
								<wgt:widget id="${parameter.name}" name="parameterValues[${parameter.name}]" defaultValue="<%= new java.util.Date() %>" type="${parameter.type.name}"/>	
							</div>
						</li>						
					</c:forEach>
					<li>					
						<hr/>
					</li>				
					<li>
						<span># of Patients returned:</span>
						
						<span>
							<c:choose>
								<c:when test="${empty indicatorResult}">?</c:when>
								<c:otherwise><strong>${indicatorResult.value}</strong></c:otherwise>
							</c:choose>
						</span>
					</li>
				</ul>										
			</springform:form>

		</div>		
		<div class="buttonBar" align="center">						
			<input class="button" id="preview-indicator-button" type="button" value="Preview" />
			<input class="button" id="cancel-indicator-button" type="button" value="Cancel"/>								
		</div>					


	</div>	
</div>
	
