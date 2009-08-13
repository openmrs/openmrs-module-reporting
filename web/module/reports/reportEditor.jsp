<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/reports/reportManager.list" />
<%@ include file="../localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
	
		$('#report-tabs').tabs();
	
		$('#report-dataset-table').dataTable( {
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		} );
	
		$('#report-parameter-table').dataTable( {
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		} );

		// Redirect to listing page
		$('#cancel-button').click(function(event){
			window.location.href='<c:url value="/module/reporting/report/reportManager.list"/>';
		});
	} );
</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>

<div id="page">
	<div id="container">
		<h1>Report Editor</h1>
		
		<div id="report-basic-tab">
		
			<form method="post" action="saveReportDefinition.form">
				<input type="hidden" name="uuid" value="${report.uuid}"/>
				<input type="hidden" name="type" value="${report.class.name}"/>
				<ul>				

					<li>
						<label class="desc" for="name">Name</label>			
						<div>
							<input 	type="text" class="field text medium" id="name"  tabindex="2"
									name="name" value="${report.name}" size="50"/>
						</div>
					</li>
					<li>
						<label class="desc" for="description">Description</label>			
						<div>
							<textarea 	id="description" class="field text short" cols="80" tabindex="3"
										name="description">${report.description}</textarea>			
						</div>
					</li>
					<li>					
						<div align="center">				
							<input id="save-button" class="btTxt submit" type="submit" value="Save" tabindex="7" />
							<button id="cancel-button" name="cancel">Cancel</button>
						</div>					
					</li>
				</ul>				
			</form>		
		
		</div>
	</div>
</div>
	
<%@ include file="/WEB-INF/template/footer.jsp"%>