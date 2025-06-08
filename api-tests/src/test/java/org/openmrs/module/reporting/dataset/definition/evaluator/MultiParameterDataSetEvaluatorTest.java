/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiParameterDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	DataSetDefinitionService dataSetDefinitionService;

	@Autowired
	PatientService patientService;

	@Before
	// This is needed due to a change to standardTestDataset in the OpenMRS 2.2 release that changed person 6 birth year from 2007 to 1975
	public void setup() {
		Patient p = patientService.getPatient(6);
		p.setBirthdate(DateUtil.getDateTime(2007, 5, 27));
		patientService.savePatient(p);
	}

	/**
	 * @see {@link org.openmrs.module.reporting.dataset.definition.evaluator.MultiParameterDataSetEvaluator#evaluate(org.openmrs.module.reporting.dataset.definition.DataSetDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a MultiParameterDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAMultiParameterDataSetDefinition() throws Exception {

		SqlDataSetDefinition sqlDataSetDefinition = new SqlDataSetDefinition();
		sqlDataSetDefinition.setSqlQuery("select t.patient_id, p.gender, p.birthdate from patient t inner join person p on t.patient_id = p.person_id where p.birthdate < :maxBirthDate order by patient_id asc");
		sqlDataSetDefinition.addParameter(new Parameter("maxBirthDate", "maxBirthDate", Date.class));

		MultiParameterDataSetDefinition multiParameterDataSetDefinition = new MultiParameterDataSetDefinition();
		multiParameterDataSetDefinition.setBaseDefinition(sqlDataSetDefinition);

		List<Map<String, Object>> iterations = new ArrayList<Map<String, Object>>();

		Map<String, Object> iteration = new HashMap<String, Object>();
		iteration.put("maxBirthDate", "${input}");
		iterations.add(iteration);
		iteration = new HashMap<String, Object>();
		iteration.put("maxBirthDate", "${input+2d}");
		iterations.add(iteration);
		multiParameterDataSetDefinition.setIterations(iterations);
		multiParameterDataSetDefinition.addParameter(new Parameter("input", "input", Date.class));

		Calendar cal = Calendar.getInstance();
		cal.set(1976, Calendar.AUGUST, 24, 0, 0);

		Date firstIterationParameter = cal.getTime();

		cal.add(Calendar.DATE, 2);
		Date secondIterationParameter = cal.getTime();

		EvaluationContext evaluationContext = new EvaluationContext(new Date());
		evaluationContext.addParameterValue("input", firstIterationParameter);

		SimpleDataSet result = (SimpleDataSet) dataSetDefinitionService.evaluate(multiParameterDataSetDefinition, evaluationContext);

		Assert.assertNotNull(result.getMetaData().getColumn("parameter.maxBirthDate"));
		Assert.assertNotNull(result.getMetaData().getColumn("PATIENT_ID"));
		Assert.assertNotNull(result.getMetaData().getColumn("GENDER"));
		Assert.assertNotNull(result.getMetaData().getColumn("BIRTHDATE"));

		Assert.assertEquals(3, result.getRows().size());

		// Asserting result parameter for first iteration
		Assert.assertEquals(firstIterationParameter, result.getColumnValue(1, "parameter.maxBirthDate"));

		// Asserting result parameters for second iteration
		Assert.assertEquals(secondIterationParameter, result.getColumnValue(2, "parameter.maxBirthDate"));
		Assert.assertEquals(secondIterationParameter, result.getColumnValue(3, "parameter.maxBirthDate"));

		Date firstDateResult = (Date) result.getColumnValue(1, "BIRTHDATE");
		Date secondDateResult = (Date) result.getColumnValue(2, "BIRTHDATE");
		Date thirdDateResult = (Date) result.getColumnValue(3, "BIRTHDATE");

		// Asserting evaluation results values; first and second dates are the same - both iteration returns them
		Assert.assertEquals(Timestamp.valueOf("1975-04-08 00:00:00.0"), firstDateResult);
		Assert.assertEquals(Timestamp.valueOf("1975-04-08 00:00:00.0"), secondDateResult);
		Assert.assertEquals(Timestamp.valueOf("1976-08-25 00:00:00.0"), thirdDateResult);

		// Asserting values for first iteration
		Assert.assertTrue(firstDateResult.before(firstIterationParameter));
		Assert.assertFalse(thirdDateResult.before(firstIterationParameter));

		// Asserting values for second iteration
		Assert.assertTrue(secondDateResult.before(secondIterationParameter));
		Assert.assertTrue(thirdDateResult.before(secondIterationParameter));

	}
}
