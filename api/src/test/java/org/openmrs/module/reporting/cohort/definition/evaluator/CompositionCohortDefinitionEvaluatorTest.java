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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.GenderCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.DefinitionContext;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class CompositionCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
     * @see {@link CompositionCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     * 
     */
    @Test
    @Verifies(value = "should evaluate a definition with a search that contains an under score or space", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldEvaluateADefinitionWithASearchThatContainsAnUnderScoreOrSpace() throws Exception {
    	GenderCohortDefinition females = new GenderCohortDefinition();
		females.setName("females patients");
		females.setFemaleIncluded(true);
		DefinitionContext.saveDefinition(females);
		
		String query = "select patient_id from patient where voided = 1";
		SqlCohortDefinition voided = new SqlCohortDefinition(query);
		voided.setName("voided patients");
		DefinitionContext.saveDefinition(voided);
		
		Map<String, Mapped<CohortDefinition>> searches = new HashMap<String, Mapped<CohortDefinition>>();
		searches.put("females_patients_only", new Mapped<CohortDefinition>(females, null));
		searches.put("voided patient  only", new Mapped<CohortDefinition>(voided, null));
		
		CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
		compositionCohortDefinition.setName("Voided Female Patients");
		compositionCohortDefinition.setSearches(searches);
		compositionCohortDefinition.setCompositionString("voided patient  only AND females_patients_only");
		
		EvaluationContext context = new EvaluationContext();
		//should not fail
		Context.getService(CohortDefinitionService.class).evaluate(compositionCohortDefinition, context);
	}
}
