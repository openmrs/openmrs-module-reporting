package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports={ProgramEnrollmentCohortDefinition.class})
public class ProgramEnrollmentCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/**
	 * Default constructor 
	 */
	public ProgramEnrollmentCohortDefinitionEvaluator() { }
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return patients enrolled in the given programs before the given date
     * @should return patients enrolled in the given programs after the given date
	 * @should return patients that completed the given programs before the given date
     * @should return patients that completed the given programs after the given date
	 * @should return patients enrolled in the given programs on the given date if passed in time is at midnight
	 * @should return patients that completed the given programs on the given date if passed in time is at midnight
	 * @should return patients enrolled at the given locations
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {

		ProgramEnrollmentCohortDefinition cd = (ProgramEnrollmentCohortDefinition) cohortDefinition;

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("distinct p.patientId");
		q.from(PatientProgram.class, "pp");
		q.innerJoin("pp.patient", "p");
		q.whereEqual("p.voided", false);
		q.whereIn("pp.program", cd.getPrograms());
		q.whereGreaterOrEqualTo("pp.dateEnrolled", cd.getEnrolledOnOrAfter());
		q.whereLessOrEqualTo("pp.dateEnrolled", cd.getEnrolledOnOrBefore());
		q.whereGreaterOrEqualTo("pp.dateCompleted", cd.getCompletedOnOrAfter());
		q.whereLessOrEqualTo("pp.dateCompleted", cd.getCompletedOnOrBefore());
		q.whereIn("pp.location", cd.getLocationList());
		q.wherePatientIn("p.patientId", context);

		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		return new EvaluatedCohort(new Cohort(pIds), cohortDefinition, context);
	}
	
}
