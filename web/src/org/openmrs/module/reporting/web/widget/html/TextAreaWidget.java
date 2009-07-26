package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.PageContext;

/**
 * This represents a single text area widget.
 */
public class TextAreaWidget extends BaseWidget {
    
	/** 
	 * @see BaseWidget#configure()
	 */
	@Override
	public void configure() {
    	setAttribute("cols", "20", true);
    	setAttribute("rows", "2", true);
	}
	
	/** 
	 * @see Widget#render(PageContext)
	 */
	public void render(Writer w) throws IOException {
		HtmlUtil.renderOpenTag(w, "textarea", getAttributes());
		w.write(getDefaultValue() == null ? "" : getDefaultValue().toString());
		HtmlUtil.renderCloseTag(w, "textarea");
	}
}