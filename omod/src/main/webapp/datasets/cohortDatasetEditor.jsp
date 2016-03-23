<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />

<c:url value="/module/reporting/datasets/cohortDatasetEditor.form" var="pageUrl">
	<c:param name="uuid" value="${dsd.uuid}" />
</c:url>

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {

		// Redirect to listing page
		$j('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/definition/manageDefinitions.list?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition"/>';
		});
	} );
</script>

<div id="page">
	<div id="container">
		<h1>Cohort DataSet Editor</h1>
		
		<c:choose>
			
			<c:when test="${dsd.id == null}">
				<b class="boxHeader">Create New Cohort DataSet</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition|size=380|mode=edit|dialog=false|cancelUrl=../definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition|successUrl=/module/reporting/datasets/cohortDatasetEditor.form?type=org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition&uuid=uuid" />
				</div>
			</c:when>
			
			<c:otherwise>
		
				<table style="width:100%;">
					<tr>
						<td valign="top" style="width:30%; white-space: nowrap;">
							<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|size=380|label=Basic Details" />
							<br/>
							<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|label=Parameters|parentUrl=${pageUrl}" />
							<br/>
						</td>
						<td valign="top" style="width:70%;">
							<table width="100%" style="font-size:small;"><tr>
								<td style="padding-right:50px;"><openmrs:portlet url="mappedProperty" id="newRow" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|property=rows|mode=add|label=Add a Row" /></td>
								<td align="right"><openmrs:portlet url="mappedProperty" id="newColumn" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|property=columns|mode=add|label=Add a Column" /></td>
							</tr></table>
							<table width="100%" border="1" style="font-size:smaller;">
								<c:if test="${!empty dsd.columns}">
									<tr>
										<c:if test="${!empty dsd.rows}"><td>&nbsp;</td></c:if>									
										<c:forEach items="${dsd.columns}" var="columnEntry" varStatus="columnStatus">
											<td>
												<openmrs:portlet url="mappedProperty" id="column${columnStatus.index}" moduleId="reporting" 
															parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|property=columns|currentKey=${columnEntry.key}|label=${columnEntry.key}|parentUrl=${pageUrl}|viewId=dsdView${columnStatus.index}|headerClass=columnHeader" />
											</td>
										</c:forEach>
									</tr>
								</c:if>
								<c:if test="${empty dsd.rows}">
									<tr>
										<c:forEach items="${dsd.columns}" var="columnEntry" varStatus="columnStatus">
											<td align="center" style="font-weight:bold; color:blue;" nowrap>${columnEntry.key}</td>
										</c:forEach>
									</tr>
								</c:if>
								<c:forEach items="${dsd.rows}" var="rowEntry" varStatus="rowStatus">
									<tr>
										<td nowrap>
											<openmrs:portlet url="mappedProperty" id="row${rowStatus.index}" moduleId="reporting" 
														 	parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|property=rows|currentKey=${rowEntry.key}|label=${rowEntry.key}|parentUrl=${pageUrl}|viewId=dsdView${rowStatus.index}|headerClass=rowHeader" />
										</td>
										<c:forEach items="${dsd.columns}" var="columnEntry" varStatus="columnStatus">
											<td align="center" style="font-weight:bold; color:blue;" nowrap>${rowEntry.key}.${columnEntry.key}</td>
										</c:forEach>
										<c:if test="${empty dsd.columns}">
											<td align="center" style="font-weight:bold; color:blue;" nowrap>${rowEntry.key}</td>
										</c:if>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</table>
				
			</c:otherwise>
			
		</c:choose>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>