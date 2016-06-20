<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<style type="text/css">
    .alt { background-color: #EEE; }
    .hover { background-color: #B0BED9; }
    .althover { background-color: #B0BED9; }
</style>

<script type="text/javascript">
$j(document).ready(function(){
	$j('.dataset tr:even').addClass('alt');
	$j('.dataset tr:even').hover(
			function(){$j(this).addClass('hover')},
			function(){$j(this).removeClass('hover')}
	);	
	$j('.dataset tr:odd').hover(
			function(){$j(this).addClass('althover')},
			function(){$j(this).removeClass('althover')}
	);
	$j('#tabs').tabs();
	$j('.ui-tabs-panel').css('padding','0').css('padding-top','.5em').css('overflow', 'auto');
});
</script>


<%--
  This page assumes a ReportData object in the session as the attribute '__openmrs_report_data'
--%>

<style type="text/css">
	#wrapper input, #wrapper select, #wrapper textarea, #wrapper label, #wrapper button, #wrapper span, #wrapper div { font-size: large; }
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	fieldset { padding: 5px; margin:5px; }
	fieldset legend { font-weight: bold; background: #E2E4FF; padding: 6px; border: 1px solid black; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>

<openmrs:portlet url="currentReportHeader" moduleId="reporting" parameters="showDiscardButton=true"/>

<div id="page">
	<div id="container">
		<div id="tabs">
			<ul>
				<c:forEach var="dataSetDefinitionEntry" items="${__openmrs_report_data.definition.dataSetDefinitions}" varStatus="itemStatus">
					<li><a href="#tabs-${itemStatus.index}">${dataSetDefinitionEntry.key}</a></li>
				</c:forEach>
			</ul>
			<c:forEach var="dataSetDefinitionEntry" items="${__openmrs_report_data.definition.dataSetDefinitions}" varStatus="itemStatus">
				<c:set var="dataSetKey" value="${dataSetDefinitionEntry.key}"/>
				<c:set var="dataSet" value="${__openmrs_report_data.dataSets[dataSetKey]}"/>
				<div id="tabs-${itemStatus.index}">
					<table class="dataset" cellpadding="2" style="border: 1px solid black; font-size: 0.8em; width:100%;">
						<c:choose>
							<c:when test="${rpt:instanceOf(dataSet, 'org.openmrs.module.reporting.dataset.MapDataSet')}">
								<c:choose>
									<c:when test="${rpt:instanceOf(dataSetDefinitionEntry.value.parameterizable, 'org.openmrs.module.reporting.dataset.definition.CohortCrossTabDataSetDefinition')}">
										<c:set var="rows" value="${dataSetDefinitionEntry.value.parameterizable.rows}"/>
										<c:set var="cols" value="${dataSetDefinitionEntry.value.parameterizable.columns}"/>
										<tr>
											<c:if test="${!empty rows}"><th></th></c:if>
											<c:if test="${empty cols}"><th></th></c:if>
											<c:forEach items="${cols}" var="colEntry">
												<th style="text-align:left;">${colEntry.key}</th>
											</c:forEach>
										</tr>
										<c:choose>
											<c:when test="${empty rows}">
												<tr>
													<c:forEach items="${cols}" var="colEntry">
														<c:set var="colName" value="${colEntry.key}"/>
														<c:set var="column" value="${dataSetDefinitionEntry.value.parameterizable.dataSetColumnsByKey[colName]}"/>
														<c:set var="columnValue" value="${dataSet.data.columnValues[column]}"/>
														<c:url var="url" value="/module/reporting/dashboard/viewCohortDataSet.form?savedDataSetKey=${dataSetKey}&savedColumnKey=${colName}"/>
														<td>
															<a style="text-decoration: underline" href="${url}">
																<rpt:format object="${columnValue}"/>
															</a>
														</td>
													</c:forEach>
												</tr>
											</c:when>
											<c:otherwise>
												<c:forEach items="${rows}" var="rowEntry">
													<tr>
														<th>${rowEntry.key}</th>
														<c:choose>
															<c:when test="${empty cols}">
																<c:set var="colName" value="${rowEntry.key}"/>
																<c:set var="column" value="${dataSetDefinitionEntry.value.parameterizable.dataSetColumnsByKey[colName]}"/>
																<c:set var="columnValue" value="${dataSet.data.columnValues[column]}"/>
																<c:url var="url" value="/module/reporting/dashboard/viewCohortDataSet.form?savedDataSetKey=${dataSetKey}&savedColumnKey=${colName}"/>
																<td>
																	<a style="text-decoration: underline" href="${url}">
																		<rpt:format object="${columnValue}"/>
																	</a>
																</td>
															</c:when>
															<c:otherwise>
																<c:forEach items="${cols}" var="colEntry">
																	<c:set var="colName" value="${rowEntry.key}.${colEntry.key}"/>
																	<c:set var="column" value="${dataSetDefinitionEntry.value.parameterizable.dataSetColumnsByKey[colName]}"/>
																	<c:set var="columnValue" value="${dataSet.data.columnValues[column]}"/>
																	<c:url var="url" value="/module/reporting/dashboard/viewCohortDataSet.form?savedDataSetKey=${dataSetKey}&savedColumnKey=${colName}"/>
																	<td>
																		<a style="text-decoration: underline" href="${url}">
																			<rpt:format object="${columnValue}"/>
																		</a>
																	</td>
																</c:forEach>
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:forEach items="${dataSet.metaData.columns}" var="col">
											<c:set var="columnValue" value="${dataSet.data.columnValues[col]}"/>
											<c:set var="showUrl" value="${rpt:instanceOf(columnValue, 'org.openmrs.Cohort') || rpt:instanceOf(columnValue, 'org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult')}"/>
											<c:url var="url" value="/module/reporting/dashboard/viewCohortDataSet.form?savedDataSetKey=${dataSetKey}&savedColumnKey=${col.name}"/>
											<tr>
												<td style="white-space:nowrap;">
													${col.name}&nbsp;
												</td>
												<td style="width:100%">
													<spring:message code="${col.label}" text="${col.label}"/>
												</td>
												<td style="white-space:nowrap;">
													<c:if test="${showUrl}"><a style="text-decoration: underline" href="${url}"></c:if>
													<rpt:format object="${columnValue}"/>
													<c:if test="${showUrl}"></a></c:if>
												</td>
											</tr>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<tr>
									<c:forEach items="${dataSet.metaData.columns}" var="col">
										<th style="text-align:left;"><spring:message code="${col.label}" text="${col.label}"/></th>
									</c:forEach>
								</tr>
								<c:forEach items="${dataSet.rows}" var="row">
									<tr>
										<c:forEach items="${dataSet.metaData.columns}" var="col">
											<td><rpt:format object="${row.columnValues[col]}"/></td>
										</c:forEach>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>