<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/definition/invalidSerializedDefinitions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<script type="text/javascript" charset="utf-8">

	$j(document).ready(function() {
	
		// Redirect to listing page
		$j('#test-button').click(function(event){

			$j('#resultDialog').dialog('option', 'title', 'Serialization Result')
			.dialog('option', 'height', $j(window).height()-50)
			.dialog('open');

			var $type = $j("#subtype").val();
			var $serializer = $j("#serializationClass").val();
			var $data = $j("#serializedData").val();

			$j("#resultDialog").load("${pageContext.request.contextPath}/module/reporting/definition/testSerializedDefinition.form",
				{type: $type, serializationClass: $serializer, data: $data}
			);
		});

		$j('#cancel-button').click(function(event){
			document.location.href='invalidSerializedDefinitions.form';
		});
		
		$j('#resultDialog').dialog({
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '90%',
			modal: true
		});
		
	} );

</script>

<style>
	form ul { margin:0; padding:0; list-style-type:none; width:100%; }
	form li { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
	label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
</style>

<div id="resultDialog"></div>

<div id="page">

	<div id="container">
		
		<form method="post" action="saveSerializedDefinition.form">
			<input type="hidden" name="uuid" value="${definition.uuid}"/>
			<table style="font-size:small;">
				<tr>
					<td valign="top">
						<ul>
							<li>
								<label class="desc"><spring:message code="reporting.id" /></label>
								${definition.id}
							</li>		
							<li>
								<label class="desc" for="name"><spring:message code="reporting.name" /></label>
								<input type="text" id="name"  tabindex="2" name="name" value="${definition.name}" size="50"/>
							</li>
							<li>
								<label class="desc" for="description"><spring:message code="reporting.description" /></label>
								<textarea id="description" class="field text short" cols="40" rows="3" tabindex="3" name="description">${definition.description}</textarea>
							</li>
							<li>
								<label class="desc" for="type"><spring:message code="reporting.type" /></label>
								<input type="text" id="type" tabindex="4" name="type" value="${definition.type}" size="50"/>
							</li>
							<li>
								<label class="desc" for="subtype"><spring:message code="reporting.subtype" /></label>
								<input type="text" id="subtype" tabindex="5" name="subtype" value="${definition.subtype}" size="50"/>
							</li>
							<li>
								<label class="desc" for="type"><spring:message code="reporting.serializationClass" /></label>
								<input type="text" id="serializationClass" tabindex="6" name="serializationClass" value="${definition.serializationClass.name}" size="50"/>
							</li>
							<li>
								<input id="test-button" name="test" type="button" tabindex="7" value="Test"/>
								<input id="save-button" type="submit" tabindex="8" value="Save" />
								<input id="cancel-button" name="cancel" type="button" tabindex="9" value="Cancel"/>
							</li>
						</ul>
					</td>
					<td valign="top">
						<textarea id="serializedData" name="serializedData" cols="100" rows="30">${definition.serializedData}</textarea>
					</td>
				</tr>
			</table>
		</form>
	</div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
