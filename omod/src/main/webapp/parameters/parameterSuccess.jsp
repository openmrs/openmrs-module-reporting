<%@ include file="/WEB-INF/template/include.jsp"%> 
<openmrs:require privilege="Manage Reports" otherwise="/login.htm" redirect="/module/reporting/index.htm" />
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="springform" %>
<%@ include file="../localHeaderMinimal.jsp"%>
<%@ include file="../dialogSupport.jsp"%>

<script type="text/javascript" charset="utf-8">

$(document).ready(function() {
	$('#close-parameter-button').click(function(event){
		closeReportingDialog(false);
	});
});

</script>


<div id="page">
	<div id="container">
		<div align="center">
			Your parameter was created successfully.
			<br/>
			<input id="close-parameter-button" type="button" name="action" value="Close"/>								
		


		</div>		
	</div>	
</div>
	
