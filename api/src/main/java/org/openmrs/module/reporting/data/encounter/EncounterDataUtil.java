/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.data.encounter;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encounter Data Utility methods
 */
public class EncounterDataUtil {

	/**
	 * @return the base set of encounter ids relevant for the passed EvaluationContext or null for all encounter ids
	 * If returnNullForAllEncounterIds is false, then this will return all encounter ids in the system if unconstrained by the context
	 */
	public static Set<Integer> getEncounterIdsForContext(EvaluationContext context, boolean returnNullForAllEncounterIds) {

		Cohort patIds = context.getBaseCohort();
		EncounterIdSet encIds = (context instanceof EncounterEvaluationContext ? ((EncounterEvaluationContext)context).getBaseEncounters() : null);

		// If either context filter is not null and empty, return an empty set
		if ((patIds != null && patIds.isEmpty()) || (encIds != null && encIds.isEmpty())) {
			return new HashSet<Integer>();
		}

		// Retrieve the encounters for the baseCohort if specified
		if (patIds != null) {

			HqlQueryBuilder qb = new HqlQueryBuilder();
			qb.select("e.encounterId").from(Encounter.class, "e").wherePatientIn("e.patient.patientId", context);
			List<Integer> encIdsForPatIds = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context);

			if (encIds == null) {
				encIds = new EncounterIdSet(encIdsForPatIds);
			}
			else {
				encIds.getMemberIds().retainAll(encIdsForPatIds);
			}
		}

		// If any filter was applied, return the results of this
		if (encIds != null) {
			return encIds.getMemberIds();
		}

		// Otherwise, all encounters are needed, so return appropriate value
		if (returnNullForAllEncounterIds) {
			return null;
		}

		HqlQueryBuilder qb = new HqlQueryBuilder().select("e.encounterId").from(Encounter.class, "e");
		return new HashSet<Integer>(Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context));
	}
}
