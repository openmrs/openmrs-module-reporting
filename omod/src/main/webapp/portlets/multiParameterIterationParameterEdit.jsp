<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:if test="${model.dialog != 'false'}">
	<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
</c:if>

<script type="text/javascript" charset="utf-8">
    $j(document).ready(function() {
        $j('#cancelButton').click(function(event){
            closeReportingDialog(false);
        });

        $j('#typeSelector').change(function(event) {
            $j('#typeSelector_fixed').hide();
            $j('#typeSelector_mapped').hide();
            $j('#typeSelector_complex').hide();
            var val = $j('#typeSelector').val();
            $j('#typeSelector_' + val + '').show();
        });
        $j('#typeSelector').trigger('change');

    });
</script>

<form id="mapParametersForm${model.id}" method="post" action="${pageContext.request.contextPath}/module/reporting/datasets/multiParameterEditIterationParameter.form">
    <input type="hidden" name="dsdUuid" value="${model.dsd.uuid}"/>
    <input type="hidden" name="index" value="${model.index}"/>
    <input type="hidden" name="iteration" value="${model.iteration}"/>
    <input type="hidden" name="parameterName" value="${model.param.name}"/>

    <span>${model.param.name} for iteration ${model.iteration}: </span>
    <c:choose>
        <c:when test="${empty model.allowedParams}">
            <input type="hidden" name="valueType" value="fixed"/>
            <wgt:widget id="fixedValue" name="fixedValue" type="${model.param.type.name}" defaultValue="${model.fixedParams[model.param.name]}" attributes="style=vertical-align:top;"/>
        </c:when>
        <c:otherwise>
            <select id="typeSelector" name="valueType">
                <option value="fixed" <c:if test="${model.mappedParams[model.param.name] == null && model.complexParams[model.param.name] == null}">selected</c:if>>
                    <spring:message code="reporting.value" />:
                </option>
                <option value="mapped" <c:if test="${model.mappedParams[model.param.name] != null}">selected</c:if>>
                    <spring:message code="reporting.multiParameter.Parameter" />:
                </option>
                <option value="complex" <c:if test="${model.complexParams[model.param.name] != null}">selected></c:if>>
                    <spring:message code="reporting.expression" />:
                </option>
            </select>
            <span id="typeSelector_fixed" style="display:none;">
                <wgt:widget id="fixedValue" name="fixedValue" type="${model.param.type.name}" defaultValue="${model.fixedParams[model.param.name]}" attributes="style=vertical-align:top;"/>
            </span>
            <span id="typeSelector_mapped" style="display:none;">
                <select name="mappedValue">
                    <option value="" <c:if test="${model.mappedParams[model.param.name] == null}">selected</c:if>><spring:message code="reporting.multiParameter.Choose" />...</option>
                    <c:forEach var="allowedParam" items="${model.allowedParams}">
                        <option value="${allowedParam.key}" <c:if test="${model.mappedParams[model.param.name] == allowedParam.key}">selected</c:if>>
                            ${allowedParam.value}
                        </option>
                    </c:forEach>
                </select>
            </span>
            <span id="typeSelector_complex" style="display:none;">
                <input type="text" name="complexValue" size="40" value="${model.complexParams[model.param.name]}"/>
            </span>
        </c:otherwise>
    </c:choose>

    <hr style="color:blue;"/>
    <div style="width:100%; text-align:left;">
        <input type="submit" class="ui-button ui-state-default ui-corner-all" value="Submit" />
        <input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
    </div>
</form>
