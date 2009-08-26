package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

public class AutocompleteWidget extends CodedWidget {
	
	/**
	 * Default rendering of the Autocomplete widget, does not use Ajax
	 * @param w the Writer
	 * @param config the WidgetConfig
	 * @throws IOException
	 */
	protected void renderAutocomplete(Writer w, WidgetConfig config) throws IOException {
		w.write("$textField.autocomplete( [");
		for (Iterator<Option> i = getOptions().iterator(); i.hasNext();) {
			Option o = i.next();
			w.write("{");
			w.write("code: \"" + o.getCode() + "\"");
			w.write(", label: \"" + o.getLabel() + "\"");
			w.write("}");
			if (i.hasNext()) {
				w.write(",");
			}

		}
		w.write("],{");
		w.write("minChars: " + config.getAttributeValue("minChars", "0") + ",");
		w.write("width: " + config.getAttributeValue("width", "600") + ",");
		w.write("scroll: " + config.getAttributeValue("scroll", "false") + ",");
		w.write("matchContains: " + config.getAttributeValue("matchContains", "true") + ",");
		w.write("autoFill: " + config.getAttributeValue("autoFill", "false") + ",");
		w.write("formatItem: function(row, i, max) { return row.label; },");
		w.write("formatMatch: function(row, i, max) { return row.label; },");
		w.write("formatResult: function(row) { return row.label; }");
		w.write("} );");
		w.write("$textField.result(function(event, data, formatted) { $hiddenField.val(data.code); });");
	}
	
	/** 
	 * @see Widget#render(WidgetConfig)
	 */
	public void render(WidgetConfig config) throws IOException {
		
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/jquery/jquery-1.3.2.min.js");
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/jquery/autocomplete/css/jquery.autocomplete.css");
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/jquery/autocomplete/jquery.autocomplete.js");
		Writer w = config.getPageContext().getOut();
		String id = config.getId();

		
		w.write("<script type=\"text/javascript\" charset=\"utf-8\">" +
			"		function setupAutocomplete_"+id+"(element) {" +
			"			var $hiddenField = $(element).parent().children('.autoCompleteHidden');" +
			"			var $textField = $(element).parent().children('.autoCompleteText');" +
			"			if ($hiddenField.length > 0 && $textField.length > 0) {");
							renderAutocomplete(w, config);
		w.write("		} ");
		w.write("	};");
		w.write("</script>");
		
		String size = config.getAttributeValue("size", "40");
		Option selected = getSelectedOption(config);
		String valAtt = selected == null ? "" : "|value="+selected.getCode();
		String labelAtt = selected == null ? "" : "|value="+selected.getLabel();
		
		HtmlUtil.renderOpenTag(w, "span", "class=autoCompleteSection");
		HtmlUtil.renderSimpleTag(w, "input", "id="+id+"HiddenField|class=autoCompleteHidden|name="+config.getName()+"|type=hidden"+valAtt);
		HtmlUtil.renderSimpleTag(w, "input", "id="+id+"TextField|size="+size+"|type=text|class=autoCompleteText|onfocus=setupAutocomplete_"+id+"(this);"+labelAtt);
		HtmlUtil.renderCloseTag(w, "span");
	}
}