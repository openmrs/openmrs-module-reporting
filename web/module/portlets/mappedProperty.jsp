<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#${model.id}EditLink').click(function(event){
			showReportingDialog({
				title: '${model.label}',
				url: 'mappedPropertyEditor.form?parentType=${model.type}&parentUuid=${model.uuid}&mappedProperty=${model.property}&collectionKey=${model.collectionKey}',
				successCallback: function() { window.location.reload(true); }
			});
		});
	} );
</script>

<div <c:if test="${model.size != null}">style="width:${model.size};"</c:if>>
	<b class="boxHeader" style="font-weight:bold; text-align:right;">
		<span style="float:left;">${model.label}</span>
		<a style="color:lightyellow;" href="#" id="${model.id}EditLink">Edit</a>
	</b>
	<div class="box">
		<table>
			<tr><th colspan="3" align="left">
				<c:choose>
					<c:when test="${model.mappedObj != null}">
						${model.mappedObj.name}
					</c:when>
					<c:otherwise>
						${model.nullValueLabel}
					</c:otherwise>
				</c:choose>
			</th></tr>
			<c:forEach items="${model.mappedObj.parameters}" var="p">
				<tr>
					<td align="right">${p.name}</td>
					<td align="left">--&gt;</td>
					<td align="left" width="100%">
						<c:choose>
							<c:when test="${model.mappings[p.name] == null}">
								<span style="color:red; font-style:italic;">Undefined</span>
							</c:when>
							<c:otherwise>${model.mappings[p.name]}</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>