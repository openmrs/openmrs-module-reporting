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
import org.openmrs.module.reporting.web.widget.WidgetTag;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;
import org.openmrs.module.reporting.web.widget.html.RadioWidget;

/**
 * FieldGenHandler for Boolean Types
 */
@Handler(supports={Boolean.class}, order=50)
public class BooleanHandler extends CodedHandler {

	/** 
	 * @see CodedHandler#populateOptions(WidgetTag, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetTag tag, CodedWidget widget) {
		widget.addOption(new Option("t", null, "general.true", Boolean.TRUE));
		widget.addOption(new Option("f", null, "general.false", Boolean.FALSE));
	}

	/**
	 * @see CodedHandler#getDefaultWidget()
	 */
	@Override
	protected Class<? extends CodedWidget> getDefaultWidget() {
		return RadioWidget.class;
	}
}
