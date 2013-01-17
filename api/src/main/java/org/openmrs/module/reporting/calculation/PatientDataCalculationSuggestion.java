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
package org.openmrs.module.reporting.calculation;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationRegistration;
import org.openmrs.calculation.CalculationRegistrationSuggestion;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.springframework.stereotype.Component;

/**
 * Provides suggested Patient Data Calculations to consumers
 */
@Component
public class PatientDataCalculationSuggestion implements CalculationRegistrationSuggestion {
	
	/**
	 * @see CalculationRegistrationSuggestion#getName()
	 */
	public String getName() {
		return "Reporting Data Items";
	}
	
	/**
	 * @see org.openmrs.calculation.CalculationRegistrationSuggestion#getSuggestions()
	 */
	public List<CalculationRegistration> getSuggestions() {
		
		List<CalculationRegistration> ret = new ArrayList<CalculationRegistration>();
		String provider = PatientDataCalculationProvider.class.getName();
		
		List<DataDefinition> dataDefinitions = new ArrayList<DataDefinition>();
		for (DataDefinition d : Context.getService(PatientDataService.class).getAllDefinitions(false)) {
			dataDefinitions.add(d);
		}
		for (DataDefinition d : Context.getService(PersonDataService.class).getAllDefinitions(false)) {
			dataDefinitions.add(d);
		}
		for (DataDefinition d : dataDefinitions) {
			ret.add(new CalculationRegistration(d.getName(), provider, d.getName(), null));
		}
		
		if (ret.isEmpty()) {
			ret.add(new CalculationRegistration("gender", provider, GenderDataDefinition.class.getName(), null));
			ret.add(new CalculationRegistration("age", provider, AgeDataDefinition.class.getName(), null));
			ret.add(new CalculationRegistration("birthdate", provider, BirthdateDataDefinition.class.getName(), null));
		}
		
		return ret;
	}
}
