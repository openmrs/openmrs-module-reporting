<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>
<%@ taglib prefix="rptTag" tagdir="/WEB-INF/tags/module/reporting" %>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/reporting/resources/reporting.tld" %>

<script type="text/javascript" charset="utf-8">
//var $j = jQuery.noConflict();
$(document).ready(function() {

	// ======  Tabs: Cohort Definition  ======================================

	$('#cohort-definition-tabs').tabs();

	
	// ======  DataTable: Cohort Definition Parameter  ======================================
	
	$('#cohort-definition-parameter-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );

	// Redirect to listing page
	$('#cancel-button').click(function(event){
		window.location.href='<c:url value="/module/reporting/manageCohortDefinition.list"/>';
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

	<h1>Cohort Definition Editor</h1>

		<div id="cohort-definition-tabs" >			
			<ul>
                <li><a href="#cohort-definition-basic-tab"><span>Basic</span></a></li>
                <li><a href="#cohort-definition-advanced-tab"><span>Advanced</span></a></li>
            </ul>

	
			<div id="cohort-definition-basic-tab">
				<form method="post" action="saveCohortDefinition.form">
					<input type="hidden" name="uuid" value="${cohortDefinition.uuid}"/>
					<input type="hidden" name="type" value="${cohortDefinition.class.name}"/>
					<ul>				
						<li>
							<label class="desc" for="id">ID</label>				
							<div>
								<c:choose>
									<c:when test="${!empty cohortDefinition.uuid}">${cohortDefinition.uuid}</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${!empty cohortDefinition.id}">${cohortDefinition.id}</c:when>
											<c:otherwise>(new)</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>	
							</div>
						</li>				
						<li>
							<label class="desc" for="type">Type</label>
							<div>${cohortDefinition.class.name}</div>
						</li>
						
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
								<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
								<button id="cancel-button" name="cancel">Cancel</button>
							</div>					
						</li>
					</ul>				
				</form>
			</div>
			
			
			<div id="cohort-definition-advanced-tab">
				<form method="post" action="<c:url value='/module/reporting/parameter.form'/>">
					<fieldset>
						<ul>
							<li>
								<label class="desc">Parameters</label>			
								<a href="<c:url value='/module/reporting/parameter.form'/>?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}&redirectUrl=/module/reporting/manageCohortDefinitions.list">Add new parameter</a>	
								
								<div>
									<table id="cohort-definition-parameter-table" class="display">
										<thead>
											<tr>
												<th align="left">Edit</th>
												<th align="left">Name</th>
												<th align="left">Label</th>
												<th align="left">Type</th>
												<th align="left">Collection Type</th>
												<th align="left">Default Value</th>
											</tr>	
										</thead>
										<tbody>
											<c:forEach items="${cohortDefinition.parameters}" var="parameter" varStatus="varStatus">
												<tr <c:if test="${varStatus.index % 2 == 0}">class="odd"</c:if>>
													<td valign="top" nowrap="true">
														<a href="<c:url value='/module/reporting/parameter.form'/>?uuid=${cohortDefinition.uuid}&type=${cohortDefinition.class.name}&parameterName=${parameter.name}&redirectUrl=/module/reporting/manageCohortDefinitions.list">Edit</a>
													</td>
													<td valign="top" nowrap="true">
														${parameter.name}
													</td>
													<td valign="top">
														${parameter.label}
													</td>
													<td valign="top">
														${parameter.clazz}																									
													</td>
													<td valign="top">
														${parameter.collectionType}
													</td>
													<td valign="top">
														${parameter.defaultValue}
													</td>
												</tr>	
											</c:forEach>
										</tbody>
									</table>
								</div>
							</li>
						</ul>
					</fieldset>
				</form>
			</div>
			
			
		</div>
	</div>	

