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
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationRegistration;
import org.openmrs.calculation.api.CalculationRegistrationService;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;

/**
 * FieldGenHandler for Calculations
 */
@Handler(supports = {CalculationRegistration.class}, order = 50)
public class CalculationRegistrationHandler extends CodedHandler {

	/**
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig widgetConfig, CodedWidget codedWidget) {
		for (CalculationRegistration registration : Context.getService(CalculationRegistrationService.class).getAllCalculationRegistrations()) {
			codedWidget.addOption(new Option(registration.getUuid(), registration.getToken(), null, registration), widgetConfig);
		}
	}

	/**
	 * @see org.openmrs.module.htmlwidgets.web.handler.WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String s, Class<?> aClass) {
		CalculationRegistration registration = Context.getService(CalculationRegistrationService.class).getCalculationRegistrationByUuid(s);
		return registration;
	}
}
