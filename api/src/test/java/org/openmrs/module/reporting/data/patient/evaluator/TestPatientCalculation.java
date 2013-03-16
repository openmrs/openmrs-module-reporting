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