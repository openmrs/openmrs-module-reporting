package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;

import javax.servlet.jsp.PageContext;

/**
 * This represents a single text area widget.
 */
public class TextAreaWidget extends BaseWidget {
    
	/** 
	 * @see BaseWidget#configureAttributes()
	 */
	@Override
	public void configureAttributes() {
    	addAttribute("cols", "20");
    	addAttribute("rows", "2");
	}
	
	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(PageContext pageContext) throws IOException {
		HtmlUtil.renderOpenTag(pageContext, "textarea", getAttributes());
		pageContext.getOut().write(getDefaultValueString());
		HtmlUtil.renderCloseTag(pageContext, "textarea");
	}
}