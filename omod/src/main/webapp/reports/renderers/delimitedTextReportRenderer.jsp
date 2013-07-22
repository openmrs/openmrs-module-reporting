<%@ include file="../../manage/localHeader.jsp"%>
<openmrs:require privilege="Manage Report Designs" otherwise="/login.htm" redirect="/module/reporting/reports/manageReportDesigns.form" />



<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {

		$('#cancelButton').click(function(event){
			window.location.href='<c:url value="/module/reporting/reports/manageReportDesigns.form"/>';
		});

		$('#submitButton').click(function(event){
			$('#reportDesignForm').submit();
		});


	});

	function showResourceChange(element) {
		$(element).parent().parent().children('.currentResourceSection').hide();
		$(element).parent().parent().children('.resourceChangeSection').show();
	}
	function hideResourceChange(element) {		
		$(element).parent().parent().children('.currentResourceSection').show();		
		$(element).parent().parent().children('.resourceChangeSection').hide();	
	}
</script>

<style>
	.metadataField { padding-top:5px; border:none; color:#222; display:block; vertical-align:top; font-weight:bold; white-space:nowrap; }
</style>

<form id="reportDesignForm" method="post" action="${pageContext.request.contextPath}/module/reporting/reports/renderers/saveDelimitedTextReportDesign.form">
	<input type="hidden" name="uuid" value="${design.uuid}" />

	<table style="margin:0; padding:0; font-size:small;" padding="5">
		<tr>
			<td valign="top" align="left">
				<span class="metadataField">Name</span>
				<wgt:widget id="name" name="name" object="${design}" property="name" attributes="size=50"/>
				<br/>
				<span class="metadataField">Description</span>			
				<wgt:widget id="description" name="description" object="${design}" property="description" attributes="cols=38|rows=2"/>
				<br/>
				<span class="metadataField">Report Definition</span>
				<c:choose>
					<c:when test="${!empty reportDefinitionUuid}">
						<span style="color:navy;">${design.reportDefinition.name}</span>
						<input type="hidden" name="reportDefinition" value="${reportDefinitionUuid}"/>
					</c:when>
					<c:otherwise>
						<wgt:widget id="reportDefinition" name="reportDefinition" object="${design}" property="reportDefinition" />
					</c:otherwise>
				</c:choose>		
				<br/>

				<span class="metadataField">File Name Extension</span>
				<wgt:widget id="filenameExtension" name="filenameExtension" type="java.lang.String" />
				<span class="metadataField">After Column Delimiter</span>
				<wgt:widget id="afterColumnDelimiter" name="afterColumnDelimiter" type="java.lang.String" />
				<br/>
			</td>
		</tr>
	</table>
	<hr style="color:blue;"/>
	<div style="width:100%; text-align:left;">
		<input type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
		<input type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
	</div>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>