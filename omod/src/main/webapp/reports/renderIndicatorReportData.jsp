<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="__openmrs_hide_report_link" value="true"/>
<%@ include file="../run/localHeader.jsp"%>

<style type="text/css">
    .alt { background-color: #EEE; }
    .hover { background-color: #DED; }
    .althover { background-color: #EFE; }        
</style>

<script type="text/javascript">
$(document).ready(function(){
	$('.dataset tr:even').addClass('alt');
	$('.dataset tr:even').hover(
			function(){$(this).addClass('hover')},
			function(){$(this).removeClass('hover')}
	);	
	$('.dataset tr:odd').hover(
			function(){$(this).addClass('althover')},
			function(){$(this).removeClass('althover')}
	);
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
	.errors { margin-left:200px; margin-top:20px; margin-bottom:20px; font-family:Verdana,Arial,sans-serif; font-size:12px; }
	#report-schema-basic-tab { margin: 50px; }
	#wrapper { margin-top: 50px; }
</style>

<openmrs:portlet url="currentReportHeader" moduleId="reporting" parameters="showDiscardButton=true"/>

<div id="page">
	<div id="container">
		<div align="center">
			<c:forEach var="dataSetMapEntry" items="${__openmrs_report_data.dataSets}">
				<table class="dataset" cellpadding="2" style="border: 1px solid black; width:100%;">
					<tr>
						<th colspan="4" align="center" style="background-color: #8FABC7">
							<span style="color:white;">${dataSetMapEntry.value.definition.name}</span>
						</th>
					</tr>			
					<rpt:forEach var="dataSetRow" items="${dataSetMapEntry.value}" varStatus="varStatus">
						<c:forEach var="dataSetCol" items="${dataSetRow.columnValues}">
							<tr>
								<c:url var="url"  value="/module/reporting/dashboard/viewCohortDataSet.form?savedDataSetKey=${dataSetMapEntry.key}&savedColumnKey=${dataSetCol.key.name}"/>
								<td align="left">
									${dataSetCol.key.name}
								</td>
								<td align="left">
									<spring:message code="${dataSetCol.key.label}"/>
								</td>
								<td align="center" class="value" width="1%" nowrap>
									<a style="text-decoration: underline" href="${url}">
										<c:choose>
											<c:when test="${rpt:instanceOf(dataSetCol.value, 'org.openmrs.module.reporting.indicator.Indicator')}">${dataSetCol.value.value}</c:when>
											<c:when test="${rpt:instanceOf(dataSetCol.value, 'org.openmrs.Cohort')}">${dataSetCol.value.size}</c:when>
											<c:otherwise>${dataSetCol.value}</c:otherwise>
										</c:choose>
									</a>
								</td>
							</tr>
						</c:forEach>
					</rpt:forEach>
					<tfoot>
						<tr>
							<c:set var="now" value="<%=new java.util.Date()%>" />
							<td height="25px" valign="bottom" colspan="4" align="right">
								<span style="font-size: small">
									<spring:message code="reporting.Report.generatedBy"/> <strong>${authenticatedUser.username}</strong> | 
									<fmt:formatDate type="date" value="${now}"/> <fmt:formatDate type="time" value="${now}"/>
								</span>
							</td>
						</tr>
					</tfoot>
				</table>
			</c:forEach>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>