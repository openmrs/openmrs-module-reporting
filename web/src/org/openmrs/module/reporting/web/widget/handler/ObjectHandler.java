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

/**
 * FieldGenHandler for String Types
 */
@Handler(supports={Object.class}, order=100)
public class ObjectHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config) throws IOException {
		config.getPageContext().getOut().write("There is no handler for type: " + config.getType().getSimpleName());
	}

	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return input;
	}
}
