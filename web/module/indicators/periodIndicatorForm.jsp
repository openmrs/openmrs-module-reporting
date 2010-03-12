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
	$('#select-location-filter').change(function(event){ $('#indicator-form').submit(); });

	$('#show-advanced-logic').click(function(event){ 
		var showLogic = $('show-advanced-logic').val();
		$('#advanced-logic').show(); 
	});

	/*
	$('#parameter-mapping-prompt').click(function(){
		$('#parameter-mapping-table').toggle();
    });

	$('#custom-indicator-type-radio').click(function(){
		$('#custom-indicator-type-form').show();
    });

	$('#fraction-indicator-type-radio').click(function(){
		$('#fraction-indicator-type-form').show();
    });

	$('#count-indicator-type-radio').click(function(){
		$('#count-indicator-type-form').show();
    });
    */
	
	
});
</script>


<style type="text/css">
	* { margin: 0; }
	#container { height: 99%; border: 1px }
	#wrapper { min-height: 100%; height: auto !important; height:100%; margin: 0 auto -4em; }
	.button { margin: 5px; width: 10%; } 
	.buttonBar { height: 4em; background-color: #eee; vertical-align: middle; text-align:center;}
	input, select, textarea, label, button, span { font-size: 100%; } 
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
								<form:hidden path="cohortIndicator.id"/>
								<form:hidden path="cohortIndicator.uuid"/>
							</div>
						</li>		
					</c:if>
					<li>
						<label class="desc" for="name">Indicator name:</label>
						<div>
							<form:input path="cohortIndicator.name" cssClass="field text large" size="40"/>														
						</div>
					</li>		
					<li>		
						<label class="desc" for="description">Describe what is being measured:</label>
						<div>
							<form:textarea path="cohortIndicator.description" cssClass="field text large" cols="40"/>
						</div>					
					</li>
				</ul>
				<ul>
					<li>					
						<label class="desc" for="parameters">Parameters:</label>
						<div style="width: 40%">
							<table width="100%">
								<tr>
									<th></th>
									<th style="border-bottom: 1px solid black; text-align: left;">name</th>
									<th style="border-bottom: 1px solid black; text-align: left;">label</th>
									<th style="border-bottom: 1px solid black; text-align: left;">type</th>
									<th style="border-bottom: 1px solid black; text-align: left;">default value</th>
									<th></th>
								</tr>
								<c:forEach var="parameter" items="${indicatorForm.cohortIndicator.parameters}">
									<tr>
										<td align="center"><img src="${pageContext.request.contextPath}/images/lock.gif" title="<spring:message code="General.locked.help"/>" alt="<spring:message code="General.locked"/>" /></td>
										<td align="center">${parameter.label}</td>
										<td align="center">${parameter.name}</td>
										<td align="center">${parameter.type.simpleName}</td>
										<td align="center">${parameter.defaultValue}</td>
										<td><img src="${pageContext.request.contextPath}/images/trash.gif" title="<spring:message code="General.remove.help"/>" alt="<spring:message code="General.remove"/>" /></td>
									</tr>
							</c:forEach>
							</table>
						</div>															
					</li>							
				</ul>				
														
				<ul>
					<li>					
						<label class="desc" for="description">Filter on location:</label>
						<div style="width: 40%">
							<form:select id="select-location-filter" path="locationFilter" cssStyle="width: 100%">										
								<form:option value="" label="select a location filter"/>
					            <form:options items="${locationFilters}" itemValue="uuid" itemLabel="name"/>
							</form:select>		
						</div>															
					</li>							
					<c:if test="${indicatorForm.locationFilter!=null}">				
						<li>
							<div id="location-filter-parameter-mapping-table">			
								<table border="0" style="border: black 1px dashed;">		
									<c:forEach var="parameter" items="${indicatorForm.locationFilter.parameters}">
										<tr>
											<td><label class="inline">${parameter.label}</label></td>
											<td>=</td>
											<td>				
												<form:select path="locationFilterParameterMapping[${parameter.name}]" multiple="false">										
													<form:option value="" label="Do not filter on location (default)"/>
										            <form:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="expression" itemLabel="name"/>
												</form:select>					
											</td>
										</tr>
									</c:forEach>
								</table>
							</div>
						</li>
					</c:if>								
				</ul>				

				<ul>
					<li>		
					
						<label class="desc" for="description">Indicator calculation:</label>			
						
						<div id="count-indicator-type-form" style="border: 1px #ccc dashed; width: 40%;">	
							<div style="background-color: #70A8D2; padding: 2px; padding-top-5px; vertical-align: middle;">
								<c:url var="countImage" value="/moduleResources/reporting/images/indicator-type-simple.png"/>
								<label class="desc" for="cohortDefinition">
									<input type="radio" name="indicatorType" value="count" id="count-indicator-type-radio" checked="checked"/>
									<img src="${countImage}" width="24" height="24" border="0" alt="period indicator" 
										style="vertical-align:middle"/>
									<span style="color: white;">Simple count of patients</span>
								</label>	
							</div>						
												
							<div style="padding:10px">					
								<i>Count # of patients that are included in ...</i><br/>
								<form:select id="select-cohort-definition" path="cohortDefinition" cssStyle="width: 100%">										
									<form:option value="" label="select a cohort definition ..."/>
						            <form:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
								</form:select>
							
								<c:if test="${indicatorForm.cohortDefinition!=null}">				
									<input type="hidden" name="action" value="save"/>
									<div id="parameter-mapping-table">			
										<!-- 
										<a href="javascript:void(0);" id="parameter-mapping-prompt">edit parameters</a>
										 -->
										<table border="0" width="100%">		
											<c:forEach var="parameter" items="${indicatorForm.cohortDefinition.parameters}">
												<tr>
													<td><label class="inline">${parameter.label}</label> </td>
													<td>=</td>
													<td>				
														<form:select path="parameterMapping[${parameter.name}]" multiple="false">										
															<form:option value="" label="Select a parameter"/>
												            <form:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="expression" itemLabel="name"/>
														</form:select>					
													</td>
												</tr>
											</c:forEach>
										</table>
									</div>
								</c:if>
							</div>	

							<!-- Indicator Type  Fraction -->
							<div style="background-color: #D0E5F5; padding: 2px; vertical-align: middle;">
								<c:url var="fractionImage" value="/moduleResources/reporting/images/indicator-type-fraction.png"/>
								<label class="desc" for="cohortDefinition">
									<input type="radio" name="indicatorType" value="fraction" id="-indicator-type-radio" disabled="disabled"/>
									<img src="${fractionImage}" width="24" height="24" border="0" alt="period indicator" 
										style="vertical-align:middle"/>
									<span style="color: white;">Fractional indicator</span>
								</label>	
							</div>																		
							<div style="padding:10px">
								<!-- needs to be implemented -->
								e.g. # of patients on 1st line regimen / # of total active patients
							</div>
							
							<!-- Indicator Type Custom  -->
							<div style="background-color: #D0E5F5; padding: 2px;  vertical-align: middle;">
								<c:url var="customImage" value="/moduleResources/reporting/images/indicator-type-custom.png"/>
								<label class="desc" for="cohortDefinition">
									<input type="radio" name="indicatorType" value="custom" id="-indicator-type-radio" disabled="disabled"/>
									<img src="${customImage}" width="24" height="24" border="0" alt="period indicator" 
										style="vertical-align:middle"/>
									<span style="color: white;">Advanced logic</span>
								</label>	
							</div>																		
							<div style="padding:10px">
								<!-- needs to be implemented -->
								e.g. Average CD4 Count of patients with visit in last month
							</div>
						</div>	
						

						
																				
					</li>							
				</ul>		
				
				
<%-- 						
				<ul>
					<li>					
						<c:url var="fractionImage" value="/moduleResources/reporting/images/fraction.png"/>
						<label class="desc" for="cohortDefinition">
							<input type="radio" name="indicatorType" value="fraction" id="fraction-indicator-type-radio" disabled="disabled"/>
							<img src="${fractionImage}" width="24" height="21" border="0" alt="period indicator" style="vertical-align:middle"/>
							(fraction)
						</label>
						<div id="fraction-indicator-type-form" style="padding:20px; border: 1px black dashed; color: #ccc;">
							<table>
								<tr>
									<td>
										<b>Numerator</b> # of patients in<br/>
										<form:select id="select-cohort-definition" path="cohortDefinition">										
											<form:option value="" label="Select a cohort definition"/>
								            <form:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
										</form:select>		
										<c:if test="${indicatorForm.cohortDefinition!=null}">
											<a href="javascript:void(0);" id="parameter-mapping-prompt">specify parameter mapping</a>
										</c:if>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<hr/>
									</td>
								</tr>
								<tr>
									<td>
										<b>Denominator</b> # of patients in <br/>
										<form:select id="select-cohort-definition" path="cohortDefinition">										
											<form:option value="" label="Select a cohort definition"/>
								            <form:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
										</form:select>		
										<c:if test="${indicatorForm.cohortDefinition!=null}">
											<a href="javascript:void(0);" id="parameter-mapping-prompt">specify parameter mapping</a>
										</c:if>
									</td>
								</tr>
							</table>							
							
						</div>															
					</li>							
				</ul>				


				<ul>
					<li>							
						<c:url var="customLogicImage" value="/moduleResources/reporting/images/custom-logic.png"/>
						<label class="desc" for="cohortDefinition">
							<input type="radio" name="indicatorType" value="custom" id="custom-indicator-type-radio" disabled="disabled"/>
							<img src="${customLogicImage}" width="24" height="24" border="0" alt="period indicator" 
								style="vertical-align:middle"/>
							(custom)
						</label>
						<div id="custom-indicator-type-form" style="padding:20px; border: 1px black dashed;">						
							(not implemented yet)
						</div>															
					</li>							
				</ul>				
				
--%>			
				
			</form:form>

		</div>		
		<div class="buttonBar" style="align: center; vertical-algin: middle">						
			<input class="button" id="save-indicator-button" type="button" value="Save" />
			<input class="button" id="cancel-indicator-button" type="button" value="Cancel"/>								
		</div>					


	</div>	
</div>
	
