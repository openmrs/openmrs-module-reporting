package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

public class AjaxAutocompleteWidget extends AutocompleteWidget {

	/** 
	 * @see AutocompleteWidget#renderAutocomplete(Writer, WidgetConfig)
	 */
	@Override
	protected void renderAutocomplete(Writer w, WidgetConfig config) throws IOException {
		String baseUrl = config.getRequest().getContextPath();
		w.write("$textField.autocomplete( '" + baseUrl + config.getAttributeValue("ajaxUrl") + "',{");
		w.write("minChars: " + config.getAttributeValue("minChars", "0") + ",");
		w.write("width: " + config.getAttributeValue("width", "600") + ",");
		w.write("scroll: " + config.getAttributeValue("scroll", "false") + ",");
		w.write("matchContains: " + config.getAttributeValue("matchContains", "true") + ",");
		w.write("autoFill: " + config.getAttributeValue("autoFill", "false") + ",");
		w.write("formatItem: function(row, i, max) { return row[0]; },");
		w.write("formatResult: function(row) { return row[0]; }");
		w.write("});");
		w.write("$textField.result(function(event, data, formatted) { $hiddenField.val(data[1]); });");
	}
}