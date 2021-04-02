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

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Condition;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ConditionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { ConditionCohortDefinition.class })
public class ConditionCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	EvaluationService evaluationService;
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		
		ConditionCohortDefinition cd = (ConditionCohortDefinition) cohortDefinition;
		
		HqlQueryBuilder query = new HqlQueryBuilder();
		query.select("c.patient.patientId")
				.from(Condition.class, "c")
				.wherePatientIn("c.patient.patientId", context)
		        .whereEqual("c.condition.coded", cd.getConditionCoded())
		        .whereEqual("c.condition.nonCoded", cd.getConditionNonCoded())
		        .whereGreaterOrEqualTo("c.dateCreated", cd.getCreatedOnOrAfter())
		        .whereLessOrEqualTo("c.dateCreated", cd.getCreatedOnOrBefore())
		        .whereGreaterOrEqualTo("c.onsetDate", cd.getOnsetDateOnOrAfter())
		        .whereLessOrEqualTo("c.onsetDate", cd.getOnsetDateOnOrBefore())
		        .whereGreaterOrEqualTo("c.endDate", cd.getEndDateOnOrAfter())
		        .whereLessOrEqualTo("c.endDate", cd.getEndDateOnOrBefore())
				.whereGreaterOrEqualTo("c.endDate", cd.getActiveOnDate())
				.whereLessOrEqualTo("c.onsetDate", cd.getActiveOnDate());	
		List<Integer> patientIds = evaluationService.evaluateToList(query, Integer.class, context);
		Cohort cohort = new Cohort(patientIds);
		return new EvaluatedCohort(cohort, cd, context);
	}
}
