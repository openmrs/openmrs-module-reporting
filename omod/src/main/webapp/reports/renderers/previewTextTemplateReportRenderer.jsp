<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />

<c:choose>
	<c:when test="${not iframe}">
		<%@ include file="/WEB-INF/template/header.jsp"%>
	</c:when>
	<c:otherwise>
		<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
	</c:otherwise>
</c:choose>



<script type="text/javascript" charset="utf-8">

	//update the temp design field
	<c:if test="${!empty tempDesignUuid}">
		$j( "#reportDesignForm input#uuid", window.parent.document ).val("${tempDesignUuid}");
	</c:if>

	var fixedValueElementIds = new Array();
	jQuery(document).ready( function() {

			// disable the Report Definition select
			var reportDefSelect = $j( "#reportDesignForm select#reportDefinition", window.parent.document );
			if ( reportDefSelect.length ) {
				reportDefSelect.prev().text( "${reportDefinition.name}" ).show();
				reportDefSelect.hide();
			}

			for( var i in fixedValueElementIds ){
				jQuery( "#" + fixedValueElementIds[ i ] ).addClass( fixedValueElementIds[ i ] );
			}
			
	});

	function toggleInputElements( idPrefix ){
		jQuery( '.'+idPrefix ).toggle();
	}
	

</script>

<table style="width:99%;">
	<tr>
		<td style="white-space:nowrap; vertical-align:top; padding-right:10px;">
			<form id="previewForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/previewTextTemplateReportRenderer.form">
				<input type="hidden" id="reportDefinition" name="reportDefinition" value="${reportDefinition.uuid}"/>
				<input type="hidden" id="uuid" name="uuid" value="${design.uuid}" />
				<input type="hidden" name="rendererType" value="${rendererType.name}"/>
				<c:if test="${!empty iframe}">
					<input type="hidden" name="iframe" value="${iframe}"/>
				</c:if>
				<c:if test="${!empty scriptType}">
					<textarea style="display:none;" name="script">${script}</textarea>
					<input type="hidden" name="scriptType" value="${scriptType}"/>
				</c:if>
				<table style="padding:10px;">
					<c:forEach var="parameter" items="${reportDefinition.parameters}">
						<script type="text/javascript">
							fixedValueElementIds.push('userEnteredParam${parameter.name}');
						</script>
		                <tr>
		                    
				            <td><spring:message code="${parameter.label}"/>:</td>
		                    <td>
		                    	<spring:bind path="userParams.userEnteredParams[${parameter.name}]">
	                   				<c:choose>
										<c:when test="${parameter.collectionType != null}">
											<wgt:widget id="userEnteredParam${parameter.name}" name="${status.expression}" type="${parameter.collectionType.name}" genericTypes="${parameter.type.name}" defaultValue="${status.value}" attributes="${parameter.widgetConfigurationAsString}"/>	
										</c:when>
										<c:otherwise>
											<wgt:widget id="userEnteredParam${parameter.name}" name="${status.expression}" type="${parameter.type.name}" defaultValue="${status.value}" attributes="${parameter.widgetConfigurationAsString}"/>	
											<c:if test="${fn:contains(expSupportedTypes, parameter.type.name)}">
												<spring:bind path="userParams.expressions[${parameter.name}]">
													<input class="userEnteredParam${parameter.name}" type="text" name="${status.expression}" value="${status.value}" style="display: none" /> 
													<span onclick="toggleInputElements('userEnteredParam${parameter.name}')">
														<input class="userEnteredParam${parameter.name} smallButton" type="button" value='<spring:message code="reporting.Report.run.enterExpression"/>' style="width:100px;"/>
														<input class="userEnteredParam${parameter.name} smallButton" type="button" value='<spring:message code="reporting.Report.run.enterFixedValue"/>' style="display:none; width:100px;" />
													</span>
													<c:if test="${not empty status.errorMessage}">
					                            		<span class="error">${status.errorMessage}</span>
					                        		</c:if>
												</spring:bind>
											</c:if>
										</c:otherwise>
									</c:choose>
									<c:if test="${not empty status.errorMessage}">
										<span class="error">${status.errorMessage}</span>
									</c:if>
								</spring:bind>
		                    </td>
		                </tr>
		            </c:forEach>
		            <openmrs:globalProperty var="mode" key="reporting.runReportCohortFilterMode" defaultValue="showIfNull"/>
					<c:set var="showCohortFilter" value="${mode == 'hide' ? false : (mode == 'show' ? true : reportDefinition.baseCohortDefinition == null)}"/>
					<c:if test="${showCohortFilter}">
						<tr>
							<td>
								<spring:message code="reporting.TextTemplateRenderer.preview.runForSpecificCohort"/>
							</td>
							<td>
								<spring:message code="reporting.Report.run.optionalFilterCohort" var="filterCohortLabel"/>
								<spring:message code="reporting.allPatients" var="allPatients"/>
								<rptTag:mappedPropertyForObject id="baseCohort" formFieldName="baseCohort" object="${userParams}" propertyName="baseCohort" label="${filterCohortLabel}" emptyValueLabel="${allPatients}"/>
				          	</td>
				         </tr>
					</c:if>
		            <tr>
		            	<td>
		            		<input type="submit" value="Preview"/>
		            	</td>
		            </tr>
				</table>
				<br/>
				<hr/>
				<c:choose>
					<c:when test="${!empty errorDetails}">
						<b style="font-size:smaller;"><ps:exception exception="${errorDetails}"/></b><br/>
					</c:when>
					<c:when test="${!empty previewResult}">
						<div id="textarea-container" class="border">
							<textarea id="templateResult" name="results" cols="100" rows="80"><c:out value="${previewResult}"/></textarea>
						</div>
						<script type="text/javascript">
							var editor = CodeMirror.fromTextArea('templateResult', {
								height: "250px",
								parserfile: ["tokenizejavascript.js", "parsejavascript.js"],
								stylesheet: "${pageContext.request.contextPath}/moduleResources/reporting/css/reportDesigns/jscolors.css",
								path: "${pageContext.request.contextPath}/moduleResources/reporting/scripts/reportDesigns/",
								continuousScanning: 500,
								lineNumbers: true,
								textWrapping: false,
								autoMatchParens: true,
								tabMode: "spaces",
								readOnly: true
							});
						</script>						
					</c:when>
				</c:choose>
			</form>
		</td>
	</tr>
</table>