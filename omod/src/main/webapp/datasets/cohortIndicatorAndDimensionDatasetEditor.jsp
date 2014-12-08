<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form" />			

<c:set value="/module/reporting/datasets/cohortIndicatorAndDimensionDatasetEditor.form?uuid=uuid" var="pageUrl"/>
<c:url value="/module/reporting/datasets/cohortIndicatorAndDimensionDatasetEditor.form" var="pageUrlWithUuid">
	<c:param name="uuid" value="${dsd.uuid}" />
</c:url>
<c:set value="${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" var="manageUrl"/>
<br/>
<c:choose>
	<c:when test="${dsd.id == null}">

		<b class="boxHeader">Create Cohort Indicator and Dimension Data Set</b>
		<div class="box">
			<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${dsd['class'].name}|size=380|mode=edit|dialog=false|cancelUrl=${manageUrl}|successUrl=${pageUrl}" />
		</div>

	</c:when>		
	<c:otherwise>		
		<script type="text/javascript">
			$(document).ready(function() {
				$('#dimensions-table').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false
				} );
				$('#indicators-table').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false
				} );
				$('#previewButton').click(function(event) { 
					showReportingDialog({ 
						title: 'Preview Data Set', 
						url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${dsd.uuid}&type=${dsd['class'].name}'
					});
				}).height(32);
				$('#closeButton').click(function(event) {
					window.location = '${manageUrl}';
				}).height(32);
				
				<c:forEach var="dim" varStatus="dimStatus" items="${dsd.dimensions}">
					$("#${model.portletUUID}EditDimLink${dimStatus.index}").click(function(event){ 
						showReportingDialog({
							title: 'Dimension: ${dim.key}',
							url: '<c:url value="/module/reporting/viewPortlet.htm?id=mappedPropertyPortlet&url=mappedProperty&parameters=type=${dsd['class'].name}|uuid=${dsd.uuid}|property=dimensions|currentKey=${dim.key}|mode=edit"/>',
							successCallback: function() { window.location.reload(true); }
						});
					});
					
				</c:forEach>
				
				<c:forEach var="ind" varStatus="indStatus" items="${dsd.specifications}">
					$("#editIndicatorLink${indStatus.index}").click(function(event){ 
						showReportingDialog({
							title: 'Indicator: ${ind.indicatorNumber} - ${ind.label}',
							url: '<c:url value="/module/reporting/viewPortlet.htm?id=mappedIndicatorPortlet&url=cohortIndicatorAndDimensionSpecification&parameters=dsdUuid=${dsd.uuid}|index=${indStatus.index}|mode=edit"/>',
							successCallback: function() { window.location.reload(true); }
						});
					});
					$("#removeIndicatorLink${indStatus.index}").click(function(event){ 			
						if (confirm('Please confirm you wish to remove ${ind.indicatorNumber} - ${ind.label}')) {
							document.location.href='<c:url value="cohortIndicatorAndDimensionRemoveIndicator.form?index=${indStatus.index}&dsdUuid=${dsd.uuid}"/>';
						}
					});
					$("#viewDimensionColumns${indStatus.index}").click(function(event){ 
						showReportingDialog({
							title: 'Indicator and Dimension Details: ${ind.indicatorNumber} - ${ind.label}',
							url: '<c:url value="/module/reporting/viewPortlet.htm?id=cohortIndicatorAndDimensionPortlet&url=cohortIndicatorAndDimensionSpecification&parameters=dsdUuid=${dsd.uuid}|index=${indStatus.index}|mode=details"/>',
							successCallback: function() { window.location.reload(true); }
						});
					});
				</c:forEach>
				
				$('#addIndicatorLink').click(function(event){
					showReportingDialog({
						title: 'Add Indicator and Dimensions',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=addIndicatorLink&url=cohortIndicatorAndDimensionSpecification&parameters=dsdUuid=${dsd.uuid}|mode=edit"/>',
						successCallback: function() { window.location.reload(true); }
					});
				});
			} ); 
		</script>
		
		<table>
			<tr valign="top">
				<td width="30%">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|size=380|label=Basic Details" />
					<br/>
					<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|label=Parameters|parentUrl=${pageUrl}" />
					<br/>
					<b class="boxHeader"><spring:message code="reporting.datasets.dimensions"/></b>
					<div class="box">
						<table id="dimensions-table" width="100%">
							<thead>
								<tr>
									<th><spring:message code="reporting.datasets.key"/></th>
									<th><spring:message code="reporting.datasets.dimension"/></th>
									<th>&nbsp;</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="dim" varStatus="dimStatus" items="${dsd.dimensions}">
									<tr>
										<td nowrap><spring:message code="reporting.datasets.dim.key"/></td>
										<td width="100%"><spring:message code="reporting.datasets.dim.value.parameterizable.name"/></td>
										<td nowrap>
											&nbsp;
											<a href="#" id="${model.portletUUID}EditDimLink${dimStatus.index}">
												<img src='<c:url value="/images/edit.gif"/>' border="0"/>
											</a>
											&nbsp;
											<a href="cohortIndicatorAndDimensionRemoveDimension.form?key=${dim.key}&uuid=${dsd.uuid}">
												<img src='<c:url value="/images/trash.gif"/>' border="0" onclick="return confirm('<spring:message code="reporting.Report.periodIndicatorReport.cascadedeletewarning" />')"/>
											</a>
										</td>
									</tr>
								</c:forEach>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="3">
										<openmrs:portlet url="mappedProperty" id="newDim" moduleId="reporting" parameters="type=${dsd['class'].name}|uuid=${dsd.uuid}|property=dimensions|mode=add|label=Add Dimension" />
									</td>
								</tr>
							</tfoot>
						</table>
					</div>
					
					<br/>
					<button id="previewButton">
						<img src="<c:url value="/images/play.gif"/>" border="0"/>
						<spring:message code="reporting.datasets.preview"/>
					</button>
					<button id="closeButton">
						<spring:message code="general.close"/>
					</button>
				</td>
				<td>
					<b class="boxHeader" style="font-weight:bold; text-align:right;">
						<span style="float:left;"><spring:message code="reporting.datasets.indicators"/></span><br/>
					</b>			
					<div class="box">
						<div align="center" style="padding:10px;">
							<c:if test="${empty dsd.specifications}">
								<span><spring:message code="reporting.indicators.empty"/></span>
							</c:if>
							<c:if test="${!empty dsd.specifications}">
								<table id="indicators-table">
									<thead>
										<tr>
											<th style="text-align:left; border-bottom:1px solid black; white-space:nowrap;"><spring:message code="reporting.datasets.indicatorNum"/></th>
											<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.datasets.label"/></th>
											<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.datasets.indicator"/></th>
											<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.datasets.dimensions"/></th>
											<th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.datasets.actions"/></th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${dsd.specifications}" var="col" varStatus="colStatus">
											<tr>
												<td nowrap>${col.indicatorNumber}</td>
												<td width="100%">${col.label}</td>
												<td nowrap style="padding-left:5px; padding-right:5px;">${col.indicator.parameterizable.name}</td>
												<td align="center">
													<c:choose>
														<c:when test="${empty col.dimensionOptions}"><spring:message code="general.none"/></c:when>
														<c:otherwise>
															<a href="#" id="viewDimensionColumns${colStatus.index}">
																${fn:length(col.dimensionOptions)} included
															</a>
														</c:otherwise>
													</c:choose>
												</td>
												<td nowrap align="center">
													&nbsp;
													<a href="#" id="editIndicatorLink${colStatus.index}">
														<img src='<c:url value="/images/edit.gif"/>' border="0"/>
													</a>
													&nbsp;
													<a href="#" id="removeIndicatorLink${colStatus.index}">
														<img src='<c:url value="/images/trash.gif"/>' border="0"/>
													</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:if>
						</div>
						<a style="font-weight:bold;" href="#" id="addIndicatorLink">[+] Add Indicator</a>
					</div>
				</td>
			</tr>
		</table>
	
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>