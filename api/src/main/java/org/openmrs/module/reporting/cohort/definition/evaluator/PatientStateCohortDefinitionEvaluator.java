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
import org.openmrs.PatientState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
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
		qb.whereIn("ps.patientProgram.location", getLocationList(def));
		qb.whereEqual("ps.patientProgram.patient.voided", false);
		qb.wherePatientIn("ps.patientProgram.patient.patientId", context);

		List<Integer> pIds = evaluationService.evaluateToList(qb, Integer.class, context);

    	return new EvaluatedCohort(new Cohort(pIds), cohortDefinition, context);
    }

    private List<Location> getLocationList(PatientStateCohortDefinition def) {
    	if (def.isIncludeChildLocations()) {
    		return DefinitionUtil.getAllLocationsAndChildLocations(def.getLocationList());
		}
		return def.getLocationList();
	}
}