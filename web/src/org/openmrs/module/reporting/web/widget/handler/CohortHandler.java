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

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.web.widget.WidgetConfig;
import org.openmrs.module.reporting.web.widget.html.CodedWidget;
import org.openmrs.module.reporting.web.widget.html.Option;

/**
 * FieldGenHandler for Cohorts
 */
@Handler(supports={Cohort.class}, order=50)
public class CohortHandler extends CodedHandler {
	
	/** 
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		for (Cohort c : Context.getCohortService().getAllCohorts()) {
			widget.addOption(new Option(c.getUuid(), c.getName(), null, c), config);
		}
	}
	
	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> clazz) {
		return Context.getCohortService().getCohortByUuid(input);
	}
}
