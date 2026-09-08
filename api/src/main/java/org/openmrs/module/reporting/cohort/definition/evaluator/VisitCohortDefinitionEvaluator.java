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
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.CohortUtil;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = VisitCohortDefinition.class)
public class VisitCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
        VisitCohortDefinition cd = (VisitCohortDefinition) cohortDefinition;

        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("v.patient.id");
        q.from(Visit.class, "v");
        q.whereIn("v.visitType", cd.getVisitTypeList());
        q.whereIn("v.location", cd.getLocationList());
        q.whereIn("v.indication", cd.getIndicationList());
        q.whereGreaterOrEqualTo("v.startDatetime", cd.getStartedOnOrAfter());
        q.whereLessOrEqualTo("v.startDatetime", cd.getStartedOnOrBefore());
        q.whereGreaterOrEqualTo("v.stopDatetime", cd.getStoppedOnOrAfter());
        q.whereLessOrEqualTo("v.stopDatetime", cd.getStoppedOnOrBefore());
        q.whereEqual("v.creator", cd.getCreatedBy());
        q.whereGreaterOrEqualTo("v.dateCreated", cd.getCreatedOnOrAfter());
        q.whereLessOrEqualTo("v.dateCreated", cd.getCreatedOnOrBefore());

        if (cd.isActive() != null) {
            q.whereNull("v.stopDatetime");
        }
        if (cd.getActiveOnOrAfter() != null) {
            q.whereGreaterEqualOrNull("v.stopDatetime", cd.getActiveOnOrAfter());
        }
        if (cd.getActiveOnOrBefore() != null) {
            q.whereLessOrEqualTo("v.startDatetime", cd.getActiveOnOrBefore());
        }

        q.wherePatientIn("v.patient.id", context);

        // TODO need to exclude voided patients (just in case there are non-voided visits for voided patients)

        Cohort c = new Cohort(evaluationService.evaluateToList(q, Integer.class, context));

        if (cd.getReturnInverse()) {
            Cohort baseCohort = Cohorts.allPatients(context);
            c = CohortUtil.subtract(baseCohort, c);
        }

        return new EvaluatedCohort(c, cohortDefinition, context);
    }

}
