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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;

/**
 * FieldGenHandler for Enumerated Types
 */
@Handler(supports = { CohortDefinition.class }, order = 50)
public class CohortDefinitionHandler extends CodedHandler {
	
	/**
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		List<CohortDefinition> l = null;
		String tag = config.getAttributeValue("tag", null);
		if (tag != null) {
			l = Context.getService(CohortDefinitionService.class).getDefinitionsByTag(tag);
		} else {
			l = Context.getService(CohortDefinitionService.class).getAllDefinitions(false);
		}
		for (CohortDefinition d : l) {
			widget.addOption(new Option(d.getUuid(), d.getName(), null, d), config);
		}
	}
	
	/**
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return Context.getService(CohortDefinitionService.class).getDefinitionByUuid(input);
	}
}
