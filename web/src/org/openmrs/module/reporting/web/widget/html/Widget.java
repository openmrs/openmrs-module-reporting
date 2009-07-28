package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * This represents a single widget on a form.
 */
public interface Widget {
	
    /**
     * Writes the generated HTML for this widget
     * @param config the WidgetConfig
     */
    public void render(WidgetConfig config) throws IOException;
}