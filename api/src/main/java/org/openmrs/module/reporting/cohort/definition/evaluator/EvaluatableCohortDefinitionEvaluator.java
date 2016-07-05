package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.EvaluatableCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = EvaluatableCohortDefinition.class)
public class EvaluatableCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		EvaluatableCohortDefinition cd = (EvaluatableCohortDefinition) cohortDefinition;
		return cd.evaluate(context);
	}

}
