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

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientIdentifierCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * This tests the evaluation of a PatientCharacteristicCohortDefinition
 */
public class PatientIdentifierCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Test
	public void evaluate_shouldEvaluate() throws Exception {
		List<PatientIdentifierType> patientIdentifierTypes = Context.getPatientService().getAllPatientIdentifierTypes();
		PatientIdentifierCohortDefinition patientIdentifierCD = new PatientIdentifierCohortDefinition();
		//patientIdentifierCD.setRegex(regex);
		patientIdentifierCD.setPatientIdentifierTypes(patientIdentifierTypes);		

		// Evaluate cohort definiton
		Cohort baseCohort = Context.getPatientSetService().getAllPatients();
		EvaluationContext evalContext = new EvaluationContext();
		evalContext.setBaseCohort(baseCohort);			
		Cohort cohort = Context.getService(CohortDefinitionService.class).evaluate(patientIdentifierCD, evalContext);
		Assert.assertEquals("Cohort should have 4 members", 4, cohort.getSize());
	}
}
