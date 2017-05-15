/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
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
		q.whereIn("pp.location", getLocationList(cd));
		q.wherePatientIn("p.patientId", context);

		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		return new EvaluatedCohort(new Cohort(pIds), cohortDefinition, context);
	}

	private List<Location> getLocationList(ProgramEnrollmentCohortDefinition cd) {
		if (cd.isIncludeChildLocations()) {
			return DefinitionUtil.getAllLocationsAndChildLocations(cd.getLocationList());
		}
		return cd.getLocationList();
	}
	
}
