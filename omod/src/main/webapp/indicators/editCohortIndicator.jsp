<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Indicator Definitions" otherwise="/login.htm" redirect="/module/reporting/indicators/editCohortIndicator.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script>					
	$j(document).ready(function() {
		$j("#preview-indicator").click(function(event){
			showReportingDialog({ 
				title: 'Preview Indicator', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${indicator.uuid}&type=${indicator['class'].name}',
				successCallback: function() { 
					window.location = window.location; //.reload(true);
				} 
			});
		});
	} );
</script>

<c:set var="pageUrl" value="/module/reporting/indicators/editCohortIndicator.form?uuid=uuid"/>

<c:choose>
	<c:when test="${indicator.id == null}">
		<b class="boxHeader"><spring:message code="reporting.createCohortIndicator" /></b>
		<div class="box">
			<openmrs:portlet url="baseCohortIndicator" id="baseCohortIndicator" moduleId="reporting" parameters="size=380|mode=edit|dialog=false|cancelUrl=manageIndicators.form|successUrl=${pageUrl}" />
		</div>
	</c:when>		
	<c:otherwise>
		<table width="100%">
			<tr valign="top">
				<td width="50%">	
					<openmrs:portlet url="baseCohortIndicator" id="baseCohortIndicator" moduleId="reporting" parameters="uuid=${indicator.uuid}|label=Basic Details" />			
					<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${indicator['class'].name}|uuid=${indicator.uuid}|label=Parameters|parentUrl=${pageUrl}" />						
					<span style="padding-left: 10px; ">
						<a href="javascript:void(0)" id="preview-indicator" style="color:green; text-decoration:none;">
							<spring:message code="reporting.preview" /> <img src="<c:url value='/images/play.gif'/>" border="0" style="vertical-align:middle;"/>
						</a>
					</span>
				</td>
				<td width="50%">
					<openmrs:portlet url="mappedProperty" id="locationFilter" moduleId="reporting" 
					 parameters="type=${indicator['class'].name}|uuid=${indicator.uuid}|property=locationFilter|label=Location Filter|tag=Location" />

					<c:choose>
					
						<c:when test="${indicator.type == 'FRACTION'}">
						
							<openmrs:portlet url="mappedProperty" id="cohortDefinition" moduleId="reporting" 
					 		parameters="type=${indicator['class'].name}|uuid=${indicator.uuid}|property=cohortDefinition|label=Numerator" />
					 		
					 		<openmrs:portlet url="mappedProperty" id="denominator" moduleId="reporting" 
					 		parameters="type=${indicator['class'].name}|uuid=${indicator.uuid}|property=denominator|label=Denominator" />
					 		
						</c:when>
						
						<c:when test="${indicator.type == 'LOGIC'}">
						
							<openmrs:portlet url="mappedProperty" id="cohortDefinition" moduleId="reporting" 
							parameters="type=${indicator['class'].name}|uuid=${indicator.uuid}|property=cohortDefinition|label=Cohort Definition" />
							
							<openmrs:portlet url="baseCohortIndicator" id="logicExpression" moduleId="reporting" 
							parameters="uuid=${indicator.uuid}|label=Logic Expression|subfields=logic" />			
							
						</c:when>
						<c:otherwise>
						
							<openmrs:portlet url="mappedProperty" id="cohortDefinition" moduleId="reporting" 
							parameters="type=${indicator['class'].name}|uuid=${indicator.uuid}|property=cohortDefinition|label=Cohort Definition" />
						
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>
