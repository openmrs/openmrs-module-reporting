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
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.QueryBuilder;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.EncounterEvaluationContext;
import org.openmrs.module.reporting.query.encounter.EncounterIdSet;

import java.util.ArrayList;
import java.util.Collection;
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
	public static Set<Integer> getEncounterIdsForContext(EvaluationContext context, boolean returnNullForAllEncounterIds) throws EvaluationException {

		Cohort patIds = context.getBaseCohort();
		EncounterIdSet encIds = (context instanceof EncounterEvaluationContext ? ((EncounterEvaluationContext)context).getBaseEncounters() : null);

		// If either context filter is not null and empty, return an empty set
		if ((patIds != null && patIds.isEmpty()) || (encIds != null && encIds.isEmpty())) {
			return new HashSet<Integer>();
		}

		// Retrieve the encounters for the baseCohort if specified
		if (patIds != null) {
			Set<Integer> encIdsForPatIds = new HashSet<Integer>();
			int batchSize = ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE();
			List<Integer> ids = new ArrayList<Integer>(patIds.getMemberIds());
			for (int i=0; i<ids.size(); i+=batchSize) {
				List<Integer> batchList = ids.subList(i, i + Math.min(batchSize, ids.size()-i));
				encIdsForPatIds.addAll(getEncounterIdsForPatients(batchList));
			}
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

	public static Set<Integer> getEncounterIdsForPatients(Collection<Integer> patientIds) {
		Set<Integer> ret = new HashSet<Integer>();
		if (patientIds != null && patientIds.isEmpty()) {
			return ret;
		}
		QueryBuilder qb = new QueryBuilder();
		qb.addClause("select 	encounterId from Encounter");
		qb.addClause("where 	voided = false and patient.voided = false");
		if (patientIds != null) {
			qb.addClause("and 		patient.patientId in (:patIds)").withParameter("patIds", patientIds);
		}
		ret.addAll((List<Integer>) qb.execute());
		return ret;
	}
}
