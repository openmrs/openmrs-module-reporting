<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Cohort Definitions" otherwise="/login.htm" redirect="/module/reporting/cohorts/manageCohortDefinitions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<%-- 
<c:url value="/module/reporting/cohorts/sqlCohortDefinition.form" var="pageUrl">
	<c:param name="uuid" value="${definition.uuid}" />
</c:url>
--%>


<style>

</style>

<c:set var="pageUrl" value="/module/reporting/cohorts/sqlCohortDefinition.form?uuid=uuid"/>



<div id="page" style="padding-top: 25px;">

	<div id="container">

		<c:choose>
			<c:when test="${definition.uuid == null}">
		
				<b class="boxHeader">Create SQL Cohort Query</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition|size=380|mode=edit|dialog=false|cancelUrl=manageCohortDefinitions.form|successUrl=${pageUrl}" />
				</div>
				
			</c:when>		
			<c:otherwise>
			
				<script>
					$(document).ready(function() {
						makeDialog('saveAsDialog');
						$('#saveAsButton').click(function(event) {
							showDialog('saveAsDialog', 'Save a Copy');
						});
					});
				</script>
		
				<table cellspacing="10" cellpadding="10">
					<tr valign="top">
						<td width="34%">	
			
							<openmrs:portlet 
								id="baseMetadata" 
								url="baseMetadata" 
								moduleId="reporting" 
								parameters="type=${definition.class.name}|uuid=${definition.uuid}|label=Basic Details" />
							
							<openmrs:portlet 
								id="newParameter" 
								url="parameter" 
								moduleId="reporting" 
								parameters="type=${definition.class.name}|uuid=${definition.uuid}|label=Parameters|parentUrl=${pageUrl}" />
				
						</td>
						<td width="66%">
							<div style="margin: 0.1em; padding-bottom:0.3em; padding-top:0.1em;"> <!--  .portlet -->
								<b class="boxHeader">SQL Query</b>
								<div class="box" style="padding: 25px;">
									<form method="post" action="sqlCohortDefinitionAssignQueryString.form">
										<input type="hidden" name="uuid" value="${definition.uuid}"/>
										<textarea rows="10" cols="80" id="queryString" name="queryString">${definition.queryDefinition.queryString}</textarea>
										<br/>
										<span>
											<a href="javascript:void(0)" id="saveAsButton">Save as ...</a>
										</span>							
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<span style="float:right">
											<input type="submit" value="Save"/>
											<input id="closeButton" type="button" value="Close" onClick="window.location='manageCohortDefinitions.form';"/>
										</span>
									</form>
								</div>	
							</div>
						</td>
					</tr>
				</table>
				
				<div id="saveAsDialog" style="display:none">
					<form method="get" action="sqlCohortDefinitionClone.form">
						<input type="hidden" name="copyFromUuid" value="${definition.uuid}"/>
						<table>
							<tr>
								<th align="right">Name:</th>
								<td><input type="text" name="name"/></td>
							</tr>
							<tr valign="top">
								<th align="right">Description:</th>
								<td><textarea name="description"></textarea></td>
							</tr>
							<tr>
								<td></td>
								<td><input type="submit" value="Save As"/></td>
							</tr>
						</table>
					</form>
				</div>
		
			</c:otherwise>
		</c:choose>

	</div> <!-- #container -->

</div><!-- #page -->

<%@ include file="/WEB-INF/template/footer.jsp"%>