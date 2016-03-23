<%@tag import="java.beans.PropertyDescriptor"%>
<%@tag import="java.lang.reflect.ParameterizedType"%>
<%@tag import="java.util.Map"%>
<%@tag import="org.apache.commons.beanutils.PropertyUtils"%>
<%@tag import="org.openmrs.module.reporting.common.ReflectionUtil"%>
<%@tag import="org.openmrs.module.reporting.evaluation.parameter.Mapped"%>
<%@tag import="org.openmrs.module.reporting.evaluation.parameter.Parameterizable"%>
<%@tag import="org.openmrs.module.reporting.evaluation.MissingDependencyException"%>
<%@tag import="org.openmrs.module.reporting.propertyeditor.MappedEditor"%>
<%@tag import="org.openmrs.module.reporting.web.taglib.FormatTag"%>

<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="formFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="object" required="true" type="java.lang.Object" %>
<%@ attribute name="propertyName" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" type="java.lang.String" %>
<%@ attribute name="emptyValueLabel" required="false" type="java.lang.String" %>
<%@ attribute name="changeFunction" required="false" type="java.lang.String" %>

<%
PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(object, propertyName);
if (!Mapped.class.isAssignableFrom(pd.getPropertyType())) {
	throw new IllegalArgumentException("mappedPropertyForObject must refer to a Mapped<?> but it refers to a " + pd.getPropertyType());
}

// determine the type of ${object}.${propertyName} 
Class<Parameterizable> mappedType = (Class<Parameterizable>) ((ParameterizedType) pd.getReadMethod().getGenericReturnType()).getActualTypeArguments()[0];

// determine the property's current value (when the page was loaded)
Mapped<?> currentValue = (Mapped<?>) PropertyUtils.getProperty(object, propertyName);
if (currentValue != null && currentValue.getParameterizable() == null)
    throw new MissingDependencyException();

// The url to load (in a dialog) when the user clicks change
String changeUrl = "/module/reporting/widget/getMappedAsString.form?valueType=" + mappedType.getName() + "&label=" + label + "&saveCallback=save" + id + "&cancelCallback=cancel" + id;
jspContext.setAttribute("changeUrl", changeUrl);

// determine formatted and serialized values for initial display
if (currentValue != null) {
	jspContext.setAttribute("currentValue", currentValue);
	jspContext.setAttribute("initialValueLabel", "<b>" + currentValue.getParameterizable().getName() + "</b>");
	if (currentValue.getParameterMappings() != null && currentValue.getParameterMappings().size() > 0) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> e : currentValue.getParameterMappings().entrySet()) {
			sb.append(e.getKey() + ": " + FormatTag.format(e.getValue()) + "<br/>");
		}
		jspContext.setAttribute("initialParamLabel", sb.toString());
	}
	MappedEditor editor = new MappedEditor();
    editor.setValue(currentValue);
    jspContext.setAttribute("initialFormFieldValue", editor.getAsText());
} else {
	jspContext.setAttribute("currentValue", null);
	jspContext.setAttribute("initialParamLabel", null);
	String emptyValueLabel = (String)jspContext.getAttribute("emptyValueLabel");
	jspContext.setAttribute("initialValueLabel", emptyValueLabel == null ? "Choose" : emptyValueLabel);
    jspContext.setAttribute("initialFormFieldValue", null);
}
%>

<script>
<c:choose>
    <c:when test="${ currentValue !=  null }">
        var currentUuid${ id } = '${ currentValue.parameterizable.uuid }';
    </c:when>
    <c:otherwise>
        var currentUuid${ id } = null;
    </c:otherwise>
</c:choose>
$j(document).ready(function() {
	$j('#${ id } .changeButton').click(function() {
		var url = '<c:url value="${ changeUrl }"/>';
		if (currentUuid${ id })
	        url += '&initialUuid=' + currentUuid${ id };
		showReportingDialog({ title: '${ label }', url: url });
	});
});

function save${ id }(serializedResult, jsResult) {
	$j('#${ id } .formField').val(serializedResult);
	$j('#${ id } .valueLabel').html('<b>' + jsResult.parameterizable + '</b>');
	var string = "";
	for (var key in jsResult.parameterMappings) {
		string += key + ": " + jsResult.parameterMappings[key] + "<br/>";
	}
	$j('#${ id } .parameterValuesLabel').html(string);
	currentUuid${ id } = jsResult.parameterizableUuid;
	closeReportingDialog(false);
	<c:if test="${ not empty changeFunction }">
	   ${ changeFunction}(jsResult);
	</c:if>
}

function cancel${ id }() {
	closeReportingDialog(false);
}

<c:if test="${ not empty initialFormFieldValue }">
$j(document).ready(function() {
	$j('#${ id } .formField').val("<spring:message javaScriptEscape="true" text="${ initialFormFieldValue }"/>");
});
</c:if>
</script>

<div id="${ id }">
    <input type="hidden" name="${ formFieldName }" class="formField"/>
    <button type="button" class="changeButton">
        <span class="valueLabel">${ initialValueLabel }</span><br/>
        <span class="parameterValuesLabel">${ initialParamLabel }</span>
    </button>
</div>