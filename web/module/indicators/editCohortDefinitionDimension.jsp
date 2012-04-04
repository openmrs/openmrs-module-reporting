<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Dimension Definitions" otherwise="/login.htm" redirect="/module/reporting/indicators/manageDimensions.form" />

<c:url value="/module/reporting/indicators/editCohortDefinitionDimension.form" var="pageUrl">
	<c:param name="uuid" value="${dimension.uuid}" />
</c:url>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/indicators/editCohortDefinitionDimension.form"/>';
		});

		$("#previewButton").click(function(event){ 
			showReportingDialog({ 
				title: 'Preview <rpt:displayLabel type="${dimension['class'].name}"/>', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${dimension.uuid}&type=${dimension['class'].name}',
				successCallback: function() { 
					window.location = window.location; //.reload(true);
				} 
			});
		});
		
		<c:forEach var="cd" varStatus="cdStatus" items="${dimension.cohortDefinitions}">
			$("#${model.portletUUID}EditDimLink${cdStatus.index}").click(function(event){ 
				showReportingDialog({
					title: 'Dimension Option: ${cd.key}',
					url: '<c:url value="/module/reporting/viewPortlet.htm?id=mappedPropertyPortlet&url=mappedProperty&parameters=type=${dimension['class'].name}|uuid=${dimension.uuid}|property=cohortDefinitions|currentKey=${cd.key}|mode=edit"/>',
					successCallback: function() { window.location.reload(true); }
				});
			});
		</c:forEach>
		
		$('#options-table').dataTable({
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		} );
		
	} );
</script>

<div id="page">
	<div id="container">
		<h1>Dimension Editor</h1>
		
		<c:choose>
			
			<c:when test="${dimension.id == null}">
				<b class="boxHeader">Create New Dimension</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension|size=380|mode=edit|dialog=false|cancelUrl=manageDimensions.form|successUrl=/module/reporting/indicators/editCohortDefinitionDimension.form?uuid=uuid" />
				</div>
			</c:when>
			
			<c:otherwise>
		
				<table style="font-size:small;">
					<tr>
						<td valign="top" nowrap>
							<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${dimension['class'].name}|uuid=${dimension.uuid}|size=380|label=Basic Details" />
							<br/>
							<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${dimension['class'].name}|uuid=${dimension.uuid}|label=Parameters|parentUrl=${pageUrl}" />
							<br/>
							<input id="previewButton" name="preview" type="button" value="Preview"/>
						</td>
						<td valign="top" width="100%">
							<b class="boxHeader">Options</b>
							<div class="box">
								<table id="options-table">
									<thead>
										<tr>
											<th>Key</th>
											<th>Cohort Definition</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="cd" varStatus="cdStatus" items="${dimension.cohortDefinitions}">
											<tr>
												<td nowrap>${cd.key}</td>
												<td>${cd.value.parameterizable.name}</td>
												<td nowrap>
													&nbsp;
													<a href="#" id="${model.portletUUID}EditDimLink${cdStatus.index}">
														<img src='<c:url value="/images/edit.gif"/>' border="0"/>
													</a>
													&nbsp;
													<a href="editCohortDefinitionDimensionRemoveOption.form?key=${cd.key}&uuid=${dimension.uuid}">
														<img src='<c:url value="/images/trash.gif"/>' border="0"/>
													</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
									<tfoot>
										<tr>
											<td colspan="3">
												<openmrs:portlet url="mappedProperty" id="newCd" moduleId="reporting" 
															 parameters="type=${dimension['class'].name}|uuid=${dimension.uuid}|property=cohortDefinitions|mode=add|label=New Dimension Option" />
											</td>
										</tr>
									</tfoot>
								</table>
							</div>
						</td>
					</tr>
				</table>
				
			</c:otherwise>
			
		</c:choose>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>