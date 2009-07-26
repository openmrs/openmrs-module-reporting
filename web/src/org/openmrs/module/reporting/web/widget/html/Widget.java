package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.PageContext;

/**
 * This represents a single widget on a form.
 */
public interface Widget {
	
    /**
     * Writes the generated HTML for the passed PageContext
     * @param writer
     */
    public void render(PageContext pageContext) throws IOException;
    
	/**
	 * This is the main method which should be implemented to configure the Widget
	 */
    public void configure(Map<String, String> userAttributes);
	
	/**
	 * @param the id of the widget
	 */
	public void setId(String id);
	
	/**
	 * @param the form field name of the widget
	 */
	public void setName(String name);
	
	/**
	 * @param the default value of the widget
	 */
	public void setDefaultValue(Object value);
}