<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<%--
    available properties:
        showDecoration -> (boolean, default true) whether or not to show this in a decorated box.
        showIfNone -> (boolean, default true) whether to display an empty box is there are no saved reports
--%>

<c:set var="showDecoration" value="${model.showDecoration}"/>
<c:set var="showIfNone" value="${model.showIfNone}"/>
<c:if test="${showDecoration == null}">
    <c:set var="showDecoration" value="true"/>
</c:if>
<c:if test="${showIfNone == null}">
    <c:set var="showIfNone" value="true"/>
</c:if>

<c:if test="${showIfNone || model.any}">

    <c:if test="${showDecoration}">
        <div class="portlet">
            <div class="portlet-header">
                <spring:message code="reporting.Report.inProgress.title" />
            </div>
            <div class="portlet-content">
    </c:if>
    
    <c:choose>
        <c:when test="${ model.any }">

            <fieldset>
                <legend><small><i><spring:message code="reporting.runningNow" /></i></small></legend>
                <c:if test="${fn:length(model.inProgress) == 0}"><spring:message code="reporting.none" /></c:if>
			    <table cellspacing="0" cellpadding="2">
			        <c:forEach var="r" items="${model.inProgress}" varStatus="iterstatus">
			            <tr valign="top">
			                <td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
			                    ${r.reportDefinition.parameterizable.name}
			                </td>
			                <td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
			                    <table class="small" cellspacing="0" cellpadding="0">
			                        <c:forEach var="p" items="${r.reportDefinition.parameterMappings}">
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
							<td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
							    by <openmrs:format user="${ r.requestedBy }"/>
							</td>
			            <tr>
			        </c:forEach>
			    </table>
            </fieldset>

            <fieldset>
                <legend><small><i><spring:message code="reporting.queued" /></i></small></legend>
                <c:if test="${fn:length(model.queue) == 0}"><spring:message code="reporting.none" /></c:if>
                <table cellspacing="0" cellpadding="2">
                    <c:forEach var="r" items="${model.queue}" varStatus="iterstatus">
                        <tr valign="top">
                            <td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
                                ${r.reportDefinition.parameterizable.name}
                            </td>
                            <td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
                                <table class="small" cellspacing="0" cellpadding="0">
                                    <c:forEach var="p" items="${r.reportDefinition.parameterMappings}">
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
                            <td <c:if test="${!iterstatus.last}"> style="border-bottom: 1px #c0c0c0 solid; white-space: nowrap;" </c:if>>
                                by <openmrs:format user="${ r.requestedBy }"/>
                            </td>
                        <tr>
                    </c:forEach>
                </table>
            </fieldset>
        
        </c:when>
        <c:otherwise>
            <spring:message code="reporting.none"/>
        </c:otherwise>
    </c:choose>
    
    <c:if test="${showDecoration}">
            </div>
        </div>
    </c:if>
</c:if>
