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
	$('#cancel-indicator-button').click(function(event){ closeReportingDialog(false); });
	$('#save-indicator-button').click(function(event){ $('#indicator-form').submit(); });
	$('#select-cohort-definition').change(function(event){ $('#indicator-form').submit(); });

	$('#show-advanced-logic').click(function(event){ 
		var showLogic = $('show-advanced-logic').val();
		alert('test: ' + showLogic);
		$('#advanced-logic').show(); 
	});
});

</script>


<style type="text/css">
	* { margin: 0; }
	#container { height: 99%; border: 1px }
	#wrapper { min-height: 100%; height: auto !important; height:100%; margin: 0 auto -4em; }
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
			<c:url var="postUrl" value='/module/reporting/indicators/periodIndicator.form'/>
			<form:form id="indicator-form" commandName="indicatorForm" action="${postUrl}" method="POST">
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
					<c:if test="${indicatorForm.cohortIndicator.uuid != null}">
						<li>
							<label class="desc" for="uuid">ID</label>
							<div>
								${indicatorForm.cohortIndicator.uuid}
							</div>
						</li>		
					</c:if>
					<li>
						<label class="desc" for="name">Name</label>
						<div>
							<form:input path="cohortIndicator.name" cssClass="field text small" size="30"/>														
						</div>
					</li>		
					<li>		
						<label class="desc" for="description">Description</label>
						<div>
							<form:textarea path="cohortIndicator.description" cssClass="field text medium" cols="29"/>
						</div>					
					</li>
					<li>		
						<label class="desc" for="indicatorType">Type of calculation</label>
						<div>							
							<span id="indicator-type">COUNT [count the number of patients]</span>
						</div>						
					</li>
					<li>
						<label class="desc" for="cohortDefinition">Which patients?</label>
						<div>
							<form:select id="select-cohort-definition" path="cohortDefinition">										
								<form:option value="" label="Select a cohort definition"/>
					            <form:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
							</form:select>					
						</div>															
					</li>								
				</ul>		
										
				<ul>
					<c:if test="${indicatorForm.cohortDefinition!=null}">
						<input type="hidden" name="action" value="save"/>
						<li>
							<label class="desc" for="parameterMapping">Map parameters</label>
							<div>					
								<c:forEach var="parameter" items="${indicatorForm.cohortDefinition.parameters}">
									<label class="inline">${parameter.name} -> </label> 
									<form:select path="parameterMapping[${parameter.name}]" multiple="false">										
										<form:option value="" label="Select a parameter"/>
							            <form:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="name" itemLabel="name"/>
									</form:select>					
								</c:forEach>
							</div>
						</li>
					</c:if>								
				</ul>				
			</form:form>

		</div>		
		<div class="buttonBar" align="center">						
			<input class="button" id="save-indicator-button" type="button" value="Save" />
			<input class="button" id="cancel-indicator-button" type="button" value="Cancel"/>								
		</div>					


	</div>	
</div>
	
