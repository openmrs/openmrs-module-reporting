<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.list" />

<c:url value="/module/reporting/reports/reportEditor.form" var="pageUrl">
	<c:param name="uuid" value="${report.uuid}" />
</c:url>

<openmrs:htmlInclude file="/dwr/interface/DWRReportingService.js"/>

<script type="text/javascript" charset="utf-8">
	
	$(document).ready(function() {

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/report/reportEditor.form"/>';
		});

		<c:forEach items="${designs}" var="design" varStatus="designStatus">
			$('#${design.uuid}DesignEditLink').click(function(event){
				showReportingDialog({
					title: 'Edit Report Design',
					url: '<c:url value="/module/reporting/viewPortlet.htm?id=reportDesignPortlet&url=reportDesignForm&parameters=reportDesignUuid=${design.uuid}"/>',
					successCallback: function() { window.location.reload(true); }
				});
			});
			$('#${design.uuid}DesignRemoveLink').click(function(event){					
				if (confirm('Please confirm you wish to permanantly delete <b>${design.name}</b>')) {
					document.location.href='${pageContext.request.contextPath}/module/reporting/reports/deleteReportDesign.form?uuid=${design.uuid}&returnUrl=${pageUrl}';
				}
			});
		</c:forEach>
		$('#designAddLink').click(function(event){
			showReportingDialog({
				title: 'Add New Report Design',
				url: '<c:url value="/module/reporting/viewPortlet.htm?id=reportDesignPortlet&url=reportDesignForm&parameters=reportDefinitionUuid=${report.uuid}"/>',
				successCallback: function() { window.location.reload(true); }
			});
		});
		
		$('#previewButton').click(function(event) { 
			showReportingDialog({ 
				title: 'Preview Report', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${report.uuid}&type=${report['class'].name}'
			});
		}).height(32);
		
		$('#addTagLink').click(function(event) {
			$('#addTagDetails').show();
			
		});
		
		$('#addTag').click(function(event) {
			if($.trim($('#newTagEle').val()) == '')
				return;
			
			tag = $('#newTagEle').val();	
			DWRReportingService.addTag('${report.uuid}', tag, '${report.class.name}', function(success){
				if(success == true){
					row = document.getElementById('newTagRow');
					newrow = row.cloneNode(true);
					newrow.style.display = "";
					row.parentNode.insertBefore(newrow, row);
					tds = newrow.getElementsByTagName("td");
					$(tds[0]).html(tag);
					imgs = newrow.getElementsByTagName("img");
					$(imgs[0]).click(function(event) {
						removeTag(this.parentNode.parentNode, tag);
					});
					
					$('#newTagEle').val('');
				}else{
					alert('<spring:message code="reporting.Report.addTag.error" />');
				}
			});
		});
		
	} );
		
	function removeTag(node, tag){
		DWRReportingService.removeTag('${report.uuid}', tag, '${report.class.name}', function(success){
			if(success == true){
				node.parentNode.removeChild(node);
			}else{
				alert('<spring:message code="reporting.Report.removeTag.error" />');
			}
		});
	}
	
	function doneAddingTags(){
		$('#newTagEle').val(''); 
		$('#addTagDetails').hide();
	}
</script>

<style>
img.removeImage{
	cursor: pointer;
}
</style>

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
		
				<table style="font-size:small;">
					<tr>
						<td valign="top" nowrap>
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
											<th style="text-align:left; border-bottom:1px solid black;">Name</th>
											<th style="text-align:left; border-bottom:1px solid black;">Type</th>
											<th style="border-bottom:1px solid black;">[X]</th>
										</tr>
										<c:forEach items="${designs}" var="design" varStatus="designStatus">
											<tr>
												<td nowrap><a href="#" id="${design.uuid}DesignEditLink">${design.name}</a></td>
												<td width="100%">${design.rendererType.simpleName}</td>
												<td nowrap align="center"><a href="#" id="${design.uuid}DesignRemoveLink">[X]</a></td>
											</tr>
										</c:forEach>
									</table>
								</c:if>
								<a style="font-weight:bold;" href="#" id="designAddLink">[+] Add</a>
							</div>
							<!-- Can't tag a definition on creation since its uuid isn't yet persisted -->
							<c:if test="${report.id != null}">
							<br/>
							<b class="boxHeader" style="font-weight:bold; text-align:right;">
								<span style="float:left;"><spring:message code="reporting.Report.tags" /></span>
								<a style="font-weight:bold;"  href="javascript:void(0)" id="addTagLink">[+] <spring:message code="general.add" /></a>
							</b>
							<div class="box">
								<span id="addTagDetails" style="display: none">
									<input type="text" id="newTagEle" size="10" />
									<input id="addTag" type="button" class="smallButtons" value="+" />
									<input type="button" class="smallButtons" value="<spring:message code="general.close" />" 
										onclick="doneAddingTags()" />
								</span>
								<table id="tagsTable" width="100%">
									<c:forEach items="${tags}" var="tag">
									<tr>
										<td><div style="width:50% !important;">${tag}</td>
										<td>
												<img class="removeImage" src="<c:url value='/images/trash.gif'/>" border="0" 
												 onclick="removeTag(this.parentNode.parentNode, '${tag}')" />
										</td>
									</tr>
									</c:forEach>
									<tr id="newTagRow" style="display: none">
										<td></td>
										<td>
											<img class="removeImage" src="<c:url value='/images/trash.gif'/>" border="0" />
										</td>
									</tr>				
								</table>
							</div>
							</c:if>
							<br/>
							<button id="previewButton">
								<img src="<c:url value="/images/play.gif"/>" border="0"/>
								<spring:message code="reporting.preview"/>
							</button>
						</td>
						<td valign="top" width="100%">
							<b class="boxHeader">Dataset Definitions</b>
							
							<c:forEach items="${report.dataSetDefinitions}" var="dsd" varStatus="dsdStatus">
								<div style="display:none; width:100%" id="dsdView${dsdStatus.index}">
									<table style="font-size:smaller; color:grey; border:1px solid black;">
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
															</c:forEach><img border="0" src="/openmrs/images/trash.gif">
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
									<openmrs:portlet url="mappedProperty" id="dsd${dsd.key}" moduleId="reporting" 
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