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
	public void render(WidgetConfig config, Writer w) throws IOException {
		
		// Open Select Tag
		HtmlUtil.renderOpenTag(w, "select", config.getAttributes());
		
		Label currentGroup = null;
		boolean inGroup = false;
		
		for (Option option : getOptions()) {
			
			// Open New Option Group if Appropriate
			if (option.getGroup() != null) {
				if (!option.getGroup().equals(currentGroup)) {
					currentGroup = option.getGroup();
					if (inGroup) {
						HtmlUtil.renderCloseTag(w, "optgroup");
					}
					List<Attribute> atts = new ArrayList<Attribute>();
					atts.add(new Attribute("label", currentGroup.getLabel(), null, null));
					HtmlUtil.renderOpenTag(w, "optgroup", atts);
				}
				inGroup = true;
			}
			else {
				inGroup = false;
			}
			
			// Render Option
			List<Attribute> atts = new ArrayList<Attribute>();
			atts.add(new Attribute("value", option.getCode(), null, null));
			if (ObjectUtils.equals(option.getValue(), config.getDefaultValue())) {
				atts.add(new Attribute("selected", "true", null, null));
			}
			HtmlUtil.renderOpenTag(w, "option", atts);
			w.write(option.getLabel());
			HtmlUtil.renderCloseTag(w, "option");
			
		}
		
		// Close Last Option Group if Appropriate
		if (inGroup) {
			HtmlUtil.renderCloseTag(w, "optgroup");
		}
		HtmlUtil.renderCloseTag(w, "select");
	}
}