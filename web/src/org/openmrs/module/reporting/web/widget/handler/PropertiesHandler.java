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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.TextAreaWidget;
import org.openmrs.module.reporting.web.widget.html.WidgetFactory;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;

/**
 * FieldGenHandler for Properties Types
 */
@Handler(supports={Properties.class}, order=50)
public class PropertiesHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config, Writer w) throws IOException {	
		TextAreaWidget widget = WidgetFactory.getInstance(TextAreaWidget.class, config);
		Properties p = (Properties) config.getDefaultValue();
		if (p != null && !p.isEmpty()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OpenmrsUtil.storeProperties(p, baos, null);
			try {
				config.setDefaultValue(baos.toString("UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unable to load properties from string", e);
			}
		}
		else {
			config.setDefaultValue(null);
		}
		widget.render(config, w);
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		if (StringUtils.hasText(input)) {
			Properties p = new Properties();
			try {
				OpenmrsUtil.loadProperties(p, new ByteArrayInputStream(input.getBytes("UTF-8")));
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unable to load properties from string", e);
			}
			return p;
		}
		return null;
	}
}
