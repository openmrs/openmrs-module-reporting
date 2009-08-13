<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
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
	
		<h1>Simple Indicator Report</h1>
		<div>
			<form id="form65" name="form65" class="wufoo topLabel" autocomplete="off"
				method="post" action="${pageContext.request.contextPath}/module/reporting/renderIndicatorReport.form">		
					<input type="hidden" id="uuid" name="uuid" value="0123456789"/>									
					<input type="hidden" id="action" name="action" value="render"/>					
					
					<h2>Report Details</h2>
					<ul>		
						<li>
							<label class="desc" for="name">ID</label>
							<span>
								${reportDefinition.uuid}
							</span>
						</li>
						<li>
							<label class="desc" for="name">Name</label>
							<span>
								${reportDefinition.name}
							</span>
						</li>
						<li>
							<label class="desc" for="name">Description</label>
							<span>
								${reportDefinition.description}
							</span>
						</li>
					</ul>
					
					<h2>Report Parameters</h2>
					<ul>
						<c:forEach var="parameter" items="${reportDefinition.parameters}" varStatus="varStatus">
							<li>
									<label class="desc" for="${parameter.name}">${parameter.label}</label>
									<span>
										<input type="text" name="${parameter.name}" class="text field long" value="${parameter.defaultValue}"/>
									</span>
							</li>
						</c:forEach>
					</ul>
					
					<h2>Report Renderer</h2>					
					<ul>						
						<li>
							<label class="desc" for="renderType">Choose a renderer:</label>
							<span>
								<input type="radio" name="renderType" value="XLS" checked> XLS
								<input type="radio" name="renderType" value="CSV"> CSV
							</span>
						</li>
						
						
						<li class="buttons">
							<span>
								<input id="saveForm" class="btTxt submit" type="submit" value="Render" tabindex="7" />
								<input id="saveForm" class="btTxt submit" type="submit" value="Preview" tabindex="7" />
								<input id="cancelForm" class="btTxt submit" type="submit" value="Cancel" tabindex="8"/>
							</span>
						</li>

					</ul>		
					
				</form>
				
			</div>
			
		</form>
	</div>
	
</div>

	
<%@ include file="/WEB-INF/template/footer.jsp"%>
					