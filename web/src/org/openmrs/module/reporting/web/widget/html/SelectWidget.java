package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

/**
 * This represents a select list widget
 */
public class SelectWidget extends CodedWidget {

	/** 
	 * @see BaseWidget#configure()
	 */
	@Override
	public void configure() { }
	
	/** 
	 * @see Widget#render(Writer)
	 */
	public void render(Writer w) throws IOException {
		HtmlUtil.renderOpenTag(w, "select", getAttributes());
		for (Option option : getOptions()) {
			List<Attribute> atts = new ArrayList<Attribute>();
			atts.add(new Attribute("value", option.getCode()));
			if (ObjectUtils.equals(option.getValue(), getDefaultValue())) {
				atts.add(new Attribute("selected", "true"));
			}
			HtmlUtil.renderOpenTag(w, "option", atts);
			w.write(option.getLabel());
			HtmlUtil.renderCloseTag(w, "option");
		}
		HtmlUtil.renderCloseTag(w, "select");
	}
}