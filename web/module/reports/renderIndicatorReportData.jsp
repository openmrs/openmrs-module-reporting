<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../run/localHeader.jsp"%>

<style type="text/css">
	#wrapper input, #wrapper select, #wrapper textarea, #wrapper label, #wrapper button, #wrapper span, #wrapper div { font-size: large; } 
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	fieldset { padding: 5px; margin:5px; }
	fieldset legend { font-weight: bold; background: #E2E4FF; padding: 6px; border: 1px solid black; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	.errors { margin-left:200px; margin-top:20px; margin-bottom:20px; font-family:Verdana,Arial,sans-serif; font-size:12px; }
	#report-schema-basic-tab { margin: 50px; }
	#wrapper { margin-top: 50px; }
</style>


<div id="page">
	<div id="container">
			<div id="wrapper">
			
				<c:forEach var="dataSetMapEntry" items="${__openmrs_report_data.dataSets}">
					<table border="1">					
						<c:forEach var="dataSetRow" items="${dataSetMapEntry.value.iterator}" varStatus="varStatus">
							<c:if test="${varStatus.first}">
								<tr>
									<td colspan="3">
										<h2>${dataSetMapEntry.value.definition.name}<h2>
									</td>
								</tr>
								<tr>
									<th>Key</th>
									<th>Indicator</th>
									<th>Value</th>
								</tr>
							</c:if>
							<tr>
								<c:forEach var="dataSetCol" items="${dataSetRow.columnValues}">
									<c:url var="url" value="/module/reporting/dashboard/manageCohortDashboard.form?cohort=none&indicator=${dataSetCol.key.indicator.uuid}"/>
									<td>
										${dataSetCol.key.columnKey}
									</td>
									<td>
										${dataSetCol.key.displayName}
									</td>
									<td>
										<a href="${url}">${dataSetCol.value}</a>
									</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</table>
				</c:forEach>
			</div>			
	</div>
</div>

