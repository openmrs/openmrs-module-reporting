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
		<table style="font-size:small;">
			<tr>
				<td valign="top">
					<rptTag:baseParameterizableField id="rptBase" label="Basic Details" type="${report.class.name}" object="${report}" width="380"/>
					<br/>
					<openmrs:portlet url="mappedProperty" id="baseCohortDefinition" moduleId="reporting" parameters="type=${report.class.name}|uuid=${report.uuid}|property=baseCohortDefinition|size=380|label=Base Cohort Definition" />
				</td>
				<td valign="top" width="100%">
					<b class="boxHeader">Dataset Definitions</b>
					<div class="box" style="vertical-align:top;">
						<c:forEach items="${report.dataSetDefinitions}" var="dsd" varStatus="dsdStatus">
							<openmrs:portlet url="mappedProperty" id="dataSetDefinition${dsdStatus.index}" moduleId="reporting" 
											parameters="type=${report.class.name}|uuid=${report.uuid}|property=dataSetDefinitions|collectionKey=${dsdStatus.index}|label=${dsdStatus.count}" />
							<br/>
						</c:forEach>
						<openmrs:portlet url="mappedProperty" id="dataSetDefinition${dsdStatus.index}" moduleId="reporting" 
										 parameters="type=${report.class.name}|uuid=${report.uuid}|property=dataSetDefinitions|collectionKey=|label=Add new" />

					</div>
				</td>
			</tr>
		</table>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>