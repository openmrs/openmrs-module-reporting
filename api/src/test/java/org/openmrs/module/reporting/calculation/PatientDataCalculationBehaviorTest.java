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

import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.*;
import org.openmrs.module.reporting.common.TestUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientDataCalculationBehaviorTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/";
	
	protected static final String XML_REPORT_TEST_DATASET = "ReportTestDataset";

	private PatientDataCalculationProvider savedProvider = new PatientDataCalculationProvider();
	private PatientDataClasspathCalculationProvider classProvider = new PatientDataClasspathCalculationProvider();
	
	@Before
	public void setup() throws Exception {
		executeDataSet(XML_DATASET_PATH + new TestUtil().getTestDatasetFilename(XML_REPORT_TEST_DATASET));
	}

	@Test
	public void evaluate_shouldEvaluateWithSavedPersonDefinition() throws Exception {
		List<Integer> patientsToTest = Arrays.asList(2, 7);

		GenderDataDefinition gdd = new GenderDataDefinition();
		gdd.setName("Gender");
		gdd = Context.getService(PersonDataService.class).saveDefinition(gdd);

		PatientCalculation c = savedProvider.getCalculation(gdd.getUuid(), null);
		CalculationResultMap m = c.evaluate(patientsToTest, null, null);

		checkResult(m, SimpleResult.class, patientsToTest, "M", "F");
	}

	@Test
	public void evaluate_shouldEvaluateWithClasspathPersonDefinition() throws Exception {
		List<Integer> patientsToTest = Arrays.asList(2, 7);

		PatientCalculation c = classProvider.getCalculation(GenderDataDefinition.class.getName(), null);
		CalculationResultMap m = c.evaluate(patientsToTest, null, null);

		checkResult(m, SimpleResult.class, patientsToTest, "M", "F");
	}

	@Test
	public void evaluate_shouldEvaluateWithSavedPatientDefinition() throws Exception {
		List<Integer> patientsToTest = Arrays.asList(2, 7);

		EncountersForPatientDataDefinition dd = new EncountersForPatientDataDefinition();
		dd.setName("Most Recent Encounter");
		dd.setWhich(TimeQualifier.LAST);
		dd = Context.getService(PatientDataService.class).saveDefinition(dd);

		PatientCalculation c = savedProvider.getCalculation(dd.getUuid(), null);
		CalculationResultMap m = c.evaluate(patientsToTest, null, null);

		Encounter pat2Enc = null;
		Encounter pat7Enc = Context.getEncounterService().getEncounter(5);

		checkResult(m, EncounterResult.class, patientsToTest, pat2Enc, pat7Enc);
	}

	@Test
	public void evaluate_shouldEvaluateWithClasspathPatientDefinition() throws Exception {
		List<Integer> patientsToTest = Arrays.asList(2, 7);

		PatientCalculation c = classProvider.getCalculation(PatientIdDataDefinition.class.getName(), null);
		CalculationResultMap m = c.evaluate(patientsToTest, null, null);

		checkResult(m, SimpleResult.class, patientsToTest, 2, 7);
	}

	@Test
	public void evaluate_shouldEvaluateWithParameters() throws Exception {
		List<Integer> patientsToTest = Arrays.asList(2, 7);

		PatientCalculationContext pcc = Context.getService(PatientCalculationService.class).createCalculationContext();

		PatientCalculation c = classProvider.getCalculation(EncountersForPatientDataDefinition.class.getName(), null);
		Map<String, Object> parameterValues = new HashMap<String, Object>();
		parameterValues.put("which", TimeQualifier.LAST);

		CalculationResultMap m = c.evaluate(patientsToTest, parameterValues, pcc);

		Encounter pat2Enc = null;
		Encounter pat7Enc = Context.getEncounterService().getEncounter(5);
		checkResult(m, EncounterResult.class, patientsToTest, pat2Enc, pat7Enc);

		parameterValues.put("which", TimeQualifier.ANY);

		m = c.evaluate(patientsToTest, parameterValues, pcc);
		CalculationResult result = m.get(7);
		Assert.assertTrue(result instanceof ListResult);
		Assert.assertEquals(3, ((ListResult)result).size());
		Assert.assertNull(m.get(2));
	}

	protected void checkResult(CalculationResultMap m, Class<? extends CalculationResult> clazz, List<Integer> patientsToTest, Object...expectedValues) {
		Assert.assertEquals(patientsToTest.size(), m.size());
		for (int i=0; i<patientsToTest.size(); i++) {
			CalculationResult result = m.get(patientsToTest.get(i));
			if (result != null) {
				Assert.assertEquals(clazz, result.getClass());
				Assert.assertEquals(expectedValues[i], result.getValue());
			}
			else {
				Assert.assertNull(expectedValues[i]);
			}
		}
	}
}
