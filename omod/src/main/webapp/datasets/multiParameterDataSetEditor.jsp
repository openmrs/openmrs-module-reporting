<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require privilege="Manage Data Set Definitions" otherwise="/login.htm" redirect="/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition" />
<%@ include file="../manage/localHeader.jsp"%>

<c:url value="/module/reporting/datasets/multiParameterDataSetEditor.form" var="pageUrlWithUuid">
	<c:param name="uuid" value="${definition.uuid}" />
</c:url>

<c:set var="pageUrl" value="/module/reporting/datasets/multiParameterDataSetEditor.form?uuid=uuid"/>

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

        $j('#addIterationParameterLink').click(function(event){
            showReportingDialog({
                title: 'Add iteration parameter',
                url: '<c:url value="/module/reporting/viewPortlet.htm?id=addIterationParameterLink&url=multiParameterIterationParameter&parameters.dsdUuid=${definition.uuid}&parameters.mode=edit"/>',
                successCallback: function() { window.location.reload(true); }
            });
        });

        $j("#addIterationLink${indStatus.index}").click(function(event){
            document.location.href='<c:url value="multiParameterAddIteration.form?index=${indStatus.index}&dsdUuid=${definition.uuid}"/>';
        });

        <c:forEach var="iteration" varStatus="iterationStatus" items="${definition.iterations}">
            $j("#removeIterationLink${iterationStatus.index}").click(function(event){
                if (confirm('Please confirm you wish to remove iteration number ${iterationStatus.index}')) {
                    document.location.href='<c:url value="multiParameterRemoveIteration.form?index=${iterationStatus.index}&dsdUuid=${definition.uuid}"/>';
                }
            });

            <c:forEach items="${definition.baseDefinition.parameters}" var="baseDefinitionParam">

                $j("a[id='editIterationParameterLink${iterationStatus.index}${baseDefinitionParam.name}']").click(function(event){
                    showReportingDialog({
                        title: 'Edit parameter ${baseDefinitionParam.name} for iteration ${iterationStatus.index}.',
                        url: '<c:url value="/module/reporting/viewPortlet.htm?id=edutIterationParameterLink&url=multiParameterIterationParameterEdit&iteration=${iterationStatus.index}&paramName=${baseDefinitionParam.name}&parameters.dsdUuid=${definition.uuid}&parameters.mode=edit"/>',
                        successCallback: function() { window.location.reload(true); }
                    });
                });

            </c:forEach>

        </c:forEach>

        $j("#baseDefinitionSelect").change(function() {
            var selectedDefinitionUUID = $j(this).val();
            if (selectedDefinitionUUID == "0") {
                alert("Please, choose dataset definition");
            }
            <c:if test="${definition.baseDefinition != null && !empty definition.iterations}">
                if (confirm('Please confirm you wish to change base dataset. All currently added iteration will be removed.')) {
            </c:if>
                    document.location.href='<c:url value="multiParameterChangeBaseDefinition.form?dsdUuid=${definition.uuid}&baseDefinitionUuid=' + selectedDefinitionUUID + '"/>';
            <c:if test="${definition.baseDefinition != null && !empty definition.iterations}">
                }
            </c:if>
        });

	});
</script>

<div id="page" style="padding-top: 25px;">
	<div id="container">
		<c:choose>
			<c:when test="${definition.id == null}">
		
				<b class="boxHeader"><spring:message code="reporting.MultiParameterDataSetDefinition" /></b>
				<div class="box">
					<openmrs:portlet url="baseMetadata" id="baseMetadata" moduleId="reporting" parameters="type=org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition|size=380|mode=edit|dialog=false|cancelUrl=${pageContext.request.contextPath}/module/reporting/definition/manageDefinitions.form?type=org.openmrs.module.reporting.dataset.definition.DataSetDefinition|successUrl=${pageUrl}" />
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
								parameters="type=${definition['class'].name}|uuid=${definition.uuid}|label=Parameters|parentUrl=${pageUrlWithUuid}" />								
						</td>

						<td width="75%">
						
							<div style="margin: 0.1em; width:100%;">
								<b class="boxHeader"><spring:message code="reporting.iterations" /></b>
								<div class="box">
                                    <div align="center" style="padding:10px;">
                                        <p>
                                            <label><spring:message code="reporting.baseDataset" /></label>
                                            <select id="baseDefinitionSelect">
                                                <option value="0">---<spring:message code="reporting.chooseBaseDef" />---</option>
                                                <c:forEach items="${availableDefinitions}" var="availableDef">
                                                    <option value="${availableDef.uuid}" <c:if test="${availableDef == definition.baseDefinition}">selected</c:if>>${availableDef.name}</option>
                                                </c:forEach>
                                            </select>
                                        </p>
                                        <c:if test="${empty definition.iterations}">
                                            <span><spring:message code="reporting.noIterationsYet" /></span>
                                        </c:if>
                                        <c:if test="${!empty definition.iterations}">
                                            <table id="iterations-table" style="width: 100%">
                                                <thead>
                                                    <tr>
                                                        <th style="text-align:left; border-bottom:1px solid black; white-space:nowrap; width: 50px;"><spring:message code="reporting.iteration" /> #</th>
                                                        <th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.parameters" /></th>
                                                        <th style="text-align:left; border-bottom:1px solid black;"><spring:message code="reporting.actions" /></th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${definition.iterations}" var="iteration" varStatus="iterationStatus">
                                                        <tr>
                                                            <td nowrap style="text-align:center; width:50px;">${iterationStatus.index}</td>
                                                            <td nowrap style="padding-left:5px; padding-right:5px;">
                                                                <table style="width: 100%">
                                                                    <thead>
                                                                        <tr>
                                                                            <th style="width: 30%"><spring:message code="reporting.name" /></th>
                                                                            <th style="width: 70%"><spring:message code="reporting.value" /></th>
                                                                            <th></th>
                                                                        </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                        <c:forEach items="${definition.baseDefinition.parameters}" var="baseDefinitionParam">
                                                                            <tr>
                                                                                <c:choose>
                                                                                    <c:when test="${not empty baseDefinitionParam.label}">
                                                                                        <td>${baseDefinitionParam.label}</td>
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        <td>${baseDefinitionParam.name}</td>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                                <td>${iteration[baseDefinitionParam.name]}</td>
                                                                                <td>
                                                                                    <a href="#" id="editIterationParameterLink${iterationStatus.index}${baseDefinitionParam.name}">
                                                                                        <img src='<c:url value="/images/edit.gif"/>' border="0"/>
                                                                                    </a>
                                                                                </td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                    </tbody>
                                                                </table>
                                                            </td>
                                                            <td nowrap align="center">
                                                                <a href="#" id="removeIterationLink${iterationStatus.index}">
                                                                    <img src='<c:url value="/images/trash.gif"/>' border="0"/>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </c:if>
                                    </div>
                                    <form name="addIterationForm" id="addIterationForm" method="POST" action="">
                                    </form>
                                    <a style="font-weight:bold;" href="#" id="addIterationLink"><spring:message code="reporting.addIteration" /></a>
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
