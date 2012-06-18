<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../../localHeader.jsp"%>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>

<spring:bind path="indicatorForm.*">
  <c:forEach var="error" items="${status.errorMessages}">
    <B><FONT color=RED>
      <BR><c:out value="${error}"/>
    </FONT></B>
  </c:forEach>
</spring:bind>


<div id="page">
	<div id="container">
		<h1>Create a new cohort indicator </h1>
		<form action="<c:url value="/module/reporting/indicators/indicatorWizard.form"/>" method="post">			
			<fieldset>
			<legend>Step 1</legend>			
				<div>
					<ul>		
						<li>
							
							<div>
								<label class="desc">Choose an indicator type</label>
								<spring:bind path="indicatorForm.indicatorType">
									<input type="radio" checked name="${status.expression}" value="CohortDefinition"/> I want to select a pre-defined cohort definition<br/>
									<input type="radio" disabled name="${status.expression}" value="LogicQuery"/> I want to write a custom logic query									
								</spring:bind>
							</div>
						</li>					
						<li>							
							<input type="submit" name="_target1" value="Next">
						</li>
					</ul>
				</div>
			</fieldset>			
		</form>
	</div>
</div>		
		

<%@ include file="/WEB-INF/template/footer.jsp"%>
