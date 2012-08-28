<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="formFieldName" required="true" type="java.lang.String" %>
<%@ attribute name="formFieldValue" required="false" type="java.lang.String" %>

<openmrs:htmlInclude file="/openmrs.js" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/reporting/scripts/cron-editing.js"/>

<script type="text/javascript" charset="utf-8">

	jQuery(document).ready(function() {
	
		// We need to show "Once time scheduling" div for first time by default
		jQuery('.scheduleTypes').each(function() {
			if (jQuery(this).attr('id') != '${id}once' && jQuery("#${id}scheduleExpression").val() == '') {
				jQuery(this).hide();
			}
		});
		
		if (jQuery("#${id}scheduleExpression").val() != '') {
			populateValues(jQuery("#${id}scheduleExpression").val());
			jQuery("#${id}labelScheduleExpression").text(getScheduleDescription(jQuery("#${id}scheduleExpression").val(), '<openmrs:datePattern/>'));
		}
		
		jQuery('#${id}selectScheduleType').change(function(){
			jQuery('div.scheduleTypes').hide();
			jQuery('#${id}' + jQuery(this).val()).show();
		});
		
		// scheduling dialog init (without opening)
		jQuery('#${id}selectScheduleDialog').dialog({
			autoOpen: false,
			resizable: false,
			title:'<spring:message code="reporting.runReport.schedule.dialogTitle"/>',
			modal: true,
			width: 480,
			height: 450,
			overlay: { backgroundColor: "#000", opacity: 0.5 },
			buttons: { '<spring:message code="general.close"/>': function() { jQuery(this).dialog('close'); }, '<spring:message code="general.save"/>': buildExpression },
			close: function(ev, ui) { jQuery(this).hide(); }
		});
		// click event
		jQuery('#${id}showSelectScheduleDialog').click(function(){
			var currentExpression = jQuery("#${id}scheduleExpression").val();
			// if cron expression has been already specified we need  to populate
			// scheduling dialog fileds with it
			if (currentExpression != '') {
				populateValues(currentExpression);
			}
			jQuery('#${id}selectScheduleDialog').dialog('open').css('display','inline');      
		});
		jQuery('#${id}once-scheduleDate').datepicker({ dateFormat : '<openmrs:datePattern />'.replace('yyyy', 'yy')});
	});

	/*
		Builds  cron expression depending on selection type and sets it into
		filed with selector #${id}scheduleExpression
	*/
	function buildExpression () {
		// determine which type of scheduling was selected
		var schedulingType = 'advanced';
		// iterating over each div with types
		jQuery('.scheduleTypes').each(function() {
			if (jQuery(this).css('display') != 'none') {
				schedulingType = jQuery(this).attr('id').substring('${id}'.length);
			}
		});
		// depending on type we forming cron expression and
		// setting it into hidden form field
		var expression = '';
		if (schedulingType == 'advanced') {
			expression = jQuery("#${id}advanced-expression").val();
			if (expression == '') {
				alert('<spring:message code="reporting.cron.pleaseInputValidExpression"/>');
				jQuery("#${id}advanced-expression").focus();
				return;
			} else {
				jQuery("#${id}scheduleExpression").val(expression);
				jQuery("#${id}labelScheduleExpression").html(getScheduleDescription(expression));
			}
		} else if (schedulingType == 'once') {
			var datePattern = '<openmrs:datePattern/>';
			var exactDate = jQuery("#${id}once-scheduleDate").val();
			var exactDateObj = parseDateFromStringToJs(datePattern, exactDate);
			var minute = parseInt(jQuery("#${id}once-minutes").val());
			var hour = parseInt(jQuery("#${id}once-hours").val());
			
			// if date is valid we need to build cron expression 
			// with it and with two another time fields
			if (isValidScheduleDate(exactDateObj, minute, hour)) {
				var date = exactDateObj.getDate();
				var month = exactDateObj.getMonth() + 1;
				var year = exactDateObj.getFullYear();
				// for this type of scheduling we have the next expression pattern
				// 0 m h dom M ? y
				expression = '0 ' + minute + ' ' + hour + ' ' + date + ' ' + month + ' ? ' + year;
				jQuery("#${id}scheduleExpression").val(expression);
				jQuery("#${id}labelScheduleExpression").text(getScheduleDescription(expression, datePattern));
			} else {
				alert('<spring:message code="reporting.runReport.schedule.incorrectDate"/>');
				jQuery("${id}once-scheduleDate").focus();
				return;
			}
		} else if (schedulingType == 'every-day') {
			var minute = parseInt(jQuery("#${id}every-day-minutes").val());
			var hour = parseInt(jQuery("#${id}every-day-hours").val());
			// for this type of scheduling we have the next expression pattern
			// 0 m h * * ?
			expression = '0 ' + minute + ' ' + hour + ' * * ?';
			jQuery("#${id}scheduleExpression").val(expression);
			jQuery("#${id}labelScheduleExpression").text(getScheduleDescription(expression));
		} else if (schedulingType == 'every-week') {
			var dow = getDows();
			if (dow == '') {
				alert('<spring:message code="reporting.runReport.schedule.everyWeek.selectDayOfWeek"/>');
				return;
			}
			var minute = parseInt(jQuery("#${id}every-week-minutes").val());
			var hour = parseInt(jQuery("#${id}every-week-hours").val());
			// for this type of scheduling we have the next expression pattern
			// 0 m h ? * dow
			expression = '0 ' + minute + ' ' + hour + ' ? * ' + dow;
			jQuery("#${id}scheduleExpression").val(expression);
			jQuery("#${id}labelScheduleExpression").text(getScheduleDescription(expression));
		} else if (schedulingType == 'every-month') {
			var dom = jQuery("#${id}every-month-select").val();
			var minute = parseInt(jQuery("#${id}every-month-minutes").val());
			var hour = parseInt(jQuery("#${id}every-month-hours").val());
			// for this type of scheduling we have the next expression pattern
			// 0 m h dom * ?
			expression = '0 ' + minute + ' ' + hour + ' ' + dom + ' * ? ';
			jQuery("#${id}scheduleExpression").val(expression);
			jQuery("#${id}labelScheduleExpression").text(getScheduleDescription(expression));
		} else {
			console.log("Undefined scheduling type found.");
		}
		// hidding span with validation error message on cron 
		// expression and closing scheduling dialog
		if (jQuery("#${id}invalidExpression")) {
			jQuery("#${id}invalidExpression").hide();
		}
		jQuery("#${id}selectScheduleDialog").dialog('close');
	}

	/*
		Get all selected days of week as string separated by comma
	*/
	function getDows() {
		var retval = '';
		for( index = 1; index < 8; index++ ){
		  var dowCheckbox = jQuery("#${id}day-of-week" + index);
		  if (dowCheckbox.is(":checked")) {
			if (retval == '') {
				retval = dowCheckbox.val();
			} else {
				retval = retval + ',' + dowCheckbox.val();
			}
		  }
		}
		return retval;
	}
	
	/*
		Sets values to corresponding day of week check boxes
	*/
	function setDows(dowsTokens) {
		for( index = 1; index < dowsTokens.length; index++ ){
			var dowCheckbox = jQuery("#${id}day-of-week" + dowsTokens[index]);
			dowCheckbox.attr('checked', true);
		}
	}
	
	/*
		Populates values of given expression on schedule dialog fields
	*/
	function populateValues(expression) {
		// first we need to determine the type of expression
		var schedulingType = detectSchedulingType(expression);
		// need to hide all selection type div elements
		// and show that one, that matches detected scheduling type
		jQuery('.scheduleTypes').each(function() {
			jQuery(this).hide();
		});
		// selecting detected scheduling type on dropdown list
		jQuery("#${id}selectScheduleType").val(schedulingType);
		if (schedulingType == 'advanced') {
			jQuery("#${id}advanced-expression").val(expression);
			
			jQuery("#${id}advanced").show();
		} else if (schedulingType == 'once') {
			var tokens = expression.split(' ');
			// selecting values on dropdowns
			jQuery("#${id}once-minutes").val(tokens[1]);
			jQuery("#${id}once-hours").val(tokens[2]);
			
			var exactDate = new Date();
			exactDate.setDate(parseInt(tokens[3]));
			exactDate.setMonth(parseInt(tokens[4]) - 1);
			exactDate.setYear(parseInt(tokens[6]));
			dateString = parseDateFromJsToString('<openmrs:datePattern/>', exactDate);
			
			jQuery("#${id}once-scheduleDate").val(dateString);
			jQuery("#${id}once").show();
		} else if (schedulingType == 'every-day') {
			var tokens = expression.split(' ');
			// getting values from expression (only hours and minutes, actually others are ignored)
			var minutes = tokens[1];
			var hours = tokens[2];
			// selecting values on dropdowns
			jQuery("#${id}every-day-minutes").val(minutes);
			jQuery("#${id}every-day-hours").val(hours);
			jQuery("#${id}every-day").show();
		} else if (schedulingType == 'every-week') {
			var tokens = expression.split(' ');
			// getting values from expression
			var minutes = tokens[1];
			var hours = tokens[2];
			// selecting values on dropdowns
			jQuery("#${id}every-week-minutes").val(minutes);
			jQuery("#${id}every-week-hours").val(hours);
			// getting days of week and picking corresponding check boxes
			var dows = tokens[5];
			setDows(dows.split(','));
			jQuery("#${id}every-week").show();
		} else if (schedulingType == 'every-month') {
			var tokens = expression.split(' ');
			// getting values from expression
			var minutes = tokens[1];
			var hours = tokens[2];
			// selecting values on dropdowns
			jQuery("#${id}every-month-minutes").val(minutes);
			jQuery("#${id}every-month-hours").val(hours);
			// getting days of month and selecting corresponding option within every-month-select
			var dom = tokens[3];
			jQuery("#${id}every-month-select").val(dom);
			jQuery("#${id}every-month").show();
		} else {
			console.log('Undefined scheduling type found.');
		}
	}
</script>

<style>

.cronExpression {
	font-family: trebuchet ms;
	font-style: italic;
	font-size: 14px;
}

#ui-datepicker-div {
    z-index: 10000;
}

.scheduleTypes {
}

</style>

<div id="${id}selectScheduleDialog" style="width:480px; height:520px; display:none;">
	<table>
		<tbody>
			<tr>
				<td style="vertical-align: top; position: relative;">
					<select id="${id}selectScheduleType">
						<option value="once" selected="selected"><spring:message code="reporting.runReport.schedule.once"/></option>
						<option value="every-day"><spring:message code="reporting.runReport.schedule.everyDay"/></option>
						<option value="every-week"><spring:message code="reporting.runReport.schedule.everyWeek"/></option>
						<option value="every-month"><spring:message code="reporting.runReport.schedule.everyMonth"/></option>
						<option value="advanced"><spring:message code="reporting.runReport.schedule.advanced"/></option>
					</select>
				</td>
				<td>
					<div class="scheduleTypes" id="${id}once">
						<table>
							<tr>
								<td style="vertical-align: top; position: relative;">
									<spring:message code="general.onDate"/> 
								</td>
								<td>
									<input id="${id}once-scheduleDate" /><span class="datePatternHint"> (<openmrs:datePattern />)</span>
								</td>
								<td>								
									<spring:message code="reporting.runReport.schedule.atTime"/> 
								</td>
								<td>
									<select id="${id}once-hours">
										<c:forEach var="hour" begin="0" end="23" step="1">
											<option value="${hour}" <c:if test="${hour eq 12 }"> selected="selected" </c:if></option><c:if test="${hour < 10 }">0</c:if>${hour}</option>
										</c:forEach>
									</select>
									:
									<select id="${id}once-minutes">
										<c:forEach var="minute" begin="0" end="59" step="1">
											<option value="${minute}" <c:if test="${minute eq 0 }"> selected="selected" </c:if></option><c:if test="${minute < 10 }">0</c:if>${minute}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table>
					</div>
					<div class="scheduleTypes" id="${id}every-day">
						<table>
							<tr>
								<td>
									<spring:message code="reporting.runReport.schedule.atTime"/> 
								</td>
								<td>
									<select id="${id}every-day-hours">
										<c:forEach var="hour" begin="0" end="23" step="1">
											<option value="${hour}" <c:if test="${hour eq 12 }"> selected="selected" </c:if></option><c:if test="${hour < 10 }">0</c:if>${hour}</option>
										</c:forEach>
									</select>
									:
									<select id="${id}every-day-minutes">
										<c:forEach var="minute" begin="0" end="59" step="1">
											<option value="${minute}" <c:if test="${minute eq 0 }"> selected="selected" </c:if></option><c:if test="${minute < 10 }">0</c:if>${minute}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table> 
					</div>
					<div class="scheduleTypes" id="${id}every-week">
						<table>
							<tr>
								<td style="vertical-align: top; position: relative;">
									<spring:message code="general.onDate"/> 
								</td>
								<td>
									<input type="checkbox" id="${id}day-of-week2" value="2"/><spring:message code="reporting.runReport.schedule.everyWeek.monday"/><br />
									<input type="checkbox" id="${id}day-of-week3" value="3"/><spring:message code="reporting.runReport.schedule.everyWeek.tuesday"/><br />
									<input type="checkbox" id="${id}day-of-week4" value="4"/><spring:message code="reporting.runReport.schedule.everyWeek.wednesday"/><br />
									<input type="checkbox" id="${id}day-of-week5" value="5"/><spring:message code="reporting.runReport.schedule.everyWeek.thursday"/><br />
									<input type="checkbox" id="${id}day-of-week6" value="6"/><spring:message code="reporting.runReport.schedule.everyWeek.friday"/><br />
									<input type="checkbox" id="${id}day-of-week7" value="7"/><spring:message code="reporting.runReport.schedule.everyWeek.saturday"/><br />
									<input type="checkbox" id="${id}day-of-week1" value="1"/><spring:message code="reporting.runReport.schedule.everyWeek.sunday"/><br />
								</td>
								<td style="vertical-align: top; position: relative;">
									<spring:message code="reporting.runReport.schedule.atTime"/> 
								</td>
								<td style="vertical-align: top; position: relative;">
									<select id="${id}every-week-hours">
										<c:forEach var="hour" begin="0" end="23" step="1">
											<option value="${hour}" <c:if test="${hour eq 12 }"> selected="selected" </c:if></option><c:if test="${hour < 10 }">0</c:if>${hour}</option>
										</c:forEach>
									</select>
									:
									<select id="${id}every-week-minutes">
										<c:forEach var="minute" begin="0" end="59" step="1">
											<option value="${minute}" <c:if test="${minute eq 0 }"> selected="selected" </c:if></option><c:if test="${minute < 10 }">0</c:if>${minute}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table>
					</div>
					<div class="scheduleTypes" id="${id}every-month">
						<table>
							<tr>
								<td>
									<spring:message code="general.onDate"/> 
								</td>
								<td>
									<select id="${id}every-month-select">
										<option value="1" selected="selected"><spring:message code="reporting.runReport.schedule.everyMonth.theFirstDay"/></option>
										<option value="L"><spring:message code="reporting.runReport.schedule.everyMonth.theLastDay"/></option>
									</select>
								</td>
								<td>
									<spring:message code="reporting.runReport.schedule.atTime"/> 
								</td>
								<td>
									<select id="${id}every-month-hours">
										<c:forEach var="hour" begin="0" end="23" step="1">
											<option value="${hour}" <c:if test="${hour eq 12 }"> selected="selected" </c:if></option><c:if test="${hour < 10 }">0</c:if>${hour}</option>
										</c:forEach>
									</select>
									:
									<select id="${id}every-month-minutes">
										<c:forEach var="minute" begin="0" end="59" step="1">
											<option value="${minute}" <c:if test="${minute eq 0 }"> selected="selected" </c:if></option><c:if test="${minute < 10 }">0</c:if>${minute}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</table>
					</div>
					<div class="scheduleTypes" id="${id}advanced">
						<input type="text" id="${id}advanced-expression" style="width:95%"> <br/>
						<p>
							<spring:message code="reporting.runReport.schedule.advanced.instrucion"/>
						</p>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</div>

<input type="hidden" id="${id}scheduleExpression" name="${formFieldName}" value="${formFieldValue}">
<input id="${id}showSelectScheduleDialog" type="button" value="<spring:message code="reporting.manageTasks.scheduleDescription"/>">
<span id="${id}labelScheduleExpression" class="cronExpression"><spring:message code="reporting.immediately"/></span>
