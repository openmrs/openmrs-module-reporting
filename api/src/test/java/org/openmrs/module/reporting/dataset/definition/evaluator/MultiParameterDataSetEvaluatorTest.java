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

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.MultiParameterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiParameterDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see {@link org.openmrs.module.reporting.dataset.definition.evaluator.MultiParameterDataSetEvaluator#evaluate(org.openmrs.module.reporting.dataset.definition.DataSetDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)}
	 */
	@Test
	@Verifies(value = "should evaluate a MultiParameterDataSetDefinition", method = "evaluate(DataSetDefinition,EvaluationContext)")
	public void evaluate_shouldEvaluateAMultiParameterDataSetDefinition() throws Exception {

		SqlDataSetDefinition sqlDataSetDefinition = new SqlDataSetDefinition();
		sqlDataSetDefinition.setSqlQuery("select t.patient_id, p.gender, p.birthdate from patient t inner join person p on t.patient_id = p.person_id where p.birthdate < :birthdate order by patient_id asc");
		sqlDataSetDefinition.addParameter(new Parameter("birthdate", "birthdate", Date.class));

		MultiParameterDataSetDefinition multiParameterDataSetDefinition = new MultiParameterDataSetDefinition();
		multiParameterDataSetDefinition.setBaseDefinition(sqlDataSetDefinition);

		List<Map<String, Object>> iterations = new ArrayList<Map<String, Object>>();

		Map<String, Object> iteration = new HashMap<String, Object>();
		iteration.put("birthdate", "${input}");
		iterations.add(iteration);
		iteration = new HashMap<String, Object>();
		iteration.put("birthdate", "${input+2d}");
		iterations.add(iteration);
		multiParameterDataSetDefinition.setIterations(iterations);
		multiParameterDataSetDefinition.addParameter(new Parameter("input", "input", Date.class));

		EvaluationContext evaluationContext = new EvaluationContext(new Date());
		evaluationContext.addParameterValue("input", new Date(2013, 01, 01));

		SimpleDataSet result = (SimpleDataSet) Context.getService(DataSetDefinitionService.class).evaluate(multiParameterDataSetDefinition, evaluationContext);

		Assert.assertTrue(result.getMetaData().getColumns().contains(new DataSetColumn("param: birthdate", "param: birthdate", String.class)));
		Assert.assertTrue(result.getMetaData().getColumns().contains(new DataSetColumn("PATIENT_ID", "PATIENT_ID", Integer.class)));
		Assert.assertTrue(result.getMetaData().getColumns().contains(new DataSetColumn("GENDER", "GENDER", String.class)));
		Assert.assertTrue(result.getMetaData().getColumns().contains(new DataSetColumn("BIRTHDATE", "BIRTHDATE", Date.class)));
	}
}