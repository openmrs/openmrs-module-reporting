package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EncounterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;


public class EncounterCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	/**
     * @see {@link EncounterCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     */
    @Test
    @Verifies(value = "should return all patients with encounters if all arguments to cohort definition are null", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnAllPatientsWithEncountersIfAllArgumentsToCohortDefinitionAreNull() throws Exception {
	   EncounterCohortDefinition cd = new EncounterCohortDefinition();
	   Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
	   Assert.assertEquals(1, c.size());
	   Assert.assertTrue(c.contains(7));
    }
	

}
