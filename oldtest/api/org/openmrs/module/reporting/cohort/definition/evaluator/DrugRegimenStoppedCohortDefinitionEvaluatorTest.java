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

import java.util.Calendar;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.cohort.definition.toreview.FormCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * This tests the evaluation of a PatientCharacteristicCohortDefinition
 */
public class DrugRegimenStoppedCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	@Test
	public void evaluate_shouldReturnPatientsWhoHadFormCompletedOnDate() throws Exception {
		FormCohortDefinition fcd = new FormCohortDefinition();
		fcd.setForms(Context.getFormService().getAllForms());
		
		Calendar newYears = Calendar.getInstance();
		newYears.set(2009, 0, 1);
		
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.addParameterValue("sinceDate", newYears.getTime());
		evalContext.addParameterValue("untilDate", newYears.getTime());		
		
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(fcd, evalContext);

		Assert.assertEquals(1, cohort.getSize());
	}
}
