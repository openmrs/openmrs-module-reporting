package org.openmrs.module.reporting.cohort.definition.evaluator;


import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class InStateCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
	 * 
	 */
	@Test
	@Verifies(value = "should return no patients if none have the given state", method = "evaluate(CohortDefinition,EvaluationContext)")
	public void evaluate_shouldReturnNoPatientsIfNoneHaveTheGivenState() throws Exception {
		InStateCohortDefinition cd = new InStateCohortDefinition();
		List<ProgramWorkflowState> states = Collections.singletonList(Context.getProgramWorkflowService().getStateByUuid("92584cdc-6a20-4c84-a659-e035e45d36b0"));
		cd.setStates(states);
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(0, c.size());
	}

	/**
     * @see {@link InStateCohortDefinitionEvaluator#evaluate(CohortDefinition,EvaluationContext)}
     * 
     */
    @Test
    @Verifies(value = "should return patients in given state on given date", method = "evaluate(CohortDefinition,EvaluationContext)")
    public void evaluate_shouldReturnPatientsInGivenStateOnGivenDate() throws Exception {
    	InStateCohortDefinition cd = new InStateCohortDefinition();
		List<ProgramWorkflowState> states = Collections.singletonList(Context.getProgramWorkflowService().getStateByUuid("e938129e-248a-482a-acea-f85127251472"));
		cd.setStates(states);
		cd.setOnDate(DateUtil.getDateTime(2009, 8, 15));
		Cohort c = Context.getService(CohortDefinitionService.class).evaluate(cd, null);
		Assert.assertEquals(1, c.size());
		Assert.assertTrue(c.contains(2));
    }
}