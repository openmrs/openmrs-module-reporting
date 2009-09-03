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

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;
import org.openmrs.module.reporting.web.widget.html.TextWidget;
import org.openmrs.module.reporting.web.widget.html.WidgetFactory;

/**
 * FieldGenHandler for Enumerated Types
 */
@Handler(supports={Class.class}, order=50)
public class ClassHandler extends CodedHandler {
	
	/** 
	 * @see CodedHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {
		if (StringUtils.isNotEmpty(config.getAttributeValue("type"))) {
			super.render(config);
		}
		else {
			TextWidget w = WidgetFactory.getInstance(TextWidget.class, config);
			config.setConfiguredAttribute("size", "60");
			if (config.getDefaultValue() != null) {
				Class<?> clazz = (Class<?>) config.getDefaultValue();
				config.setDefaultValue(clazz.getName());
			}
			w.render(config);
		}
	}

	/** 
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		String type = config.getAttributeValue("type");
		if (StringUtils.isNotEmpty(type)) {
			try {
				Class<?> clazz = Context.loadClass(type);
				for (Object o : Context.getRegisteredComponents(clazz)) {
					Class<?> c = o.getClass();
					String simple = config.getAttributeValue("simple", "false");
					String display = "true".equals(simple) ? c.getSimpleName() : c.getName();
					widget.addOption(new Option(c.getName(), display, null, c), config);
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Error rendering widget for class property of type <" + type + ">", e);
			}
		}
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		try {
			return Context.loadClass(input);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load class <" + input + ">", e);
		}
	}
}
