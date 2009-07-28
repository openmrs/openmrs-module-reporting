package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * This represents a select list widget
 */
public class SelectWidget extends CodedWidget {
	
	/** 
	 * @see CodedWidget#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {
		Writer w = config.getPageContext().getOut();
		HtmlUtil.renderOpenTag(w, "select", config.getAttributes());
		for (Option option : getOptions()) {
			List<Attribute> atts = new ArrayList<Attribute>();
			atts.add(new Attribute("value", option.getCode(), null, null));
			if (ObjectUtils.equals(option.getValue(), config.getDefaultValue())) {
				atts.add(new Attribute("selected", "true", null, null));
			}
			HtmlUtil.renderOpenTag(w, "option", atts);
			w.write(option.getLabel());
			HtmlUtil.renderCloseTag(w, "option");
		}
		HtmlUtil.renderCloseTag(w, "select");
	}
}