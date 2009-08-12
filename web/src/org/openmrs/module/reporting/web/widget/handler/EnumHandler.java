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

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;

/**
 * FieldGenHandler for Enumerated Types
 */
@Handler(supports={Enum.class}, order=50)
public class EnumHandler extends CodedHandler {

	/** 
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		Class<?> c = config.getType();
		Object[] enums = c.getEnumConstants();
		if (enums != null) {
			for (Object o : enums) {
				widget.addOption(new Option(o.toString(), o.toString(), null, o), config);
			}
		}
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> clazz) {
		if (input != null) {
			Object[] enums = clazz.getEnumConstants();
			if (enums != null) {
				for (Object o : enums) {
					if (input.equals(o.toString())) {
						return o;
					}
				}
			}
		}
		return null;
	}
}
