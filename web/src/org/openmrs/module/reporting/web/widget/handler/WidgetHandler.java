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
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.web.widget.WidgetConfig;

/**
 * Base WidgetHandler class.
 */
public abstract class WidgetHandler {
	
	protected static final Log log = LogFactory.getLog(WidgetHandler.class);
	
	/**
	 * This is the main method that should be overridden by subclasses to render the appropriate Widget
	 * @param config
	 * @param out
	 */
	public abstract void render(WidgetConfig config, Writer w) throws IOException;
	
	/**
	 * This is the main method that should be overridden by subclasses to parse an input string to an object
	 * @param input
	 * @param type
	 */
	public abstract Object parse(String input, Class<?> type);
}