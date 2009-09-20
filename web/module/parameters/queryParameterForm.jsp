<%@ include file="/WEB-INF/template/include.jsp"%> 
<%@ include file="../localHeaderMinimal.jsp"%>
<%@ include file="../dialogSupport.jsp"%>

<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>


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
	/*input, select, textarea, label, button, span { font-size: 2em; }*/ 
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
			<table>
				<tr valign="top">
					<td style="padding-right: 1em">
						<h1>${parameterizable.name}</h1>
						<h4>${parameterizable.description}</h4>
						
						
						<%-- <c:url var="postUrl" value='/module/reporting/reports/renderReport.form'/>--%>
						<%-- <c:url var="postUrl" value='/module/reporting/parameters/queryParameter.form'/>--%>
						<c:url var="postUrl" value='/module/reporting/parameters/queryParameter.form'/>
						<form id="preview-parameterizable-form" action="${postUrl}" method="POST">
							<input type="hidden" name="action" value="preview"/>
							<input type="hidden" name="uuid" value="${parameterizable.uuid}"/>
							<input type="hidden" name="type" value="${parameterizable.class.name}"/>
							<input type="hidden" name="format" value="${param.format}"/>
							<input type="hidden" name="successView" value="${param.successView}"/>
							
							<ul>								
								<spring:hasBindErrors name="indicatorForm">  
									<li>
										<div class="errors"> 
											<font color="red"> 
												<h3><u>Please correct the following errors</u></h3>   
												
												<form:errors path="parameterizable"></form:errors>
											</font>  
										</div>
									</li>
								</spring:hasBindErrors>
							</ul>	
							<div>			
								<c:if test="${empty parameterizable.parameters}">
									${parameterizable.name} does not have any parameters.  
									Click 'Run' to evaluate based on this objects properties.
								</c:if> 
							</div>
							<ul>									
								<c:forEach var="parameter" items="${parameterizable.parameters}">				
									<li>
										<label class="desc" for="${parameter.name}">${parameter.label}</label>
										<div>						
											<rpt:widget id="${parameter.name}" name="${parameter.name}" defaultValue="<%= new java.util.Date() %>" type="${parameter.type.name}"/>	
										</div>
									</li>						
								</c:forEach>
							</ul>
						</form>	
					</td>
					
					<c:if test="${!empty results}">
						<td style="padding-left: 1em; border-left: 1px #e0e0e0 solid">
							<h1>Evaluation Result</h1>
							<rpt:format object="${results}"/>
						</td>
					</c:if>
				</tr>
			</table>
		</div>
		<div class="buttonBar" align="left">						
			<input class="button" id="preview-parameterizable-button" type="button" value="Run" />
			<input class="button" id="cancel-button" type="button" value="Cancel"/>								
		</div>					
	</div>	
</div>
	
	
	

</div>

</div>


