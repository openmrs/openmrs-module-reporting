<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="../manage/localHeader.jsp"%>

<c:set var="pageUrl" value="/module/reporting/definition/sqlDefinition.form?type=${type.name}&uuid=uuid"/>

<style>
	input["submit"], button { 
		font-size: 0.9em; 
		vertical-align:middle; 
		/*border:none;*/ 
		padding:0px;
		width:100px;
	}
	input["text"], textarea { 
		margin: 2px;
		padding: 2px; 
		border: 1px solid #008000;
		font-size: 1.2em;
	} 
	textarea#editBox { 
		font-size: 1.2em;
		font-family: "Courier New";
		border: 4px solid #3366FF;
		width: 99%;
		background-color: Khaki; 
	}
	label { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; font-weight:bold; }
	label.desc { display:block; }
	label.inline { display:inline; }	
	img#play { vertical-align: middle; margin: 0; }  
</style>


<script type="text/javascript" charset="utf-8">
	$j(document).ready(function() {
		$j("#previewButton").click(function(event){
			showReportingDialog({ 
				title: 'Preview SQL Definition',
				url: '<c:url value="/module/reporting/parameters/queryParameter.form"/>?uuid=${definition.uuid}&type=${definition['class'].name}',
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
			<c:when test="${definition.id == null}">
		
				<b class="boxHeader"><spring:message code="reporting.sqlQuery" /></b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=${type.name}|size=380|mode=edit|dialog=false|cancelUrl=${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=${type.name}|successUrl=${pageUrl}" />
				</div>
				
			</c:when>		
			<c:otherwise>
		
				<table cellspacing="5" cellpadding="5" width="100%">
					<tr valign="top">
						<td width="25%">	
			
							<openmrs:portlet 
								id="baseMetadata" 
								url="baseMetadata" 
								moduleId="reporting" 
								parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Basic Details" />
							
							<openmrs:portlet 
								id="newParameter" 
								url="parameter" 
								moduleId="reporting" 
								parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Parameters|parentUrl=${pageUrl}" />								
						</td>

						<td width="75%">
						
							<!-- Using .portlet style -->
							<div style="margin: 0.1em; width:100%;"> 
								<b class="boxHeader"><spring:message code="reporting.sqlQuery" /></b>
								<div class="box">
									<form method="post" action="sqlDefinitionAssignQueryString.form">
										<input type="hidden" name="uuid" value="${definition.uuid}"/>
										<input type="hidden" name="type" value="${type.name}"/>
										<textarea id="editBox" rows="18" cols="50" id="queryString" name="queryString">${definition.query}</textarea>
										<br/>
										<span>
											<input type="submit" value="Save"/>
											<input id="closeButton" type="button" value="Close" onClick="window.location='${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=${type.name}';"/>
											<span style="float: right">
												<button id="previewButton"><img id="play" src='<c:url value="/images/play.gif"/>' border="0"/>&nbsp;&nbsp;Preview</button>
											</span>
										</span>
									</form>							
								</div>	
							</div>
						</td>
					</tr>
				</table>
		
			</c:otherwise>
		</c:choose>

	</div> <!-- #container -->

</div><!-- #page -->

<%@ include file="/WEB-INF/template/footer.jsp"%>
