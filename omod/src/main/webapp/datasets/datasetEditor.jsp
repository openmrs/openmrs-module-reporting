<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />			

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
	
		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition"/>';
		});

		<c:forEach items="${dataSetDefinition.parameters}" var="cdparam">
			$('#selectValue${cdparam.name}').val('t');
			$('#paramLabel${cdparam.name}').val('${cdparam.label}');
			$('#dynamicValue${cdparam.name}').show();
		</c:forEach>

		<c:forEach items="${configurationProperties}" var="p" varStatus="varStatus">
			$('#selectValue${p.field.name}').change(function(event){
				if ($(this).val() == 't') {
					$('#paramLabel${p.field.name}').val('${cdparam.label}');
					$('#dynamicValue${p.field.name}').show();
				}
				else {
					$('#paramLabel${p.field.name}').val('');
					$('#dynamicValue${p.field.name}').hide();
				}
				if ($(this).val() == 'f') {
					$('#fixedValue${p.field.name}').show();
				}
				else {
					$('#fixedValue${p.field.name}').hide();
				}
			});
			if ($('#${p.field.name}').val() != '') {
				$('#selectValue${p.field.name}').val('f');
				$('#fixedValue${p.field.name}').show();
			}
		</c:forEach>

	} );

</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	#cohort-definition-property-table td { font-size: small; }
</style>

<div id="page">

	<h1>
		<c:choose>
			<c:when test="${empty dataSetDefinition.uuid}">
				Unsaved DataSet Definition
			</c:when>
			<c:otherwise><c:out value="${dataSetDefinition.name}" /> </c:otherwise>
		</c:choose>
	</h1>

	<form method="post" action="saveDataSet.form">
		<input type="hidden" name="uuid" value="${dataSetDefinition.uuid}"/>
		<input type="hidden" name="type" value="${dataSetDefinition['class'].name}"/>

		<table style="font-size:small;">
			<tr>
				<td valign="top">
					<ul>				
						<li>
							<label class="desc" for="name">Name</label>
							<input type="text" id="name"  tabindex="2" name="name" value="${dataSetDefinition.name}" size="50"/>
						</li>
						<li>
							<label class="desc" for="description">Description</label>
							<textarea id="description" class="field text short" cols="50" rows="10" tabindex="3" name="description"><c:out value="${dataSetDefinition.description}" /> </textarea>
						</li>
						<li>
							<label class="desc" for="type">Type</label>
							<rpt:displayLabel type="${dataSetDefinition['class'].name}"/>
						</li>
					</ul>
				</td>
				<td valign="top" width="100%">
					<ul>
						<li>
							<c:forEach items="${groupedProperties}" var="entry" varStatus="entryStatus">
								<fieldset>
									<c:if test="${!empty entry.key}"><legend><c:out value="${entry.key}" /> </legend></c:if>
									<table id="cohort-definition-property-table">
										<c:forEach items="${entry.value}" var="p" varStatus="varStatus">
	
											<tr>
												<td valign="top" nowrap="true">
													<c:out value="${p.displayName}" /> 
													<c:if test="${p.required}"><span style="color:red;">*</span></c:if>
												</td>
												<td style="vertical-align:top;">
													<select id="selectValue${p.field.name}" name="parameter.${p.field.name}.allowAtEvaluation">
														<option value=""></option>
														<option value="f">Fixed value</option>
														<option value="t">Parameter</option>
													</select>
												</td>
												<td style="vertical-align:top; width:100%;">
													<span id="dynamicValue${p.field.name}" style="display:none;">
														labeled: <input type="text" id="paramLabel${p.field.name}" name="parameter.${p.field.name}.label" size="30"/>
													</span>
													<span id="fixedValue${p.field.name}" style="display:none;">
														<c:choose>
															<c:when test="${p.field.name == 'sqlQuery'}">
																<wgt:widget id="${p.field.name}" name="parameter.${p.field.name}.value" object="${dataSetDefinition}" property="${p.field.name}" attributes="rows=15|cols=70"/>
															</c:when>
															<c:otherwise>
																<wgt:widget id="${p.field.name}" name="parameter.${p.field.name}.value" object="${dataSetDefinition}" property="${p.field.name}"/>
															</c:otherwise>
														</c:choose>
													</span>
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
							</div>					
						</li>
					</ul>
				</td>
			</tr>
		</table>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>