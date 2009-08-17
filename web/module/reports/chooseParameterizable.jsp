<%@ include file="../include.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		
		$('#testOnePage').change(function(event){
			var currVal = $(this).val();
			if (currVal != '') {
				$("#mapParameterSection").html('<iframe id="mapParameterFrame" src="mapParameters.form?parentType=${parentType.name}&parentUuid=${parentValue.uuid}&childType=${childType.name}&childUuid='+currVal+'" width="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto" />');
			}
			else {
				$("#mapParameterSection").html('');
			}
		});
		$('#testOnePage').trigger('change');
	});
</script>

${childType.simpleName}: <rpt:widget id="testOnePage" name="childUuid" type="${childType.name}" defaultValue="${childValue}"/>
<div id="mapParameterSection"></div>