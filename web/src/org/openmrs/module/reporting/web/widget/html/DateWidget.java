package org.openmrs.module.reporting.web.widget.html;

import java.util.Date;
import org.openmrs.api.context.Context;

public class DateWidget extends TextWidget {

	/** 
	 * @see TextWidget#configureAttributes()
	 */
	@Override
	public void configure() {
		addResource("/scripts/calendar/calendar.js");
    	setAttribute("size", "10", false);
    	setAttribute("onClick", "showCalendar(this);", false);
		Object v = getDefaultValue();
		if (v != null && v instanceof Date) {
			setAttribute("value", Context.getDateFormat().format((Date) v), false);
		}
	}
}
