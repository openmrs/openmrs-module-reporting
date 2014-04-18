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
			Set<Integer> encIdsForPatIds = getEncounterIdsForPatients(patIds.getMemberIds());
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
		return getEncounterIdsForPatients(null);
	}

	public static Set<Integer> getEncounterIdsForPatients(Set<Integer> patientIds) {
		EvaluationContext context = new EvaluationContext();
		if (patientIds != null) {
			context.setBaseCohort(new Cohort(patientIds));
		}
		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("e.encounterId").from(Encounter.class, "e").wherePatientIn("e.patient.patientId", context);
		List<Integer> ids = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class);
		return new HashSet<Integer>(ids);
	}
}
