/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.web.widget.handler;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.DateWidget;
import org.openmrs.module.reporting.web.widget.html.WidgetFactory;
import org.springframework.util.StringUtils;

/**
 * FieldGenHandler for String Types
 */
@Handler(supports={Date.class}, order=50)
public class DateHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {	
		DateWidget w = WidgetFactory.getInstance(DateWidget.class, config);
		w.render(config);
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		if (StringUtils.hasText(input)) {
			try {
				return Context.getDateFormat().parse(input);
			}
			catch (ParseException e) {
				throw new IllegalArgumentException("Unable to parse input <" + input + "> to a Date");
			}
		}
		return null;
	}
}
