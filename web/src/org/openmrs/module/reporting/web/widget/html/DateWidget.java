package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.web.widget.WidgetConfig;

public class DateWidget implements Widget {

	/** 
	 * @see Widget#render(WidgetConfig)
	 */
	public void render(WidgetConfig config, Writer w) throws IOException {

		HtmlUtil.renderResource(w, config.getRequest(), "/scripts/calendar/calendar.js");
		
		config.setFixedAttribute("size", "10");
		config.setFixedAttribute("onClick", "showCalendar(this);");
		
		Object v = config.getDefaultValue();
		if (v != null && v instanceof Date) {
			config.setDefaultValue(Context.getDateFormat().format((Date) v));
		}
		TextWidget widget = WidgetFactory.getInstance(TextWidget.class, config);
		widget.render(config, w);
	}
}
