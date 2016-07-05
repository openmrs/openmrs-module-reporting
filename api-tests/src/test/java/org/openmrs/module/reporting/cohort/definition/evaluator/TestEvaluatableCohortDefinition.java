package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.EvaluatableCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Defined in its own file because defining it as an inner class in {@link EvaluatableCohortDefinitionEvaluatorTest}
 * throws an internal reporting exception.
 */
public class TestEvaluatableCohortDefinition extends EvaluatableCohortDefinition {

	@Override
	public EvaluatedCohort evaluate(EvaluationContext context) {
		EvaluatedCohort cohort = new EvaluatedCohort(this, context);
		cohort.addMember(7);
		return cohort;
	}

}