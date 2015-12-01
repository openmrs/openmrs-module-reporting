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
package org.openmrs.module.reporting.dataset.definition.evaluator;

import org.junit.Assert;
import org.junit.Test;
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

		Assert.assertEquals(3, result.getRowMap().size());

		// Asserting result parameter for first iteration
		Assert.assertEquals(firstIterationParameter, result.getRowMap().get(1).getColumnValue("parameter.maxBirthDate"));

		// Asserting result parameters for second iteration
		Assert.assertEquals(secondIterationParameter, result.getRowMap().get(2).getColumnValue("parameter.maxBirthDate"));
		Assert.assertEquals(secondIterationParameter, result.getRowMap().get(3).getColumnValue("parameter.maxBirthDate"));

		Date firstDateResult = (Date) result.getRowMap().get(1).getColumnValue("BIRTHDATE");
		Date secondDateResult = (Date) result.getRowMap().get(2).getColumnValue("BIRTHDATE");
		Date thirdDateResult = (Date) result.getRowMap().get(3).getColumnValue("BIRTHDATE");

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