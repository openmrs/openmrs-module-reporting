<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ include file="../localHeader.jsp"%>
<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>

<script type="text/javascript" charset="utf-8">
var jQ = jQuery.noConflict();
jQ(document).ready(function() {

	jQ('#cancel-button').click(function(event){
		// Redirect to the listing page
		window.location.href='<c:url value="/module/reporting/manageCohortDefinitions.list"/>';
	});
	
} );

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
		<div>			
			<form:form id="saveForm" commandName="parameter" method="POST">
				<fieldset>
					<legend>Parameter Editor</legend>
					<ul>								
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
								<!-- <input type="hidden" name="redirectUrl" value="${param.redirectUrl}"/> -->
								<input id="save-button" name="save" type="submit" value="Save" />
								<button id="cancel-button" name="cancel">Cancel</button>
							</div>					
						</li>
					</ul>				
				</fieldset>
			</form:form>
		</div>		
	</div>	
</div>
	
