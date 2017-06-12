<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>

<c:if test="${model.dialog != 'false'}">
<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
</c:if>

<script type="text/javascript" charset="utf-8">

$j(document).ready(function() {
	$j('#cancel-button').click(function(event){
		<c:choose>
			<c:when test="${model.dialog != 'false'}">closeReportingDialog(false);</c:when>
			<c:otherwise>
				window.location = window.location;
				//window.refresh();
				//document.location.href = '${model.cancelUrl}';
			</c:otherwise>
		</c:choose>
	});
	$j('#preview-parameterizable-button').click(function(event){
		$j('#preview-parameterizable-form').submit();
	});
});

</script>


<style type="text/css">
	* { margin: 0; }
	#container { height: 95%; border: 1px }
	#wrapper { min-height: 100%; height: auto !important; height:100%; margin: 0 auto -4em; }
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
            <br/>
            <div>
                <c:if test="${randomBaseCohortSize != null}">
                    <spring:message code="reporting.previewLimitedToRandomCohort" arguments="${randomBaseCohortSize}" />
                </c:if>
            </div>
            <br/>
			<table height="100%">
				<tr valign="top">
					<td style="padding-right: 1em">
						<h4><c:out value="${parameterizable.name}" /></h4>
						<c:url var="postUrl" value='/module/reporting/parameters/queryParameter.form'/>
						<form id="preview-parameterizable-form" action="${postUrl}" method="POST">
							<input type="hidden" name="action" value="preview"/>
							<input type="hidden" name="uuid" value="${parameterizable.uuid}"/>
							<input type="hidden" name="type" value="${parameterizable['class'].name}"/>
							<input type="hidden" name="format" value="${param.format}"/>
							<input type="hidden" name="successView" value="${param.successView}"/>

							<ul>									
								<c:forEach var="parameter" items="${parameterizable.parameters}">				
									<li>
										<label class="desc" for="${parameter.name}">${parameter.label}</label>
										<div>
											<c:choose>
												<c:when test="${parameter.collectionType != null}">
													<wgt:widget id="${parameter.name}" name="${parameter.name}" type="${parameter.collectionType.name}" genericTypes="${parameter.type.name}" defaultValue="${evaluationContext.parameterValues[parameter.name]}" attributes="${parameter.widgetConfigurationAsString}"/>	
												</c:when>
												<c:otherwise>
													<wgt:widget id="${parameter.name}" name="${parameter.name}" type="${parameter.type.name}" defaultValue="${evaluationContext.parameterValues[parameter.name]}" attributes="${parameter.widgetConfigurationAsString}"/>	
												</c:otherwise>
											</c:choose>
										</div>
									</li>						
								</c:forEach>
							</ul>
							<input class="button" id="preview-parameterizable-button" type="button" value="Run" />
							<input class="button" id="cancel-button" type="button" value="Cancel"/>	
						</form>	
					</td>
					
					<c:if test="${!empty results}">
						<td style="padding-left: 1em; border-left: 1px #e0e0e0 solid">
							<h4><spring:message code="reporting.evaluationResult" /></h4>
							<i>${executionTime} <spring:message code="reporting.seconds" /></i><br/><br/>
							<rpt:format object="${results}"/>
						</td>
					</c:if>
				</tr>
			</table>
		</div>				
	</div>	
</div>

<c:if test="${model.dialog != 'false'}">
<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>
</c:if>
