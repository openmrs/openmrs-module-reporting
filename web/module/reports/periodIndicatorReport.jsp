<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.form" />

		<c:choose>
			<c:when test="${report.uuid == null}">

				<b class="boxHeader">Create Period Indicator Report</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition|size=380|mode=edit|dialog=false|cancelUrl=manageReports.form|successUrl=periodIndicatorReport.form?uuid=uuid" />
				</div>

			</c:when>		
			<c:otherwise>
			
				<script type="text/javascript">
					$(document).ready(function() {
						$('#addColumnDialog').dialog({
							autoOpen: false,
							draggable: false,
							resizable: false,
							show: null,
							width: '90%',
							modal: true,
							title: 'Add another column'											
						});
						$('#cancelDialogButton').click(function() { $('#addColumnDialog').dialog('close') });
						$('.addColumnButton').click(function() { $('#addColumnDialog').dialog('open') });
						$('#column-table').dataTable({
							"bPaginate": false,
							"bLengthChange": false,
							"bFilter": false,
							"bSort": false,
							"bInfo": false,
							"bAutoWidth": false
						} );
						$('#dimensions-table').dataTable({
							"bPaginate": false,
							"bLengthChange": false,
							"bFilter": false,
							"bSort": false,
							"bInfo": false,
							"bAutoWidth": false
						} );
						$('#previewButton').click(function(event) { 
							showReportingDialog({ 
								title: 'Preview Report', 
								url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${report.uuid}&type=${report.class.name}'
							});
						}).height(32);
						$('#closeButton').click(function(event) {
							window.location = 'manageReports.form';
						}).height(32);
					} ); 
				</script>
			
				<div id="addColumnDialog" style="display: none">
					<form method="post" action="periodIndicatorReportAddColumn.form">
						<input type="hidden" name="uuid" value="${report.uuid}"/>
						<table>
							<tr>
								<td>Indicator Number</td>
								<td><input size="6" maxlength="10" type="text" name="key"/></td>
							</tr>
							<tr>
								<td>Label</td>
								<td><input size="40" type="text" name="displayName"/></td>
							</tr>
							<tr>
								<td>Indicator</td>
								<td>
									<select name="indicator">
										<option value=""></option>
										<c:forEach var="ind" items="${indicators}">
											<option value="${ind.uuid}">${ind.name}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
							<c:if test="${fn:length(report.indicatorDataSetDefinition.dimensions) > 0}">
								<tr valign="top">
									<td>Dimensions</td>
									<td>
										<table>
											<c:forEach var="dim" items="${report.indicatorDataSetDefinition.dimensions}">
												<tr>
													<td align="right">
														${dim.key}:
													</td>
													<td>
														<select name="dimensionOption_${dim.key}">
															<option value="">all</option>
															<c:forEach var="dimOpt" items="${dim.value.parameterizable.cohortDefinitions}">
																<option value="${dimOpt.key}">${dimOpt.key}</option>
															</c:forEach>
															<option value="?">unclassified</option>
														</select>
													</td>
												</tr>
											</c:forEach>
										</table>
									</td>
								</tr>
							</c:if>
							<tr>
								<td colspan="2" align="center">
									<input type="submit" value="<spring:message code="general.add"/>"/>
									<input type="button" id="cancelDialogButton" value="<spring:message code="general.cancel"/>"/>
								</td>
							</tr>
						</table>
					</form>
				</div>
			
				<table>
					<tr valign="top">
						<td>

							<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${report.class.name}|uuid=${report.uuid}|size=380|label=Basic Details" />
			
							<br/>
							
							<b class="boxHeader">Dimensions</b>
							<div class="box">
								<table id="dimensions-table">
									<thead>
										<tr>
											<th>Key</th>
											<th>Display Name</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="dim" items="${report.indicatorDataSetDefinition.dimensions}">
											<tr>
												<td>${dim.key}</td>
												<td>${dim.value.parameterizable.name}</td>
												<td>
													<a href="periodIndicatorReportRemoveDimension.form?key=${dim.key}&uuid=${report.uuid}">
														<img src='<c:url value="/images/trash.gif"/>' border="0"/>
													</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
									<tfoot>
										<tr>
											<td colspan="3">
												<openmrs:portlet url="mappedProperty" id="newDim" moduleId="reporting" 
															 parameters="type=${report.indicatorDataSetDefinition.class.name}|uuid=${report.indicatorDataSetDefinition.uuid}|property=dimensions|mode=add|label=New Dimension" />
											</td>
										</tr>
									</tfoot>
								</table>
							</div>
					
							<br/>
							<openmrs:portlet url="mappedProperty" id="baseCohortDefinition" moduleId="reporting" 
											 parameters="type=${report.class.name}|uuid=${report.uuid}|property=baseCohortDefinition|label=Filter|nullValueLabel=All Patients" />
							
							<br/>
							<button id="previewButton">
								<img src="<c:url value="/images/play.gif"/>" border="0"/>
								Preview
							</button>
							<button id="closeButton">
								<spring:message code="general.close"/>
							</button>
						</td>
						<td>

							<b class="boxHeader" style="text-align: right">
								<span style="float:left">
									Indicators
								</span>
								<a href="javascript:void(0)" class="addColumnButton"><spring:message code="general.add"/></a>
							</b>
								<table id="column-table">
									<thead>
										<tr>
											<th>Ind. #</th>
											<th>Label</th>
											<th>Indicator</th>
											<th>Dimensions</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="col" items="${report.indicatorDataSetDefinition.columns}">
											<tr>
												<td>${col.name}</td>
												<td>${col.label}</td>
												<td>${col.indicator.parameterizable.name}</td>
												<td>
													<c:forEach var="dimOpt" items="${col.dimensionOptions}">
														${dimOpt.key}=${dimOpt.value} <br/>
													</c:forEach>
												</td>
												<td>
													<a href="periodIndicatorReportRemoveColumn.form?key=${col.name}&uuid=${report.uuid}">
														<img src='<c:url value="/images/trash.gif"/>' border="0"/>
													</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
						</td>
					</tr>
				</table>
			
			</c:otherwise>
		</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>