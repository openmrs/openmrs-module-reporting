<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>     

<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<spring:htmlEscape defaultHtmlEscape="false" />

<script>
$j(document).ready(function() {
	$j('#valueUuid').change(function() {
		// todo clear rest of form
		$j('#valueAndMappingsForm').submit();
	});
	$j('.chooseStyle').change(function() {
		var index = $j(this).attr('id').substring(11);
		var toShow = $j(this).val();
		$j('.valueField' + index).hide();
		$j('#' + toShow + index).show();
	});
	$j('#cancelButton').click(function() {
		window.parent.${ param.cancelCallback }();
	});
	<c:if test="${!empty param.removeCallback}">
		$j('#deleteButton').click(function() {
			window.parent.${ param.removeCallback }();
		});
	</c:if>
	<c:if test="${ not empty selectedValue }">
	   $j('#valueUuid').val('${ selectedValue.uuid }');
	   <c:if test="${!empty param.removeCallback}">
	  	 $j('#deleteButton').show();
	   </c:if>
	</c:if>
});
</script>

<form method="post" id="valueAndMappingsForm">
    Choose a ${ param.label }:
    <select name="valueUuid" id="valueUuid">
        <option value=""></option>
	    <c:forEach var="opt" items="${ valueOptions }">
	       <option value="${ opt.uuid }">${ opt.name }</option>
	    </c:forEach>
	</select>
	
	<c:if test="${ not empty selectedValue }">
        <div>
            <h4>Specify Parameter Values</h4>
	        <c:choose>
	            <c:when test="${ not empty selectedValue.parameters }">
                    <table>
                    <c:forEach var="p" items="${ selectedValue.parameters }" varStatus="status">
                        <tr>
                            <td>${ p.label }:</td>
                            <td>
		                        <select id="chooseStyle${ status.count }" name="chooseStyle${ p.name }" class="chooseStyle">
		                            <option value="fixed">Value:</option>
		                            <option value="complex">Expression:</option>
		                        </select>
		                        <span class="valueField${ status.count }" id="fixed${ status.count }">
		                            <wgt:widget id="param${ status.count }" name="fixedValue_${p.name}" type="${ p.type.name }"/>
		                        </span>
		                        <span class="valueField${ status.count }" id="complex${ status.count }" style="display: none">
		                            <input type="text" size="40" name="complexValue_${ p.name }"/>
		                        </span>
                            </td>
                        </tr>
                    </c:forEach>
                    </table>
		       </c:when>
		       <c:otherwise>
		           None required
		       </c:otherwise>
		   </c:choose>
		   <br/>
		   <input type="submit" name="action" value="Use this mapped value"/>
		   <input type="button" value="Cancel" id="cancelButton"/>
		   <input type="button" value="Delete" id="deleteButton" style="display:none;"/>
	   </div>
	</c:if>
</form>

<c:if test="${ not empty serializedResult }">
    (The save callback should close the dialog, so you should not see this.)<br/><br/>
    The result is:<br/>
    <pre>${ fn:replace(fn:replace(serializedResult, "<", "&lt;"), ">", "&gt;") }</pre>
    <script>
        window.parent.${ param.saveCallback }('<spring:message javaScriptEscape="true" text="${ serializedResult }"/>', ${ jsResult });
    </script>
</c:if>

<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>