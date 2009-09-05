package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

public class FileUploadWidget implements Widget {
	
	/** 
	 * @see Widget#render(WidgetConfig)
	 */
	public void render(WidgetConfig config) throws IOException {
		
		HtmlUtil.renderResource(config.getPageContext(), "/moduleResources/reporting/scripts/jquery/jquery-1.3.2.min.js");
		Writer w = config.getPageContext().getOut();
		String id = config.getId();
		
		w.write("<script type=\"text/javascript\" charset=\"utf-8\">" +
				"	function showResourceChange_"+id+"(element, action) {" +
				"		$(element).parent().parent().children('.currentResourceSection').hide();" +
				"		$(element).parent().parent().children('.resourceChangeSection').show();" +
				"	}" +
				"	function hideResourceChange_"+id+"(element) {" +
				"		$(element).parent().parent().children('.currentResourceSection').show();" +
				"		$(element).parent().parent().children('.resourceChangeSection').hide();" +
				"	}" +
				"</script>"
		);
		
		String uploadName = config.getAttributeValue("linkName", null);
		HtmlUtil.renderOpenTag(w, "span", "class=fileUploadWidget");
		
		HtmlUtil.renderOpenTag(w, "span", "class=currentResourceSection" + (config.getDefaultValue() != null ? "" : "|style=display:none;"));
		if (uploadName != null) {
			HtmlUtil.renderOpenTag(w, "a", "href="+config.getAttributeValue("linkUrl", "#"));
			w.write(uploadName);
			HtmlUtil.renderCloseTag(w, "a");
		}
		HtmlUtil.renderSimpleTag(w, "input", "name=foo|type=button|value=Change|onclick=showResourceChange_"+id+"(this, 'show');");
		HtmlUtil.renderCloseTag(w, "span");
		
		HtmlUtil.renderOpenTag(w, "span", "class=resourceChangeSection" + (config.getDefaultValue() == null ? "" : "|style=display:none;"));
		HtmlUtil.renderSimpleTag(w, "input", "type=file|id="+id+"|name="+config.getName());
		if (config.getDefaultValue() != null) {
			HtmlUtil.renderSimpleTag(w, "input", "type=button|value=Cancel|onclick=hideResourceChange_"+id+"(this, 'hide');");
		}
		HtmlUtil.renderCloseTag(w, "span");
		
		HtmlUtil.renderCloseTag(w, "span");
	}
}