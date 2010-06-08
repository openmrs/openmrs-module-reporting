<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
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
			<form id="saveIndicatorForm" method="post" action="<c:url value='/module/reporting/indicators/periodIndicator.form'/>">
					<ul>								
						<spring:hasBindErrors name="indicator">  
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
									<input id="name" name="name" type="text" class="field text large" 
										value="${indicator.name}" size="20" tabindex="1" />
								</div>
							</li>		
							<li>		
								<label class="desc" for="description">Description</label>
								<div>
									<textarea id="description" name="description" 
										class="field textarea small" rows="3" cols="20"
										tabindex="2">${indicator.description}</textarea>				
								</div>					
							</li>
							
						<li>
							<div align="center">				
								
								<!-- hidden form fields from URL--> 
								<input type="hidden" name="uuid" value="${param.uuid}"/> 
								<input type="hidden" name="type" value="${param.type}"/> 
								<input type="hidden" name="redirectUrl" value="${param.redirectUrl}"/> 

								<input id="save-parameter-button" type="submit" name="action" value="Save" />
								<input id="cancel-parameter-button" type="button" name="action" value="Cancel"/>								
							</div>					
						</li>
					</ul>				
		
			</form>

		</div>		
	</div>	
</div>
	
