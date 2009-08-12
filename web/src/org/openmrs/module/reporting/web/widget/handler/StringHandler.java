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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.TextWidget;
import org.openmrs.module.reporting.web.widget.html.WidgetFactory;

/**
 * FieldGenHandler for String Types
 */
@Handler(supports={String.class, Character.class}, order=50)
public class StringHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {
		
		TextWidget w = WidgetFactory.getInstance(TextWidget.class, config);
		
		if (config.getType() == Character.class) {
			config.setConfiguredAttribute("size", "2");
			config.setConfiguredAttribute("maxLength", "1");
		}
		else {
			config.setConfiguredAttribute("size", "20");
		}
		w.render(config);
	}

	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> clazz) {
		return input;
	}
}
