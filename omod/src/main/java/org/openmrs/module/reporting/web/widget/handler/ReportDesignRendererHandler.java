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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.util.HandlerUtil;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.reporting.report.renderer.ReportDesignRenderer;
import org.openmrs.module.reporting.report.service.ReportService;

/**
 * FieldGenHandler for Enumerated Types
 */

@Handler(supports={ReportDesignRenderer.class}, order=50)
public class ReportDesignRendererHandler extends CodedHandler {

	/** 
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		for ( ReportDesignRenderer renderer : HandlerUtil.getHandlersForType(ReportDesignRenderer.class, null) ) {
			widget.addOption(new Option(renderer.getClass().getName(), renderer.getLabel(), null, renderer), config);
		}
		
	}

	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return Context.getService(ReportService.class).getReportRenderer(input);
	}
}
