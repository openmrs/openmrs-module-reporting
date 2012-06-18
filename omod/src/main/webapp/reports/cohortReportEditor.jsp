<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Definitions" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../localHeader.jsp"%>

<!--  CSS -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/page.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/table.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.dataTables/custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.ui/jquery-ui-1.7.1.custom.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/jquery.autocomplete/jquery.autocomplete.css" rel="stylesheet"/>

<!-- Autocomplete -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.js"></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/thickbox-compressed.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.bgiframe.min.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.ajaxQueue.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.autocomplete/jquery.autocomplete.js'></script>

<!-- Other -->
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.dataTables/jquery.dataTables.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery.ui/jquery-ui-1.7.1.custom.min.js"></script>


<div id="page"><!-- -->
	<div id="container"><!--  -->
	
		<h1>Cohort Report</h1>
		<div>
			<form id="form65" name="form65" class="wufoo topLabel" autocomplete="off"
				method="post" action="${pageContext.request.contextPath}/module/reporting/generateSimpleLabReport.form">		
					<input type="hidden" id="uuid" name="uuid" value="0123456789"/>									
					
					<ul>		
						<li>
							<label class="desc" for="startDate">Start Date</label>
							<span>
								<input id="startDate" name="startDate" value="01/01/2009"/>
							</span>
						</li>
						<li>
							<label class="desc" for="endDate">End Date</label>
							<span>
								<input id="endDate" name="endDate" value="01/31/2009"/>
							</span>
						</li>
						<li>
							<label class="desc" for="locationId">Location</label>
							<select name="locationId">
								<option value="0">All Locations</option>
								<option value="0">--------------------------------</option>
								<c:forEach var="location" items="${locations}">
									<option value="${location.locationId}">${location.name}</option>
								</c:forEach>
							</select>		
						</li>
						<li>
							<label class="desc" for="renderType">Renderer:</label>
							<input type="radio" name="renderType" value="XLS" checked> XLS
							<input type="radio" name="renderType" value="CSV"> CSV
						</li>
						<li class="buttons">
							<input id="saveForm" class="btTxt submit" type="submit" value="Generate" tabindex="7" />
							<input id="cancelForm" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
						</li>

					</ul>
				</form>
				
			</div>
			
		</form>
	</div>
	
</div>

	
<%@ include file="/WEB-INF/template/footer.jsp"%>
					