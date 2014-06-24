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
package org.openmrs.module.reporting.data.visit;

import org.openmrs.Cohort;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.VisitEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.visit.VisitIdSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Visit Data Utility methods
 */
public class VisitDataUtil {

    /**
     * @return the base set of visit ids relevant for the passed EvaluationContext or null for all visit ids
     * If returnNullForAllVisitIds is false, then this will return all visit ids in the system if unconstrained by the context
     */
    public static Set<Integer> getVisitIdsForContext(EvaluationContext context, boolean returnNullForAllVisitIds) {

        Cohort patIds = context.getBaseCohort();
        VisitIdSet visitIds = (context instanceof VisitEvaluationContext ? ((VisitEvaluationContext)context).getBaseVisits() : null);

        // If either context filter is not null and empty, return an empty set
        if ((patIds != null && patIds.isEmpty()) || (visitIds != null && visitIds.isEmpty())) {
            return new HashSet<Integer>();
        }

        // Retrieve the visits for the baseCohort if specified
        if (patIds != null) {

			HqlQueryBuilder qb = new HqlQueryBuilder();
			qb.select("v.visitId").from(Visit.class, "v").wherePatientIn("v.patient.patientId", context);
			List<Integer> visitIdsForPatients = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context);

            if (visitIds == null) {
                visitIds = new VisitIdSet(visitIdsForPatients);
            }
            else {
                visitIds.getMemberIds().retainAll(visitIdsForPatients);
            }
        }

        // If any filter was applied, return the results of this
        if (visitIds != null) {
            return visitIds.getMemberIds();
        }

        // Otherwise, all visit are needed, so return appropriate value
        if (returnNullForAllVisitIds) {
            return null;
        }

		HqlQueryBuilder qb = new HqlQueryBuilder().select("v.visitId").from(Visit.class, "v");
		return new HashSet<Integer>(Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context));
    }
}
