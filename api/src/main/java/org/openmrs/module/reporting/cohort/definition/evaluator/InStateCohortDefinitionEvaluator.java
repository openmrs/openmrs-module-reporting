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
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;


@Handler(supports={InStateCohortDefinition.class})
public class InStateCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should return no patients if none have the given state
	 * @should return patients in given state on given date
	 * @should return patients in the given state on or before the given start date
	 * @should return patients in the given state on or after the given end date
	 * @should find patients in a state on the onOrBefore date if passed in time is at midnight
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		InStateCohortDefinition cd = (InStateCohortDefinition) cohortDefinition;

		Date onOrAfter = cd.getOnDate() != null ? cd.getOnDate() : cd.getOnOrAfter();
		Date onOrBefore = cd.getOnDate() != null ? cd.getOnDate() : cd.getOnOrBefore();

		// By default, return patients who are actively enrolled "now" if no other date constraints are given
		if (onOrAfter == null && onOrBefore == null) {
			onOrAfter = context.getEvaluationDate();
			onOrBefore = context.getEvaluationDate();
		}

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("distinct ps.patientProgram.patient.patientId");
		q.from(PatientState.class, "ps");
		q.wherePatientIn("ps.patientProgram.patient.patientId", context);
		q.whereEqual("ps.patientProgram.patient.voided", false);
		q.whereIn("ps.state", cd.getStates());
		q.whereIn("ps.patientProgram.location", getLocations(cd));
		q.whereLessOrEqualTo("ps.startDate", onOrBefore);
		q.whereGreaterEqualOrNull("ps.endDate", onOrAfter);

		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		return new EvaluatedCohort(new Cohort(pIds), cd, context);
	}

	private List<Location> getLocations(InStateCohortDefinition cd) {
		if (cd.isIncludeChildLocations()) {
			return DefinitionUtil.getAllLocationsAndChildLocations(cd.getLocations());
		}
		return cd.getLocations();
	}
}
