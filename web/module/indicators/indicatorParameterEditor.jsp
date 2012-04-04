<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>
<%@ include file="../localHeader.jsp"%>
<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>

<div id="page">
	<div id="container">
		
		<h1>Indicator Parameter Editor</h1>

		<springform:errors path="parameter.type"></springform:errors>

		<div class="errors"> 
			<spring:hasBindErrors name="indicatorParameter">  
				<font color="red"> 
					<h3><u>Please correct the following errors</u></h3>   
					<ul class="none">
						<c:forEach items="${errors.allErrors}" var="error">
							<li><spring:message code="${error.code}" text="${error.defaultMessage}"/></li>
						</c:forEach> 
					</ul> 
				</font>  
			</spring:hasBindErrors>
		</div>
	
			<springform:form id="saveForm" commandName="indicatorParameter" method="POST">				
			
				<ul>				
					<li>
						<label class="desc" for="type">Indicator</label>			
						<div>
							
							<strong>${indicatorParameter.cohortIndicator.name}</strong> <br/>
							<em>(uuid${indicatorParameter.cohortIndicator.uuid}, type=${indicatorParameter.cohortIndicator['class'].simpleName})</em>
							<springform:hidden path="cohortIndicator.uuid" />								
							<springform:hidden path="cohortIndicator['class'].name" />														
						</div>
					</li>
					<li>			
						<div>
						
							<table id="indicator-parameter-mapping-table" >
								<thead>
									<tr>
										<th>parameters to map</th>
										<th>inherit from cohort definition</th>
										<th>choose from indicator</th>
									</tr>
								</thead>	
								<tbody>										
									<c:forEach var="parameter" items="${indicator.cohortDefinition.parameterizable.parameters}" varStatus="varstatus">
										<tr>
											<td>
												<strong>${parameter.name}</strong>
											</td>
											<td>							
												<input type="radio" name="source_${parameter_name}" "value="inherit"/>
												${parameter.name}
											</td>
											<td>
												<input type="radio" name="source_${parameter_name}" value="choose"/>
												<select>
													<option></option>
													<c:if test="${empty indicator.parameters}">
														<option>(add more parameters)</option>
													</c:if>
													<c:forEach var="mappedParameter" items="${indicator.parameters}">										
														<option>${mappedParameter.name}</option>
													</c:forEach>
												</select>							
												<a href="<c:url value='/module/reporting/indicators/indicatorParameter.form?uuid=${indicator.uuid}'/>">
													<img src="<c:url value='/images/add.gif'/>" border='0'/>
												</a>
											</td>									
			
										</tr>
									</c:forEach>
								</tbody>
								<tfoot>			
								</tfoot>
							</table>							
						
					
						<label class="desc" for="name">Parameter Name</label>
						<div>
							<springform:input path="parameter.name" cssClass="field text short" />		
						</div>
					</li>
					<li>	
						<label class="desc" for="type">Type</label>
						<div>
							<springform:input path="parameter.type" cssClass="field text short" />		
							<springform:errors path="parameter.type"></springform:errors>
						</div>
					</li>
					<li>
						<label class="desc" for="defaultValue">Default Value</label>
						<div>
							<springform:input path="parameter.defaultValue" cssClass="field text short" />
							<springform:errors path="parameter.defaultValue"></springform:errors>		
						</div>
					</li>
					<li>	
						<label class="desc" for="prompt">Prompt</label>
						<div>
						</div>
					</li>
					<li>						
						<label class="desc" for="required">Required</label>
						<div>
							<springform:checkbox path="parameter.required"  />		
						</div>
					</li>
					<li>	
						<div class="buttons">
							<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<button id="cancel-button" name="cancel">Cancel</button>
						</div>
					</li>
				</ul>
		</springform:form>		
	</div><!--#container-->
</div><!-- #page -->


<%@ include file="/WEB-INF/template/footer.jsp"%>