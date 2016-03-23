<%@tag import="org.openmrs.module.reporting.common.ObjectUtil"%>
<%@tag import="org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition"%>
<%@tag import="java.util.Map"%>
<%@tag import="org.openmrs.module.reporting.web.taglib.FormatTag"%>
<%@tag import="org.openmrs.module.reporting.propertyeditor.MappedEditor"%>
<%@tag import="org.openmrs.module.reporting.evaluation.MissingDependencyException"%>
<%@tag import="org.openmrs.module.reporting.evaluation.parameter.Mapped"%>
<%@tag import="org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil"%>
<%@tag import="org.openmrs.module.reporting.evaluation.parameter.Parameterizable"%>
<%@tag import="java.util.Collection"%>
<%@tag import="org.openmrs.module.reporting.common.ReflectionUtil"%>

<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="formFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="object" required="true" type="java.lang.Object" %>
<%@ attribute name="propertyName" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" type="java.lang.String" %>
<%@ attribute name="width" required="false" type="java.lang.String" %>
<%@ attribute name="changeFunction" required="false" type="java.lang.String" %>
<%@ attribute name="removeFunction" required="false" type="java.lang.String" %>

<%
	Collection currentValues = (Collection)ReflectionUtil.getPropertyValue(object, propertyName);
	Class<? extends Parameterizable> mappedType = ParameterizableUtil.getMappedType(object.getClass(), propertyName);
	String changeUrl = "/module/reporting/widget/getMappedAsString.form?valueType=" + mappedType.getName() + "&label=" + label + "&saveCallback=save" + id + "&cancelCallback=cancel" + id + "&removeCallback=remove" + id;
	jspContext.setAttribute("currentValues", currentValues);
	jspContext.setAttribute("changeUrl", changeUrl);
%>

<div id="${id}">
	
	<script type="text/javascript">
	
		var currentRowId = null;
		var nextRowIndex = ${currentValues == null ? 0 : fn:length(currentValues)}
		var currentUuidsForRows = {};
		
		$j(document).ready(function() {
			
			refreshRowSeparators();
			
			$j('#${id} .changeButton').click(function() {
				var url = '<c:url value="${changeUrl}"/>';
				var rowId = $j(this).parents('.rowSection${id}:first').attr("id");
				var title = 'Add';
				var currentUuidForRow = currentUuidsForRows[rowId];
				if (currentUuidForRow) {
					currentRowId = rowId;
			        url += '&initialUuid=' + currentUuidForRow;
			        title = 'Edit ${label}';
				}
				else {
					currentRowId = null;
				}
				showReportingDialog({ title: title, url: url });
			});
		});
		
		function refreshRowSeparators() {
			$j('.andSection').remove();
			var i = 0;
			$j('.formField').each(function(event) {
				var val = $j(this).val();
				if (i++ > 0) {
					$j(this).before('<div class="andSection" style="text-align:center; padding:10px; font-size:10px; font-weight:bold;">AND</div>');
				}
			});
		}
		
		function save${id}(serializedResult, jsResult) {
			var rowToChange = null;
			if (currentRowId == null) {
				currentRowId = '${id}' + nextRowIndex++;
				rowToChange = $j('#template${id}').clone(true).attr('id', currentRowId);
				$j('#newRows${id}').append(rowToChange);
			}
			else {
				rowToChange = $j('#'+currentRowId);
			}
			
			var formField = $j(rowToChange).find('.formField');
			$j(formField).attr('name', '${formFieldName}');
			$j(formField).val(serializedResult);
			
			$j(rowToChange).find('.valueLabel').html('<b>' + jsResult.parameterizable + '</b>');
			var string = "";
			for (var key in jsResult.parameterMappings) {
				string += key + ": " + jsResult.parameterMappings[key] + "<br/>";
			}
			$j(rowToChange).find('.parameterValuesLabel').html(string);
			
			currentUuidsForRows[currentRowId] = jsResult.parameterizableUuid;
			
			refreshRowSeparators();
			closeReportingDialog(false);
			
			<c:if test="${!empty changeFunction}">
			   ${changeFunction}(jsResult);
			</c:if>
			
			$j(rowToChange).show();
		}

		function cancel${id}() {
			closeReportingDialog(false);
		}
		
		function remove${id}() {
			$j('#' + currentRowId).remove();
			refreshRowSeparators();
			closeReportingDialog(false);
			<c:if test="${!empty removeFunction}">
			   ${removeFunction}(currentRowId);
			</c:if>
		}
		
	</script>
	
	<b class="boxHeader" style="font-weight:bold; text-align:right; width:${!empty width ? width : '300px'}">
		<span style="float:left;">${label}</span>
		<a style="color:lightyellow; font-weight:bold;" href="#" class="changeButton">[+] Add</a>
	</b>
	<div class="box" style="width:${!empty width ? width : '300px'}">
		
		<%	
			if (currentValues != null) {
				int index = 0;
				for (Object o : currentValues) {
					Mapped<?> currentValue = (Mapped<?>)o;
					if (currentValue == null || currentValue.getParameterizable() == null) {
					    throw new MissingDependencyException();
					}
					String initialParamLabel = null;
					if (currentValue.getParameterMappings() != null && currentValue.getParameterMappings().size() > 0) {
						StringBuilder sb = new StringBuilder();
						for (Map.Entry<String, Object> e : currentValue.getParameterMappings().entrySet()) {
							sb.append(e.getKey() + ": " + FormatTag.format(e.getValue()) + "<br/>");
						}
						initialParamLabel = sb.toString();
					}
					MappedEditor editor = new MappedEditor();
				    editor.setValue(currentValue);
					
				    jspContext.setAttribute("index", "" + index);
				    jspContext.setAttribute("rowId", id + index);
					jspContext.setAttribute("currentValue", currentValue);
					jspContext.setAttribute("initialValueLabel", "<b>" + currentValue.getParameterizable().getName() + "</b>");
					jspContext.setAttribute("initialValueUuid", currentValue.getParameterizable().getUuid());
					jspContext.setAttribute("initialParamLabel", initialParamLabel);
				    jspContext.setAttribute("initialFormFieldValue", editor.getAsText());
				    
				    index++;
		%>

			<c:if test="${ not empty initialFormFieldValue }">
				<script type="text/javascript">
					$j(document).ready(function() {
						$j('#${rowId}').find('.formField').val("<spring:message javaScriptEscape="true" text="${initialFormFieldValue}"/>");
						currentUuidsForRows['${rowId}'] = '${initialValueUuid}';
					});
				</script>
			</c:if>
		
			<div id="${rowId}" class="rowSection${id}">
			    <input type="hidden" name="${formFieldName}" class="formField" value=""/>
			    <button class="changeButton" style="width:${!empty width ? width : '300px'}">
			        <span class="valueLabel">${initialValueLabel}</span><br/>
			        <span class="parameterValuesLabel">${initialParamLabel}</span>
			    </button>
			</div>
		
		<% 
				}
			}
		%>

		<div id="newRows${id}"></div>
		
		<div id="template${id}" style="display:none;" class="rowSection${id}">
		    <input type="hidden" class="formField"/>
		    <button type="button" class="changeButton" style="width:300px;">
		        <span class="valueLabel"></span><br/>
		        <span class="parameterValuesLabel"></span>
		    </button>
		</div>
	</div>
</div>