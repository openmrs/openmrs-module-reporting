/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.openmrs.annotation.Handler;

import java.util.List;
import org.openmrs.Cohort;
import org.openmrs.DrugOrder;
import org.openmrs.Order.Action;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.StartedDrugOrderCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;

@Handler(supports = { StartedDrugOrderCohortDefinition.class })
public class StartedDrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	EvaluationService evaluationService;
	
	public StartedDrugOrderCohortDefinitionEvaluator() {
	}
	
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		StartedDrugOrderCohortDefinition cd = (StartedDrugOrderCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());
		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("do.patient.patientId");
		q.from(DrugOrder.class, "do");
		q.wherePatientIn("do.patient.patientId", context);
		q.whereEqual("do.action", Action.NEW);
		q.whereNull("do.previousOrder");
		
		if (cd.getActiveOnOrBefore() != null) {
			q.whereLessOrEqualTo("do.dateActivated", cd.getActiveOnOrBefore());
			q.whereGreaterOrNull("do.dateStopped", cd.getActiveOnOrBefore());
		}
		if (cd.getActiveOnOrAfter() != null) {
			q.whereNotNull("do.dateActivated");
			q.whereGreaterEqualOrNull("do.dateStopped", cd.getActiveOnOrAfter());
		}
		if (cd.getStartedOnOrAfter() != null) {
			q.whereGreaterOrEqualTo("do.dateStopped", cd.getStartedOnOrAfter());
		}
		if (cd.getStartedOnOrBefore() != null) {
			q.whereLessOrEqualTo("do.dateStopped", cd.getStartedOnOrBefore());
		}
		
		if (cd.getDrugSet() != null && cd.getDrugSet().size() > 0) {
			q.whereInAny("do.drug", cd.getDrugSet().toArray());
		}
		
		if (cd.getGenericDrugList() != null && cd.getGenericDrugList().size() > 0) {
			q.whereInAny("do.drug", cd.getGenericDrugList().toArray());
		}
		
		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		Cohort c = new Cohort(pIds);
		
		return new EvaluatedCohort(c, cd, context);
	}
	
}
