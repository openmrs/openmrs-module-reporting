/*
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
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.Cohorts;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.VisitCohortDefinition;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        q.whereIn("v.location", getLocationList(cd));
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
            c = Cohort.subtract(baseCohort, c);
        }

        return new EvaluatedCohort(c, cohortDefinition, context);
    }

    private List<Location> getLocationList(VisitCohortDefinition cd) {
        if (cd.isIncludeChildLocations()) {
            return DefinitionUtil.getAllLocationsAndChildLocations(cd.getLocationList());
        }
        return cd.getLocationList();
    }

}
