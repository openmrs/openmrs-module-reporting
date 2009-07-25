package org.openmrs.module.reporting.web.widget.html;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.PageContext;

import org.openmrs.api.context.Context;

public class DateWidget extends TextWidget {

	/** 
	 * @see TextWidget#configureAttributes()
	 */
	@Override
	public void configureAttributes() {
		super.configureAttributes();
    	setAttribute("size", "10");
    	setAttribute("onClick", "showCalendar(this);");
    	setAttribute("value", getDefaultValueString());
	}

	/** 
	 * @see BaseWidget#getValueString()
	 */
	@Override
	public String getDefaultValueString() {
		if (getDefaultValue() != null) {
			if (getDefaultValue() instanceof Date) {
				return Context.getDateFormat().format((Date) getDefaultValue());
			}
		}
		return "";
	}

	/** 
	 * @see TextWidget#render(PageContext)
	 */
	@Override
	public void render(PageContext pageContext) throws IOException {
		List<String> resources = Arrays.asList("/scripts/calendar/calendar.js");
		HtmlUtil.renderResourceFiles(pageContext, resources);
		super.render(pageContext);
	}
}
