package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;

import javax.servlet.jsp.PageContext;

public class TextWidget extends BaseWidget {

	/** 
	 * @see BaseWidget#configureAttributes()
	 */
	@Override
	public void configureAttributes() {
    	setAttribute("type","text");
    	setAttribute("value", getDefaultValueString());
    	addAttribute("size", "20");
	}

	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(PageContext pageContext) throws IOException {
		HtmlUtil.renderSimpleTag(pageContext, "input", getAttributes());
	}
}