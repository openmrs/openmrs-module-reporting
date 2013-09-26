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
