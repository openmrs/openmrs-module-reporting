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

import org.openmrs.module.reporting.web.widget.WidgetTag;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;
import org.openmrs.module.reporting.web.widget.html.SelectWidget;

/**
 * FieldGenHandler for Coded Types
 */
public abstract class CodedHandler extends WidgetHandler {
	
	/**
	 * Provides a means for specifying the default Widget
	 */
	protected Class<? extends CodedWidget> getDefaultWidget() {
		return SelectWidget.class;
	}

	/** 
	 * @see WidgetHandler#handle(WidgetTag)
	 */
	@Override
	public void handle(WidgetTag tag) throws IOException {
		CodedWidget w = WidgetHandler.getCodedWidget(tag, getDefaultWidget());
		if ("true".equals(tag.getAttribute("showEmptyOption", ((w instanceof SelectWidget) ? "true" : "")))) {
			String emptyCode = tag.getAttribute("emptyCode", null);
			String emptyLabel = tag.getAttribute("emptyLabel", "");
			w.addOption(new Option("", emptyLabel, emptyCode, null));
		}
		populateOptions(tag, w);
		w.render(tag.getPageContext());
	}
	
	/**
	 * Subclasses should define the Coded Options here
	 * @return
	 */
	public abstract void populateOptions(WidgetTag tag, CodedWidget widget);
}
