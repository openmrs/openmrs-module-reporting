/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
			ret.add(new CalculationRegistration("villageName", provider, PreferredAddressDataDefinition.class.getName(),
			        "{cityVillage}"));
		}
		
		return ret;
	}
}
