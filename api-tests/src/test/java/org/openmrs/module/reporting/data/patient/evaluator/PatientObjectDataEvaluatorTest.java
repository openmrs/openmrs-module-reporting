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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientObjectDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import java.util.Map;

public class PatientObjectDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";
	
	/**
	 * Run this before each unit test in this class. The "@BeforeEach" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}
	
	/**
	 * @see PatientIdDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 */
	@Test
	public void evaluate_shouldReturnPatientIdsForAllPatientsInTheThePassedContext() throws Exception {
		
		// Test for all patients
		PatientObjectDataDefinition d = new PatientObjectDataDefinition();
		EvaluationContext context = new EvaluationContext();
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assertions.assertEquals(9, pd.getData().size());
        for (Map.Entry<Integer, Object> e : pd.getData().entrySet()) {
            Assertions.assertTrue(e.getValue() instanceof Patient);
            Assertions.assertEquals(e.getKey(), ((Patient)e.getValue()).getPatientId());
        }

        // Test for a limited base cohort of patients
		context.setBaseCohort(new Cohort("2,6,7,8"));
		pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assertions.assertEquals(4, pd.getData().size());
	}
}