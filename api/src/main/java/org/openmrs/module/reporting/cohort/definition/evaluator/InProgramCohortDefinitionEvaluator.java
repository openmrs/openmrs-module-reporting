package org.openmrs.module.reporting.cohort.definition.evaluator;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports={InProgramCohortDefinition.class})
public class InProgramCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default constructor
	 */
	public InProgramCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return patients enrolled in the given programs on or before the given date
	 * @should return patients enrolled in the given programs on or after the given date
	 * @should find patients in a program on the onOrBefore date if passed in time is at midnight
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		InProgramCohortDefinition definition = (InProgramCohortDefinition) cohortDefinition;
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		if (definition.getOnDate() != null) {
			onOrAfter = definition.getOnDate();
			onOrBefore = definition.getOnDate();
		}
		Cohort c = Context.getService(CohortQueryService.class).getPatientsInProgram(definition.getPrograms(), onOrAfter, onOrBefore);
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
