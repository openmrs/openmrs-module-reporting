/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import org.junit.Ignore;
import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;

import java.util.Collection;
import java.util.Map;

/**
 * Test Calculation for use in unit tests
 */
@Ignore
public class TestPatientCalculation implements PatientCalculation {

	/**
	 * returns UUID or null for each cohort member
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameters, PatientCalculationContext context) {
		PersonService personService = Context.getPersonService();
		CalculationResultMap results = new CalculationResultMap();
		for (Integer personId : cohort) {
			Person p = personService.getPerson(personId);
			results.put(personId, p == null ? new SimpleResult(null, this) : new SimpleResult(p.getUuid(), this));
		}
		return results;
	}

	/**
	 * unused but required method
	 */
	@Override
	public ParameterDefinitionSet getParameterDefinitionSet() {
		return null;
	}
}