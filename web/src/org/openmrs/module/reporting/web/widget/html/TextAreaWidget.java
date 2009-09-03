package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * This represents a single text area widget.
 */
public class TextAreaWidget implements Widget {

	/** 
	 * @see Widget#render(WidgetConfig)
	 */
	public void render(WidgetConfig config) throws IOException {
		Writer w = config.getPageContext().getOut();
		config.setDefaultAttribute("cols", "20");
		config.setDefaultAttribute("rows", "2");
		HtmlUtil.renderOpenTag(w, "textarea", config.getAttributes());
		w.write(config.getDefaultValue() == null ? "" : config.getDefaultValue().toString());
		HtmlUtil.renderCloseTag(w, "textarea");
	}
}