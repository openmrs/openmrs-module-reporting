<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		
		$("#report-design-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

		<c:forEach items="${reportDesigns}" var="design" varStatus="designStatus">
			$('#${design.uuid}DesignEditLink').click(function(event){
				document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/renderers/editReportDesign.form?type=${design.rendererType.name}&reportDesignUuid=${design.uuid}';
			});
			$('#${design.uuid}DesignRemoveLink').click(function(event){					
				if (confirm('Please confirm you wish to permanantly delete ${design.name}')) {
					document.location.href='${pageContext.request.contextPath}/module/reporting/reports/deleteReportDesign.form?uuid=${design.uuid}';
				}
			});
		</c:forEach>
		
		$( '#rendererType' ).change( function() {
			$( this ).next( 'input#designAddLink' ).attr( 'href', $( this ).val() );
		} );
		
		$('#designAddLink').click(function(event){
			document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/renderers/editReportDesign.form?type=' + $( this ).attr( 'href' );
		});
	});
</script>

<div id="page">
	<div id="container">
		<h1>Report Design Manager</h1>
		<wgt:widget id="rendererType" name="rendererType" type="org.openmrs.module.reporting.report.renderer.ReportDesignRenderer"/>
		<input id="designAddLink" href="#" type="button" value="<spring:message code="reporting.manage.createNew"/>"/>
		<br/>

		<table id="report-design-table" class="display" >
			<thead>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>Report Definition</th>
					<th>Renderer Type</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportDesigns}" var="design" varStatus="designStatus">
					<tr>
						<td width="20%" nowrap="true">
							<a id="${design.uuid}DesignEditLink" href="#">${design.name}</a>
						</td>
						<td>${design.description}</td>
						<td width="20%">${design.reportDefinition.name}</td>
						<td width="20%"><rpt:displayLabel type="${design.rendererType.name}"/></td>
						<td width="5%" align="center"><a id="${design.uuid}DesignRemoveLink" href="#">
							<img src='<c:url value="/images/trash.gif"/>' border="0"/>
						</a></td>
					</tr>
				</c:forEach>	
			</tbody>
			<tfoot>
			</tfoot>
		</table>
	
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>