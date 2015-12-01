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
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * This tests the evaluation of an AgeCohortDefinition
 * Patients in standard test dataset:
 * 		2: not voided, birthdate: 1975-04-08
 * 		6: not voided, birthdate: 2007-05-27
 * 		7: not voided, birthdate: 1976-08-25
 *		8: not voided, birthdate: null
 * 		999: voided, birthdate: null
 */
public class AgeCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void evaluate_shouldReturnOnlyPatientsBornOnOrBeforeTheEvaluationDate() throws Exception {
		testAgeRange(3, null, null, false, null, null); // Using the default evaluation date
		testAgeRange(2, null, null, false, "2007-01-01", null); // Using the set evaluation date
	}
	
	@Test
	public void evaluate_shouldReturnOnlyNonVoidedPatients() throws Exception {
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(new AgeCohortDefinition(), null);
		Assert.assertEquals(3, cohort.getSize());
		Assert.assertFalse(cohort.contains(999));
	}
	
	@Test
	public void evaluate_shouldReturnOnlyPatientsInTheGivenAgeRange() throws Exception {
		// Test year calculations
		testAgeRange(1, 0, 5, false, "2009-01-01", null);
		testAgeRange(2, 32, 33, false, "2009-01-01", null);
		testAgeRange(1, 33, null, false, "2009-01-01", null);

		// Test month calculations
		testAgeRange(1, 15, 20, false, "2009-01-01", DurationUnit.MONTHS);
		testAgeRange(1, 23, 23, false, "2009-05-26", DurationUnit.MONTHS);
		testAgeRange(1, 24, 24, false, "2009-05-27", DurationUnit.MONTHS);
	}
	
	@Test
	public void evaluate_shouldOnlyReturnPatientsWithUnknownAgeIfSpecified() throws Exception {
		testAgeRange(3, null, null, false, null, null);
		testAgeRange(4, null, null, true, null, null);
	}

	/**
	 * Private utility method that contains necessary logic for testing various combinations of age calculations
	 * @param numPats
	 * @param minAge
	 * @param maxAge
	 * @param unknown
	 * @param ymdEffectiveDate
	 * @param ageUnits
	 * @throws EvaluationException 
	 */
	private void testAgeRange(int numPats, Integer minAge, Integer maxAge, boolean unknown, String ymdEffectiveDate, DurationUnit ageUnits) throws EvaluationException {
		AgeCohortDefinition acd = new AgeCohortDefinition();
		acd.setMinAge(minAge);
		acd.setMaxAge(maxAge);
		acd.setUnknownAgeIncluded(unknown);
		if (ymdEffectiveDate != null) {
			acd.setEffectiveDate(DateUtil.parseDate(ymdEffectiveDate, "yyyy-MM-dd"));
		}
		if (ageUnits != null) {
			acd.setMinAgeUnit(ageUnits);
			acd.setMaxAgeUnit(ageUnits);
		}
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(acd, null);
		Assert.assertEquals(numPats, c.getSize());
	}	
}
