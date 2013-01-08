package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports={ProgramEnrollmentCohortDefinition.class})
public class ProgramEnrollmentCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default constructor 
	 */
	public ProgramEnrollmentCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return patients enrolled in the given programs before the given date
     * @should return patients enrolled in the given programs after the given date
	 * @should return patients that completed the given programs before the given date
     * @should return patients that completed the given programs after the given date
	 * @should return patients enrolled in the given programs on the given date if passed in time is at midnight
	 * @should return patients that completed the given programs on the given date if passed in time is at midnight
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		ProgramEnrollmentCohortDefinition definition = (ProgramEnrollmentCohortDefinition) cohortDefinition;
		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingProgramEnrollment(definition.getPrograms(),
			definition.getEnrolledOnOrAfter(),
			definition.getEnrolledOnOrBefore(),
			definition.getCompletedOnOrAfter(),
			definition.getCompletedOnOrBefore());
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
	
}
