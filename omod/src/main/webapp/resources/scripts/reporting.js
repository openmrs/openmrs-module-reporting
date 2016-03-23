function showError(errorDivId, errorMessage) {
	var errorDiv = document.getElementById(errorDivId);
	if (errorDiv == null) {
		window.alert("Error: " + errorMessage + "\n\n(Cannot find div: " + errorDivId + ")");
	} else {
		if (errorDiv.innerHTML != '')
			errorDiv.innerHTML += ', ' + errorMessage;
		else
			errorDiv.innerHTML = errorMessage;
		showDiv(errorDivId);
	}
}

function clearError(errorDivId) {
	hideDiv(errorDivId);
	var errorDiv = document.getElementById(errorDivId);
	if (errorDiv != null) {
		errorDiv.innerHTML = '';
	}
}

function checkNumber(el, errorDivId, floatOkay, absoluteMin, absoluteMax) {
	clearError(errorDivId);
	
	if (el.value == '') {
		el.className = null;
	}
		
	var errorMessage = verifyNumber(el, floatOkay, absoluteMin, absoluteMax);
	if (errorMessage == null) {
		el.className = 'legalValue';
		clearError(errorDivId);
	} else {
		el.className = 'illegalValue';
		showError(errorDivId, errorMessage);
	}
}

function verifyNumber(el, floatOkay, absoluteMin, absoluteMax) {
	var val = el.value;
	if (val == '')
		return null;

	// TODO replace parse* functions with something that catches 12a.
	if (floatOkay) {
		val = parseFloat(val);
	} else {
		val = parseInt(val);
	}
	
	if (isNaN(val)) {
		if (floatOkay)
			return "Not a number";
		else
			return "Not an integer";
	}
		
	if (absoluteMin != null) {
		if (val < absoluteMin)
			return "Cannot be less than " + absoluteMin;
	}
	if (absoluteMax != null) {
		if (val > absoluteMax)
			return "Cannot be greater than " + absoluteMax;
	}
	return null;
}

function findParentWithClass(element, parentClass) {
	var parent = element.parentNode;
	while (!$j(parent).hasClass(parentClass)) {
		parent = parent.parentNode;
	}
	return parent;
}

function removeParentWithClass(element, parentClass) {
	var parent = findParentWithClass(element, parentClass);
	$j(parent).remove(); 
}

function getClone(idToClone) {
	var template = $j("#"+idToClone);
	var c = $j(template).clone(true);
	$j(c).show();
	return c;
}

function cloneAndInsertBefore(idToClone, elementToAddBefore) {
	var newRow = getClone(idToClone);
	$j(newRow).insertBefore(elementToAddBefore);
	return newRow;
}

////// non-shared dialogs for DIVs ///////////

function makeDialog(divId) {
	$j('#' + divId).dialog({
		autoOpen: false,
		draggable: false,
		resizable: false,
		show: null,
		width: '90%',
		modal: true
	});
}

function showDialog(divId, title) {
	$j('#' + divId).dialog('option', 'title', title).dialog('open');
}





		
////// Support for a single modal dialog for all reporting pages //////////////////

var dialogCurrentlyShown = null;
var reportingDialogSuccessCallback = null;

function showReportingDialog(opts) {
	reportingDialogSuccessCallback = opts.successCallback;
	$j('#reportingDialog')
		.dialog('option', 'title', opts.title)
		.dialog('option', 'height',$j(window).height()-50)
		.dialog('open');
	dialogCurrentlyShown = $j('#reportingDialog');
	$j("#reportingDialog > iframe").attr("src", opts.url);
}

function closeReportingDialog(doCallback) {
	if (dialogCurrentlyShown && dialogCurrentlyShown.length > 0) {
		dialogCurrentlyShown.dialog('close');
		var callMe = reportingDialogSuccessCallback;
		reportingDialogSuccessCallback = null;
		if (doCallback && callMe) {
			callMe.call();
		}
	} else if (window.parent && window.parent != window) {
		window.parent.closeReportingDialog(doCallback);
	}
}

function navigateParent(url) {
	if (dialogCurrentlyShown && dialogCurrentlyShown.length > 0) {
		//dialogCurrentlyShown.dialog('close');
		window.location = url;
	} else if (window.parent && window.parent != window) {
		window.parent.navigateParent(url);
	}
}

function jqUiDecoration() {
	$j(".portlet").addClass("ui-widget ui-corner-all")
		.find(".portlet-header")
			.addClass("ui-widget-header ui-corner-top")
			.end()
		.find(".portlet-content")
			.addClass("ui-widget-content ui-corner-bottom");
}