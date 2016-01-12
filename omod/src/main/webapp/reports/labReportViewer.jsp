<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<!-- 
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
 -->
 
 
<!-- JQuery Engine -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>

<!-- JQuery Data Tables -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>

<!-- JQuery UI -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui-1.6/ui.all.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui-1.6/jquery-ui-1.6.custom.min.js"></script>
<!-- 
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>
 -->
 
<!-- JQuery Autocomplete -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>



<script type="text/javascript" charset="utf-8">
$(document).ready(function() {


	// ======  Tabs: Indicator Dataset Tabs  ================================================

	$('#sample-report-tabs').tabs();
	$('#sample-report-tabs').show();

	// Redirect to the listing page
	$('#cancel-button').click(function(event){
		event.preventDefault();  // To prevent the submit		
		window.location.href='<c:url value="/module/reporting/reports/manageReports.list"/>';
	});
	

} );

</script>


<div id="page"><!-- -->
	<div id="container"><!--  -->

		<h1><spring:message code ="reporting.RwandaReports" /> </h1>
		
		
		<div id="sample-report-tabs" class="ui-tabs-hide">			
			<ul>
                <li><a href="#sample-lab-report-tab"><span><spring:message code ="reporting.RwandaLabReport" /></span></a></li>
                <li><a href="#more"><span><spring:message code ="reporting.more" /></span></a></li>
                <!-- 
                <li><a href="#sample-cohort-report-tab"><span><spring:message code ="reporting.SampleCohortReport" /></span></a></li>
                <li><a href="#sample-indicator-report-tab"><span><spring:message code ="reporting.SampleIndicatorReport" /></span></a></li>
                 -->
            </ul>
	
			<div id="sample-lab-report-tab">		
	
				<div align="left">
					
					<form method="post" action="${pageContext.request.contextPath}/module/reporting/renderLabReport.form">		
						<input type="hidden" id="uuid" name="uuid" value="0123456789"/>									
						<input type="hidden" name="action" value="render"/>
						<div>
								<label class="desc" for="renderType"><spring:message code ="reporting.DownloadAs" /></label>
								<span>
									<input type="radio" name="renderType" value="XLS" checked tabindex="1"><spring:message code ="reporting.XLS" />
									<input type="radio" name="renderType" value="CSV" tabindex="2"> <spring:message code ="reporting.CSV" />
								</span>
						</div>
						<div>
								<label class="desc" for="locationId"><spring:message code ="reporting.location" /></label>
								<span>
									<select name="locationId"  tabindex="5">
										<option value="0"><spring:message code ="reporting.AllLocations" /></option>									
										<c:forEach var="location" items="${locations}">
											<option value="${location.locationId}">${location.name}</option>
										</c:forEach>
									</select>		
								</span>
						</div>
						<div>
								<label class="desc" for="startDate"><spring:message code ="reporting.parameter.startDate" /></label>
								<span>
									<openmrs:fieldGen type="java.util.Date" formFieldName="startDate" val="" parameters=""/>
								</span>
						</div>
						<div>
								<label class="desc" for="endDate"><spring:message code ="reporting.parameter.endDate" /></label>
								<span>
									<openmrs:fieldGen type="java.util.Date" formFieldName="endDate" val="" parameters=""/>							
								</span>
						</div>
						<div class="buttons">
							<span>
								<input id="save-button" class="btTxt submit" type="submit" value="Download" tabindex="6" />
								<button id="cancel-button" name="cancel"><spring:message code ="reporting.cancel" /></button>
							</span>
						</div>
	
					</form>
				</div>
			</div>
			<div id="more">		
				<i><spring:message code ="reporting.moreReportsToCome" /></i>		
			</div>
	        <!-- 
				<div id="sample-cohort-report-tab">		
					<i><spring:message code ="reporting.CohortNotSupported" /></i>		
				</div>
				
				
				<div id="sample-indicator-report-tab">		
					<i><spring:message code ="reporting.CohortNotSupported" /></i>		
				</div>
			-->		
			
		</div>
	</div>
</div>


	
<%@ include file="/WEB-INF/template/footer.jsp"%>
					
