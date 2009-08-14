<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%--<%@ include file="../localHeader.jsp"%>--%>
<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>

<script type="text/javascript" charset="utf-8">
//var jQ = jQuery.noConflict();
/*
$(document).ready(function() {

	$('#cancel-button').click(function(event){
		// Redirect to the listing page
		event.preventDefault();
		window.location.href='<c:url value="/module/reporting/manageCohortDefinitions.list"/>';
	});
	
} );
*/
</script>

<style type="text/css">
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
			<c:url var="formAction" value='/module/reporting/parameter.form'/>
			<form:form id="saveParameterForm" commandName="parameter" action="${formAction}" method="POST">
				<fieldset>
					<legend>Parameter Editor</legend>
					<ul>								
						<li>
						
							<div class="errors"> 
								<spring:hasBindErrors name="parameter">  
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
						
						
						</li>
						<li>
							<label class="desc" for="type">Type</label>			
							<div>
								<form:select path="clazz"										
									itemLabel="labelText"
									itemValue="value"
									items="${supportedTypes}"/>	
							</div>
						</li>
						<li>
							<label class="desc" for="name">Name</label>			
							<div>
								<form:input path="name" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
						<li>
							<label class="desc" for="label">Label</label>			
							<div>
								<form:input path="label" tabindex="1" cssClass="field text medium"/>														
							</div>
						</li>
						<li>
							<label class="desc" for="collectionType">Collection Type</label>			
							<div>
								<form:select path="collectionType">										
									<form:option value="" label="None"/>
						            <form:options items="${supportedCollectionTypes}" itemValue="value" itemLabel="labelText"/>
								</form:select>
							</div>
						</li>
						<li>
							<label class="desc" for="defaultValue">Default Value</label>			
							<div>
								<form:input path="defaultValue" tabindex="1" cssClass="field text medium"/>														
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
								<c:if test="${empty param.dialog}">
									<input id="save-button" class="button" name="action" type="submit" value="Save" />
									<input id="delete-button" class="button" name="action" type="submit" value="Delete" />								
									<input id="cancel-button" class="button" name="cancel" type="submit" value="Cancel"/>
								</c:if>
							</div>					
						</li>
					</ul>				
				</fieldset>
			</form:form>
		</div>		
	</div>	
</div>
	
