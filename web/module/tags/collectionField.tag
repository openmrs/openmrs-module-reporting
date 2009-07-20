<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ attribute name="type" required="true" %>
<%@ attribute name="formFieldName" required="true" %>
<%@ attribute name="initialValues" required="false" type="java.util.Collection" %>
<%@ attribute name="parameters" required="false" %>

<openmrs:htmlInclude file="/scripts/jquery/jquery-1.2.6.min.js" />
<script type="text/javascript">
	var $j = jQuery.noConflict();

	function removeRow(btn) {
		var parent = btn.parentNode;
		while (parent.tagName.toLowerCase() != "span") {
			parent = parent.parentNode;
		}
		$j(parent).remove(); 
	}

	function addRow(btn) {
		var template = btn.parentNode.getElementsByTagName("span")[0];
		var newRow = $j(template).clone(true);
		$j(newRow).show();
		$j(newRow).insertBefore(btn);
	}

	$j(document).ready(function(){

	});
</script>

<div id="${formFieldName}MultiFieldDiv">
	<c:if test="${!empty initialValues}">
		<c:forEach items="${initialValues}" var="initialVal" varStatus="varStatus">
			<span>
				<openmrs:fieldGen type="${type}" formFieldName="${formFieldName}" val="${initialVal}" parameters="${parameters}"/> 
				<input type="button" value="X" size="1" onclick="removeRow(this);"/></a><br/>
			</span>
		</c:forEach>
	</c:if>
	<div>
		<span id="${formFieldName}Template" style="display:none;">
			<openmrs:fieldGen type="${type}" formFieldName="${formFieldName}" val="" parameters="${parameters}"/> 
			<input type="button" value="X" size="1" onclick="removeRow(this);"/></a><br/>
		</span>
		<input type="button" value="+" size="1" onclick="addRow(this);"/></a><br/>
	</div>
</div>