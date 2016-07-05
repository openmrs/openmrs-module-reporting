package org.openmrs.module.reporting.cohort.definition.evaluator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EvaluatableCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluatableCohortDefinitionEvaluatorTest extends BaseModuleContextSensitiveTest {

	@Autowired
	CohortDefinitionService service;

	@Test
	public void evaluate() throws Exception {
		EvaluatableCohortDefinition evaluatableCohortDefinition = new TestEvaluatableCohortDefinition();
		EvaluatedCohort cohort = service.evaluate(evaluatableCohortDefinition, new EvaluationContext());
		assertThat(cohort.size(), is(1));
		assertThat(cohort.getDefinition(), Is.<CohortDefinition>is(evaluatableCohortDefinition));
	}
	
}