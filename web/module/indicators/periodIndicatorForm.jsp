<%@ include file="../include.jsp"%> 
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/index.htm" />


<%@ taglib prefix="springform" uri="/WEB-INF/view/module/reporting/resources/spring-form.tld" %>
<%@ include file="../manage/localHeader.jsp"%>

<!-- Form 
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
-->
<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	$('#cancel-button').click(function(event){ 
		alert('canceled has been disabled');
	});
	$('#save-button').click(function(event){ $('#indicator-form').submit(); });

	// selecting a cohort definition
	$('#select-cohort-definition').change(function(event){ $('#indicator-form').submit(); });
	$('#select-numerator').change(function(event){ $('#indicator-form').submit(); });
	$('#select-denominator').change(function(event){ $('#indicator-form').submit(); });
	$('#select-location-filter').change(function(event){ $('#indicator-form').submit(); });

	// logic query
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

	.button { margin: 5px; width:150px; } 
	.buttonBar { height: 4em; height: 100%; vertical-align: middle; text-align:center; padding-top:25px;}
	input, select, textarea, label, button, span { font-size: 100%; } 
	#indicator-form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	#indicator-form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	fieldset { padding: 25px; margin:25px; }
	fieldset legend { font-weight: bold; background: #E2E4FF; padding: 6px; border: 1px solid black; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	label.inline { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:inline; font-weight:bold; }
	.errors { 
		width: 100%;
		border:1px dashed darkred; 
		margin-left:4px; 
		margin-right:4px; 
		margin-top:20px; margin-bottom:20px;
		padding: 6px; 
		vertical-align:middle; 
		background-color: lightpink;
	}
	.radio { margin-right: 20px; } 		
</style>


<div id="page">

	<div id="container">
		
		<h1>Period Indicator Editor</h1>

		<div id="navigation">
		
			<!--  no menu actions -->
			
		</div>
		
		
		<div id="main">		
			<c:url var="postUrl" value='/module/reporting/indicators/periodIndicator.form'/>
			<springform:form id="indicator-form" commandName="indicatorForm" action="${postUrl}" method="POST">
			
				<input type="hidden" name="action" value="save"/>

				<spring:hasBindErrors name="indicatorForm">  
					<div class="errors"> 									 
						<h3 style="color: darkred;">Please correct the following errors ...</h3>
						<c:forEach items="${errors.allErrors}" var="error">
							<img src="${pageContext.request.contextPath}/images/error.gif" style="vertical-align:middle"/>
							<spring:message code="${error.code}" text="${error.defaultMessage}"/><br/>
						</c:forEach> 
					</div>
				</spring:hasBindErrors>

			
	<table style="border: 1px #ccc solid; padding: 10px">
		<tr>

<!--  Indicator calculation -->			
			
			<td width="50%" valign="top" height="100%">
			
				<h5>Step 1. Build your indicator ...</h5>
			
				<ul>
					<li>		
												
						<div id="count-indicator-type-form" style="border: 1px #ccc dashed; width:90%; padding: 5px;">	
							<div style="background-color: #70A8D2; padding: 2px; padding-top-5px; vertical-align: middle;">
								<c:url var="countImage" value="/moduleResources/reporting/images/indicator-type-simple.png"/>
								<label class="desc" for="cohortDefinition">
									
									<springform:radiobutton path="indicatorType" value="COUNT" id="count-indicator-type-radio" />
									
									<img src="${countImage}" width="24" height="24" border="0" alt="period indicator" 
										style="vertical-align:middle"/>
									<span style="color: white;">COUNT</span>
								</label>	
							</div>			
							
<!-- Indicator Type  Simple -->
												
							<div style="padding:50px" align="center">					
								<table border="0" cellpadding="5" cellspacing="5">
									<tr>
										<td width="50" align="center" valign="middle">
											
										</td>
										<td width="10"></td>
										<td>
											<springform:select id="select-cohort-definition" path="cohortDefinition">										
												<springform:option value="" label="choose a cohort definition ..."/>
									            <springform:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
											</springform:select>
										</td>
									</tr>
									<tr>
										<td></td>
										<td></td>
										<td>																	
											<c:if test="${indicatorForm.cohortDefinition!=null}">				
												<div id="parameter-mapping-table">			
													<table border="0" width="100%">		
														<c:forEach var="parameter" items="${indicatorForm.cohortDefinition.parameters}">
															<tr>
																<td><label class="inline">${parameter.label}</label> </td>
																<td>
																	<c:url var="mappingImageUrl" value="/moduleResources/reporting/images/mapping.gif"/>
																	<img src="${mappingImageUrl}" width="24" height="24" border="0" alt="maps to" style="vertical-align:middle"/>																
																</td>
																<td>				
																	<springform:select path="parameterMapping[${parameter.name}]" multiple="false">										
																		<springform:option value="" label="choose a parameter ..."/>
															            <springform:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="expression" itemLabel="name"/>
																	</springform:select>					
																</td>
															</tr>
														</c:forEach>
													</table>
												</div>
											</c:if>
										</td>
									</tr>
								</table>
							</div>	

<!-- Indicator Type  Fraction -->


							<div style="background-color: #70A8D2; padding: 2px; vertical-align: middle;">
								<c:url var="fractionImage" value="/moduleResources/reporting/images/indicator-type-fraction.png"/>
								<label class="desc" for="numerator">
								
									<springform:radiobutton path="indicatorType" value="FRACTION" id="fraction-indicator-type-radio" />
																	
									<img src="${fractionImage}" width="24" height="24" border="0" alt="period indicator" 
										style="vertical-align:middle"/>
									<span style="color: white;">FRACTION</span>
								</label>	
							</div>																		
							<div style="padding:50px" align="center">
								<table border="0" cellpadding="1" cellspacing="1">
									<tr>
										<td align="center">
										
											<div style="width:100%; background-color: #D0E5F5; padding:5px;">
												<label class="desc">Numerator</label>
											</div>										
											<div style="border: 1px dashed #ccc; padding: 25px">
													
												<springform:select id="select-numerator" path="numerator">										
													<springform:option value="" label="choose a numerator ..."/>
										            <springform:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
												</springform:select>		
												<c:if test="${indicatorForm.numerator!=null}">				
													<div id="parameter-mapping-table">			
														<table border="0" width="100%">		
															<c:forEach var="parameter" items="${indicatorForm.numerator.parameters}">
																<tr>
																	<td>numerator.${parameter.name}</td>
																	<td>																
																		<c:url var="mappingImageUrl" value="/moduleResources/reporting/images/mapping.gif"/>
																		<img src="${mappingImageUrl}" width="24" height="24" border="0" alt="maps to" style="vertical-align:middle"/>																																
																	</td>
																	<td>				
																		<springform:select path="numeratorParameterMapping[${parameter.name}]" multiple="false">										
																			<springform:option value="" label="choose a parameter ..."/>
																            <springform:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="expression" itemLabel="name"/>
																		</springform:select>					
																	</td>
																</tr>
															</c:forEach>
														</table>
													</div>
												</c:if>
											</div>
											
										</td>
									</tr>
									<tr>
										<td>
											<hr/>
										</td>
									</tr>
									<tr>
										<td align="center">
											<div style="width:100%; background-color: #D0E5F5; padding:5px;">
												<label class="desc">Denominator</label>
											</div>
										
											<div style="border: 1px dashed #ccc; padding: 25px">
										
												<springform:select id="select-denominator" path="denominator">										
													<springform:option value="" label="choose the denominator ..."/>
										            <springform:options items="${cohortDefinitions}" itemValue="uuid" itemLabel="name"/>
												</springform:select>		
												<c:if test="${indicatorForm.denominator!=null}">				
													<div id="parameter-mapping-table">			
														<table border="0" width="100%">		
															<c:forEach var="parameter" items="${indicatorForm.denominator.parameters}">
																<tr>
																	<td>denominator.${parameter.name}</td>
																	<td>
																		<c:url var="mappingImageUrl" value="/moduleResources/reporting/images/mapping.gif"/>
																		<img src="${mappingImageUrl}" width="24" height="24" border="0" alt="maps to" style="vertical-align:middle"/>																
																	</td>
																	<td>				
																		<springform:select path="denominatorParameterMapping[${parameter.name}]" multiple="false">										
																			<springform:option value="" label="choose a parameter ..."/>
																            <springform:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="expression" itemLabel="name"/>
																		</springform:select>					
																	</td>
																</tr>
															</c:forEach>
														</table>
													</div>
												</c:if>
											</div>
										</td>
									</tr>
								</table>		
							</div>
							
<!-- Indicator Type Custom  -->

							<div style="background-color: #D0E5F5; padding: 2px;  vertical-align: middle;">
								<c:url var="customImage" value="/moduleResources/reporting/images/indicator-type-custom.png"/>
								<label class="desc" for="cohortDefinition">
								
									<springform:radiobutton path="indicatorType" value="LOGIC" id="logic-indicator-type-radio" />
								
									<img src="${customImage}" width="24" height="24" border="0" alt="period indicator" 
										style="vertical-align:middle"/>
									<span style="color: white;">LOGIC</span>
								</label>	
							</div>																		
							<div style="padding:50px; color: #ccc;">
								<!-- needs to be implemented -->
								e.g. Average CD4 Count of patients with visit in last month
							</div>
						</div>	
					</li>							
				</ul>		
					</td>
		
		
<!--  Indicator details -->
		
			<td width="20%" valign="top">
				<h5>Step 2. Save the indicator ...</h5>						
			
				<div style="padding:15px; background-color: #eee;">
					<ul>								
						<c:if test="${indicatorForm.cohortIndicator.uuid != null}">
							<li>
								<label class="desc" for="uuid">ID</label>
								<div>
									${indicatorForm.cohortIndicator.uuid}
									<springform:hidden path="cohortIndicator.id"/>
									<springform:hidden path="cohortIndicator.uuid"/>
								</div>
							</li>		
						</c:if>
						<li>
							<label class="desc" for="name">Indicator name</label>
							<div>
								<springform:input path="cohortIndicator.name" cssClass="field text large" size="40"/>														
							</div>
						</li>		
						<li>		
							<label class="desc" for="description">Describe what is being measured</label>
							<div>
								<springform:textarea path="cohortIndicator.description" cssClass="field text large" cols="40"/>
							</div>					
						</li>
						<li>					
							<c:url var="filterImageUrl" value="/moduleResources/reporting/images/filter.png"/>
							<img src="${filterImageUrl}" width="16" height="16" border="0" alt="period indicator" style="vertical-align:middle"/>										
							<label class="inline" for="description">Location Filter</label> <i>(optional)</i><br/>
							<div style="border: 1px #ccc dashed; padding: 15px;">
								<springform:select id="select-location-filter" path="locationFilter" cssStyle="width:80%;">
									<springform:option value="" label="none"/>
						            <springform:options items="${locationFilters}" itemValue="uuid" itemLabel="name"/>
								</springform:select>							
								<c:if test="${indicatorForm.locationFilter!=null}">											
									<div id="location-filter-parameter-mapping-table">			
										<table border="0" style="width:80%;">								
											<c:forEach var="parameter" items="${indicatorForm.locationFilter.parameters}">
												<tr>
													<td>${parameter.label} (${parameter.name})</td>
													<td>
														<c:url var="mappingImageUrl" value="/moduleResources/reporting/images/mapping.gif"/>
														<img src="${mappingImageUrl}" width="24" height="24" border="0" alt="maps to" style="vertical-align:middle"/>													
													</td>
													<td>				
														<springform:select path="locationFilterParameterMapping[${parameter.name}]" multiple="false">										
															<springform:option value="" label="choose a parameter ..."/>
												            <springform:options items="${indicatorForm.cohortIndicator.parameters}" itemValue="expression" itemLabel="name"/>
														</springform:select>					
													</td>
												</tr>
											</c:forEach>
										</table>
									</div>
								</c:if>		
							</div>							
						</li>
						<li>							
							<c:url var="parameterImageUrl" value="/moduleResources/reporting/images/parameter.gif"/>
							<img src="${parameterImageUrl}" border="0" alt="period indicator" style="vertical-align:middle"/>
							<label class="inline" for="parameters">Parameters</label>
							<div style="padding-left:15px; border: 1px #ccc dashed;">
								<ul>
									<c:forEach var="parameter" items="${indicatorForm.cohortIndicator.parameters}">
										<li>${parameter.label} (${parameter.name}) : <i>${parameter.type.simpleName}</i></li>
									</c:forEach>
								</ul>
							</div>															
						</li>							
						<li>
							<div class="buttonBar">						
								<input class="button" id="save-button" type="button" value="Save" />
								<a href="${pageContext.request.contextPath}/module/reporting/indicators/manageIndicators.form">Cancel</a>
								

							</div>										
						</li>												
					</ul>	
				</div>		
			</td>
			
			
				</tr>
			</table>
		</springform:form>
		</div><!-- main -->
	</div><!-- container -->
</div><!-- page -->
	
	
<%@ include file="/WEB-INF/template/footer.jsp"%>
