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
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;

@Handler(supports = { CohortIndicator.class }, order = 25)
public class CohortIndicatorHandler extends IndicatorHandler {
	
	/**
	 * @see IndicatorHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		List<Indicator> listOfAllIndicators = null;
		String tag = config.getAttributeValue("tag", null);
		if (tag != null) {
			listOfAllIndicators = Context.getService(IndicatorService.class).getDefinitionsByTag(tag);
		} else {
			listOfAllIndicators = Context.getService(IndicatorService.class).getAllDefinitions(false);
		}
		for (Indicator indicator : listOfAllIndicators) {
			if (indicator instanceof CohortIndicator) {
				widget.addOption(new Option(indicator.getUuid(), indicator.getName(), null, indicator), config);
			}
		}
	}
}
