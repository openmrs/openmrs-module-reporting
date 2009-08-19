<%@ include file="/WEB-INF/view/module/reporting/localHeader.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportManager.list" />

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/report/reportManager.list"/>';
		});

		$('#editBaseCohortDefinitionLink').click(function(event){
			showReportingDialog({
				title: 'Base Cohort Definition',
				url: 'mappedPropertyEditor.form?parentType=org.openmrs.module.report.ReportDefinition&parentUuid=${report.uuid}&mappedProperty=baseCohortDefinition',
				successCallback: function() { window.location.reload(true); }
			});
		});

	} );
</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>

<div id="page">
	<div id="container">
		<h1>Report Editor</h1>	
		
		<div id="report-basic-tab">
		
			<form method="post" action="saveReportDefinition.form">
				<input type="hidden" name="uuid" value="${report.uuid}"/>
				<input type="hidden" name="type" value="${report.class.name}"/>
				<ul>				

					<li>
						<label class="desc" for="name">Name</label>			
						<div>
							<input 	type="text" class="field text medium" id="name"  tabindex="2"
									name="name" value="${report.name}" size="50"/>
						</div>
					</li>
					<li>
						<label class="desc" for="description">Description</label>			
						<div>
							<textarea 	id="description" class="field text short" cols="50" tabindex="3"
										name="description">${report.description}</textarea>			
						</div>
					</li>
					<li>					
						<div align="left">				
							<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<button id="cancel-button" name="cancel">Cancel</button>
						</div>					
					</li>
					</ul>
				</form>
				<div style="width:375px;">
					<b class="boxHeader" style="font-weight:bold; text-align:right;">
						<span style="float:left;">Base Cohort Definition</span>
						<a style="color:lightyellow;" href="#" id="editBaseCohortDefinitionLink">Edit</a>
					</b>
					<div class="box">

						<table>
							<tr><th colspan="3" align="left">
								<c:choose>
									<c:when test="${report.baseCohortDefinition != null}">
										${report.baseCohortDefinition.parameterizable.name}
									</c:when>
									<c:otherwise>
										All Patients
									</c:otherwise>
								</c:choose>
							</th></tr>
							<c:forEach items="${report.baseCohortDefinition.parameterizable.parameters}" var="p">
								<tr>
									<td align="right">${p.name}</td>
									<td align="left">--&gt;</td>
									<td align="left" width="100%">
										<c:choose>
											<c:when test="${report.baseCohortDefinition.parameterMappings[p.name] == null}">
												<span style="color:red; font-style:italic;">Undefined</span>
											</c:when>
											<c:otherwise>${report.baseCohortDefinition.parameterMappings[p.name]}</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</div>
				<br/>
				<hr/>
				
			<rptTag:mappedField id="baseCohortDefNew" label="Base Cohort Definition" parentType="${report.class.name}" 
								parentObj="${report}" mappedProperty="baseCohortDefinition" 
								defaultValue="${report.baseCohortDefinition}" nullValueLabel="All Patients" width="375"/>
				
		</div>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>