<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="../localHeaderMinimal.jsp"%>
<%@ include file="../dialogSupport.jsp"%>

<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>


<%--<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%> --%>
<c:if test="${model.dialog != 'false'}">
<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
</c:if>

<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	$('#cancel-button').click(function(event){ 
		<c:choose>
			<c:when test="${model.dialog != 'false'}">closeReportingDialog(false);</c:when>
			<c:otherwise>
				window.location = window.location;
				//window.refresh();
				//document.location.href = '${model.cancelUrl}';
			</c:otherwise>
		</c:choose>
	});
	$('#preview-parameterizable-button').click(function(event){ 
		alert('submit');
		$('#preview-parameterizable-form').submit(); 
	});
});

</script>


<style type="text/css">
	* { margin: 0; }
	#container { height: 95%; border: 1px }
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
	.hr { height: 1px solid black; width: 40% } 
</style>

<div id="page">
	<div id="container">
		<div id="wrapper">
			<c:if test="${empty results}">
				<span><h2>${parameterizable.name}</h2></span>
				<span><h3>${parameterizable.description}</h3></span>
				
				
					<%-- <c:url var="postUrl" value='/module/reporting/reports/renderReport.form'/>--%>
					<%-- <c:url var="postUrl" value='/module/reporting/parameters/queryParameter.form'/>--%>
					<c:url var="postUrl" value='/module/reporting/reports/renderReport.form'/>
					<form id="preview-parameterizable-form" action="${postUrl}" method="POST">
						<input type="hidden" name="action" value="preview"/>
						<input type="hidden" name="uuid" value="${parameterizable.uuid}"/>
						<input type="hidden" name="type" value="${parameterizable['class'].name}"/>
						<input type="hidden" name="format" value="indicator"/>
						
						<ul>								
							<spring:hasBindErrors name="indicatorForm">  
								<li>
									<div class="errors"> 
										<font color="red"> 
											<h3><u>Please correct the following errors</u></h3>   
											
											<springform:errors path="parameterizable"></springform:errors>
										</font>  
									</div>
								</li>
							</spring:hasBindErrors>
						</ul>																
						<ul>									
							<c:forEach var="parameter" items="${parameterizable.parameters}">				
								<li>
									<label class="desc" for="${parameter.name}">${parameter.label}</label>
									<div>						
										<wgt:widget id="${parameter.name}" name="${parameter.name}" defaultValue="<%= new java.util.Date() %>" type="${parameter.type.name}"/>	
									</div>
								</li>						
							</c:forEach>
						</ul>
					</form>	
				</c:if>
				
				Results:  ${results}
				
				
				<c:if test="${!empty results}">
					<ul>
						<li>				
							<h4>Evaluation Result</h4>
							<div>
								<span>Result Type: </span>
								<strong>${results['class'].simpleName}</strong>							
							</div>
							<div>
								<span>Result:</span>						
								<span>
									<strong>${results}</strong>
								</span>
							</div>
						</li>
					</ul>
				</c:if>
		</div>
		<div class="buttonBar" align="left">						
			<input class="button" id="preview-parameterizable-button" type="button" value="Run" />
			<input class="button" id="cancel-button" type="button" value="Cancel"/>								
		</div>					
	</div>	
</div>
	
	
	

</div>

</div>


