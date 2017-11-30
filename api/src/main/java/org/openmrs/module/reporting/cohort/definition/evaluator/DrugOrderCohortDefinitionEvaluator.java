/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.cohort.definition.evaluator;

import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.Cohort;
import org.openmrs.DrugOrder;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.DrugOrderCohortDefinition;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.annotation.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Date;

@Handler(supports = { DrugOrderCohortDefinition.class })
public class DrugOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  @Autowired
  EvaluationService evaluationService;

  public DrugOrderCohortDefinitionEvaluator() {
  }

  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
    DrugOrderCohortDefinition drugOrderCohortDefinition = (DrugOrderCohortDefinition) cohortDefinition;
    context = ObjectUtil.nvl(context, new EvaluationContext());

    HqlQueryBuilder query = new HqlQueryBuilder();
    query.select("drugOrder.patient.patientId");
    query.from(DrugOrder.class, "drugOrder");

    query.wherePatientIn("drugOrder.patient.patientId", context);

    if (drugOrderCohortDefinition.getOnlyCurrentlyActive() != null) {
      if (drugOrderCohortDefinition.getOnlyCurrentlyActive() == true) {
      query.whereNotNull("drugOrder.dateActivated");
      query.whereGreaterOrNull("drugOrder.dateStopped", new Date());
      }
      else if (drugOrderCohortDefinition.getOnlyCurrentlyActive() == false) {
      query.whereNull("drugOrder.dateActivated");
      query.whereLessOrEqualTo("drugOrder.dateStopped", new Date());
    }
  }
    if (drugOrderCohortDefinition.getDrugSets() != null) {
      if (drugOrderCohortDefinition.getWhich() == "ANY") {
        query.whereInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugSets().toArray());
      } else if (drugOrderCohortDefinition.getWhich() == "ALL") {
        query.whereIn("drugOrder.concept", drugOrderCohortDefinition.getDrugSets());
        query.groupBy(
            "drugOrder.patient.patientId" + " having count(*) = " + drugOrderCohortDefinition.getDrugSets().size());
      } else if (drugOrderCohortDefinition.getWhich() == "NONE") {
        query.whereNotInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugSets());

      }
    }

    if (drugOrderCohortDefinition.getDrugConcepts() != null) {
      if (drugOrderCohortDefinition.getWhich().equals("ANY")) {
        query.whereInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugConcepts().toArray());
      } else if (drugOrderCohortDefinition.getWhich().equals("ALL")) {
        query.whereIn("drugOrder.concept", drugOrderCohortDefinition.getDrugConcepts());
        query.groupBy(
            "drugOrder.patient.patientId" + " having count(*) = " + drugOrderCohortDefinition.getDrugSets().size());
      } else if (drugOrderCohortDefinition.getWhich() == "NONE") {
        query.whereNotInAny("drugOrder.concept", drugOrderCohortDefinition.getDrugConcepts());
      }
    }

    if (drugOrderCohortDefinition.getActivatedOnOrBefore() != null) {
      query.whereLessOrEqualTo("drugOrder.dateActivated", drugOrderCohortDefinition.getActivatedOnOrBefore());
    }
    if (drugOrderCohortDefinition.getActivatedOnOrAfter() != null) {
      query.whereGreaterOrEqualTo("drugOrder.dateActivated", drugOrderCohortDefinition.getActivatedOnOrAfter());
    }

    if (drugOrderCohortDefinition.getCareSetting() != null) {
      query.whereEqual("drugOrder.careSetting", drugOrderCohortDefinition.getCareSetting());
    }

    if (drugOrderCohortDefinition.getActiveWithinLastDays() != null) {
      Integer days = drugOrderCohortDefinition.getActiveWithinLastDays();
      query.whereLessOrEqualTo("drugOrder.dateActivated", DateUtils.addDays(new Date(), -days));
    }

    if (drugOrderCohortDefinition.getActiveWithinLastMonths() != null) {
      Integer months = drugOrderCohortDefinition.getActiveWithinLastMonths();
      query.whereLessOrEqualTo("drugOrder.dateActivated", DateUtils.addMonths(new Date(), -months));
    }

    List<Integer> patientIds = evaluationService.evaluateToList(query, Integer.class, context);
    Cohort cohort = new Cohort(patientIds);

    return new EvaluatedCohort(cohort, drugOrderCohortDefinition, context);
  }

}