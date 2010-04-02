<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Cohort Definitions" otherwise="/login.htm" redirect="/module/reporting/cohorts/manageCohortDefinitions.form" />
<%@ include file="../manage/localHeader.jsp"%>

<c:set var="pageUrl" value="/module/reporting/cohorts/sqlCohortDefinition.form?uuid=uuid"/>

<style>
input, button { 
	font-size: 0.9em; 
	vertical-align:middle; 
	/*border:none;*/ 
	padding:0px;
	width:100px;
}

textarea { 
	/*border: 1px solid #008000;*/
	border: 1px solid #008000; /* 1px dashed #D1C7AC */
	padding: 2px; 
	font-size: 1.2em;
	font-family: "Courier New";
	border-left: 4px solid #3366FF;
	width: 100%;
} 

#previewBox { 
 background-color: #dcdcdc;	
} 

#editBox { 
	background-color : #99FFCC; 
}

label { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; font-weight:bold; }

label.desc { display:block; }

label.inline { display:inline; }	
img#play { vertical-align: middle; margin: 0; }  



</style>


<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$("#previewButton").click(function(event){ 
			showReportingDialog({ 
				title: 'Preview SQL Cohort Definition', 
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${definition.uuid}&type=${definition.class.name}',
				successCallback: function() { 
					window.location = window.location; //.reload(true);
				} 
			});
			event.preventDefault();			
		});
	});
</script>

<div id="page" style="padding-top: 25px;">
	<div id="container">
		<c:choose>
			<c:when test="${definition.uuid == null}">
		
				<b class="boxHeader">SQL Cohort Query</b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition|size=380|mode=edit|dialog=false|cancelUrl=manageCohortDefinitions.form|successUrl=${pageUrl}" />
				</div>
				
			</c:when>		
			<c:otherwise>
			
				<script>
					$(document).ready(function() {
						makeDialog('saveAsDialog');
						$('#saveAsButton').click(function(event) {
							showDialog('saveAsDialog', 'Save a Copy');
						});
					});
				</script>
		
				<table cellspacing="10" cellpadding="10">
					<tr valign="top">
						<td width="34%">	
			
							<openmrs:portlet 
								id="baseMetadata" 
								url="baseMetadata" 
								moduleId="reporting" 
								parameters="type=${definition.class.name}|uuid=${definition.uuid}|label=Basic Details" />
							
							<openmrs:portlet 
								id="newParameter" 
								url="parameter" 
								moduleId="reporting" 
								parameters="type=${definition.class.name}|uuid=${definition.uuid}|label=Parameters|parentUrl=${pageUrl}" />								
						</td>
						
	
						
						<td width="66%">
							<div style="margin: 0.5em; padding-bottom:0.5em; padding-top:0.5em;"> <!--  .portlet -->
								<b class="boxHeader">SQL Query</b>
								<div class="box" style="padding: 15px; margin: ">

									
									<label class="desc">SQL Query Editor</label>									
									<form method="post" action="sqlCohortDefinitionAssignQueryString.form">
										<input type="hidden" name="uuid" value="${definition.uuid}"/>
										<textarea id="editBox" rows="10" cols="50" id="queryString" name="queryString">${definition.queryDefinition.queryString}</textarea>
										<br/>
										<span>
											<input type="submit" value="Save"/>
											<button id="saveAsButton">Save As ...</button>
											<input id="closeButton" type="button" value="Close" onClick="window.location='manageCohortDefinitions.form';"/>
										</span>
									</form>

									<hr/>

									<label class="desc">SQL Query Preview</label>
									<div>
										<textarea id="previewBox" rows="6" cols="50" disabled="true">${definition.queryDefinition.queryString}</textarea>
										<button id="previewButton"><img id="play" src='<c:url value="/images/play.gif"/>' border="0"/>&nbsp;&nbsp;Preview</button>
									</div>									


								</div>	
							</div>
						</td>
					</tr>
				</table>
				
				<div id="saveAsDialog" style="display:none">
					<form method="get" action="sqlCohortDefinitionClone.form">
						<input type="hidden" name="copyFromUuid" value="${definition.uuid}"/>
						<table>
							<tr>
								<th align="right">Name:</th>
								<td><input type="text" name="name"/></td>
							</tr>
							<tr valign="top">
								<th align="right">Description:</th>
								<td><textarea name="description"></textarea></td>
							</tr>
							<tr>
								<td></td>
								<td><input type="submit" value="Save As"/></td>
							</tr>
						</table>
					</form>
				</div>
		
			</c:otherwise>
		</c:choose>

	</div> <!-- #container -->

</div><!-- #page -->

<%@ include file="/WEB-INF/template/footer.jsp"%>