package org.openmrs.module.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.cohort.definition.CohortDefinition;
import org.openmrs.module.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.cohort.query.service.CohortQueryService;
import org.openmrs.module.evaluation.EvaluationContext;

@Handler(supports={ProgramEnrollmentCohortDefinition.class})
public class ProgramEnrollmentCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * Default constructor 
	 */
	public ProgramEnrollmentCohortDefinitionEvaluator() {
	}
	
	/**
	 * @see org.openmrs.module.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.cohort.definition.CohortDefinition, org.openmrs.module.evaluation.EvaluationContext)
	 */
	public Cohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		ProgramEnrollmentCohortDefinition definition = (ProgramEnrollmentCohortDefinition) cohortDefinition;
		return Context.getService(CohortQueryService.class).getPatientsHavingProgramEnrollment(definition.getPrograms(),
			definition.getEnrolledOnOrAfter(),
			definition.getEnrolledOnOrBefore(),
			definition.getCompletedOnOrAfter(),
			definition.getCompletedOnOrBefore());
	}
	
}
