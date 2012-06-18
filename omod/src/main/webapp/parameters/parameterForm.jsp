<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
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

	$('#cancel-parameter-button').click(function(event){
		closeReportingDialog(false);
	});

	$('#save-parameter-button').click(function(event){
		$('#saveParameterForm').submit();
		
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
		
		
			<form id="saveParameterForm" method="post" action="<c:url value='/module/reporting/parameters/parameter.form'/>">
					<ul>								
						<spring:hasBindErrors name="parameter">  
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
							<label class="desc" for="type">Type</label>			
							<div>
								<select name="collectionType">
									<option value=""></option>
									<c:forEach var="supportedType" items="${supportedCollectionTypes}">
										<option value="${supportedType.value}">${supportedType.labelText}</option>
									</c:forEach>
								</select>

								<select name="type">
									<c:forEach var="supportedType" items="${supportedTypes}">
										<option value="${supportedType.value}">${supportedType.labelText}</option>
									</c:forEach>
								</select>
							</div>
						</li>
						<li>
							<label class="desc" for="name">Name</label>			
							<div>
								<input type="input" name="name" value="${parameter.name}" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
						<li>
							<label class="desc" for="label">Label</label>			
							<div>
								<input type="input" name="label" value="${parameter.label}" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
		
						<li>					
							<div align="center">				
								
								<!--since we don't have IDs to identify/distinguish parameters, we
									need a way to remove a parameter whose name has changed -->
								<input type="hidden" name="originalName" value="${parameter.name}"/> 
								
								<!-- hidden form fields from URL--> 
								<input type="hidden" name="uuid" value="${param.uuid}"/> 
								<input type="hidden" name="type" value="${param.type}"/> 
								<input type="hidden" name="redirectUrl" value="${param.redirectUrl}"/> 

								<!-- and now for the buttons, if the page is opened in a dialog
										then there will likely be jquery buttons doing the work -->
								<%-- <c:if test="${empty param.dialog}"> --%>
									<input id="save-parameter-button" type="submit" name="action" value="Save" />
									<input id="cancel-parameter-button" type="button" name="action" value="Cancel"/>								
								<%-- </c:if> --%>
							</div>					
						</li>
					</ul>				
		
		
			</form>
		
		
<!-- 


			<c:url var="formAction" value='/module/reporting/parameters/parameter.form'/>
			<springform:form id="saveParameterForm" commandName="parameter" action="parameter.form" method="POST">
					<ul>								
						
						<spring:hasBindErrors name="parameter">  
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
							<label class="desc" for="type">Type</label>			
							<div>
								<springform:select path="collectionType">										
									<springform:option value="" label="None"/>
						            <springform:options items="${supportedCollectionTypes}" itemValue="value" itemLabel="labelText"/>
								</springform:select>

								<springform:select path="type"										
									itemLabel="labelText"
									itemValue="value"
									items="${supportedTypes}"/>	
							</div>
						</li>
						<li>
							<label class="desc" for="name">Name</label>			
							<div>
								<springform:input path="name" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
						<li>
							<label class="desc" for="label">Label</label>			
							<div>
								<springform:input path="label" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
						<li>
							<label class="desc" for="defaultValue">Default Value</label>			
							<div>
								<springform:input path="defaultValue" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
		
		
						<li>					
							<div align="center">				
								
								<%--since we don't have IDs to identify/distinguish parameters, we
									need a way to remove a parameter whose name has changed --%>
								<input type="hidden" name="originalName" value="${parameter.name}"/> 
								
								<%-- hidden form fields from URL--%> 
								<input type="hidden" name="uuid" value="${param.uuid}"/> 
								<input type="hidden" name="type" value="${param.type}"/> 
								<input type="hidden" name="redirectUrl" value="${param.redirectUrl}"/> 

								<%-- and now for the buttons, if the page is opened in a dialog
										then there will likely be jquery buttons doing the work --%>
								<%-- <c:if test="${empty param.dialog}"> --%>
									<input id="save-button" type="button" class="ui-button ui-state-default ui-corner-all" name="action" value="Save" />
									<input id="cancel-button" type="button" class="ui-button ui-state-default ui-corner-all" name="action" value="Cancel"/>								
								<%-- </c:if> --%>
							</div>					
						</li>
					</ul>				

			</springform:form>
-->


		</div>		
	</div>	
</div>
	
