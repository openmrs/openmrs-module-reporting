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

import org.openmrs.annotation.Handler;

import java.util.List;
import java.util.Date;

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

/**
 * Evaluates a StartedDrugOrderCohortDefinition and produces a cohort
 */
@Handler(supports={StartedDrugOrderCohortDefinition.class})
public class StartedDrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  @Autowired
  EvaluationService evaluationService;

  /**
   * Default constructor
   */
  public StartedDrugOrderCohortDefinitionEvaluator(){}

  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context){
    StartedDrugOrderCohortDefinition cd = (StartedDrugOrderCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("do.patient.patientId");
		q.from(DrugOrder.class, "do");
		q.wherePatientIn("do.patient.patientId", context);
		// A "started" drug order means a drug order with action=NEW and previousOrder=null
		q.whereEqual("do.action", Action.NEW);
		q.whereNull("do.previousOrder");

		if (cd.getActiveOnOrBefore() != null) {
			System.out.println("Active On or Before " + cd.getActiveOnOrBefore());
			q.whereLessOrEqualTo("do.dateActivated", cd.getActiveOnOrBefore());
			q.whereGreaterOrNull("do.dateStopped", cd.getActiveOnOrBefore());
		}
		if (cd.getActiveOnOrAfter() != null) {
			System.out.println("Active On or After " + cd.getActiveOnOrAfter());
			q.whereNotNull("do.dateActivated");
			q.whereGreaterEqualOrNull("do.dateStopped", cd.getActiveOnOrAfter());
		}
		if (cd.getStoppedOnOrAfter() != null) {
			q.whereGreaterOrEqualTo("do.dateStopped", cd.getStoppedOnOrAfter());
		}
		if (cd.getStoppedOnOrBefore() != null) {
			q.whereLessOrEqualTo("do.dateStopped", cd.getStoppedOnOrAfter());
		}

		if (cd.getDrugList() != null && cd.getDrugList().size() > 0) {
			if (cd.getGroupMethod().equals("ANY")) {
				q.whereInAny("do.drug", cd.getDrugList().toArray());
			}
			if (cd.getGroupMethod().equals("ALL")) {
				q.whereIn("do.drug", cd.getDrugList());
				q.groupBy("do.patient.patientId" + " having count(*) = " + cd.getDrugList().size());
			}
			if (cd.getGroupMethod().equals("NONE")) {
					q.whereNotInAny("do.drug", cd.getDrugList());
			}
		}

		if (cd.getGenericDrugList() != null && cd.getGenericDrugList().size() > 0) {
			if (cd.getGroupMethod().equals("ANY")) {
				q.whereInAny("do.concept", cd.getGenericDrugList().toArray());
			}
			if (cd.getGroupMethod().equals("ALL")) {
				q.whereIn("do.concept", cd.getGenericDrugList());
				q.groupBy("do.patient.patientId" + " having count(*) = " + cd.getGenericDrugList().size());
			}
			if (cd.getGroupMethod().equals("NONE")) {
					q.whereNotInAny("do.concept", cd.getGenericDrugList());
			}
		}

		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		Cohort c = new Cohort(pIds);

		return new EvaluatedCohort(c, cd, context);
  }

}