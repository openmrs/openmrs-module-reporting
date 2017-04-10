<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/reports/manageReports.form" />

		<c:choose>
			<c:when test="${report.id == null}">

				<b class="boxHeader"<spring:message code="reporting.>CreatePeriodIndicatorReport" /></b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition|size=380|mode=edit|dialog=false|cancelUrl=manageReports.form|successUrl=periodIndicatorReport.form?uuid=uuid" />
				</div>

			</c:when>		
			<c:otherwise>
			
				<script type="text/javascript">
					$j(document).ready(function() {
						$j('#addColumnDialog').dialog({
							autoOpen: false,
							draggable: false,
							resizable: false,
							show: null,
							width: '90%',
							modal: true,
							title: 'Save column'											
						});
						
						$j('.addColumnButton').click(function() {
							$j('#indexField').val('');
							$j('#keyField').val('');
							$j('#labelField').val('');
							$j('#indicatorField').val('');
							$j('#cohortQueryField').val('');
							<c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
								$j('#dimensionOption_${dimStatus.index}').val('');
							</c:forEach>
							$j('#addColumnDialog').dialog('open');
						});

                        var updateIndicatorLabel = function() {
                            var labelFieldValNew = $j("#indicatorField option:selected").text();
							if ($j('#createFromCohortQueryCheckbox').is(':checked')) {
								labelFieldValNew = $j("#cohortQueryField option:selected").text();
							}

                            if(labelFieldValNew != '') {
                                <c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
                                if($j('#dimensionOption_${dimStatus.index}').text() != '') {
                                    labelFieldValNew += '-' + $j('#dimensionOption_${dimStatus.index} option:selected').text();
                                }
                                </c:forEach>
                            }
                            $j("#labelField").val(labelFieldValNew);
                        }

                        $j('#indicatorField').change(updateIndicatorLabel);
						$j('#cohortQueryField').change(updateIndicatorLabel);

                        <c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
                       		$j('#dimensionOption_${dimStatus.index}').change(updateIndicatorLabel);
                        </c:forEach>

						$j('#cancelDialogButton').click(function() {
							$j('#addColumnDialog').dialog('close');
						});
						
						$j('#dimensions-table').dataTable({
							"bPaginate": false,
							"bLengthChange": false,
							"bFilter": false,
							"bSort": false,
							"bInfo": false,
							"bAutoWidth": false
						} );
						$j('#previewButton').click(function(event) {
							showReportingDialog({ 
								title: 'Preview Report', 
								url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${report.uuid}&type=${report['class'].name}'
							});
						}).height(32);
						$j('#closeButton').click(function(event) {
							window.location = 'manageReports.form';
						}).height(32);
						
						<c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
							$j("#${model.portletUUID}EditDimLink${dimStatus.index}").click(function(event){
								showReportingDialog({
									title: 'Dimension: ${dim.key}',
									url: '<c:url value="/module/reporting/viewPortlet.htm?id=mappedPropertyPortlet&url=mappedProperty&parameters.=${report.indicatorDataSetDefinition['class'].name}&parameters.uuid=${report.indicatorDataSetDefinition.uuid}&parameters.property=dimensions&parameters.currentKey=${dim.key}&parameters.mode=edit"/>',
									successCallback: function() { window.location.reload(true); }
								});
							});
						</c:forEach>
						
						<c:forEach var="col" varStatus="colStatus" items="${report.indicatorDataSetDefinition.columns}">
						
							$j('#editIndicator${colStatus.index}').click(function() {
								$j('#indexField').val('${colStatus.index}');
								$j('#keyField').val('${col.name}');
								$j('#labelField').val('${col.label}');
								$j('#indicatorField').val('${col.indicator.parameterizable.uuid}');
								<c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
									$j('#dimensionOption_${dimStatus.index}').val('${col.dimensionOptions[dim.key]}');
								</c:forEach>
								$j('#addColumnDialog').dialog('open');
							});
						
							$j("#deleteIndicator${colStatus.index}").click(function(event){
								if (confirm("Are you sure you wish to remove column: ${col.name}?")) {
									document.location.href="periodIndicatorReportRemoveColumn.form?key=${col.name}&uuid=${report.uuid}";
								}
							});
							
						</c:forEach>
						
						$j('#column-table').dataTable({
							"bPaginate": true,
							"iDisplayLength": 15,
							"bLengthChange": false,
							"bFilter": false,
							"bSort": false,
							"bInfo": false,
							"bAutoWidth": false
						} );

						$j('#createFromCohortQueryCheckbox').change(function() {
							if ($j(this).is(':checked')) {
								$j('#createFromIndicator').hide();
								$j('#createFromCohortQuery').show();
							} else {
								$j('#createFromIndicator').show();
								$j('#createFromCohortQuery').hide();
							}
						});

					} );
				</script>

				<div id="addColumnDialog" style="display: none">
					<form method="post" action="periodIndicatorReportSaveColumn.form">
						<input type="hidden" name="uuid" value="${report.uuid}"/>
						<input type="hidden" id="indexField" name="index" value=""/>
						<table>
							<tr>
								<td><spring:message code="reporting.indicatorNumber" /></td>
								<td><input id="keyField" size="30" maxlength="10" type="text" name="key"/></td>
							</tr>
							<tr>
								<td><spring:message code="reporting.label" /></td>
								<td><input id="labelField" size="60" type="text" name="displayName"/></td>
							</tr>
							<tr id="createFromIndicator">
								<td><spring:message code="reporting.Indicator" /></td>
								<td>
									<select id="indicatorField" name="indicator">
										<option value=""></option>
										<c:forEach var="ind" items="${indicators}">
											<option value="${ind.uuid}">${ind.name}</option>
										</c:forEach>
									</select>
									<span style="font-size:small; text-decoration:italics; padding-left:10px;">
										<spring:message code="reporting.Report.periodIndicatorReport.indicatorMessage"/>
									</span>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<input type="checkbox" id="createFromCohortQueryCheckbox" name="createFromCohortQuery" value="createFromCohortQuery">Create from cohort query
								</td>
							</tr>
							<tr id="createFromCohortQuery" style="display: none;">
								<td><spring:message code="reporting.CohortDefinition" /></td>
								<td>
									<select id="cohortQueryField" name="cohortQuery">
										<option value=""></option>
										<c:forEach var="query" items="${cohortQueries}">
											<option value="${query.uuid}">${query.name}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
							<c:if test="${fn:length(report.indicatorDataSetDefinition.dimensions) > 0}">
								<tr valign="top">
									<td><spring:message code="reporting.dimensions" /></td>
									<td>
										<table>
											<c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
												<tr>
													<td align="right">
														${dim.key}:
													</td>
													<td>
														<select id="dimensionOption_${dimStatus.index}" name="dimensionOption_${dim.key}">
															<option value=""><spring:message code="reporting.all" /></option>
															<c:forEach var="dimOpt" items="${dim.value.parameterizable.cohortDefinitions}">
																<option value="${dimOpt.key}">${dimOpt.key}</option>
															</c:forEach>
															<option value="?"><spring:message code="reporting.dimension.unclassified" /></option>
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
									<input type="submit" value="<spring:message code="general.save"/>"/>
									<input type="button" id="cancelDialogButton" value="<spring:message code="general.cancel"/>"/>
								</td>
							</tr>
						</table>
					</form>
				</div>
			
				
				<table style="width:100%;">
					<tr valign="top">
						<td style="width:30%;">
							<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${report['class'].name}|uuid=${report.uuid}|size=380|label=Basic Details" />
							<br/>
							<b class="boxHeader"><spring:message code="reporting.dimensions" /></b>
							<div class="box">
								<table id="dimensions-table" style="width:100%;">
									<thead>
										<tr>
											<th><spring:message code="reporting.key" /></th>
											<th><spring:message code="reporting.dimension" /></th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="dim" varStatus="dimStatus" items="${report.indicatorDataSetDefinition.dimensions}">
											<tr>
												<td nowrap>${dim.key}</td>
												<td width="100%">${dim.value.parameterizable.name}</td>
												<td nowrap>
													&nbsp;
													<a href="#" id="${model.portletUUID}EditDimLink${dimStatus.index}">
														<img src='<c:url value="/images/edit.gif"/>' border="0"/>
													</a>
													&nbsp;
													<a href="periodIndicatorReportRemoveDimension.form?key=${dim.key}&uuid=${report.uuid}">
														<img src='<c:url value="/images/trash.gif"/>' border="0" onclick="return confirm('<spring:message code="reporting.Report.periodIndicatorReport.cascadedeletewarning" />')"/>
													</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
									<tfoot>
										<tr>
											<td colspan="3">
												<openmrs:portlet url="mappedProperty" id="newDim" moduleId="reporting" 
															 parameters="type=${report.indicatorDataSetDefinition['class'].name}|uuid=${report.indicatorDataSetDefinition.uuid}|property=dimensions|mode=add|label=Add Dimension" />
											</td>
										</tr>
									</tfoot>
								</table>
							</div>
					
							<br/>
							<openmrs:portlet url="mappedProperty" id="baseCohortDefinition" moduleId="reporting" 
											 parameters="type=${report['class'].name}|uuid=${report.uuid}|property=baseCohortDefinition|label=Filter|nullValueLabel=All Patients" />
							
							<br/>
							<button id="previewButton">
								<img src="<c:url value="/images/play.gif"/>" border="0"/>
								<spring:message code="reporting.preview"/>
							</button>
							<button id="closeButton">
								<spring:message code="general.close"/>
							</button>
						</td>
						<td width="70%">
							<b class="boxHeader" style="text-align: right">
								<span style="float:left">
									<spring:message code="reporting.indicators" />
								</span>
								<a href="javascript:void(0)" class="addColumnButton"><spring:message code="general.add"/></a>
							</b>
							<div class="box">
								<table id="column-table" width="100%">
									<thead>
										<tr>
											<th>Ind. #</th>
											<th><spring:message code="reporting.label" /></th>
											<th><spring:message code="reporting.indicator" /></th>
											<th><spring:message code="reporting.dimensions" /></th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="col" varStatus="colStatus" items="${report.indicatorDataSetDefinition.columns}">
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
													&nbsp;
													<a href="#" id="editIndicator${colStatus.index}">
														<img src='<c:url value="/images/edit.gif"/>' border="0"/>
													</a>
													&nbsp;
													<a href="#" id="deleteIndicator${colStatus.index}">
														<img src='<c:url value="/images/trash.gif"/>' border="0"/>
													</a>
													&nbsp;
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
				</table>
			
			</c:otherwise>
		</c:choose>

<%@ include file="/WEB-INF/template/footer.jsp"%>
