<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<%@ attribute name="id" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="object" type="org.openmrs.module.evaluation.parameter.Parameterizable" required="true" %>
<%@ attribute name="width" required="false" %>

<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#${id}EditLink').click(function(event){
			showReportingDialog({
				title: '${label}',
				url: 'baseParameterizableEditor.form?type=${type}&uuid=${object.uuid}',
				successCallback: function() { window.location.reload(true); }
			});
		});
		if ('${object.uuid}' == '') {
			$('#${id}EditLink').trigger('click');
		}
	} );
</script>

<div <c:if test="${width != null}">style="width:${width};"</c:if>">
	<b class="boxHeader" style="font-weight:bold; text-align:right;">
		<span style="float:left;">${label}</span>
		<a style="color:lightyellow;" href="#" id="${id}EditLink">Edit</a>
	</b>
	<div class="box">
		<div style="padding-bottom:5px;">
			<b>Name:&nbsp;&nbsp;</b>${report.name}
		</div>
		<b>Description:&nbsp;&nbsp;</b>${report.description}
	</div>
</div>