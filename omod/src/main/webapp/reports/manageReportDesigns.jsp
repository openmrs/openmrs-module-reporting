<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		
		$j("#report-design-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

		<c:forEach items="${reportDesigns}" var="design" varStatus="designStatus">
			$j('#${design.uuid}DesignEditLink').click(function(event){
				document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/renderers/editReportDesign.form?type=${design.rendererType.name}&reportDesignUuid=${design.uuid}';
			});
			$j('#${design.uuid}DesignRemoveLink').click(function(event){
				var encodedStr = "<c:out value='${design.name}'/>";
				var parser = new DOMParser;
				var dom = parser.parseFromString('<!doctype html><body>' + encodedStr,'text/html');
				var decodedString = dom.body.textContent;
				if (confirm('Please confirm you wish to permanantly delete '+decodedString)) {
					document.location.href='${pageContext.request.contextPath}/module/reporting/reports/deleteReportDesign.form?uuid=${design.uuid}';
				}
			});
		</c:forEach>
		
		$j( '#rendererType' ).change( function() {
			$j( this ).next( 'input#designAddLink' ).attr( 'href', $j( this ).val() );
		} );
		
		$j('#designAddLink').click(function(event){
			if($j("#rendererType").val()==="")
				alert("Please select a value from Dropdown");
			else	
				document.location.href = '${pageContext.request.contextPath}/module/reporting/reports/renderers/editReportDesign.form?type=' + $j( this ).attr( 'href' );
		});
	});
</script>

<div id="page">
	<div id="container">
		<h1><spring:message code="reporting.reportDesignManager" /></h1>
		<wgt:widget id="rendererType" name="rendererType" type="org.openmrs.module.reporting.report.renderer.ReportDesignRenderer"/>
		<input id="designAddLink" href="#" type="button" value="<spring:message code="reporting.manage.createNew"/>"/>
		<br/>

		<table id="report-design-table" class="display" >
			<thead>
				<tr>
					<th><spring:message code="reporting.name" /></th>
					<th><spring:message code="reporting.description" /></th>
					<th><spring:message code="reporting.ReportDefinition" /></th>
					<th><spring:message code="reporting.rendererType" /></th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reportDesigns}" var="design" varStatus="designStatus">
					<tr>
						<td width="20%" nowrap="true">
							<a id="${design.uuid}DesignEditLink" href="#"><c:out value='${design.name}' /></a>
						</td>
						<td><c:out value="${design.description}"/></td>
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
