/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.reporting.cohort.definition.AgeCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

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

    @Autowired
    TestDataManager tdf;

	@Autowired
	PatientService patientService;

	@Before
	// This is needed due to a change to standardTestDataset in the OpenMRS 2.2 release that changed person 6 birth year from 2007 to 1975
	public void setup() {
		Patient p = patientService.getPatient(6);
		p.setBirthdate(DateUtil.getDateTime(2007, 5, 27));
		patientService.savePatient(p);
	}
	
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

	@Test
    public void evaluate_shouldHandleBoundaryConditionCorrectly() throws Exception {
	    Date birthDate = DateUtil.getDateTime(2002, 1, 1);
	    Date currentDate = DateUtil.getDateTime(2017, 1, 1);
	    Patient p1 = tdf.randomPatient().birthdate(birthDate).save();
	    EvaluationContext context = new EvaluationContext();
	    context.setBaseCohort(new Cohort(Arrays.asList(p1.getPatientId())));
        Cohort children = evaluate(new AgeCohortDefinition(0, 14, currentDate), context);
        Assert.assertEquals(0, children.size());
        Cohort adults = evaluate(new AgeCohortDefinition(15, null, currentDate), context);
        Assert.assertEquals(1, adults.size());
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
		Cohort c = evaluate(acd, null);
		Assert.assertEquals(numPats, c.getSize());
	}

    private Cohort evaluate(AgeCohortDefinition acd, EvaluationContext context) throws EvaluationException {
        return Context.getService(CohortDefinitionService.class).evaluate(acd, context);
    }
}
