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

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;



/**
 * FieldGenHandler for Enumerated Types
 */
@Handler(supports = { Indicator.class }, order = 50)
public class IndicatorHandler extends CodedHandler {
	
	/**
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		List<Indicator> l = null;
		String tag = config.getAttributeValue("tag", null);
		if (tag != null) {
			l = Context.getService(IndicatorService.class).getDefinitionsByTag(tag);
		} else {
			l = Context.getService(IndicatorService.class).getAllDefinitions(false);
		}
		for (Indicator d : l) {
			widget.addOption(new Option(d.getUuid(),StringEscapeUtils.escapeHtml(d.getName()), null, d), config);
		}
	}
	
	/**
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return Context.getService(IndicatorService.class).getDefinitionByUuid(input);
	}
}
