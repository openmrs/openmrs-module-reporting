<%@ tag import="java.net.URLEncoder" %>
<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="formFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="types" required="true" type="java.lang.String" %>
<%@ attribute name="changeFunction" required="false" type="java.lang.String" %>

<%
String label = URLEncoder.encode("Data Definition");
//The url to load (in a dialog) when the user clicks change
String changeUrl = "/module/reporting/widget/getMappedAsString.form?valueType=" + types + "&label=" + label + "&saveCallback=save" + id + "&cancelCallback=cancel" + id;
jspContext.setAttribute("changeUrl", changeUrl);

jspContext.setAttribute("currentValue", null);
jspContext.setAttribute("initialParamLabel", null);
jspContext.setAttribute("initialValueLabel", "Choose");
jspContext.setAttribute("initialFormFieldValue", null);
%>

<script>
$j(function() {
	$j('#${ id } .changeButton').click(function() {
		var url = '<c:url value="${ changeUrl }"/>';
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
	closeReportingDialog(false);
	<c:if test="${ not empty changeFunction }">
	   ${ changeFunction}(jsResult);
	</c:if>
}

function cancel${ id }() {
	closeReportingDialog(false);
}
</script>

<div id="${ id }">
    <input type="hidden" name="${ formFieldName }" class="formField"/>
    <button type="button" class="changeButton">
        <span class="valueLabel">${ initialValueLabel }</span><br/>
        <span class="parameterValuesLabel">${ initialParamLabel }</span>
    </button>
</div>