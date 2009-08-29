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
package org.openmrs.module.reporting.web.widget;

import org.openmrs.module.reporting.web.widget.handler.WidgetHandler;
import org.openmrs.util.HandlerUtil;

/**
 * Utility library for Widgets
 */
public class WidgetUtil {

	/**
	 * Return the object value of the passed type, given it's String representation
	 * @param input the String representation
	 * @param type the type to return
	 * @return the Object of the passed type, given the passed input
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T parseInput(String input, Class<? extends T> type) {
		try {
			WidgetHandler handler = HandlerUtil.getPreferredHandler(WidgetHandler.class, type);
			return (T) handler.parse(input, type);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to convert input <" + input + "> to type " + type.getSimpleName(), e);
		}
	}
}