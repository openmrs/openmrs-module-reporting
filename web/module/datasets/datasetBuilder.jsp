<%@ include file="../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/datasets/datasetBuilder.form" />			
<c:set value="/module/reporting/datasets/datasetBuilder.form?uuid=uuid" var="pageUrl"/>

<script type="text/javascript">
	function testChangeCallback(mappedFilter) {
		alert('Adding: ' + mappedFilter.parameterizable);
	}
	function testRemoveCallback(rowId) {
		alert('Removed row: ' + rowId);
	}
</script>

<table>
	<tr valign="top">
		<td width="30%">
			<openmrs:portlet url="parameter" id="newParameter" moduleId="reporting" parameters="type=${datasetType.name}|label=Parameters|parentUrl=${pageUrl}" />
			<br/>
			<rptTag:mappedCollectionProperty id="filters" formFieldName="filters" object="${dsd}" propertyName="rowFilters" label="Row Filters" changeFunction="testChangeCallback" removeFunction="testRemoveCallback" />
		</td>
		<td width="30%">

		</td>
		<td width="40%">
		
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>