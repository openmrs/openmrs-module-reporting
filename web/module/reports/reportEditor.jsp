<%@ include file="/WEB-INF/view/module/reporting/localHeader.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportManager.list" />

<script type="text/javascript" charset="utf-8">

	$(document).ready(function() {

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/report/reportEditor.form"/>';
		});
		
	} );
</script>

<div id="page">
	<div id="container">
		<h1>Report Editor</h1>	

		<rptTag:baseParameterizableField id="rptBase" label="Basic Details" type="${report.class.name}" object="${report}" width="375"/>
		<br/>
		<rptTag:mappedField id="baseCohortDefNew" label="Base Cohort Definition" parentType="${report.class.name}" 
							parentObj="${report}" mappedProperty="baseCohortDefinition" 
							defaultValue="${report.baseCohortDefinition}" nullValueLabel="All Patients" width="375"/>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>