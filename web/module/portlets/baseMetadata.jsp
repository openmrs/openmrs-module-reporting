<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#${model.id}EditLink').click(function(event){
			showReportingDialog({
				title: '${model.label}',
				url: 'baseParameterizableEditor.form?type=${model.type}&uuid=${model.uuid}',
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
		<div style="padding-bottom:5px;">
			<b>Name:&nbsp;&nbsp;</b>${model.obj.name}
		</div>
		<b>Description:&nbsp;&nbsp;</b>${model.obj.description}
	</div>
</div>
