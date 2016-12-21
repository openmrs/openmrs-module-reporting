<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<script type="text/javascript">
	jQuery(document).ready(function() {
		jQuery('#${model.portletUUID}ReportTable').dataTable({
		    "bPaginate": true,
		    "iDisplayLength": ${model.numOnPage},
		    "bLengthChange": false,
		    "bFilter": false,
		    "bInfo": true,
		    "bAutoWidth": false,
		    "bSortable": true,
		    "aoColumns": [{ "sType": "html" }]
		});
	});
</script>

<table id="${model.portletUUID}ReportTable" style="width:100%;" class="reporting-data-table display">
	<thead style="display:none;">
		<tr><th></th></tr>
	</thead>
	<tbody>
		<c:forEach items="${model.definitionSummaries}" var="r">
			<tr>
				<td>
					<a href="${pageContext.request.contextPath}/module/reporting/run/runReport.form?reportId=${r.uuid}">
						<c:out value="${r.name}" />
					</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>