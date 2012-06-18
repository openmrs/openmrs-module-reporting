	
	/**
	 * This file contains bunch of functions for sophisticated
	 * cron expressions editing and viewing
	 **/
	
	/*
		Determines if entered date is valid (e.g. matches dd/mm/yyyy format)
		and is not in the past
	*/
	function isValidScheduleDate(dateVal, minutes, hours){
		var isValid = true;
		var scheduleDate = null;
		try{
			// in jquery two letters yy means 4 digit year representation
			scheduleDate = jQuery.datepicker.parseDate('dd/mm/yy', dateVal, null);
		}
		catch(error){
			console.log(error);
			isValid = false;
		}
		
		// if date has valid format we need to check whether it is in future or not 
		if (isValid) {
			var now = new Date();
			scheduleDate.setMinutes(minutes);
			scheduleDate.setHours(hours);
			if (scheduleDate.getTime() < now.getTime()) {
				console.log('The schedule date is in the past');
				isValid = false;
			}
		}
		return isValid;
	}
	
	/*
		Detects type of scheduling considering with given cron expression
	*/
	function detectSchedulingType(expression) {
		var onceRegexp = new RegExp('^0\\s\\d{1,2}\\s\\d{1,2}\\s\\d{1,2}\\s\\d{1,2}\\s[?]\\s\\d{4}');
		if (onceRegexp.test(expression)) {
			return 'once';
		} 
		var everyDayRegexp = new RegExp('^0\\s\\d{1,2}\\s\\d{1,2}\\s[*]\\s[*]\\s[?]');
		if (everyDayRegexp.test(expression)) {
			return 'every-day';
		} 
		var everyWeekRegexp = new RegExp('^0\\s\\d{1,2}\\s\\d{1,2}\\s[?]\\s[*]\\s[1-7*,]*');
		if (everyWeekRegexp.test(expression)) {
			return 'every-week';
		} 
		var everyMonthRegexp = new RegExp('^0\\s\\d{1,2}\\s\\d{1,2}\\s[1|L]\\s[*]\\s[?]');
		if (everyMonthRegexp.test(expression)) {
			return 'every-month';
		}
		return 'advanced';
	}
	
	/*
		Tries to get human readable description of given cron expression. If 
		advanced expression is passed it returns expression without modifications, 
		otherwise it will trying to represent expression depending on its type
	*/
	function getScheduleDescription(expression) {
		var scheduleDescription;
		// first we need to determine the type of expression
		var schedulingType = detectSchedulingType(expression);
		if (schedulingType == 'advanced') {
			scheduleDescription = expression;
		} else if (schedulingType == 'once') {
			var tokens = expression.split(' ');
			// getting values from expression (seconds and day-of-week are ignored)
			var minutes = parseInt(tokens[1]);
			var hours = parseInt(tokens[2]);
			var day = parseInt(tokens[3]);
			var month = parseInt(tokens[4]);
			month++;
			var year = tokens[6];
			// formatting exact date string
			var dateString = prettyFormat(day) + '/' + prettyFormat(month) + '/' + year;
			scheduleDescription = "Once on " + dateString + " at " + prettyFormat(hours) + ":" + prettyFormat(minutes);
		} else if (schedulingType == 'every-day') {
			var tokens = expression.split(' ');
			// getting values from expression (only hours and minutes, actually others are ignored)
			var minutes = parseInt(tokens[1]);
			var hours = parseInt(tokens[2]);
			scheduleDescription = "Every day at " + prettyFormat(hours) + ":" + prettyFormat(minutes);
		} else if (schedulingType == 'every-week') {
			var tokens = expression.split(' ');
			// getting values from expression
			var minutes = parseInt(tokens[1]);
			var hours = parseInt(tokens[2]);
			// getting days of week and picking corresponding check boxes
			var dows = tokens[5];
			var dowsNames = formatDowsNames(dows.split(','));
			scheduleDescription = "Every week on " + dowsNames + " at " + prettyFormat(hours) + ":" + prettyFormat(minutes);
		} else if (schedulingType == 'every-month') {
			var tokens = expression.split(' ');
			// getting values from expression
			var minutes = parseInt(tokens[1]);
			var hours = parseInt(tokens[2]);
			var domName = formatDomName(tokens[3]);
			scheduleDescription = "Every month on " + domName + " at " + prettyFormat(hours) + ":" + prettyFormat(minutes);
		}
		return scheduleDescription;
	}
	
	/*
		Get human readable names of days of weeks
	*/
	function formatDowsNames(dowsTokens) {
		var dowsNames = getDowName(dowsTokens[0]);
		if (dowsTokens.length > 1) {
			for( index = 1; index < dowsTokens.length; index++ ){
				dowsNames = dowsNames + ', ' + getDowName(dowsTokens[index]);
			}
		}
		return dowsNames;
	}
	
	/*
		Get name of week day by given code
	*/
	function getDowName(code) {
		var dowName;
		if (code == 1) {
			dowName = "Sunday";
		} else if (code == 2) {
			dowName = "Monday";
		} else if (code == 3) {
			dowName = "Tuesday";
		} else if (code == 4) {
			dowName = "Wednesday";
		} else if (code == 5) {
			dowName = "Thursday";
		} else if (code == 6) {
			dowName = "Friday";
		} else if (code == 7) {
			dowName = "Saturday";
		} else {
			console.log('Unknown day of week code found.');
			dowName = "Unknown"; 
		}
		return dowName;
	}
	
	/*
		Get human readable equivalent for given day of month cron sub-expression
	*/
	function formatDomName(dom) {
		var domName;
		if (dom == '1') {
			domName = "the first day of month";
		} else if (dom == 'L') {
			domName = "the last day of month";
		}
		return domName;
	}
	
	/*
		Simple function that prepends '0' to beginning of the given val if it's lower then 10 
	*/
	function prettyFormat(val) {
		if (val < 10) {
			return '0' + val;
		} else {
			return val;
		}
	}