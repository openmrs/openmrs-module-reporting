<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<%@ include file="/WEB-INF/view/module/reporting/includeScripts.jsp"%>

<c:choose>

	<c:when test="${model.mode == 'edit'}">
		<c:if test="${model.dialog != 'false'}">
			<%@ include file="/WEB-INF/view/module/reporting/localHeaderMinimal.jsp"%>
		</c:if>
		<script type="text/javascript" charset="utf-8">
			$j(document).ready(function() {
		
				$j('#cancelButton').click(function(event){
					<c:choose>
						<c:when test="${model.dialog != 'false'}">
							closeReportingDialog(false);
						</c:when>
						<c:otherwise>
							document.location.href = '${model.cancelUrl}';
						</c:otherwise>
					</c:choose>
				});
		
				$j('#submitButton').click(function(event){
					$j('#baseCohortIndicatorEditorForm').submit();
				});

				$j('#logicSubmitButton').click(function(event){
					$j('#logicFieldForm').submit();
				});
		
			});
		</script>
		<style>
			div.metadataField { display:block; margin:0; padding:6px 5px 9px 9px; clear:both; color:#444; }
			div.metadataField label.desc { line-height:150%; margin:0; padding:0 0 3px 0; border:none; color:#222; display:block; font-weight:bold; }
		</style>
		
		<c:choose>
			<c:when test="${model.subfields == 'logic'}">
				<form id="logicFieldForm" method="post" action="${pageContext.request.contextPath}/module/reporting/indicators/saveLogicCohortIndicator.form">
					<input type="hidden" name="uuid" value="${model.uuid}"/>
					<div style="margin:0; padding:0; width:100%; padd">
						<div class="metadataField">
							<label class="desc" for="name"><spring:message code="reporting.aggregation" /></label>
							<wgt:widget id="aggregator" name="aggregator" object="${model.indicator}" property="aggregator" attributes="displayProperty=name|type=org.openmrs.module.reporting.indicator.aggregation.Aggregator"/>
						</div>
						<div class="metadataField">
							<label class="desc" for="logicExpression"><spring:message code="reporting.logicExpression" /></label>
							<wgt:widget id="logicExpression" name="logicExpression" object="${model.indicator}" property="logicExpression" attributes="cols=40|rows=3"/>
						</div>
					</div>
					<hr style="color:blue;"/>
					<div style="width:100%; text-align:left;">
						<input tabindex="3" type="button" id="logicSubmitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
						<input tabindex="4" type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
					</div>
				</form>
			</c:when>
			<c:otherwise>
				<form id="baseCohortIndicatorEditorForm" method="post" action="${pageContext.request.contextPath}/module/reporting/indicators/saveBaseCohortIndicator.form">
					<input type="hidden" name="uuid" value="${model.uuid}"/>
					<div style="margin:0; padding:0; width:100%; padd">
						<div class="metadataField">
							<label class="desc" for="name"><spring:message code="reporting.name" /></label>
							<input type="text" id="name" tabindex="1" name="name" value="<c:out value='${model.indicator.name}' />" size="50"/>
						</div>
						<div class="metadataField">
							<label class="desc" for="description"><spring:message code="reporting.description" /></label>			
							<textarea id="description" cols="40" rows="10" tabindex="2" name="description"><c:out value="${model.indicator.description}"/></textarea>
						</div>
						<div class="metadataField">
							<label class="desc" for="indicatorType"><spring:message code="reporting.indicatorType" /></label>
							<wgt:widget id="type" name="type" object="${model.indicator}" property="type" format="radio"/>
						</div>
						<c:if test="${empty model.indicator.uuid}">
							<div class="metadataField">
								<label class="desc" for="parameters"><spring:message code="reporting.includeStandardParameters" /></label>
								<c:forEach items="${model.indicator.parameters}" var="p">
									<input type="checkbox" name="parameters" value="${p.name}" checked/> ${p.name}
									&nbsp;&nbsp;&nbsp;
								</c:forEach>
							</div>
						</c:if>
					</div>
					<hr style="color:blue;"/>
					<div style="width:100%; text-align:left;">
						<input tabindex="3" type="button" id="submitButton" class="ui-button ui-state-default ui-corner-all" value="Submit"/>
						<input tabindex="4" type="button" id="cancelButton" class="ui-button ui-state-default ui-corner-all" value="Cancel"/>
					</div>
				</form>
			</c:otherwise>
		</c:choose>
	</c:when>
	
	<c:otherwise>
	
		<script type="text/javascript" charset="utf-8">
			$j(document).ready(function() {
				$j('#${model.id}EditLink').click(function(event){
					showReportingDialog({
						title: '${model.label}',
						url: '<c:url value="/module/reporting/viewPortlet.htm?id=editBaseCohortIndicatorPortlet&url=baseCohortIndicator&parameters.uuid=${model.uuid}&parameters.mode=edit&parameters.subfields=${model.subfields}"/>',
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
				<c:choose>
					<c:when test="${model.subfields == 'logic'}">
						<div style="padding-bottom:5px;">
							<b><spring:message code="reporting.aggregator" />:&nbsp;&nbsp;</b>${model.aggregatorName}
						</div>
						<div style="padding-bottom:5px;">
							<b><spring:message code="reporting.logicExpression" />:&nbsp;&nbsp;</b>${model.indicator.logicExpression}
						</div>
					</c:when>
					<c:otherwise>
						<div style="padding-bottom:5px;">
							<b><spring:message code="reporting.name" />:&nbsp;&nbsp;</b><c:out value="${model.indicator.name}"/>
						</div>
						<div style="padding-bottom:5px;">
							<b><spring:message code="reporting.description" />:&nbsp;&nbsp;</b><c:out value="${model.indicator.description}"/>
						</div>
						<div style="padding-bottom:5px;">
							<b><spring:message code="reporting.indicatorType" />:&nbsp;&nbsp;</b>${model.indicator.type}
						</div>					
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
	</c:otherwise>
	
</c:choose>
