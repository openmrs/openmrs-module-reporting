<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<%@ attribute name="id" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="parentType" required="true" %>
<%@ attribute name="parentObj" type="org.openmrs.module.evaluation.parameter.Parameterizable" required="true" %>
<%@ attribute name="mappedProperty" required="true" %>
<%@ attribute name="defaultValue" type="org.openmrs.module.evaluation.parameter.Mapped" required="false" %>
<%@ attribute name="nullValueLabel" required="false" %>
<%@ attribute name="width" required="false" %>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#${id}EditLink').click(function(event){
			showReportingDialog({
				title: '${label}',
				url: 'mappedPropertyEditor.form?parentType=${parentType}&parentUuid=${parentObj.uuid}&mappedProperty=${mappedProperty}',
				successCallback: function() { window.location.reload(true); }
			});
		});
	} );
</script>

<div <c:if test="${width != null}">style="width:${width};"</c:if>">
	<b class="boxHeader" style="font-weight:bold; text-align:right;">
		<span style="float:left;">${label}</span>
		<a style="color:lightyellow;" href="#" id="${id}EditLink">Edit</a>
	</b>
	<div class="box">
		<table>
			<tr><th colspan="3" align="left">
				<c:choose>
					<c:when test="${defaultValue != null}">
						${defaultValue.parameterizable.name}
					</c:when>
					<c:otherwise>
						${nullValueLabel}
					</c:otherwise>
				</c:choose>
			</th></tr>
			<c:forEach items="${defaultValue.parameterizable.parameters}" var="p">
				<tr>
					<td align="right">${p.name}</td>
					<td align="left">--&gt;</td>
					<td align="left" width="100%">
						<c:choose>
							<c:when test="${defaultValue.parameterMappings[p.name] == null}">
								<span style="color:red; font-style:italic;">Undefined</span>
							</c:when>
							<c:otherwise>${defaultValue.parameterMappings[p.name]}</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>