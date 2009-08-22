<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../../localHeader.jsp"%>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>


<div id="page">
	<div id="container">
		<h1>Done</h1>
		
		<center>
			<b>Please confirm the information below and then press continue...</b>
		</center>
			
		<form action="<c:url value="/module/reporting/indicators/indicatorWizard.form"/>" method="post">			
			<fieldset>
			<legend>Done!</legend>			
				<div>
					<ul>		
						<li>
							<div>
								Your Cohort Indicator has been created!
							
								message = ${message}<br/>
								indicatorForm = ${indicatorForm}<br/>
								cohortIndicator = ${cohortIndicator}<br/>
							</div>
						</li>					
					</ul>
				</div>
			</fieldset>			
		</form>
	</div>
</div>	


<%@ include file="/WEB-INF/template/footer.jsp"%>