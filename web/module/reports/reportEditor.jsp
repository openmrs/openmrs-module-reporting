<%@ include file="/WEB-INF/view/module/reporting/localHeader.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportManager.list" />

<c:url value="reportEditor.form" var="pageUrl">
	<c:param name="uuid" value="${report.uuid}" />
</c:url>

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/report/reportEditor.form"/>';
		});
		
	} );
</script>

<div id="page">
	<div id="container">
		<h1>Report Editor</h1>
		
		<c:choose>
			
			<c:when test="${report.uuid == null}">
				<b class="boxHeader">Create New Report</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.report.ReportDefinition|size=380|mode=edit|dialog=false|cancelUrl=reportManager.form|successUrl=reportEditor.form?type=org.openmrs.module.report.ReportDefinition&uuid=uuid" />
				</div>
			</c:when>
			
			<c:otherwise>
		
				<table style="font-size:small;">
					<tr>
						<td valign="top">
							<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${report.class.name}|uuid=${report.uuid}|size=380|label=Basic Details" />
							<br/>
							<b class="boxHeader">Parameters</b>
							<div class="box" style="vertical-align:top;">
								<c:forEach items="${report.parameters}" var="p">
									<openmrs:portlet url="parameter" id="parameter${p.name}" moduleId="reporting" 
													 parameters="type=${report.class.name}|uuid=${report.uuid}|name=${p.name}|label=Parameter: ${p.name}" />
									<br/>
								</c:forEach>
								<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" 
												 parameters="type=${report.class.name}|uuid=${report.uuid}|mode=add|label=New Parameter" />
							</div>
							<br/>
							<openmrs:portlet url="mappedProperty" id="baseCohortDefinition" moduleId="reporting" 
											 parameters="type=${report.class.name}|uuid=${report.uuid}|property=baseCohortDefinition|label=Base Cohort Definition|nullValueLabel=All Patients" />
						</td>
						<td valign="top" width="100%">
							<b class="boxHeader">Dataset Definitions</b>
							<div class="box" style="vertical-align:top;">
								<c:forEach items="${report.dataSetDefinitions}" var="dsd" varStatus="dsdStatus">
									<openmrs:portlet url="mappedProperty" id="dsd${dsd.key}" moduleId="reporting" 
													parameters="type=${report.class.name}|uuid=${report.uuid}|property=dataSetDefinitions|currentKey=${dsd.key}|label=${dsd.value.parameterizable.name}|parentUrl=${pageUrl}" />
									<br/>
								</c:forEach>
								<openmrs:portlet url="mappedProperty" id="newDsd" moduleId="reporting" 
												 parameters="type=${report.class.name}|uuid=${report.uuid}|property=dataSetDefinitions|mode=add|label=New Dataset Definition" />
							</div>
							
						</td>
					</tr>
				</table>
				
			</c:otherwise>
			
		</c:choose>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>