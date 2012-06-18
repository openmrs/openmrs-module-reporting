<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../../localHeader.jsp"%>
<%@ taglib prefix="rptTag" tagdir="/WEB-INF/tags/module/reporting" %>
<%@ taglib prefix="rpt" uri="/WEB-INF/view/module/reporting/resources/reporting.tld" %>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {

	$('#parameter-mapping-table').dataTable( {
		"bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": false,
		"bInfo": false,
		"bAutoWidth": false
	} );
});
</script>
<style>
.instr { font-size: 1.5em; }
</style>

<!-- Support for Spring errors holder -->
<spring:bind path="indicatorForm.*">
  <c:forEach var="error" items="${status.errorMessages}">
    <B><FONT color=RED>
      <BR><c:out value="${error}"/>
    </FONT></B>
  </c:forEach>
</spring:bind>



<div id="page">
	<div id="container">
		<h1>Create a new cohort indicator</h1>
					
		<form action="<c:url value="/module/reporting/indicators/indicatorWizard.form"/>" method="post">			
			<fieldset>
			<legend>Step 3</legend>			
				<div>
					<ul>		
						<li>
							<div>
								<label class="desc">Map parameters</label>
								
								
									<table id="parameter-mapping-table" class="display">
										<thead>
											<tr>
												<th>${indicatorForm.cohortDefinition.name}</th>
												<th></th>
												<th>${indicatorForm.cohortIndicator.name}</th>
												<th></th>
												<th></th>
											</tr>
										</thead>
										<tbody>										
											<tr>
												<td class=""></td>
												<td class=""></td>
												<td class="instr">Use the following default value</td>
												<td class="instr">Copy parameter from <strong>${indicatorForm.cohortDefinition.name}</strong></td>
												<td class="instr">Use existing parameter from <strong>${indicatorForm.cohortIndicator.name}</strong></td>
											</tr>
											<c:forEach var="parameter" items="${indicatorForm.cohortDefinition.parameters}" varStatus="varstatus">
												<tr>
													<td width="1%" valign="top" align="center">
														${parameter.name}
													</td>
													<td valign="top" align="center" width="5%" nowrap="true">&lt;- should map to -&gt;</td>
													<td width="5%" valign="top">
														<input type="radio" name="source_${parameter.name}" value="default"/>													 
														<input type="text" name="value_${parameter.name}" value="${parameter.defaultValue}"/>
													</td>
													<td width="10%" valign="top">
														<input type="radio" name="source_${parameter.name}" value="inherit"/>
														<input type="text" name="value_${parameter.name}" value="${parameter.name}"/>
													</td>
													<td width="10%" valign="top">
														<input disabled type="radio" name="source_${parameter.name}" value="existing"/>														
														<select name="value_${parameter.name}" disabled>
															<c:if test="${empty indicator.parameters}">
																<option>(choose parameter)</option>
															</c:if>
															<c:forEach var="mappedParameter" items="${indicator.parameters}">										
																<option>${mappedParameter.name}</option>
															</c:forEach>
														</select><br/>
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
							<input type="submit" name="_target1" value="Back">
							<input type="submit" name="_target3" value="Next" >
						</li>
					</ul>
				</div>
			</fieldset>			
		</form>
	</div>
</div>	


<%@ include file="/WEB-INF/template/footer.jsp"%>