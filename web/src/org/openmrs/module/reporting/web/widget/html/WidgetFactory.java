package org.openmrs.module.reporting.web.widget.html;

import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * Returns an instance of the specified Widget
 */
public class WidgetFactory {
	
	/**
	 * Factory method for instantiating a new Widget instance
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Widget> T getInstance(Class<T> widgetClass, WidgetConfig config) {
		
		if (CodedWidget.class.isAssignableFrom(widgetClass)) {
			if ("radio".equals(config.getFormat())) {
				widgetClass = (Class<T>)RadioWidget.class;
			}
			else if ("checkbox".equals(config.getFormat())) {
				widgetClass = (Class<T>)CheckboxWidget.class;
			}
			else if ("select".equals(config.getFormat())) {
				widgetClass = (Class<T>)SelectWidget.class;
			}
		}
		
		try {
			return widgetClass.newInstance();
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to get instance of Widget class: " + widgetClass, e);
		}
	}
}