<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Cohort Definitions" otherwise="/login.htm" redirect="/module/reporting/cohorts/manageCohortDefinitions.form" />			

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {
	
		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/cohorts/manageCohortDefinitions.form"/>';
		});
		
	} );

</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>

<div id="page">

	<h1>Cohort Definition Editor</h1>

	<div id="cohort-definition-basic-tab">
		<form method="post" action="saveCohortDefinition.form">
			<input type="hidden" name="uuid" value="${cohortDefinition.uuid}"/>
			<input type="hidden" name="type" value="${cohortDefinition.class.name}"/>
			<ul>				
				<li>
					<label class="desc" for="name">Name</label>			
					<div>
						<input 	type="text" class="field text medium" id="name"  tabindex="2"
								name="name" value="${cohortDefinition.name}" size="50"/>
					</div>
				</li>
				<li>
					<label class="desc" for="description">Description</label>			
					<div>
						<textarea 	id="description" class="field text short" cols="80" tabindex="3"
									name="description">${cohortDefinition.description}</textarea>			
					</div>
				</li>
				<li>
					<label class="desc" for="type">Type</label>
					<div><spring:message code="reporting.${cohortDefinition.class.name}.name" /></div>
				</li>
				<li>
					<label class="desc">Fixed Properties</label>			
					
					<div>
						<table id="cohort-definition-property-table" class="display">
							<thead>
								<tr>
									<th align="left">Name</th>
									<th align="left">Dynamic</th>
									<th align="left">-or-</th>
									<th align="left" width="100%">Fixed Value</th>
								</tr>	
							</thead>
							<tbody>
								<c:forEach items="${cohortDefinition.configurationProperties}" var="p" varStatus="varStatus">
								
									<c:set var="isParam" value="f" />
									<c:forEach items="${cohortDefinition.parameters}" var="cdparam">
										<c:if test="${p.field.name == cdparam.name}">
											<c:set var="isParam" value="t" />
										</c:if>
									</c:forEach>

									<tr <c:if test="${varStatus.index % 2 == 0}">class="odd"</c:if>>
										<td valign="top" nowrap="true">
											${p.field.name}
											<c:if test="${p.required}"><span style="color:red;">*</span></c:if>
										</td>
										<td valign="top">
											<input 	type="checkbox" name="parameter.${p.field.name}.allowAtEvaluation" value="t"
													<c:if test="${isParam == 't'}">checked="true"</c:if>/>
										</td>
										<td>&nbsp;</td>
										<td valign="top">
											<rpt:widget id="${p.field.name}" name="parameter.${p.field.name}.value" object="${cohortDefinition}" property="${p.field.name}"/>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
							</tfoot>								
						</table>
					</div>
				</li>					
				<li>					
					<div align="center">				
						<input id="save-button" type="submit" value="Save" tabindex="7" />
						<input id="cancel-button" name="cancel" type="button" value="Cancel"/>
					</div>					
				</li>
			</ul>				
		</form>
	</div>
</div>