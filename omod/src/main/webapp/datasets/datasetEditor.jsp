<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />			

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() { 
		// Redirect to listing page
		$j('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition"/>';
		});

		<c:forEach items="${dataSetDefinition.parameters}" var="cdparam">
			$j('#selectValue${cdparam.name}').val('t');
			$j('#paramLabel${cdparam.name}').val('${cdparam.label}');
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
					$j('#fixedValue${p.field.name}').hide();
				}
			});
			if ($j('#${p.field.name}').val() != '') {
				$j('#selectValue${p.field.name}').val('f');
				$j('#fixedValue${p.field.name}').show();
			}
		</c:forEach>
		
		$j('#loadAffectedReports').click(function() {
			$j.ajax({
				type: 'POST',
			    contentType : "application/json",
			    url: 'loadAffectedDatasetDefs.form?uuid=' + '${dataSetDefinition.uuid}',
			    dataType: 'json',
			    success: function(json) {
			    	$j('#affectedReports').html('');
				    $j('#affectedReports').append('<ul></ul>')
		                if (json.length > 0) {
		            	    for (i in json) {
		        		         var uuid = uuid = json[i].uuid;
		                         var url = "${pageContext.request.contextPath}/module/reporting/reports/reportEditor.form?uuid=" + uuid;
		        		         $j('#affectedReports ul').append('<li><a href=' + url + '>' + json[i].name + "</a></li>");	        	 
		                    }
		                } else {
		        	        $j('#affectedReports').append('<li><b><spring:message code="reporting.noAffectedReportDefs.label" /></b></li>');
		                }
			    },
			    error: function() {
				    $j('#error-load-affected-defs').text('<spring:message code="dao.error.title" />');
			    }
			});
		});

		
	} );
	
</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	#cohort-definition-property-table td { font-size: small; }
	#loadAffectedReports {font-weight:bold;}
	#affectedReports {padding:5px; }
	#affectedReports li {padding:1.5px;font-weight:bold; }
	#error-load-affected-defs {font-weight:bold; color:#ff0000; padding:1.5px; }
</style>

<div id="page">

	<h1>
		<c:choose>
			<c:when test="${empty dataSetDefinition.uuid}">
				Unsaved DataSet Definition
			</c:when>
			<c:otherwise>${dataSetDefinition.name}</c:otherwise>
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
							<textarea id="description" class="field text short" cols="50" rows="10" tabindex="3" name="description">${dataSetDefinition.description}</textarea>
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
                         <tr>
        			<td>
		      			<c:if test="${!empty dataSetDefinition.name}">
			      			<div>
		          				<fieldset>
		        	   				<legend><spring:message code="reporting.affectedReportDefinitions.label" /></legend>
		             					<div id="affectedReports">
		                					<a href="javascript:void(0);" id="loadAffectedReports"><spring:message code="reporting.loadAffectedReportDefinitions.label" /></a><br/>
		                					<span id="error-load-affected-defs"></span>
		             					</div>
		          				</fieldset>         
		        			</div>
		      			</c:if>
	      			</td>
       			 </tr>
		</table>
	</form>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>