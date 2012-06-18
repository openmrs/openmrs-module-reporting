<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ include file="../../localHeader.jsp"%>

<!-- Form -->
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" rel="stylesheet"/>
<!-- 
<script type="text/javascript" src="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"></script>
 -->

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {

	// ======  Autocomplete: Cohort Definition ===============================================

	function formatItem(row) {
		return row[0] + " (<strong>id: " + row[1] + "</strong>)";
	}
	function formatResult(row) {
		return row[0].replace(/(<.+?>)/gi, '');
	}

	// Set the cohort definition field to be an autocomplete field
	$("#cohortDefinitionName").autocomplete(cohortDefinitions, {
		minChars: 0,
		width: 600,
		scroll:true,
		matchContains: true,
		autoFill: false,
		formatItem: 
			function(row, i, max) {return row.name + " " + row.description;},
		formatMatch: 
			function(row, i, max) {return row.name + " " + row.description;},
		formatResult: 
			function(row) {return row.name;}
	});

	// Hide the field if it's populated
	if ($("#cohortDefinitionName").val() != '') { 
		$("#cohortDefinitionName").hide();
	}
	
	// Highlight the field whenever the user enters it
	$("#cohortDefinitionName").bind('focus', function() {
		this.select();
	}); 
	
	// Show the field and hide the name 
	$("#cohortDefinitionSpan").click(function() {
		//this.hide();
		$("#cohortDefinitionSpan").hide();
		$("#cohortDefinitionName").show();
		$("#cohortDefinitionName").focus();
				
	}); 

	// Set the UUID after a cohort definition has been selected
	$("#cohortDefinitionName").result(function(event, data, formatted) {
		$("#cohortDefinitionUuid").val(data.uuid);
		$("#cohortDefinitionSpan").html(data.name + " " + data.description);
		$("#cohortDefinitionSpan").show();
		$("#cohortDefinitionName").hide();
		
	});
	
});


</script>

<script type="text/javascript">

var cohortDefinitions = [
		<c:forEach var="cohortDefinition" items="${cohortDefinitions}" varStatus="varStatus">
			{ 	
				id: ${cohortDefinition.id}, 
				uuid: "${cohortDefinition.uuid}", 
				name: "${cohortDefinition.name}", 
				description: "(<i>${cohortDefinition['class'].simpleName}</i>)"
			}
			<c:if test="${!varStatus.last}">,</c:if>
		</c:forEach>	            	
	];
</script>


<spring:bind path="indicatorForm.*">
  <c:forEach var="error" items="${status.errorMessages}">
    <B><FONT color=RED>
      <BR><c:out value="${error}"/>
    </FONT></B>
  </c:forEach>
</spring:bind>


<div id="page">
	<div id="container">
		<h1>Create a new cohort indicator</h1>
		<form action="<c:url value="/module/reporting/indicators/indicatorWizard.form"/>" method="post">			
			<fieldset>
			<legend>Step 2</legend>			
				<div>
					<ul>		
						<li>
							<div>
								<label class="desc">Give your indicator a name</label> 
								<spring:bind path="indicatorForm.cohortIndicator.name">
									<input type="text" name="${status.expression}" value="${status.value}"/>
								</spring:bind>
							</div>
						</li>
						<li>
							<div>
								<label class="desc">Choose a cohort definition:</label> 
								<spring:bind path="indicatorForm.cohortDefinitionUuid">
									<select name="<c:out value="${status.expression}"/>">
										<c:forEach var="cohortDefinition" items="${cohortDefinitions}">
											<c:set var="isSelected">
												<c:if test="${cohortDefinition.uuid == status.value}">selected</c:if> 
											</c:set>
											<option value="${cohortDefinition.uuid}" ${isSelected}>${cohortDefinition.name}</option>
										</c:forEach>
									</select>
								</spring:bind>
							</div>
						</li>					
						<li>							
							<input type="submit" name="_target0" value="Back">
							<input type="submit" name="_target2" value="Next" >
						</li>
					</ul>
				</div>
			</fieldset>			
		</form>
	</div>
</div>		
		

<%@ include file="/WEB-INF/template/footer.jsp"%>