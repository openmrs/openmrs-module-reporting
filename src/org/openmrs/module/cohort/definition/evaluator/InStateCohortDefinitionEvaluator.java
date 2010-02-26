package org.openmrs.module.cohort.definition.evaluator;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.cohort.query.service.CohortQueryService;
import org.openmrs.module.evaluation.EvaluationContext;


@Handler(supports={InStateCohortDefinition.class})
public class InStateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default constructor
	 */
	public InStateCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see org.openmrs.module.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.cohort.definition.CohortDefinition, org.openmrs.module.evaluation.EvaluationContext)
	 */
	public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		InStateCohortDefinition definition = (InStateCohortDefinition) cohortDefinition;
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		if (definition.getOnDate() != null) {
			onOrAfter = definition.getOnDate();
			onOrBefore = definition.getOnDate();
		}
		return Context.getService(CohortQueryService.class).getPatientsInStates(definition.getStates(), onOrAfter, onOrBefore);
	}	
}
