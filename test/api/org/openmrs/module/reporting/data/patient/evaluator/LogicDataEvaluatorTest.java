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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.LogicDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = {"classpath:org/openmrs/module/reporting/logic/logicServiceContext.xml"}, inheritLocations = true)
public class LogicDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see LogicDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return Logic Results for all patients in the context baseCohort
	 */
	@Test
	public void evaluate_shouldReturnLogicResultsForAllPatientsInTheContextBaseCohort() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("2,6,7,8"));
		
		LogicDataDefinition d = new LogicDataDefinition();
		d.setLogicQuery("gender");
		
		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(4, pd.getData().size());
		Assert.assertEquals("M", pd.getData().get(2));
		Assert.assertEquals("M", pd.getData().get(6));
		Assert.assertEquals("F", pd.getData().get(7));
		Assert.assertEquals("F", pd.getData().get(8));
	}
}