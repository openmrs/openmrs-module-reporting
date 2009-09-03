<!-- Wufoo Forms -->
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/structure.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/css/wufoo/form.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/wufoo/wufoo.js"/>


<div id="conceptColumn">
	<form id="datasetColumnForm" name="datasetColumnForm" class="wufoo topLabel" method="post" 
		action="${pageContext.request.contextPath}/module/reporting/datasets/addConceptColumn.form">
	
	
		<input type="hidden" id="id" name="id" value="${dataSetDefinition.id}"/>
		<input type="hidden" id="uuid" name="uuid" value="${dataSetDefinition.uuid}"/>
		<input type="hidden" id="className" name="className" value="${dataSetDefinition.class.name}"/>
	
		<ul>		
			<li>
				<label class="desc" for="conceptId"><spring:message code="reporting.columnValue"/></label>
				<span>				
					<input type="text" name="conceptId" size="30" />
				</span>
			</li>				
			<li>
				<label class="desc" for="columnName"><spring:message code="reporting.columnName"/></label>								
				<span>
					<input type="text" name="columnName" size="30" />
				</span>
			</li>
			
			<!--  
			
				Need to add jquery binding for radio button label
				
					onclick="this.previousSibling.click()"
			
			 -->

			<li>
				<label class="desc" for="type"><spring:message code="reporting.columnModifier"/></label>								
				<div>
					<input class="field radio" type="radio" name="modifier" checked="checked" value="mostRecent"/>
					<label class="choice" for="modifier">
						<spring:message code="reporting.columnModifier.mostRecent"/>
					</label>						
				</div>
				<div>
					<input class="field radio" type="radio" name="modifier" value="first"/>
					<label class="choice" for="modifier">
						<spring:message code="reporting.columnModifier.first"/>
					</label>					
				</div>
				<div>
					<input class="field radio" type="radio" name="modifier" value="firstNum"/>
					<label class="choice" for="modifier">
						<spring:message code="reporting.columnModifier.firstNum"/>						
					</label>
				</div>
				<div>
					<input class="field radio" type="radio" name="modifier" value="mostRecentNum" />
					<label class="choice" for="modifier">
						<spring:message code="reporting.columnModifier.mostRecentNum"/>
						<input class="choice" type="text" name="modifierNum" size="3" />
					</label>					
						
				</div>
			</li>
			<li>
				<label class="desc" for="type"><spring:message code="reporting.conceptExtras"/></label>								
				<div>
					<input type="checkbox" name="extras" value="obsDatetime" class="field radio"/>
					<label class="choice" for="extras">
						<spring:message code="reporting.conceptExtra.obsDatetime"/>
					</label>
				</div>	
				<div>				
					<input type="checkbox" name="extras" value="location"  class="field radio"/>
					<label class="choice" for="extras">
						<spring:message code="reporting.conceptExtra.location"/>
					</label>
				</div>
				<div>
					<input type="checkbox" name="extras" value="comment"  class="field radio"/>
					<label class="choice" for="extras">
						<spring:message code="reporting.conceptExtra.comment"/>
					</label>					
				</div>
				<div>
					<input type="checkbox" name="extras" value="encounterType" class="field radio" />
					<label class="choice" for="extras">
						<spring:message code="reporting.conceptExtra.encounterType"/>
					</label>						
				</div>
				<div>
					<input type="checkbox" name="extras" value="provider" class="field radio" />
					<label class="choice" for="extras">
						<spring:message code="reporting.conceptExtra.provider"/>
					</label>						
				</div>			
			</li>
			<li>
				<div align="center">
					<input id="save-button" type="submit" value="Add"/>
					<input id="cancel-button" type="button" value="Close"/>
				</div>
			</li>
			
			

		</ul>
	</form>

</div>
	