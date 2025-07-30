/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.Cohort;
import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Evaluates an PatientStateCohortDefinition and produces a Cohort
 */
@Handler(supports={PatientStateCohortDefinition.class})
public class PatientStateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	@Autowired
	EvaluationService evaluationService;

	/**
	 * Default Constructor
	 */
	public PatientStateCohortDefinitionEvaluator() {}
	
	/**
     * @see CohortDefinitionEvaluator#evaluate(CohortDefinition, EvaluationContext)
     * @should return patients in the specified states before the start date
     * @should return patients in the specified states after the start date
	 * @should return patients in the specified states before the end date
	 * @should return patients in the specified states after the end date
	 * @should find patients in specified states on the before start date if passed in time is at midnight
	 * @should find patients in specified states on the before end date if passed in time is at midnight
     */
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {

		PatientStateCohortDefinition def = (PatientStateCohortDefinition) cohortDefinition;

		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("ps.patientProgram.patient.patientId");
		qb.from(PatientState.class, "ps");
		qb.whereIn("ps.state", def.getStates());
		qb.whereGreaterOrEqualTo("ps.startDate", def.getStartedOnOrAfter());
		qb.whereLessOrEqualTo("ps.startDate", def.getStartedOnOrBefore());
		qb.whereGreaterOrEqualTo("ps.endDate", def.getEndedOnOrAfter());
		qb.whereLessOrEqualTo("ps.endDate", def.getEndedOnOrBefore());
		qb.whereIn("ps.patientProgram.location", def.getLocationList());
		qb.whereEqual("ps.patientProgram.patient.voided", false);
		qb.wherePatientIn("ps.patientProgram.patient.patientId", context);

		List<Integer> pIds = evaluationService.evaluateToList(qb, Integer.class, context);

    	return new EvaluatedCohort(new Cohort(pIds), cohortDefinition, context);
    }
}