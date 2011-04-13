<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Scheduled Report Tasks" otherwise="/login.htm" redirect="/module/reporting/task/listScheduledReportTasks.form" />
<%@ include file="../manage/localHeader.jsp"%>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/jquery-ui-timepicker-addon.js"/>

<script>
function updateRenderingModes(mappedReportDefinition) {
	var uuid = null;
	if (mappedReportDefinition)
	   uuid = mappedReportDefinition.parameterizableUuid;
	updateRenderingModesByReportDefinitionUuid(uuid)
}

function updateRenderingModesByReportDefinitionUuid(uuid, selectedModeDescriptor) {
	$('#renderingMode').empty();
	if (uuid) {
	    $.getJSON('${ pageContext.request.contextPath }/module/reporting/ajax/getRenderingModes.form?reportDefinitionUuid=' + uuid, function(data) {
	    	var str = "<option value=\"\"></option>";
            for (var i = 0; i < data.length; ++i) {
            	var descriptor = data[i].descriptor;
                str += '<option value="' + descriptor + '"';
                if (descriptor == selectedModeDescriptor)
                	str += ' selected="true"';
                str += '>' + data[i].label + '</option>';
            }
            $('#renderingMode').html(str);
	    });
	}
}

$(document).ready(function() {
	$('#startTime').datetimepicker({
		dateFormat: 'yy-mm-dd',
		timeFormat: 'hh:mm:ss',
		separator: ' '
	});
	$('#cancelButton').click(function() {
		window.location = 'listScheduledReportTasks.list';
	});
	<c:if test="${ not empty task.reportDefinition }">
        updateRenderingModesByReportDefinitionUuid('${ task.reportDefinition.parameterizable.uuid }' <c:if test="${ not empty task.renderingMode }">, '${ task.renderingMode.descriptor }'</c:if>);
    </c:if>
});
</script>

<c:if test="${ not empty task.taskDefinitionId }">
    <span style="float: right">
        <form method="post" action="deleteScheduledReportTask.form?id=${ task.taskDefinitionId }">
            <input type="submit" value="Delete this task"/>
        </form>
    </span>
</c:if>

<h2>Schedule Report Run</h2>

<c:if test="${ not empty errors && errors.errorCount > 0 }">
    <div class="error">
        <ul>
            <c:forEach var="e" items="${ errors.globalErrors }">
                <li>${ e }</li>
            </c:forEach>
            <c:forEach var="e" items="${ errors.fieldErrors }">
                <li>${ e.field }: ${ e.code }</li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form method="post">
    <fieldset>
        <legend>Evaluation Details</legend>
	    <table>
	        <tr valign="top">
	            <td>Report</td>
	            <td>
	                <rptTag:mappedPropertyForObject id="reportDefinition"
	                    formFieldName="reportDefinition" object="${ task }"
	                    propertyName="reportDefinition" label="Report"
	                    changeFunction="updateRenderingModes"/>
	            </td>
	        </tr>
            <tr valign="top">
                <td>Base Cohort</td>
                <td>
                    <rptTag:mappedPropertyForObject id="baseCohort"
                        formFieldName="baseCohort" object="${ task }"
                        propertyName="baseCohort" label="Base Cohort"/>
                </td>
            </tr>
	        <tr>
	            <td>Output format</td>
	            <td>
	                <select name="renderingMode" id="renderingMode"></select>
	            </td>
	        </tr>
	        <tr>
	            <td>Priority</td>
	            <td>
	                <select name="priority">
	                    <c:forTokens var="token" items="HIGHEST,HIGH,NORMAL,LOW,LOWEST" delims=",">
	                        <option value="${ token }" <c:if test="${ task.priority == token }">selected="true"</c:if>>${ token }</option>
	                    </c:forTokens>                      
	                </select>
	            </td>
	        </tr>
	    </table>        
    </fieldset>

    <br/>
    
    <fieldset>
        <legend>Schedule</legend>
        <table>
	        <tr>
	            <td>Start Time</td>
	            <td>
	                <spring:bind path="task.startTime">
	                    <input type="text" id="startTime" name="startTime" value="${ status.value }"/>
	                </spring:bind>
	            </td>
	        </tr>
	        <tr>
	            <td>Repeat Interval</td>
	            <td>
	                <select name="repeatInterval">
	                    <option value=""></option>
	                    <c:forEach var="i" items="${ repeatIntervals }">
	                        <option value="${ i.key }" <c:if test="${ task.repeatInterval == i.key }">selected="true"</c:if>>${ i.value }</option>
	                    </c:forEach>
	                </select>
	            </td>
	        </tr>
	    </table>
    </fieldset>

    <br/>
    <input type="submit" value="Save Scheduled Report Task"/>
    <input type="button" id="cancelButton" value="<spring:message code="general.cancel"/>"/>

</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>