package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

/**
 * This represents a select list widget
 */
public class SelectWidget extends CodedWidget {

	/** 
	 * @see BaseWidget#configureAttributes()
	 */
	@Override
	public void configureAttributes() {
	}
	
	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(PageContext pageContext) throws IOException {
		HtmlUtil.renderOpenTag(pageContext, "select", getAttributes());
		for (Option option : getOptions()) {
			List<Attribute> atts = new ArrayList<Attribute>();
			atts.add(new Attribute("value", option.getCode()));
			if (option.getCode().equalsIgnoreCase(getDefaultValueString())) {
				atts.add(new Attribute("selected", "true"));
			}
			HtmlUtil.renderOpenTag(pageContext, "option", atts);
			pageContext.getOut().write(option.getLabel());
			HtmlUtil.renderCloseTag(pageContext, "option");
		}
		HtmlUtil.renderCloseTag(pageContext, "select");
	}
}