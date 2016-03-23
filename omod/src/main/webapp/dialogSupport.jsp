<script type="text/javascript">
	$j(document).ready(function() {
		$j('#reportingDialog').dialog({
			autoOpen: false,
			draggable: false,
			resizable: false,
			show: null,
			width: '90%',
			modal: true,
			close: function(event, ui) { dialogCurrentlyShown = null }
		});
	});
</script>

<div id="reportingDialog" style="display: none">
	<iframe width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
</div>