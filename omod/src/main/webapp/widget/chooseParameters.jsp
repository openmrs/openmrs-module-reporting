<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>		

<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<form method="post">
	<c:forEach var="p" items="${ parameters }" varStatus="status">
	    ${ p.label }:
	    <wgt:widget id="param${ status.count }" name="${ p.name }" type="${ p.type.name }"/>
	    <br/>
	    <!--
	    ${ p.label } (${ p.name }) = ${ p.type } ${ p.collectionType }<br/>
	    -->
	</c:forEach>
	<input type="submit" value="Guardar"/>
</form>

Chosen: <pre id="theValue">${ fn:replace(fn:replace(serialized, "<", "&lt;"), ">", "&gt;") }</pre>

<c:if test="${ not empty serialized }">
    <script>
        window.parent.${ parentSaveCallback }($j('#theValue').html());
    </script>
</c:if>

<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>