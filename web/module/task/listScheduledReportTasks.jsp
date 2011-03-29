<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Scheduled Report Tasks" otherwise="/login.htm" redirect="/module/reporting/task/listScheduledReportTasks.form" />
<%@ include file="../manage/localHeader.jsp"%>

<br/>

<a href="editScheduledReportTask.form">Add Scheduled Report Task</a>

<br/><br/>

<div class="boxHeader">
    Scheduled Report Tasks
</div>
<div class="box">
    <table>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Report</th>
            <th>Parameters</th>
            <th>Next Run</th>
        </tr>
        <c:forEach var="task" items="${ tasks }">
            <tr valign="top">
                <td>
                    ${ task.taskDefinitionId }
                </td>
                <td>
                    <a href="editScheduledReportTask.form?id=${ task.taskDefinitionId }">${ task.taskDefinition.name }</a>
                </td>
                <td>
                    ${ task.reportDefinition.parameterizable.name }
                </td>
                <td>
                    <table class="small" cellspacing="0" cellpadding="0">
                        <c:forEach var="p" items="${task.reportDefinition.parameterMappings}">
                            <tr valign="top">
                                <td class="faded" align="right">
                                    ${p.key}:
                                </td>
                                <td>
                                    <rpt:format object="${p.value}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
                <td>
                    <openmrs:formatDate format="dd/MMM/yyyy hh:mm:ss" date="${ task.taskDefinition.nextExecutionTime }"/>
                </td>
            </tr>
        </c:forEach>
    </table>
	<ul>
		
	</ul>
	
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>