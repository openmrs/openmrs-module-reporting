package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;


@Handler(supports={InStateCohortDefinition.class})
public class InStateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * Default constructor
	 */
	public InStateCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return no patients if none have the given state
	 * @should return patients in given state on given date
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		InStateCohortDefinition definition = (InStateCohortDefinition) cohortDefinition;
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		if (definition.getOnDate() != null) {
			onOrAfter = definition.getOnDate();
			onOrBefore = definition.getOnDate();
		}
		Cohort c = Context.getService(CohortQueryService.class).getPatientsInStates(definition.getStates(), onOrAfter, onOrBefore);
		return new EvaluatedCohort(c, cohortDefinition, context);
	}	
}
