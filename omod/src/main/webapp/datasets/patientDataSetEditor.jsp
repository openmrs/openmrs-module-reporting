<%@ include file="../manage/localHeader.jsp"%>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/validate/jquery.validate.min.js"/>
<c:if test="${ pageContext.response.locale.language != 'en' }">
	<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery/validate/localization/messages_${ pageContext.response.locale.language }.js"/>
</c:if>

<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />

<spring:message var="cohortDefinitionLabel" code="reporting.CohortDefinition"/>
<c:url var="addFilterUrl" value="/module/reporting/widget/getMappedAsString.form">
	<c:param name="valueType" value="org.openmrs.module.reporting.cohort.definition.CohortDefinition"/>
	<c:param name="label" value="${ cohortDefinitionLabel }"/> 
	<c:param name="saveCallback" value="addFilterSave"/>
	<c:param name="cancelCallback" value="addFilterCancel"/>
</c:url>

<style>
	#unsaved { background-color: yellow; }
	#unsaved form { display: inline; }
	div.metadataField { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	div.metadataField label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
	#sortable-columns { list-style-type: none; margin: 0; padding: 0; width: 60%; }
	#sortable-columns li { margin: 0 3px 3px 3px; padding: 0.4em; padding-left: 1.5em; }
	#sortable-columns li span.column-sort-icon { position: absolute; margin-left: -1.3em; }
	#sortable-columns li span.column-name { font-weight: bold; }
	#sortable-columns li span.column-description { font-style: italic; font-size: 0.9em; font-weight: normal; }
	#sortable-columns li form { float: right; }
	div#addCol { display: inline; }
	.boxHeader input[type=button] { float: right; }
</style>

<script>
$j(function() {
	$j('#edit-name-dialog,#add-parameter-dialog').dialog({
		autoOpen: false,
		modal: true,
		width: '80%',
		height: 600
	});
	$j('#edit-name-button').click(function() {
		$j('#edit-name-dialog').dialog('open');
	});
	$j('#edit-name-cancel').click(function() {
		$j('#edit-name-dialog').dialog('close');
	});
	$j('#add-parameter-button').click(function() {
		$j('#add-parameter-dialog').dialog('open');
	});
	$j('#add-parameter-cancel').click(function() {
		$j('#add-parameter-dialog').dialog('close');
	});
	$j('#sortable-columns').sortable({
		placeholder: 'ui-state-highlight',
		update: function(evt, ui) {
			submitColumnOrder($j(this));
		}
	}).disableSelection();
	$j('#addFilterButton').click(function() {
		showReportingDialog({ title: 'Add Row Filter', url: '${ addFilterUrl }' });
	});
	$j('#add-column-form').validate({
		ignore: [], // don't ignore hidden elements
		rules: {
			label: 'required',
			columnDefinition: 'required'
		},
		errorPlacement: function(error, element) {
			error.appendTo( element.parents("td").next("td") );
		}
	});
});

function addFilterSave(serializedResult, jsResult) {
	$j('#addFilterForm input[name=filterDefinition]').val(serializedResult);
	$j('#addFilterForm').submit();
}

function addFilterCancel() {
	closeReportingDialog(false);
}

function submitColumnOrder(sortable) {
	var columnOrder = { };
	// don't know the jquery version, so we ensure we get a submission like column1=name1&column2=name2&...
	sortable.children().each(function(index) {
		columnOrder["column" + index] = $j(this).children('.column-name').html();
	});
	$j.post('patientDataSetEditor-sortColumns.form', columnOrder, function(data, textStatus, xhr) {
		window.location.reload();
	});
}
</script>

<c:if test="${ unsaved }">
	<div id="unsaved">
		You have unsaved changes.
		<form method="post" action="patientDataSetEditor-save.form">
			<input type="submit" value="<spring:message code="general.save"/>"/>
		</form>
		<form method="post" action="patientDataSetEditor-discard.form">
			<input type="submit" value="<spring:message code="reporting.discardButton"/>"/>
		</form>
	</div>
</c:if>

<div style="float: left; width: 40%">

	<div class="boxHeader">
		${ dsd.name }
		<input type="button" id="edit-name-button" value="Edit"/>
	</div>
	<div class="box">
		<h3></h3>
		<c:choose>
			<c:when test="${ empty dsd.description }">
				<i><span style="color: #e0e0e0"><spring:message code="general.none"/></span></i>
			</c:when>
			<c:otherwise>
				<i>${ dsd.description }</i>
			</c:otherwise>
		</c:choose>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		Parameters
		<input type="button" id="add-parameter-button" value="Add"/>
	</div>
	<div class="box">
		<table>
			<tr>
				<th><spring:message code="general.name"/></th>
				<th>Label</th>
				<th>Type</th>
				<th></th>
			</tr>
			<c:forEach var="p" items="${ dsd.parameters }">
				<tr>
					<td>${ p.name }</td>
					<td>${ p.label }</td>
					<td>
						<c:choose>
							<c:when test="${p.collectionType != null}">
								${p.collectionType.simpleName}&lt;${p.type.simpleName}&gt;
							</c:when>
							<c:otherwise>
								${p.type.simpleName}
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<form method="post" action="patientDataSetEditor-removeParam.form">
							<input type="hidden" name="name" value="${ p.name }"/>
							<input type="submit" value="Remove"/>
						</form>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	
	<br/>
	
	<div class="boxHeader">
		Row Filters
		<input type="button" id="addFilterButton" value="<spring:message code="general.add"/>"/>
	</div>
	<div class="box">
		<c:choose>
			<c:when test="${ empty dsd.rowFilters }">
				<spring:message code="general.none"/>
			</c:when>
			<c:otherwise>
				<table>
					<tr>
						<th><spring:message code="general.name"/></th>
						<th>Mappings</th>
						<th></th>
					</tr>
					<c:forEach var="mappedFilter" items="${ dsd.rowFilters }" varStatus="iter">
						<tr>
							<td>${ mappedFilter.parameterizable.name }</td>
							<td>
								<c:forEach var="mapping" items="${ mappedFilter.parameterMappings }">
									${ mapping.key } -> <rpt:format object="${ mapping.value }"/>
									<br/>
								</c:forEach>
							</td>
							<td>
								<form method="post" action="patientDataSetEditor-removeFilter.form">
									<input type="hidden" name="filterIndex" value="${ iter.index }"/>
									<input type="submit" value="<spring:message code="general.remove"/>"/>
								</form>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
		
		<form method="post" action="patientDataSetEditor-addFilter.form" id="addFilterForm">
			<input type="hidden" name="filterDefinition" value=""/>
		</form>	
	</div>
	
	<div id="edit-name-dialog">
		<form method="post" action="patientDataSetEditor-nameDescription.form">
			<table>
				<tr valign="top">
					<td><spring:message code="general.name"/></td>
					<td>
						<input type="text" name="name" value="<spring:message javaScriptEscape="true" text="${ dsd.name }"/>"/>
					</td>
				</tr>
				<tr valign="top">
					<td><spring:message code="general.description"/></td>
					<td>
						<textarea name="description" rows="5" cols="80"><c:out value="${ dsd.description }"/></textarea>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="submit" value="<spring:message code="reporting.apply"/>"/>
						<input type="button" value="<spring:message code="general.cancel"/>" id="edit-name-cancel"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
	
	<div id="add-parameter-dialog">
		<form method="post" action="patientDataSetEditor-addParam.form">

			<div class="metadataField">
				<label class="desc"><spring:message code="reporting.type" /></label>
				<select name="collectionType">
					<option value=""><spring:message code="reporting.single" /></option>
					<c:forEach var="supportedType" items="${ parameterCollectionTypes }">
						<option value="${supportedType.value}">${supportedType.labelText} of</option>
					</c:forEach>
				</select>
				<select name="parameterType">
					<option value=""></option>
					<c:forEach var="supportedType" items="${ parameterTypes }">
						<option value="${supportedType.value}">${supportedType.labelText}</option>
					</c:forEach>
				</select>
			</div>

			<div class="metadataField">
				<label class="desc" for="name"><spring:message code="general.name"/></label>
				<input type="text" id="name" tabindex="1" name="name" size="50"/>
			</div>
			<div class="metadataField">
				<label class="desc" for="label"><spring:message code="reporting.label" /></label>
				<textarea id="label" cols="50" rows="2" tabindex="2" name="label"></textarea>
			</div>
			<div class="metadataField">
				<label class="desc" for="widgetConfiguration"><spring:message code="reporting.advancedConfig" /></label>
				<textarea id="widgetConfiguration" cols="50" rows="5" tabindex="3" name="widgetConfiguration"></textarea>
			</div>

			<input type="submit" value="<spring:message code="reporting.apply"/>" tabindex="4"/>
			<input type="button" value="<spring:message code="general.cancel"/>" id="add-parameter-cancel" tabindex="5"/>
		</form>
	</div>
	
</div>

<div style="float: left; margin-left: 2em; width: 50%">

	<div class="boxHeader">
		Columns
	</div>
	<div class="box">
		<ul id="sortable-columns">
			<c:forEach var="col" items="${ dsd.columnDefinitions }">
				<li class="ui-state-default">
					<form method="post" action="patientDataSetEditor-removeColumn.form">
						<input type="hidden" name="name" value="${ col.name }"/>
						<input type="submit" value="Remove"/>
					</form>

					<span class="column-sort-icon ui-icon ui-icon-arrowthick-2-n-s"></span>
					<span class="column-name">${ col.name }</span>
					<br/>
					<span class="column-description">
						${ col.dataDefinition.parameterizable.name }
						<c:forEach var="mapping" items="${ col.dataDefinition.parameterMappings }">
							<br/>
							&nbsp;&nbsp;
							${ mapping.key } -> <rpt:format object="${ mapping.value }"/>
						</c:forEach>
					</span>
				</li>
			</c:forEach>
		</ul>
	</div>

	<br/>
	
	<div class="boxHeader">
		<spring:message code="general.add"/>
	</div>
	<div class="box">
		<form id="add-column-form" method="post" action="patientDataSetEditor-addColumn.form" style="background-color: #e0e0e0">
			<table>
				<tr>
					<td><spring:message code="reporting.label" /></td>
					<td><input type="text" name="label"/></td>
					<td></td>
				</tr>
				<tr>
					<td><spring:message code="reporting.definition" /></td>
					<td><rptTag:chooseDataDefinition id="addCol" formFieldName="columnDefinition" types="${dataDefinitionTypes}"/></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="<spring:message code="general.add"/>"/></td>
				</tr>
			</table>
		</form>
	</div>

</div>

<div style="clear: both"></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
