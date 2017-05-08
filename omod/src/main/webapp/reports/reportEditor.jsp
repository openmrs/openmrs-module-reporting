<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.list" />

<c:url value="/module/reporting/reports/reportEditor.form" var="pageUrl">
	<c:param name="uuid" value="${report.uuid}" />
</c:url>

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {

		// Redirect to listing page
		$j('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/report/reportEditor.form"/>';
		});

		<c:forEach items="${designs}" var="design" varStatus="designStatus">
			$j('#${design.uuid}DesignEditLink').click(function(event){
				document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/renderers/editReportDesign.form?type=${design.rendererType.name}&reportDesignUuid=${design.uuid}&reportDefinitionUuid=${report.uuid}&returnUrl=${pageUrl}';
			});
			$j('#${design.uuid}DesignRemoveLink').click(function(event){
				if (confirm('Please confirm you wish to permanantly delete ${rpt:getSafeJsString(design.name)}')) {
					document.location.href='${pageContext.request.contextPath}/module/reporting/reports/deleteReportDesign.form?uuid=${design.uuid}&returnUrl=${pageUrl}';
				}
			});
		</c:forEach>
		$j( '#rendererType' ).change( function() {
			$j( this ).next( 'input#designAddLink' ).attr( 'href', $j( this ).val() );
		} );
		
		$j('#designAddLink').click(function(event){
			document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/renderers/editReportDesign.form?type=' + $j( this ).attr( 'href' ) + '&reportDefinitionUuid=${report.uuid}&returnUrl=${pageUrl}';
		});
		
		$j('#previewButton').click(function(event) {
			showReportingDialog({ 
				title: 'Preview Report', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${report.uuid}&type=${report['class'].name}'
			});
		}).height(32);
		
	} );
</script>

<div id="page">
	<div id="container">
		<h1>Report Editor</h1>
		
		<c:choose>
			
			<c:when test="${report.id == null}">
				<b class="boxHeader">Create New Report</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.report.definition.ReportDefinition|size=380|mode=edit|dialog=false|cancelUrl=manageReports.form|successUrl=reportEditor.form?type=org.openmrs.module.reporting.report.definition.ReportDefinition&uuid=uuid" />
				</div>
			</c:when>
			
			<c:otherwise>
		
				<table style="font-size:small; width:100%;">
					<tr>
						<td valign="top" style="width:35%;">
							<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${report['class'].name}|uuid=${report.uuid}|size=380|label=Basic Details" />
							<br/>
							<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${report['class'].name}|uuid=${report.uuid}|label=Parameters|parentUrl=${pageUrl}" />
							<br/>
							<openmrs:portlet url="mappedProperty" id="baseCohortDefinition" moduleId="reporting" 
											 parameters="type=${report['class'].name}|uuid=${report.uuid}|property=baseCohortDefinition|label=Base Cohort Definition|nullValueLabel=All Patients" />
							<br/>
							<b class="boxHeader">Output Designs</b>
							<div class="box">
								<c:if test="${!empty designs}">
									<table width="100%" style="margin-bottom:5px;">
										<tr>
											<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.name" /></th>
											<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.type" /></th>
											<th style="border-bottom:1px solid black;">[X]</th>
										</tr>
										<c:forEach items="${designs}" var="design" varStatus="designStatus">
											<tr>
												<td nowrap><a href="#edit" id="${design.uuid}DesignEditLink"><c:out value='${design.name}' /></a></td>
												<td width="100%"><c:out value='${design.rendererType.simpleName}' /></td>
												<td nowrap align="center"><a href="#" id="${design.uuid}DesignRemoveLink">[X]</a></td>
											</tr>
										</c:forEach>
									</table>
								</c:if>
								<wgt:widget id="rendererType" name="rendererType" type="org.openmrs.module.reporting.report.renderer.ReportDesignRenderer"/>
								<input id="designAddLink" href="" type="button" value="<spring:message code="reporting.manage.createNew"/>"/>
							</div>
							<br/>
							<button id="previewButton">
								<img src="<c:url value="/images/play.gif"/>" border="0"/>
								<spring:message code="reporting.preview"/>
							</button>
						</td>
						<td valign="top" style="width:65%; padding-left:20px;">
							<b class="boxHeader"><spring:message code="reporting.datasetDefinitions" /></b>
							
							<c:forEach items="${report.dataSetDefinitions}" var="dsd" varStatus="dsdStatus">
								<div style="display:none; width:100%" id="dsdView${dsdStatus.index}">
									<table style="font-size:smaller; color:grey; border:1px solid black; width:100%;">
										<tr>
											<th colspan="7">
												${dsd.value.parameterizable.name}
												(<a href="../definition/editDefinition.form?type=${dsd.value.parameterizable['class'].name}&uuid=${dsd.value.parameterizable.uuid}">Edit this Definition</a>)
											</th>
										</tr>
										<c:if test="${rpt:instanceOf(dsd.value.parameterizable, 'org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition')}">
											<tr>
												<c:if test="${!empty dsd.value.parameterizable.rows}">
													<th>&nbsp;</th>
												</c:if>
												<c:forEach items="${dsd.value.parameterizable.columns}" var="columnEntry" varStatus="colStatus">
													<th style="border-bottom:1px solid black;">${columnEntry.key}</th>
												</c:forEach>
											</tr>
											<c:choose>
												<c:when test="${empty dsd.value.parameterizable.rows}">
													<c:forEach items="${dsd.value.parameterizable.columns}" var="columnEntry" varStatus="colStatus">
														<td style="border-bottom:1px solid black;">...</td>
													</c:forEach>
												</c:when>
												<c:otherwise>
													<c:forEach items="${dsd.value.parameterizable.rows}" var="rowEntry" varStatus="rowStatus">
														<tr>
															<th>${rowEntry.key}</th>
															<c:forEach items="${dsd.value.parameterizable.columns}" var="columnEntry" varStatus="colStatus">
																<td style="border-bottom:1px solid black;">...</td>
															</c:forEach>
														</tr>
													</c:forEach>
												</c:otherwise>
											</c:choose>

										</c:if>
									</table>
								</div>
							</c:forEach>
							
							<div class="box" style="vertical-align:top;">
								<c:forEach items="${report.dataSetDefinitions}" var="dsd" varStatus="dsdStatus">
									<openmrs:portlet url="mappedProperty" id="dsd${dsdStatus.index}" moduleId="reporting" 
													parameters="type=${report['class'].name}|uuid=${report.uuid}|property=dataSetDefinitions|currentKey=${dsd.key}|label=${dsd.key}|parentUrl=${pageUrl}|viewId=dsdView${dsdStatus.index}|headerClass=dsdHeader" />
									<br/>
								</c:forEach>
								<openmrs:portlet url="mappedProperty" id="newDsd" moduleId="reporting" 
												 parameters="type=${report['class'].name}|uuid=${report.uuid}|property=dataSetDefinitions|mode=add|label=New Dataset Definition" />
							</div>
							
						</td>
					</tr>
				</table>
				
			</c:otherwise>
			
		</c:choose>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>
