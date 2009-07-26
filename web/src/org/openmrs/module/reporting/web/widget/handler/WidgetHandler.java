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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.web.widget.WidgetTag;
import org.openmrs.module.reporting.web.widget.html.CheckboxWidget;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.RadioWidget;
import org.openmrs.module.reporting.web.widget.html.SelectWidget;
import org.openmrs.module.reporting.web.widget.html.Widget;

/**
 * Base WidgetHandler class.
 */
@Handler(supports={Object.class})
public class WidgetHandler {
	
	protected static final Log log = LogFactory.getLog(WidgetHandler.class);
	
	/**
	 * This is the main method that should be overridden by subclasses to render the appropriate Widget
	 * @param tag
	 */
	public void handle(WidgetTag tag) throws IOException {
		String output = "Cannot handle type [" + tag.getField().getType() + "]. Please add a module to handle this type.";
		tag.getPageContext().getOut().write(output);
	}
	
	/**
	 * Factory method for instantiating a new Widget instance
	 */
	public static <T extends Widget> T getWidgetInstance(WidgetTag tag, Class<? extends T> clazz) {
		try {
			T widget = clazz.newInstance();
			widget.setId(tag.getId());
			widget.setName(tag.getName());
			widget.setDefaultValue(tag.getDefaultValue());
			widget.configure(tag.getAttributeMap());
			return widget;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to get instance of Widget class: " + clazz, e);
		}
	}
	
	/**
	 * Utility method to retrieve a CodedWidget based on the given format string
	 */
	public static CodedWidget getCodedWidget(WidgetTag tag, Class<? extends CodedWidget> defaultType) {
		if ("radio".equals(tag.getFormat())) {
			defaultType = RadioWidget.class;
		}
		else if ("checkbox".equals(tag.getFormat())) {
			defaultType = CheckboxWidget.class;
		}
		else if ("select".equals(tag.getFormat()) || defaultType == null) {
			defaultType = SelectWidget.class;
		}
		return getWidgetInstance(tag, defaultType);
	}
}