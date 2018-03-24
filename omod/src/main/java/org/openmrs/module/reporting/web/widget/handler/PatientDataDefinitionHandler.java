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
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;

/**
 * FieldGenHandler for Enumerated Types
 */
@Handler(supports = { PatientDataDefinition.class }, order = 50)
public class PatientDataDefinitionHandler extends CodedHandler {
	
	/**
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		List<PatientDataDefinition> l = null;
		String tag = config.getAttributeValue("tag", null);
		if (tag != null) {
			l = Context.getService(PatientDataService.class).getDefinitionsByTag(tag);
		} else {
			l = Context.getService(PatientDataService.class).getAllDefinitions(false);
		}
		
		for (PatientDataDefinition d : l) {
			widget.addOption(new Option(d.getUuid(), d.getName(), null, d), config);
		}
		
		for (PersonDataDefinition d : Context.getService(PersonDataService.class).getAllDefinitions(false)) {
			
			/*this is to prevent a person data definition to be displayed multiple times into the widget 
			(if it is has been joined, it well be picked up as a patient data definition)*/

			if (!isJoined(d)) {
				widget.addOption(new Option(d.getUuid(), d.getName(), null, d), config);
			}
		}
	}
	
	/**
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		return Context.getService(PatientDataService.class).getDefinitionByUuid(input);
	}
	
	/**
	 * Checks if a Person Data Definition has already been exposed as a Patient Data Definition
	 * 
	 * @param personDataDefintion the person data definition
	 * @return true if the person data definition is joined to a patient data definition, false
	 *         otherwise
	 */
	public boolean isJoined(PersonDataDefinition personDataDefintion) {
		for (PatientDataDefinition d : Context.getService(PatientDataService.class).getAllDefinitions(false)) {
			if (d instanceof PersonToPatientDataDefinition) {
				PersonDataDefinition df = ((PersonToPatientDataDefinition) d).getJoinedDefinition();
				if (personDataDefintion.getUuid() == df.getUuid())
					return true;
			}
		}
		return false;
	}
}
