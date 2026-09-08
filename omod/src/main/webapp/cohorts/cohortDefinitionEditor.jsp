<%@ include file="../manage/localHeader.jsp"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>

<openmrs:require privilege="Manage Cohort Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition" />			

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
	
		// Redirect to listing page
		$j('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.cohort.definition.CohortDefinition"/>';
		});

		$j("#previewButton").click(function(event){
			showReportingDialog({ 
				title: 'Preview <rpt:displayLabel type="${cohortDefinition['class'].name}"/>', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${cohortDefinition.uuid}&type=${cohortDefinition['class'].name}',
				successCallback: function() { 
					window.location = window.location; //.reload(true);
				} 
			});
		});

		<c:forEach items="${cohortDefinition.parameters}" var="cdparam">
			$j('#selectValue${cdparam.name}').val('t');
			$j('#paramLabel${cdparam.name}').val('${cdparam.label}');
			<c:if test="${cdparam.widgetConfiguration != null}">
				<c:forEach items="${cdparam.widgetConfiguration}" var="configParam">
					$j('#paramConfigOptions${cdparam.name}').append('${configParam.key}=${configParam.value}\n');
				</c:forEach>
				$j('#configureParameterSection${cdparam.name}').show();
			</c:if>
			$j('#dynamicValue${cdparam.name}').show();
		</c:forEach>

		<c:forEach items="${configurationProperties}" var="p" varStatus="varStatus">
			$j('#selectValue${p.field.name}').change(function(event){
				if ($j(this).val() == 't') {
					$j('#paramLabel${p.field.name}').val('${cdparam.label}');
					$j('#dynamicValue${p.field.name}').show();
				}
				else {
					$j('#paramLabel${p.field.name}').val('');
					$j('#dynamicValue${p.field.name}').hide();
				}
				if ($j(this).val() == 'f') {
					$j('#fixedValue${p.field.name}').show();
				}
				else {
					$j("#fixedValue${p.field.name} [id^='${p.field.name}']").val('');
					$j('#fixedValue${p.field.name}').hide();
				}
			});
			
			$j("#fixedValue${p.field.name} [id^='${p.field.name}']").each(function() {
				if ($j(this).val() && $j(this).val != '') {
					$j('#selectValue${p.field.name}').val('f');
					$j('#fixedValue${p.field.name}').show();
				}
			});
			
			$j("#configureParameter${p.field.name}").click(function(event){
				$j('#configureParameterSection${p.field.name}').toggle();
			});
		</c:forEach>
	} );

</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; font-weight:bold; }
	label.desc { display:block; }
	label.inline { display:inline; }
	input[type="text"] { padding: 2px; font-size: 1em; } 
	textarea { padding: 2px; font-size: 1em; } 
	legend { padding: 1em; } 
	fieldset { padding: 1em; } 
	
</style>

<div id="page">

	<h1>
		<c:choose>
			<c:when test="${empty cohortDefinition.uuid || empty cohortDefinition.name}">
				(unsaved <rpt:displayLabel type="${cohortDefinition['class'].name}"/>)
			</c:when>
			<c:otherwise>${cohortDefinition.name}</c:otherwise>
		</c:choose>
	</h1>

	<springform:form method="post" modelAttribute="cohortDefinition" action="saveCohortDefinition.form">
		<input type="hidden" name="uuid" value="${cohortDefinition.uuid}"/>
		<input type="hidden" name="type" value="${cohortDefinition['class'].name}"/>

		<table style="font-size:small;">
			<tr>
				<td valign="top">
					<ul>				
						<li>
							<label class="inline" for="type">Type</label>
							<rpt:displayLabel type="${cohortDefinition['class'].name}"/>
						</li>
						<li>
							<label class="desc" for="name"><spring:message code="reporting.name" /></label>
			                <springform:input path="name" size="50" tabindex="2"/> 
			                <br/><springform:errors path="name" cssClass="error"/>
						</li>
						<li>
							<label class="desc" for="description"><spring:message code="reporting.description" /></label>
							<springform:textarea path="description" cols="50" rows="6" tabindex="3"/>
						</li>
					</ul>
				</td>
				<td valign="top" width="100%">
					<ul>
						<li>
						
							<label class="desc" for="name"><spring:message code="reporting.properties" /></label>
							<c:forEach var="entry" items="${groupedProperties}" varStatus="entryStatus">
								<fieldset>
									<c:if test="${!empty entry.key}"><legend>${entry.key}</legend></c:if>
									<table id="cohort-definition-property-table">
										<c:forEach items="${entry.value}" var="p" varStatus="varStatus">
	
											<tr>
												<td valign="top" nowrap="true">
													${p.displayName}
													<c:if test="${p.required}"><span style="color:red;">*</span></c:if>
												</td>
												<td style="vertical-align:top;">
													<select id="selectValue${p.field.name}" name="parameter.${p.field.name}.allowAtEvaluation">
														<option value=""></option>
														<option value="f"><spring:message code="reporting.fixedValue" /></option>
														<option value="t"><spring:message code="reporting.parameter" /></option>
													</select>
												</td>
												<td style="vertical-align:top; width:100%;">
													<span id="dynamicValue${p.field.name}" style="display:none;">
														label as <input type="text" id="paramLabel${p.field.name}" name="parameter.${p.field.name}.label" size="30"/>
														<span>
															<a href="#" id="configureParameter${p.field.name}"><spring:message code="reporting.advancedConfig" /></a>
															<div id="configureParameterSection${p.field.name}" style="display:none;">
																<textarea id="paramConfigOptions${p.field.name}" name="parameter.${p.field.name}.widgetConfiguration" cols="50" rows="3"></textarea>
															</div>
														</span>
													</span>
													<span id="fixedValue${p.field.name}" style="display:none;">
														<wgt:widget id="${p.field.name}" name="parameter.${p.field.name}.value" object="${cohortDefinition}" property="${p.field.name}" format="${p.displayFormat}" attributes="${p.displayAttributes}"/>
													</span>
													<spring:bind path="${p.field.name}">
														<c:if test="${status.errorMessage != ''}">
															<span style="vertical-align:top;" class="error">${status.errorMessage}</span>
														</c:if>
													</spring:bind>
												</td>
											</tr>
										</c:forEach>
									</table>
								</fieldset>
								<br/>
							</c:forEach>
						</li>
						<li>					
							<div align="center">				
								<input id="save-button" type="submit" value="Save" tabindex="7" />
								<input id="cancel-button" name="cancel" type="button" value="Cancel"/>
								<c:if test="${!empty cohortDefinition.uuid}">
									<input id="previewButton" name="preview" type="button" value="Preview"/>
								</c:if>
							</div>
						</li>
					</ul>
				</td>
			</tr>
		</table>
	</springform:form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
