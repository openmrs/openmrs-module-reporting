/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
