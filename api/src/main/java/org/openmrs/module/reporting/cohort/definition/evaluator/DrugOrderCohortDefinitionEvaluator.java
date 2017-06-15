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
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;

/**
 * Evaluates a DrugOrderCohortDefinition and produces a cohort
 */
@Handler(supports={DrugOrderCohortDefinition.class})
public class DrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  @Autowired
  EvaluationService evaluationService;

  /**
   * Default constructor
   */
  public DrugOrderCohortDefinitionEvaluator(){}

  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context){
    DrugOrderCohortDefinition cd = (DrugOrderCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());

		HqlQueryBuilder q = new HqlQueryBuilder();
		q.select("do.patient.patientId");
		q.from(DrugOrder.class, "do");

		q.wherePatientIn("do.patient.patientId", context);
		
		// For a drug to be active, it must have a valid activated date and a stoppedDate of null value or greater than now?
		if (cd.getOnlyCurrentlyActive()) {
			q.whereNotNull("do.dateActivated");
			q.whereGreaterOrNull("do.dateStopped", new Date());
		}

		if (cd.getStoppedOrChanged()) {

		}
		else { // drug started
			// q.whereEqual("do.action", Action.NEW);
			q.whereNull("do.previousOrder");
		}

		if (cd.getDrugList() != null) {
			if (cd.getGroupMethod().equals("ANY")) {
				q.whereInAny("do.drug", cd.getDrugList().toArray());
			}
			else if (cd.getGroupMethod().equals("ALL")) {
				q.whereIn("do.drug", cd.getDrugList());
				q.groupBy("do.patient.patientId" + " having count(*) = " + cd.getDrugList().size());
			}
			else if (cd.getGroupMethod().equals("NONE")) {
					q.whereNotInAny("do.drug", cd.getDrugList());
			}
		}

		if (cd.getCareSetting() != null) {
			q.whereEqual("do.careSetting", cd.getCareSetting()); // not working
		}
		
		if (cd.getGenericDrugList() != null) {
			if (cd.getGroupMethod().equals("ANY")) {
				q.whereInAny("do.concept", cd.getGenericDrugList().toArray());
			}
			else if (cd.getGroupMethod().equals("ALL")) {
					q.whereIn("do.concept", cd.getGenericDrugList());
					q.groupBy("do.patient.patientId" + " having count(*) = " + cd.getDrugList().size());
			}
			else if (cd.getGroupMethod().equals("NONE")) {
					q.whereNotInAny("do.concept", cd.getGenericDrugList());
			}
		}

		if (cd.getActivatedOnOrBefore() != null) {
			q.whereLessOrEqualTo("do.dateActivated", cd.getActivatedOnOrBefore());
		}
		if (cd.getActivatedOnOrAfter() != null) {
			q.whereGreaterOrEqualTo("do.dateActivated", cd.getActivatedOnOrAfter());
		}
		if (cd.getStoppedOnOrBefore() != null) {
			q.whereLessOrEqualTo("do.dateStopped", cd.getStoppedOnOrBefore());
		}	
		if (cd.getStoppedOnOrAfter() != null) {
			q.whereGreaterOrEqualTo("do.dateStopped", cd.getStoppedOnOrAfter());
		}

		List<Integer> pIds = evaluationService.evaluateToList(q, Integer.class, context);
		Cohort c = new Cohort(pIds);

		return new EvaluatedCohort(c, cd, context);
  }

}