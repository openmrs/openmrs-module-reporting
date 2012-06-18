<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../../localHeader.jsp"%>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>

<div id="page">
	<div id="container">
		<h1>Create a new cohort indicator</h1>
		
			
		<form action="<c:url value="/module/reporting/indicators/indicatorWizard.form"/>" method="post">			
			<fieldset>
			<legend>Step 4</legend>			
				<div>
					<ul>		
						<li>
							<div>
								<center>
									<b>Please confirm the information below and then press continue...</b>
								</center>
							
								${indicatorForm}
							</div>
						</li>					
						<li>							
							<input type="submit" name="_target2" value="Back">
							<input type="submit" name="_finish" value="Next" >						
						</li>
					</ul>
				</div>
			</fieldset>			
		</form>
	</div>
</div>	


<%@ include file="/WEB-INF/template/footer.jsp"%>